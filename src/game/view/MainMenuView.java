package game.view;

import javafx.animation.FadeTransition;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class MainMenuView {

    private static final double ROOT_WIDTH = 1280.0;
    private static final double ROOT_HEIGHT = 720.0;

    // The source menu art is 1408 x 768 and is stretched into the 1280 x 720 game canvas.
    // These constants let the popup images line up exactly with the original painted buttons.
    private static final double MENU_SOURCE_WIDTH = 1408.0;
    private static final double MENU_SOURCE_HEIGHT = 768.0;

    private static final double PLAY_IDLE_OPACITY = 0.45;
    private static final double QUIT_IDLE_OPACITY = 0.42;
    private static final double HOVER_OPACITY = 1.0;
    private static final double PRESSED_OPACITY = 0.82;

    private Pane root;
    private Button playButton;
    private Button quitButton;
    private Button muteButton;

    private ImageView playPopupGlow;
    private ImageView quitPopupGlow;

    public MainMenuView() {
        root = new Pane();
        root.setPrefSize(ROOT_WIDTH, ROOT_HEIGHT);

        Image backgroundImage = GuiStyle.loadAssetImage(getClass(), "menu.png");
        if (backgroundImage != null) {
            ImageView background = new ImageView(backgroundImage);
            background.setFitWidth(ROOT_WIDTH);
            background.setFitHeight(ROOT_HEIGHT);
            background.setPreserveRatio(false);
            root.getChildren().add(background);
        }

        createPopupGlowLayer();
        createButtons();
        createMuteButton();
    }

    private void createPopupGlowLayer() {
        // These transparent PNG overlays now stay visible at low opacity.
        // Hover only brightens them, so there is no rough "appearing" animation.
        playPopupGlow = createPopupGlowImage(
                "ui/menu/play_popup_glow.png",
                360, 230, 680, 280,
                Color.rgb(33, 247, 255, 0.92),
                PLAY_IDLE_OPACITY);

        quitPopupGlow = createPopupGlowImage(
                "ui/menu/quit_popup_glow.png",
                358, 368, 700, 300,
                Color.rgb(255, 132, 46, 0.88),
                QUIT_IDLE_OPACITY);

        root.getChildren().addAll(playPopupGlow, quitPopupGlow);
    }

    private ImageView createPopupGlowImage(String assetPath,
                                           double sourceX,
                                           double sourceY,
                                           double sourceWidth,
                                           double sourceHeight,
                                           Color glowColor,
                                           double idleOpacity) {
        Image image = GuiStyle.loadAssetImage(getClass(), assetPath);
        ImageView view = new ImageView(image);

        // Do not change these sizing/position calculations unless you want to manually realign the PNG.
        view.setLayoutX(toCanvasX(sourceX));
        view.setLayoutY(toCanvasY(sourceY));
        view.setFitWidth(toCanvasX(sourceWidth));
        view.setFitHeight(toCanvasY(sourceHeight));
        view.setPreserveRatio(false);
        view.setSmooth(true);
        view.setMouseTransparent(true);
        view.setVisible(true);
        view.setOpacity(idleOpacity);
        view.setScaleX(1.0);
        view.setScaleY(1.0);
        view.setEffect(createMenuGlow(glowColor, 22, 0.22));

        return view;
    }

    private DropShadow createMenuGlow(Color glowColor, double radius, double spread) {
        DropShadow glow = new DropShadow();
        glow.setColor(glowColor);
        glow.setRadius(radius);
        glow.setSpread(spread);
        return glow;
    }

    private double toCanvasX(double sourceValue) {
        return sourceValue * ROOT_WIDTH / MENU_SOURCE_WIDTH;
    }

    private double toCanvasY(double sourceValue) {
        return sourceValue * ROOT_HEIGHT / MENU_SOURCE_HEIGHT;
    }

    private void createButtons() {
        // Invisible hitbox. The visible glow is a separate ImageView above the original menu art.
        playButton = new Button();
        playButton.setLayoutX(512);
        playButton.setLayoutY(304);
        playButton.setPrefWidth(278);
        playButton.setPrefHeight(88);
        playButton.setStyle(GuiStyle.transparentButton());
        GuiStyle.applyTransparentHover(playButton);
        connectPopupHover(playButton, playPopupGlow,
                PLAY_IDLE_OPACITY,
                Color.rgb(33, 247, 255, 0.92));

        quitButton = new Button();
        quitButton.setLayoutX(512);
        quitButton.setLayoutY(424);
        quitButton.setPrefWidth(278);
        quitButton.setPrefHeight(90);
        quitButton.setStyle(GuiStyle.transparentButton());
        GuiStyle.applyTransparentHover(quitButton);
        connectPopupHover(quitButton, quitPopupGlow,
                QUIT_IDLE_OPACITY,
                Color.rgb(255, 132, 46, 0.88));

        root.getChildren().addAll(playButton, quitButton);
        playButton.toFront();
        quitButton.toFront();
    }

    private void connectPopupHover(final Button button,
                                   final ImageView popup,
                                   final double idleOpacity,
                                   final Color glowColor) {
        button.setOnMouseEntered(e -> {
            bringHoveredPopupToFront(popup);
            popup.setEffect(createMenuGlow(glowColor, 30, 0.36));
            fadePopup(popup, HOVER_OPACITY, 140);
        });

        button.setOnMouseExited(e -> {
            popup.setEffect(createMenuGlow(glowColor, 22, 0.22));
            fadePopup(popup, idleOpacity, 190);
        });

        button.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> popup.setOpacity(PRESSED_OPACITY));

        button.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if (button.isHover()) {
                popup.setOpacity(HOVER_OPACITY);
            } else {
                popup.setOpacity(idleOpacity);
            }
        });
    }

    private void fadePopup(ImageView popup, double targetOpacity, int durationMillis) {
        FadeTransition fade = new FadeTransition(Duration.millis(durationMillis), popup);
        fade.setToValue(targetOpacity);
        fade.play();
    }

    private void bringHoveredPopupToFront(ImageView hoveredPopup) {
        // Only the hovered glow is moved above the other glow image.
        // The transparent hitbox buttons are then returned to the front so hover/click input still works.
        if (hoveredPopup != null) hoveredPopup.toFront();
        if (playButton != null) playButton.toFront();
        if (quitButton != null) quitButton.toFront();
        if (muteButton != null) muteButton.toFront();
    }

    private void createMuteButton() {
        muteButton = new Button();
        // Exact hitbox on the round MUTE button at the top-right.
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

    public Button getPlayButton() {
        return playButton;
    }

    public Button getQuitButton() {
        return quitButton;
    }

    public Button getMuteButton() {
        return muteButton;
    }
}
