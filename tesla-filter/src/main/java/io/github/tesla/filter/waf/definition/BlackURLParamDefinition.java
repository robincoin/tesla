package io.github.tesla.filter.waf.definition;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.utils.JsonUtils;

public class BlackURLParamDefinition extends PluginDefinition {
    private List<String> blackURLParams;

    public List<String> getBlackURLParams() {
        return blackURLParams;
    }

    public void setBlackURLParams(List<String> blackURLParams) {
        this.blackURLParams = blackURLParams;
    }

    @Override
    public String validate(String paramJson) {
        if (StringUtils.isBlank(paramJson)) {
            return null;
        }
        blackURLParams = JsonUtils.fromJson(paramJson, List.class);
        return JsonUtils.serializeToJson(this);
    }
}
