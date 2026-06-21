package game.view;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class SideSelectionView {

    private Pane root;
    private Button scarerButton;
    private Button laugherButton;
    private Button muteButton;

    private ImageView scarerPortraitView;
    private ImageView laugherPortraitView;
    private Label choiceLabel;

    private static final Color SCARER_GLOW = Color.rgb(188, 54, 255, 0.92);
    private static final Color LAUGHER_GLOW = Color.rgb(72, 255, 74, 0.88);
    private static final Color TEXT_GLOW = Color.rgb(71, 235, 255, 0.92);

    public SideSelectionView() {
        root = new Pane();
        root.setPrefSize(1280, 720);

        Image backgroundImage = GuiStyle.loadAssetImage(getClass(), "SideSelection.png");
        if (backgroundImage != null) {
            ImageView background = new ImageView(backgroundImage);
            background.setFitWidth(1280);
            background.setFitHeight(720);
            background.setPreserveRatio(false);
            root.getChildren().add(background);
        }

        createTeamPortraits();
        createChoiceLabel();
        createConfirmButtons();
        createMuteButton();
    }

    private void createTeamPortraits() {
        Image scarer = GuiStyle.loadAssetImage(getClass(), "ui/portraits/scarer_avatar.png");
        Image laugher = GuiStyle.loadAssetImage(getClass(), "ui/portraits/laugher_avatar.png");

        // Fall back to older clean monsters if the avatar assets are not copied yet.
        if (scarer == null) scarer = GuiStyle.loadAssetImage(getClass(), "monsters/small/scarer_monster.png");
        if (laugher == null) laugher = GuiStyle.loadAssetImage(getClass(), "monsters/small/laugher_monster.png");

        if (scarer != null) {
            scarerPortraitView = createPortraitView(scarer, 145, 478, 292);
            scarerPortraitView.setEffect(createPortraitGlow(SCARER_GLOW, 18, 0.30));
            root.getChildren().add(scarerPortraitView);
        }

        if (laugher != null) {
            laugherPortraitView = createPortraitView(laugher, 145, 662, 292);
            laugherPortraitView.setEffect(createPortraitGlow(LAUGHER_GLOW, 18, 0.28));
            root.getChildren().add(laugherPortraitView);
        }
    }

    private ImageView createPortraitView(Image image, double size, double x, double y) {
        ImageView view = new ImageView(image);
        view.setFitWidth(size);
        view.setFitHeight(size);
        view.setPreserveRatio(true);
        view.setSmooth(true);
        view.setCache(true);
        view.setLayoutX(x);
        view.setLayoutY(y);
        view.setMouseTransparent(true);
        view.setOpacity(0.94);
        return view;
    }

    private DropShadow createPortraitGlow(Color color, double radius, double spread) {
        DropShadow glow = new DropShadow();
        glow.setColor(color);
        glow.setRadius(radius);
        glow.setSpread(spread);
        glow.setOffsetX(0);
        glow.setOffsetY(0);
        return glow;
    }

    private void createChoiceLabel() {
        choiceLabel = new Label("CHOOSE YOUR SIDE");
        choiceLabel.setLayoutX(350);
        choiceLabel.setLayoutY(646);
        choiceLabel.setPrefWidth(580);
        choiceLabel.setPrefHeight(44);
        choiceLabel.setAlignment(Pos.CENTER);
        choiceLabel.setMouseTransparent(true);
        choiceLabel.setStyle(monsterLabelStyle("#EAFDFF", 30));
        choiceLabel.setEffect(createTextGlow(TEXT_GLOW, 22, 0.42));
        root.getChildren().add(choiceLabel);
    }

    private String monsterLabelStyle(String textColor, int size) {
        return "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS';" +
               "-fx-font-size: " + size + "px;" +
               "-fx-font-weight: 900;" +
               "-fx-text-fill: " + textColor + ";" +
               "-fx-padding: 0;" +
               "-fx-background-color: transparent;";
    }

    private DropShadow createTextGlow(Color color, double radius, double spread) {
        DropShadow glow = new DropShadow();
        glow.setColor(color);
        glow.setRadius(radius);
        glow.setSpread(spread);
        glow.setOffsetX(0);
        glow.setOffsetY(0);
        return glow;
    }

    private void createConfirmButtons() {
        scarerButton = new Button();
        // Larger invisible hitbox: hovering/clicking the whole side card feels better,
        // but the visible glow is only applied to the portrait image.
        scarerButton.setLayoutX(440);
        scarerButton.setLayoutY(268);
        scarerButton.setPrefWidth(205);
        scarerButton.setPrefHeight(345);
        scarerButton.setStyle(GuiStyle.transparentButton());
        GuiStyle.applyTransparentHover(scarerButton);
        connectSideHover(scarerButton, true);

        laugherButton = new Button();
        laugherButton.setLayoutX(645);
        laugherButton.setLayoutY(268);
        laugherButton.setPrefWidth(205);
        laugherButton.setPrefHeight(345);
        laugherButton.setStyle(GuiStyle.transparentButton());
        GuiStyle.applyTransparentHover(laugherButton);
        connectSideHover(laugherButton, false);

        root.getChildren().addAll(scarerButton, laugherButton);
        bringControlsToFront();
    }

    private void connectSideHover(Button button, final boolean scarerSide) {
        button.setOnMouseEntered(e -> showSidePreview(scarerSide));
        button.setOnMouseExited(e -> resetSidePreview());

        button.setOnMousePressed(e -> {
            if (choiceLabel != null) {
                choiceLabel.setText(scarerSide ? "SCARER TEAM SELECTED!" : "LAUGHER TEAM SELECTED!");
                choiceLabel.setStyle(monsterLabelStyle("#FFF2B2", 28));
                choiceLabel.setEffect(createTextGlow(
                        scarerSide ? SCARER_GLOW : LAUGHER_GLOW,
                        28,
                        0.52));
            }
        });
    }

    private void showSidePreview(boolean scarerSide) {
        ImageView active = scarerSide ? scarerPortraitView : laugherPortraitView;
        ImageView inactive = scarerSide ? laugherPortraitView : scarerPortraitView;
        Color activeColor = scarerSide ? SCARER_GLOW : LAUGHER_GLOW;
        Color inactiveColor = scarerSide ? LAUGHER_GLOW : SCARER_GLOW;

        if (inactive != null) {
            inactive.setEffect(createPortraitGlow(Color.color(
                    inactiveColor.getRed(),
                    inactiveColor.getGreen(),
                    inactiveColor.getBlue(),
                    0.30), 10, 0.12));
            fadeTo(inactive, 0.48, 150);
        }

        if (active != null) {
            active.toFront();
            active.setEffect(createPortraitGlow(activeColor, 34, 0.48));
            fadeTo(active, 1.0, 120);
        }

        if (choiceLabel != null) {
            choiceLabel.toFront();
            choiceLabel.setText(scarerSide ? "SCARER TEAM  •  SCARE POWER" : "LAUGHER TEAM  •  LAUGH POWER");
            choiceLabel.setStyle(monsterLabelStyle(scarerSide ? "#F6DAFF" : "#DFFFF0", 26));
            choiceLabel.setEffect(createTextGlow(activeColor, 26, 0.50));
        }

        bringControlsToFront();
    }

    private void resetSidePreview() {
        if (scarerPortraitView != null) {
            scarerPortraitView.setEffect(createPortraitGlow(SCARER_GLOW, 18, 0.30));
            fadeTo(scarerPortraitView, 0.94, 180);
        }

        if (laugherPortraitView != null) {
            laugherPortraitView.setEffect(createPortraitGlow(LAUGHER_GLOW, 18, 0.28));
            fadeTo(laugherPortraitView, 0.94, 180);
        }

        if (choiceLabel != null) {
            choiceLabel.setText("CHOOSE YOUR SIDE");
            choiceLabel.setStyle(monsterLabelStyle("#EAFDFF", 30));
            choiceLabel.setEffect(createTextGlow(TEXT_GLOW, 22, 0.42));
            choiceLabel.toFront();
        }

        bringControlsToFront();
    }

    private void fadeTo(ImageView view, double opacity, int durationMillis) {
        FadeTransition fade = new FadeTransition(Duration.millis(durationMillis), view);
        fade.setToValue(opacity);
        fade.play();
    }

    private void bringControlsToFront() {
        if (choiceLabel != null) choiceLabel.toFront();
        if (scarerButton != null) scarerButton.toFront();
        if (laugherButton != null) laugherButton.toFront();
        if (muteButton != null) muteButton.toFront();
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

    public Button getScarerButton() {
        return scarerButton;
    }

    public Button getLaugherButton() {
        return laugherButton;
    }

    public Button getMuteButton() {
        return muteButton;
    }
}
