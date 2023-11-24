package com.soterians.securitron.UI;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProgressBarWindow {
  /**
   * Creates a window containing a progress bar with custom text
   * @param text Text to be displayed
   * @return Stage object containing the progress bar
   */
  public static Stage showProgressBar(String text) {
    Label progressLabel = new Label(text);
    progressLabel.setFont(new Font(21));
    AnchorPane.setRightAnchor(progressLabel, 0.0);
    AnchorPane.setLeftAnchor(progressLabel, 0.0);
    AnchorPane.setTopAnchor(progressLabel, 0.0);
    AnchorPane.setBottomAnchor(progressLabel, 0.0);
    AnchorPane ap1 = new AnchorPane();
    ap1.getChildren().add(progressLabel);
    ap1.setPadding(new Insets(10,10,10,10));

    ProgressIndicator progressIndicator = new ProgressIndicator();
    progressIndicator.setPrefHeight(40);
    progressIndicator.setPrefWidth(40);
    AnchorPane.setRightAnchor(progressIndicator, 0.0);
    AnchorPane.setLeftAnchor(progressIndicator, 0.0);
    AnchorPane.setTopAnchor(progressIndicator, 0.0);
    AnchorPane.setBottomAnchor(progressIndicator, 0.0);
    AnchorPane ap2 = new AnchorPane();
    ap2.getChildren().add(progressIndicator);
    ap2.setPadding(new Insets(5, 5, 5, 5));

    GridPane gridPane = new GridPane();
    AnchorPane.setRightAnchor(gridPane, 0.0);
    AnchorPane.setLeftAnchor(gridPane, 0.0);
    AnchorPane.setTopAnchor(gridPane, 0.0);
    AnchorPane.setBottomAnchor(gridPane, 0.0);
    ColumnConstraints cc = new ColumnConstraints();
    cc.setHgrow(Priority.ALWAYS);
    RowConstraints rc = new RowConstraints();
    rc.setVgrow(Priority.NEVER);
    gridPane.getColumnConstraints().add(cc);
    gridPane.getRowConstraints().add(rc);
    gridPane.addRow(0, ap1, ap2);

    AnchorPane root = new AnchorPane();
    root.setPadding(new Insets(5, 10, 5, 10));
    root.getChildren().add(gridPane);
    root.setStyle("-fx-border-color: black; -fx-border-width: 1");

    Stage stage = new Stage();
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setResizable(false);
    stage.setTitle("SecuriTron: Encrypting");
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.initStyle(StageStyle.UNDECORATED);

    // make the window movable
    final double[] xOffSet = {0};
    final double[] yOffSet = {0};
    root.setOnMousePressed(event -> {
      xOffSet[0] = event.getSceneX();
      yOffSet[0] = event.getSceneY();
    });
    root.setOnMouseDragged(event -> {
      stage.setX(event.getScreenX()-xOffSet[0]);
      stage.setY(event.getScreenY()-yOffSet[0]);
    });

    return stage;
  }
}

