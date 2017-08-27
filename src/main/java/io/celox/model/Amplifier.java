package io.celox.model;

import com.pepperonas.jbasx.log.Log;

import io.celox.utils.XmlParser;

/**
 * @author Martin Pfeffer
 *         <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class Amplifier {

    private static final String TAG = "Amplifier";

    public static final String NET_RADIO = "NET_RADIO", SPOTIFY = "Spotify";

    public static final String ON = "On", OFF = "Off", STANDBY = "Standby";

    public static final String PLAY = "Play", STOP = "Stop", PAUSE = "Pause";

    public static final String DOWN = "Down", UP = "Up";

    public static final String ARTIST = "Artist", TRACK = "Track", SONG = "Song", STATION = "Station", ALBUM = "Album";

    public static String[] inputs = {"AV1", "AV2", "AV3", "AV4",
            "DVD", "Spotify", "NET RADIO", "TUNER",
            "USB", "AUX", "AUDIO",
            "HDMI1", "HDMI2", "HDMI3", "HDMI4", "HDMI5", "HDMI6"};

    public void dbgMsg() {
        Log.d(TAG, "dbgMsg " + "LOGGING DEVICE (" + ip + ")\n" +
                "On: " + isOn + "\n" +
                "Mute: " + isMute + "\n" +
                "Standby: " + isInStandby + "\n" +
                "Volume: " + volume + "\n" +
                "Input: " + activeInput + "\n" +
                "Zone: " + activeZone + "\n" +
                "Last Update: " + lastUpdate);
    }

    public static int getInputPosition(String activeInput) {
        int i = 0;
        for (String input : inputs) {
            if (activeInput.equals(input)) return i;
            i++;
        }
        return 0;
    }

    private String ip = "";

    public String getIp() { return ip; }

    public void setIp(String ip) { this.ip = ip; }

    private boolean isOn = false;
    private boolean isMute = false;
    private boolean isInStandby = false;

    private float volume = -400;
    private String activeInput = inputs[0];
    private int activeZone = -1;

    private long lastUpdate = -1;

    public String getActiveInput() {
        return activeInput;
    }

    public void setActiveInput(String activeInput) {
        this.activeInput = activeInput;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setIsOn(boolean isOn) {
        this.isOn = isOn;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setIsMute(boolean isMute) {
        this.isMute = isMute;
    }

    public boolean isInStandby() {
        return isInStandby;
    }

    public void setIsInStandby(boolean isInStandby) {
        this.isInStandby = isInStandby;
    }

    public float getVolume() {
        return volume / 10;
    }

    public void setVolume(float volume) {
        this.volume = volume;
        if (this.volume >= 0) this.volume *= -1;
    }

    public int getActiveZone() {
        return activeZone;
    }

    public void setActiveZone(int activeZone) {
        this.activeZone = activeZone;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void updateAmpVars(StringBuilder response) {
        setLastUpdate(System.currentTimeMillis());
        setIsOn(response.toString().contains("<Power>On"));
        setIsMute(response.toString().contains("<Mute>On"));
        setIsInStandby(response.toString().contains("<Standby_Through_Info>On"));
        setVolume(XmlParser.parseGetVolume(response));
        setActiveInput(response.toString().split("<Input_Sel>")[1].split("<")[0]);
    }

    public String getInputAsXml() {
        if (activeInput.equals("NET RADIO")) {
            return NET_RADIO;
        } else {
            return activeInput;
        }
    }
}
