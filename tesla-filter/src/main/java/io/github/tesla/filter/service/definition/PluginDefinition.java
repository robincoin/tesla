package io.github.tesla.filter.service.definition;

import java.util.Map;

import io.github.tesla.common.dto.EndpointDTO;
import io.github.tesla.common.dto.ServiceDTO;

public class PluginDefinition {

    public static final String FILEPREFIX = "file_";

    public static final String FILETABENDPOINT = "::";

    public static final String FILETABSERVICE = ":::";

    public final static String CONVERGE_ATTR_KEY = "QueryConvergeAttr";

    public final static String HOST_AND_PORT = "hostAndPort";

    public final static String ROUTER_PATH = "routerPath";

    public final static String CONVERGE_TAG = "convergeTag";

    public final static String X_TESLA_CONVERGE_TAG = "X-Tesla-Converge-Tag";

    public static final String X_TESLA_ENABLE_SSL = "X-Tesla-Enable-SSL";

    public static final String X_TESLA_SELF_SIGN_CRT = "X-Tesla-Self-Sign-Crt";

    public static final ThreadLocal<Map<String, byte[]>> UPLOADFILEMAP = new ThreadLocal<Map<String, byte[]>>();

    public String validate(String paramJson) {
        throw new UnsupportedOperationException("this operration is not support");
    }

    public String validate(String paramJson, ServiceDTO serviceDTO) {
        throw new UnsupportedOperationException("this operration is not support");
    }

    public String validate(String paramJson, ServiceDTO serviceDTO, EndpointDTO endpointDTO) {
        throw new UnsupportedOperationException("this operration is not support");
    }
}
