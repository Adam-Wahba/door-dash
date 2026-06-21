package game.view;

import java.io.File;
import java.io.InputStream;

import game.view.audio.AudioManager;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.util.Duration;

public final class GuiStyle {

    private GuiStyle() { }

    public static String panel() {
        return "-fx-background-color: rgba(7, 18, 32, 0.88);" +
               "-fx-border-color: #23f7ff;" +
               "-fx-border-width: 2;" +
               "-fx-border-radius: 8;" +
               "-fx-background-radius: 8;" +
               "-fx-padding: 10;" +
               "-fx-effect: dropshadow(gaussian, rgba(0, 238, 255, 0.45), 14, 0.35, 0, 0);";
    }

    public static String hudPanel(String theme) {
        String glow = color(theme);
        return "-fx-background-color: linear-gradient(to bottom, rgba(8, 20, 33, 0.96), rgba(3, 7, 18, 0.96));" +
               "-fx-border-color: " + glow + ";" +
               "-fx-border-width: 2;" +
               "-fx-border-radius: 12;" +
               "-fx-background-radius: 12;" +
               "-fx-padding: 10;" +
               "-fx-effect: dropshadow(gaussian, " + glow + ", 10, 0.22, 0, 0);";
    }

    public static String darkPanel() {
        return "-fx-background-color: rgba(4, 8, 20, 0.90);" +
               "-fx-border-color: rgba(35, 247, 255, 0.9);" +
               "-fx-border-width: 2;" +
               "-fx-border-radius: 8;" +
               "-fx-background-radius: 8;" +
               "-fx-padding: 8;";
    }

    public static String statusPanel(String theme) {
        String glow = color(theme);
        return "-fx-background-color: linear-gradient(to bottom, rgba(13, 23, 38, 0.96), rgba(2, 6, 18, 0.96));" +
               "-fx-border-color: " + glow + ";" +
               "-fx-border-width: 2;" +
               "-fx-border-radius: 10;" +
               "-fx-background-radius: 10;" +
               "-fx-padding: 7;" +
               "-fx-effect: dropshadow(gaussian, " + glow + ", 8, 0.20, 0, 0);";
    }

    public static String boardFrame() {
        return "-fx-background-color: rgba(3, 8, 15, 0.72);" +
               "-fx-padding: 8;" +
               "-fx-border-color: rgba(35, 247, 255, 0.86);" +
               "-fx-border-width: 2;" +
               "-fx-border-radius: 14;" +
               "-fx-background-radius: 14;" +
               "-fx-effect: dropshadow(gaussian, rgba(0, 240, 255, 0.45), 16, 0.26, 0, 0);";
    }

    public static String titleText(int size) {
        return "-fx-font-size: " + size + "px;" +
               "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
               "-fx-font-weight: bold;" +
               "-fx-text-fill: white;" +
               "-fx-effect: dropshadow(gaussian, #00f7ff, 12, 0.55, 0, 0);";
    }

    public static String normalText(int size) {
        return "-fx-font-size: " + size + "px;" +
               "-fx-font-family: 'Trebuchet MS', 'Arial';" +
               "-fx-font-weight: bold;" +
               "-fx-text-fill: white;";
    }

    public static String smallInfoText() {
        return "-fx-font-size: 11px;" +
               "-fx-font-family: 'Trebuchet MS', 'Arial Black', 'Arial';" +
               "-fx-font-weight: bold;" +
               "-fx-text-fill: white;";
    }

    public static String button(String top, String bottom) {
        return "-fx-font-size: 13px;" +
               "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
               "-fx-font-weight: bold;" +
               "-fx-text-fill: white;" +
               "-fx-background-color: linear-gradient(to bottom, " + top + ", " + bottom + ");" +
               "-fx-background-radius: 13;" +
               "-fx-border-color: rgba(255,255,255,0.55);" +
               "-fx-border-width: 2;" +
               "-fx-border-radius: 13;" +
               "-fx-cursor: hand;" +
               "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.65), 8, 0.35, 0, 3);";
    }

    public static String glowButton(String theme, boolean hover) {
        String top;
        String bottom;
        String glow = color(theme);

        if ("orange".equals(theme)) {
            top = hover ? "#ffcf55" : "#ffa928";
            bottom = hover ? "#b96200" : "#874400";
        } else if ("purple".equals(theme)) {
            top = hover ? "#b58aff" : "#9d6cff";
            bottom = hover ? "#5e20c8" : "#3e147e";
        } else if ("green".equals(theme)) {
            top = hover ? "#89ff72" : "#53d94b";
            bottom = hover ? "#168a24" : "#0d5c19";
        } else if ("red".equals(theme)) {
            top = hover ? "#ff8585" : "#ff5757";
            bottom = hover ? "#9e1515" : "#6e0d12";
        } else {
            top = hover ? "#65fbff" : "#21f6ff";
            bottom = hover ? "#0b8fa8" : "#075b75";
        }

        int radius = hover ? 16 : 9;
        double spread = hover ? 0.36 : 0.18;

        return "-fx-font-size: 13px;" +
               "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
               "-fx-font-weight: bold;" +
               "-fx-text-fill: white;" +
               "-fx-background-color: linear-gradient(to bottom, " + top + ", " + bottom + ");" +
               "-fx-background-radius: 16;" +
               "-fx-border-color: rgba(255,255,255,0.78);" +
               "-fx-border-width: 2;" +
               "-fx-border-radius: 16;" +
               "-fx-cursor: hand;" +
               "-fx-effect: dropshadow(gaussian, " + glow + ", " + radius + ", " + spread + ", 0, 0);";
    }

    private static String color(String theme) {
        if ("orange".equals(theme)) return "rgba(255, 170, 35, 0.95)";
        if ("purple".equals(theme)) return "rgba(190, 85, 255, 0.95)";
        if ("green".equals(theme)) return "rgba(80, 255, 80, 0.95)";
        if ("red".equals(theme)) return "rgba(255, 70, 80, 0.88)";
        return "rgba(35, 247, 255, 0.95)";
    }


    public static String muteButton(boolean muted) {
        if (muted) {
            return "-fx-background-color: rgba(0,0,0,0.14);" +
                   "-fx-background-radius: 60;" +
                   "-fx-border-color: rgba(255,255,255,0.22);" +
                   "-fx-border-width: 1;" +
                   "-fx-border-radius: 60;" +
                   "-fx-padding: 0;" +
                   "-fx-focus-color: transparent;" +
                   "-fx-faint-focus-color: transparent;" +
                   "-fx-cursor: hand;";
        }
        return transparentButton();
    }

    public static String mutePillButton(boolean muted) {
        String glow = muted ? "rgba(255, 110, 110, 0.90)" : "rgba(35, 247, 255, 0.92)";
        String top = muted ? "rgba(75, 15, 18, 0.96)" : "rgba(8, 50, 62, 0.96)";
        String bottom = muted ? "rgba(25, 4, 7, 0.96)" : "rgba(2, 13, 24, 0.96)";
        return "-fx-font-size: 10px;" +
               "-fx-font-family: 'Impact', 'Arial Black', 'Trebuchet MS', 'Arial';" +
               "-fx-font-weight: bold;" +
               "-fx-text-fill: white;" +
               "-fx-background-color: linear-gradient(to bottom, " + top + ", " + bottom + ");" +
               "-fx-background-radius: 16;" +
               "-fx-border-color: " + glow + ";" +
               "-fx-border-width: 2;" +
               "-fx-border-radius: 16;" +
               "-fx-padding: 0;" +
               "-fx-focus-color: transparent;" +
               "-fx-faint-focus-color: transparent;" +
               "-fx-cursor: hand;" +
               "-fx-effect: dropshadow(gaussian, " + glow + ", 9, 0.25, 0, 0);";
    }

    // Completely invisible clickable area. No cyan rectangle on hover.
    public static String transparentButton() {
        return "-fx-background-color: transparent;" +
               "-fx-border-color: transparent;" +
               "-fx-padding: 0;" +
               "-fx-background-insets: 0;" +
               "-fx-focus-color: transparent;" +
               "-fx-faint-focus-color: transparent;" +
               "-fx-cursor: hand;";
    }

    public static String transparentButtonHover() {
        return transparentButton();
    }

    public static void applyHover(Button button) {
        applyHover(button, null, null, 1.045);
    }

    public static void applyTransparentHover(Button button) {
        applyHover(button, transparentButton(), transparentButton(), 1.025);
    }

    public static void applyGlowHover(final Button button, final String normalStyle, final String hoverStyle) {
        applyHover(button, normalStyle, hoverStyle, 1.055);
    }

    public static void applyHover(final Button button, final String normalStyle, final String hoverStyle, final double hoverScale) {
        if (button == null) return;

        button.setFocusTraversable(false);

        button.setOnMouseEntered(e -> {
            try { AudioManager.playHover(); } catch (Throwable ignored) { }
            if (hoverStyle != null) button.setStyle(hoverStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(95), button);
            st.setToX(hoverScale);
            st.setToY(hoverScale);
            st.play();
        });

        button.setOnMouseExited(e -> {
            if (normalStyle != null) button.setStyle(normalStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(95), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        button.setOnMousePressed(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(65), button);
            st.setToX(0.975);
            st.setToY(0.975);
            st.play();
        });

        button.setOnMouseReleased(e -> {
            if (hoverStyle != null) button.setStyle(hoverStyle);
            ScaleTransition st = new ScaleTransition(Duration.millis(65), button);
            st.setToX(hoverScale);
            st.setToY(hoverScale);
            st.play();
        });
    }

    public static Image loadAssetImage(Class<?> owner, String relativePath) {
        if (relativePath == null) return null;
        String cleanPath = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;

        String[] resourcePaths = {
                "/game/assets/" + cleanPath,
                "game/assets/" + cleanPath,
                "/assets/" + cleanPath
        };

        for (int i = 0; i < resourcePaths.length; i++) {
            try {
                InputStream stream = owner.getResourceAsStream(resourcePaths[i]);
                if (stream != null) return new Image(stream);
            } catch (Exception ignored) { }
        }

        String[] filePaths = {
                "src/game/assets/" + cleanPath,
                "game/assets/" + cleanPath,
                "assets/" + cleanPath
        };

        for (int i = 0; i < filePaths.length; i++) {
            try {
                File file = new File(filePaths[i]);
                if (file.exists()) return new Image(file.toURI().toString());
            } catch (Exception ignored) { }
        }

        return null;
    }
}
