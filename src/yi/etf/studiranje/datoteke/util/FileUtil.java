package yi.etf.studiranje.datoteke.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Алатке односно функционалности које се врше над објектом типа 
 * датотеке.
 * @author Computer
 * @version 1.0
 */
public class FileUtil {
	private FileUtil() {}
	
	public static String getPath(File file) {
		try {
			return file.getCanonicalPath(); 
		}catch(Exception ex) {
			throw new RuntimeException(ex); 
		}
	}
	
	public static boolean isIn(File object, File target) {
		String p1 = getPath(object);
		String p2 = getPath(target);
		return p2.startsWith(p1) && p1.length()!=p2.length(); 
	}
	
	public static boolean isInRelated(File f1, File f2) {
		return isIn(f1,f2) || isIn(f2,f1); 
	}
	
	public static boolean isRelated(File f1, File f2) {
		String p1 = getPath(f1);
		String p2 = getPath(f2);
		return p1.contentEquals(p2) || isInRelated(f1,f2); 
	}
	
	public static String relativePath(File root, File file) {
		String p1 = getPath(root);
		String p2 = getPath(file);
		if(!p2.startsWith(p1)) return null; 
		return p2.substring(p1.length()); 
	}
	
	public static List<File> onRelation(File root, File file){
		String relativePath = relativePath(root,file);
		if(relativePath==null) return null;
		ArrayList<File> files = new ArrayList<>();
		if(relativePath.length()==0) return files; 
		String [] parts = relativePath.substring(1).split(Pattern.quote(File.separator)); 
		String path = FileUtil.getPath(root); 
		for(String part: parts) {
			path += File.separator + part; 
			files.add(new File(path)); 
		}
		return files; 
	}
	
	public static int level(File root, File file) {
		if(file.equals(root)) return 0; 
		return onRelation(root,file).size()+1;  
	}
}
