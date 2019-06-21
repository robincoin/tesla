package io.github.tesla.filter.service.plugin.request.token;

import io.github.tesla.filter.endpoint.plugin.request.JarExecuteRequestPlugin;
import io.github.tesla.filter.service.annotation.AuthType;
import io.github.tesla.filter.service.definition.JarAuthDefinition;
import io.github.tesla.filter.service.plugin.request.AuthRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.ClassUtils;
import io.github.tesla.filter.utils.JsonUtils;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

@AuthType(authType = "jar", definitionClazz = JarAuthDefinition.class)
public class JarAuthRequestPlugin extends AuthRequestPlugin {

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        JarAuthDefinition definition = JsonUtils.json2Definition(filterParam, JarAuthDefinition.class);
        if (definition == null) {
            return null;
        }
        String className = definition.getClassName();
        JarExecuteRequestPlugin userFilter = ClassUtils.getUserJarFilterRule(className, definition.getFileId(),
            getFileBytesByKey(definition.getFileId()));
        if (userFilter == null) {
            LOGGER.error(" request not found jar file ,fileId:" + definition.getFileId());
            return null;
        }
        userFilter.setSpringCloudDiscovery(this.getSpringCloudDiscovery());
        HttpResponse userResponse = userFilter.doFilter(servletRequest, (HttpRequest)realHttpObject);
        return userResponse;
    }

}
