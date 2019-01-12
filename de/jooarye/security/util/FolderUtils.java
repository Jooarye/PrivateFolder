package de.jooarye.security.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public class FolderUtils {
    public static ArrayList<File> listFolders(File dir) {
        FileFilter directoryFileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        File[] directoryListAsFile = dir.listFiles(directoryFileFilter);
        ArrayList<File> foldersInDirectory = new ArrayList<File>(directoryListAsFile.length);
        File[] arrayOfFile1;
        int j = (arrayOfFile1 = directoryListAsFile).length;
        for (int i = 0; i < j; i++) {
            File directoryAsFile = arrayOfFile1[i];
            foldersInDirectory.addAll(listFolders(directoryAsFile));
            foldersInDirectory.add(directoryAsFile);
        }
        return foldersInDirectory;
    }

    public static Collection<File> listFiles(File folder) {
        return FileUtils.listFiles(folder, null, true);
    }
}