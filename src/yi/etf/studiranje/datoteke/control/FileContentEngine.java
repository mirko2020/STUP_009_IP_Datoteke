package yi.etf.studiranje.datoteke.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import yi.etf.studiranje.datoteke.lang.FileContentException;

/**
 * Управљање садржајем датотека. 
 * @author Computer
 * @version 1.0
 */
public class FileContentEngine {
	private File file; 
	private RandomAccessFile reader; 
	
	public FileContentEngine(File file) {
		try {
			this.file = file; 
			this.reader = new RandomAccessFile(file, "r");
		} catch (FileNotFoundException ex) {
			throw new FileContentException("Датотека не постоји или јединица није датотека.", ex); 
		} 
	}
	
	public File getFile() {
		return file; 
	}
	
	public RandomAccessFile getReader() {
		return reader; 
	}
	
	public byte[] read(int pageSize, int pageNo) {
		int offset = pageSize*pageNo; 
		if(offset>file.length()) return new byte[0];
		if(offset+pageSize>file.length()) pageSize = (int)(file.length()-offset); 
		byte[] bytes = new byte[pageSize]; 
		try {
			reader.seek(offset);
			reader.read(bytes); 
		} catch (IOException ex) {
			throw new FileContentException("Грешке при очитавању датотеке.",ex); 
		}
		return bytes; 
	}
	
	public String readBasicRecord(int pageSize, int pageNo) {
		String record = ""; 
		byte[] bytes = read(pageSize, pageNo); 
		for(byte by: bytes) {
			record += String.format("%02x ", by); 
		}
		return record;
	}
}
