package io.github.tesla.common.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@SuppressWarnings("unchecked")
public class SpringContextHolder
    implements DisposableBean, ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger log = LoggerFactory.getLogger(SpringContextHolder.class);
    private static ApplicationContext applicationContext = null;
    private static Boolean enableWaf = false;
    private static Boolean enabledMetrcis = false;

    public static void clearHolder() {
        applicationContext = null;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(Class<T> requiredType) {
        try {
            return applicationContext.getBean(requiredType);
        } catch (NoSuchBeanDefinitionException exception) {
            log.debug("not found bean in spring cotext", exception);
            return null;
        }

    }

    public static <T> T getBean(String name) {
        return (T)applicationContext.getBean(name);
    }

    public static Boolean isEnableWaf() {
        return enableWaf;
    }

    public static Boolean isEnabledMetrcis() {
        return enabledMetrcis;
    }

    @Override
    public void destroy() throws Exception {
        SpringContextHolder.clearHolder();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        SpringContextHolder.applicationContext = applicationContext;
        Environment environment = applicationContext.getEnvironment();
        SpringContextHolder.enableWaf = environment.getProperty("filter.enabledWaf", Boolean.class, Boolean.FALSE);
        SpringContextHolder.enableWaf = environment.getProperty("filter.enabledMetrcis", Boolean.class, Boolean.FALSE);
    }
}
