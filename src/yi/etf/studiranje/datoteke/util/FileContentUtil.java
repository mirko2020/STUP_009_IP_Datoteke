package yi.etf.studiranje.datoteke.util;

import java.io.File;

/**
 * Аластке за баратање информацијама о садржају датотетека и директоријума, то 
 * јест фолдера, односно фасцикла. 
 * @author Computer
 * @version 1.0
 */
public final class FileContentUtil {
	private FileContentUtil() {}
	
	public static long fileLength(File file) {
		return file.length(); 
	}
	
	public static int count(File file) {
		return file.listFiles().length; 
	}
	
	public static int countFiles(File file) {
		int n = 0;
		for(File f: file.listFiles()) {
			if(f.isFile()) n++; 
		}
		return n; 
	}
	
	public static int countFolders(File file) {
		int n = 0;
		for(File f: file.listFiles()) {
			if(f.isDirectory()) n++; 
		}
		return n; 
	}
}
