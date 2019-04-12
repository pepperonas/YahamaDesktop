/*
 * Copyright (c) 2019 kjtech.de - All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 */

package io.celox.utils;

import java.text.DecimalFormat;

/**
 * @author Martin Pfeffer <a href="mailto:martin.pfeffer@kjtech.com">martin.pfeffer@com.kjtech.com</a>, <a
 *         href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 * @see <a href="http://www.kjtech.de">www.com.kjtech.de</a>
 */
public class UtilsFormat {

    private static final String TAG = "UtilsFormat";

    public static String formatVersionCode(int versionCode) {
        String res = String.valueOf(versionCode);
        switch (res.length()) {
            case 1:
                return "0.0." + res;
            case 2:
                return "0." + res.charAt(0) + "." + res.charAt(1);
            case 3:
                return "" + res.charAt(0) + "." + res.charAt(1) + "." + res.charAt(2);
        }
        return "-1";
    }

    public static String formatVelocity(float speedKmh) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return df.format(speedKmh) + " km/h";
    }

    public static String formatKm(float speedKmh) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(3);
        return df.format(speedKmh) + " km";
    }

}
