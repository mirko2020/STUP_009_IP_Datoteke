package yi.etf.studiranje.datoteke.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import yi.etf.studiranje.datoteke.gui.dialog.ExceptionDialog;

/**
 * Почетна тачка апликације, односно програма. 
 * @author Computer
 * @version 1.0
 */
public class FileExplorerProgram extends Application {
	private static Stage mainStage;  
	private static boolean breaked; 
	
	public static Stage getMainStage() {
		return mainStage;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			FileExplorerCenter.init();
			mainStage = primaryStage; 
			Parent parent = (Parent) FXMLLoader.load(getClass()
			 .getResource("/yi/etf/studiranje/datoteke/gui/FileExplorerView.fxml"));
			primaryStage.setResizable(false);
			primaryStage.setScene(new Scene(parent));
			primaryStage.setTitle("Датотеке");
			primaryStage.show();
		}catch(Exception ex) {
			breaked = true; 
			new ExceptionDialog(ex, "Грешка у конфигурацијама", "Извршавање апликације за датотеке.")
			.showAndWait();
			Platform.exit(); 
			
		}
	}
	
	public static Stage generateRootForm() throws IOException {
		Parent parent = (Parent) FXMLLoader.load(FileExplorerProgram.class
				 .getResource("/yi/etf/studiranje/datoteke/gui/RootsListFormView.fxml"));
		Stage stage = new Stage(); 
		stage.setScene(new Scene(parent));
		stage.setTitle("Датотеке");
		stage.setResizable(false);
		return stage; 
	}
	
	public static void main(String ... args) {
		Application.launch(args); if(breaked) return; 
		FileExplorerCenter.fileEngine.store(); 
	}
}
