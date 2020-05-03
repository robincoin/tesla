package io.github.tesla.filter.waf.definition;

import java.util.Map;

import io.github.tesla.filter.service.definition.PluginDefinition;

public class AppKeyControlDefinition extends PluginDefinition {

    // value 为 prefix key 为services的service_id
    private Map<String, String> ignoreServices;

    public Map<String, String> getIgnoreServices() {
        return ignoreServices;
    }

    public void setIgnoreServices(Map<String, String> ignoreServices) {
        this.ignoreServices = ignoreServices;
    }

}
