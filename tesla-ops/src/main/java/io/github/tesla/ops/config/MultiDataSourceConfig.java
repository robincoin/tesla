package io.github.tesla.ops.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Maps;

import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.ops.common.MultiDataSourceSwitcher;

@Configuration
public class MultiDataSourceConfig {

    @Value("${tesla.datasource.url}")
    private String dataSourceUrl;

    @Value("${tesla.datasource.name}")
    private String dataSourceName;

    @Value("${tesla.datasource.username}")
    private String dataSourceUsername;

    @Value("${tesla.datasource.password}")
    private String dataSourcePassword;

    @Bean("multiDataSourceSwitcher")
    public MultiDataSourceSwitcher getDataSourceSwitcher() {
        MultiDataSourceSwitcher multiDataSourceSwitcher = new MultiDataSourceSwitcher();
        initMultiDataSource(multiDataSourceSwitcher);
        return multiDataSourceSwitcher;
    }

    public void initMultiDataSource(MultiDataSourceSwitcher multiDataSourceSwitcher) {
        if (JsonUtils.isJson(dataSourceUrl)) {
            initMultiDataSourceWithMapConfig(multiDataSourceSwitcher);
        } else {
            initMultiDataSourceWithStringConfig(multiDataSourceSwitcher);
        }
    }

    public void initMultiDataSourceWithStringConfig(MultiDataSourceSwitcher multiDataSourceSwitcher) {
        Map<Object, Object> multiDataSources = Maps.newConcurrentMap();
        Map<String, String> multiDataSourceShowNames = Maps.newConcurrentMap();
        DataSourceProperties properties = new DataSourceProperties();
        properties.setUrl(dataSourceUrl);
        properties.setUsername(dataSourceUsername);
        properties.setPassword(dataSourcePassword);
        rewriteDruidDataSourceProperties(properties);
        DruidDataSource dataSource = build(properties);
        if (dataSource != null) {
            multiDataSources.put("local", dataSource);
        }
        multiDataSourceShowNames.put("local", dataSourceName);
        multiDataSourceSwitcher.setTargetDataSources(multiDataSources);
        multiDataSourceSwitcher.setMultiDataSourceNames(multiDataSourceShowNames);
        multiDataSourceSwitcher.setDefaultTargetDataSource(multiDataSources.get("local"));
    }

    public void initMultiDataSourceWithMapConfig(MultiDataSourceSwitcher multiDataSourceSwitcher) {
        Map<Object, Object> multiDataSources = Maps.newConcurrentMap();
        Map<String, String> multiDataSourceShowNames = Maps.newConcurrentMap();
        Map<String, String> multiDataSourceUrlMap = JsonUtils.fromJson(dataSourceUrl, HashMap.class);
        Map<String, String> multiDataSourceUsernameMap = JsonUtils.fromJson(dataSourceUsername, HashMap.class);
        Map<String, String> multiDataSourcePasswordMap = JsonUtils.fromJson(dataSourcePassword, HashMap.class);
        Map<String, String> multiDataSourceNameMap = JsonUtils.fromJson(dataSourceName, HashMap.class);
        multiDataSourceUrlMap.entrySet().forEach(entry -> {
            String configKey = entry.getKey();
            DataSourceProperties properties = new DataSourceProperties();
            properties.setUrl(multiDataSourceUrlMap.get(configKey));
            properties.setUsername(multiDataSourceUsernameMap.get(configKey));
            properties.setPassword(multiDataSourcePasswordMap.get(configKey));
            rewriteDruidDataSourceProperties(properties);
            DruidDataSource dataSource = build(properties);
            if (dataSource != null) {
                multiDataSources.put(configKey, dataSource);
            }
            multiDataSourceShowNames.put(configKey, multiDataSourceNameMap.get(configKey));
        });
        multiDataSourceSwitcher.setTargetDataSources(multiDataSources);
        multiDataSourceSwitcher.setMultiDataSourceNames(multiDataSourceShowNames);
        multiDataSourceSwitcher.setDefaultTargetDataSource(multiDataSources.getOrDefault("default", multiDataSources.values().toArray()[0]));
    }

    private void rewriteDruidDataSourceProperties(DataSourceProperties properties) {
        if (properties.getDriverClassName() == null) {
            properties.setDriverClassName("com.mysql.jdbc.Driver");
        }
    }

    DruidDataSource build(DataSourceProperties properties) {
        DruidDataSource dataSource = new DruidDataSource();
        if (properties.getUrl() != null) {
            dataSource.setUrl(properties.getUrl());
        }
        if (properties.getUsername() != null) {
            dataSource.setUsername(properties.getUsername());
        }
        if (properties.getPassword() != null) {
            dataSource.setPassword(properties.getPassword());
        }
        if (properties.getDriverClassName() != null) {
            dataSource.setDriverClassName(properties.getDriverClassName());
        }
        dataSource.setInitialSize(10);
        dataSource.setMinIdle(5);
        dataSource.setMaxActive(100);
        dataSource.setMaxWait(60000L);
        dataSource.setTimeBetweenEvictionRunsMillis(1800000L);
        dataSource.setMinEvictableIdleTimeMillis(1800000L);
        dataSource.setValidationQuery("SELECT 1 FROM DUAL");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(false);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(30);
        dataSource.setKeepAlive(true);
        return dataSource;
    }

}
