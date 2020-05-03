package io.github.tesla.filter.common.plugin.request;

import org.apache.commons.collections.MapUtils;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.common.definition.AccessControlDefinition;
import io.github.tesla.filter.support.annnotation.AppKeyRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.AntMatchUtil;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

@AppKeyRequestPlugin(filterType = "AccessControlRequestPlugin", filterOrder = 0, filterName = "访问权限插件")
public class AccessControlRequestPlugin extends AbstractRequestPlugin {

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        String  filterParam) {

        AccessControlDefinition definition = JsonUtils.json2Definition(filterParam, AccessControlDefinition.class);
        if (definition == null) {
            return null;
        }
        if (MapUtils.isEmpty(definition.getAccessServices())) {
            return null;
        }

        for (String prefix : definition.getAccessServices().values()) {
            if (AntMatchUtil.matchPrefix(prefix, servletRequest.getRequestURI())) {
                return null;
            }
        }
        return PluginUtil.createResponse(HttpResponseStatus.NOT_ACCEPTABLE, servletRequest.getNettyRequest(),
            "access reject ");
    }
}
