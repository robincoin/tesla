package io.github.tesla.filter.support.plugins;

import java.io.Serializable;

import io.github.tesla.filter.service.definition.PluginDefinition;

public class FilterMetadata implements Serializable {
    public static final String FILTER_SCAN_PACKAGE = "io.github.tesla.filter";
    private static final long serialVersionUID = 1L;
    private String filterType;
    private String filterName;
    private int filterOrder;
    private String ignoreClassType;
    private Class<? extends PluginDefinition> definitionClazz;

    public static String errorMsg(String type) {
        return String.format("未找到对应的插件- [%s] ，请联系管理员检查后端服务是否正确", type);
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public int getFilterOrder() {
        return filterOrder;
    }

    public void setFilterOrder(int filterOrder) {
        this.filterOrder = filterOrder;
    }

    public String getIgnoreClassType() {
        return ignoreClassType;
    }

    public void setIgnoreClassType(String ignoreClassType) {
        this.ignoreClassType = ignoreClassType;
    }

    public Class<? extends PluginDefinition> getDefinitionClazz() {
        return definitionClazz;
    }

    public void setDefinitionClazz(Class<? extends PluginDefinition> definitionClazz) {
        this.definitionClazz = definitionClazz;
    }

}
