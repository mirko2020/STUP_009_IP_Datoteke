package yi.etf.studiranje.datoteke.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import yi.etf.studiranje.datoteke.bean.ListPaggingBean;
import yi.etf.studiranje.datoteke.control.FileContentEngine;
import yi.etf.studiranje.datoteke.control.FileListEngine;
import yi.etf.studiranje.datoteke.gui.dialog.ExceptionDialog;
import yi.etf.studiranje.datoteke.gui.font.FontUtil;
import yi.etf.studiranje.datoteke.gui.model.RootDescriptor;
import yi.etf.studiranje.datoteke.model.FileList;
import yi.etf.studiranje.datoteke.util.FileContentUtil;

/**
 * Контрола графике за форму апликације којом се контролише
 * листа датотека, прегледа стабло, у бинарном обилику садржај 
 * датотеке и информације, као и путања. 
 * @author Computer
 * @version 1.0
 */
public class FileExplorerController implements Initializable{
	private FileListEngine engine = FileExplorerCenter.fileEngine;
	private FileList files = engine.getRootList(); 
	
	private ListPaggingBean bean = new ListPaggingBean(); 
	
	@FXML TreeView<File> folderContent; 
	@FXML TextArea fileInformation; 
	@FXML TextArea fileContent; 
	@FXML TextField path; 
	@FXML TextField fileCount; 
	@FXML TextField directoryCount; 
	@FXML TextField totalCount; 
	@FXML TextField total; 
	@FXML TextField pageNo; 
	@FXML TextField pageSize; 
	@FXML TextField pagesCount; 
	@FXML TextField count; 
	@FXML Button refresh;
	@FXML Button roots;
	
	private static Stage rootsForm; 
	
	public synchronized static Stage getRootsForm() {
		return rootsForm; 
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		fileInformation.setFont(FontUtil.getCourierNew());
		fileContent.setFont(FontUtil.getCourierNew());
		refresh.setOnAction(e->refreshAction());
		roots.setOnAction(e->rootsAction());
		folderContent.setRoot(new TreeItem<>());
		folderContent.setShowRoot(false);
		folderContent.applyCss();
		refreshAction(); 
		folderContent.getSelectionModel().selectedItemProperty().addListener((e,o,n)->fileSelect(o,n)); 
	}
	
	private void rootsAction() {
		try {
			Stage stage = FileExplorerProgram.generateRootForm(); 
			stage.initOwner(FileExplorerProgram.getMainStage()); 
			stage.initModality(Modality.WINDOW_MODAL);
			synchronized(FileExplorerController.class) {rootsForm=stage;}
			stage.showAndWait(); 
		}catch(Exception ex) {
			new ExceptionDialog(ex,"Грешка при раду апликације.","Грешка услед изузетка.")
				.setDescribe().showAndWait(); 
		}
	}
	
	private void refreshAction() {
		folderContent.getRoot().getChildren().clear();
		for(var mEntry: files.getRootFiles().entrySet()) {
			RootDescriptor rd = new RootDescriptor(mEntry.getValue().getName(), mEntry.getValue());
			try{files.expand(rd.getFile());}
			catch(Exception ex) {continue;}
			TreeItem<File> newNode = new TreeItem<File>();
			load(newNode, rd.getFile()); 
			folderContent.getRoot().getChildren().add(newNode);
			folderContent.refresh();
		} 
		for(var mEntry: files.getRootFolders().entrySet()) {
			RootDescriptor rd = new RootDescriptor(mEntry.getValue().getName(), mEntry.getValue());
			try{files.expand(rd.getFile());}
			catch(Exception ex) {continue;}
			TreeItem<File> newNode = new TreeItem<>();  
			loadLevel(newNode, rd.getFile()); 
			folderContent.getRoot().getChildren().add(newNode);
			generateNodeReactions(newNode);
			folderContent.refresh();
		}
	}
	
	public void loadLevel(TreeItem<File> node, File file) {
		load(node, file);
		for(var mEntry: files.getAllFilesDirect(file).entrySet()) {
			TreeItem<File> newNode = new TreeItem<File>(); 
			RootDescriptor rd = new RootDescriptor(mEntry.getValue().getName(), mEntry.getValue());
			load(newNode, rd.getFile());  
			node.getChildren().add(newNode);
			folderContent.refresh();
		}
		for(var mEntry: files.getAllFoldersDirect(file).entrySet()) {
			TreeItem<File> newNode = new TreeItem<>(); 
			RootDescriptor rd = new RootDescriptor(mEntry.getValue().getName(), mEntry.getValue());
			loadLevel(newNode, rd.getFile()); 
			node.getChildren().add(newNode);
			generateNodeReactions(newNode); 
			folderContent.refresh();
		}
	}
	public void load(TreeItem<File> node, File file) { 
		node.setValue(new File(file.getPath()) {
			private static final long serialVersionUID = 2632234973654085025L;

			@Override
			public String toString() {
				String str = "";
				if(isFile()) str+="[FILE] "; 
				else if(isDirectory()) str+="[DIR] ";
				str+=getName()+" "; 
				if(isFile()) str+="["+length()+"]"; 
				else if(isDirectory()) str+="["+FileContentUtil.count(this)+"="+FileContentUtil.countFolders(this)+"+"+FileContentUtil.countFiles(this)+"]"; 
				return str; 
			}
		}); 
	}
	
	public void generateNodeReactions(TreeItem<File> node){ 
		node.expandedProperty().addListener((e,o,n)->{
			if(n) {
				for(TreeItem<File> file: node.getChildren()) { 
					try { files.expand(new File(file.getValue().getPath()));
					}catch(Exception ex) {continue; }
					file.getChildren().clear(); 
					for(var mEntry: files.getAllFilesDirect(file.getValue()).entrySet()) { 
						TreeItem<File> newNode = new TreeItem<>(); 
						load(newNode, mEntry.getValue());
						file.getChildren().add(newNode);
					}
				}
				for(TreeItem<File> file: node.getChildren()) { 
					try { files.expand(new File(file.getValue().getPath()));
					}catch(Exception ex){ continue; }
					
					for(var mEntry: files.getAllFoldersDirect(file.getValue()).entrySet()) { 
						TreeItem<File> newNode = new TreeItem<>(); 
						loadLevel(newNode, mEntry.getValue());
						file.getChildren().add(newNode);
						generateNodeReactions(newNode); 
					}
				}
			}
		});
	}
	
	public void fileSelect(TreeItem<File> old, TreeItem<File> neo) {
		fileInformation.clear(); 
		fileContent.clear();
		if(neo==null) {
			path.setText("");
			fileCount.setText(""); 
			directoryCount.setText(""); 
			totalCount.setText(""); 
			total.setText(""); 
			pagesCount.setText(""); 
			count.setText("");
		}else {
			path.setText(neo.getValue().getPath());
			fileCount.setText("0"); 
			directoryCount.setText("0"); 
			totalCount.setText("0"); 
			total.setText("0"); 
			pagesCount.setText("0");
			count.setText("0");
			if(neo.getValue().isFile()) {
				count.setText("0");
				total.setText(""+neo.getValue().length());
				totalCount.setText("0");
			}else if(neo.getValue().isDirectory()) {
				fileCount.setText(""+files.countDirectFiles(neo.getValue()));
				directoryCount.setText(""+files.countDirectFolders(neo.getValue()));
				count.setText(""+files.countDirect(neo.getValue())); 
			}

			if(neo.getValue().isFile()) {
				fileInformation.appendText("ДАТОТЕКА");
			}else if(neo.getValue().isDirectory()) {
				fileInformation.appendText("ДИРЕКТОРИЈУМ");
			}
			
			if(neo.getValue().isFile()) {
				bean.setCount(neo.getValue().length());
				try {bean.setPageSize(Integer.parseInt(this.pageSize.getText()));}catch(Exception ex){}
				try {bean.setPageNo(Integer.parseInt(this.pageNo.getText()));}catch(Exception ex){}
				pagesCount.setText(""+bean.getPageCount());
				pageSize.setText(""+bean.getPageSize());
				totalCount.setText(""+bean.getCount());
				pageNo.setText(""+bean.getPageNo());
			}
			
			try {
				String content = new FileContentEngine(neo.getValue()).readBasicRecord((int)bean.getPageSize(), (int)bean.getPageNo());
				fileContent.setText(content);
			}catch(Exception ex) {
				return; 
			}
		}
	}
}
