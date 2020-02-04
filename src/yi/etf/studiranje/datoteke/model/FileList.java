package yi.etf.studiranje.datoteke.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import yi.etf.studiranje.datoteke.lang.FileListException;
import yi.etf.studiranje.datoteke.util.FileListChecker;
import yi.etf.studiranje.datoteke.util.FileUtil;

/**
 * Користи се као управљачки механизам за прегле и отварање и скривање датотека,
 * и сепарацију, али и као листа коријенских датотека. Правила за коријенске датотеке
 * не смију бити у релацији подфолдери и надфолдери. Коријенски дио је неизмјеив, а 
 * измјена листе се може једионо промјенити отварањем и затварањем подфолдера. 
 * Тако да коријенска листа у реалној је увијек садржана. Ипак дозвољене су операције 
 * додавања коријена и брисања коријена. 
 * @author Computer
 * @version 1.0
 */
public class FileList implements Serializable{
	private static final long serialVersionUID = -2745452311151430842L;
	private HashMap<String, File> roots = new HashMap<>(); 
	private TreeMap<String, File> files = new TreeMap<>(); 
	
	public FileList(File ... files) {
		if(!FileListChecker.noSubfolders(files)) 
			throw new FileListException("Неконзистентност. Постије путање у релацији субфолдер или исти фолдери, односно датотеке."); 
		for(File file: files) {
			this.roots.put(FileUtil.getPath(file), file); 
			this.files.put(FileUtil.getPath(file), file); 
		}
	}
	
	public FileList(List<File> files) {
		if(!FileListChecker.noSubfolders(files))
			throw new FileListException("Неконзистентност. Постије путање у релацији субфолдер или исти фолдери, односно датотеке."); 
		for(File file: files) {
			this.roots.put(FileUtil.getPath(file), file); 
			this.files.put(FileUtil.getPath(file), file); 
		}
	}
	
	public FileList(Collection<File> files) {
		if(!FileListChecker.noSubfolders(new ArrayList<>(files)))
			throw new FileListException("Неконзистентност. Постије путање у релацији субфолдер или исти фолдери, односно датотеке."); 
		for(File file: files) {
			this.roots.put(FileUtil.getPath(file), file); 
			this.files.put(FileUtil.getPath(file), file); 
		}
	}
	
	public synchronized Map<String, File> getRoots(){
		return new TreeMap<>(roots); 
	}
	
	public synchronized Map<String, File> getOpened(){
		return new TreeMap<>(files); 
	}
	
	public synchronized Set<File> getRootsSet(){
		return new TreeSet<>(roots.values()); 
	}
	
	public synchronized File getRoot(File file) {
		for(File root: roots.values()) { 
			if(FileUtil.isIn(root,file) || file.equals(root)) return root; 
		}
		return null; 
	}
	
	public synchronized boolean opened(File file) {
		return files.containsKey(FileUtil.getPath(file)); 
	}
	
	public synchronized FileList goTo(File path) {
		if(opened(path)) return this;
		File root = getRoot(path);
		for(File file: FileUtil.onRelation(root, path)) {
			files.put(file.getPath(), file); 
		}
		files.put(FileUtil.getPath(path), path); 
		return this; 
	}
	
	public synchronized TreeMap<String, File> getParents(File file) {
		File root = getRoot(file);
		if(root==null) return null; 
		TreeMap<String, File> parents = new TreeMap<String, File>(); 
		if(file.equals(root)) return parents; 
		files.put(FileUtil.getPath(root), root); 
		for(File f: FileUtil.onRelation(root, file)) {
			files.put(f.getPath(), f); 
		}
		return parents;
	}
	
	public synchronized TreeMap<String, File> getOpenedChilds(File file){
		File root = getRoot(file);
		if(root==null) return null; 
		TreeMap<String, File> childs = new TreeMap<String, File>(); 
		for(Map.Entry<String,File> me: files.entrySet()) {
			if(FileUtil.isIn(me.getValue(), file)) {
				childs.put(me.getKey(),me.getValue()); 
			}
		}
		return childs; 
	}
	
	public synchronized TreeMap<String, File> getOpenedDirectChilds(File file){
		File root = getRoot(file);
		if(root==null) return null; 
		TreeMap<String, File> childs = new TreeMap<String, File>(); 
		for(Map.Entry<String,File> me: files.entrySet()) {
			if(FileUtil.isIn(file, me.getValue())) {
				if(FileUtil.onRelation(file,me.getValue()).size()<=1) {
					childs.put(me.getKey(),me.getValue()); 
				}
			}
		} 
		return childs; 
	}
	
	
	public synchronized FileList collapse(File file){
		File root = getRoot(file);
		if(root==null) return this; 
		if(!opened(file)) return this;
		for(String key: getOpenedChilds(file).keySet())
		files.remove(key);
		return this;
	}
	
	public synchronized FileList expand(File file){
		File root = getRoot(file);
		if(root==null) return this;
		
		if(opened(file))
			collapse(file);
		else
			goTo(file); 
	
		
		if(file.isDirectory()) {
			for(File f: file.listFiles()) {
				try {
					files.put(FileUtil.getPath(f), f); 
				}catch(Exception ex) {
					continue; 
				}
			}
		}
		return this; 
	}
	
	
	public synchronized Map<String, File> getAllFiles() {
		TreeMap<String, File> map = new TreeMap<>(); 
		for(var mEntry: files.entrySet()) {
			if(mEntry.getValue().isFile()) {
				map.put(mEntry.getKey(), mEntry.getValue()); 
			}
		}
		return map; 
	}
	
	public Map<String, File> getAllFolders() {
		TreeMap<String, File> map = new TreeMap<>(); 
		for(var mEntry: files.entrySet()) {
			if(mEntry.getValue().isDirectory()) {
				map.put(mEntry.getKey(), mEntry.getValue()); 
			}
		}
		return map;
	}
	
	public Map<String, File> getAllFiles(File file) {
		TreeMap<String, File> map = new TreeMap<>(); 
		for(var mEntry: this.getOpenedChilds(file).entrySet()) {
			if(mEntry.getValue().isFile()) {
				map.put(mEntry.getKey(), mEntry.getValue()); 
			}
		}
		return map; 
	}
	
	public Map<String, File> getAllFolders(File file) {
		TreeMap<String, File> map = new TreeMap<>(); 
		for(var mEntry: this.getOpenedChilds(file).entrySet()) {
			if(mEntry.getValue().isDirectory()) {
				map.put(mEntry.getKey(), mEntry.getValue()); 
			}
		}
		return map; 
	}
	
	public Map<String, File> getAllFilesDirect(File file) { 
		TreeMap<String, File> map = new TreeMap<>(); 
		try {
			for(var mEntry: this.getOpenedDirectChilds(file).entrySet()) {
				if(mEntry.getValue().isFile()) {
					map.put(mEntry.getKey(), mEntry.getValue()); 
				}
			}
		}catch(Exception ex) {
			return map; 
		}
		return map; 
	}
	
	public Map<String, File> getAllFoldersDirect(File file) {
		TreeMap<String, File> map = new TreeMap<>(); 
		try {
			for(var mEntry: this.getOpenedDirectChilds(file).entrySet()) {
				if(mEntry.getValue().isDirectory()) {
					map.put(mEntry.getKey(), mEntry.getValue()); 
				}
			}
		}catch(Exception ex) {
			return map; 
		}
		return map; 
	}
	
	public Map<String, File> getRootFiles(){
		TreeMap<String, File> map = new TreeMap<>(); 
		for(var entry: roots.entrySet()) {
			if(entry.getValue().isFile())
				map.put(entry.getKey(), entry.getValue()); 
		}
		return map; 
	}
	
	public Map<String, File> getRootFolders(){
		TreeMap<String, File> map = new TreeMap<>(); 
		for(var entry: roots.entrySet()) {
			if(entry.getValue().isDirectory())
				map.put(entry.getKey(), entry.getValue()); 
		}
		return map; 
	}
	
	
	public int count() {
		return files.size();
	}
	
	public int countFiles() {
		return getAllFiles().size(); 
	}
	
	public int countDirectories() {
		return getAllFolders().size(); 
	}
	
	public int count(File file) {
		return getOpenedChilds(file).size();
	}
	
	public int countFiles(File file) {
		return getAllFiles(file).size();
	}
	
	public int countFolders(File file) {
		return getAllFolders(file).size();
	}
	
	public int countDirect(File file) {
		return getOpenedDirectChilds(file).size(); 
	}
	
	public int countDirect() {
		return roots.size(); 
	}
	
	public int countDirectFiles() {
		return getRootFiles().size();
	}
	
	public int countDirectFolders() {
		return getRootFolders().size();
	}
	
	public int countDirectFiles(File file) {
		return getAllFilesDirect(file).size();
	}
	
	public int countDirectFolders(File folder) {
		return getAllFoldersDirect(folder).size();
	}
	
	public int level(File file) {
		return FileUtil.level(getRoot(file), file); 
	}
	
	public synchronized FileList refresh(File file) {
		File root = getRoot(file);
		if(root==null) return this; 
		for(var entry: getOpenedDirectChilds(file).entrySet()) {
			if(!entry.getValue().exists()) files.remove(entry.getKey()); 
		}
		return this; 
	}
	
	public synchronized FileList refresh() {
		for(File root: roots.values()) {
			refresh(root); 
		}
		return this; 
	}
	
	public synchronized FileList reload(File file) {
		File root = getRoot(file);
		if(root==null) return this; 
		for(var entry: getOpenedDirectChilds(file).entrySet()) {
			if(!entry.getValue().exists()) files.remove(entry.getKey());
			else {
				for(File f: entry.getValue().listFiles()) {
					try {
						if(!files.containsKey(FileUtil.getPath(f)))
							files.put(FileUtil.getPath(f), f); 
					}catch(Exception ex) {
						continue;
					}
				}
			}
		}
		return this; 
	}
	
	public synchronized FileList reload() {
		for(File root: roots.values()) {
			refresh(root); 
		}
		return this; 
	}
	
	public synchronized FileList addRoot(File root) {
		for(File r: roots.values()) {
			if(FileUtil.isRelated(r, root) || FileUtil.isRelated(root, r)) 
				throw new FileListException("Додавање отказано. Неконзистентност. Постије путање у релацији субфолдер или исти фолдери, односно датотеке."); 
		}
		roots.put(FileUtil.getPath(root),root); 
		files.put(FileUtil.getPath(root),root); 
		return this; 
	}
	
	public synchronized FileList removeRoot(File root) {
		File r = roots.get(FileUtil.getPath(root)); 
		if(r!=null) collapse(r);
		files.remove(FileUtil.getPath(root)); 
		roots.remove(FileUtil.getPath(root)); 
		return this; 
	}
}
