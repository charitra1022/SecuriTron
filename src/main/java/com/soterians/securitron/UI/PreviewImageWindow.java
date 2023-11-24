package com.soterians.securitron.UI;

import com.soterians.securitron.Utils.CryptoClasses.EncryptedFileMetadata;
import com.soterians.securitron.Utils.IconPack;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;

public class PreviewImageWindow {
  private AnchorPane parent;
  private Stage stage;
  private Label previewChild;
  private ImageView imageView;

  public PreviewImageWindow(EncryptedFileMetadata fileMetadata, byte[] dataBytes) {
    stage = new Stage();  // stage that will contain the popup for preview
    previewChild = new Label("this is a label, to display image here");
    previewChild.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

    parent = new AnchorPane();
    AnchorPane.setTopAnchor(previewChild, 0.0);
    AnchorPane.setBottomAnchor(previewChild, 0.0);
    AnchorPane.setLeftAnchor(previewChild, 0.0);
    AnchorPane.setRightAnchor(previewChild, 0.0);
    parent.getChildren().add(previewChild);

    imageView = new ImageView(new Image(new ByteArrayInputStream(dataBytes)));
    imageView.setPreserveRatio(true);
    imageView.setFitHeight(Math.min(imageView.getImage().getHeight(), 600));  // choose the image view height to be minimum of canvas size and iamge size
    previewChild.setGraphic(imageView);
    stage.setResizable(false);
    stage.setScene(new Scene(parent));

    // preview content modal
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setTitle(fileMetadata.getFileName());
    stage.getIcons().add(IconPack.APP_ICON.getImage());
  }

  public Stage getStage() { return stage; }
}
