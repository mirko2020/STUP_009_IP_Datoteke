package yi.etf.studiranje.datoteke.gui.dialog;

import javafx.scene.control.Alert;

/**
 * Дијалог који се употребљава за обавјештења. 
 * @author Computer
 * @version 1.0
 */
public class SuccessDialog extends Alert{
	public SuccessDialog(String header, String message){
		super(AlertType.INFORMATION);
		setTitle("Датотеке");
		setHeaderText(header);
		setContentText(message);
	}
}
