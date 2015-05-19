package eu.musesproject.windowsclient.contextmonitoring;

import java.io.IOException;

/**
 * Created by Saeed on 5/18/2015.
 */
public class SensorsMain {
    public static void main(String[] args) {
        try {
            SensorController.getInstance().startSensors();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
