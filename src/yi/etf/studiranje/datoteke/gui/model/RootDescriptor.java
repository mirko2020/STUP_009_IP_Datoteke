package yi.etf.studiranje.datoteke.gui.model;

import java.io.File;
import java.io.Serializable;

import yi.etf.studiranje.datoteke.util.FileContentUtil;
import yi.etf.studiranje.datoteke.util.FileUtil;

/**
 * Опсник коријена за датотеке. 
 * @author Computer
 * @version 1.0
 */
public class RootDescriptor implements Serializable, Comparable<RootDescriptor>{
	private static final long serialVersionUID = -6125599156281105923L;
	private File file; 
	private String name; 
	private String path; 
	private String description; 
	
	public RootDescriptor(String name, File file) {
		this.file = file; 
		this.name = name;
		this.path = FileUtil.getPath(file);
		this.description = ""; 
		if(file.isFile()) {
			this.description = "[FIlE "+file.length()+"]";
		}else if(file.isDirectory()) {
			this.description = "[DIR "+
				FileContentUtil.count(file)+"="
			  + FileContentUtil.countFolders(file)+"+"
			  + FileContentUtil.countFiles(file)+"]";
		}
	}
	
	public File getFile() {
		return file; 
	}
	
	public String getName() {
		return name; 
	}
	
	public String getPath() {
		return path; 
	}
	
	public String getDescription() {
		return description; 
	}
	
	public String originalString() {
		return super.toString(); 
	}
	
	public int originalCode() {
		return super.hashCode(); 
	}
	
	@Override
	public int compareTo(RootDescriptor o) {
		return name.compareTo(o.getName());
	}
	
	public boolean equals(Object object) {
		if(object instanceof RootDescriptor) {
			RootDescriptor root = (RootDescriptor) object; 
			return name.equals(root.name); 
		}
		return false; 
	}
	
	@Override 
	public String toString() {
		return name.toString(); 
	}
}
