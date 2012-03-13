package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupElement;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.WicketTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.parser.AbstractMarkupFilter;
import org.apache.wicket.markup.parser.filter.RelativePathPrefixHandler;
import org.apache.wicket.markup.resolver.IComponentResolver;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.util.string.UrlUtils;
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
    private static final long serialVersionUID = 1L;

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
    public final IBehavior IDBUS_RELATIVE_PATH_BEHAVIOR = new AbstractBehavior() {
        
        private static final long serialVersionUID = 1L;
        
        @Override
        public void onComponentTag(Component component, ComponentTag tag) {
            IRequestCodingStrategy coder = RequestCycle.get()
                    .getProcessor()
                    .getRequestCodingStrategy();

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
                        tag.getAttributes().put(attrName, mountPoint + "/" + newPath);
                    }
                }
            }

        }



    };


    /**
     * Get the next MarkupElement from the parent MarkupFilter and handle it if the specific filter
     * criteria are met. Depending on the filter, it may return the MarkupElement unchanged,
     * modified or it remove by asking the parent handler for the next tag.
     *
     * @return Return the next eligible MarkupElement
     * @see org.apache.wicket.markup.parser.IMarkupFilter#nextTag()
     */
    public MarkupElement nextTag() throws ParseException {
        // Get the next tag. If null, no more tags are available
        final ComponentTag tag = (ComponentTag) getParent().nextTag();
        if ((tag == null) || tag.isClose()) {
            return tag;
        }

        // Don't touch any wicket:id component and any auto-components
        if ((tag instanceof WicketTag) || (tag.isAutolinkEnabled() == true) ||
                (tag.getAttributes().get("wicket:id") != null)) {
            return tag;
        }

        // Work out whether we have any attributes that require us to add a
        // behavior that prepends the relative path.
        for (int i = 0; i < attributeNames.length; i++) {
            String attrName = attributeNames[i];
            String attrValue = tag.getAttributes().getString(attrName);
            if ((attrValue != null) && (attrValue.startsWith("/") == false) &&
                    (attrValue.indexOf(":") < 0) && !(attrValue.startsWith("#"))) {
                if (tag.getId() == null) {
                    tag.setId(WICKET_RELATIVE_PATH_PREFIX_CONTAINER_ID);
                    tag.setAutoComponentTag(true);
                }
                tag.addBehavior(IDBUS_RELATIVE_PATH_BEHAVIOR);
                tag.setModified(true);
                break;
            }
        }

        return tag;
    }

    /**
     * @see org.apache.wicket.markup.resolver.IComponentResolver#resolve(org.apache.wicket.MarkupContainer,
     *      org.apache.wicket.markup.MarkupStream, org.apache.wicket.markup.ComponentTag)
     */
    public boolean resolve(MarkupContainer container, MarkupStream markupStream, ComponentTag tag) {
        if (WICKET_RELATIVE_PATH_PREFIX_CONTAINER_ID.equals(tag.getId())) {
            final Component wc;
            String id = WICKET_RELATIVE_PATH_PREFIX_CONTAINER_ID +
                    container.getPage().getAutoIndex();
            if (tag.isOpenClose()) {
                wc = new WebComponent(id);
            } else {
                // we do not want to mess with the hierarchy, so the container has to be
                // transparent as it may have wicket components inside. for example a raw anchor tag
                // that contains a label.
                wc = new WebMarkupContainer(id) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public boolean isTransparentResolver() {
                        return true;
                    }
                };
            }
            container.autoAdd(wc, markupStream);
            return true;
        }
        return false;
    }
}
