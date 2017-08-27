package io.celox.dialog;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Martin Pfeffer
 *         <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class JfxDialog {

    public JfxDialog(Class c, String title, String message) {
        FXMLLoader loader = new FXMLLoader(c.getResource("/io/celox/dialog/dialog.fxml"));
        try {
            final Parent dialogRoot = loader.load();
            Stage primaryStage = new Stage(StageStyle.UNDECORATED);
            Scene scene = new Scene(dialogRoot);
            primaryStage.setScene(scene);
            primaryStage.show();
            DialogController docController = loader.getController();
            docController.loadDialog(title, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
