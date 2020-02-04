package yi.etf.studiranje.datoteke.gui.dialog;

import javafx.scene.control.Alert;

public class QuestionDialog extends Alert{

	public QuestionDialog(String header, String message) {
		super(AlertType.CONFIRMATION);
		setTitle("Датотеке");
		setHeaderText(header);
		setContentText(message);
	}
	
}
