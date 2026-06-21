package game.view;

import game.view.audio.AudioManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public final class PopupUtil {

    private PopupUtil() { }

    public static void showMessage(Stage owner, String title, String message) {
        show(owner, title, message, false);
    }

    public static void showError(Stage owner, String title, String message) {
        show(owner, title, message, true);
    }

    private static void show(Stage owner, String title, String message, boolean error) {
        if (error) {
            playErrorSafe();
        }

        final Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);
        if (owner != null) {
            popup.initOwner(owner);
        }
        popup.setResizable(false);
        popup.setTitle(title);

        String accent = error ? "#ff5757" : "#23f7ff";
        String accentSoft = error ? "rgba(255,87,87,0.35)" : "rgba(35,247,255,0.32)";
        String header = error ? "FACTORY WARNING" : "FACTORY NOTICE";

        Label headerLabel = new Label(header);
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setPrefWidth(430);
        headerLabel.setStyle("-fx-font-size: 12px;" +
                             "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
                             "-fx-font-weight: bold;" +
                             "-fx-text-fill: " + accent + ";" +
                             "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.95), 4, 0.7, 0, 1);");

        Label titleLabel = new Label(title);
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPrefWidth(430);
        titleLabel.setStyle("-fx-font-size: 22px;" +
                            "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: white;" +
                            "-fx-effect: dropshadow(gaussian, " + accent + ", 10, 0.42, 0, 0);");

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(420);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setStyle("-fx-font-size: 14px;" +
                              "-fx-font-family: 'Trebuchet MS', 'Arial Black', 'Arial';" +
                              "-fx-font-weight: bold;" +
                              "-fx-text-fill: rgba(240,250,255,0.98);" +
                              "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.95), 3, 0.75, 0, 1);");

        Button okButton = new Button("OK");
        okButton.setPrefWidth(142);
        okButton.setPrefHeight(40);
        okButton.setDefaultButton(true);
        okButton.setCancelButton(true);
        String okNormal = error ? GuiStyle.glowButton("red", false) : GuiStyle.glowButton("cyan", false);
        String okHover = error ? GuiStyle.glowButton("red", true) : GuiStyle.glowButton("cyan", true);
        okButton.setStyle(okNormal);
        GuiStyle.applyHover(okButton, okNormal, okHover, 1.035);
        okButton.setOnAction(e -> {
            playClickSafe();
            popup.close();
        });

        VBox root = new VBox(12);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(470, 236);
        root.setPadding(new Insets(18, 22, 18, 22));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(10,24,42,0.98), rgba(3,7,18,0.98));" +
                      "-fx-background-radius: 18;" +
                      "-fx-border-color: " + accent + ";" +
                      "-fx-border-width: 3;" +
                      "-fx-border-radius: 18;" +
                      "-fx-effect: dropshadow(gaussian, " + accentSoft + ", 24, 0.34, 0, 0);");

        root.getChildren().addAll(headerLabel, titleLabel, messageLabel, okButton);

        Scene scene = new Scene(root);
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.ENTER) {
                playClickSafe();
                popup.close();
                e.consume();
            }
        });
        popup.setScene(scene);
        popup.showAndWait();
    }

    private static void playErrorSafe() {
        try { AudioManager.playError(); } catch (Throwable ignored) { }
    }

    private static void playClickSafe() {
        try { AudioManager.playClick(); } catch (Throwable ignored) { }
    }
}
