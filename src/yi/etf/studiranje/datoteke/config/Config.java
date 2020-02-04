package yi.etf.studiranje.datoteke.config;

import java.io.File;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * Конфогурације. 
 * Коријени за датотеке. 
 * @author Computer
 *
 */
public final class Config {
	public static String FILE_LIST = "yi.config.filelist"; 
	
	public final static Config config = new Config(); 
	private Config() {}
	static {
		config.load().loadFilelist();
	}
	
	private Properties properties = new Properties(); 
	public Config load() {
		try {
			properties.load(getClass().getResourceAsStream("/yi/etf/studiranje/datoteke/config/config.properties"));
		}catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		return this; 
	}
	
	public Set<String> keys(){
		TreeSet<String> res = new TreeSet<>();
		for(Object key: properties.keySet())
			res.add(key.toString());		
		return res; 
	}
	
	public String get(String key) {
		return properties.getProperty(key); 
	}
	
	public String getFilelist() {
		return properties.getProperty(FILE_LIST); 
	}
	
	public Config loadFilelist() {
		File file = new File(getFilelist()); 
		File dir = file.getParentFile(); 
		if(dir!=null && !dir.exists()) dir.mkdirs(); 
		if(!file.exists()) {
			try {
				file.createNewFile(); 
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return this; 
	}
}
