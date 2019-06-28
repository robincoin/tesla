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
package io.github.tesla.filter.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import com.google.common.collect.Maps;

import io.github.tesla.filter.AbstractPlugin;
import io.github.tesla.filter.support.classLoader.BytesClassLoader;

@SuppressWarnings("unchecked")
public final class ClassUtils {
    private static final Map<String, Reflections> REFLECTIONS_CACHE = Maps.newConcurrentMap();
    private static final Map<String, Object> BEAN_CACHE = Maps.newConcurrentMap();
    private static final Map<Pair<String, String>, AbstractPlugin> USER_RULE_JAR_FILTER_CACHE = Maps.newConcurrentMap();
    private static final Map<String, Set<Class<?>>> CLASS_CACHE = Maps.newConcurrentMap();
    private static final Logger log = LoggerFactory.getLogger(ClassUtils.class);

    private ClassUtils() {}

    public static void cleanCacheBean() {
        USER_RULE_JAR_FILTER_CACHE.clear();
    }

    private static Set<Class<?>> convert(ClassLoader loader, Resource[] resources) {
        Set<Class<?>> classSet = new HashSet<>(resources.length);
        for (Resource resource : resources) {
            Class<?> clazz = loadClass(loader, resource);
            if (clazz != null) {
                classSet.add(clazz);
            }
        }
        return classSet;
    }

    public static Set<Class<?>> findAllClasses(String scanPackages, Class<? extends Annotation> anno) {
        Set<Class<?>> allClasses;
        if (CLASS_CACHE.containsKey(scanPackages)) {
            allClasses = CLASS_CACHE.get(scanPackages);
        } else {
            allClasses = findAllClasses(scanPackages);
            CLASS_CACHE.put(scanPackages, allClasses);
        }
        Set<Class<?>> filterCLasses = allClasses.stream()
            .filter(clazz -> AnnotationUtils.isAnnotationDeclaredLocally(anno, clazz)).collect(Collectors.toSet());
        return filterCLasses;
    }

    public static Set<Class<?>> findAllClasses(String scanPackages) {
        ClassLoader loader = org.springframework.util.ClassUtils.getDefaultClassLoader();
        Resource[] resources = new Resource[0];
        try {
            resources = scan(loader, scanPackages);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convert(loader, resources);
    }

    public static Class<?> getClass(String className) {
        try {
            ClassLoader loader = org.springframework.util.ClassUtils.getDefaultClassLoader();
            return org.springframework.util.ClassUtils.forName(className, loader);
        } catch (LinkageError | ClassNotFoundException e) {
            return null;
        } catch (Throwable e) {
            return null;
        }
    }

    public static <T> T getFilterObject(byte[] filterJarByte, String className) {
        try {
            BytesClassLoader jarStreamClassLoader = new BytesClassLoader(filterJarByte);
            Class<?> clazz = jarStreamClassLoader.loadClass(className);
            if (clazz == null) {
                return null;
            }
            T object = (T)clazz.newInstance();
            return object;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> T getSingleBeanWithAnno(String packageName, Class<? extends Annotation> annotation, String value,
        String methodName) throws Exception {
        String key = packageName + "_" + annotation.getName() + "_" + value + "_" + methodName;
        if (BEAN_CACHE.get(key) == null) {
            synchronized (key) {
                if (BEAN_CACHE.get(key) == null) {
                    if (REFLECTIONS_CACHE.get(packageName) == null) {
                        REFLECTIONS_CACHE.put(packageName, new Reflections(packageName));
                    }
                    Set<Class<?>> classesList = REFLECTIONS_CACHE.get(packageName).getTypesAnnotatedWith(annotation);
                    for (Class<?> classes : classesList) {
                        if (annotation.getDeclaredMethod(methodName)
                            .invoke(AnnotationUtils.findAnnotation(classes, annotation)).equals(value)) {
                            try {
                                BEAN_CACHE.put(key, classes.newInstance());
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                                throw e;
                            }
                        }
                    }
                }
            }
        }
        return (T)BEAN_CACHE.get(key);
    }

    public static <T extends AbstractPlugin> T getUserJarFilterRule(String className, String filterId,
        byte[] filterJarByte) {
        Pair<String, String> classNameAndFilterId = new ImmutablePair<String, String>(className, filterId);
        if (USER_RULE_JAR_FILTER_CACHE.get(classNameAndFilterId) == null) {
            if (filterJarByte != null && filterJarByte.length > 0) {
                T userFilter = getFilterObject(filterJarByte, className);
                if (userFilter == null) {
                    return null;
                }
                USER_RULE_JAR_FILTER_CACHE.put(classNameAndFilterId, userFilter);
            }
        }
        return (T)USER_RULE_JAR_FILTER_CACHE.get(classNameAndFilterId);
    }

    private static Class<?> loadClass(ClassLoader loader, Resource resource) {
        try {
            CachingMetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(loader);
            MetadataReader reader = metadataReaderFactory.getMetadataReader(resource);
            return org.springframework.util.ClassUtils.forName(reader.getClassMetadata().getClassName(), loader);
        } catch (LinkageError | ClassNotFoundException e) {
            log.error("Ignoring candidate class resource " + resource + " due to " + e);
            return null;
        } catch (Throwable e) {
            log.error("Unexpected failure when loading class resource " + resource, e);
            return null;
        }
    }

    private static Resource[] scan(ClassLoader loader, String packageName) throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(loader);
        String pattern = "classpath*:" + org.springframework.util.ClassUtils.convertClassNameToResourcePath(packageName)
            + "/**/*.class";
        return resolver.getResources(pattern);
    }

}
