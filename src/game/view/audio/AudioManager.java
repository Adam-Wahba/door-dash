package game.view.audio;

import java.io.File;
import java.net.URL;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public final class AudioManager {

    private static MediaPlayer musicPlayer;
    private static String currentMusicPath;
    private static boolean musicMuted = false;

    private static AudioClip clickClip;
    private static AudioClip rollClip;
    private static AudioClip powerupClip;
    private static AudioClip cardClip;
    private static AudioClip errorClip;
    private static AudioClip winClip;
    private static AudioClip energyClip;
    private static AudioClip hoverClip;
    private static AudioClip gameOverClip;

    private static boolean clipsLoaded = false;

    private static final double MUSIC_VOLUME = 0.30;
    private static final double SFX_VOLUME = 0.58;
    private static final double HOVER_VOLUME = 0.22;

    private AudioManager() { }

    public static void prepare() {
        loadClipsIfNeeded();
    }

    public static void playMenuMusic() {
        playMusic("audio/music/menu_music.mp3");
    }

    public static void playGameMusic() {
        playMusic("audio/music/game_music.mp3");
    }

    public static void stopMusic() {
        try {
            if (musicPlayer != null) {
                musicPlayer.stop();
            }
        } catch (Throwable ignored) { }
        musicPlayer = null;
        currentMusicPath = null;
    }

    public static boolean toggleMusicMute() {
        musicMuted = !musicMuted;
        applyMusicMuteState();
        return musicMuted;
    }

    public static boolean isMusicMuted() {
        return musicMuted;
    }

    public static void setMusicMuted(boolean muted) {
        musicMuted = muted;
        applyMusicMuteState();
    }

    public static void playClick() {
        playClip(clickClip);
    }

    public static void playRoll() {
        playClip(rollClip);
    }

    public static void playPowerup() {
        playClip(powerupClip);
    }

    public static void playCard() {
        playClip(cardClip);
    }

    public static void playError() {
        playClip(errorClip);
    }

    public static void playWin() {
        playClip(winClip);
    }

    public static void playGameOver() {
        playClip(gameOverClip);
    }

    public static void playEnergy() {
        playClip(energyClip);
    }

    public static void playHover() {
        playClip(hoverClip);
    }

    private static void playMusic(String relativePath) {
        if (relativePath == null) return;

        try {
            String source = findAssetURL(relativePath);
            if (source == null) return;

            if (musicPlayer != null && relativePath.equals(currentMusicPath)) {
                applyMusicMuteState();
                try {
                    musicPlayer.play();
                } catch (Throwable ignored) { }
                return;
            }

            if (musicPlayer != null) {
                try {
                    musicPlayer.stop();
                    musicPlayer.dispose();
                } catch (Throwable ignored) { }
            }

            Media media = new Media(source);
            musicPlayer = new MediaPlayer(media);
            currentMusicPath = relativePath;
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            musicPlayer.setVolume(MUSIC_VOLUME);
            musicPlayer.setMute(musicMuted);
            musicPlayer.play();
        } catch (Throwable ignored) {
            musicPlayer = null;
            currentMusicPath = null;
        }
    }

    private static void applyMusicMuteState() {
        try {
            if (musicPlayer != null) {
                musicPlayer.setMute(musicMuted);
            }
        } catch (Throwable ignored) { }
    }

    private static void loadClipsIfNeeded() {
        if (clipsLoaded) return;
        clipsLoaded = true;

        clickClip = loadClip("audio/sfx/click.wav");
        rollClip = loadClip("audio/sfx/roll.wav");
        powerupClip = loadClip("audio/sfx/powerup.wav");
        cardClip = loadClip("audio/sfx/card.wav");
        errorClip = loadClip("audio/sfx/error.wav");
        winClip = loadClip("audio/sfx/win.wav");
        energyClip = loadClip("audio/sfx/energy.wav");
        hoverClip = loadClip("audio/sfx/hover.wav");
        gameOverClip = loadClip("audio/sfx/gameover.wav");
        if (hoverClip != null) hoverClip.setVolume(HOVER_VOLUME);
    }

    private static AudioClip loadClip(String relativePath) {
        try {
            String source = findAssetURL(relativePath);
            if (source == null) return null;
            AudioClip clip = new AudioClip(source);
            clip.setVolume(SFX_VOLUME);
            return clip;
        } catch (Throwable e) {
            return null;
        }
    }

    private static void playClip(AudioClip clip) {
        loadClipsIfNeeded();
        try {
            if (clip != null) {
                clip.play();
            }
        } catch (Throwable ignored) { }
    }

    private static String findAssetURL(String relativePath) {
        if (relativePath == null) return null;
        String cleanPath = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;

        String[] resourcePaths = {
                "/game/assets/" + cleanPath,
                "game/assets/" + cleanPath,
                "/assets/" + cleanPath
        };

        for (int i = 0; i < resourcePaths.length; i++) {
            try {
                URL url = AudioManager.class.getResource(resourcePaths[i]);
                if (url != null) return url.toExternalForm();
            } catch (Throwable ignored) { }
        }

        String[] filePaths = {
                "src/game/assets/" + cleanPath,
                "game/assets/" + cleanPath,
                "assets/" + cleanPath
        };

        for (int i = 0; i < filePaths.length; i++) {
            try {
                File file = new File(filePaths[i]);
                if (file.exists()) return file.toURI().toString();
            } catch (Throwable ignored) { }
        }

        return null;
    }
}
