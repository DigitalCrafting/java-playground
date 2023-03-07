package org.digitalcrafting.javaPlayground.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    public static byte[] readBytes(String aPath) throws IOException {
        InputStream is = FileUtils.class.getClassLoader().getResourceAsStream(aPath);
        return IOUtils.toByteArray(is);
    }
}
