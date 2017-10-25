package io.celox.settings;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import io.celox.main.Main;
import io.celox.utils.Const;
import io.celox.utils.Shortcut;
import io.celox.utils.Utils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author Martin Pfeffer <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class Settings {

    @SuppressWarnings("unused")
    private static final String TAG = "Settings";

    private Main mMain;
    private ResourceBundle mRbLang;

    private JFXComboBox choiceBoxVolSteps, choiceBoxRefreshInterval;

    private JFXCheckBox checkBoxPwrOffExit;

    public Settings(Main main) {
        this.mMain = main;
        mRbLang = ResourceBundle.getBundle("LangBundle", Setup.getAppsLocale());

        Stage primaryStage = new Stage(StageStyle.UTILITY);
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/io/celox/settings/settings.fxml"));
        try {
            load(primaryStage, loader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private EventHandler eh = (EventHandler<ActionEvent>) event -> {
        if (event.getSource() instanceof CheckBox) {
            CheckBox chk = (CheckBox) event.getSource();
            Setup.setPwrOffWhenExit(chk.isSelected());
        }
    };

    private void load(Stage primaryStage, FXMLLoader loader) throws java.io.IOException {
        final Parent root = loader.load();
        Scene scene = initScene(primaryStage, root);
        Utils.closeOnEsc(root, scene);
    }

    private Scene initScene(Stage settingsStage, Parent root) {
        Scene scene = new Scene(root, 400, 300);
        settingsStage.setScene(scene);
        settingsStage.show();

        choiceBoxRefreshInterval = (JFXComboBox) root.lookup(Const.ID_CHOICE_BOX_REFRESH_INTERVAL);
        int[] refreshIntervals = {1500, 3000, 5000, 10000, 30000};
        for (int i : refreshIntervals) choiceBoxRefreshInterval.getItems().add(i);
        choiceBoxRefreshInterval.valueProperty().addListener((observable, oldValue, newValue) -> {
            Setup.setAutoRefreshInterval((Integer) choiceBoxRefreshInterval.getValue());
        });
        choiceBoxRefreshInterval.setValue(Setup.getAutoRefreshInterval());

        checkBoxPwrOffExit = (JFXCheckBox) root.lookup(Const.ID_CHECKBOX_PWR_OFF_EXIT);
        checkBoxPwrOffExit.setSelected(Setup.getPwrOffWhenExit());
        checkBoxPwrOffExit.setOnAction(eh);
        checkBoxPwrOffExit.getParent().requestFocus();

        choiceBoxVolSteps = (JFXComboBox) root.lookup(Const.ID_CHOICE_BOX_VOL_STEPS);
        choiceBoxVolSteps.getItems().clear();

        double[] volSteps = {0.5d, 1.0d, 2.5d, 5.0d};
        for (double d : volSteps) choiceBoxVolSteps.getItems().add(d);
        choiceBoxVolSteps.valueProperty().addListener((observable, oldValue, newValue) -> {
            Setup.setVolSteps((Double) choiceBoxVolSteps.getValue());
            mMain.getVolumeSlider().setBlockIncrement(Setup.getVolSteps());
            Shortcut.setShortcutsForMain(mMain.getScene(), mMain.getStage(), mMain);
        });
        choiceBoxVolSteps.setValue(Setup.getVolSteps());

        JFXComboBox<Locale> choiceBoxLanguage = (JFXComboBox) root.lookup(Const.ID_CHOICE_BOX_LANGUAGE);
        choiceBoxLanguage.getItems().clear();
        List<Locale> locales = new ArrayList<>();
        locales.add(Locale.ENGLISH);
        locales.add(Locale.GERMAN);
        choiceBoxLanguage.getItems().addAll(locales);
        choiceBoxLanguage.valueProperty().addListener((observable, oldValue, newValue) -> {
            Setup.setAppsLocale(newValue);
            updateLanguage(settingsStage, root);
        });
        Locale l = Setup.getAppsLocale();
        choiceBoxLanguage.getSelectionModel().select(l);

        JFXTextField textFieldDeviceIp = (JFXTextField) root.lookup(Const.ID_TF_DEVICE_IP);
        textFieldDeviceIp.textProperty().addListener((observable, oldValue, newValue) -> {
            Setup.setAmpIp(newValue);
        });
        String deviceIp = Setup.getAmpIp();
        if (deviceIp != null && !deviceIp.isEmpty()) {
            textFieldDeviceIp.setText(deviceIp);
        }

        return scene;
    }

    private void updateLanguage(Stage stage, Parent root) {
        mRbLang = ResourceBundle.getBundle("LangBundle", Setup.getAppsLocale());

        stage.setTitle(Utils.mkUtf8(mRbLang, "la_settings"));

        Text tvUsability = (Text) root.lookup(Const.ID_TV_USABILITY);
        Text tvLanguage = (Text) root.lookup(Const.ID_TV_LANGUAGE);
        Text tvVolSteps = (Text) root.lookup(Const.ID_TV_VOL_STEPS);
        Text tvRefreshInterval = (Text) root.lookup(Const.ID_TV_REFRESH_INTERVAL);

        tvUsability.setText(Utils.mkUtf8(mRbLang, "usability"));
        tvLanguage.setText(Utils.mkUtf8(mRbLang, "language"));
        tvVolSteps.setText(Utils.mkUtf8(mRbLang, "volume_steps"));
        tvRefreshInterval.setText(Utils.mkUtf8(mRbLang, "refresh_interval"));
        checkBoxPwrOffExit.setText(Utils.mkUtf8(mRbLang, "pwr_off_exit"));

        mMain.updateLanguage();
    }

}
