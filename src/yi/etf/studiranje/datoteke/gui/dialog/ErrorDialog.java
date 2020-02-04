package yi.etf.studiranje.datoteke.gui.dialog;

import javafx.scene.control.Alert;

/**
 * Диалог који се употребљава за грешке. 
 * @author Computer
 * @version 1.0
 */
public class ErrorDialog extends Alert{
	public ErrorDialog(String header, String message) {
		super(AlertType.ERROR);
		setTitle("Датотеке");
		setHeaderText(header);
		setContentText(message);
	}
}
