package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupElement;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.WicketTag;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.parser.AbstractMarkupFilter;
import org.apache.wicket.markup.parser.filter.RelativePathPrefixHandler;
import org.apache.wicket.markup.resolver.IComponentResolver;
import org.apache.wicket.request.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

/**
 * Work-around to make apache-wicket play nice with servlet context used in OSGi
 * Due to extensibility limitations in Apache Wicket, we had to clone&own this class.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdBusRelativePathPrefixHandler extends AbstractMarkupFilter implements IComponentResolver {


    /**
     * Logging
     */
    private static final Logger log = LoggerFactory.getLogger(RelativePathPrefixHandler.class);

    /**
     * The id automatically assigned to tags without an id which we need to prepend a relative path
     * to.
     */
    public static final String WICKET_RELATIVE_PATH_PREFIX_CONTAINER_ID = "_relative_path_prefix_";

    /**
     * List of attribute names considered
     */
    private static final String attributeNames[] = new String[]{"href", "src", "background",
            "action"};

    private String mountPoint;

    public IdBusRelativePathPrefixHandler(String mountPoint) {
        this.mountPoint = mountPoint;
    }

    /**
     * Behavior that adds a prefix to src, href and background attributes to make them
     * context-relative
     */
    public final Behavior IDBUS_RELATIVE_PATH_BEHAVIOR = new Behavior() {
        
        @Override
        public void onComponentTag(Component component, ComponentTag tag) {

            /*
            IRequestCodingStrategy coder = RequestCycle.get()
                    .getProcessor()
                    .getRequestCodingStrategy(); */

            // Modify all relevant attributes
            for (int i = 0; i < attributeNames.length; i++) {
                String attrName = attributeNames[i];
                String attrValue = tag.getAttributes().getString(attrName);


                if ((attrValue != null) && (attrValue.startsWith("/") == false) &&
                        (attrValue.indexOf(":") < 0) && !(attrValue.startsWith("#"))) {
                    if (UrlUtils.isRelative(attrValue)) {
                        // All resources MUST be relative to mount point, just remove all the ../ added by wicket:
                        int idx = attrValue.lastIndexOf("../");
                        String newPath = attrValue.substring(idx + 1);
                        tag.getAttributes().put(attrName, "/" + mountPoint + "/" + newPath);
                    }
                }
            }

        }



    };

    @Override
    protected final MarkupElement onComponentTag(ComponentTag tag) throws ParseException
    {
        if (tag.isClose())
        {
            return tag;
        }

        String wicketIdAttr = getWicketNamespace() + ":" + "id";

        // Don't touch any wicket:id component and any auto-components
        if ((tag instanceof WicketTag) || (tag.isAutolinkEnabled() == true) ||
                (tag.getAttributes().get(wicketIdAttr) != null))
        {
            return tag;
        }

        // Work out whether we have any attributes that require us to add a
        // behavior that prepends the relative path.
        for (String attrName : attributeNames)
        {
            String attrValue = tag.getAttributes().getString(attrName);
            if ((attrValue != null) && (attrValue.startsWith("/") == false) &&
                    (!attrValue.contains(":")) && !(attrValue.startsWith("#")))
            {
                if (tag.getId() == null)
                {
                    tag.setId(WICKET_RELATIVE_PATH_PREFIX_CONTAINER_ID);
                    tag.setAutoComponentTag(true);
                }
                tag.addBehavior(IDBUS_RELATIVE_PATH_BEHAVIOR );
                tag.setModified(true);
                break;
            }
        }

        return tag;
    }


    public Component resolve(final MarkupContainer container, final MarkupStream markupStream,
                             final ComponentTag tag)
    {
        if ((tag != null) && (tag.getId().startsWith(WICKET_RELATIVE_PATH_PREFIX_CONTAINER_ID)))
        {
            String id = WICKET_RELATIVE_PATH_PREFIX_CONTAINER_ID +
                    container.getPage().getAutoIndex();

            // we do not want to mess with the hierarchy, so the container has to be
            // transparent as it may have wicket components inside. for example a raw anchor tag
            // that contains a label.
            return new TransparentWebMarkupContainer(id);
        }
        return null;
    }
}
