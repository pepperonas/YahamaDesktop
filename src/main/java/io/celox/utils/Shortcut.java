package io.celox.utils;

import io.celox.main.Main;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * @author Martin Pfeffer
 *         <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class Shortcut {

    public static void setShortcutsForMain(Scene scene, Stage stage, Main main) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.close();
                main.exitApp();
            }
            if (event.getCode() == KeyCode.PLUS || event.getCode() == KeyCode.ADD) {
                main.getVolumeSlider().increment();
            }
            if (event.getCode() == KeyCode.MINUS || event.getCode() == KeyCode.SUBTRACT) {
                main.getVolumeSlider().decrement();
            }
        });
    }

}
