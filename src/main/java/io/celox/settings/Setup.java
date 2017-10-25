package io.celox.settings;

import com.pepperonas.fxaesprefs.Log;

import java.util.Locale;
import java.util.prefs.Preferences;

/**
 * @author Martin Pfeffer <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class Setup {

    @SuppressWarnings("unused")
    private static final String TAG = "Setup";

    public static boolean getPwrOffWhenExit() {
        return Preferences.userNodeForPackage(Setup.class).getBoolean("pwr_off_ext", false);
    }

    public static void setPwrOffWhenExit(boolean b) {
        Preferences.userNodeForPackage(Setup.class).putBoolean("pwr_off_ext", b);
    }

    public static void setAppsLocale(Locale locale) {
        String loc = locale.getLanguage();
        Log.i(TAG, "setAppsLocale: " + loc);
        Preferences.userNodeForPackage(Setup.class).put("users_locale", locale.getLanguage());
    }

    public static Locale getAppsLocale() {
        Preferences prefs = Preferences.userNodeForPackage(Setup.class);
        if (!prefs.get("users_locale", "en").isEmpty()) {
            return new Locale(prefs.get("users_locale", "en"));
        }
        return new Locale(System.getProperty("user.language"));
    }

    public static void setVolSteps(double value) {
        Preferences.userNodeForPackage(Setup.class).putDouble("vol_steps", value);
    }

    public static double getVolSteps() {
        return Preferences.userNodeForPackage(Setup.class).getDouble("vol_steps", 2.5d);
    }

    public static void setAutoRefreshInterval(int value) {
        Preferences.userNodeForPackage(Setup.class).putInt("refresh_interval", value);
    }

    public static int getAutoRefreshInterval() {
        return Preferences.userNodeForPackage(Setup.class).getInt("refresh_interval", 1500);
    }

    public static void setAmpIp(String amplifierIp) {
        Preferences.userNodeForPackage(Setup.class).put("amplifier_ip", amplifierIp);
    }

    public static String getAmpIp() {
        return Preferences.userNodeForPackage(Setup.class).get("amplifier_ip", "");
    }
}
