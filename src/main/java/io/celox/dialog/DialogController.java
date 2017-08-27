package io.celox.dialog;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author Martin Pfeffer
 *         <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class DialogController implements Initializable {

    @FXML
    StackPane stackPane;

    @FXML
    public JFXDialog dialog;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void loadDialog(String title, String message) {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();

        Text dialogTitle = new Text(title);
        dialogTitle.setStyle("-fx-font-size: 20.0px;" +
                "-fx-font-weight: BOLD;" +
                "-fx-alignment: center-left;" +
                "-fx-padding: 5.0 0.0 5.0 0.0;");
        dialogLayout.setHeading(dialogTitle);

        Text dialogMessage = new Text(message);
        dialogMessage.setStyle("-fx-font-size: 14.0px;" +
                "-fx-pref-width: 400.0px;" +
                "-fx-wrap-text: true;");
        dialogLayout.setBody(dialogMessage);

        JFXButton acceptButton = new JFXButton("CLOSE");
        acceptButton.setStyle("-fx-font-weight: BOLD;" +
                "-fx-padding: 0.7em 0.8em;");
        dialogLayout.setActions(acceptButton);

        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        dialog.setContent(dialogLayout);

        dialog.show(stackPane);
        dialog.getScene().getWindow().sizeToScene();
        Stage stage = (Stage) dialog.getScene().getWindow();
        stage.setAlwaysOnTop(true);

        acceptButton.setOnAction(event -> {
            dialog.close();
            stage.close();
        });
    }

}
