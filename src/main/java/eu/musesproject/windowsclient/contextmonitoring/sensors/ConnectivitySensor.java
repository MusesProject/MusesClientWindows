package eu.musesproject.windowsclient.contextmonitoring.sensors;

/*
 * #%L
 * musesclient
 * %%
 * Copyright (C) 2013 - 2014 HITEC
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import eu.musesproject.windowsclient.contextmonitoring.ContextListener;
import eu.musesproject.client.model.contextmonitoring.BluetoothState;
import eu.musesproject.contextmodel.ContextEvent;
import org.apache.http.util.TextUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author alirezaalizadeh
 *
 * Class to collect information about the connection of
 * the device
 */

public class ConnectivitySensor implements ISensor {
    private static final String TAG = ConnectivitySensor.class.getSimpleName();

    // time in seconds when the sensor polls information
    private static final long OBSERVATION_INTERVALL = TimeUnit.SECONDS.toMillis(10);

    // sensor identifier
    public static final String TYPE = "CONTEXT_SENSOR_CONNECTIVITY";

    // context property keys
    public static final String PROPERTY_KEY_ID 				  	= "id";
    public static final String PROPERTY_KEY_DEVICE_CONNECTED   	= "deviceconnected"; //changed MOBILE to DEVICE
    public static final String PROPERTY_KEY_WIFI_ENABLED 	  	= "wifienabled";
    public static final String PROPERTY_KEY_WIFI_CONNECTED 	  	= "wificonnected";
    public static final String PROPERTY_KEY_WIFI_NEIGHBORS 	  	= "wifineighbors";
    public static final String PROPERTY_KEY_HIDDEN_SSID 		= "hiddenssid";
    public static final String PROPERTY_KEY_BSSID 			  	= "bssid";
    public static final String PROPERTY_KEY_NETWORK_ID 		  	= "networkid";
    public static final String PROPERTY_KEY_NETWORK_NAME 		= "networkname";
    public static final String PROPERTY_KEY_ETHERNET_CONNECTED	= "ethernetconnected";
    public static final String PROPERTY_KEY_BLUETOOTH_CONNECTED	= "bluetoothconnected";
    public static final String PROPERTY_KEY_AIRPLANE_MODE 	  	= "airplanemode";
    public static final String PROPERTY_WIFI_ENCRYPTION         = "wifiencryption";
    public static final String PROPERTY_KEY_CONNECTED_TO_TRUSTED_IP_RANGE         = "connectedtotrustediprange";
    public static final String PROPERTY_KEY_IP_ADDRESS          = "ipaddress";

    // application context
    private ContextListener listener;

    // stores all fired context events of this sensor
    private List<ContextEvent> contextEventHistory;


    // enable and disable sensor
    private boolean sensorEnabled;


    public ConnectivitySensor() {
        init();
    }

    // initializes all necessary default values
    private void init() {
        sensorEnabled = false;
        contextEventHistory = new ArrayList<ContextEvent>(CONTEXT_EVENT_HISTORY_SIZE);
    }


    @Override
    public void enable() {
        if (!sensorEnabled) {
            //Log.d(TAG, "start connectivity tracking");
            sensorEnabled = true;
            new ConnectivityObserver().backgroundProcess.start();
        }
    }

    @Override
    public void disable() {
        if(sensorEnabled) {
            //Log.d(TAG, "stop connectivity tracking");
            sensorEnabled = false;
        }
    }

    /** adds the context event to the context event history */
    private void createContextEvent(ContextEvent contextEvent) {
    	contextEvent.generateId();
        //Log.d(TAG, "Connectivity  - context event created");

        // add context event to the context event history
        contextEventHistory.add(contextEvent);
        if(contextEventHistory.size() > CONTEXT_EVENT_HISTORY_SIZE) {
            contextEventHistory.remove(0);
        }

        if(listener != null) {
            listener.onEvent(contextEvent);
        }
    }

    private boolean identicalContextEvent(ContextEvent oldEvent, ContextEvent newEvent) {
        if(oldEvent.getProperties().size() != newEvent.getProperties().size()) {
            return false;
        }

        // compare property values
        Set<String> oldValues = new HashSet<String>(oldEvent.getProperties().values());
        Set<String> newValues = new HashSet<String>(newEvent.getProperties().values());

        return oldValues.equals(newValues);
    }

    /**
     * Observes the windows desktop device's connectivity status. Creates a context event whenever a the connectivity status changes.
     */
	public class ConnectivityObserver  {
        public Thread backgroundProcess = new Thread(){
            public void run() {
                try {
                    doInBackground();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        private Void doInBackground() throws IOException {
            while (sensorEnabled) {

                Map<String, String> sensorInfo = RESTController.requestSensorInfo(TYPE);

                // WiFi status
                int id = contextEventHistory != null ? (contextEventHistory.size() + 1) : - 1;

                ContextEvent contextEvent = new ContextEvent();
                contextEvent.setType(TYPE);
                contextEvent.setTimestamp(System.currentTimeMillis());
                contextEvent.addProperty(PROPERTY_KEY_ID, String.valueOf(id));
                contextEvent.addProperty(PROPERTY_KEY_DEVICE_CONNECTED, String.valueOf(sensorInfo.get(PROPERTY_KEY_DEVICE_CONNECTED)));
                contextEvent.addProperty(PROPERTY_KEY_WIFI_ENABLED, String.valueOf(sensorInfo.get(PROPERTY_KEY_WIFI_ENABLED)));
                contextEvent.addProperty(PROPERTY_KEY_WIFI_CONNECTED, String.valueOf(sensorInfo.get(PROPERTY_KEY_WIFI_CONNECTED)));

                // wifi encryption status
                contextEvent.addProperty(PROPERTY_WIFI_ENCRYPTION, sensorInfo.get(PROPERTY_WIFI_ENCRYPTION));

                contextEvent.addProperty(PROPERTY_KEY_BSSID, sensorInfo.get(PROPERTY_KEY_BSSID));
                contextEvent.addProperty(PROPERTY_KEY_NETWORK_ID, sensorInfo.get(PROPERTY_KEY_NETWORK_ID));
                contextEvent.addProperty(PROPERTY_KEY_NETWORK_NAME, sensorInfo.get(PROPERTY_KEY_NETWORK_NAME));

                contextEvent.addProperty(PROPERTY_KEY_ETHERNET_CONNECTED, String.valueOf(sensorInfo.get(PROPERTY_KEY_ETHERNET_CONNECTED)));
                contextEvent.addProperty(PROPERTY_KEY_CONNECTED_TO_TRUSTED_IP_RANGE, String.valueOf(sensorInfo.get(PROPERTY_KEY_CONNECTED_TO_TRUSTED_IP_RANGE)));
                contextEvent.addProperty(PROPERTY_KEY_IP_ADDRESS, sensorInfo.get(PROPERTY_KEY_IP_ADDRESS));

                //contextEvent.addProperty(PROPERTY_KEY_WIFI_NEIGHBORS, String.valueOf(wifiManager.getScanResults().size()));
                //contextEvent.addProperty(PROPERTY_KEY_HIDDEN_SSID, String.valueOf(wifiInfo.getHiddenSSID()));
                //contextEvent.addProperty(PROPERTY_KEY_BLUETOOTH_CONNECTED,String.valueOf(bluetoothState));
                //contextEvent.addProperty(PROPERTY_KEY_AIRPLANE_MODE, String.valueOf(airplaneMode));

                // check if something has changed. If something changed fire a context event, do nothing otherwise.
                int connectivityContextListSize = contextEventHistory.size();
                if(connectivityContextListSize > 0) {
                    ContextEvent previousContext = contextEventHistory.get(connectivityContextListSize - 1);
                    // fire new context event if a connectivity context field changed
                    if(!identicalContextEvent(previousContext, contextEvent)) {
                        createContextEvent(contextEvent);
                    }
                }
                else {
                    createContextEvent(contextEvent);
                }

                try {
                    Thread.sleep(OBSERVATION_INTERVALL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    @Override
    public void addContextListener(ContextListener listener) {
        this.listener = listener;
    }

    @Override
    public void removeContextListener(ContextListener listener) {
        this.listener = listener;
    }

    @Override
    public ContextEvent getLastFiredContextEvent() {
        if(contextEventHistory.size() > 0) {
            return contextEventHistory.get(contextEventHistory.size() - 1);
        }
        else {
            return null;
        }
    }

//	@Override
//	public void configure(List<SensorConfiguration> config) {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public String getSensorType() {
		return TYPE;
	}
}