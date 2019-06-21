package io.github.tesla.filter.service.plugin.request.token;

import io.github.tesla.filter.service.annotation.AuthType;
import io.github.tesla.filter.service.definition.JarAuthDefinition;
import io.github.tesla.filter.service.plugin.request.AuthRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.ClassUtils;
import io.github.tesla.filter.utils.JsonUtils;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

@AuthType(authType = "jarOauth", definitionClazz = JarAuthDefinition.class)
public class JarAuthRequestPlugin extends AuthRequestPlugin {

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        JarAuthDefinition definition = JsonUtils.json2Definition(filterParam, JarAuthDefinition.class);
        if (definition == null) {
            return null;
        }
        JarAuthRequestPlugin userFilter = ClassUtils.getUserJarFilterRule(JarAuthRequestPlugin.class.getName(),
            definition.getFileId(), getFileBytesByKey(definition.getFileId()));
        if (userFilter == null) {
            LOGGER.error(" request not found jar file ,fileId:" + definition.getFileId());
            return null;
        }
        userFilter.setSpringCloudDiscovery(this.getSpringCloudDiscovery());
        HttpResponse userResponse = userFilter.doFilter(servletRequest, (HttpRequest)realHttpObject);
        return userResponse;
    }

    /**
     * @desc: 上传的Jar包执行类需实现该方法
     * @method: doFilter
     * @param: [servletRequest,
     *             realHttpObject]
     * @return: io.netty.handler.codec.http.HttpResponse
     * @auther: zhipingzhang
     * @date: 2018/11/29 14:36
     */
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpRequest realHttpRequest) {
        return null;
    }
}
