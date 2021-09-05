package com.payneteasy.jdbcproc.plugin.maven;

import java.io.File;

public class FileUtils {

    public static File createDirectories(File aFile) {
        if(aFile.exists()) {
            if(aFile.isDirectory()) {
                return aFile;
            } else {
                throw new IllegalStateException(aFile.getAbsolutePath()  + " already exists but not a directory");
            }
        }

        if(!aFile.mkdirs()) {
            throw new IllegalStateException("Cannot create dir " + aFile.getAbsolutePath());
        }

        return aFile;
    }
}
