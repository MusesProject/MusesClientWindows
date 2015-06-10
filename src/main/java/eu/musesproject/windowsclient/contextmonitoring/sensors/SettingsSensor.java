package eu.musesproject.windowsclient.contextmonitoring.sensors;

/**
 * Created by christophstanik on 6/9/15.
 */
public class SettingsSensor {
    public static String getOSVersion() {
        return System.getProperty("os.name");
    }
}