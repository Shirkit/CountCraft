package com.shirkit.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileUtils {

	public static ArrayList<String> getFileNamesInJar(File source, String folder, String extension) {
		ArrayList<String> list = new ArrayList();
		try {
			JarFile jarFile = new JarFile(source);
			Enumeration e = jarFile.entries();
			while (e.hasMoreElements()) {
				JarEntry je = (JarEntry) e.nextElement();
				if (je.isDirectory() || !je.getName().endsWith(extension) || !je.getName().startsWith(folder)) {
					continue;
				}
				list.add(je.getName());
			}
			jarFile.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return list;
	}

}
