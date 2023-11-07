package com.soterians.securitron.UI;

import com.soterians.securitron.Utils.EncryptedFileMetadata;
import com.soterians.securitron.Utils.Encryption;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainWindowController {
  private ArrayList<File> files, folders;   // list of files and folders
  private List<File> filesList;   // list of files returned by the file selection event

  @FXML
  private Button settingsBtn, aboutBtn, closeBtn, selectBtn, encryptBtn;

  @FXML
  private Pane dragPane;  // container over which files will be dropped

  @FXML
  private ListView<String> filesListView;  // to display list of encrypted files

  @FXML
  private void settingsBtnClicked(ActionEvent actionEvent) {
    System.out.println("MainWindowController: settingsBtnClicked -> settings button clicked");
  }

  @FXML
  private void aboutBtnClicked(ActionEvent actionEvent) {
    System.out.println("MainWindowController: aboutBtnClicked -> about button clicked");
  }


  /**
   * Close button click event. Closes the app
   * @param actionEvent button click event
   */
  @FXML
  private void closeBtnClicked(ActionEvent actionEvent) {
    ((Stage)((Node)actionEvent.getSource()).getScene().getWindow()).close();
  }


  /**
   * Select_files button. Opens file_chooser to select files to encrypt
   * @param actionEvent button click event
   */
  @FXML
  private void selectBtnClicked(ActionEvent actionEvent) {
    Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select files to encrypt");
    fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Files", "*.*")
    );
    filesList = fileChooser.showOpenMultipleDialog(stage);
    handleFilesSelected();
  }


  /**
   * called when encrypt button is clicked
   * @param actionEvent button click event
   */
  @FXML
  private void encryptBtnClicked(ActionEvent actionEvent) {
    if(filesList == null || filesList.isEmpty()) {
      showSimpleDialog("No files to encrypt!", "Open files or Drag files in the above box to encrypt them.");
      return;
    }

    System.out.println("MainWindowController: encryptBtnClicked -> encryption started");

    // to add code for calling encryption function
    Encryption.encryptFiles(files);

    // release resources after encryption process
    filesList = null;
    files = folders = null;
  }


  /**
   * Creates and shows a dialog box with specific text and title
   * @param title text to display as dialogBox title
   * @param text text to display as dialogBox content
   */
  private void showSimpleDialog(String title, String text) {
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle(title);
    dialog.setContentText(text);
    dialog.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
    dialog.showAndWait();
  }


  /**
   * Separates files and folders from the list returned by the selection event (drag/open)
   */
  private void handleFilesSelected() {
    if(filesList == null) {
      files = folders = null;
      System.out.println("MainWindowController: handleFilesSelected -> No files chosen!");
      return;
    }

    files = new ArrayList<>();
    folders = new ArrayList<>();
    for(int i=0; i<filesList.size(); i++) {
      File f = filesList.get(i);
      if(f.isFile()) files.add(f);
      else folders.add(f);
    }

    // create appropriate message to display
    String msg = "Added ";
    if(!files.isEmpty()) msg += files.size() + " files";
    if(!files.isEmpty() && !folders.isEmpty()) msg += " & ";
    if(!folders.isEmpty()) msg += folders.size() + " folders\n";

    System.out.println("MainWindowController: handleFilesSelected -> " + msg);
  }


  /**
   * called when the items are dropped over the location
   * @param event: drag event object
   */
  @FXML
  private void onDragDropped(DragEvent event) {
    filesList = event.getDragboard().getFiles();  // get the list of files from drop event
    event.consume();
    handleFilesSelected();
  }


  /**
   * called when the mouse just enters the drop location
   * @param event: drag event object
   */
  @FXML
  private void onDragEntered(DragEvent event) {
    dragPane.setStyle("-fx-border-color: #0096FF; -fx-border-width: 2; -fx-border-radius: 10");
  }


  /**
   * called when the mouse exits the drop location or drag event ends
   * @param event: drag event object
   */
  @FXML
  private void onDragExited(DragEvent event) {
    dragPane.setStyle("-fx-border-color: black; -fx-border-width: 0.5; -fx-border-radius: 5");
  }


  /**
   * invoked when a file is dragged and hovered over the drop location
   * @param event: drag event object
   */
  @FXML
  private void onDragOver(DragEvent event) {
    // check if the type of dragged items are files
    if(event.getDragboard().hasFiles()) {
      event.acceptTransferModes(TransferMode.ANY);
      System.out.println("MainWindowController: onDragOver -> Drop the file here!");
    } else {
      System.out.println("MainWindowController: onDragOver -> Content not a file!");
    }
    event.consume();
  }

  public void updateListView(ArrayList<EncryptedFileMetadata> encryptedFiles) {
    System.out.println("MainWindowController: updateListView -> " + encryptedFiles.toString());
    
//    ObservableList<String> names = FXCollections.observableArrayList();
//
//    for(int i=0; i<files.size(); i++) names.add(files.get(i).getName());
//    for(int i=0; i<folders.size(); i++) names.add(folders.get(i).getName());
//
//    filesListView.setItems(names);
  }
}