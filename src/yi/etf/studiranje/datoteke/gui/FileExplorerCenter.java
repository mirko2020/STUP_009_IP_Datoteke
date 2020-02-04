package yi.etf.studiranje.datoteke.gui;

import java.io.File;

import yi.etf.studiranje.datoteke.config.Config;
import yi.etf.studiranje.datoteke.control.FileListEngine;

/**
 * Апликациони центар са објектима и мехнизмима потребним за извршавање. 
 * @author Computer
 * @version 1.0
 */
public final class FileExplorerCenter {
	private FileExplorerCenter() {}
	
	public static final FileListEngine fileEngine = new FileListEngine(new File(Config.config.getFilelist())); 
	public static final void init() { fileEngine.load(); }
}
