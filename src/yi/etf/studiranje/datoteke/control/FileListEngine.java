
package yi.etf.studiranje.datoteke.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;

import yi.etf.studiranje.datoteke.lang.FileListException;
import yi.etf.studiranje.datoteke.model.FileList;

/**
 * Механизам, односно контролер којим се управља скупом, односно листом 
 * коријенских датотека. 
 * @author Computer
 * @version 1.0
 */
public class FileListEngine {
	private FileList rootList; 
	private File config; 
	private Properties rootNames = new  Properties(); 
	
	public FileListEngine(File configFile) {
		config = configFile; 
		rootList = new FileList(); 
	}

	public File getConfig() {
		return config;
	}

	public void setConfig(File config) {
		this.config = config;
	}

	public FileList getRootList() {
		return rootList;
	}
	
	public synchronized FileListEngine load() {
		try {
			rootNames.load(new FileInputStream(config));
			TreeSet<File> roots = new TreeSet<>(); 
			for(var mEntry: getNameMap().entrySet()) {
				roots.add(new File(mEntry.getValue())); 
			}
			if(roots.size() != rootNames.entrySet().size())
				throw new RuntimeException("Неправилне конфигурације"); 
			rootList = new FileList(roots); 
		}catch(Exception ex){
			throw new RuntimeException(ex); 
		}
		return this;
	}
	
	public synchronized FileListEngine store() {
		try {
			rootNames.store(new FileOutputStream(config),""); 
		}catch(Exception ex) {
			throw new RuntimeException(ex); 
		}
		return this; 
	}
	
	public synchronized Map<String, String> getNameMap(){
		TreeMap<String, String> map = new TreeMap<>(); 
		for(Object key: rootNames.keySet()) {
			String k = key.toString(); 
			String v = rootNames.getProperty(k); 
			map.put(k,v);
		}
		return map; 
	}
	
	public synchronized FileListEngine addRoot(String name, File file) {
		try {
			if(rootNames.containsKey(name)) throw new RuntimeException("Дупло име за коријена стабла датотека.");
			rootList.addRoot(file); 
			rootNames.setProperty(name, file.getPath()); 
		}catch(FileListException ex) {
			throw ex; 
		}
		return this; 
	}
	
	public synchronized String getName(File file) {
		for(Object key: rootNames.keySet()) {
			if(file.equals(new File(rootNames.getProperty(key.toString())))) {
				return key.toString(); 
			}
		}
		return null; 
	}
	
	public synchronized FileListEngine removeRoot(String name) {
		if(rootNames.getProperty(name)==null) return this; 
		File file = new File(rootNames.getProperty(name)); 
		rootList.removeRoot(file); 
		rootNames.remove(name); 
		return this; 
	}
	
	public synchronized FileListEngine removeRoot(File file) {
		rootList.removeRoot(file); 
		rootNames.remove(getName(file));
		return this;
	}
}
