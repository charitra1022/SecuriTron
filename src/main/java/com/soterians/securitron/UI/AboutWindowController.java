package com.soterians.securitron.UI;

import com.soterians.securitron.Utils.IconPack;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutWindowController implements Initializable {

    @FXML
    private Label appTitleLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // set app icon in label
        ImageView imgView = new ImageView(IconPack.APP_ICON_LIGHT.getImage());
        imgView.setFitHeight(60);
        imgView.setPreserveRatio(true);
        appTitleLabel.setGraphic(imgView);
    }
}
