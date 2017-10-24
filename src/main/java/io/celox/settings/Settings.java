package io.celox.settings;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.pepperonas.jbasx.log.Log;

import java.io.IOException;
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

    private static final String TAG = "Settings";

    private Main mMain;
    private ResourceBundle mRbLang;

    private JFXComboBox choiceBoxLanguage, choiceBoxVolSteps, choiceBoxRefreshInterval;

    private JFXCheckBox checkBoxPwrOffExit;

    private JFXTextField textFieldDeviceIp;

    public Settings(Main main) {
        this.mMain = main;

        mRbLang = ResourceBundle.getBundle("LangBundle", Setup.getAppsLocale());

        Log.i(TAG, "Settings: " + Utils.mkUtf8(mRbLang, "menuHelp"));

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

        double[] volSteps = {2.5d, 5.0d, 10.0d};
        for (double d : volSteps) choiceBoxVolSteps.getItems().add(d);
        choiceBoxVolSteps.valueProperty().addListener((observable, oldValue, newValue) -> {
            Setup.setVolSteps((Double) choiceBoxVolSteps.getValue());
            mMain.getVolumeSlider().setBlockIncrement(Setup.getVolSteps());
            Shortcut.setShortcutsForMain(mMain.getScene(), mMain.getStage(), mMain);
        });
        choiceBoxVolSteps.setValue(Setup.getVolSteps());

        choiceBoxLanguage = (JFXComboBox) root.lookup(Const.ID_CHOICE_BOX_LANGUAGE);
        choiceBoxLanguage.getItems().clear();
        String[] locales = mRbLang.getString("la_languages").split(",");
        int i = 0, selectedLang = -1;
        for (String s : locales) {
            if (s.equals(Setup.getAppsLocale().getLanguage())) selectedLang = i;
            //            Locale tmpLocale = LocaleUtils.toLocale(s);
            choiceBoxLanguage.getItems().addAll(s);
            i++;
        }

        choiceBoxLanguage.valueProperty().addListener((observable, oldValue, newValue) -> {
            int selected = choiceBoxLanguage.getSelectionModel().getSelectedIndex();
            Locale locale = new Locale(locales[selected]);
            Setup.setAppsLocale(locale);
            updateLanguage(settingsStage, root);
        });
        choiceBoxLanguage.getSelectionModel().select(selectedLang);

        textFieldDeviceIp = (JFXTextField) root.lookup("#textFieldDeviceIp");
        textFieldDeviceIp.textProperty().addListener((observable, oldValue, newValue) -> {
            Log.i(TAG, "changed: " + newValue);
            Setup.setAmpIp(newValue);
        });

        return scene;
    }

    private void updateLanguage(Stage stage, Parent root) {
        mRbLang = ResourceBundle.getBundle("LangBundle", Setup.getAppsLocale());

        stage.setTitle(Utils.mkUtf8(mRbLang, "la_settings"));

        Text tvUsability = (Text) root.lookup(Const.ID_TV_USABILITY);
        Text tvLookAndFeel = (Text) root.lookup(Const.ID_TV_LOOK_AND_FEEL);
        Text tvLanguage = (Text) root.lookup(Const.ID_TV_LANGUAGE);
        Text tvVolSteps = (Text) root.lookup(Const.ID_TV_VOL_STEPS);
        Text tvRefreshInterval = (Text) root.lookup(Const.ID_TV_REFRESH_INTERVAL);

        tvUsability.setText(Utils.mkUtf8(mRbLang, "usability"));
        tvLookAndFeel.setText(Utils.mkUtf8(mRbLang, "look_and_feel"));
        tvLanguage.setText(Utils.mkUtf8(mRbLang, "language"));
        tvVolSteps.setText(Utils.mkUtf8(mRbLang, "volume_steps"));
        tvRefreshInterval.setText(Utils.mkUtf8(mRbLang, "refresh_interval"));
        checkBoxPwrOffExit.setText(Utils.mkUtf8(mRbLang, "pwr_off_exit"));

        mMain.updateLanguage();

    }

}
