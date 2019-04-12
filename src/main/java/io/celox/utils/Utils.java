package io.celox.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.ResourceBundle;

import io.celox.settings.Setup;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * @author Martin Pfeffer <a href="mailto:martin.pfeffer@celox.io">martin.pfeffer@celox.io</a>
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class Utils {

    public static void closeOnEsc(Parent root, Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, t -> {
            if (t.getCode() == KeyCode.ESCAPE) {
                System.out.println("-ESC- pressed, closing stage...");
                Stage sb = (Stage) root.getScene().getWindow();
                sb.close();
            }
        });
    }

    public static ResourceBundle getUTFResourceBundle() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ResourceBundle bundle = null;
        Utf8Control control = new Utf8Control();
        try {
            bundle = control.newBundle("bundles.LangBundle", Setup.getAppsLocale(), "", classLoader, false);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    public static String mkUtf8(ResourceBundle rb, String key) {
        String val = rb.getString(key);
        try {
            return new String(val.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "rnf";
    }

    public static int getVersionFromProperties(Class clazz) {
        InputStream in = null;
        try {
            in = clazz.getResource("/io/celox/main/app.properties").openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Properties props = new Properties();
        try {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = (String) props.get("version");
        return Integer.parseInt(s);
    }

//    public static String getAppNameFromProperties(Class clazz) {
//        InputStream in = clazz.getResourceAsStream("app.properties");
//        Properties props = new Properties();
//        try {
//            props.load(in);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return (String) props.get("app_name");
//    }

    public static String getAppNameFromProperties(Class clazz) {
        InputStream in = null;
        try {
            in = clazz.getResource("/io/celox/main/app.properties").openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Properties props = new Properties();
        try {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (String) props.get("app_name");
    }

}
