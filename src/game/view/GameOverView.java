package game.view;

import game.engine.Role;
import game.engine.monsters.Monster;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

public class GameOverView {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    private static final String GAME_FONT = "'Impact', 'Arial Black', 'Trebuchet MS', 'Arial'";
    private static final String INFO_FONT = "'Trebuchet MS', 'Arial Black', 'Arial'";

    private Pane root;
    private Button returnButton;
    private Button tryAgainButton;

    public GameOverView(Monster winner, Monster player, Monster opponent) {
        root = new Pane();
        root.setPrefSize(WIDTH, HEIGHT);

        Image backgroundImage = GuiStyle.loadAssetImage(getClass(), "menu.png");
        if (backgroundImage != null) {
            ImageView background = new ImageView(backgroundImage);
            background.setFitWidth(WIDTH);
            background.setFitHeight(HEIGHT);
            background.setPreserveRatio(false);
            root.getChildren().add(background);
        }

        // Restored to the original overlay strength/alignment so the old composition stays in place.
        Rectangle dim = new Rectangle(WIDTH, HEIGHT);
        dim.setFill(Color.web("rgba(0,0,0,0.52)"));
        root.getChildren().add(dim);

        boolean playerWon = winner != null && winner == player;
        createResultCard(winner, player, opponent, playerWon);
    }

    private void createResultCard(Monster winner, Monster player, Monster opponent, boolean playerWon) {
        // Original panel sizing/placement restored. Do not change these unless realigning the art manually.
        final double panelW = 560;
        final double panelH = playerWon ? 551 : 557;
        final double panelX = (WIDTH - panelW) / 2.0;
        final double panelY = 54;
        final String theme = playerWon ? "cyan" : "orange";

        Image panelImage = GuiStyle.loadAssetImage(getClass(), playerWon ? "ui/gameover/victory_panel.png" : "ui/gameover/defeat_panel.png");
        if (panelImage != null) {
            ImageView panel = new ImageView(panelImage);
            panel.setFitWidth(panelW);
            panel.setPreserveRatio(true);
            panel.setSmooth(true);
            panel.setLayoutX(panelX);
            panel.setLayoutY(panelY);
            root.getChildren().add(panel);
        }

        Label title = new Label(playerWon ? "VICTORY!" : "GAME OVER");
        title.setAlignment(Pos.CENTER);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setLayoutX(panelX + 60);
        title.setLayoutY(panelY + 46);
        title.setPrefWidth(panelW - 120);
        title.setPrefHeight(54);
        title.setStyle(gameTitleStyle(playerWon ? "#23f7ff" : "#ff9e2c", 38));
        root.getChildren().add(title);

        Image winnerImage = choosePortrait(winner);
        if (winnerImage != null) {
            ImageView winnerPortrait = new ImageView(winnerImage);
            winnerPortrait.setFitWidth(225);
            winnerPortrait.setFitHeight(225);
            winnerPortrait.setPreserveRatio(true);
            winnerPortrait.setSmooth(true);
            winnerPortrait.setCache(true);
            // Original centered position restored.
            winnerPortrait.setLayoutX(panelX + (panelW - 225) / 2.0);
            winnerPortrait.setLayoutY(panelY + 118);
            winnerPortrait.setStyle("-fx-effect: dropshadow(gaussian, rgba(35,247,255,0.75), 18, 0.32, 0, 0);");
            root.getChildren().add(winnerPortrait);
        }

        Label winnerLabel = new Label(formatWinnerLine(winner));
        winnerLabel.setAlignment(Pos.CENTER);
        winnerLabel.setTextAlignment(TextAlignment.CENTER);
        winnerLabel.setWrapText(true);
        winnerLabel.setLayoutX(panelX + 62);
        winnerLabel.setLayoutY(panelY + 335);
        winnerLabel.setPrefWidth(panelW - 124);
        winnerLabel.setPrefHeight(58);
        winnerLabel.setStyle(gameInfoStyle(19));
        root.getChildren().add(winnerLabel);

        Label finalStats = new Label(formatFinalStats(player, opponent));
        finalStats.setAlignment(Pos.CENTER);
        finalStats.setTextAlignment(TextAlignment.CENTER);
        finalStats.setWrapText(true);
        finalStats.setLayoutX(panelX + 58);
        finalStats.setLayoutY(panelY + 385);
        finalStats.setPrefWidth(panelW - 116);
        finalStats.setPrefHeight(76);
        finalStats.setStyle(gameStatsStyle());
        root.getChildren().add(finalStats);

        // Button alignment restored to the old positions, but the newer themed button style is kept.
        returnButton = new Button("LOBBY");
        returnButton.setLayoutX(panelX + (panelW - 374) / 2.0);
        returnButton.setLayoutY(panelY + 497);
        returnButton.setPrefWidth(170);
        returnButton.setPrefHeight(40);
        returnButton.setAlignment(Pos.CENTER);
        returnButton.setFocusTraversable(false);
        returnButton.setStyle(gameOverButtonStyle(theme, false));
        GuiStyle.applyGlowHover(returnButton, gameOverButtonStyle(theme, false), gameOverButtonStyle(theme, true));
        root.getChildren().add(returnButton);
        returnButton.toFront();

        tryAgainButton = new Button("TRY AGAIN");
        tryAgainButton.setLayoutX(panelX + (panelW + 34) / 2.0);
        tryAgainButton.setLayoutY(returnButton.getLayoutY());
        tryAgainButton.setPrefWidth(returnButton.getPrefWidth());
        tryAgainButton.setPrefHeight(returnButton.getPrefHeight());
        tryAgainButton.setAlignment(Pos.CENTER);
        tryAgainButton.setFocusTraversable(false);
        tryAgainButton.setStyle(gameOverButtonStyle(theme, false));
        GuiStyle.applyGlowHover(tryAgainButton, gameOverButtonStyle(theme, false), gameOverButtonStyle(theme, true));
        root.getChildren().add(tryAgainButton);
        tryAgainButton.toFront();
    }

    private String gameTitleStyle(String accent, int size) {
        return "-fx-font-size: " + size + "px;" +
               "-fx-font-family: " + GAME_FONT + ";" +
               "-fx-font-weight: bold;" +
               "-fx-text-fill: white;" +
               "-fx-effect: dropshadow(gaussian, " + accent + ", 14, 0.30, 0, 0);";
    }

    private String gameInfoStyle(int size) {
        return "-fx-font-size: " + size + "px;" +
               "-fx-font-family: " + GAME_FONT + ";" +
               "-fx-font-weight: bold;" +
               "-fx-text-fill: white;" +
               "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.96), 5, 0.78, 0, 1);";
    }

    private String gameStatsStyle() {
        return "-fx-font-size: 14px;" +
               "-fx-font-family: " + INFO_FONT + ";" +
               "-fx-font-weight: bold;" +
               "-fx-text-fill: white;" +
               "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.96), 4, 0.75, 0, 1);";
    }

    private String gameOverButtonStyle(String theme, boolean hover) {
        String accent = "orange".equals(theme) ? "#ff9e2c" : "#23f7ff";
        String top = "orange".equals(theme) ? "rgba(72,35,8,0.98)" : "rgba(12,56,70,0.98)";
        String bottom = "rgba(3,8,18,0.98)";
        return "-fx-font-size: 14px;" +
               "-fx-font-family: " + GAME_FONT + ";" +
               "-fx-font-weight: bold;" +
               "-fx-text-fill: white;" +
               "-fx-background-color: linear-gradient(to bottom, " + top + ", " + bottom + ");" +
               "-fx-border-color: " + accent + ";" +
               "-fx-border-width: " + (hover ? "2.5" : "1.6") + ";" +
               "-fx-background-radius: 13;" +
               "-fx-border-radius: 13;" +
               "-fx-padding: 0;" +
               "-fx-cursor: hand;" +
               "-fx-effect: dropshadow(gaussian, " + accent + ", " + (hover ? "16" : "8") + ", 0.36, 0, 0);";
    }

    private Image choosePortrait(Monster monster) {
        if (monster == null) return null;
        if (monster.getOriginalRole() == Role.SCARER) {
            Image scarer = GuiStyle.loadAssetImage(getClass(), "ui/portraits/scarer_avatar.png");
            if (scarer != null) return scarer;
            return GuiStyle.loadAssetImage(getClass(), "monsters/small/scarer_monster.png");
        }
        Image laugher = GuiStyle.loadAssetImage(getClass(), "ui/portraits/laugher_avatar.png");
        if (laugher != null) return laugher;
        return GuiStyle.loadAssetImage(getClass(), "monsters/small/laugher_monster.png");
    }

    private String formatWinnerLine(Monster winner) {
        if (winner == null) return "Winner unavailable";
        return winner.getName() + " WINS AS " + winner.getRole() + "\nType: " + winner.getClass().getSimpleName();
    }

    private String formatFinalStats(Monster player, Monster opponent) {
        String p = player == null ? "Player: -" : "Player: " + player.getName() + " | " + player.getEnergy() + " energy | Cell " + player.getPosition();
        String o = opponent == null ? "Opponent: -" : "Opponent: " + opponent.getName() + " | " + opponent.getEnergy() + " energy | Cell " + opponent.getPosition();
        return "FINAL RESULTS\n" + p + "\n" + o;
    }

    public Pane getRoot() {
        return root;
    }

    public Button getReturnButton() {
        return returnButton;
    }

    public Button getTryAgainButton() {
        return tryAgainButton;
    }
}
