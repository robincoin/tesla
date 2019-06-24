package io.github.tesla.filter;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.hazelcast.core.HazelcastInstance;

import io.github.tesla.common.dto.ServiceRouterDTO;
import io.github.tesla.common.service.GatewayApiTextService;
import io.github.tesla.common.service.SpringContextHolder;
import io.github.tesla.filter.common.definition.CacheConstant;
import io.github.tesla.filter.support.springcloud.SpringCloudDiscovery;

public class AbstractPlugin {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractPlugin.class);

    private static final Map<String, byte[]> JAR_CACHE = Maps.newHashMap();

    private static final Map<String, ServiceRouterDTO> ROUTE_CACHE = Maps.newHashMap();

    private static HazelcastInstance hazelcastInstance;

    private static final Map<String, Map<String, String>> APPKEYLOCALDEFINITIONMAP = Maps.newHashMap();

    public static Map<String, Map<String, String>> getAppKeyMap() {
        return APPKEYLOCALDEFINITIONMAP;
    }

    public static void clearLocalCache() {
        JAR_CACHE.clear();
        ROUTE_CACHE.clear();
    }

    public static byte[] getFileBytesByKey(String key) {
        try {
            CacheConstant.READ_WRITE_LOCK.readLock().lock();
            if (JAR_CACHE.get(key) == null) {
                JAR_CACHE.put(key, (byte[])getHazelcastInstance().getMap(CacheConstant.FILE_CACHE_MAP).get(key));
            }
            return JAR_CACHE.get(key);
        } finally {
            CacheConstant.READ_WRITE_LOCK.readLock().unlock();
        }
    }

    public static HazelcastInstance getHazelcastInstance() {
        if (AbstractPlugin.hazelcastInstance == null) {
            AbstractPlugin.hazelcastInstance = SpringContextHolder.getBean(HazelcastInstance.class);
        }
        return hazelcastInstance;
    }

    public static ServiceRouterDTO getRouterByServiceId(String serviceId) {
        try {
            CacheConstant.READ_WRITE_LOCK.readLock().lock();
            if (ROUTE_CACHE.get(serviceId) == null) {
                ServiceRouterDTO routeDTO = SpringContextHolder.getBean(GatewayApiTextService.class)
                    .loadGatewayServiceByServiceId(serviceId).getRouterDTO();
                ROUTE_CACHE.put(serviceId, routeDTO);
                return ROUTE_CACHE.get(serviceId);
            }
            return ROUTE_CACHE.get(serviceId);
        } finally {
            CacheConstant.READ_WRITE_LOCK.readLock().unlock();
        }
    }

    public static void setHazelcastInstance(HazelcastInstance hz) {
        hazelcastInstance = hz;
    }

    private SpringCloudDiscovery springCloudDiscovery;

    public Map<String, String> getAppKeyMap(String appKey) {
        return APPKEYLOCALDEFINITIONMAP.get(appKey);
    }

    public SpringCloudDiscovery getSpringCloudDiscovery() {
        return springCloudDiscovery;
    }

    public void setSpringCloudDiscovery(SpringCloudDiscovery springCloudDiscovery) {
        this.springCloudDiscovery = springCloudDiscovery;
    }
}
