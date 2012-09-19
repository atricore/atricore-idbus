package org.atricore.idbus.kernel.planning.jbpm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.NodeCollection;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.NodeTypes;
import org.jbpm.graph.node.StartState;
import org.jbpm.jpdl.JpdlException;
import org.jbpm.jpdl.xml.JpdlParser;
import org.jbpm.jpdl.xml.JpdlXmlReader;
import org.jbpm.jpdl.xml.Problem;
import org.jbpm.jpdl.xml.ProblemListener;
import org.xml.sax.InputSource;

import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class StatelessJpdlXmlReader extends JpdlXmlReader {

    private static final Log log = LogFactory.getLog(StatelessJpdlXmlReader.class);

    private ProcessFragmentResolver fragmentResolver;

    private ProcessFragmentRegistry fragmentRegistry;

    private ProcessDescriptor processDescriptor;

    public StatelessJpdlXmlReader(InputSource inputSource) {
        super(inputSource);
        if (log.isTraceEnabled())
            log.trace("Using " + getClass().getName() + " JPDL Reader");
    }

    public StatelessJpdlXmlReader(InputSource inputSource, ProblemListener problemListener) {
        super(inputSource, problemListener);
        if (log.isTraceEnabled())
            log.trace("Using " + getClass().getName() + " JPDL Reader");
    }

    public StatelessJpdlXmlReader(Reader reader) {
        super(reader);
        if (log.isTraceEnabled())
            log.trace("Using " + getClass().getName() + " JPDL Reader");
    }

    public ProcessFragmentResolver getFragmentResolver() {
        return fragmentResolver;
    }

    public void setFragmentResolver(ProcessFragmentResolver fragmentResolver) {
        this.fragmentResolver = fragmentResolver;
    }

    public ProcessFragmentRegistry getFragmentRegistry() {
        return fragmentRegistry;
    }

    public void setFragmentRegistry(ProcessFragmentRegistry fragmentRegistry) {
        this.fragmentRegistry = fragmentRegistry;
    }

    @Override
    public ProcessDefinition readProcessDefinition() {
        // create a new definition
        processDefinition = ProcessDefinition.createNewProcessDefinition();

        // initialize lists
        problems = new ArrayList();
        unresolvedTransitionDestinations = new ArrayList();
        unresolvedActionReferences = new ArrayList();

        try {
            // parse the document into a dom tree
            document = JpdlParser.parse(inputSource, this);
            Element root = document.getRootElement();

            // We have the raw DOM tree here, we're going to replace fragments with the corresponding sub processes nodes
            // This requires re-wired start/end states
            resolveFragments(root);

            // read the process name
            parseProcessDefinitionAttributes(root);

            // get the process description
            String description = root.elementTextTrim("description");
            if (description!=null) {
                processDefinition.setDescription(description);
            }

            // first pass: read most content
            readSwimlanes(root);
            readActions(root, null, null);
            readNodes(root, processDefinition);
            readEvents(root, processDefinition);
            readExceptionHandlers(root, processDefinition);
            readTasks(root, null);

            // second pass processing
            resolveTransitionDestinations();
            resolveActionReferences();
            verifySwimlaneAssignments();

            // Remove once this works:
            /*
            Writer writer = new FileWriter("/tmp/" + processDefinition.getName() + ".jpdl");
            OutputFormat outputFormat = new OutputFormat( "  ", true );
            // OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            XMLWriter xmlWriter = new XMLWriter( writer, outputFormat );
            xmlWriter.write( document );
            xmlWriter.flush();
            writer.flush();
            */

        } catch (Exception e) {
            log.error("couldn't parse process definition", e);
            addProblem(new Problem(Problem.LEVEL_ERROR, "couldn't parse process definition", e));
        }

        if (Problem.containsProblemsOfLevel(problems, Problem.LEVEL_ERROR)) {
            throw new JpdlException(problems);
        }

        if (problems!=null) {
            Iterator iter = problems.iterator();
            while (iter.hasNext()) {
                Problem problem = (Problem) iter.next();
                log.warn("process parse warning: "+problem.getDescription());
            }
        }

        return processDefinition;
    }

    @Override
    public void readNodes(Element element, NodeCollection nodeCollection) {
        Iterator nodeElementIter = element.elementIterator();
        while (nodeElementIter.hasNext()) {
            Element nodeElement = (Element) nodeElementIter.next();
            String nodeName = nodeElement.getName();
            // get the node type
            Class nodeType = NodeTypes.getNodeType(nodeName);
            if (nodeType!=null) {

                Node node = null;
                try {
                    // create a new instance
                    node = (Node) nodeType.newInstance();
                } catch (Exception e) {
                    log.error("couldn't instantiate node '"+nodeName+"', of type '"+nodeType.getName()+"'", e);
                }

                node.setProcessDefinition(processDefinition);

                // check for duplicate start-states
                if ( (node instanceof StartState)
                        && (processDefinition.getStartState()!=null)
                        ) {
                    addError("max one start-state allowed in a process");

                } else {
                    // read the common node parts of the element
                    readNode(nodeElement, node, nodeCollection);

                    // if the node is parsable
                    // (meaning: if the node has special configuration to parse, other then the
                    //  common node data)
                    node.read(nodeElement, this);
                }
            }
        }
    }

    @Override
    public void readNode(Element nodeElement, Node node, NodeCollection nodeCollection) {
        // first put the node in its collection.  this is done so that the
        // setName later on will be able to differentiate between nodes contained in
        // processDefinitions and nodes contained in superstates
        nodeCollection.addNode(node);

        // get the node name
        String name = nodeElement.attributeValue("name");
        if (name!=null) {
            node.setName(name);

            // check if this is the initial node
            if ( (initialNodeName!=null)
                    && (initialNodeName.equals(node.getFullyQualifiedName()))
                    ) {
                processDefinition.setStartState(node);
            }
        }

        // get the node description
        String description = nodeElement.elementTextTrim("description");
        if (description!=null) {
            node.setDescription(description);
        }

        String asyncText = nodeElement.attributeValue("async");
        if ("true".equalsIgnoreCase(asyncText)) {
            node.setAsync(true);
        } else if ("exclusive".equalsIgnoreCase(asyncText)) {
            node.setAsync(true);
            node.setAsyncExclusive(true);
        }

        // parse common subelements
        readNodeTimers(nodeElement, node);
        readEvents(nodeElement, node);
        readExceptionHandlers(nodeElement, node);

        // save the transitions and parse them at the end
        addUnresolvedTransitionDestination(nodeElement, node);
    }

    protected void resolveFragments(Element root) throws Exception {
        Iterator eIter = root.elementIterator();
        while (eIter.hasNext()) {

            Element child = (Element) eIter.next();
            String text = child.getText();
            String name = child.getName();

            if (!name.equals("process-fragment-state"))
                continue;

            Iterator pfIter = child.elementIterator();
            while (pfIter.hasNext()) {
                Element pfElement = (Element) pfIter.next();
                if (!pfElement.getName().equals("process-fragment"))
                    continue;

                Iterator pfAttrIter = pfElement.attributeIterator();

                String lifecycle = null;
                String phase = null;

                while (pfAttrIter.hasNext()) {
                    Attribute attr = (Attribute) pfAttrIter.next();
                    String attrName = attr.getName();
                    attrName.toString();

                    if (attrName.equals("lifecycle"))
                        lifecycle = attr.getValue();

                    if (attrName.equals("phase"))
                        phase = attr.getValue();
                }


                Document fragment = loadFragment(lifecycle, phase);


                if (fragment == null) {
                    if (log.isDebugEnabled())
                        log.debug("No fragments found for " + lifecycle + ":" + phase);
                    continue;
                }

                Element rootFragment = fragment.getRootElement();

                if (log.isDebugEnabled())
                    log.debug("Fragment found for " + lifecycle + ":" + phase);

                if (!rootFragment .getName().equals("process-definition")) {
                    // This is wrong !!!
                    continue;
                }

                injectFragment(root, child, fragment);

            }

        }

    }

    /**
     *
     * @param processDef main process definition element (process-definition)
     * @param fragmentReference reference to a fragment (process-fragment-state)
     * @param fragmentDefinition a document defining a process, used as a fragment (process-definition)
     */
    protected void injectFragment(Element processDef, Element fragmentReference, Document fragmentDefinition) {

        // This must be set as the transition of the last added node
        String nextStateName = getAttributeValue(getFirstChild(fragmentReference, "transition"), "to");

        // This is were we must start adding the nodes at process definition, after fragmentReference:
        int idx = getChildPosition(processDef, fragmentReference) + 1;

        // A prefix to avoid name coalitions among fragment state names
        String fragmentPrefix = getAttributeValue(fragmentReference, "name");

        // The first element on the fragment definition, normally a process-definition element
        Element fragmentRoot = fragmentDefinition.getRootElement();

        // Fragment elements, we're looking for state elements
        Iterator itFrag = fragmentRoot.elementIterator();

        // The first and last states on the fragment, needed to connect them with the rest of the process through transitions
        Element lastFragmentState = null;
        Element firstFragmentState = null;

        while (itFrag.hasNext()) {

            // Only interested in state elements (not in start-state, etc)
            Element fragmentElement = (Element) itFrag.next();

            if (fragmentElement.getName().equals("state")) {

                // Clone the state to add it to the current tree:
                Element newState = (Element) fragmentElement.clone();

                // Rename the state using the prefix
                Attribute fragmentStateName = getAttribute(newState, "name");
                fragmentStateName.setValue(fragmentPrefix + "-" + fragmentStateName.getValue());

                // Rename the transition to the next state using the prefix
                Element t = getFirstChild(newState, "transition");
                Attribute tt = getAttribute(t, "to");
                tt.setValue(fragmentPrefix + "-" + tt.getValue());

                // Add all variables declarations from fragmentReference to these new states
                Collection<Element> vars = getChildren(fragmentReference,"variable");
                for (Iterator<Element> iterator = vars.iterator(); iterator.hasNext(); ) {
                    Element var = iterator.next();
                    Element newVar = (Element) var.clone();
                    newState.elements().add(newVar);
                }


                // Keep track of first state
                if (firstFragmentState == null)
                    firstFragmentState = newState;

                // Keep track of last state
                lastFragmentState = newState;

                // Add the new state and increase the index
                processDef.elements().add(newState);

                // Increase the index by 2, one for the new element and another to set it at the end
                idx += 2;

            }
        }

        // If the fragment was not empty, connect first and last states
        if (lastFragmentState != null) {
            Element lastT = getFirstChild(lastFragmentState, "transition");
            Attribute lastTAttr = getAttribute(lastT, "to");
            // This is taken from the transition of the fragment reference
            lastTAttr.setValue(nextStateName);
        }

        if (firstFragmentState != null) {
            // Update the fragment reference and point it to the first state of the fragment
            String firstFragmentStateName = getAttributeValue(firstFragmentState, "name");

            Element t = getFirstChild(fragmentReference, "transition");
            Attribute tt = getAttribute(t, "to");
            tt.setValue(firstFragmentStateName);
        }


    }

    protected int getChildPosition(Element parent, Element child) {
        List e = parent.elements();

        for (int i = 0; i < e.size(); i++) {
            Element element = (Element) e.get(i);
            if (element.getQName().equals(child.getQName()))
                return i;
        }
        return -1;
    }

    protected String getAttributeValue(Element e, String attrName) {
        Iterator it =  e.attributeIterator();
        while (it.hasNext()) {
            Attribute attribute = (Attribute) it.next();
            if (attribute.getName().equals(attrName))
                return attribute.getValue();
        }
        return null;
    }

    protected Attribute getAttribute(Element e, String attrName) {
        Iterator it =  e.attributeIterator();
        while (it.hasNext()) {
            Attribute attribute = (Attribute) it.next();
            if (attribute.getName().equals(attrName))
                return attribute;
        }
        return null;
    }


    protected Element getFirstChild(Element e, String eName) {
        Iterator it = e.elementIterator();
        while (it.hasNext()) {
            Element child = (Element) it.next();
            if (child.getName().equals(eName))
                return child;
        }
        return null;
    }

    protected Collection<Element> getChildren(Element e, String eName) {
        List<Element> children = new ArrayList<Element>();

        Iterator it = e.elementIterator();
        while (it.hasNext()) {
            Element child = (Element) it.next();
            if (child.getName().equals(eName))
                children.add(child);
        }
        return children;

    }

    protected Element getChildByAttr(Element e, String attrName, String attrValue) {
        Iterator it = e.elementIterator();
        while (it.hasNext()) {
            Element child = (Element) it.next();
            String v = getAttributeValue(child, attrName);
            if (v != null && v.equals(attrValue))
                return child;
        }
        return null;

    }

    protected Document loadFragment(String lifecycle, String phase) throws Exception {

        ProcessFragment processFragment = fragmentResolver.findProcessFragment(lifecycle, phase);
        if (processFragment == null)
            return null;

        boolean active = false;
        for(Object o : processDescriptor.getActiveProcessFragments()) {
            String activeFragmentName = (String) o;
            if (activeFragmentName.equals(processFragment.getName())) {
                active = true;
                break;
            }
        }

        if (!active)
            return null;

        java.io.InputStream is = processFragment.getProcessFragmentDescriptor().getInputStream();
        InputSource isrc = new InputSource(is);
        Document fragmentDoc = JpdlParser.parse(isrc, new JpdlXmlReader(isrc));
        // Now we have a new DOM document for the fragment , we need to include it after child ..,(or replacing child !?)
        return fragmentDoc;
    }

    public void setProcessDescriptor(ProcessDescriptor pd) {
        this.processDescriptor = pd;
    }

    public ProcessDescriptor getProcessDescriptor() {
        return processDescriptor;
    }
}
