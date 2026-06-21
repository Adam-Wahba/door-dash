package game.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Random;

import game.engine.Board;
import game.engine.Constants;
import game.engine.Game;
import game.engine.Role;
import game.engine.cards.Card;
import game.engine.cells.CardCell;
import game.engine.cells.Cell;
import game.engine.cells.ContaminationSock;
import game.engine.cells.ConveyorBelt;
import game.engine.cells.DoorCell;
import game.engine.cells.MonsterCell;
import game.engine.cells.TransportCell;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.monsters.Monster;
import game.view.GameBoardView;
import game.view.GameOverView;
import game.view.GuiStyle;
import game.view.InstructionsView;
import game.view.MainMenuView;
import game.view.PopupUtil;
import game.view.SideSelectionView;
import game.view.audio.AudioManager;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameGUI extends javafx.application.Application {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    private Stage primaryStage;
    private Game game;
    private GameBoardView boardView;
    private Random random;
    private boolean powerupUsedThisTurn;
    private Role lastChosenRole;
    private boolean fullScreenWanted;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.random = new Random();
        audioPrepareSafe();

        primaryStage.setTitle("DooR DasH");
        primaryStage.fullScreenProperty().addListener((obs, oldValue, newValue) -> fullScreenWanted = newValue);
        // Safe fullscreen/maximize support: every screen still keeps its internal 1280 x 720 layout.
        primaryStage.setResizable(true);
        primaryStage.setFullScreenExitHint("");
        showMainMenu();
        primaryStage.show();
    }

    public void showMainMenu() {
        game = null;
        boardView = null;
        powerupUsedThisTurn = false;

        playMenuMusicSafe();

        MainMenuView view = new MainMenuView();
        wireMuteButton(view.getMuteButton());
        view.getPlayButton().setOnAction(e -> {
            playClickSafe();
            showInstructions();
        });
        view.getQuitButton().setOnAction(e -> {
            playClickSafe();
            primaryStage.close();
        });

        Scene scene = createScaledScene(view.getRoot());
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER) {
                playClickSafe();
                showInstructions();
                e.consume();
            } else if (e.getCode() == KeyCode.ESCAPE) {
                playClickSafe();
                primaryStage.close();
                e.consume();
            } else if (e.getCode() == KeyCode.M) {
                playClickSafe();
                toggleMusicMuteSafe();
                refreshMuteButton(view.getMuteButton());
                e.consume();
            }
        });
        setScenePreserveFullscreen(scene);
    }

    public void showInstructions() {
        playMenuMusicSafe();

        InstructionsView view = new InstructionsView();
        wireMuteButton(view.getMuteButton());
        view.getContinueButton().setOnAction(e -> {
            playClickSafe();
            showSideSelection();
        });
        setScenePreserveFullscreen(createScaledScene(view.getRoot()));
    }

    public void showSideSelection() {
        playMenuMusicSafe();

        SideSelectionView view = new SideSelectionView();
        wireMuteButton(view.getMuteButton());
        view.getScarerButton().setOnAction(e -> {
            playClickSafe();
            showGameBoard(Role.SCARER);
        });
        view.getLaugherButton().setOnAction(e -> {
            playClickSafe();
            showGameBoard(Role.LAUGHER);
        });
        setScenePreserveFullscreen(createScaledScene(view.getRoot()));
    }

    public void showGameBoard(Role chosenRole) {
    	this.lastChosenRole = chosenRole;
    	
    	try {
            game = new Game(chosenRole);
            boardView = new GameBoardView();
            powerupUsedThisTurn = false;

            playGameMusicSafe();

            wireMuteButton(boardView.getMuteButton());
            boardView.getRollDiceButton().setOnAction(e -> handleRollDice());
            boardView.getPowerupButton().setOnAction(e -> handlePowerup());
            boardView.getMenuButton().setOnAction(e -> {
                playClickSafe();
                showMainMenu();
            });
            boardView.getDemoEnergyButton().setOnAction(e -> {
                playClickSafe();
                handleDemoEnergyKey();
            });
            boardView.getDemoWinButton().setOnAction(e -> {
                playClickSafe();
                handleDemoWinKey();
            });

            refreshBoard("Game started as " + chosenRole);
            boardView.updateDiceRoll(0);
            boardView.updateCardDrawn("-");
            boardView.setAction("Game ready. Choose POWERUP or ROLL.");

            Scene scene = createScaledScene(boardView.getRoot());

            boardView.getRoot().addEventFilter(KeyEvent.KEY_PRESSED, this::handleGameBoardShortcut);
            scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleGameBoardShortcut);
            setScenePreserveFullscreen(scene);
            boardView.getRoot().setFocusTraversable(true);
            boardView.getRoot().requestFocus();
            javafx.application.Platform.runLater(() -> boardView.getRoot().requestFocus());

        } catch (IOException e) {
            PopupUtil.showError(primaryStage, "Game Loading Error",
                    "Could not load the CSV files.\nCheck cards.csv, cells.csv and monsters.csv.\n\n" + e.getMessage());
        } catch (RuntimeException e) {
            PopupUtil.showError(primaryStage, "Game Loading Error", e.getMessage());
        }
    }


    private void handleGameBoardShortcut(KeyEvent e) {
        if (e == null || e.isConsumed()) return;

        if (e.getCode() == KeyCode.R) {
            handleRollDice();
            e.consume();
        } else if (e.getCode() == KeyCode.P) {
            handlePowerup();
            e.consume();
        } else if (e.getCode() == KeyCode.E) {
            handleDemoEnergyKey();
            e.consume();
        } else if (e.getCode() == KeyCode.W) {
            handleDemoWinKey();
            e.consume();
        } else if (e.getCode() == KeyCode.M) {
            playClickSafe();
            toggleMusicMuteSafe();
            if (boardView != null) {
                refreshMuteButton(boardView.getMuteButton());
            }
            e.consume();
        } else if (e.getCode() == KeyCode.ESCAPE) {
            playClickSafe();
            showMainMenu();
            e.consume();
        }
    }

    private Scene createScaledScene(final Pane screenRoot) {
        // Fixed 1280 x 720 canvas: fullscreen scales this canvas as one unit.
        // This prevents effects/glows inside the game from changing the scene bounds and causing visual shifts.
        final Pane fixedCanvas = new Pane();
        fixedCanvas.setPrefSize(WIDTH, HEIGHT);
        fixedCanvas.setMinSize(WIDTH, HEIGHT);
        fixedCanvas.setMaxSize(WIDTH, HEIGHT);
        fixedCanvas.getChildren().add(screenRoot);

        final StackPane wrapper = new StackPane();
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setStyle("-fx-background-color: black;");
        wrapper.getChildren().add(fixedCanvas);

        final Scene scene = new Scene(wrapper, WIDTH, HEIGHT);

        fixedCanvas.scaleXProperty().bind(
                Bindings.min(scene.widthProperty().divide(WIDTH), scene.heightProperty().divide(HEIGHT))
        );
        fixedCanvas.scaleYProperty().bind(fixedCanvas.scaleXProperty());

        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.F11) {
                fullScreenWanted = !primaryStage.isFullScreen();
                primaryStage.setFullScreen(fullScreenWanted);
                e.consume();
            }
        });

        return scene;
    }

    private void setScenePreserveFullscreen(Scene scene) {
        boolean restoreFullScreen = fullScreenWanted || primaryStage.isFullScreen();
        primaryStage.setScene(scene);
        if (restoreFullScreen) {
            fullScreenWanted = true;
            primaryStage.setFullScreen(true);
            javafx.application.Platform.runLater(() -> {
                if (fullScreenWanted) {
                    primaryStage.setFullScreen(true);
                }
            });
        }
    }

    private void wireMuteButton(final Button muteButton) {
        if (muteButton == null) return;
        refreshMuteButton(muteButton);
        muteButton.setOnAction(e -> {
            playClickSafe();
            toggleMusicMuteSafe();
            refreshMuteButton(muteButton);
        });
    }

    private void refreshMuteButton(Button muteButton) {
        if (muteButton != null) {
            String text = muteButton.getText();
            if (text != null && text.trim().length() > 0) {
                muteButton.setText(isMusicMutedSafe() ? "MUTED" : "MUSIC");
                muteButton.setStyle(GuiStyle.mutePillButton(isMusicMutedSafe()));
            } else {
                muteButton.setStyle(GuiStyle.muteButton(isMusicMutedSafe()));
            }
        }
    }

    private void audioPrepareSafe() {
        try { AudioManager.prepare(); } catch (Throwable ignored) { }
    }

    private void playMenuMusicSafe() {
        try { AudioManager.playMenuMusic(); } catch (Throwable ignored) { }
    }

    private void playGameMusicSafe() {
        try { AudioManager.playGameMusic(); } catch (Throwable ignored) { }
    }

    private void stopMusicSafe() {
        try { AudioManager.stopMusic(); } catch (Throwable ignored) { }
    }

    private void playClickSafe() {
        try { AudioManager.playClick(); } catch (Throwable ignored) { }
    }

    private void playRollSafe() {
        try { AudioManager.playRoll(); } catch (Throwable ignored) { }
    }

    private void playPowerupSafe() {
        try { AudioManager.playPowerup(); } catch (Throwable ignored) { }
    }

    private void playCardSafe() {
        try { AudioManager.playCard(); } catch (Throwable ignored) { }
    }

    private void playWinSafe() {
        try { AudioManager.playWin(); } catch (Throwable ignored) { }
    }

    private void playGameOverSafe() {
        try { AudioManager.playGameOver(); } catch (Throwable ignored) { }
    }

    private void playEnergySafe() {
        try { AudioManager.playEnergy(); } catch (Throwable ignored) { }
    }

    private void toggleMusicMuteSafe() {
        try { AudioManager.toggleMusicMute(); } catch (Throwable ignored) { }
    }

    private boolean isMusicMutedSafe() {
        try { return AudioManager.isMusicMuted(); } catch (Throwable ignored) { return false; }
    }

    private void handleDemoEnergyKey() {
        if (game == null || boardView == null) return;

        int boost = Constants.WINNING_ENERGY;
        Monster player = game.getPlayer();
        Monster opponent = game.getOpponent();

        if (player != null) {
            player.setEnergy(player.getEnergy() + boost);
            boardView.showFloatingText(player.getPosition(), "+" + boost, true);
        }
        if (opponent != null) {
            opponent.setEnergy(opponent.getEnergy() + boost);
            boardView.showFloatingText(opponent.getPosition(), "+" + boost, true);
        }

        playEnergySafe();
        refreshBoard("DEMO ENERGY: both monsters gained +" + boost + " energy.");
        boardView.setAction("Demo energy boost (+" + boost + ")");
        checkWinnerAndShow();
    }

    private void handleDemoWinKey() {
        if (game == null || boardView == null || game.getPlayer() == null) return;

        Monster player = game.getPlayer();
        player.setPosition(Constants.WINNING_POSITION);

        playClickSafe();
        refreshBoard("DEMO TO 99: player moved directly to cell " + Constants.WINNING_POSITION + ".");
        boardView.setAction("Demo win position: player to cell " + Constants.WINNING_POSITION);
        boardView.highlightCell(Constants.WINNING_POSITION);

        if (player.getEnergy() < Constants.WINNING_ENERGY) {
            boardView.addLog("DEMO TIP: player still needs " + (Constants.WINNING_ENERGY - player.getEnergy()) + " energy. Press E to boost energy.");
        }

        checkWinnerAndShow();
    }

    private void handlePowerup() {
        if (game == null || boardView == null) return;

        Monster current = game.getCurrent();
        if (current == null) return;

        if (powerupUsedThisTurn) {
            PopupUtil.showMessage(primaryStage, "Powerup Already Used",
                    "You already activated a powerup this turn. Roll the dice now.");
            return;
        }

        if (current.isFrozen()) {
            PopupUtil.showMessage(primaryStage, "Frozen Turn",
                    current.getName() + " is frozen and cannot use a powerup this turn.");
            return;
        }

        ActionSnapshot before = takeSnapshot();

        try {
            game.usePowerup();
            playPowerupSafe();
            powerupUsedThisTurn = true;
            boardView.getPowerupButton().setDisable(true);

            ActionSnapshot after = takeSnapshot();
            refreshBoard(null);

            String summary = describePowerup(current);
            boardView.setAction("Powerup: " + current.getName());
            boardView.showActionBanner("POWERUP ACTIVATED", current.getName() + " used " + current.getClass().getSimpleName() + " power.\n" + summary, "#ffa928");
            boardView.addLog("POWERUP: " + current.getName() + " used " + current.getClass().getSimpleName() + " power.");
            boardView.addLog("POWERUP COST: -" + Constants.POWERUP_COST + " energy.");
            reportSnapshotChanges(before, after);
            checkWinnerAndShow();

        } catch (OutOfEnergyException e) {
            boardView.setAction("Powerup failed");
            PopupUtil.showError(primaryStage, "Not Enough Energy", e.getMessage());
            refreshBoard("INVALID POWERUP: " + e.getMessage());
        } catch (Exception e) {
            boardView.setAction("Powerup failed");
            PopupUtil.showError(primaryStage, "Powerup Error", safeMessage(e));
            refreshBoard("POWERUP ERROR: " + safeMessage(e));
        }
    }

    private void handleRollDice() {
        if (game == null || boardView == null) return;

        Monster current = game.getCurrent();
        Monster opponent = getOpponentOf(current);
        if (current == null || opponent == null) return;

        ActionSnapshot before = takeSnapshot();

        if (current.isFrozen()) {
            current.setFrozen(false);
            ActionSnapshot after = takeSnapshot();
            boardView.setAction(current.getName() + " skipped: frozen");
            boardView.showActionBanner("TURN SKIPPED", current.getName() + " was frozen, so the turn was skipped.\nFreeze is now removed.", "#74e8ff");
            boardView.addLog("FREEZE: " + current.getName() + " skipped this turn.");
            switchTurn();
            beginNewTurn();
            refreshBoard(null);
            reportSnapshotChanges(before, after);
            checkWinnerAndShow();
            return;
        }

        int roll = rollDice();
        playRollSafe();
        int predictedLandingIndex = predictLandingIndex(current, roll);
        Cell predictedLandingCell = getCellAt(game.getBoard(), predictedLandingIndex);
        Card topCardBefore = peekTopCard();
        int deckBefore = safeDeckCount();

        try {
            boardView.updateDiceRoll(roll);
            boardView.setAction(current.getName() + " rolled " + roll);
            boardView.addLog("ROLL: " + current.getName() + " rolled " + roll + ".");
            boardView.addLog("LANDING CHECK: expected cell " + predictedLandingIndex + " (" + cellName(predictedLandingCell) + ").");

            game.getBoard().moveMonster(current, roll, opponent);

            Card drawn = detectDrawnCard(topCardBefore, deckBefore);
            if (drawn != null) {
                playCardSafe();
            }
            ActionSnapshot after = takeSnapshot();

            switchTurn();
            beginNewTurn();
            refreshBoard(null);

            reportCellEffect(predictedLandingIndex, predictedLandingCell, before, after, current, drawn);
            reportCardEffect(drawn, before, after);
            reportSnapshotChanges(before, after);
            checkWinnerAndShow();

        } catch (InvalidMoveException e) {
            boardView.setAction("Invalid move blocked");
            PopupUtil.showError(primaryStage, "Invalid Move", e.getMessage());
            refreshBoard("INVALID MOVE: " + e.getMessage());
        } catch (Exception e) {
            boardView.setAction("Turn error");
            PopupUtil.showError(primaryStage, "Turn Error", safeMessage(e));
            refreshBoard("TURN ERROR: " + safeMessage(e));
        }
    }

    private void beginNewTurn() {
        powerupUsedThisTurn = false;
        boardView.getPowerupButton().setDisable(false);
        boardView.getRollDiceButton().setDisable(false);
    }

    private void switchTurn() {
        if (game.getCurrent() == game.getPlayer()) {
            game.setCurrent(game.getOpponent());
        } else {
            game.setCurrent(game.getPlayer());
        }
    }

    private Monster getOpponentOf(Monster monster) {
        if (monster == null) return null;
        if (monster == game.getPlayer()) return game.getOpponent();
        return game.getPlayer();
    }

    private int rollDice() {
        return random.nextInt(6) + 1;
    }

    private void refreshBoard(String logMessage) {
        if (boardView == null || game == null) return;

        boardView.refresh(game.getBoard(), game.getPlayer(), game.getOpponent(), game.getCurrent());
        if (logMessage != null && logMessage.length() > 0) {
            boardView.addLog(logMessage);
        }
    }

    private void reportCellEffect(int index, Cell cell, ActionSnapshot before, ActionSnapshot after, Monster current, Card drawn) {
        if (cell == null) return;

        if (cell instanceof DoorCell) {
            DoorCell door = (DoorCell) cell;
            boolean wasActivated = index >= 0 && index < before.doorActivated.length && before.doorActivated[index];
            if (wasActivated) {
                boardView.addLog("DOOR: Cell " + index + " was already exhausted. No door energy should be gained again.");
                boardView.showFloatingText(index, "USED", false);
            } else {
                String side = door.getRole() == Role.SCARER ? "SCARER" : "LAUGHER";
                boardView.addLog("DOOR: " + current.getName() + " landed on " + side + " door at cell " + index + " (energy " + door.getEnergy() + ").");
                boardView.showFloatingText(index, "DOOR", true);
            }
            return;
        }

        if (cell instanceof ConveyorBelt) {
            int effect = ((TransportCell) cell).getEffect();
            boardView.addLog("CONVEYOR: Cell " + index + " pushed " + current.getName() + " forward by +" + effect + " cells.");
            boardView.showFloatingText(index, "+" + effect, true);
            return;
        }

        if (cell instanceof ContaminationSock) {
            int effect = ((TransportCell) cell).getEffect();
            boardView.addLog("SOCK: Cell " + index + " moved " + current.getName() + " by " + effect + " and applied -" + Constants.SLIP_PENALTY + " energy penalty.");
            boardView.showFloatingText(index, effect + " / -" + Constants.SLIP_PENALTY, false);
            return;
        }

        if (cell instanceof CardCell) {
            if (drawn != null) {
                boardView.addLog("CARD CELL: Cell " + index + " drew " + drawn.getName() + ".");
            } else {
                boardView.addLog("CARD CELL: Cell " + index + " triggered a card draw.");
            }
            boardView.showFloatingText(index, "CARD", true);
            return;
        }

        if (cell instanceof MonsterCell) {
            Monster stationed = ((MonsterCell) cell).getCellMonster();
            if (stationed != null) {
                boolean ally = before.getRole(current) == before.getRole(stationed);
                if (ally) {
                    boardView.addLog("MONSTER CELL: " + current.getName() + " met ally " + stationed.getName() + ", so a free powerup was triggered.");
                    boardView.showFloatingText(index, "ALLY", true);
                } else {
                    boardView.addLog("MONSTER CELL: " + current.getName() + " met rival " + stationed.getName() + ". Energy battle checked by engine.");
                    boardView.showFloatingText(index, "BATTLE", false);
                }
            } else {
                boardView.addLog("MONSTER CELL: Cell " + index + " triggered monster-cell logic.");
            }
            return;
        }

        boardView.addLog("CELL: " + current.getName() + " landed on a normal corridor cell.");
    }

    private void reportCardEffect(Card drawn, ActionSnapshot before, ActionSnapshot after) {
        if (drawn == null) {
            boardView.updateCardDrawn("-");
            return;
        }

        String result = summarizeCardResult(drawn, before, after);
        boardView.updateCardDrawn(drawn);
        boardView.showCardEffect(drawn, result);
        boardView.addLog("CARD: " + drawn.getName() + " - " + drawn.getDescription());
        if (result.length() > 0) {
            boardView.addLog("CARD RESULT: " + result.replace('\n', ' '));
        }
    }

    private void reportSnapshotChanges(ActionSnapshot before, ActionSnapshot after) {
        for (int i = 0; i < before.monsters.size(); i++) {
            Monster monster = before.monsters.get(i);
            MonsterState oldState = before.get(monster);
            MonsterState newState = after.get(monster);
            if (oldState == null || newState == null) continue;
            logMonsterStateDiff(monster, labelFor(monster), oldState, newState);
        }

        for (int i = 0; i < before.doorActivated.length && i < after.doorActivated.length; i++) {
            if (!before.doorActivated[i] && after.doorActivated[i]) {
                boardView.addLog("DOOR USED: Cell " + i + " is now exhausted.");
                boardView.showFloatingText(i, "USED", false);
                boardView.highlightCell(i);
            }
        }
    }

    private void logMonsterStateDiff(Monster monster, String label, MonsterState oldState, MonsterState newState) {
        if (oldState.position != newState.position) {
            boardView.addLog(label + " MOVE: " + oldState.position + " -> " + newState.position + " (" + signed(newState.position - oldState.position) + ").");
            boardView.animateMonsterMovement(monster, oldState.position, newState.position, monster == game.getPlayer());
            boardView.highlightCell(newState.position);
        }

        if (oldState.energy != newState.energy) {
            int diff = newState.energy - oldState.energy;
            playEnergySafe();
            boardView.addLog(label + " ENERGY: " + signed(diff) + " (" + oldState.energy + " -> " + newState.energy + ").");
            boardView.showFloatingText(newState.position, signed(diff), diff >= 0);
        }

        if (oldState.shielded && !newState.shielded && oldState.energy == newState.energy) {
            boardView.addLog(label + " SHIELD BLOCKED: energy loss was blocked.");
            boardView.showFloatingText(newState.position, "SHIELD", true);
        } else if (!oldState.shielded && newState.shielded) {
            boardView.addLog(label + " SHIELD: shield is now ACTIVE.");
            boardView.showFloatingText(newState.position, "SHIELD", true);
        } else if (oldState.shielded && !newState.shielded) {
            boardView.addLog(label + " SHIELD: shield was consumed/removed.");
        }

        if (!oldState.frozen && newState.frozen) {
            boardView.addLog(label + " STATUS: frozen and will skip the next turn.");
            boardView.showFloatingText(newState.position, "FROZEN", false);
        } else if (oldState.frozen && !newState.frozen) {
            boardView.addLog(label + " STATUS: freeze removed.");
        }

        if (oldState.role != newState.role) {
            boardView.addLog(label + " ROLE: " + oldState.role + " -> " + newState.role + " (confusion active).");
            boardView.showFloatingText(newState.position, "CONFUSED", false);
        }

        if (oldState.confusionTurns != newState.confusionTurns) {
            boardView.addLog(label + " CONFUSION TURNS: " + oldState.confusionTurns + " -> " + newState.confusionTurns + ".");
        }

        if (oldState.momentumTurns != newState.momentumTurns) {
            boardView.addLog(label + " MOMENTUM: " + oldState.momentumTurns + " -> " + newState.momentumTurns + ".");
        }

        if (oldState.focusTurns != newState.focusTurns) {
            boardView.addLog(label + " FOCUS: " + oldState.focusTurns + " -> " + newState.focusTurns + ".");
        }
    }

    private String summarizeCardResult(Card card, ActionSnapshot before, ActionSnapshot after) {
        String type = card.getClass().getSimpleName();
        StringBuilder sb = new StringBuilder();

        if ("SwapperCard".equals(type)) {
            appendPositionChanges(sb, before, after, "Swapper checked position swap.");
        } else if ("EnergyStealCard".equals(type)) {
            appendEnergyChanges(sb, before, after, "Energy steal checked shield and actual stolen value.");
        } else if ("ShieldCard".equals(type)) {
            appendShieldChanges(sb, before, after, "Shield card updated shields.");
        } else if ("StartOverCard".equals(type)) {
            appendPositionChanges(sb, before, after, "Start-over card checked who returns to cell 0.");
        } else if ("ConfusionCard".equals(type)) {
            appendRoleAndConfusionChanges(sb, before, after, "Confusion card swapped roles temporarily.");
        } else {
            sb.append("Card effect applied. Check the action log for exact changes.");
        }

        if (sb.length() == 0) {
            sb.append("No visible player/opponent change was detected. The card condition may not have been satisfied.");
        }
        return sb.toString();
    }

    private void appendPositionChanges(StringBuilder sb, ActionSnapshot before, ActionSnapshot after, String fallback) {
        appendLine(sb, fallback);
        for (int i = 0; i < before.monsters.size(); i++) {
            Monster m = before.monsters.get(i);
            MonsterState oldState = before.get(m);
            MonsterState newState = after.get(m);
            if (oldState != null && newState != null && oldState.position != newState.position) {
                appendLine(sb, m.getName() + ": " + oldState.position + " -> " + newState.position);
            }
        }
    }

    private void appendEnergyChanges(StringBuilder sb, ActionSnapshot before, ActionSnapshot after, String fallback) {
        appendLine(sb, fallback);
        for (int i = 0; i < before.monsters.size(); i++) {
            Monster m = before.monsters.get(i);
            MonsterState oldState = before.get(m);
            MonsterState newState = after.get(m);
            if (oldState != null && newState != null && oldState.energy != newState.energy) {
                appendLine(sb, m.getName() + ": " + signed(newState.energy - oldState.energy));
            }
        }
    }

    private void appendShieldChanges(StringBuilder sb, ActionSnapshot before, ActionSnapshot after, String fallback) {
        appendLine(sb, fallback);
        for (int i = 0; i < before.monsters.size(); i++) {
            Monster m = before.monsters.get(i);
            MonsterState oldState = before.get(m);
            MonsterState newState = after.get(m);
            if (oldState != null && newState != null && oldState.shielded != newState.shielded) {
                appendLine(sb, m.getName() + ": shield " + oldState.shielded + " -> " + newState.shielded);
            }
        }
    }

    private void appendRoleAndConfusionChanges(StringBuilder sb, ActionSnapshot before, ActionSnapshot after, String fallback) {
        appendLine(sb, fallback);
        for (int i = 0; i < before.monsters.size(); i++) {
            Monster m = before.monsters.get(i);
            MonsterState oldState = before.get(m);
            MonsterState newState = after.get(m);
            if (oldState != null && newState != null && (oldState.role != newState.role || oldState.confusionTurns != newState.confusionTurns)) {
                appendLine(sb, m.getName() + ": role " + oldState.role + " -> " + newState.role + ", confusion " + oldState.confusionTurns + " -> " + newState.confusionTurns);
            }
        }
    }

    private void appendLine(StringBuilder sb, String line) {
        if (line == null || line.length() == 0) return;
        if (sb.length() > 0) sb.append('\n');
        sb.append(line);
    }

    private String describePowerup(Monster current) {
        if (current == null) return "Powerup effect applied.";
        String type = current.getClass().getSimpleName();
        if ("Dynamo".equals(type)) return "Freeze effect: opponent becomes frozen and must skip their next turn.";
        if ("Dasher".equals(type)) return "Momentum Rush: movement becomes 3x speed for 3 turns.";
        if ("MultiTasker".equals(type)) return "Focus Mode: movement becomes normal speed for 2 turns.";
        if ("Schemer".equals(type)) return "Chain Attack: steals energy from the opponent and stationed monsters.";
        return "Powerup effect applied.";
    }

    private int predictLandingIndex(Monster monster, int roll) {
        if (monster == null) return 0;
        int distance = roll;
        String type = monster.getClass().getSimpleName();
        if ("Dasher".equals(type)) {
            int momentum = safeIntGetter(monster, "getMomentumTurns");
            distance *= momentum > 0 ? 3 : 2;
        } else if ("MultiTasker".equals(type)) {
            int focus = safeIntGetter(monster, "getNormalSpeedTurns");
            if (focus <= 0) distance /= 2;
        }
        int result = monster.getPosition() + distance;
        result = result % Constants.BOARD_SIZE;
        if (result < 0) result += Constants.BOARD_SIZE;
        return result;
    }

    private ActionSnapshot takeSnapshot() {
        ActionSnapshot snapshot = new ActionSnapshot();
        if (game == null) return snapshot;

        addMonsterToSnapshot(snapshot, game.getPlayer());
        addMonsterToSnapshot(snapshot, game.getOpponent());
        try {
            ArrayList<Monster> stationed = Board.getStationedMonsters();
            if (stationed != null) {
                for (int i = 0; i < stationed.size(); i++) addMonsterToSnapshot(snapshot, stationed.get(i));
            }
        } catch (Exception ignored) { }

        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            Cell cell = getCellAt(game.getBoard(), i);
            snapshot.doorActivated[i] = cell instanceof DoorCell && ((DoorCell) cell).isActivated();
        }

        snapshot.deckCount = safeDeckCount();
        snapshot.topCard = peekTopCard();
        return snapshot;
    }

    private void addMonsterToSnapshot(ActionSnapshot snapshot, Monster monster) {
        if (snapshot == null || monster == null) return;
        if (!snapshot.states.containsKey(monster)) {
            snapshot.monsters.add(monster);
            snapshot.states.put(monster, new MonsterState(monster));
        }
    }

    private Card peekTopCard() {
        try {
            if (Board.getCards() != null && Board.getCards().size() > 0) return Board.getCards().get(0);
        } catch (Exception ignored) { }
        return null;
    }

    private Card detectDrawnCard(Card topCardBefore, int deckBefore) {
        int deckAfter = safeDeckCount();
        if (topCardBefore != null && deckBefore > deckAfter) return topCardBefore;
        return null;
    }

    private int safeDeckCount() {
        try {
            return Board.getCards() == null ? -1 : Board.getCards().size();
        } catch (Exception e) {
            return -1;
        }
    }

    private Cell getCellAt(Board board, int index) {
        if (board == null || index < 0 || index >= Constants.BOARD_SIZE) return null;
        try {
            Cell[][] cells = board.getBoardCells();
            int row = index / Constants.BOARD_COLS;
            int colInsideRow = index % Constants.BOARD_COLS;
            int col = (row % 2 == 0) ? colInsideRow : (Constants.BOARD_COLS - 1 - colInsideRow);
            return cells[row][col];
        } catch (Exception e) {
            return null;
        }
    }

    private String cellName(Cell cell) {
        if (cell == null) return "unknown";
        if (cell instanceof DoorCell) return ((DoorCell) cell).getRole() + " Door";
        if (cell instanceof ConveyorBelt) return "Conveyor Belt";
        if (cell instanceof ContaminationSock) return "Contamination Sock";
        if (cell instanceof CardCell) return "Card Cell";
        if (cell instanceof MonsterCell) {
            Monster stationed = ((MonsterCell) cell).getCellMonster();
            return stationed == null ? "Monster Cell" : "Monster Cell: " + stationed.getName();
        }
        return cell.getName();
    }

    private String labelFor(Monster monster) {
        if (monster == null) return "MONSTER";
        if (game != null && monster == game.getPlayer()) return "PLAYER " + monster.getName();
        if (game != null && monster == game.getOpponent()) return "OPPONENT " + monster.getName();
        return "STATIONED " + monster.getName();
    }

    private String signed(int value) {
        return value > 0 ? "+" + value : String.valueOf(value);
    }

    private String safeMessage(Exception e) {
        if (e == null) return "Unknown error";
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
    }

    private int safeIntGetter(Monster monster, String methodName) {
        try {
            Object value = monster.getClass().getMethod(methodName).invoke(monster);
            if (value instanceof Integer) return ((Integer) value).intValue();
        } catch (Exception ignored) { }
        return 0;
    }

    private void checkWinnerAndShow() {
        Monster winner = game.getWinner();
        if (winner == null) return;

        stopMusicSafe();
        if (winner == game.getPlayer()) {
            playWinSafe();
        } else {
            playGameOverSafe();
        }

        GameOverView overView = new GameOverView(winner, game.getPlayer(), game.getOpponent());

        final Role replayRole;
        if (lastChosenRole != null) {
            replayRole = lastChosenRole;
        } else if (game.getPlayer() != null) {
            replayRole = game.getPlayer().getOriginalRole();
        } else {
            replayRole = Role.SCARER;
        }

        overView.getTryAgainButton().setOnAction(e -> {
            playClickSafe();
            showGameBoard(replayRole);
        });

        overView.getReturnButton().setOnAction(e -> {
            playClickSafe();
            showMainMenu();
        });

        setScenePreserveFullscreen(createScaledScene(overView.getRoot()));
    }

    private class ActionSnapshot {
        private ArrayList<Monster> monsters = new ArrayList<Monster>();
        private IdentityHashMap<Monster, MonsterState> states = new IdentityHashMap<Monster, MonsterState>();
        private boolean[] doorActivated = new boolean[Constants.BOARD_SIZE];
        private int deckCount;
        private Card topCard;

        private MonsterState get(Monster monster) { return states.get(monster); }
        private Role getRole(Monster monster) {
            MonsterState state = get(monster);
            return state == null ? null : state.role;
        }
    }

    private class MonsterState {
        private String name;
        private String type;
        private Role role;
        private Role originalRole;
        private int energy;
        private int position;
        private boolean frozen;
        private boolean shielded;
        private int confusionTurns;
        private int momentumTurns;
        private int focusTurns;

        private MonsterState(Monster monster) {
            this.name = monster.getName();
            this.type = monster.getClass().getSimpleName();
            this.role = monster.getRole();
            this.originalRole = monster.getOriginalRole();
            this.energy = monster.getEnergy();
            this.position = monster.getPosition();
            this.frozen = monster.isFrozen();
            this.shielded = monster.isShielded();
            this.confusionTurns = monster.getConfusionTurns();
            this.momentumTurns = safeIntGetter(monster, "getMomentumTurns");
            this.focusTurns = safeIntGetter(monster, "getNormalSpeedTurns");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
