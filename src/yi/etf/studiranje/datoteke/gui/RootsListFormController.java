package yi.etf.studiranje.datoteke.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TreeSet;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import yi.etf.studiranje.datoteke.control.FileListEngine;
import yi.etf.studiranje.datoteke.gui.dialog.ExceptionDialog;
import yi.etf.studiranje.datoteke.gui.dialog.SuccessDialog;
import yi.etf.studiranje.datoteke.gui.model.RootDescriptor;
import yi.etf.studiranje.datoteke.lang.FileListException;
import yi.etf.studiranje.datoteke.util.FileUtil;

/**
 * Контролер за форму на којој се ради са коријенима. 
 * Прегледају се, али и додају и бришу. 
 * @author Computer
 * @version 1.0
 */
public class RootsListFormController implements Initializable{
	@FXML private TextField nameField; 
	@FXML private TextField pathField; 
	@FXML private TableView<RootDescriptor> contentView;
	@FXML private Button chooseButton; 
	@FXML private Button addButton; 
	@FXML private Button removeButton; 
	@FXML private Button changeButton; 
	@FXML private CheckBox fileOrFolder; 
	@FXML private TableColumn<String, RootDescriptor> nameColumn; 
	@FXML private TableColumn<String, RootDescriptor> pathColumn; 
	@FXML private TableColumn<String, RootDescriptor> descriptionColumn; 
	
	
	private FileListEngine engine = FileExplorerCenter.fileEngine;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		addButton.setOnAction(e->addAction());
		removeButton.setOnAction(e->removeAction());
		changeButton.setOnAction(e->changeAction());
		chooseButton.setOnAction(e->chooseAction()); 
		
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));
		descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
		
		TreeSet<RootDescriptor> descriptor = new TreeSet<>(); 
 		for(var mEntry: engine.getNameMap().entrySet()) {
			descriptor.add(new RootDescriptor(mEntry.getKey(), new File(mEntry.getValue()))); 
		}
 		for(RootDescriptor description: descriptor) {
 			contentView.getItems().add(description);  
 		}
 		
 		contentView.getSelectionModel().selectedItemProperty().addListener((e,o,n)->{
 			contentViewOnSelected(o,n);
 		});
	}
	public void contentViewOnSelected(RootDescriptor old, RootDescriptor neo) {
		if(neo==null) {
			nameField.setText("");
			pathField.setText("");
		}else {
			nameField.setText(neo.getName()); 
			pathField.setText(neo.getPath());
		}
	}
	
	public void addAction() {
		try {
			String name = nameField.getText(); 
			String path = pathField.getText();
			File file = new File(path);
			if(name.length()==0 || path.length()==0)
				throw new FileListException("Додавање непостојећег директроијума или датотеке.");
			if(!file.exists())
				throw new FileListException("Додавање непостојећег директроијума или датотеке.");  
			String existsName =engine.getName(file); 
			String existsFile =engine.getNameMap().get(FileUtil.getPath(file));
			if(existsFile!=null || existsName!=null)
				throw new FileListException("Додавање учитаног директроијума или датотеке.");
			engine.addRoot(name, file); 
			contentView.getItems().add(new RootDescriptor(name, file)); 
			new SuccessDialog("Додавање новог коријена.", "Додавање је успјешно.").showAndWait(); 
		}catch(Exception ex) {
			new ExceptionDialog(ex,"Грешка при додавњу коријена за датотеке.","Грешка у раду апликације.")
			.setDescribe().showAndWait(); 
		}
	}
	
	public void removeAction() {
		try {
			String name = nameField.getText(); 
			if(name.length()==0)
				throw new FileListException("Брисање непостојећег директроијума или датотеке.");
			engine.removeRoot(name); 
			for(RootDescriptor rd: contentView.getItems()) {
				if(rd.getName().contentEquals(name)) {
					Platform.runLater(()->contentView.getItems().remove(rd));
					new SuccessDialog("Додавање постојећег коријена.", "Брисање је успјешно.").showAndWait(); 
					return; 
				}
			}
			new SuccessDialog("Додавање постојећег коријена.", "Коријен није пронађен.").showAndWait(); 
		}catch(Exception ex) {
			new ExceptionDialog(ex,"Грешка при брисању коријена за датотеке.","Грешка у раду апликације.")
			.setDescribe().showAndWait(); 
		}
	}
	
	public void changeAction() {
		RootDescriptor selected = contentView.getSelectionModel().getSelectedItem(); 
		try {
			String newName = nameField.getText(); 
			String newPath = pathField.getText();
			File file = new File(newPath);
			if(newName.length()==0 || newName.length()==0)
				throw new FileListException("Измјена у непостојећег директроијума или датотеке.");
			if(!file.exists())
				throw new FileListException("Измјена у непостојећег директроијума или датотеке.");  
			if(selected==null) {
				throw new FileListException("Измјена непостојећег директроијума или датотеке."); 
			}
			try {
				RootDescriptor neo = new RootDescriptor(newName, new File(newPath)); 
				contentView.getItems().remove(selected);
				engine.removeRoot(selected.getName());
				engine.addRoot(newName, neo.getFile());
				contentView.getItems().add(neo); 
				contentView.refresh();
				contentView.getSelectionModel().select(neo);
				new SuccessDialog("Додавање постојећег коријена.", "Измјена коријена "+selected.getName()+" успјешна.").showAndWait(); 
			}catch(Exception ex) {
				contentView.getItems().add(selected); 
				engine.addRoot(selected.getName(), selected.getFile()); 
				throw ex; 
			}
		}catch(Exception ex) {
			new ExceptionDialog(ex,"Грешка при измјени коријена за датотеке.","Грешка у раду апликације.")
			.setDescribe().showAndWait(); 
			
		}
	}
	
	public void chooseAction() {
		if(fileOrFolder.isSelected()) {
			 FileChooser fileChooser = new FileChooser();
			 File file  = new File(pathField.getText());
			 fileChooser.setInitialDirectory(new File("."));
			 if(file.exists()) {
				 if(file.isFile()) file = file.getParentFile(); 
				 if(file.isDirectory()) fileChooser.setInitialDirectory(file);
			 }
			 fileChooser.setTitle("Датотеке");
			 File selectedFile = fileChooser.showOpenDialog(FileExplorerController.getRootsForm());
			 if(selectedFile!=null) pathField.setText(selectedFile.getPath());
		 }else {
			DirectoryChooser directoryChooser = new DirectoryChooser(); 
			File file  = new File(pathField.getText());
		    directoryChooser.setInitialDirectory(new File("."));
			if(file.exists()) {
				 if(file.isFile()) file = file.getParentFile(); 
				 if(file.isDirectory()) directoryChooser.setInitialDirectory(file);
			}
			directoryChooser.setTitle("Датотеке");
			File selectedFile = directoryChooser.showDialog(FileExplorerController.getRootsForm());
			if(selectedFile!=null) pathField.setText(selectedFile.getPath());
		 }
	}
}
