/**
 * module class loader
 * greatly inspired form http://twit88.com/blog/2007/10/08/develop-a-java-plugin-framework-search-for-plugin-dynamically/
 */
package com.dipole.jwavetool.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.dipole.jwavetool.common.Log;

@SuppressWarnings("unchecked")
public class ModulesLoader {

	private ArrayList<ModuleInterface> moduleList;
	
	public ModulesLoader() {
		this.moduleList  = new ArrayList <ModuleInterface> (5);
	}

	/**
	 * find modules in moulePath
	 * @param modulePath
	 * @throws Exception
	 */
	public void search(final String modulePath) throws Exception {
		Log.trace("Enter ModulesLoader.seach: "+ modulePath);
		
		File dir = new File(modulePath);
		File[] jars;		
		
		//check if is directory
		if(dir.isFile()) {
			return;
		}
		
		jars = dir.listFiles(new jarFilter());
		for(File file : jars) {
			ArrayList <String> classList = this.loadClass(file.getAbsolutePath());
			
			for(String className : classList){
				//remove extension
				String name = className.substring(0, className.length() - 6);
				
				Class tmpClass = this.getClass(file, name);
				Class[] interfaces = tmpClass.getInterfaces();
				
				if(tmpClass.getSuperclass().getName().equals("com.dipole.jwavetool.modules.AbstractModule")) {
						this.moduleList.add((ModuleInterface) tmpClass.newInstance());
						Log.debug("Found AbstractModule child: "+ tmpClass.getCanonicalName());
				} else {
					for(Class inter : interfaces){
						
						if(inter.getName().equals("com.dipole.jwavetool.Modules.ModuleInterface")) {
							this.moduleList.add((ModuleInterface) tmpClass.newInstance());
							Log.debug("Found ModuleInterface child: "+ tmpClass.getCanonicalName());
						}
					}
				}
			}
		}
		
		Log.trace("Quit ModulesLoader.search\n");
	}
	
	/**
	 * 
	 * @param jar
	 * @return
	 * @throws IOException
	 */
	protected ArrayList <String> loadClass(final String jar) throws IOException {
		Log.trace("Enter ModulesLoader.loadClass: "+ jar);
		
		ArrayList <String> classes = new ArrayList <String> ();
		JarInputStream jarInStream = new JarInputStream(new FileInputStream(jar));
		JarEntry jarEntry;
		
		Log.debug("open: "+ jar);
		while(true){
			jarEntry = jarInStream.getNextJarEntry();
			
			if(jarEntry == null) {
				break;
			}
			
			if(jarEntry.getName().endsWith(".class")) {
				Log.debug("add: "+ jarEntry.getName());
				classes.add(jarEntry.getName().replaceAll("/", "."));
			}
		}
		
		Log.trace("Quit ModulesLoader.loadClass: "+ classes +"\n");
		
		return classes;
	}
	
	/**
	 * 
	 * @param file
	 * @param className
	 * @return
	 * @throws Exception
	 */
	protected Class getClass(final File file, final String className) throws Exception {
        URLClassLoader classLoader;
        Class tmpClass;
        String filePath;
        URL url;
        
		this.addUrl(file.toURI().toURL());

        filePath = "jar:file://"+ file.getAbsolutePath() +"!/";
        
        url = new File(filePath).toURI().toURL();
        classLoader = new URLClassLoader(new URL[]{url});
        tmpClass = classLoader.loadClass(className);
        return tmpClass;
	}
	
	/**
	 * 
	 * @param url
	 * @throws IOException
	 */
	protected void addUrl(final URL url) throws IOException {
		URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		URL[] sysUrls = loader.getURLs();
		
		//check if URL is not present in classPath
		for(int i = 0; i < sysUrls.length; i++) {
			if (sysUrls[i].toString().equalsIgnoreCase(url.toString())) {
				return;
			}
		}
		
		//add URL to classPath
		Class sysclass = URLClassLoader.class;
        try {
        	Method method = sysclass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(loader, new Object[]{url});
        } catch (Throwable t) {
            Log.fatal(t.getMessage());
            throw new IOException("Error, could not add URL to system classloader");
        }
	}
	
	/**
	 * 
	 * @param moduleList
	 */
	public void setModuleList(final ArrayList<ModuleInterface> moduleList) {
		this.moduleList = moduleList;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<ModuleInterface> getModuleList() {
		return moduleList;
	}

	private class jarFilter implements FilenameFilter{

		@Override
		public boolean accept(final File dir, final String name) {
			return name.endsWith(".jar");
		}
		
	}
}
