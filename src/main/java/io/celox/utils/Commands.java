package io.celox.utils;

/**
 * @author Martin Pfeffer
 *         <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class Commands {

    private static final String XML_HEAD = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

    public static final String CMD_GET_INFO =
            XML_HEAD +
                    "<YAMAHA_AV cmd=\"GET\">" +
                    "<Main_Zone>" +
                    "<Basic_Status>" +
                    "GetParam" +
                    "</Basic_Status>" +
                    "</Main_Zone>" +
                    "</YAMAHA_AV>";

    /**
     * @param volume abs. value (!10)
     */
    public static final String CMD_SET_VOL(int volume) {
        return XML_HEAD +
                "<YAMAHA_AV cmd=\"PUT\">" +
                "<Main_Zone>" +
                "<Volume>" +
                "<Lvl>" +
                "<Val>" +
                String.valueOf(volume) +
                "</Val>" +
                "<Exp>" +
                "1" +
                "</Exp>" +
                "<Unit>" +
                "dB" +
                "</Unit>" +
                "</Lvl>" +
                "</Volume>" +
                "</Main_Zone>" +
                "</YAMAHA_AV>";
    }

    /**
     * @param state "On", "Off"
     */
    public static String CMD_TOGGLE_MUTE(String state) {
        return XML_HEAD +
                "<YAMAHA_AV cmd=\"PUT\">" +
                "<Main_Zone>" +
                "<Volume>" +
                "<Mute>" +
                state +
                "</Mute>" +
                "</Volume>" +
                "</Main_Zone>" +
                "</YAMAHA_AV>";
    }

    /**
     * @param input "AV1", "AV2", "AV3", "AV4",
     *              "DVD", "Spotify", "NET_RADIO", "TUNER",
     *              "USB", "AUX", "AUDIO",
     *              "HDMI1", "HDMI2", "HDMI3", "HDMI4"
     */
    public static String CMD_SELECT_INPUT(String input) {
        return XML_HEAD +
                "<YAMAHA_AV cmd=\"PUT\">" +
                "<Main_Zone>" +
                "<Input>" +
                "<Input_Sel>" +
                input +
                "</Input_Sel>" +
                "</Input>" +
                "</Main_Zone>" +
                "</YAMAHA_AV>";
    }

    /**
     * @param state "On", "Standby"
     */
    public static final String CMD_PWR(String state) {
        return XML_HEAD +
                "<YAMAHA_AV cmd=\"PUT\">" +
                "<Main_Zone>" +
                "<Power_Control>" +
                "<Power>" +
                state +
                "</Power>" +
                "</Power_Control>" +
                "</Main_Zone>" +
                "</YAMAHA_AV>";
    }

    /**
     * @param action "Play", "Pause", "Stop"
     */
    public static final String CMD_ACTION(String action) {
        return XML_HEAD +
                "<YAMAHA_AV cmd=\"PUT\">" +
                "<Main_Zone>" +
                "<Play_Control>" +
                "<Playback>" +
                action +
                "</Playback>" +
                "</Play_Control>" +
                "</Main_Zone>" +
                "</YAMAHA_AV>";
    }

    /**
     * @param what "NET_RADIO",
     */
    public static String CMD_LIST_INFO(String what) {
        return XML_HEAD +
                "<YAMAHA_AV cmd=\"GET\">" +
                "<" + what + ">" +
                "<List_Info>" +
                "GetParam" +
                "</List_Info>" +
                "</" + what + ">" +
                "</YAMAHA_AV>";
    }

    /**
     * @param what "NET_RADIO",
     */
    public static String CMD_LIST_CLICK(String what, int selection) {
        return XML_HEAD +
                "<YAMAHA_AV cmd=\"PUT\">" +
                "<" + what + ">" +
                "<List_Control>" +
                "<Direct_Sel>" +
                "Line_" + String.valueOf(selection) +
                "</Direct_Sel>" +
                "</List_Control>" +
                "</" + what + ">" +
                "</YAMAHA_AV>";
    }

    /**
     * @param what      "NET_RADIO",
     * @param direction "Up", "Down"
     */
    public static String CMD_LIST_DIRECTION(String what, String direction) {
        return XML_HEAD +
                "<YAMAHA_AV cmd=\"PUT\">" +
                "<" + what + ">" +
                "<List_Control>" +
                "<Page>" +
                direction +
                "</Page>" +
                "</List_Control>" +
                "</" + what + ">" +
                "</YAMAHA_AV>";
    }

    /**
     * @param what "NET_RADIO",
     */
    public static String CMD_LIST_RETURN(String what) {
        return XML_HEAD +
                "<YAMAHA_AV cmd=\"PUT\">" +
                "<" + what + ">" +
                "<List_Control>" +
                "<Cursor>" +
                "Return" +
                "</Cursor>" +
                "</List_Control>" +
                "</" + what + ">" +
                "</YAMAHA_AV>";
    }

    /**
     * @param what "NET_RADIO", "Spotify"
     */
    public static String GET_PLAY_INFO(String what) {
        return XML_HEAD +
                "<YAMAHA_AV cmd=\"GET\">" +
                "<" + what + ">" +
                "<Play_Info>" +
                "GetParam" +
                "</Play_Info>" +
                "</" + what + ">" +
                "</YAMAHA_AV>";
    }

    //    /**
    //     * @param action "Skip Rev"
    //     *                <Main_Zone><Play_Control><Plus_Minus><Minus_1>
    //     *                Skip Rev
    //     *                </Minus_1></Plus_Minus></Play_Control></Main_Zone>
    //     *                <p>
    //     *                <p>
    //     *                "Skip Fwd"
    //     *                <Main_Zone><Play_Control><Plus_Minus><Plus_1>
    //     *                Skip Fwd
    //     *                </Plus_1></Plus_Minus></Play_Control></Main_Zone>
    //     */
    //    public static String CMD_PLAY_CONTROLE(String action) {
    //        return XML_HEAD +
    //               "<YAMAHA_AV cmd=\"PUT\">" +
    //               "<Main_Zone>" +
    //               "<Play_Control>" +
    //               "<Plus_Minus>" +
    //               "<Minus_1>" +
    //               "Skip Rev" +
    //               "</Minus_1>" +
    //               "</Plus_Minus>" +
    //               "</Play_Control>" +
    //               "</Main_Zone>" +
    //               "</YAMAHA_AV>";
    //    }
    //
    //
    //    /**
    //     * @param action "Sel", "Up", "Down", "Left", "Right", "Return",
    //     *                "Option", "Display","On Screen", "Up", "Up",
    //     */
    //    public static String CMD_LIST_CONTROL(String action) {
    //        return XML_HEAD +
    //               "<YAMAHA_AV cmd=\"PUT\">" +
    //               "<Main_Zone>" +
    //               "<List_Control>" +
    //               "<Cursor>" +
    //               action +
    //               "</Cursor>" +
    //               "</List_Control>" +
    //               "</Main_Zone>" +
    //               "</YAMAHA_AV>";
    //    }
    //
    //
    //    /**
    //     * @param which "NET_RADIO" <NET_RADIO><Play_Info>GetParam</Play_Info></NET_RADIO>
    //     *               <p>
    //     *               <p>
    //     *               "TUNER" <Tuner><Play_Info>GetParam</Play_Info></Tuner>
    //     */
    //    public static String CMD_RADIO_INFO(String which) {
    //        return XML_HEAD +
    //               "<YAMAHA_AV cmd=\"GET\">" +
    //               "<" + which + ">" +
    //               "<Play_Info>" +
    //               "GetParam" +
    //               "</Play_Info>" +
    //               "</" + which + ">" +
    //               "</YAMAHA_AV>";
    //    }

}
