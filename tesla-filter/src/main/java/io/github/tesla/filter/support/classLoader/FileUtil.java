package io.github.tesla.filter.support.classLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class FileUtil {
    public static InputStream close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (Throwable t) {
                // ignore
            }
        }

        return null;
    }

    public static OutputStream close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (Throwable t) {
                // ignore
            }
        }

        return null;
    }

    public static Reader close(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (Throwable t) {
                // ignore
            }
        }

        return null;
    }

    public static Writer close(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (Throwable t) {
                // ignore
            }
        }

        return null;
    }

    public static byte[] readAll(InputStream fin) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buff = new byte[4096];
        int n = fin.read(buff);
        while (n >= 0) {
            out.write(buff, 0, n);
            n = fin.read(buff);
        }
        return out.toByteArray();
    }
}
