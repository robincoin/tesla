package io.github.tesla.filter.support.classLoader;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class BytesClassLoader extends ClassLoader {

    private BytesMap bytes;

    public BytesClassLoader(BytesMap bytes) {
        this(bytes, null);
    }

    public BytesClassLoader(BytesMap bytes, ClassLoader parent) {
        super(parent);
        this.bytes = bytes;
    }

    public BytesClassLoader(byte[] jarBytes) {
        this(jarBytes, BytesClassLoader.class.getClassLoader());
    }

    public BytesClassLoader(byte[] jarBytes, ClassLoader parent) {
        super(parent);
        this.bytes = load(jarBytes);
    }

    protected BytesMap load(byte[] bytes) {
        BytesMap bmap = new BytesMap();
        JarInputStream jar = null;
        try {
            jar = new JarInputStream(new ByteArrayInputStream(bytes));
            for (JarEntry ent = jar.getNextJarEntry(); ent != null; ent = jar.getNextJarEntry()) {
                byte[] buff = FileUtil.readAll(jar);
                if (buff != null) {
                    bmap.put(ent.getName(), buff);
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                if (jar != null) {
                    jar.close();
                }
            } catch (Throwable e) {

            }
        }
        return bmap;
    }

    protected Class<?> findClass(String classname) throws ClassNotFoundException {
        String name = classname.replace('.', '/') + ".class";
        try {
            Class<?> clazz = super.findClass(name);
            if (clazz != null) {
                return clazz;
            }
        } catch (ClassNotFoundException e) {
            // ignore
        }
        byte[] b = bytes.getBytes(name);
        if (b == null) {
            throw new ClassNotFoundException("not found class " + classname);
        }
        return defineClass(classname, b, 0, b.length, null);
    }

    protected URL findResource(String name) {
        return bytes.getResource(name);
    }
}
