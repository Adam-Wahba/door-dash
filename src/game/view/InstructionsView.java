package game.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

public class InstructionsView {

    private Pane root;
    private Button continueButton;
    private Button muteButton;

    public InstructionsView() {
        root = new Pane();
        root.setPrefSize(1280, 720);

        Image backgroundImage = GuiStyle.loadAssetImage(getClass(), "Instructions.png");
        if (backgroundImage != null) {
            ImageView background = new ImageView(backgroundImage);
            background.setFitWidth(1280);
            background.setFitHeight(720);
            background.setPreserveRatio(false);
            root.getChildren().add(background);
        }

        createInstructionsText();
        createContinueButton();
        createMuteButton();
    }

    private void createInstructionsText() {
        Label instructionsText = new Label(
            "MONSTERS, INC. FIELD BRIEFING\n\n" +
            "MISSION:\n" +
            "Race through the factory board, collect\n" +
            "canister energy, and reach the final door.\n\n" +
            "TURN FLOW:\n" +
            "01  Choose SCARER or LAUGHER team.\n" +
            "02  Decide if you want to use a powerup.\n" +
            "03  Roll the dice and move your monster.\n" +
            "04  Let the landed cell do its effect.\n\n" +
            "FACTORY CELLS:\n" +
            "DOORS give or drain canister energy.\n" +
            "CARDS trigger surprise monster effects.\n" +
            "MONSTER cells may activate free powers.\n" +
            "CONVEYORS push you forward.\n" +
            "SOCKS drag you back and drain energy.\n\n" +
            "POWERUPS:\n" +
            "Use your monster skill before rolling if\n" +
            "your canister has enough energy.\n\n" +
            "STATUS ALERTS:\n" +
            "SHIELD blocks one negative energy hit.\n" +
            "FREEZE skips a turn.\n" +
            "CONFUSION swaps team roles temporarily.\n\n" +
            "WIN CONDITION:\n" +
            "Reach cell 99 with at least 1000 energy.\n\n" +
            "DEV SHORTCUTS:\n" +
            "E adds energy. W sends the player to 99."
        );

        instructionsText.setWrapText(true);
        instructionsText.setAlignment(Pos.TOP_CENTER);
        instructionsText.setTextAlignment(TextAlignment.CENTER);
        instructionsText.setMaxWidth(332);
        instructionsText.setPrefWidth(332);
        instructionsText.setPadding(new Insets(8, 12, 8, 12));
        instructionsText.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-font-family: 'Arial Black', 'Impact', 'Verdana';" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: linear-gradient(to bottom, #ffffff, #bffcff, #64f7ff);" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.98), 5, 0.78, 0, 1);"
        );

        StackPane textHolder = new StackPane(instructionsText);
        textHolder.setAlignment(Pos.TOP_CENTER);
        textHolder.setPadding(new Insets(0));
        textHolder.setStyle("-fx-background-color: transparent;");
        textHolder.setPrefWidth(348);
        textHolder.setMinWidth(348);

        ScrollPane scroll = new ScrollPane(textHolder);
        // Layout is intentionally unchanged so the text remains aligned with the monitor art.
        scroll.setLayoutX(452);
        scroll.setLayoutY(273);
        scroll.setPrefWidth(352);
        scroll.setPrefHeight(140);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setPannable(true);
        scroll.setFocusTraversable(false);
        scroll.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-background: transparent;" +
            "-fx-control-inner-background: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-padding: 0;" +
            "-fx-font-size: 11px;"
        );

        root.getChildren().add(scroll);
    }

    private void createContinueButton() {
        continueButton = new Button();
        continueButton.setLayoutX(508);
        continueButton.setLayoutY(520);
        continueButton.setPrefWidth(282);
        continueButton.setPrefHeight(76);
        continueButton.setStyle(GuiStyle.transparentButton());
        GuiStyle.applyTransparentHover(continueButton);
        root.getChildren().add(continueButton);
        continueButton.toFront();
    }

    private void createMuteButton() {
        muteButton = new Button();
        muteButton.setLayoutX(1164);
        muteButton.setLayoutY(38);
        muteButton.setPrefWidth(92);
        muteButton.setPrefHeight(92);
        muteButton.setFocusTraversable(false);
        muteButton.setStyle(GuiStyle.muteButton(false));
        root.getChildren().add(muteButton);
        muteButton.toFront();
    }

    public Pane getRoot() {
        return root;
    }

    public Button getContinueButton() {
        return continueButton;
    }

    public Button getMuteButton() {
        return muteButton;
    }
}
