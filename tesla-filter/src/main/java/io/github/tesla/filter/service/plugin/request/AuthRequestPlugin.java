package io.github.tesla.filter.service.plugin.request;

import java.nio.charset.Charset;
import java.util.Base64;

import io.github.tesla.filter.AbstractPlugin;
import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.service.annotation.AuthType;
import io.github.tesla.filter.service.definition.AuthCommonDefinition;
import io.github.tesla.filter.support.annnotation.ServiceRequestPlugin;
import io.github.tesla.filter.support.enums.YesOrNoEnum;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.ClassUtils;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

@ServiceRequestPlugin(filterType = "AuthRequestPlugin", definitionClazz = AuthCommonDefinition.class, filterOrder = 3,
    filterName = "权限验证插件")
public class AuthRequestPlugin extends AbstractRequestPlugin {
    protected void claims2TransferHeader(String claims, HttpObject httpObject, String headerKey) {
        final FullHttpRequest realRequest = (FullHttpRequest)httpObject;
        realRequest.headers().add(headerKey,
            new String(Base64.getEncoder().encode(claims.getBytes(Charset.forName("UTF-8")))));
    }

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        String filterParam) {
        AuthCommonDefinition authDefinition = JsonUtils.json2Definition(filterParam, AuthCommonDefinition.class);
        // 不启用auth，则返回
        if (authDefinition == null || YesOrNoEnum.NO.getCode().equals(authDefinition.getEnabled())) {
            return null;
        }
        // 根据注解得到对应的实现filter，并执行
        try {
            AuthRequestPlugin authRequestPlugin = ClassUtils.getSingleBeanWithAnno(
                this.getClass().getPackage().getName(), AuthType.class, authDefinition.getAuthType(), "value");
            return authRequestPlugin.doFilter(servletRequest, realHttpObject, authDefinition.getAuthParamJson());
        } catch (Exception e) {
            AbstractPlugin.LOGGER.error(e.getMessage(), e);
            PluginUtil.writeFilterLog(AuthRequestPlugin.class, e.getMessage(), e);
            return PluginUtil.createResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, servletRequest.getNettyRequest(),
                "auth filter error " + e.getMessage());
        }
    }

    protected static class ResponseMessage {
        public enum MesageCodeType {

            OSG001("Token expire"), OSG002("Wrong Token"), OSG003(" Token has not exist");

            private String code;

            MesageCodeType(String code) {
                this.code = code;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

        }

        private MesageCodeType code;

        private String message;

        private String content;

        public ResponseMessage(MesageCodeType code, String message) {
            super();
            this.code = code;
            this.message = message;
        }

        public MesageCodeType getCode() {
            return code;
        }

        public String getContent() {
            return content;
        }

        public String getMessage() {
            return message;
        }

        public void setCode(MesageCodeType code) {
            this.code = code;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
