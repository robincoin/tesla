package io.github.tesla.filter.plugin.request.myapp;

import io.github.tesla.filter.endpoint.plugin.request.JarExecuteRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.ProxyUtils;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * @Auther: zhipingzhang
 * @Date: 2018/10/29 11:21:
 * @Description: 需要自己编写的入请求过滤器代码，当前demo的类路径为 io.github.tesla.filter.plugin.request.myapp 自己编写时，包路径中的myapp换成自己的项目名称, 类名
 *               MyRequestPlugin 也尽量命令为和过滤器规则相符合的名称
 *               <p>
 *               请注意，myapp下面的plugin将不会被扫描到，请不要在该demo目录下编写自己的规则
 */
public class MyRequestPlugin extends JarExecuteRequestPlugin {

    private final RedisClient redisClient;

    public MyRequestPlugin() {
        this.redisClient = RedisClient.create("redis://localhost:6379");
    }

    /**
     * 功能描述: 具体的过滤规则代码
     *
     * @parmname: doFilter
     * @param: [servletRequest,
     *             realHttpObject]
     * @return: io.netty.handler.codec.http.HttpResponse
     *          实现具体filter规则，如果返回null，则会继续走之后的filter,如果返回了response，则会中断后续filter直接返回给调用方
     * @auther: zhipingzhang
     * @date: 2018/10/29 11:24
     */
    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpRequest realHttpRequest) {
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisStringCommands sync = connection.sync();
        sync.set("testKey", "testKey");
        String value = (String)sync.get("testKey");
        FullHttpResponse response =
            ProxyUtils.createFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, value);
        return response;
    }
}
