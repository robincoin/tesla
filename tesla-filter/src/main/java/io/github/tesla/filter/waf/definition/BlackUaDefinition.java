package io.github.tesla.filter.waf.definition;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.utils.JsonUtils;

public class BlackUaDefinition extends PluginDefinition {
    private List<String> blackUas;

    public List<String> getBlackUas() {
        return blackUas;
    }

    public void setBlackUas(List<String> blackUas) {
        this.blackUas = blackUas;
    }

    @Override
    public String validate(String paramJson) {
        if (StringUtils.isBlank(paramJson)) {
            return null;
        }
        blackUas = JsonUtils.fromJson(paramJson, List.class);
        return JsonUtils.serializeToJson(this);
    }
}
