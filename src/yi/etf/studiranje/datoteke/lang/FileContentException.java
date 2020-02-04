package yi.etf.studiranje.datoteke.lang;

/**
 * Изутеци при раду са садржајем датотеке. 
 * @author Computer
 * @version 1.0
 */
public class FileContentException extends RuntimeException implements Describable {
	private static final long serialVersionUID = 3644956621651388824L;
	
	public FileContentException() {
		super();
	}

	public FileContentException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileContentException(String message) {
		super(message);
	}

	public FileContentException(Throwable cause) {
		super(cause);
	}

	@Override
	public String describe() {
		return "Грешке у раду са садржајем датотека.";
	}
}
