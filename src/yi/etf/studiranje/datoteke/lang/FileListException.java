package yi.etf.studiranje.datoteke.lang;

/**
 * Греше које се могу јавити код листе датотека. 
 * @author Computer
 * @version 1.0
 */
public class FileListException extends RuntimeException implements Describable{
	private static final long serialVersionUID = 2986326523219018219L;
	
	public FileListException() {
		super();
	}

	public FileListException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileListException(String message) {
		super(message);
	}

	public FileListException(Throwable cause) {
		super(cause);
	}

	@Override
	public String describe() {
		return "Грешке у раду са листама датотека.";
	}
}
