package io.celox.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import com.pepperonas.fxaesprefs.FxAesLogger;
import com.pepperonas.fxaesprefs.FxAesPrefs;
import com.pepperonas.jbasx.log.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import io.celox.model.Amplifier;
import io.celox.settings.Setup;
import io.celox.utils.Commands;
import io.celox.utils.Const;
import io.celox.utils.Conversion;
import io.celox.utils.Shortcut;
import io.celox.utils.Utils;
import javafx.animation.FadeTransition;
import javafx.animation.FadeTransitionBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.css.Styleable;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @author Martin Pfeffer
 *         <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class Main extends Application {

    private static final String TAG = "Main";

    private Point2D WINDOW_MIN = new Point2D(600d, 400d);
    private Point2D WINDOW_MAX = new Point2D(WINDOW_MIN.getX() * 2, WINDOW_MIN.getY() * 2);

    private static final String AV_SPECS = "/YamahaRemoteControl/ctrl";
    private static final String PORT = ":80";

    private static final int LIST_OFFSET = 1;

    private long mDeltaLastUpdate = 0;

    private Amplifier mAmp;

    private TimerTask mRefreshGuiTask;
    private String mAmplifierIp;

    private ResourceBundle mRbLang;

    private static Stage stage;
    private static Scene scene;
    private JFXSlider sliderVolume;
    private ToggleButton tglBtnMute;
    private JFXButton btnGetInfo;
    private JFXComboBox choiceBoxInputSelect;
    private Rectangle signalInfoRect;
    private TextArea textAreaPlayInfo;
    private Parent root;
    private Text mDeviceIp;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FxAesPrefs.init(Main.class, "fxaesprefs_config", "123kasd§(U", FxAesLogger.Mode.ALL);

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(
                "/io/celox/main/music_icon/music-icon72.png")));

        mRbLang = ResourceBundle.getBundle("LangBundle", Setup.getAppsLocale());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/io/celox/main/main.fxml"), mRbLang);

        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        root = loader.load(getClass().getResource("/io/celox/main/main.fxml").openStream());
        MainController controller = loader.getController();
        controller.setMain(this);

        mAmp = new Amplifier();
        stage = primaryStage;

        ResourceBundle versionBundle = ResourceBundle.getBundle("app_config");

        stage.setTitle(Const.APP_NAME + " " + versionBundle.getString("version"));
        stage.setScene(scene = new Scene(root, Const.INITIAL_WINDOW_WIDTH_MAIN, Const.INITIAL_WINDOW_HEIGHT_MAIN));
        initWindowSizes(stage);

        stage.show();

        mDeviceIp = (Text) root.lookup(Const.ID_TV_DEVICE_IP);

        initMenuBar(root);

        Shortcut.setShortcutsForMain(stage.getScene(), stage, this);
        initOnCloseAction(stage);

        if (Setup.getAmpIp().isEmpty()) {
            mServiceConnect.start();
        } else {
            mAmplifierIp = Setup.getAmpIp();
            mAmp.setIp(mAmplifierIp);
            startRefreshGuiTask();
        }
    }

    private Service<Void> mServiceConnect = new Service<Void>() {

        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    mAmplifierIp = null;

                    String ip = InetAddress.getLocalHost().getHostAddress();

                    int subnetLength = ip.lastIndexOf(".");
                    String subnet = ip.substring(0, subnetLength);

                    int timeout = Const.TIMEOUT_AMPLIFIER_LOOKUP;
                    int _ip = 1;
                    while (_ip < 255 && mAmplifierIp == null) {
                        String host = subnet + "." + _ip;
                        Log.i(TAG, "call: host=" + host);
                        mDeviceIp.setText(host);
                        if (InetAddress.getByName(host).isReachable(timeout)) {
                            Log.i(TAG, "mServiceConnect");
                            System.out.println("'" + host + "' is reachable");
                            mAmplifierIp = lookupAmplifier(subnet, String.valueOf(_ip), Const.TIMEOUT_AMPLIFIER_LOOKUP);
                        }
                        _ip++;
                    }

                    mAmp.setIp(mAmplifierIp);

                    return null;
                }
            };

        }

        @Override
        protected void succeeded() {
            super.succeeded();

            FxAesPrefs.put("device_ip", mAmplifierIp);

            startRefreshGuiTask();
        }
    };

    private void startRefreshGuiTask() {
        new Timer().schedule(
                mRefreshGuiTask = new TimerTask() {
                    @Override
                    public void run() {
                        initAmplifier();
                        if (mAmp.getInputAsXml().equals(Amplifier.NET_RADIO)) btnGetInfo.fire();
                        if (mAmp.getInputAsXml().equals(Amplifier.SPOTIFY)) btnGetInfo.fire();
                    }
                }, 0, Setup.getAutoRefreshInterval());

        initAmplifier();
        initGui();
    }

    private void initMenuBar(Parent root) {
        MenuBar menubar = (MenuBar) root.lookup(Const.ID_MENUBAR);
        menubar.getMenus().get(Const.MENU_FILE).setText(Utils.mkUtf8(mRbLang, "menuFile"));

        MenuItem exit = menubar.getMenus().get(Const.MENU_FILE).getItems().get(0);
        exit.setText(Utils.mkUtf8(mRbLang, "menuFileExit"));
        exit.setId(Const.ID_MENU_FILE_EXIT);
        exit.setAccelerator(new KeyCharacterCombination("(ESC)"));
        exit.setOnAction(event -> {
            if (event.getSource() instanceof MenuItem) {
                String id = ((Styleable) event.getSource()).getId();
                if (id.equals(Const.ID_MENU_FILE_EXIT)) exitApp();
            }
        });

        menubar.getMenus().get(Const.MENU_HELP).setText(Utils.mkUtf8(mRbLang, "menuHelp"));
        menubar.getMenus().get(Const.MENU_HELP).getItems().get(0).setText(Utils.mkUtf8(mRbLang, "menuHelpAbout"));
    }

    private void initOnCloseAction(Stage stage) {
        stage.setOnCloseRequest(event -> exitApp());
    }

    public void exitApp() {
        System.out.println("exitApp()");

        if (Setup.getPwrOffWhenExit()) {
            execCommand(Commands.CMD_PWR(Amplifier.STANDBY));
        }

        mRefreshGuiTask.cancel();
        Platform.exit();
        System.exit(0);
    }

    private void initWindowSizes(Stage primaryStage) {
        primaryStage.setMinWidth(WINDOW_MIN.getX());
        primaryStage.setMinHeight(WINDOW_MIN.getY());
        primaryStage.setMaxWidth(WINDOW_MAX.getX());
        primaryStage.setMaxHeight(WINDOW_MAX.getY());
    }

    private String lookupAmplifier(String subnet, String address, int timeout) {
        try {
            String amplifierIp = subnet + "." + address;
            URL url = new URL("http://" + amplifierIp + PORT + AV_SPECS);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Accept", "application/xml");
            con.setDoOutput(true);
            con.setConnectTimeout(timeout);

            DataOutputStream dos = new DataOutputStream(con.getOutputStream());
            dos.writeBytes(Commands.CMD_GET_INFO);
            dos.flush();
            dos.close();

            if (con.getResponseCode() == 200) {
                Setup.setAmpIp(amplifierIp);
                return amplifierIp;
            }

            InputStream is = checkConnection(con);

            Log.d(TAG, "lookupAmplifier message='" + con.getResponseMessage() + "' code='" + con.getResponseCode() + "'");

            if (is == null) {
                return null;
            }

            StringBuilder response = collectData(con, is);

            mAmp.updateAmpVars(response);
            mAmp.dbgMsg();

        } catch (Exception e) {
            Log.d(TAG, "lookupAmplifier, wrong IP '" + address + "'");
        }

        return null;
    }

    private StringBuilder collectData(HttpURLConnection con, InputStream is) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) response.append(line);

        rd.close();
        con.disconnect();
        return response;
    }

    private void initAmplifier() {
        if (mAmp == null || mAmp.getIp().isEmpty()) {
            return;
        }

        long sTime = System.currentTimeMillis();

        try {
            HttpURLConnection con = initHttpURLConnection();
            DataOutputStream dos = new DataOutputStream(con.getOutputStream());
            dos.writeBytes(Commands.CMD_GET_INFO);
            dos.flush();
            dos.close();

            InputStream is = checkConnection(con);
            if (is == null) return;

            StringBuilder response = collectData(con, is);

            mAmp.updateAmpVars(response);

            mAmp.dbgMsg();

            Log.i(TAG, "initAmplifier (Re)initAmplifier: " + (System.currentTimeMillis() - sTime + " ms") + "\n"
                    + "response: " + response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            if (sliderVolume != null) sliderVolume.setValue((int) mAmp.getVolume());

            if (tglBtnMute != null) tglBtnMute.setSelected(mAmp.isMute());

            if (choiceBoxInputSelect != null && !choiceBoxInputSelect.isShowing()) {
                choiceBoxInputSelect.setValue(mAmp.getActiveInput());
                choiceBoxInputSelect.getSelectionModel().select(Amplifier.getInputPosition(mAmp.getActiveInput()));
            }

        });

        FadeTransition fadeTransition = getFadeTransition();
        fadeTransition.play();
    }

    private HttpURLConnection initHttpURLConnection() throws IOException {
        URL url = new URL("http://" + mAmp.getIp() + PORT + AV_SPECS);
        com.pepperonas.jbasx.log.Log.d(TAG, "initHttpURLConnection " + url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Accept", "application/xml");
        con.setDoOutput(true);
        return con;
    }

    private InputStream checkConnection(HttpURLConnection con) throws IOException {
        InputStream is = null;
        if (con.getResponseCode() < 300) {
            is = con.getInputStream();
        } else if (con.getResponseCode() < 400) {
            is = con.getInputStream();
        } else if (con.getResponseCode() >= 400) is = con.getErrorStream();
        return is;
    }

    void execCommand(String action) {
        long sTime = System.currentTimeMillis();

        try {
            HttpURLConnection con = initHttpURLConnection();
            DataOutputStream dos = new DataOutputStream(con.getOutputStream());
            dos.writeBytes(action);
            dos.flush();
            dos.close();
            InputStream is = checkConnection(con);
            StringBuilder response = collectData(con, is);

            Log.i(TAG, "execCommand: " + (System.currentTimeMillis() - sTime + " ms") + "\n"
                    + "response: " + response.toString());

            if (response.toString().contains("<" + Amplifier.NET_RADIO + "><List_Info>")
                    && mDeltaLastUpdate < (System.currentTimeMillis() + 10000)) {
                Log.i(TAG, "execCommand Netradio..");
                showRadioInfoInList(response.toString());
            }
            if (response.toString().contains("<" + Amplifier.NET_RADIO + "><Play_Info>")
                    && mDeltaLastUpdate < (System.currentTimeMillis() + 10000)) {
                Log.i(TAG, "execCommand Netradio..");
                showTrackInfoInTextBox(response.toString());
            }
            if (response.toString().contains("<" + Amplifier.SPOTIFY + "><Play_Info>")
                    && mDeltaLastUpdate < (System.currentTimeMillis() + 10000)) {
                Log.i(TAG, "execCommand Spotify...");
                showTrackInfoInTextBox(response.toString());
            }

            FadeTransition fadeTransition = getFadeTransition();
            fadeTransition.play();

        } catch (Exception e) {
            Log.i(TAG, "execCommand: Execution failed ( " + e.getMessage() + ").");
        }

        if (action.contains(Amplifier.NET_RADIO) || action.contains(Amplifier.SPOTIFY)) {
            initAmplifier();
        }
    }

    private void showRadioInfoInList(String info) {
        mDeltaLastUpdate = System.currentTimeMillis();

        String[] params = info.split("<Txt>");

        ObservableList<String> data = FXCollections.observableArrayList();
        for (int i = 1; i < params.length - 1; i++) data.add(params[i].split("<")[0]);

        Platform.runLater(() -> {
            ListView listView = (ListView) scene.lookup(Const.ID_INFO_LIST);
            listView.setItems(data);
            listView.setOnMouseClicked(event -> {
                int clickedIndex = listView.getSelectionModel().getSelectedIndex();
                System.out.println("clicked on " + clickedIndex);
                execCommand(Commands.CMD_LIST_CLICK(mAmp.getInputAsXml(), clickedIndex + LIST_OFFSET));
            });
        });
    }

    private void showTrackInfoInTextBox(String info) {
        mDeltaLastUpdate = System.currentTimeMillis();

        String[] params = info.split("<Meta_Info>");

        if (mAmp.getInputAsXml().equals(Amplifier.SPOTIFY)) {
            Platform.runLater(() -> {
                textAreaPlayInfo.clear();
                textAreaPlayInfo.setText(
                        Utils.mkUtf8(mRbLang, "artist") + ": " + Conversion.getValueFromXml(Amplifier.ARTIST, params[1]) + "\n" +
                                Utils.mkUtf8(mRbLang, "track") + ": " + Conversion.getValueFromXml(Amplifier.TRACK, params[1]) + "\n" +
                                Utils.mkUtf8(mRbLang, "album") + ": " + Conversion.getValueFromXml(Amplifier.ALBUM, params[1]));
            });
        } else if (mAmp.getInputAsXml().equals(Amplifier.NET_RADIO)) {
            String station = Conversion.getValueFromXml(Amplifier.STATION, params[1]);
            String song = Conversion.getValueFromXml(Amplifier.SONG, params[1]);
            String album = Conversion.getValueFromXml(Amplifier.ALBUM, params[1]);
            Platform.runLater(() -> {
                textAreaPlayInfo.clear();
                textAreaPlayInfo.setText(
                        (station.isEmpty() ? "" : Utils.mkUtf8(mRbLang, "station") + ": " + station + "\n") +
                                (song.isEmpty() ? "" : Utils.mkUtf8(mRbLang, "song") + ": " + song + "\n") +
                                (album.isEmpty() ? "" : Utils.mkUtf8(mRbLang, "album") + ": " + album));
            });
        }
    }

    private void initGui() {
        Text tvDeviceIp = (Text) scene.lookup(Const.ID_TV_DEVICE_IP);
        tvDeviceIp.setText(mAmp.getIp());

        JFXToggleButton tglBtnOnOff = (JFXToggleButton) root.lookup(Const.ID_TGL_BTN_PWR);

        if (tglBtnOnOff != null) {
            tglBtnOnOff.setText(Utils.mkUtf8(mRbLang, "tgl_btn_on_off"));
            tglBtnOnOff.setSelected(mAmp.isOn());
            tglBtnOnOff.selectedProperty().addListener((observable, oldValue, newValue) -> execCommand(
                    Commands.CMD_PWR(newValue ? Amplifier.ON : Amplifier.STANDBY)));
        }

        tglBtnMute = (JFXToggleButton) scene.lookup(Const.ID_TGL_BTN_MUTE);

        tglBtnMute.setSelected(mAmp.isMute());
        tglBtnMute.selectedProperty().addListener((observable, oldValue, newValue) -> {
            execCommand(Commands.CMD_TOGGLE_MUTE(newValue ? Amplifier.ON : Amplifier.OFF));
        });

        JFXButton btnVolumeUp = (JFXButton) scene.lookup(Const.ID_BTN_VOL_UP);
        JFXButton btnVolumeDown = (JFXButton) scene.lookup(Const.ID_BTN_VOL_DOWN);

        btnVolumeUp.setOnAction(actionEvent -> sliderVolume.increment());
        btnVolumeDown.setOnAction(actionEvent -> sliderVolume.decrement());

        choiceBoxInputSelect = (JFXComboBox) scene.lookup(Const.ID_CHOICE_BOX_INPUT_SELECT);
        choiceBoxInputSelect.getItems().addAll(Amplifier.inputs);
        choiceBoxInputSelect.valueProperty().addListener((observable, oldValue, newValue) -> {
            mAmp.setActiveInput((String) newValue);
            execCommand(Commands.CMD_SELECT_INPUT((String) newValue));
        });

        sliderVolume = (JFXSlider) scene.lookup(Const.ID_SLIDER);
        sliderVolume.disableProperty().bind(tglBtnMute.selectedProperty());
        btnVolumeUp.disableProperty().bind(tglBtnMute.selectedProperty());
        btnVolumeDown.disableProperty().bind(tglBtnMute.selectedProperty());

        btnGetInfo = (JFXButton) scene.lookup(Const.ID_BTN_GET_INFO);
        btnGetInfo.setText(Utils.mkUtf8(mRbLang, "btn_get_info"));
        btnGetInfo.setOnAction(event -> {
            if (mAmp.getInputAsXml().equals(Amplifier.NET_RADIO)) {
                execCommand(Commands.CMD_LIST_INFO(mAmp.getInputAsXml()));
                execCommand(Commands.GET_PLAY_INFO(Amplifier.NET_RADIO));
            }
            if (mAmp.getInputAsXml().equals(Amplifier.SPOTIFY)) {
                execCommand(Commands.GET_PLAY_INFO(Amplifier.SPOTIFY));
            }
        });

        JFXButton btnPrev = (JFXButton) scene.lookup(Const.ID_BTN_PREV_PAGE);
        btnPrev.setOnAction(event -> execCommand(Commands.CMD_LIST_DIRECTION(mAmp.getInputAsXml(), Amplifier.DOWN)));

        JFXButton btnNext = (JFXButton) scene.lookup(Const.ID_BTN_NEXT_PAGE);
        btnNext.setOnAction(event -> execCommand(Commands.CMD_LIST_DIRECTION(mAmp.getInputAsXml(), Amplifier.UP)));

        JFXButton btnStop = (JFXButton) scene.lookup(Const.ID_BTN_STOP);
        btnStop.setOnAction(event -> execCommand(Commands.CMD_ACTION(Amplifier.STOP)));

        JFXButton btnPlay = (JFXButton) scene.lookup(Const.ID_BTN_PLAY);
        btnPlay.setOnAction(event -> execCommand(Commands.CMD_ACTION(Amplifier.PLAY)));

        JFXButton btnOk = (JFXButton) scene.lookup(Const.ID_BTN_OK);
        btnOk.setDisable(true);

        JFXButton btnListReturn = (JFXButton) scene.lookup(Const.ID_BTN_LIST_RETURN);
        btnListReturn.setText(Utils.mkUtf8(mRbLang, "btn_list_return"));
        btnListReturn.setOnAction(event -> execCommand(Commands.CMD_LIST_RETURN(mAmp.getInputAsXml())));

        textAreaPlayInfo = (TextArea) scene.lookup(Const.ID_TEXTAREA_PLAY_INFO);
        textAreaPlayInfo.setWrapText(true);

        signalInfoRect = (Rectangle) scene.lookup(Const.ID_SIGNAL_INFO_RECT);

        root.requestFocus();
    }

    public void updateLanguage() {
        mRbLang = ResourceBundle.getBundle("bundles.LangBundle", Setup.getAppsLocale());

        JFXButton btnSettings = (JFXButton) scene.lookup(Const.ID_BTN_OPEN_SETTINGS);
        btnSettings.setText(Utils.mkUtf8(mRbLang, "la_settings"));

        JFXButton btnGetInfo = (JFXButton) scene.lookup(Const.ID_BTN_GET_INFO);
        btnGetInfo.setText(Utils.mkUtf8(mRbLang, "btn_get_info"));

        JFXToggleButton tglBtnPwr = (JFXToggleButton) scene.lookup(Const.ID_TGL_BTN_PWR);
        tglBtnPwr.setText(Utils.mkUtf8(mRbLang, "tgl_btn_on_off"));

        initMenuBar(scene.getRoot());
    }

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }

    public Slider getVolumeSlider() {
        return sliderVolume;
    }

    private FadeTransition getFadeTransition() {
        return FadeTransitionBuilder.create().duration(Duration.millis(Setup.getAutoRefreshInterval())).node(signalInfoRect)
                .fromValue(1).toValue(0).cycleCount(0).autoReverse(true).build();
    }

}
