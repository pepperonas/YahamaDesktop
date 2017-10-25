package io.celox.main;

import com.jfoenix.controls.JFXSlider;

import java.net.URL;
import java.util.ResourceBundle;

import io.celox.dialog.DialogAbout;
import io.celox.settings.Settings;
import io.celox.utils.Commands;
import io.celox.utils.Conversion;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.input.ScrollEvent;
import javafx.scene.text.Text;

/**
 * @author Martin Pfeffer <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class MainController implements Initializable {

    @SuppressWarnings("unused")
    private static final String TAG = "SettingsController";

    public Text tvVolume;
    public JFXSlider sliderVolume;

    private Main mMain;

    void setMain(Main main) { this.mMain = main; }

    public void onVolumeScroll(Event event) {
        ScrollEvent se = (ScrollEvent) event;
        if (se.getDeltaY() > 0) {
            mMain.getVolumeSlider().increment();
        } else {
            mMain.getVolumeSlider().decrement();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sliderVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            float vol = Conversion.roundToHalf(newValue.floatValue());
            if (mMain != null) {
                mMain.execCommand(Commands.CMD_SET_VOL((int) vol * 10));
                tvVolume.setText(String.valueOf(roundToHalf(sliderVolume.valueProperty().doubleValue())) + " dB");
            }
        });

    }

    private static double roundToHalf(double d) {
        return Math.round(d * 2) / 2.0;
    }

    public void onSettings(ActionEvent actionEvent) {
        new Settings(mMain);
    }

    public void onAbout(ActionEvent actionEvent) {
        new DialogAbout(mMain);
    }
}
