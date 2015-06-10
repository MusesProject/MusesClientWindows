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
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.windowsclient.contextmonitoring.ContextListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author alirezaalizadeh
 *
 * Class to collect information about the currently used app by the user.
 * The class collects information about:
 *  - foreground app name
 *  - background processes name
 */

public class MockUpAppSensor implements ISensor {
    private static final String TAG = MockUpAppSensor.class.getSimpleName();

    // sensor identifier
    public static final String TYPE = "CONTEXT_SENSOR_APP";

    // time in milliseconds when the sensor polls information
    private static int OBSERVATION_INTERVALL = 10000;

    // maximal number of how many background services are stored if a context event is fired
    private static final int MAX_SHOWN_BACKGROUND_SERVICES = 1;

    // context property keys
    public static final String PROPERTY_KEY_ID 					= "id";
    public static final String PROPERTY_KEY_APP_NAME 			= "appname";
    public static final String PROPERTY_KEY_APP_VERSION			= "appversion";
    public static final String PROPERTY_KEY_BACKGROUND_PROCESS 	= "backgroundprocess";

    private ContextListener listener;

    // stores all fired context events of this sensor
    private List<ContextEvent> contextEventHistory;


    // holds a value that indicates if the sensor is enabled or disabled
    private boolean sensorEnabled;

    public MockUpAppSensor() {
        init();
    }

    // initializes all necessary default values
    private void init() {
        sensorEnabled = false;
        contextEventHistory = new ArrayList<ContextEvent>(CONTEXT_EVENT_HISTORY_SIZE);
    }

    /**
     * creates the context event for this sensor and saves it in the
     * context event history
     * @param appName name of the currently active application
     * @param appVersion version of the currently active application
     */
    private void createContextEvent(String appName, String appVersion) {
        // get the running services
        List<String> runningServicesNames = new ArrayList<String>();

        // create the context event
        ContextEvent contextEvent = new ContextEvent();
        contextEvent.setType(TYPE);
        contextEvent.setTimestamp(System.currentTimeMillis());
        contextEvent.addProperty(PROPERTY_KEY_APP_NAME, appName);
        contextEvent.addProperty(PROPERTY_KEY_APP_VERSION, String.valueOf(appVersion));
        contextEvent.addProperty(PROPERTY_KEY_BACKGROUND_PROCESS, runningServicesNames.toString());
        contextEvent.generateId();

        // add context event to the context event history
        contextEventHistory.add(contextEvent);
        if(contextEventHistory.size() > CONTEXT_EVENT_HISTORY_SIZE) {
            contextEventHistory.remove(0);
        }

        if(listener != null) {
//            Log.d(TAG, "called: listener.onEvent(contextEvent); app name: " + appName);
            listener.onEvent(contextEvent);
        }
    }

    @Override
    public void enable() throws IOException {
        //Log.d(TAG, "app sensor enable");
        if (!sensorEnabled) {
            //Log.d(TAG, "start app tracking");
            sensorEnabled = true;
            new AppObserver().backgroundProcess.start();
        }
    }

    @Override
    public void disable() {
        if(sensorEnabled) {
            //Log.d(TAG, "stop app tracking");
            sensorEnabled = false;
        }
    }

    /**
     * Observes the users application usage. Creates a context event whenever a new application is started.
     */
    private class AppObserver {

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
                try {
                    createContextEvent("Windows", "8.1");
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

	@Override
	public String getSensorType() {
		return TYPE;
	}
}