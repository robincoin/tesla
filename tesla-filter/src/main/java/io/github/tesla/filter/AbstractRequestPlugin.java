/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.github.tesla.filter;

import org.apache.commons.lang3.StringUtils;

import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.PluginUtil;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public abstract class AbstractRequestPlugin extends AbstractPlugin {

    protected HttpResponse createResponse(HttpResponseStatus httpResponseStatus, HttpRequest originalRequest) {
        return PluginUtil.createResponse(httpResponseStatus, originalRequest, StringUtils.EMPTY);
    }

    public abstract HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        String filterParam);

    protected void writeFilterLog(Class<?> type, String reason, Throwable... cause) {
        PluginUtil.writeFilterLog(type, reason, cause);
    }
}
