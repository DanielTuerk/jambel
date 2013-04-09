package com.jambit.jambel.server.config;

import com.google.common.collect.Maps;
import org.apache.tiles.Attribute;
import org.apache.tiles.Definition;
import org.apache.tiles.definition.UnresolvingLocaleDefinitionsFactory;
import org.apache.tiles.request.Request;

import java.util.Map;

/**
 * Simple factory for the JSTL tiles.
 *
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class JspDefinitionsFactory extends UnresolvingLocaleDefinitionsFactory {

    private static final Attribute TEMPLATE = getAttributeForCommonView("layout");
    public static final String TILES_COMPONENT_HEADER = "header";
    public static final String TILES_COMPONENT_MENU = "menu";
    public static final String TILES_COMPONENT_FOOTER = "footer";
    public static final String TILES_COMPONENT_CONTENT = "content";

    private static Attribute getAttributeForCommonView(String viewName) {
        return new Attribute(MvcConfiguration.VIEW_RESOLVER_COMMON_PREFIX + viewName + MvcConfiguration.VIEW_RESOLVER_SUFFIX);
    }

    private final Map<String, Definition> tiles = Maps.newHashMap();


    public JspDefinitionsFactory() {
        addDefinition("welcome", MvcConfiguration.VIEW_RESOLVER_PREFIX + "welcome" + MvcConfiguration.VIEW_RESOLVER_SUFFIX);
        addDefinition("jambeln", MvcConfiguration.VIEW_RESOLVER_PREFIX + "jambeln" + MvcConfiguration.VIEW_RESOLVER_SUFFIX);
    }

    @Override
    public Definition getDefinition(String name, Request tilesContext) {
        return tiles.get(name);
    }

    private void addDefinition(String name, String jspViewPath) {
        Map<String, Attribute> attributes = Maps.newHashMap();

        attributes.put(TILES_COMPONENT_HEADER, getAttributeForCommonView(TILES_COMPONENT_HEADER));
        attributes.put(TILES_COMPONENT_MENU, getAttributeForCommonView(TILES_COMPONENT_MENU));
        attributes.put(TILES_COMPONENT_FOOTER, getAttributeForCommonView(TILES_COMPONENT_FOOTER));

        attributes.put(TILES_COMPONENT_CONTENT, new Attribute(jspViewPath));

        tiles.put(name, new Definition(name, TEMPLATE, attributes));
    }
}