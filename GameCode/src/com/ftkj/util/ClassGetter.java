package com.ftkj.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.google.common.collect.Lists;

public class ClassGetter {

    private String basePackage;
    private ClassLoader cl;

    public ClassGetter(String basePackage) {
        this.basePackage = basePackage;
        this.cl = getClass().getClassLoader();

    }

    public ClassGetter(String basePackage, ClassLoader cl) {
        this.basePackage = basePackage;
        this.cl = cl;
    }
    
    public List<String> getFullyQualifiedClassNameList() throws IOException {
    	List<String> result = Lists.newArrayList();
    	doScan(basePackage, result);
        return result;
    }

    private void doScan(String basePackage, List<String> nameList) throws IOException {
        String splashPath = StringUtil.dotToSplash(basePackage);

        Enumeration<URL> list = cl.getResources(splashPath);
        URL url = null;
        for (; list.hasMoreElements();){
        	url = list.nextElement();
	        if(url == null) return;
	        String filePath = StringUtil.getRootPath(url);
	        List<String> names = null; 
	        boolean isJar = false;
	        if (isJarFile(filePath)) {
	        	isJar = true;
	            names = readFromJarFile(filePath, splashPath);//jar包
	        } else {
	            names = readFromDirectory(filePath);//目录
	        }
	
	        for (String name : names) {
	            if (isClassFile(name)) {
	                nameList.add(toFullyQualifiedName(name, basePackage,isJar));
	            } else {
	                doScan(basePackage + "." + name, nameList);
	            }
	        }
        }
    }

    private String toFullyQualifiedName(String shortName, String basePackage,boolean isJar) {
    	if(isJar) {
    		String name = StringUtil.trimExtension(shortName);
    		return name.replaceAll("/", ".");
    	}
    	int indx  = shortName.lastIndexOf("/");
    	String v = shortName.substring(indx+1, shortName.length());
        StringBuilder sb = new StringBuilder(basePackage);
        sb.append('.');
        sb.append(StringUtil.trimExtension(v));
        return sb.toString();
    }

    private List<String> readFromJarFile(String jarPath, String splashedPackageName) throws IOException {

        JarInputStream jarIn = new JarInputStream(new FileInputStream(jarPath));
        JarEntry entry = jarIn.getNextJarEntry();

        List<String> nameList = new ArrayList<>();
        while (null != entry) {
            String name = entry.getName();
            if (name.startsWith(splashedPackageName) && isClassFile(name)) {
                nameList.add(name);
            }

            entry = jarIn.getNextJarEntry();
        }

        jarIn.close();
        return nameList;
    }

    private List<String> readFromDirectory(String path) {
        File file = new File(path);
        String[] names = file.list();

        if (null == names) {
            return null;
        }

        return Arrays.asList(names);
    }

    private boolean isClassFile(String name) {
        return name.endsWith(".class");
    }

    private boolean isJarFile(String name) {
        return name.endsWith(".jar");
    }

    /**
     * For test purpose.
     */
    public static void main(String[] args) throws Exception {
    	long time = System.currentTimeMillis();
    	ClassGetter scan = new ClassGetter("com.ftkj.manager");
        List<String> list = scan.getFullyQualifiedClassNameList();
        for(String val : list)
        	System.out.println(val);
        System.out.println(list.size()+"个类,耗时:"+(System.currentTimeMillis()-time));
    }

}