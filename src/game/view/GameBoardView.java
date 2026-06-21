package game.view;

import game.engine.Board;
import game.engine.Constants;
import game.engine.Role;
import game.engine.cards.Card;
import game.engine.cells.CardCell;
import game.engine.cells.Cell;
import game.engine.cells.ContaminationSock;
import game.engine.cells.ConveyorBelt;
import game.engine.cells.DoorCell;
import game.engine.cells.MonsterCell;
import game.engine.cells.TransportCell;
import game.engine.monsters.Monster;
import java.io.File;
import java.io.InputStream;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class GameBoardView {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    private static final int CELL_SIZE = 45;
    private static final int CELL_GAP = 2;
    private static final int BOARD_X = 397;
    private static final int BOARD_Y = 96;

    private Pane root;
    private Pane overlayLayer;
    private GridPane boardGrid;

    private StackPane[] cellPanes;
    private Label[] indexLabels;
    private Label[] typeLabels;
    private Label[] valueLabels;
    private Label[] stationedLabels;
    private ImageView[] cellIconViews;

    private Label playerInfoLabel;
    private Label opponentInfoLabel;
    private ImageView playerPortrait;
    private ImageView opponentPortrait;
    private Pane playerEnergyBar;
    private Pane opponentEnergyBar;
    private Rectangle playerEnergyFill;
    private Rectangle opponentEnergyFill;
    private Label playerEnergyBarText;
    private Label opponentEnergyBarText;
    private Pane playerDistanceBar;
    private Pane opponentDistanceBar;
    private Rectangle playerDistanceFill;
    private Rectangle opponentDistanceFill;
    private Label playerDistanceBarText;
    private Label opponentDistanceBarText;
    private Label turnLabel;
    private Label diceLabel;
    private StackPane dicePane;
    private StackPane diceFace;
    private Canvas diceCanvas;
    private Circle[] dicePips;
    private Timeline diceTimeline;
    private Label cardLabel;
    private Label actionLabel;
    private Label deckLabel;

    private TextArea logArea;

    private Button rollDiceButton;
    private Button powerupButton;
    private Button menuButton;
    private Button muteButton;
    private Button demoEnergyButton;
    private Button demoWinButton;
    private Pane devToolsBox;

    private Monster lastPlayer;
    private Monster lastOpponent;
    private Monster activeTurnMonster;

    private Image scarerIcon;
    private Image laugherIcon;
    private Image frozenIcon;
    private Image shieldIcon;
    private Image confusionIcon;
    private Image powerupIcon;
    private Image armoredIcon;
    private Image scarerAvatar;
    private Image laugherAvatar;

    private Image scarerDoorIcon;
    private Image laugherDoorIcon;
    private Image usedDoorIcon;
    private Image cardCellIcon;
    private Image conveyorIcon;
    private Image sockIcon;
    private Image monsterCellIcon;
    private Image normalTileIcon;

    public GameBoardView() {
        root = new Pane();
        root.setPrefSize(WIDTH, HEIGHT);
        root.setFocusTraversable(true);

        cellPanes = new StackPane[Constants.BOARD_SIZE];
        indexLabels = new Label[Constants.BOARD_SIZE];
        typeLabels = new Label[Constants.BOARD_SIZE];
        valueLabels = new Label[Constants.BOARD_SIZE];
        stationedLabels = new Label[Constants.BOARD_SIZE];
        cellIconViews = new ImageView[Constants.BOARD_SIZE];

        loadMonsterIcons();
        loadCellIcons();
        createBackground();
        createMuteButton();
        createDevToolsBox();
        createInfoPanels();
        createBoardGrid();
        createControlButtons();
        createStatusPanels();
        createLogArea();
        buildLegend();
        keepControlsOnTop();
        createOverlayLayer();
    }


    private void createOverlayLayer() {
        overlayLayer = new Pane();
        overlayLayer.setPrefSize(WIDTH, HEIGHT);
        overlayLayer.setMouseTransparent(true);
        root.getChildren().add(overlayLayer);
        overlayLayer.toFront();
    }

    private void keepControlsOnTop() {
        if (rollDiceButton != null) rollDiceButton.toFront();
        if (powerupButton != null) powerupButton.toFront();
        if (menuButton != null) menuButton.toFront();
        if (muteButton != null) muteButton.toFront();
        if (devToolsBox != null) devToolsBox.toFront();
        if (demoEnergyButton != null) demoEnergyButton.toFront();
        if (demoWinButton != null) demoWinButton.toFront();
    }

    private void createBackground() {
        Image boardBackground = loadAssetImage("Board.png");

        if (boardBackground != null) {
            ImageView background = new ImageView(boardBackground);
            background.setFitWidth(WIDTH);
            background.setFitHeight(HEIGHT);
            background.setPreserveRatio(false);
            root.getChildren().add(background);
        } else {
            // Safe fallback if Eclipse did not copy the image resources yet.
            root.setStyle("-fx-background-color: #080a18;");
        }
    }

    private void loadMonsterIcons() {
        scarerIcon = loadAssetImage("ui/monsters/scarer_monster_big.png");
        laugherIcon = loadAssetImage("ui/monsters/laugher_monster_big.png");
        frozenIcon = loadAssetImage("ui/monsters/frozen_monster_big.png");
        shieldIcon = loadAssetImage("ui/cells/shield_status.png");
        confusionIcon = loadAssetImage("ui/cells/confusion_status.png");
        powerupIcon = loadAssetImage("ui/cells/powerup_star.png");
        armoredIcon = loadAssetImage("ui/monsters/armored_monster_big.png");
        scarerAvatar = loadAssetImage("ui/portraits/scarer_avatar.png");
        laugherAvatar = loadAssetImage("ui/portraits/laugher_avatar.png");

        // Fallback to the older assets if the new generated assets were not copied yet.
        if (scarerIcon == null) scarerIcon = loadAssetImage("monsters/small/scarer_monster.png");
        if (laugherIcon == null) laugherIcon = loadAssetImage("monsters/small/laugher_monster.png");
        if (frozenIcon == null) frozenIcon = loadAssetImage("monsters/small/frozen_status.png");
        if (shieldIcon == null) shieldIcon = loadAssetImage("monsters/small/shield_status.png");
        if (confusionIcon == null) confusionIcon = loadAssetImage("monsters/small/confusion_status.png");
        if (powerupIcon == null) powerupIcon = loadAssetImage("monsters/small/powerup_star.png");
        if (armoredIcon == null) armoredIcon = scarerIcon;
        if (scarerAvatar == null) scarerAvatar = scarerIcon;
        if (laugherAvatar == null) laugherAvatar = laugherIcon;
    }

    private void loadCellIcons() {
        scarerDoorIcon = loadAssetImage("ui/cell_icons/scarer_door.png");
        laugherDoorIcon = loadAssetImage("ui/cell_icons/laugher_door.png");
        usedDoorIcon = loadAssetImage("ui/cell_icons/used_door.png");
        cardCellIcon = loadAssetImage("ui/cell_icons/card_cell.png");
        conveyorIcon = loadAssetImage("ui/cell_icons/conveyor.png");
        sockIcon = loadAssetImage("ui/cell_icons/sock.png");
        monsterCellIcon = loadAssetImage("ui/cell_icons/monster_cell.png");
        normalTileIcon = loadAssetImage("ui/cell_icons/normal_node.png");

        if (scarerDoorIcon == null) scarerDoorIcon = loadAssetImage("ui/cells/scarer_door.png");
        if (laugherDoorIcon == null) laugherDoorIcon = loadAssetImage("ui/cells/laugher_door.png");
        if (usedDoorIcon == null) usedDoorIcon = loadAssetImage("ui/cells/used_door.png");
        if (cardCellIcon == null) cardCellIcon = loadAssetImage("ui/cells/card_cell.png");
        if (conveyorIcon == null) conveyorIcon = loadAssetImage("ui/cells/conveyor.png");
        if (sockIcon == null) sockIcon = loadAssetImage("ui/cells/sock.png");
        if (monsterCellIcon == null) monsterCellIcon = loadAssetImage("ui/cells/monster_cell.png");
        if (normalTileIcon == null) normalTileIcon = loadAssetImage("ui/cells/normal_tile.png");
    }

    private Image loadAssetImage(String relativePath) {
        String[] resourcePaths = {
                "/game/assets/" + relativePath,
                "game/assets/" + relativePath
        };

        for (int i = 0; i < resourcePaths.length; i++) {
            try {
                InputStream stream = getClass().getResourceAsStream(resourcePaths[i]);
                if (stream != null) {
                    return new Image(stream);
                }
            } catch (Exception ignored) { }
        }

        String[] filePaths = {
                "src/game/assets/" + relativePath,
                "game/assets/" + relativePath,
                "assets/" + relativePath
        };

        for (int i = 0; i < filePaths.length; i++) {
            try {
                File file = new File(filePaths[i]);
                if (file.exists()) {
                    return new Image(file.toURI().toString());
                }
            } catch (Exception ignored) { }
        }

        return null;
    }


    private void createMuteButton() {
        muteButton = new Button("MUSIC");
        // Game-board mute button sits above the board instead of covering the top-right art.
        muteButton.setLayoutX(496);
        muteButton.setLayoutY(14);
        muteButton.setPrefWidth(100);
        muteButton.setPrefHeight(32);
        muteButton.setFocusTraversable(false);
        muteButton.setStyle(GuiStyle.mutePillButton(false));
        root.getChildren().add(muteButton);
        muteButton.toFront();
    }

    private void createDevToolsBox() {
        devToolsBox = new Pane();
        // Move the whole DEV TOOLS group only from these two lines.
        // Everything inside uses coordinates relative to this small box.
        devToolsBox.setLayoutX(606);
        devToolsBox.setLayoutY(8);
        devToolsBox.setPrefWidth(178);
        devToolsBox.setPrefHeight(44);
        devToolsBox.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(20, 24, 8, 0.96), rgba(3, 8, 18, 0.94));" +
                             "-fx-border-color: rgba(255, 243, 49, 0.92);" +
                             "-fx-border-width: 1.5;" +
                             "-fx-border-radius: 11;" +
                             "-fx-background-radius: 11;" +
                             "-fx-effect: dropshadow(gaussian, rgba(255, 243, 49, 0.32), 9, 0.30, 0, 0);");

        Label title = new Label("DEV TOOLS");
        title.setLayoutX(6);
        title.setLayoutY(2);
        title.setPrefWidth(166);
        title.setPrefHeight(13);
        title.setAlignment(Pos.CENTER);
        title.setMouseTransparent(true);
        title.setStyle("-fx-font-size: 7.5px;" +
                       "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
                       "-fx-font-weight: bold;" +
                       "-fx-text-fill: #fff331;" +
                       "-fx-effect: dropshadow(gaussian, black, 2, 0.6, 0, 1);");

        demoEnergyButton = new Button("E ENERGY");
        demoEnergyButton.setLayoutX(10);
        demoEnergyButton.setLayoutY(18);
        demoEnergyButton.setPrefWidth(74);
        demoEnergyButton.setPrefHeight(20);
        demoEnergyButton.setFocusTraversable(false);
        demoEnergyButton.setStyle(devToolButtonStyle("green", false));
        GuiStyle.applyGlowHover(demoEnergyButton, devToolButtonStyle("green", false), devToolButtonStyle("green", true));

        demoWinButton = new Button("W TO 99");
        demoWinButton.setLayoutX(94);
        demoWinButton.setLayoutY(18);
        demoWinButton.setPrefWidth(74);
        demoWinButton.setPrefHeight(20);
        demoWinButton.setFocusTraversable(false);
        demoWinButton.setStyle(devToolButtonStyle("purple", false));
        GuiStyle.applyGlowHover(demoWinButton, devToolButtonStyle("purple", false), devToolButtonStyle("purple", true));

        devToolsBox.getChildren().addAll(title, demoEnergyButton, demoWinButton);
        root.getChildren().add(devToolsBox);
        devToolsBox.toFront();
    }

    private String devToolButtonStyle(String theme, boolean hover) {
        String border = "#fff331";
        String top = "rgba(30, 45, 26, 0.98)";
        String bottom = "rgba(8, 18, 12, 0.98)";

        if ("purple".equals(theme)) {
            border = "#c65cff";
            top = "rgba(36, 18, 58, 0.98)";
            bottom = "rgba(13, 6, 30, 0.98)";
        } else if ("green".equals(theme)) {
            border = "#69ff5a";
            top = "rgba(21, 55, 27, 0.98)";
            bottom = "rgba(6, 22, 10, 0.98)";
        }

        return "-fx-font-size: 8px;" +
               "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
               "-fx-font-weight: bold;" +
               "-fx-text-fill: white;" +
               "-fx-background-color: linear-gradient(to bottom, " + top + ", " + bottom + ");" +
               "-fx-border-color: " + border + ";" +
               "-fx-border-width: " + (hover ? "2" : "1") + ";" +
               "-fx-background-radius: 7;" +
               "-fx-border-radius: 7;" +
               "-fx-padding: 0;" +
               "-fx-cursor: hand;" +
               "-fx-effect: dropshadow(gaussian, " + border + ", " + (hover ? "9" : "4") + ", 0.35, 0, 0);";
    }

    private void createInfoPanels() {
        playerInfoLabel = new Label("PLAYER");
        playerInfoLabel.setLayoutX(24);
        playerInfoLabel.setLayoutY(148);
        playerInfoLabel.setPrefWidth(214);
        playerInfoLabel.setPrefHeight(224);
        // Keep the written monster info at the top so the bottom meters never cover it.
        playerInfoLabel.setAlignment(Pos.TOP_LEFT);
        playerInfoLabel.setStyle(GuiStyle.hudPanel("cyan") + GuiStyle.smallInfoText());

        opponentInfoLabel = new Label("OPPONENT");
        opponentInfoLabel.setLayoutX(1044);
        opponentInfoLabel.setLayoutY(playerInfoLabel.getLayoutY());
        opponentInfoLabel.setPrefWidth(214);
        opponentInfoLabel.setPrefHeight(224);
        // Keep the written monster info at the top so the bottom meters never cover it.
        opponentInfoLabel.setAlignment(Pos.TOP_LEFT);
        opponentInfoLabel.setStyle(GuiStyle.hudPanel("red") + GuiStyle.smallInfoText());

        playerPortrait = new ImageView();
        playerPortrait.setFitWidth(126);
        playerPortrait.setFitHeight(126);
        playerPortrait.setPreserveRatio(true);
        playerPortrait.setSmooth(true);
        playerPortrait.setLayoutX(48);
        playerPortrait.setLayoutY(18);
        playerPortrait.setMouseTransparent(true);
        playerPortrait.setStyle("-fx-effect: dropshadow(gaussian, rgba(35,247,255,0.95), 16, 0.45, 0, 0);");

        opponentPortrait = new ImageView();
        opponentPortrait.setFitWidth(126);
        opponentPortrait.setFitHeight(126);
        opponentPortrait.setPreserveRatio(true);
        opponentPortrait.setSmooth(true);
        opponentPortrait.setLayoutX(1108);
        opponentPortrait.setLayoutY(playerPortrait.getLayoutY());
        opponentPortrait.setMouseTransparent(true);
        opponentPortrait.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,70,80,0.95), 16, 0.45, 0, 0);");

        createEnergyBars();
        createDistanceBars();
        root.getChildren().addAll(playerInfoLabel, opponentInfoLabel, playerPortrait, opponentPortrait,
                playerDistanceBar, opponentDistanceBar, playerDistanceBarText, opponentDistanceBarText,
                playerEnergyBar, opponentEnergyBar, playerEnergyBarText, opponentEnergyBarText);
        playerDistanceBar.toFront();
        opponentDistanceBar.toFront();
        playerDistanceBarText.toFront();
        opponentDistanceBarText.toFront();
        playerEnergyBar.toFront();
        opponentEnergyBar.toFront();
        playerEnergyBarText.toFront();
        opponentEnergyBarText.toFront();
    }

    private void createEnergyBars() {
        playerEnergyBar = createEnergyBar(44, 341, "cyan", true);
        opponentEnergyBar = createEnergyBar(1064, 341, "red", false);

        playerEnergyBarText = createEnergyBarText(44, 344);
        opponentEnergyBarText = createEnergyBarText(1064, 344);
    }

    private void createDistanceBars() {
        // Small race-progress meters inside the side panels: progress to the final door, not energy.
        playerDistanceBar = createDistanceBar(44, 312, "cyan", true);
        opponentDistanceBar = createDistanceBar(1064, 312, "red", false);

        playerDistanceBarText = createDistanceBarText(44, 314);
        opponentDistanceBarText = createDistanceBarText(1064, 314);
    }

    private Pane createEnergyBar(double x, double y, String theme, boolean playerSide) {
        Pane meter = new Pane();
        meter.setLayoutX(x);
        meter.setLayoutY(y);
        meter.setPrefWidth(180);
        meter.setPrefHeight(29);
        meter.setMouseTransparent(true);
        meter.setStyle("-fx-effect: dropshadow(gaussian, " + energyAccent(theme, 0) + ", 13, 0.26, 0, 0);");

        String baseBorder = "red".equals(theme) ? "#ff5757" : "#23f7ff";

        Rectangle outer = new Rectangle(0, 0, 180, 29);
        outer.setArcWidth(16);
        outer.setArcHeight(16);
        outer.setFill(Color.web("rgba(2, 7, 18, 0.95)"));
        outer.setStroke(Color.web(baseBorder));
        outer.setStrokeWidth(2.0);

        Rectangle innerFrame = new Rectangle(8, 6, 148, 17);
        innerFrame.setArcWidth(10);
        innerFrame.setArcHeight(10);
        innerFrame.setFill(Color.web("rgba(0, 0, 0, 0.48)"));
        innerFrame.setStroke(Color.web("rgba(255,255,255,0.18)"));
        innerFrame.setStrokeWidth(0.8);

        Rectangle fill = new Rectangle(10, 8, 2, 13);
        fill.setArcWidth(8);
        fill.setArcHeight(8);
        fill.setFill(Color.web(energyAccent(theme, 0)));
        fill.setStyle("-fx-effect: dropshadow(gaussian, " + energyAccent(theme, 0) + ", 8, 0.38, 0, 0);");

        Rectangle shine = new Rectangle(12, 9, 140, 3);
        shine.setArcWidth(5);
        shine.setArcHeight(5);
        shine.setFill(Color.web("rgba(255,255,255,0.24)"));

        Rectangle cap = new Rectangle(161, 5, 13, 19);
        cap.setArcWidth(7);
        cap.setArcHeight(7);
        cap.setFill(Color.web("rgba(255,255,255,0.16)"));
        cap.setStroke(Color.web(baseBorder));
        cap.setStrokeWidth(1.2);

        Circle boltTop = new Circle(167.5, 11.0, 1.7, Color.web("rgba(255,255,255,0.70)"));
        Circle boltBottom = new Circle(167.5, 18.0, 1.7, Color.web("rgba(255,255,255,0.70)"));

        meter.getChildren().addAll(outer, innerFrame, fill, shine);
        for (int i = 1; i <= 4; i++) {
            Rectangle tick = new Rectangle(10 + i * 28, 8, 1.2, 13);
            tick.setFill(Color.web("rgba(255,255,255,0.30)"));
            meter.getChildren().add(tick);
        }
        meter.getChildren().addAll(cap, boltTop, boltBottom);

        if (playerSide) {
            playerEnergyFill = fill;
        } else {
            opponentEnergyFill = fill;
        }

        return meter;
    }

    private Label createEnergyBarText(double x, double y) {
        Label label = new Label("CANISTER 0 / " + Constants.WINNING_ENERGY);
        label.setLayoutX(x);
        label.setLayoutY(y - 1);
        label.setPrefWidth(180);
        label.setPrefHeight(20);
        label.setAlignment(Pos.CENTER);
        label.setMouseTransparent(true);
        label.setStyle("-fx-font-size: 8.5px;" +
                       "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
                       "-fx-font-weight: bold;" +
                       "-fx-text-fill: #f5feff;" +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.98), 4, 0.82, 0, 1);");
        return label;
    }

    private Pane createDistanceBar(double x, double y, String theme, boolean playerSide) {
        Pane meter = new Pane();
        meter.setLayoutX(x);
        meter.setLayoutY(y);
        meter.setPrefWidth(180);
        meter.setPrefHeight(22);
        meter.setMouseTransparent(true);

        String accent = distanceAccent(theme);
        meter.setStyle("-fx-effect: dropshadow(gaussian, " + accent + ", 8, 0.18, 0, 0);");

        Rectangle outer = new Rectangle(0, 0, 180, 22);
        outer.setArcWidth(13);
        outer.setArcHeight(13);
        outer.setFill(Color.web("rgba(2, 7, 18, 0.90)"));
        outer.setStroke(Color.web(accent));
        outer.setStrokeWidth(1.6);

        Rectangle track = new Rectangle(9, 7, 142, 8);
        track.setArcWidth(8);
        track.setArcHeight(8);
        track.setFill(Color.web("rgba(0,0,0,0.50)"));
        track.setStroke(Color.web("rgba(255,255,255,0.16)"));
        track.setStrokeWidth(0.7);

        Rectangle fill = new Rectangle(10, 8, 2, 6);
        fill.setArcWidth(7);
        fill.setArcHeight(7);
        fill.setFill(Color.web(accent));
        fill.setStyle("-fx-effect: dropshadow(gaussian, " + accent + ", 6, 0.34, 0, 0);");

        // Small final-door marker at the end of the path.
        Rectangle door = new Rectangle(158, 4, 15, 14);
        door.setArcWidth(5);
        door.setArcHeight(5);
        door.setFill(Color.web("rgba(255, 243, 49, 0.22)"));
        door.setStroke(Color.web("#fff331"));
        door.setStrokeWidth(1.0);

        Circle knob = new Circle(169, 11, 1.6, Color.web("#fff331"));

        meter.getChildren().addAll(outer, track, fill);
        for (int i = 1; i <= 4; i++) {
            Rectangle tick = new Rectangle(10 + i * 28, 8, 1.0, 6);
            tick.setFill(Color.web("rgba(255,255,255,0.22)"));
            meter.getChildren().add(tick);
        }
        meter.getChildren().addAll(door, knob);

        if (playerSide) {
            playerDistanceFill = fill;
        } else {
            opponentDistanceFill = fill;
        }

        return meter;
    }

    private Label createDistanceBarText(double x, double y) {
        Label label = new Label("99 CELLS LEFT");
        label.setLayoutX(x);
        label.setLayoutY(y - 1);
        label.setPrefWidth(180);
        label.setPrefHeight(18);
        label.setAlignment(Pos.CENTER);
        label.setMouseTransparent(true);
        label.setStyle("-fx-font-size: 7.5px;" +
                       "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
                       "-fx-font-weight: bold;" +
                       "-fx-text-fill: #f5feff;" +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.98), 4, 0.82, 0, 1);");
        return label;
    }

    private void updateDistanceBars(Monster player, Monster opponent) {
        updateDistanceBar(playerDistanceBar, playerDistanceFill, playerDistanceBarText, player, "cyan");
        updateDistanceBar(opponentDistanceBar, opponentDistanceFill, opponentDistanceBarText, opponent, "red");
    }

    private void updateDistanceBar(Pane bar, Rectangle fill, Label text, Monster monster, String theme) {
        if (bar == null || fill == null || text == null) return;

        int position = monster == null ? 0 : monster.getPosition();
        if (position < 0) position = 0;
        if (position > Constants.WINNING_POSITION) position = Constants.WINNING_POSITION;

        int cellsLeft = Math.max(0, Constants.WINNING_POSITION - position);
        double ratio = Constants.WINNING_POSITION <= 0 ? 0.0 : position / (double) Constants.WINNING_POSITION;
        double maxFillWidth = 140.0;
        fill.setWidth(Math.max(2.0, maxFillWidth * ratio));

        String accent = cellsLeft == 0 ? "#7dff7d" : distanceAccent(theme);
        fill.setFill(Color.web(accent));
        fill.setStyle("-fx-effect: dropshadow(gaussian, " + accent + ", " + (cellsLeft == 0 ? "13" : "7") + ", 0.36, 0, 0);");
        bar.setStyle("-fx-effect: dropshadow(gaussian, " + accent + ", " + (cellsLeft == 0 ? "14" : "8") + ", 0.18, 0, 0);");

        text.setText(cellsLeft == 0 ? "FINAL DOOR REACHED" : cellsLeft + " CELLS LEFT");
    }

    private String distanceAccent(String theme) {
        if ("red".equals(theme)) return "#ff8b55";
        return "#35d7ff";
    }

    private void updateEnergyBars(Monster player, Monster opponent) {
        updateEnergyBar(playerEnergyBar, playerEnergyFill, playerEnergyBarText, player, "cyan");
        updateEnergyBar(opponentEnergyBar, opponentEnergyFill, opponentEnergyBarText, opponent, "red");
    }

    private void updateEnergyBar(Pane bar, Rectangle fill, Label text, Monster monster, String theme) {
        if (bar == null || fill == null || text == null) return;

        int energy = monster == null ? 0 : Math.max(0, monster.getEnergy());
        double ratio = Math.min(1.0, energy / (double) Constants.WINNING_ENERGY);
        double maxFillWidth = 142.0;
        fill.setWidth(Math.max(2.0, maxFillWidth * ratio));

        String accent = energyAccent(theme, ratio);
        fill.setFill(Color.web(accent));
        fill.setStyle("-fx-effect: dropshadow(gaussian, " + accent + ", " + (ratio >= 1.0 ? "16" : "9") + ", 0.42, 0, 0);");
        bar.setStyle("-fx-effect: dropshadow(gaussian, " + accent + ", " + (ratio >= 1.0 ? "18" : "10") + ", 0.24, 0, 0);");

        String label = "CANISTER " + energy + " / " + Constants.WINNING_ENERGY;
        if (energy >= Constants.WINNING_ENERGY) {
            label += "  READY";
        }
        text.setText(label);
    }

    private String energyAccent(String theme, double ratio) {
        if (ratio >= 1.0) return "#7dff7d";
        if (ratio >= 0.55) return "#fff331";
        if ("red".equals(theme)) return "#ff5757";
        return "#23f7ff";
    }

    private void createBoardGrid() {
        boardGrid = new GridPane();
        boardGrid.setLayoutX(BOARD_X);
        boardGrid.setLayoutY(BOARD_Y);
        boardGrid.setHgap(CELL_GAP);
        boardGrid.setVgap(CELL_GAP);
        boardGrid.setStyle(GuiStyle.boardFrame());

        for (int boardRow = 0; boardRow < Constants.BOARD_ROWS; boardRow++) {
            for (int visualCol = 0; visualCol < Constants.BOARD_COLS; visualCol++) {
                int index = getBoardIndex(boardRow, visualCol);

                StackPane cell = createCellPane(index);
                boardGrid.add(cell, visualCol, Constants.BOARD_ROWS - 1 - boardRow);
                cellPanes[index] = cell;
            }
        }

        root.getChildren().add(boardGrid);
    }

    private StackPane createCellPane(int index) {
        StackPane cell = new StackPane();
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);
        cell.setMinSize(CELL_SIZE, CELL_SIZE);
        cell.setMaxSize(CELL_SIZE, CELL_SIZE);
        cell.setStyle(getFallbackStyle(index));

        ImageView iconView = new ImageView();
        iconView.setFitWidth(27);
        iconView.setFitHeight(27);
        iconView.setPreserveRatio(true);
        iconView.setSmooth(true);
        iconView.setMouseTransparent(true);
        iconView.setOpacity(0.0);

        Label indexLabel = new Label(String.valueOf(index));
        indexLabel.setStyle("-fx-font-size: 9px; -fx-font-family: 'Trebuchet MS', 'Impact', 'Arial Black', 'Arial'; -fx-font-weight: bold; -fx-text-fill: rgba(255,255,255,0.96); -fx-effect: dropshadow(gaussian, black, 3, 0.7, 0, 0);");
        StackPane.setAlignment(indexLabel, Pos.TOP_LEFT);
        StackPane.setMargin(indexLabel, new Insets(2, 0, 0, 3));

        Label typeLabel = new Label("");
        typeLabel.setStyle("-fx-font-size: 9px; -fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial'; -fx-font-weight: bold; -fx-text-fill: rgba(255,255,255,0.88); -fx-effect: dropshadow(gaussian, black, 4, 0.75, 0, 0);");
        StackPane.setAlignment(typeLabel, Pos.CENTER);

        Label valueLabel = new Label("");
        valueLabel.setStyle("-fx-font-size: 9px; -fx-font-family: 'Trebuchet MS', 'Impact', 'Arial Black', 'Arial'; -fx-font-weight: bold; -fx-text-fill: #fff4a0; -fx-background-color: rgba(0,0,0,0.42); -fx-background-radius: 5; -fx-padding: 0 3 0 3;");
        StackPane.setAlignment(valueLabel, Pos.BOTTOM_CENTER);
        StackPane.setMargin(valueLabel, new Insets(0, 0, 2, 0));

        Label stationedLabel = new Label("");
        stationedLabel.setStyle("-fx-font-size: 9px; -fx-font-family: 'Trebuchet MS', 'Impact', 'Arial Black', 'Arial'; -fx-font-weight: bold; -fx-text-fill: #ffccff; -fx-background-color: rgba(20,0,35,0.72); -fx-background-radius: 5; -fx-padding: 0 3 0 3;");
        StackPane.setAlignment(stationedLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(stationedLabel, new Insets(2, 3, 0, 0));

        cell.getChildren().addAll(iconView, indexLabel, typeLabel, valueLabel, stationedLabel);
        indexLabels[index] = indexLabel;
        typeLabels[index] = typeLabel;
        valueLabels[index] = valueLabel;
        stationedLabels[index] = stationedLabel;
        cellIconViews[index] = iconView;

        return cell;
    }

    private void createControlButtons() {
        rollDiceButton = new Button("ROLL  [R]");
        rollDiceButton.setLayoutX(296);
        rollDiceButton.setLayoutY(626);
        rollDiceButton.setPrefWidth(120);
        rollDiceButton.setPrefHeight(50);
        styleActionButton(rollDiceButton, "cyan");

        powerupButton = new Button("POWER  [P]");
        powerupButton.setLayoutX(428);
        powerupButton.setLayoutY(626);
        powerupButton.setPrefWidth(140);
        powerupButton.setPrefHeight(50);
        styleActionButton(powerupButton, "orange");

        menuButton = new Button("MENU");
        menuButton.setLayoutX(582);
        menuButton.setLayoutY(626);
        menuButton.setPrefWidth(110);
        menuButton.setPrefHeight(50);
        styleActionButton(menuButton, "purple");

        root.getChildren().addAll(rollDiceButton, powerupButton, menuButton);
    }

    private void createStatusPanels() {
        actionLabel = new Label("ACTION: Game ready");
        actionLabel.setAlignment(Pos.CENTER);
        actionLabel.setLayoutX(300);
        actionLabel.setLayoutY(584);
        actionLabel.setPrefWidth(390);
        actionLabel.setPrefHeight(32);
        actionLabel.setStyle(GuiStyle.statusPanel("cyan") + GuiStyle.normalText(13));

        turnLabel = new Label("Turn: -");
        turnLabel.setLayoutX(705);
        turnLabel.setLayoutY(584);
        turnLabel.setPrefWidth(250);
        turnLabel.setPrefHeight(32);
        turnLabel.setStyle(GuiStyle.statusPanel("purple") + GuiStyle.normalText(13));

        dicePane = new StackPane();
        dicePane.setLayoutX(705);
        dicePane.setLayoutY(626);
        dicePane.setPrefWidth(115);
        dicePane.setPrefHeight(46);
        dicePane.setStyle(GuiStyle.statusPanel("cyan"));

        Label diceTitle = new Label("DICE");
        diceTitle.setStyle("-fx-font-size: 8px;" +
                           "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
                           "-fx-font-weight: bold;" +
                           "-fx-text-fill: #9ffcff;");

        diceFace = new StackPane();
        diceFace.setPrefWidth(34);
        diceFace.setPrefHeight(34);
        diceFace.setMaxWidth(34);
        diceFace.setMaxHeight(34);
        diceFace.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #d9ecff);" +
                          "-fx-border-color: #23f7ff;" +
                          "-fx-border-width: 2;" +
                          "-fx-background-radius: 8;" +
                          "-fx-border-radius: 8;" +
                          "-fx-effect: dropshadow(gaussian, rgba(35,247,255,0.55), 9, 0.35, 0, 0);");

        diceCanvas = new Canvas(34, 34);
        diceCanvas.setMouseTransparent(true);
        diceFace.getChildren().add(diceCanvas);

        diceLabel = new Label("-");
        diceLabel.setMouseTransparent(true);
        diceLabel.setStyle("-fx-font-size: 1px; -fx-text-fill: transparent;");

        VBox diceBox = new VBox(0);
        diceBox.setAlignment(Pos.CENTER);
        diceBox.getChildren().addAll(diceTitle, diceFace, diceLabel);
        dicePane.getChildren().add(diceBox);
        drawDiceFace(0);

        deckLabel = new Label("Deck: -");
        deckLabel.setLayoutX(830);
        deckLabel.setLayoutY(626);
        deckLabel.setPrefWidth(115);
        deckLabel.setPrefHeight(46);
        deckLabel.setAlignment(Pos.CENTER);
        deckLabel.setStyle(GuiStyle.statusPanel("green") + GuiStyle.normalText(13));

        cardLabel = new Label("Card Drawn: -");
        cardLabel.setLayoutX(955);
        cardLabel.setLayoutY(626);
        cardLabel.setPrefWidth(300);
        cardLabel.setPrefHeight(46);
        cardLabel.setWrapText(true);
        cardLabel.setStyle(GuiStyle.statusPanel("orange") + GuiStyle.normalText(12));

        root.getChildren().addAll(actionLabel, turnLabel, dicePane, deckLabel, cardLabel);
    }

    private void createLogArea() {
        logArea = new TextArea();
        logArea.setText("");
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setLayoutX(1048);
        logArea.setLayoutY(385);
        logArea.setPrefWidth(210);
        logArea.setPrefHeight(170);
        logArea.setStyle("-fx-font-size: 11px;" +
                         "-fx-font-family: 'Trebuchet MS', 'Impact', 'Arial Black', 'Arial';" +
                         "-fx-font-weight: bold;" +
                         "-fx-text-fill: white;" +
                         "-fx-control-inner-background: rgba(2, 6, 18, 0.94);" +
                         "-fx-background-color: rgba(2, 6, 18, 0.94);" +
                         "-fx-border-color: rgba(35, 247, 255, 0.95);" +
                         "-fx-border-width: 2;" +
                         "-fx-background-radius: 10;" +
                         "-fx-border-radius: 10;" +
                         "-fx-effect: dropshadow(gaussian, rgba(0, 238, 255, 0.45), 16, 0.35, 0, 0);");

        root.getChildren().add(logArea);
    }

    public void buildLegend() {
        // Lowered and expanded legend: now shows every visible board element separately.
        StackPane legendPanel = new StackPane();
        legendPanel.setLayoutX(20);
        legendPanel.setLayoutY(405);
        legendPanel.setPrefWidth(214);
        legendPanel.setPrefHeight(208);
        legendPanel.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(5, 13, 28, 0.84), rgba(2, 6, 16, 0.90));" +
                             "-fx-border-color: rgba(35, 247, 255, 0.68);" +
                             "-fx-border-width: 2;" +
                             "-fx-background-radius: 12;" +
                             "-fx-border-radius: 12;" +
                             "-fx-effect: dropshadow(gaussian, rgba(35,247,255,0.26), 14, 0.28, 0, 0);");
        root.getChildren().add(legendPanel);

        Label title = new Label("BOARD LEGEND");
        title.setLayoutX(34);
        title.setLayoutY(410);
        title.setPrefWidth(176);
        title.setPrefHeight(16);
        title.setAlignment(Pos.CENTER);
        title.setMouseTransparent(true);
        title.setStyle("-fx-font-size: 10px;" +
                       "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
                       "-fx-font-weight: bold;" +
                       "-fx-text-fill: #9ffcff;" +
                       "-fx-letter-spacing: 1px;" +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.95), 3, 0.7, 0, 1);");
        root.getChildren().add(title);

        int x = 32;
        int y = 431;
        int step = 18;
        addLegendItem(x, y, scarerDoorIcon, "Scarer door"); y += step;
        addLegendItem(x, y, laugherDoorIcon, "Laugher door"); y += step;
        addLegendItem(x, y, usedDoorIcon, "Used door"); y += step;
        addLegendItem(x, y, cardCellIcon, "Card cell"); y += step;
        addLegendItem(x, y, monsterCellIcon, "Monster cell"); y += step;
        addLegendItem(x, y, conveyorIcon, "Conveyor"); y += step;
        addLegendItem(x, y, sockIcon, "Sock"); y += step;
        addLegendDot(x, y, "#23f7ff", "Player marker"); y += step;
        addLegendDot(x, y, "#ff5757", "Opponent marker"); y += step;
        addLegendStatusIcons(x, y);
    }

    private void addLegendItem(int x, int y, Image icon, String text) {
        if (icon != null) {
            ImageView iconView = new ImageView(icon);
            iconView.setLayoutX(x);
            iconView.setLayoutY(y - 2);
            iconView.setFitWidth(18);
            iconView.setFitHeight(18);
            iconView.setPreserveRatio(true);
            iconView.setSmooth(true);
            iconView.setMouseTransparent(true);
            root.getChildren().add(iconView);
        } else {
            addLegendFallbackTile(x, y, "#506070");
        }

        addLegendText(x + 27, y, text, 148);
    }

    private void addLegendFallbackTile(int x, int y, String color) {
        Rectangle tile = new Rectangle(16, 16);
        tile.setLayoutX(x + 1);
        tile.setLayoutY(y);
        tile.setArcWidth(5);
        tile.setArcHeight(5);
        tile.setFill(Color.web("rgba(14, 26, 42, 0.92)"));
        tile.setStroke(Color.web(color));
        tile.setStrokeWidth(1.4);
        tile.setMouseTransparent(true);
        root.getChildren().add(tile);
    }

    private void addLegendDot(int x, int y, String color, String text) {
        Circle dot = new Circle(8);
        dot.setLayoutX(x + 9);
        dot.setLayoutY(y + 8);
        dot.setFill(Color.web("rgba(0,0,0,0.42)"));
        dot.setStroke(Color.web(color));
        dot.setStrokeWidth(2.0);
        dot.setMouseTransparent(true);
        root.getChildren().add(dot);
        addLegendText(x + 27, y, text, 148);
    }

    private void addLegendStatusIcons(int x, int y) {
        int iconX = x;
        if (shieldIcon != null) {
            addLegendSmallIcon(iconX, y, shieldIcon);
            iconX += 18;
        }
        if (frozenIcon != null) {
            addLegendSmallIcon(iconX, y, frozenIcon);
            iconX += 18;
        }
        if (confusionIcon != null) {
            addLegendSmallIcon(iconX, y, confusionIcon);
        }
        if (iconX == x) {
            addLegendFallbackTile(x, y, "#c65cff");
        }
        addLegendText(x + 62, y, "Status icons", 112);
    }

    private void addLegendSmallIcon(int x, int y, Image icon) {
        ImageView iconView = new ImageView(icon);
        iconView.setLayoutX(x);
        iconView.setLayoutY(y - 1);
        iconView.setFitWidth(15);
        iconView.setFitHeight(15);
        iconView.setPreserveRatio(true);
        iconView.setSmooth(true);
        iconView.setMouseTransparent(true);
        root.getChildren().add(iconView);
    }

    private void addLegendText(int x, int y, String text, int width) {
        Label label = new Label(text);
        label.setLayoutX(x);
        label.setLayoutY(y - 1);
        label.setPrefWidth(width);
        label.setPrefHeight(17);
        label.setMouseTransparent(true);
        label.setStyle("-fx-font-size: 9px;" +
                       "-fx-font-family: 'Trebuchet MS', 'Arial Black', 'Arial';" +
                       "-fx-font-weight: bold;" +
                       "-fx-text-fill: white;" +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.94), 3, 0.78, 0, 1);");
        root.getChildren().add(label);
    }

    public void refresh(Board board, Monster player, Monster opponent, Monster current) {
        updateBoardCells(board);
        updateMonsterInfo(player, opponent);
        updateTurn(current);
        updateMonsterPositions(player, opponent);
        updateDeckCount(safeDeckCount());
    }

    public void updateBoardCells(Board board) {
        if (board == null) return;

        for (int index = 0; index < Constants.BOARD_SIZE; index++) {
            Cell cell = getCellAtIndex(board, index);
            cellPanes[index].setStyle(getCellStyle(cell, index));
            if (cellIconViews[index] != null) {
                Image icon = getCellIcon(cell, index);
                cellIconViews[index].setImage(icon);
                cellIconViews[index].setOpacity(getCellIconOpacity(cell));
                double iconSize = getCellIconSize(cell);
                cellIconViews[index].setFitWidth(iconSize);
                cellIconViews[index].setFitHeight(iconSize);
            }
            typeLabels[index].setText(getCellCode(cell, index));
            valueLabels[index].setText(getCellValueText(cell));
            stationedLabels[index].setText(getStationedText(cell));
        }
    }

    public void updateMonsterInfo(Monster player, Monster opponent) {
        this.lastPlayer = player;
        this.lastOpponent = opponent;
        renderMonsterInfoPanels();
        updateEnergyBars(player, opponent);
        updateDistanceBars(player, opponent);

        if (playerPortrait != null) {
            playerPortrait.setImage(chooseAvatarIcon(player));
            playerPortrait.toFront();
        }
        if (opponentPortrait != null) {
            opponentPortrait.setImage(chooseAvatarIcon(opponent));
            opponentPortrait.toFront();
        }
    }

    public void updateTurn(Monster current) {
        if (current == null) return;
        this.activeTurnMonster = current;
        turnLabel.setText("Turn: " + current.getName() + " | " + current.getClass().getSimpleName());
        pulse(turnLabel);
        renderMonsterInfoPanels();
    }

    private void renderMonsterInfoPanels() {
        boolean playerActive = lastPlayer != null && activeTurnMonster == lastPlayer;
        boolean opponentActive = lastOpponent != null && activeTurnMonster == lastOpponent;

        if (playerInfoLabel != null) {
            String title = playerActive ? "▶ PLAYER TURN" : "PLAYER";
            playerInfoLabel.setText(formatMonsterInfo(title, lastPlayer));
            playerInfoLabel.setStyle(GuiStyle.hudPanel("cyan") + GuiStyle.smallInfoText() + confusionBorder(lastPlayer) + turnGlowCss(playerActive, "cyan"));
        }
        if (opponentInfoLabel != null) {
            String title = opponentActive ? "▶ OPPONENT TURN" : "OPPONENT";
            opponentInfoLabel.setText(formatMonsterInfo(title, lastOpponent));
            opponentInfoLabel.setStyle(GuiStyle.hudPanel("red") + GuiStyle.smallInfoText() + confusionBorder(lastOpponent) + turnGlowCss(opponentActive, "red"));
        }
        updatePortraitTurnGlow(playerActive, opponentActive);
    }

    private void updatePortraitTurnGlow(boolean playerActive, boolean opponentActive) {
        if (playerPortrait != null) {
            playerPortrait.setOpacity(playerActive ? 1.0 : 0.84);
            playerPortrait.setScaleX(playerActive ? 1.025 : 1.0);
            playerPortrait.setScaleY(playerActive ? 1.025 : 1.0);
            playerPortrait.setStyle(playerActive
                    ? "-fx-effect: dropshadow(gaussian, rgba(35,247,255,0.98), 24, 0.56, 0, 0);"
                    : "-fx-effect: dropshadow(gaussian, rgba(35,247,255,0.48), 12, 0.28, 0, 0);");
        }
        if (opponentPortrait != null) {
            opponentPortrait.setOpacity(opponentActive ? 1.0 : 0.84);
            opponentPortrait.setScaleX(opponentActive ? 1.025 : 1.0);
            opponentPortrait.setScaleY(opponentActive ? 1.025 : 1.0);
            opponentPortrait.setStyle(opponentActive
                    ? "-fx-effect: dropshadow(gaussian, rgba(255,87,87,0.98), 24, 0.56, 0, 0);"
                    : "-fx-effect: dropshadow(gaussian, rgba(255,70,80,0.48), 12, 0.28, 0, 0);");
        }
    }

    private String turnGlowCss(boolean active, String theme) {
        String border = "cyan".equals(theme) ? "#23f7ff" : "#ff5757";
        if (active) {
            String glowBg = "cyan".equals(theme) ? "rgba(12, 42, 58, 0.98)" : "rgba(55, 14, 22, 0.98)";
            return "-fx-border-color: " + border + ";" +
                   "-fx-border-width: 2.6;" +
                   "-fx-background-color: linear-gradient(to bottom, " + glowBg + ", rgba(4, 9, 22, 0.98));" +
                   "-fx-effect: dropshadow(gaussian, " + border + ", 18, 0.34, 0, 0);";
        }
        return "-fx-border-width: 1.7;" +
               "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.58), 7, 0.18, 0, 1);";
    }

    public void updateDiceRoll(final int roll) {
        if (diceTimeline != null) {
            diceTimeline.stop();
        }

        resetDiceFaceTransform();

        if (roll <= 0) {
            drawDiceFace(0);
            return;
        }

        final int frames = 18;
        diceTimeline = new Timeline();
        for (int i = 0; i < frames; i++) {
            final int frame = i;
            diceTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(i * 38), e -> {
                int face = 1 + (int) (Math.random() * 6);
                drawDiceFace(face);

                // Contained tactile motion: rotate only the small dice face, never the panel/window.
                double damp = 1.0 - (frame / (double) frames);
                if (diceFace != null) {
                    double angle = ((frame % 2 == 0) ? 7 : -7) * damp;
                    diceFace.setRotate(angle);
                    double scale = 1.0 + (0.035 * damp);
                    diceFace.setScaleX(scale);
                    diceFace.setScaleY(scale);
                    diceFace.setOpacity(0.88 + (0.12 * (1.0 - damp)));
                }
            }));
        }

        diceTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(frames * 38 + 90), e -> {
            drawDiceFace(roll);
            resetDiceFaceTransform();
            pulse(diceFace);
            flashDiceResult();
        }));

        diceTimeline.play();
    }

    private void resetDiceFaceTransform() {
        if (diceFace == null) return;
        diceFace.setRotate(0);
        diceFace.setScaleX(1.0);
        diceFace.setScaleY(1.0);
        diceFace.setTranslateX(0);
        diceFace.setTranslateY(0);
        diceFace.setOpacity(1.0);
    }

    private void flashDiceResult() {
        if (dicePane == null) return;

        final StackPane flash = new StackPane();
        flash.setMouseTransparent(true);
        flash.setPrefWidth(115);
        flash.setPrefHeight(46);
        flash.setStyle("-fx-background-color: rgba(35,247,255,0.20);" +
                       "-fx-border-color: rgba(35,247,255,0.90);" +
                       "-fx-border-width: 2;" +
                       "-fx-background-radius: 12;" +
                       "-fx-border-radius: 12;" +
                       "-fx-effect: dropshadow(gaussian, rgba(35,247,255,0.75), 18, 0.52, 0, 0);");
        dicePane.getChildren().add(0, flash);

        FadeTransition fade = new FadeTransition(Duration.millis(360), flash);
        fade.setFromValue(0.85);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> dicePane.getChildren().remove(flash));
        fade.play();
    }

    private void positionDicePips() {
        if (dicePips == null || dicePips.length < 7) return;
        // 0 top-left, 1 middle-left, 2 bottom-left, 3 center,
        // 4 top-right, 5 middle-right, 6 bottom-right.
        setPipPosition(0, 9, 9);
        setPipPosition(1, 9, 17);
        setPipPosition(2, 9, 25);
        setPipPosition(3, 17, 17);
        setPipPosition(4, 25, 9);
        setPipPosition(5, 25, 17);
        setPipPosition(6, 25, 25);
    }

    private void setPipPosition(int index, double x, double y) {
        if (dicePips == null || index < 0 || index >= dicePips.length || dicePips[index] == null) return;
        dicePips[index].setCenterX(x);
        dicePips[index].setCenterY(y);
    }

    private void drawDiceFace(int face) {
        if (diceLabel != null) diceLabel.setText(face <= 0 ? "-" : String.valueOf(face));
        if (diceCanvas == null) return;

        double w = diceCanvas.getWidth();
        double h = diceCanvas.getHeight();
        GraphicsContext gc = diceCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);

        // Clean 2D dice face, drawn on Canvas so pips cannot shift or distort in layout.
        gc.setFill(Color.web("#f8fdff"));
        gc.fillRoundRect(2, 2, w - 4, h - 4, 8, 8);
        gc.setStroke(Color.web("#23f7ff"));
        gc.setLineWidth(2.0);
        gc.strokeRoundRect(2, 2, w - 4, h - 4, 8, 8);

        if (face <= 0) {
            gc.setStroke(Color.web("rgba(35,247,255,0.55)"));
            gc.setLineWidth(1.2);
            gc.strokeLine(10, h / 2.0, w - 10, h / 2.0);
            return;
        }

        gc.setFill(Color.web("#102033"));
        double left = 10.0;
        double mid = w / 2.0;
        double right = w - 10.0;
        double top = 10.0;
        double center = h / 2.0;
        double bottom = h - 10.0;

        if (face == 1) {
            drawPip(gc, mid, center);
        } else if (face == 2) {
            drawPip(gc, left, top);
            drawPip(gc, right, bottom);
        } else if (face == 3) {
            drawPip(gc, left, top);
            drawPip(gc, mid, center);
            drawPip(gc, right, bottom);
        } else if (face == 4) {
            drawPip(gc, left, top);
            drawPip(gc, right, top);
            drawPip(gc, left, bottom);
            drawPip(gc, right, bottom);
        } else if (face == 5) {
            drawPip(gc, left, top);
            drawPip(gc, right, top);
            drawPip(gc, mid, center);
            drawPip(gc, left, bottom);
            drawPip(gc, right, bottom);
        } else {
            drawPip(gc, left, top);
            drawPip(gc, right, top);
            drawPip(gc, left, center);
            drawPip(gc, right, center);
            drawPip(gc, left, bottom);
            drawPip(gc, right, bottom);
        }
    }

    private void drawPip(GraphicsContext gc, double x, double y) {
        double r = 3.2;
        gc.fillOval(x - r, y - r, r * 2, r * 2);
    }

    private void showPips(int... indexes) {
        if (indexes == null || dicePips == null) return;
        for (int i = 0; i < indexes.length; i++) {
            int index = indexes[i];
            if (index >= 0 && index < dicePips.length && dicePips[index] != null) {
                dicePips[index].setVisible(true);
            }
        }
    }

    public void updateCardDrawn(Card card) {
        if (card == null) {
            cardLabel.setText("Card Drawn: -");
        } else {
            cardLabel.setText("Card: " + card.getName() + "\n" + card.getDescription());
            pulse(cardLabel);
        }
    }

    public void updateCardDrawn(String text) {
        cardLabel.setText("Card Drawn: " + text);
        pulse(cardLabel);
    }

    public void updateDeckCount(int count) {
        if (count < 0) {
            deckLabel.setText("Deck: -");
        } else {
            deckLabel.setText("Deck: " + count);
        }
    }

    public void setAction(String message) {
        actionLabel.setText("ACTION: " + message);
        pulse(actionLabel);
    }

    public void addLog(String message) {
        logArea.appendText(message + "\n");
        logArea.setScrollTop(Double.MAX_VALUE);
    }

    public void updateMonsterPositions(Monster player, Monster opponent) {
        clearDynamicMarkers();
        placeActiveTurnCellGlow();

        if (player != null && opponent != null && player.getPosition() == opponent.getPosition()) {
            placeMonsterMarker(player.getPosition(), player, "P", "#23f7ff", -8);
            placeMonsterMarker(opponent.getPosition(), opponent, "O", "#ff5757", 8);
            highlightCell(player.getPosition());
            return;
        }

        if (player != null) {
            placeMonsterMarker(player.getPosition(), player, "P", "#23f7ff", 0);
            highlightCell(player.getPosition());
        }
        if (opponent != null) {
            placeMonsterMarker(opponent.getPosition(), opponent, "O", "#ff5757", 0);
            highlightCell(opponent.getPosition());
        }
    }

    private void clearDynamicMarkers() {
        for (StackPane cell : cellPanes) {
            if (cell == null) continue;
            cell.getChildren().removeIf(n -> n.getId() != null && n.getId().startsWith("dynamic-"));
        }
    }

    private void placeActiveTurnCellGlow() {
        if (activeTurnMonster == null) return;
        int position = activeTurnMonster.getPosition();
        if (position < 0 || position >= Constants.BOARD_SIZE) return;
        StackPane cell = cellPanes[position];
        if (cell == null) return;

        String accent = activeTurnMonster == lastOpponent ? "#ff57e7" : "#23f7ff";
        Circle activeGlow = new Circle((CELL_SIZE / 2.0) - 1);
        activeGlow.setId("dynamic-active-glow");
        activeGlow.setMouseTransparent(true);
        activeGlow.setFill(Color.web("rgba(255,255,255,0.06)"));
        activeGlow.setStroke(Color.web(accent));
        activeGlow.setStrokeWidth(3.0);
        activeGlow.setStyle("-fx-effect: dropshadow(gaussian, " + accent + ", 18, 0.62, 0, 0);");
        StackPane.setAlignment(activeGlow, Pos.CENTER);

        // Insert behind monster markers, but above the normal cell background.
        cell.getChildren().add(0, activeGlow);
    }

    private void placeMonsterMarker(int position, Monster monster, String tag, String accent, int xOffset) {
        if (position < 0 || position >= Constants.BOARD_SIZE) return;
        StackPane cell = cellPanes[position];
        if (cell == null) return;

        StackPane marker = new StackPane();
        marker.setId("dynamic-marker-" + tag);
        marker.setPrefSize(44, 44);
        marker.setTranslateX(xOffset);

        Circle glow = new Circle(19);
        glow.setFill(Color.web("rgba(0,0,0,0.45)"));
        glow.setStroke(Color.web(accent));
        glow.setStrokeWidth(2.0);

        Image icon = chooseMonsterIcon(monster);
        if (icon != null) {
            ImageView monsterView = new ImageView(icon);
            monsterView.setFitWidth(43);
            monsterView.setFitHeight(43);
            monsterView.setPreserveRatio(true);
            monsterView.setSmooth(true);
            monsterView.setCache(true);
            marker.getChildren().addAll(glow, monsterView);
        } else {
            Circle fallback = new Circle(14);
            fallback.setFill(Color.web(accent));
            fallback.setStroke(Color.WHITE);
            fallback.setStrokeWidth(1.5);

            Label fallbackLabel = new Label(tag);
            fallbackLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: white;");
            marker.getChildren().addAll(glow, fallback, fallbackLabel);
        }

        Label tagLabel = new Label(tag);
        tagLabel.setStyle("-fx-font-size: 8px;" +
                          "-fx-font-weight: bold;" +
                          "-fx-text-fill: white;" +
                          "-fx-background-color: rgba(0,0,0,0.80);" +
                          "-fx-background-radius: 8;" +
                          "-fx-padding: 1 4 1 4;");
        StackPane.setAlignment(tagLabel, Pos.TOP_RIGHT);
        marker.getChildren().add(tagLabel);

        addStatusMiniIcons(marker, monster);

        StackPane.setAlignment(marker, Pos.CENTER);
        cell.getChildren().add(marker);
        pulse(marker);
    }


    private Image chooseAvatarIcon(Monster monster) {
        if (monster == null) return null;
        if (monster.getOriginalRole() == Role.SCARER && scarerAvatar != null) return scarerAvatar;
        if (monster.getOriginalRole() == Role.LAUGHER && laugherAvatar != null) return laugherAvatar;
        return chooseMonsterIcon(monster);
    }

    private Image chooseMonsterIcon(Monster monster) {
        if (monster == null) return null;
        if (monster.isFrozen() && frozenIcon != null) return frozenIcon;
        if (monster.isShielded() && armoredIcon != null) return armoredIcon;
        if (monster.getOriginalRole() == Role.SCARER) return scarerIcon;
        return laugherIcon;
    }

    private void addStatusMiniIcons(StackPane marker, Monster monster) {
        if (monster == null) return;

        int x = -14;
        if (monster.isShielded() && shieldIcon != null) {
            addMiniIcon(marker, shieldIcon, x);
            x += 14;
        }
        if (monster.isFrozen() && frozenIcon != null) {
            addMiniIcon(marker, frozenIcon, x);
            x += 14;
        }
        if (monster.getConfusionTurns() > 0 && confusionIcon != null) {
            addMiniIcon(marker, confusionIcon, x);
        }
    }

    private void addMiniIcon(StackPane marker, Image image, int translateX) {
        ImageView mini = new ImageView(image);
        mini.setFitWidth(11);
        mini.setFitHeight(11);
        mini.setPreserveRatio(true);
        mini.setSmooth(true);
        mini.setTranslateX(translateX);
        mini.setTranslateY(15);
        marker.getChildren().add(mini);
    }


    public void showActionBanner(String title, String details, String accentColor) {
        if (title == null) title = "EVENT";
        if (details == null) details = "";
        if (accentColor == null || accentColor.length() == 0) accentColor = "#23f7ff";

        final StackPane banner = new StackPane();
        banner.setId("dynamic-event-banner");
        banner.setMouseTransparent(true);
        // Medium-size factory alert: visible, but it does not cover the board for too long.
        banner.setLayoutX(350);
        banner.setLayoutY(58);
        banner.setPrefWidth(580);
        banner.setPrefHeight(76);
        banner.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(8, 18, 33, 0.97), rgba(2, 5, 15, 0.94));" +
                        "-fx-border-color: " + accentColor + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 16;" +
                        "-fx-background-radius: 16;" +
                        "-fx-padding: 9 14 9 14;" +
                        "-fx-effect: dropshadow(gaussian, " + accentColor + ", 18, 0.36, 0, 0);");

        Label tagLabel = new Label("FACTORY ALERT");
        tagLabel.setStyle("-fx-font-size: 8px;" +
                          "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
                          "-fx-font-weight: bold;" +
                          "-fx-text-fill: " + accentColor + ";" +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 3, 0.75, 0, 1);");

        Label titleLabel = new Label(title);
        titleLabel.setAlignment(Pos.CENTER_LEFT);
        titleLabel.setStyle("-fx-font-size: 14px;" +
                            "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: white;" +
                            "-fx-effect: dropshadow(gaussian, " + accentColor + ", 9, 0.42, 0, 0);");

        Label bodyLabel = new Label(details);
        bodyLabel.setWrapText(true);
        bodyLabel.setMaxWidth(540);
        bodyLabel.setStyle("-fx-font-size: 10px;" +
                           "-fx-font-family: 'Trebuchet MS', 'Impact', 'Arial Black', 'Arial';" +
                           "-fx-font-weight: bold;" +
                           "-fx-text-fill: #eaffff;");

        VBox box = new VBox(2);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMaxWidth(540);
        box.getChildren().addAll(tagLabel, titleLabel, bodyLabel);
        banner.getChildren().add(box);

        if (overlayLayer == null) createOverlayLayer();
        overlayLayer.getChildren().removeIf(n -> "dynamic-event-banner".equals(n.getId()));
        overlayLayer.getChildren().add(banner);
        overlayLayer.toFront();
        banner.toFront();
        keepControlsOnTop();

        banner.setOpacity(0.0);
        banner.setTranslateY(-86);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(150), banner);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(210), banner);
        slideIn.setFromY(-86);
        slideIn.setToY(0);

        fadeIn.play();
        slideIn.play();

        PauseTransition wait = new PauseTransition(Duration.millis(2900));
        wait.setOnFinished(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(220), banner);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            TranslateTransition slideOut = new TranslateTransition(Duration.millis(220), banner);
            slideOut.setFromY(0);
            slideOut.setToY(-38);

            fadeOut.setOnFinished(done -> overlayLayer.getChildren().remove(banner));
            fadeOut.play();
            slideOut.play();
        });
        wait.play();
    }

    public void showCardEffect(Card card, String resultText) {
        if (card == null) return;
        String body = card.getDescription();
        if (resultText != null && resultText.length() > 0) {
            body += "\n" + resultText;
        }
        showCardReveal(card, body);
    }

    private void showCardReveal(Card card, String bodyText) {
        if (card == null) return;
        if (overlayLayer == null) createOverlayLayer();

        final String accent = cardAccentColor(card);
        overlayLayer.getChildren().removeIf(n -> "dynamic-card-reveal".equals(n.getId()));

        final Pane reveal = new Pane();
        reveal.setId("dynamic-card-reveal");
        reveal.setMouseTransparent(true);
        // Slight right-side reveal: keeps the card near the log without covering too much of the board.
        reveal.setLayoutX(884);
        reveal.setLayoutY(382);
        reveal.setPrefWidth(178);
        reveal.setPrefHeight(188);

        StackPane deckBack1 = createMiniCardBack(accent, 18, 22, -7);
        StackPane deckBack2 = createMiniCardBack(accent, 24, 17, -3);
        StackPane deckBack3 = createMiniCardBack(accent, 30, 12, 0);

        StackPane drawnCard = createDrawnCard(card, bodyText, accent);
        drawnCard.setLayoutX(6);
        drawnCard.setLayoutY(5);
        drawnCard.setOpacity(0.0);
        drawnCard.setTranslateX(56);
        drawnCard.setScaleX(0.92);
        drawnCard.setScaleY(0.92);

        reveal.getChildren().addAll(deckBack1, deckBack2, deckBack3, drawnCard);
        overlayLayer.getChildren().add(reveal);
        overlayLayer.toFront();
        reveal.toFront();
        keepControlsOnTop();

        FadeTransition fade = new FadeTransition(Duration.millis(170), drawnCard);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        TranslateTransition slide = new TranslateTransition(Duration.millis(310), drawnCard);
        slide.setFromX(56);
        slide.setToX(0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(310), drawnCard);
        scale.setFromX(0.92);
        scale.setFromY(0.92);
        scale.setToX(1.0);
        scale.setToY(1.0);

        fade.play();
        slide.play();
        scale.play();

        showActionBanner("CARD DRAWN: " + card.getName(), firstLine(bodyText), accent);

        PauseTransition hold = new PauseTransition(Duration.millis(4550));
        hold.setOnFinished(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(260), reveal);
            fadeOut.setFromValue(reveal.getOpacity());
            fadeOut.setToValue(0.0);
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(260), reveal);
            slideOut.setFromX(0);
            slideOut.setToX(32);
            fadeOut.setOnFinished(done -> overlayLayer.getChildren().remove(reveal));
            fadeOut.play();
            slideOut.play();
        });
        hold.play();
    }

    private StackPane createMiniCardBack(String accent, double x, double y, double rotate) {
        StackPane back = new StackPane();
        back.setLayoutX(x);
        back.setLayoutY(y);
        back.setPrefWidth(106);
        back.setPrefHeight(148);
        back.setRotate(rotate);
        back.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(7, 18, 32, 0.98), rgba(2, 6, 16, 0.98));" +
                      "-fx-border-color: " + accent + ";" +
                      "-fx-border-width: 2;" +
                      "-fx-background-radius: 13;" +
                      "-fx-border-radius: 13;" +
                      "-fx-effect: dropshadow(gaussian, " + accent + ", 13, 0.28, 0, 0);");
        Label mark = new Label("DOOR\nDASH");
        mark.setAlignment(Pos.CENTER);
        mark.setStyle("-fx-font-size: 12px;" +
                      "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
                      "-fx-font-weight: bold;" +
                      "-fx-text-fill: rgba(255,255,255,0.72);" +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.85), 4, 0.75, 0, 1);");
        back.getChildren().add(mark);
        return back;
    }

    private StackPane createDrawnCard(Card card, String bodyText, String accent) {
        StackPane cardNode = new StackPane();
        cardNode.setPrefWidth(158);
        cardNode.setPrefHeight(184);
        cardNode.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(13,31,48,0.98), rgba(236,253,255,0.98) 18%, rgba(216,244,249,0.98) 74%, rgba(11,26,42,0.96));" +
                          "-fx-border-color: " + accent + ";" +
                          "-fx-border-width: 2.5;" +
                          "-fx-background-radius: 15;" +
                          "-fx-border-radius: 15;" +
                          "-fx-effect: dropshadow(gaussian, " + accent + ", 16, 0.34, 0, 0);");

        Pane art = new Pane();
        art.setMouseTransparent(true);
        art.setPrefSize(158, 184);

        Rectangle header = new Rectangle(10, 9, 138, 27);
        header.setArcWidth(12);
        header.setArcHeight(12);
        header.setFill(Color.web(accent));
        header.setOpacity(0.94);

        Rectangle titlePlate = new Rectangle(18, 43, 122, 28);
        titlePlate.setArcWidth(10);
        titlePlate.setArcHeight(10);
        titlePlate.setFill(Color.web("rgba(255,255,255,0.88)"));
        titlePlate.setStroke(Color.web("rgba(16,32,51,0.28)"));
        titlePlate.setStrokeWidth(1.1);

        Rectangle bodyPanel = new Rectangle(14, 76, 130, 62);
        bodyPanel.setArcWidth(12);
        bodyPanel.setArcHeight(12);
        bodyPanel.setFill(Color.web("rgba(8, 21, 35, 0.10)"));
        bodyPanel.setStroke(Color.web("rgba(16,32,51,0.24)"));
        bodyPanel.setStrokeWidth(1.1);

        Rectangle footer = new Rectangle(10, 148, 138, 24);
        footer.setArcWidth(12);
        footer.setArcHeight(12);
        footer.setFill(Color.web("rgba(12, 26, 42, 0.92)"));
        footer.setStroke(Color.web(accent));
        footer.setStrokeWidth(1.0);

        Circle screwLeft = new Circle(20, 22, 2.8, Color.web("rgba(255,255,255,0.72)"));
        Circle screwRight = new Circle(138, 22, 2.8, Color.web("rgba(255,255,255,0.72)"));

        Circle powerOrb = new Circle(79, 58, 14.0, Color.web("rgba(255,255,255,0.92)"));
        powerOrb.setStroke(Color.web(accent));
        powerOrb.setStrokeWidth(2.0);
        powerOrb.setStyle("-fx-effect: dropshadow(gaussian, " + accent + ", 10, 0.40, 0, 0);");

        Label symbol = new Label(cardSymbol(card));
        symbol.setLayoutX(65);
        symbol.setLayoutY(44);
        symbol.setPrefWidth(28);
        symbol.setPrefHeight(28);
        symbol.setAlignment(Pos.CENTER);
        symbol.setMouseTransparent(true);
        symbol.setStyle("-fx-font-size: 15px;" +
                        "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #102033;");

        Label headerText = new Label(card.getClass().getSimpleName().replace("Card", " CARD").toUpperCase());
        headerText.setLayoutX(24);
        headerText.setLayoutY(15);
        headerText.setPrefWidth(110);
        headerText.setPrefHeight(14);
        headerText.setAlignment(Pos.CENTER);
        headerText.setMouseTransparent(true);
        headerText.setStyle("-fx-font-size: 8px;" +
                            "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #102033;" +
                            "-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.65), 2, 0.5, 0, 0);");

        Label title = new Label(shortText(card.getName(), 22));
        title.setLayoutX(20);
        title.setLayoutY(86);
        title.setPrefWidth(118);
        title.setPrefHeight(17);
        title.setAlignment(Pos.CENTER);
        title.setMouseTransparent(true);
        title.setStyle("-fx-font-size: 10px;" +
                       "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
                       "-fx-font-weight: bold;" +
                       "-fx-text-fill: #102033;");

        Label body = new Label(shortText(bodyText, 96));
        body.setWrapText(true);
        body.setLayoutX(21);
        body.setLayoutY(105);
        body.setPrefWidth(116);
        body.setPrefHeight(34);
        body.setAlignment(Pos.TOP_CENTER);
        body.setMouseTransparent(true);
        body.setStyle("-fx-font-size: 7.5px;" +
                      "-fx-font-family: 'Trebuchet MS', 'Impact', 'Arial Black', 'Arial';" +
                      "-fx-font-weight: bold;" +
                      "-fx-text-fill: #183149;");

        Label rarity = new Label(rarityText(card));
        rarity.setLayoutX(15);
        rarity.setLayoutY(151);
        rarity.setPrefWidth(128);
        rarity.setPrefHeight(17);
        rarity.setAlignment(Pos.CENTER);
        rarity.setMouseTransparent(true);
        rarity.setStyle("-fx-font-size: 7.8px;" +
                        "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #ffffff;");

        art.getChildren().addAll(header, titlePlate, bodyPanel, footer, screwLeft, screwRight, powerOrb,
                symbol, headerText, title, body, rarity);
        cardNode.getChildren().add(art);
        return cardNode;
    }

    private String cardSymbol(Card card) {
        if (card == null) return "?";
        String type = card.getClass().getSimpleName();
        if ("ShieldCard".equals(type)) return "SH";
        if ("ConfusionCard".equals(type)) return "?!";
        if ("EnergyStealCard".equals(type)) return "EN";
        if ("SwapperCard".equals(type)) return "SW";
        if ("StartOverCard".equals(type)) return "0";
        return "DD";
    }

    private String rarityText(Card card) {
        if (card == null) return "MONSTER FILE";
        String stars = "";
        for (int i = 0; i < Math.max(1, card.getRarity()); i++) {
            stars += "★";
        }
        return (card.isLucky() ? "LUCKY " : "UNLUCKY ") + "• RARITY " + stars;
    }

    private String cardAccentColor(Card card) {
        if (card == null) return "#fff331";
        String type = card.getClass().getSimpleName();
        if ("ShieldCard".equals(type)) return "#23f7ff";
        if ("ConfusionCard".equals(type)) return "#d25cff";
        if ("EnergyStealCard".equals(type)) return "#ff6b3b";
        if ("SwapperCard".equals(type)) return "#fff331";
        if ("StartOverCard".equals(type)) return "#74e8ff";
        return "#fff331";
    }

    private String firstLine(String text) {
        if (text == null) return "";
        int newline = text.indexOf('\n');
        String line = newline >= 0 ? text.substring(0, newline) : text;
        return shortText(line, 92);
    }

    private String shortText(String text, int max) {
        if (text == null) return "";
        String clean = text.replace('\n', ' ').replaceAll("\\s+", " ").trim();
        if (clean.length() <= max) return clean;
        if (max <= 3) return clean.substring(0, max);
        return clean.substring(0, max - 3) + "...";
    }

    public void showFloatingText(int position, String text, boolean positive) {
        showFloatingText(position, text, positive ? "#7dff7d" : "#ff6b6b");
    }

    public void showFloatingText(int position, String text, String color) {
        if (position < 0 || position >= Constants.BOARD_SIZE || text == null) return;
        StackPane cell = cellPanes[position];
        if (cell == null) return;

        Label floatLabel = new Label(text);
        floatLabel.setId("floating-text");
        floatLabel.setMouseTransparent(true);
        floatLabel.setStyle("-fx-font-size: 12px;" +
                            "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: " + color + ";" +
                            "-fx-background-color: rgba(0,0,0,0.70);" +
                            "-fx-background-radius: 10;" +
                            "-fx-padding: 1 5 1 5;" +
                            "-fx-effect: dropshadow(gaussian, " + color + ", 12, 0.60, 0, 0);");
        StackPane.setAlignment(floatLabel, Pos.CENTER);
        cell.getChildren().add(floatLabel);

        TranslateTransition tt = new TranslateTransition(Duration.millis(700), floatLabel);
        tt.setFromY(0);
        tt.setToY(-26);

        FadeTransition ft = new FadeTransition(Duration.millis(700), floatLabel);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);

        tt.setOnFinished(e -> cell.getChildren().remove(floatLabel));
        tt.play();
        ft.play();
    }

    public void highlightCell(int position) {
        if (position < 0 || position >= Constants.BOARD_SIZE) return;
        StackPane cell = cellPanes[position];
        if (cell == null) return;

        final Circle landingGlow = new Circle((CELL_SIZE / 2.0) - 2);
        landingGlow.setId("dynamic-landing-glow");
        landingGlow.setMouseTransparent(true);
        landingGlow.setFill(Color.web("rgba(255,255,255,0.08)"));
        landingGlow.setStroke(Color.web("#fff331"));
        landingGlow.setStrokeWidth(2.5);
        landingGlow.setStyle("-fx-effect: dropshadow(gaussian, #fff331, 17, 0.55, 0, 0);");
        StackPane.setAlignment(landingGlow, Pos.CENTER);
        cell.getChildren().add(landingGlow);

        FadeTransition ft = new FadeTransition(Duration.millis(560), landingGlow);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setOnFinished(e -> cell.getChildren().remove(landingGlow));
        ft.play();
    }

    public void animateMonsterMovement(Monster monster, int fromPosition, int toPosition, boolean playerMonster) {
        if (monster == null) return;
        if (fromPosition < 0 || fromPosition >= Constants.BOARD_SIZE) return;
        if (toPosition < 0 || toPosition >= Constants.BOARD_SIZE) return;
        if (fromPosition == toPosition) return;
        if (overlayLayer == null) createOverlayLayer();

        double[] start = cellCenterOnBoard(fromPosition);
        double[] end = cellCenterOnBoard(toPosition);
        String accent = playerMonster ? "#23f7ff" : "#ff5757";

        StackPane ghost = createMovingMonsterGhost(monster, accent, playerMonster ? "P" : "O");
        ghost.setId("dynamic-moving-monster");
        ghost.setMouseTransparent(true);
        ghost.setLayoutX(start[0] - 22);
        ghost.setLayoutY(start[1] - 22);
        ghost.setOpacity(0.92);

        overlayLayer.getChildren().add(ghost);
        overlayLayer.toFront();
        ghost.toFront();
        keepControlsOnTop();

        TranslateTransition move = new TranslateTransition(Duration.millis(520), ghost);
        move.setFromX(0);
        move.setFromY(0);
        move.setToX(end[0] - start[0]);
        move.setToY(end[1] - start[1]);

        FadeTransition fade = new FadeTransition(Duration.millis(520), ghost);
        fade.setFromValue(0.96);
        fade.setToValue(0.0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(520), ghost);
        scale.setFromX(1.08);
        scale.setFromY(1.08);
        scale.setToX(0.96);
        scale.setToY(0.96);

        move.setOnFinished(e -> {
            overlayLayer.getChildren().remove(ghost);
            highlightCell(toPosition);
        });
        move.play();
        fade.play();
        scale.play();
    }

    private StackPane createMovingMonsterGhost(Monster monster, String accent, String tag) {
        StackPane marker = new StackPane();
        marker.setPrefSize(44, 44);
        marker.setStyle("-fx-effect: dropshadow(gaussian, " + accent + ", 16, 0.45, 0, 0);");

        Circle glow = new Circle(20);
        glow.setFill(Color.web("rgba(0,0,0,0.42)"));
        glow.setStroke(Color.web(accent));
        glow.setStrokeWidth(2.2);

        Image icon = chooseMonsterIcon(monster);
        if (icon != null) {
            ImageView monsterView = new ImageView(icon);
            monsterView.setFitWidth(42);
            monsterView.setFitHeight(42);
            monsterView.setPreserveRatio(true);
            monsterView.setSmooth(true);
            marker.getChildren().addAll(glow, monsterView);
        } else {
            Circle fallback = new Circle(14);
            fallback.setFill(Color.web(accent));
            fallback.setStroke(Color.WHITE);
            fallback.setStrokeWidth(1.4);
            Label fallbackLabel = new Label(tag);
            fallbackLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: white;");
            marker.getChildren().addAll(glow, fallback, fallbackLabel);
        }
        return marker;
    }

    private double[] cellCenterOnBoard(int index) {
        int[] rc = indexToRowCol(index);
        int row = rc[0];
        int col = rc[1];
        int visualRow = Constants.BOARD_ROWS - 1 - row;
        double x = BOARD_X + col * (CELL_SIZE + CELL_GAP) + CELL_SIZE / 2.0;
        double y = BOARD_Y + visualRow * (CELL_SIZE + CELL_GAP) + CELL_SIZE / 2.0;
        return new double[] { x, y };
    }

    private void pulse(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(170), node);
        ft.setFromValue(0.55);
        ft.setToValue(1.0);
        ft.setCycleCount(1);
        ft.play();
    }

    private void styleActionButton(Button button, String theme) {
        String normal = GuiStyle.glowButton(theme, false);
        String hover = GuiStyle.glowButton(theme, true);
        button.setStyle(normal);
        GuiStyle.applyGlowHover(button, normal, hover);
    }

    private String formatMonsterInfo(String title, Monster monster) {
        if (monster == null) return title + "\n-";

        String confused = monster.getRole() != monster.getOriginalRole() ? "  CONFUSED" : "";

        return title + "\n" +
               "Name: " + monster.getName() + "\n" +
               "Type: " + monster.getClass().getSimpleName() + "\n" +
               "Role: " + monster.getRole() + confused + "\n" +
               "Cell: " + monster.getPosition() + " / 99\n" +
               "Shield: " + statusWord(monster.isShielded()) + "\n" +
               "Frozen: " + statusWord(monster.isFrozen()) + "\n" +
               "Confusion: " + monster.getConfusionTurns() + "\n" +
               extraStatus(monster);
    }

    private String extraStatus(Monster monster) {
        String extra = "";
        Integer momentum = callIntGetter(monster, "getMomentumTurns");
        Integer normalSpeed = callIntGetter(monster, "getNormalSpeedTurns");

        if (momentum != null) {
            extra += "Momentum: " + momentum + "\n";
        }
        if (normalSpeed != null) {
            extra += "Focus: " + normalSpeed + "\n";
        }
        return extra.length() == 0 ? "Power: ready/check energy" : extra;
    }

    private Integer callIntGetter(Monster monster, String methodName) {
        try {
            Object value = monster.getClass().getMethod(methodName).invoke(monster);
            if (value instanceof Integer) {
                return (Integer) value;
            }
        } catch (Exception ignored) { }
        return null;
    }

    private String yesNo(boolean value) {
        return value ? "ACTIVE" : "OFF";
    }

    private String statusWord(boolean value) {
        return value ? "ACTIVE" : "OFF";
    }

    private String confusionBorder(Monster monster) {
        if (monster != null && monster.getRole() != monster.getOriginalRole()) {
            return "-fx-border-color: #ff3bff; -fx-border-width: 3;";
        }
        return "";
    }

    private Cell getCellAtIndex(Board board, int index) {
        try {
            Cell[][] cells = board.getBoardCells();
            int[] rc = indexToRowCol(index);
            return cells[rc[0]][rc[1]];
        } catch (Exception e) {
            return null;
        }
    }

    private int[] indexToRowCol(int index) {
        int row = index / Constants.BOARD_COLS;
        int colInsideRow = index % Constants.BOARD_COLS;
        int col = (row % 2 == 0) ? colInsideRow : (Constants.BOARD_COLS - 1 - colInsideRow);
        return new int[] { row, col };
    }

    private int getBoardIndex(int boardRow, int visualCol) {
        if (boardRow % 2 == 0) {
            return boardRow * Constants.BOARD_COLS + visualCol;
        }
        return boardRow * Constants.BOARD_COLS + (Constants.BOARD_COLS - 1 - visualCol);
    }

    private String getCellCode(Cell cell, int index) {
        return "";
    }

    private String getCellValueText(Cell cell) {
        if (cell instanceof DoorCell) {
            DoorCell door = (DoorCell) cell;
            if (door.isActivated()) return "USED";
            return String.valueOf(door.getEnergy());
        }
        if (cell instanceof TransportCell) {
            int effect = ((TransportCell) cell).getEffect();
            return effect > 0 ? "+" + effect : String.valueOf(effect);
        }
        return "";
    }

    private String getStationedText(Cell cell) {
        if (cell instanceof MonsterCell) {
            Monster stationed = ((MonsterCell) cell).getCellMonster();
            if (stationed == null) return "M";
            return initials(stationed.getName());
        }
        return "";
    }

    private String initials(String name) {
        if (name == null || name.trim().length() == 0) return "M";
        String[] parts = name.trim().split("\\s+");
        String result = "";
        for (int i = 0; i < parts.length && result.length() < 3; i++) {
            if (parts[i].length() > 0) {
                result += parts[i].substring(0, 1).toUpperCase();
            }
        }
        return result;
    }

    private Image getCellIcon(Cell cell, int index) {
        if (cell instanceof MonsterCell) return monsterCellIcon;
        if (cell instanceof ConveyorBelt) return conveyorIcon;
        if (cell instanceof ContaminationSock) return sockIcon;
        if (cell instanceof CardCell) return cardCellIcon;
        if (cell instanceof DoorCell) {
            DoorCell door = (DoorCell) cell;
            if (door.isActivated()) return usedDoorIcon;
            return door.getRole() == Role.SCARER ? scarerDoorIcon : laugherDoorIcon;
        }
        return null;
    }

    private double getCellIconOpacity(Cell cell) {
        if (cell == null) return 0.0;
        if (cell instanceof DoorCell) {
            DoorCell door = (DoorCell) cell;
            return door.isActivated() ? 0.42 : 0.78;
        }
        if (cell instanceof MonsterCell) return 0.82;
        if (cell instanceof ConveyorBelt) return 0.86;
        if (cell instanceof ContaminationSock) return 0.86;
        if (cell instanceof CardCell) return 0.84;
        return 0.0;
    }

    private double getCellIconSize(Cell cell) {
        if (cell instanceof DoorCell) return 23;
        if (cell instanceof MonsterCell) return 23;
        if (cell instanceof ConveyorBelt) return 24;
        if (cell instanceof ContaminationSock) return 24;
        if (cell instanceof CardCell) return 23;
        return 0;
    }

    private String getCellStyle(Cell cell, int index) {
        if (cell instanceof MonsterCell) {
            return cellStyle("rgba(35, 13, 52, 0.88)", "#c65cff", 2);
        }
        if (cell instanceof ConveyorBelt) {
            return cellStyle("rgba(0, 43, 68, 0.88)", "#35dfff", 2);
        }
        if (cell instanceof ContaminationSock) {
            return cellStyle("rgba(78, 36, 5, 0.90)", "#ff982c", 2);
        }
        if (cell instanceof CardCell) {
            return cellStyle("rgba(78, 64, 6, 0.90)", "#ffe24a", 2);
        }
        if (cell instanceof DoorCell) {
            DoorCell door = (DoorCell) cell;
            if (door.isActivated()) {
                return cellStyle("rgba(35, 39, 43, 0.82)", "#7b858c", 1);
            }
            if (door.getRole() == Role.SCARER) {
                return cellStyle("rgba(42, 16, 58, 0.88)", "#d062ff", 2);
            }
            return cellStyle("rgba(20, 66, 28, 0.88)", "#69ff5a", 2);
        }
        return getFallbackStyle(index);
    }

    private String getFallbackStyle(int index) {
        if (contains(Constants.MONSTER_CELL_INDICES, index)) return cellStyle("rgba(35, 13, 52, 0.88)", "#c65cff", 2);
        if (contains(Constants.CONVEYOR_CELL_INDICES, index)) return cellStyle("rgba(0, 43, 68, 0.88)", "#35dfff", 2);
        if (contains(Constants.SOCK_CELL_INDICES, index)) return cellStyle("rgba(78, 36, 5, 0.90)", "#ff982c", 2);
        if (contains(Constants.CARD_CELL_INDICES, index)) return cellStyle("rgba(78, 64, 6, 0.90)", "#ffe24a", 2);
        if (index % 2 == 1) return cellStyle("rgba(20, 66, 28, 0.88)", "#69ff5a", 2);
        return cellStyle("rgba(13, 20, 34, 0.88)", "#5c6780", 1);
    }

    private boolean contains(int[] values, int target) {
        if (values == null) return false;
        for (int i = 0; i < values.length; i++) {
            if (values[i] == target) return true;
        }
        return false;
    }

    private String cellStyle(String background, String border, int borderWidth) {
        return "-fx-background-color: linear-gradient(to bottom, rgba(255,255,255,0.10), " + background + ");" +
               "-fx-border-color: " + border + ";" +
               "-fx-border-width: " + borderWidth + ";" +
               "-fx-background-radius: 6;" +
               "-fx-border-radius: 6;";
    }

    private int safeDeckCount() {
        try {
            return Board.getCards() == null ? -1 : Board.getCards().size();
        } catch (Exception e) {
            return -1;
        }
    }

    public Pane getRoot() { return root; }
    public Button getRollDiceButton() { return rollDiceButton; }
    public Button getPowerupButton() { return powerupButton; }
    public Button getMenuButton() { return menuButton; }
    public Button getMuteButton() { return muteButton; }
    public Button getDemoEnergyButton() { return demoEnergyButton; }
    public Button getDemoWinButton() { return demoWinButton; }
    public TextArea getLogArea() { return logArea; }
    public Label getPlayerInfoLabel() { return playerInfoLabel; }
    public Label getOpponentInfoLabel() { return opponentInfoLabel; }
    public Label getTurnLabel() { return turnLabel; }
    public Label getDiceLabel() { return diceLabel; }
    public Label getCardLabel() { return cardLabel; }
    public GridPane getBoardGrid() { return boardGrid; }
    public StackPane[] getCellPanes() { return cellPanes; }
}
