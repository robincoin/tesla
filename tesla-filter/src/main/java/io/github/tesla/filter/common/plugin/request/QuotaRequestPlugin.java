package io.github.tesla.filter.common.plugin.request;

import java.util.Date;
import java.util.Map;

import com.hazelcast.core.IAtomicLong;

import io.github.tesla.filter.AbstractPlugin;
import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.common.definition.QuotaDefinition;
import io.github.tesla.filter.support.annnotation.AppKeyRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

@AppKeyRequestPlugin(filterType = "QuotaRequestPlugin", filterOrder = 2, filterName = "访问API总流量限制插件")
public class QuotaRequestPlugin extends AbstractRequestPlugin {

    private static Map<String, Date> quotaTimeCache;

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {

        QuotaDefinition definition = JsonUtils.json2Definition(filterParam, QuotaDefinition.class);
        if (definition == null) {
            return null;
        }
        if (definition.getMaxRequest() < 0) {
            return null;
        }
        IAtomicLong atomicLong = AbstractPlugin.getHazelcastInstance().getAtomicLong(definition.getAppId());
        if (quotaTimeCache == null) {
            quotaTimeCache = AbstractPlugin.getHazelcastInstance().getMap("QUOTA_TIME_CACHE");
        }
        if (quotaTimeCache.get(definition.getAppId()) == null) {
            quotaTimeCache.put(definition.getAppId(), new Date());
            atomicLong.set(1);
        } else if (System.currentTimeMillis() - quotaTimeCache.get(definition.getAppId()).getTime() > definition
            .getIntervalMillis()) {
            atomicLong.set(1);
        }
        // 单位时间超过最大请求
        if (atomicLong.incrementAndGet() > definition.getMaxRequest()) {
            String reason = servletRequest.getRequestURI() + " QuotaRequestPlugin Filter has limited maxRequest "
                + atomicLong.get();
            PluginUtil.writeFilterLog(QuotaRequestPlugin.class, reason);
            final HttpRequest nettyRequest = servletRequest.getNettyRequest();
            return PluginUtil.createResponse(HttpResponseStatus.TOO_MANY_REQUESTS, nettyRequest, reason);
        }
        return null;
    }
}
