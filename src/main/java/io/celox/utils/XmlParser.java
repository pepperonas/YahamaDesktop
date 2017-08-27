package io.celox.utils;

/**
 * @author Martin Pfeffer
 *         <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class XmlParser {

    public static int parseGetVolume(StringBuilder response) {
        String s[] = response.toString().split("<Lvl><Val>");
        return Integer.parseInt(s[1].split("</Val>")[0].replace("-", ""));
    }
}
