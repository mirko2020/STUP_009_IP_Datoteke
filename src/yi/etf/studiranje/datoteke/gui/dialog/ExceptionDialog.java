package yi.etf.studiranje.datoteke.gui.dialog;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import yi.etf.studiranje.datoteke.lang.Describable;

public class ExceptionDialog extends Alert{
	private Exception exception; 
	private String message; 
	private String header; 
	
	public ExceptionDialog(Exception exception, String message, String title) {
		super(AlertType.ERROR);
		
		if(exception == null) exception = new RuntimeException(); 
		if(message == null) message = "";
		if(title == null) title = ""; 
		
		this.exception = exception; 
		this.message = message; 
		this.header = title; 
		
		
		setTitle("Датотеке");
		setHeaderText(title);
		setContentText(message); 

		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("Траг изутетка : ");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		expContent.setPrefWidth(1000d);
		expContent.setPrefHeight(500d);
		getDialogPane().setExpandableContent(expContent);
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		if(exception==null) exception=new RuntimeException(); 
		this.exception = exception;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		if(message==null) message = "";  
		this.message = message;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		if(header==null) header = ""; 
		this.header = header;
	}
	
	public ExceptionDialog setDescribe() {
		if(exception instanceof Describable) {
			Describable ex = (Describable) exception; 
			setHeaderText(ex.describe());
			setContentText(ex.getMessage());
		}
		return this; 
	}
}
