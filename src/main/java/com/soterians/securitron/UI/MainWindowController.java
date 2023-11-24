package com.soterians.securitron.UI;

import com.soterians.securitron.MainApplication;
import com.soterians.securitron.Utils.CryptoClasses.EncryptedFileMetadata;
import com.soterians.securitron.Utils.CryptoClasses.Encryption;
import com.soterians.securitron.Utils.DatabaseManager;
import com.soterians.securitron.Utils.IconPack;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class MainWindowController implements Initializable {
  private ArrayList<File> files, folders;   // list of files and folders
  private List<File> filesList;   // list of files returned by the file selection event

  @FXML
  private Button settingsBtn, aboutBtn, selectBtn, encryptBtn, cancelBtn;

  @FXML
  private Label dragPane; // element over which files will be dropped

  @FXML
  private Label appTitleLabel;  // app title label

  @FXML
  private ListView<File> dragSelectListView;  // display list of files dragged/selected

  @FXML
  private ListView<EncryptedFileMetadata> filesListView;  // to display list of encrypted files

  @FXML
  private GridPane fileDetailGridPane;  // grid view that displays the file details

  @FXML
  private ImageView imageView;  // display file icon on the right panel

  @FXML
  private Label fileNameLabel, fileFormatLabel, fileSizeLabel, fileEncryptedDateLabel;

  @FXML
  private Button openFileBtn, decryptFileBtn;


  /**
   * Update the app UI and other data on app load
   * @param location
   * The location used to resolve relative paths for the root object, or
   * {@code null} if the location is not known.
   *
   * @param resources
   * The resources used to localize the root object, or {@code null} if
   * the root object was not localized.
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    String dialogFxml = "";
    String dialogTitle = "";

    // if database is present, show login dialog
    if(!DatabaseManager.isDBPresent()){
      System.out.println("MainWindowController: initialize -> database not found");
      dialogFxml = "register_window.fxml";
      dialogTitle = "SecuriTron: Setup Login Password to access application";
    }

    // if database is not present, show register dialog
    else {
      System.out.println("MainWindowController: initialize -> database found");
      dialogFxml = "login_window.fxml";
      dialogTitle = "SecuriTron: Enter Password to Login";
    }


    // dialog window to login/register into the app
    Stage stage = new Stage();
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setResizable(false);
    stage.setTitle(dialogTitle);
    stage.getIcons().add(IconPack.APP_ICON.getImage());

    // when the login/register dialog is closed, close the app
    stage.setOnCloseRequest(event -> {
      Platform.exit();
      System.exit(0);
    });

    // view login/register modal
    try {
      FXMLLoader loginRegisterFxmlLoader = new FXMLLoader(MainApplication.class.getResource(dialogFxml));
      stage.setScene(new Scene(loginRegisterFxmlLoader.load()));
      stage.showAndWait();
    } catch(IOException ex) {
      System.out.println("MainWindowController: initialize -> error, exiting");
      ex.printStackTrace();
      Platform.exit();
      System.exit(-1);
    }

    // set app icon in title label
    ImageView imgView = new ImageView(IconPack.APP_ICON.getImage());
    imgView.setFitHeight(30);
    imgView.setPreserveRatio(true);
    appTitleLabel.setGraphic(imgView);

    // set the drag and drop image
    ImageView view1 = new ImageView(IconPack.DRAG_DROP_GREY.getImage());
    view1.setFitHeight(80);
    view1.setPreserveRatio(true);
    dragPane.setGraphic(view1);

    // set the button icons
    ImageView view2 = new ImageView(IconPack.GEAR.getImage());
    ImageView view3 = new ImageView(IconPack.INFO.getImage());
    view2.setFitHeight(15);
    view3.setFitHeight(15);
    view2.setPreserveRatio(true);
    view3.setPreserveRatio(true);
    settingsBtn.setGraphic(view2);
    aboutBtn.setGraphic(view3);

    updateListView(DatabaseManager.readEncryptedFileData());  // update the list view with list of encrypted files
    changeFileDetailPaneVisibility(false);

    // set the tooltip for the file info labels
    fileNameLabel.setTooltip(new Tooltip(""));
    fileFormatLabel.setTooltip(new Tooltip(""));
    fileSizeLabel.setTooltip(new Tooltip(""));
    fileEncryptedDateLabel.setTooltip(new Tooltip(""));

    // set delay for tooltip for the labels
    fileNameLabel.getTooltip().setShowDelay(Duration.seconds(0.2));
    fileFormatLabel.getTooltip().setShowDelay(Duration.seconds(0.2));
    fileSizeLabel.getTooltip().setShowDelay(Duration.seconds(0.2));
    fileEncryptedDateLabel.getTooltip().setShowDelay(Duration.seconds(0.2));

    dragPane.setOnMouseClicked(e -> selectBtn.fire());  // open the selection dialog when drag pane is clicked
    dragPane.setVisible(true);  // view drag pane
    dragSelectListView.setVisible(false); // hide selected files list view
    cancelBtn.setVisible(false);  // hide cancel operation button

    // modifies the listViews
    modifySelectedFilesListView();  // modify the selectedFilesListView (adds tooltip to items)
    modifyEncryptedFilesListView(); // modify the EncryptedFilesListView (adds tooltip to items)
  }


  /**
   * Modifies the selectedFilesListView (adds tooltip to items)
   */
  private void modifySelectedFilesListView() {
    dragSelectListView.setCellFactory(cell -> new ListCell<>(){
      final Tooltip tooltip = new Tooltip();

      @Override
      protected void updateItem(File file, boolean empty) {
        super.updateItem(file, empty);
        // if no item is present
        if(file == null || empty) {
          setText(null);
          setTooltip(null);
        }
        // if items are present, attach tooltip to them
        else {
          setText(file.getName());
          tooltip.setText(file.getAbsolutePath());
          tooltip.setShowDelay(Duration.seconds(0.2));
          setTooltip(tooltip);
        }
      }
    });
  }


  /**
   * Modifies the EncryptedFilesListView (adds tooltip to items)
   */
  private void modifyEncryptedFilesListView() {
    filesListView.setCellFactory(cell -> new ListCell<>() {
      final Tooltip tooltip = new Tooltip();

      @Override
      protected void updateItem(EncryptedFileMetadata fileMetadata, boolean empty) {
        super.updateItem(fileMetadata, empty);

        // if no item is present
        if(fileMetadata == null || empty) {
          setText(null);
          setTooltip(null);
        }
        // if items are present, attach tooltip to them
        else {
          setText(fileMetadata.toString());

          String line1 = "Name: " + fileMetadata.getFileName();
          String line2 = "Size: " + fileMetadata.getFileSizeString();
          String line3 = "Path: " + fileMetadata.getFilePath();
          tooltip.setText(line1 + "\n" + line2 + "\n" + line3);
          tooltip.setShowDelay(Duration.seconds(0.2));
          setTooltip(tooltip);
        }
      }
    });
  }


  @FXML
  private void settingsBtnClicked(ActionEvent actionEvent) throws IOException {
    // view settings modal
    FXMLLoader settingsFxmlLoader = new FXMLLoader(MainApplication.class.getResource("settings_window.fxml"));
    Parent settingsRoot = settingsFxmlLoader.load();
    Stage stage = new Stage();
    stage.setTitle("SecuriTron: Settings");
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setScene(new Scene(settingsRoot));
    stage.setResizable(false);
    stage.getIcons().add(IconPack.APP_ICON.getImage());
    stage.showAndWait();

  }

  @FXML
  private void aboutBtnClicked(ActionEvent actionEvent) {
    System.out.println("MainWindowController: aboutBtnClicked -> about button clicked");
  }


  /**
   * Select_files button. Opens file_chooser to select files to encrypt
   * @param actionEvent button click event
   */
  @FXML
  private void selectBtnClicked(ActionEvent actionEvent) {
    // get reference to the current stage
    Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();

    // show the filechooser to select files
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select files to encrypt");
    fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Files", "*.*")
    );
    filesList = fileChooser.showOpenMultipleDialog(stage);
    handleFilesSelected();  // process the selected files
  }


  /**
   * called when encrypt button is clicked
   * @param actionEvent button click event
   */
  @FXML
  private void encryptBtnClicked(ActionEvent actionEvent) throws IOException, ParseException, NoSuchAlgorithmException {
    // if no files are selected currently
    if(filesList == null || filesList.isEmpty()) {
      CustomDialogs.showAlertDialog("No files to encrypt!", "Open files or Drag files in the above box to encrypt them.", Alert.AlertType.WARNING);
      return;
    }

    System.out.println("MainWindowController: encryptBtnClicked -> encryption started");

    // TODO: add code for calling encryption on folders function
    Encryption.encryptFiles(files);

    // now update the listview by retrieving new database contents
    updateListView(DatabaseManager.readEncryptedFileData());

    // release resources after encryption process
    clearSelectedFilesAndShowDragPane();
  }


  /**
   * Clears the file arrayLists and also hides & clears selectedFileListView and shows dragPane
   * In simple words, it resets the app backend and UI w.r.t. selected files
   */
  private void clearSelectedFilesAndShowDragPane() {
    // clear the lists
    filesList = null;
    files = folders = null;

    // clear & hide selectedFilesListView and show dragPane
    dragPane.setVisible(true);
    dragSelectListView.setVisible(false);
    cancelBtn.setVisible(false);
    dragSelectListView.getItems().clear();
  }


  /**
   * Called on cancel button click. Cancels selection process for encryption
   * @param actionEvent
   */
  @FXML
  private void cancelBtnClicked(ActionEvent actionEvent) {
    System.out.println("MainWindowController: cancelBtnClicked");
    clearSelectedFilesAndShowDragPane();
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

    // hide dragPane and update & show selectedFilesListView
    dragPane.setVisible(false);
    dragSelectListView.setVisible(true);
    cancelBtn.setVisible(true);
    // remove older items and add new ones
    dragSelectListView.getItems().clear();
    for(File f: filesList) dragSelectListView.getItems().add(f);
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
    dragPane.setStyle("-fx-border-color: #0096FF; -fx-border-width: 4; -fx-border-radius: 15; -fx-text-fill: #0096FF;");
    ((ImageView)dragPane.getGraphic()).setImage(IconPack.DRAG_DROP_BLUE.getImage());
  }


  /**
   * called when the mouse exits the drop location or drag event ends
   * @param event: drag event object
   */
  @FXML
  private void onDragExited(DragEvent event) {
    dragPane.setStyle("-fx-border-color: lightgrey; -fx-border-radius: 10; -fx-border-width: 2; -fx-text-fill: #868686;");
    ((ImageView)dragPane.getGraphic()).setImage(IconPack.DRAG_DROP_GREY.getImage());
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


  /**
   * Updates the ListView with the list of encrypted files
   * @param encryptedFiles ArrayList&lt;EncryptedFileMetadata&gt; object containing list of EncryptedFileMetadata object
   */
  public void updateListView(ArrayList<EncryptedFileMetadata> encryptedFiles) {
    System.out.println("MainWindowController: updateListView -> " + encryptedFiles.toString());
    
    ObservableList<EncryptedFileMetadata> encryptedFilesList = FXCollections.observableArrayList();
    encryptedFilesList.addAll(encryptedFiles);

    filesListView.setItems(encryptedFilesList);

    filesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EncryptedFileMetadata>() {
      @Override
      public void changed(ObservableValue<? extends EncryptedFileMetadata> observable, EncryptedFileMetadata oldValue, EncryptedFileMetadata newValue) {
        // when the focus from the listview items gets removed
        if(newValue == null) {
          changeFileDetailPaneVisibility(false);  // hide the file info panel
          return;
        }

        changeFileDetailPaneVisibility(true); // show the file info panel
        // update the file info in the panel
        fileNameLabel.setText(newValue.getFileName());
        fileNameLabel.getTooltip().setText(fileNameLabel.getText());  // set the tooltip text

        fileFormatLabel.setText(newValue.getFileFormat());
        fileFormatLabel.getTooltip().setText(fileFormatLabel.getText());  // set the tooltip text

        fileSizeLabel.setText(newValue.getFileSizeString());
        fileSizeLabel.getTooltip().setText(fileSizeLabel.getText());  // set the tooltip text

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        fileEncryptedDateLabel.setText(simpleDateFormat.format(newValue.getEncryptedOn()));
        fileEncryptedDateLabel.getTooltip().setText(fileEncryptedDateLabel.getText());  // set the tooltip text

        imageView.setImage(IconPack.getFileIconImage(newValue.getFile())); // set file icon in imageview
      }
    });
  }


  /**
 * Called when the open button is clicked for any encrypted file
 * @param actionEvent button click action event
 * @throws IOException
 */
  @FXML
  private void onOpenFileBtnClicked(ActionEvent actionEvent) throws IOException {
    System.out.println("MainWindowController: onOpenFileBtnClicked -> open file button clicked");
    EncryptedFileMetadata fileMetadata = filesListView.getSelectionModel().getSelectedItem(); // get selected item
    Encryption.openFileTemporarily(fileMetadata); // open the encrypted file temporarily

    // if the file was edited while being opened, then update the UI
    if(PreviewTextWindow.isFileEdited) {
      updateListView(DatabaseManager.readEncryptedFileData());
      PreviewTextWindow.isFileEdited = false;
    }
  }


  /**
   * Called when the decrypt button is called
   * @param actionEvent button click event
   */
  @FXML
  private void onDecryptFileBtnClicked(ActionEvent actionEvent) {
    System.out.println("MainWindowController: onDecryptFileBtnClicked -> decrypt file button clicked");

    // get the selected item from the listview
    EncryptedFileMetadata fileMetadata = filesListView.getSelectionModel().getSelectedItem();
    boolean isSucess = Encryption.decryptFile(fileMetadata); // call the decrypt method on the file

    // remove the selected item from the listview if decryption was successfull
    if(isSucess) filesListView.getItems().remove(filesListView.getSelectionModel().getSelectedIndex());

    // TODO: if an encrypted file is not found, it should be updated in the list.json as its getting deleted in the listview
  }


  /**
   * changes the visibility of the file detail pane on the right side.
   * @param visible boolean value - true to show, false to hide
   */
  public void changeFileDetailPaneVisibility(boolean visible) {
    if(fileDetailGridPane.visibleProperty().get() != visible) fileDetailGridPane.setVisible(visible);
  }
}