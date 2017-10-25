package io.celox.dialog;

import com.pepperonas.jbasx.log.Log;

import io.celox.main.Main;
import io.celox.utils.Utils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Martin Pfeffer <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class DialogAbout {

    private static final String TAG = "DialogAbout";

    @SuppressWarnings("UnusedAssignment")
    public DialogAbout(Main main) {
        Stage dialogStage = new Stage(StageStyle.UTILITY);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setWidth(400);
        dialogStage.setHeight(220);
        dialogStage.setResizable(false);

        BorderPane borderPane = new BorderPane();

        borderPane.setPadding(new Insets(15));
        borderPane.setPrefWidth(Integer.MAX_VALUE);
        borderPane.setPrefHeight(Integer.MAX_VALUE);

        Scene scene = new Scene(borderPane);
        dialogStage.setScene(scene);
        dialogStage.show();
        Utils.closeOnEsc(borderPane, scene);


        // Top
        Label textTitle = new Label("ABOUT");
        textTitle.setStyle("-fx-font-size: 18px;");

        HBox hBoxTop = new HBox(0);
        hBoxTop.getChildren().addAll(textTitle);
        borderPane.setTop(hBoxTop);

        // Center
        Label textMsg = new Label("Created by Martin Pfeffer (2015-2017)");
        textMsg.setStyle("-fx-font-size: 14px;");

        GridPane grid = new GridPane();
        grid.setHgap(10);

        int row = 0;
        Hyperlink linkWebsite = new Hyperlink("https://celox.io");
        grid.add(new Text("Website:"), 0, row);
        grid.add(linkWebsite, 1, row++);

        Hyperlink linkGithub = new Hyperlink("https://github.com/pepperonas");
        grid.add(new Text("Github:"), 0, row);
        grid.add(linkGithub, 1, row++);

        Hyperlink linkMail = new Hyperlink("martin.pfeffer@celox.io");
        grid.add(new Text("Mail:"), 0, row);
        grid.add(linkMail, 1, row++);

        setClickHandler(main, linkWebsite, linkGithub, linkMail);
        styleHyperlink(linkWebsite, linkGithub, linkMail);
        grid.setStyle("-fx-font-size: 14px;");

        HBox hBoxInputPane = new HBox(10);
        hBoxInputPane.setAlignment(Pos.CENTER);

        VBox vBoxCenter = new VBox(10);
        vBoxCenter.setPadding(new Insets(15, 0, 15, 0));

        VBox vBoxCenterSub = new VBox(0, grid);
        vBoxCenter.getChildren().addAll(textMsg, vBoxCenterSub);
        borderPane.setCenter(vBoxCenter);

        // Bottom
        HBox hBoxBottom = new HBox();
        final Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        hBoxBottom.getChildren().addAll(spacer);
        borderPane.setBottom(hBoxBottom);
    }

    private void setClickHandler(Main main, Hyperlink... hyperlinks) {
        for (Hyperlink hyperlink : hyperlinks) {
            hyperlink.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
                Log.i(TAG, "handle: link clicked");
                if (!hyperlink.getText().contains("@")) {
                    main.getHostServices().showDocument(hyperlink.getText());
                } else if (hyperlink.getText().equals("martin.pfeffer@celox.io")) {
                    main.getHostServices().showDocument("mailto:" + hyperlink.getText());
                }
            });
        }
    }

    private void styleHyperlink(Hyperlink... hyperlinks) {
        for (Hyperlink hyperlink : hyperlinks) {
            hyperlink.setStyle("-fx-border-color: transparent; -fx-text-fill: #009688");
        }
    }
}
