package io.github.tesla.filter.endpoint.plugin.response;

import io.github.tesla.filter.AbstractResponsePlugin;
import io.github.tesla.filter.endpoint.definition.JarExecuteDefinition;
import io.github.tesla.filter.support.annnotation.EndpointResponsePlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.ClassUtils;
import io.github.tesla.filter.utils.JsonUtils;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author: zhangzhiping
 * @date: 2018/11/29 16:01
 * @description:
 */
@EndpointResponsePlugin(filterType = "JarExecuteResponsePlugin", definitionClazz = JarExecuteDefinition.class,
    filterOrder = 2, filterName = "执行上传jar包插件")
public class JarExecuteResponsePlugin extends AbstractResponsePlugin {

    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpResponse httpResponse) {
        return httpResponse;
    }

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpResponse httpResponse,
        Object filterParam) {
        JarExecuteDefinition definition = JsonUtils.json2Definition(filterParam, JarExecuteDefinition.class);
        if (definition == null) {
            return httpResponse;
        }
        String className = definition.getClassName();
        JarExecuteResponsePlugin userFilter = ClassUtils.getUserJarFilterRule(className, definition.getFileId(),
            getFileBytesByKey(definition.getFileId()));
        if (userFilter == null) {
            LOGGER.error(" response not found jar file ,fileId:" + definition.getFileId());
            return httpResponse;
        }
        userFilter.setSpringCloudDiscovery(this.getSpringCloudDiscovery());
        HttpResponse userResponse = userFilter.doFilter(servletRequest, httpResponse);
        return userResponse;
    }

}
