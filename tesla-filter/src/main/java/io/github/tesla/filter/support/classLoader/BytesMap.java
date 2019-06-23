package io.github.tesla.filter.support.classLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

public class BytesMap {
    private final Map<String, byte[]> table = Maps.newConcurrentMap();

    public void put(String name, byte[] data) {
        if (name == null || data == null) {
            return;
        }
        table.put(name, data);
    }

    public void remove(String name) {
        if (name == null) {
            return;
        }
        table.remove(name);
    }

    public byte[] getBytes(String name) {
        return table.get(name);
    }

    public List<URL> getResourceList() {
        List<URL> urlList = new ArrayList<URL>();
        Iterator<String> itr = table.keySet().iterator();
        while (itr.hasNext()) {
            String key = itr.next();
            urlList.add(getResource(key));
        }
        return urlList;
    }

    public URL getResource(String name) {
        try {
            return new URL(null, "bytes:///" + name, urlStreamHandler);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    protected URLStreamHandler urlStreamHandler = new URLStreamHandler() {
        protected URLConnection openConnection(URL u) throws IOException {
            return new URLConnection(u) {
                public void connect() throws IOException {

                }

                public InputStream getInputStream() throws IOException {
                    String name = this.getURL().getPath().substring(1);
                    byte[] b = table.get(name);
                    if (b == null) {
                        throw new IOException("unknown bytes name : " + name);
                    }
                    return new ByteArrayInputStream(b);
                }

            };
        }
    };
}
