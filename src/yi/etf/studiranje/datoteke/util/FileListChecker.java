package yi.etf.studiranje.datoteke.util;

import java.io.File;
import java.util.List;

/**
 * Провјере које се могу извршити за листе датотека. 
 * @author Computer
 * @version 1.0
 */
public final class FileListChecker {
	private FileListChecker() {}
	
	public static final boolean noSubfolders(File ... files) {
		for(int i=0; i<files.length-1; i++) 
			for(int j=i+1; j<files.length; j++)
				if(FileUtil.isRelated(files[i],files[j])) return false; 
		return true; 
	}
	
	public static final boolean noSubfolders(List<File> files) {
		for(int i=0; i<files.size()-1; i++) 
			for(int j=i+1; j<files.size(); j++)
				if(FileUtil.isRelated(files.get(i),files.get(j))) return false; 
		return true;
	}
}
