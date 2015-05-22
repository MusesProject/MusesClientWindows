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

//import eu.musesproject.client.contextmonitoring.ContextListener;
//import eu.musesproject.client.db.entity.SensorConfiguration;
//import eu.musesproject.client.db.handler.DBManager;
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.windowsclient.contextmonitoring.ContextListener;
import org.apache.http.conn.util.InetAddressUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * @author alirezaalizadeh
 *
 *         Class to collect information about the device configuration
 */

public class DeviceProtectionSensor implements ISensor {
	private static final String TAG = DeviceProtectionSensor.class.getSimpleName();

	// sensor identifier
	public static final String TYPE = "CONTEXT_SENSOR_DEVICE_PROTECTION";

	// time in milliseconds when the sensor polls information
	private static int OBSERVATION_INTERVALL = 1000;

	// context property keys
	public static final String PROPERTY_KEY_ID 							    = "id";
    public static final String PROPERTY_KEY_IS_PASSWORD_PROTECTED 		    = "ispasswordprotected";
    public static final String PROPERTY_KEY_IS_TRUSTED_AV_INSTALLED 	    = "istrustedantivirusinstalled";
    public static final String PROPERTY_KEY_SCREEN_TIMEOUT_IN_SECONDS 	    = "screentimeoutinseconds";
    public static final String PROPERTY_KEY_IS_SCREEN_LOCKED         	    = "isscreanlocked";
	//public static final String PROPERTY_KEY_IS_ROOTED 					= "isrooted";
	//public static final String PROPERTY_KEY_IS_ROOT_PERMISSION_GIVEN 	    = "isrootpermissiongiven";
	//public static final String PROPERTY_KEY_IP_ADRESS 					= "ipaddress";
	//public static final String PROPERTY_KEY_MUSES_DATABASE_EXISTS 		= "musesdatabaseexists";
	//public static final String PROPERTY_KEY_ACCESSIBILITY_ENABLED 		= "accessibilityenabled";

	// config keys
	public static final String CONFIG_KEY_TRUSTED_AV = "trustedav";

	private ContextListener listener;

	// history of fired context events
	List<ContextEvent> contextEventHistory;

	// holds a value that indicates if the sensor is enabled or disabled
	private boolean sensorEnabled;

	// list with names of trusted anti virus applications
	private List<String> trustedAVs;

	public DeviceProtectionSensor() {
		contextEventHistory = new ArrayList<ContextEvent>(CONTEXT_EVENT_HISTORY_SIZE);
		init();
	}

	private void init() {
		sensorEnabled = false;

		trustedAVs = new ArrayList<String>();
	}

	@Override
	public void enable() {
		if (!sensorEnabled) {
			sensorEnabled = true;

			new CreateContextEventAsync().backgroundProcess.start();
		}
	}

	private void createContextEvent() throws IOException {
        // request for sensor information from sensors REST service
        Map<String, String> sensorInfo = RESTController.requestSensorInfo(TYPE);
        // create context event
		ContextEvent contextEvent = new ContextEvent();
		contextEvent.setType(TYPE);
		contextEvent.setTimestamp(System.currentTimeMillis());
		contextEvent.addProperty(PROPERTY_KEY_ID, String.valueOf(contextEventHistory != null ? (contextEventHistory .size() + 1) : -1));
        contextEvent.addProperty(PROPERTY_KEY_IS_PASSWORD_PROTECTED, String.valueOf(sensorInfo.get(PROPERTY_KEY_IS_PASSWORD_PROTECTED)));
        contextEvent.addProperty(PROPERTY_KEY_IS_TRUSTED_AV_INSTALLED, String.valueOf(sensorInfo.get(PROPERTY_KEY_IS_TRUSTED_AV_INSTALLED)));
        contextEvent.addProperty(PROPERTY_KEY_IS_SCREEN_LOCKED, String.valueOf(sensorInfo.get(PROPERTY_KEY_IS_SCREEN_LOCKED)));

//      contextEvent.addProperty(PROPERTY_KEY_IS_ROOTED, String.valueOf(checkDeviceRooted()));
//		contextEvent.addProperty(PROPERTY_KEY_IS_ROOT_PERMISSION_GIVEN, String.valueOf(checkRootPermissionGiven()));
//		contextEvent.addProperty(PROPERTY_KEY_IP_ADRESS, getIPAddress(true));
//		contextEvent.addProperty(PROPERTY_KEY_SCREEN_TIMEOUT_IN_SECONDS, String.valueOf(getScreenTimeout()));
//		contextEvent.addProperty(PROPERTY_KEY_MUSES_DATABASE_EXISTS, String.valueOf(musesDatabaseExist(context, DBManager.DATABASE_NAME)));
//		contextEvent.addProperty(PROPERTY_KEY_ACCESSIBILITY_ENABLED, String.valueOf(isAccessibilityForMusesEnabled()));
		contextEvent.generateId();


		if(contextEventHistory.size() > 0) {
			ContextEvent previousContext = contextEventHistory.get(contextEventHistory.size() - 1);
			// fire new context event if a connectivity context field changed
			if(!identicalContextEvent(previousContext, contextEvent)) {
				// add context event to the context event history
				contextEventHistory.add(contextEvent);
				if(contextEventHistory.size() > CONTEXT_EVENT_HISTORY_SIZE) {
					contextEventHistory.remove(0);
				}

				if (contextEvent != null && listener != null) {
					debug(contextEvent);
					listener.onEvent(contextEvent);
				}
			}
		}
		else {
			contextEventHistory.add(contextEvent);
			if (contextEvent != null && listener != null) {
				debug(contextEvent);
				listener.onEvent(contextEvent);
			}
		}

	}

	public void debug(ContextEvent contextEvent) {
		for (Entry<String, String> set : contextEvent.getProperties().entrySet()) {
			//Log.d(TAG, set.getKey() + " = " + set.getValue());
		}
	}

	private boolean identicalContextEvent(ContextEvent oldEvent, ContextEvent newEvent) {
		Map<String, String> oldProperties = oldEvent.getProperties();
		oldProperties.remove(PROPERTY_KEY_ID);
		Map<String, String> newProperties = newEvent.getProperties();
		newProperties.remove(PROPERTY_KEY_ID);
		for (Entry<String, String> set : newProperties.entrySet()) {
			String oldValue = oldProperties.get(set.getKey());
			String newValue = newProperties.get(set.getKey());
			if(!oldValue.equals(newValue)) {
				return false;
			}
		}

		return true;
	}

//	public boolean musesDatabaseExist(String dbName) {
//		return dbFile.exists();
//	}

//	@Override
//	public void configure(List<SensorConfiguration> config) {
//		for (SensorConfiguration item : config) {
//			Log.d(TAG, "DEVICE CONFIG TEST: key=" + item.getKey() + ", value="+item.getValue());
//			if(item.getKey().equals(CONFIG_KEY_TRUSTED_AV)) {
//				trustedAVs.add(item.getValue());
//			}
//		}
//	}

	@Override
	public void disable() {
		if (sensorEnabled) {
			sensorEnabled = false;
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
		if (contextEventHistory.size() > 0) {
			return contextEventHistory.get(contextEventHistory.size() - 1);
		} else {
			return null;
		}
	}

	@Override
	public String getSensorType() {
		return TYPE;
	}

	private class CreateContextEventAsync {

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
				createContextEvent();

				try {
					TimeUnit.MILLISECONDS.sleep(OBSERVATION_INTERVALL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}
}