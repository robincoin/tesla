package io.github.tesla.filter.waf.definition;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.utils.JsonUtils;

public class BlackIpDefinition extends PluginDefinition {
    private List<String> blackIps;

    public List<String> getBlackIps() {
        return blackIps;
    }

    public void setBlackIps(List<String> blackIps) {
        this.blackIps = blackIps;
    }

    @Override
    public String validate(String paramJson) {
        if (StringUtils.isBlank(paramJson)) {
            return null;
        }
        blackIps = JsonUtils.fromJson(paramJson, List.class);
        return JsonUtils.serializeToJson(this);
    }
}
