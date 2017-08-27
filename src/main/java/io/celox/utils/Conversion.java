package io.celox.utils;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * @author Martin Pfeffer
 *         <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class Conversion {

    public static float roundToHalf(float x) {
        return (float) (Math.ceil(x * 2) / 2);
    }

    public static String getValueFromXml(String xmlTag, String param) {
        param = StringEscapeUtils.unescapeHtml4(param);
        return StringEscapeUtils.unescapeXml((param.split("<" + xmlTag + ">")[1]).split("</" + xmlTag)[0]);
    }

}
