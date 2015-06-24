package eu.musesproject.windowsclient.contextmonitoring;

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

import eu.musesproject.client.model.contextmonitoring.UISource;
import eu.musesproject.client.model.decisiontable.Action;
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.windowsclient.contextmonitoring.sensors.*;
import eu.musesproject.windowsclient.model.DBManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author alirezaalizadeh
 * @version 4 May 2015
 *
 * Class to control the lifecycle of all implemented sensors
 */
public class SensorController {
    private static final String TAG = SensorController.class.getSimpleName();
    private static Logger logger = Logger.getLogger(SensorController.class);

    private static SensorController sensorController = null;
    private final ContextEventBus contextEventBus = new ContextEventBus();

    private static boolean isCollectingData = false;


    private Map<String, ISensor> activeSensors;
    // stores the latest fired ContextEvent of every sensor
    private Map<String, ContextEvent> lastFiredContextEvents;
    
    private DBManager dbManager;

    private SensorController() {
        activeSensors = new HashMap<String, ISensor>();
        lastFiredContextEvents = new HashMap<String, ContextEvent>();
        
        dbManager = new DBManager();
    }

    public static SensorController getInstance() {
        if (sensorController == null) {
            sensorController = new SensorController();
        }
        return sensorController;
    }
    
    public void startSensors() throws IOException {
        System.out.println(TAG + " | SC - 1. start sensors");
        // just start the sensors if they are not already collecting data
        if(!isCollectingData) {
            System.out.println(TAG + " | SC - 2. data collection in enabled");
            List<String> enabledSensor = new ArrayList<String>();

            // just start the sensors if there is a configuration for them
            // check for sensorConfigExists later
            if(true/*sensorConfigExists*/) {
                System.out.println(TAG + " | SC - 3. config exists");
                enabledSensor.add("CONTEXT_SENSOR_APP");
                enabledSensor.add("CONTEXT_SENSOR_CONNECTIVITY");
                enabledSensor.add("CONTEXT_SENSOR_DEVICE_PROTECTION");
                enabledSensor.add("CONTEXT_SENSOR_PACKAGE");
                enabledSensor.add("CONTEXT_SENSOR_FILE_ACCESS");
                enabledSensor.add("CONTEXT_SENSOR_FILEOBSERVER");
                enabledSensor.add("CONTEXT_SENSOR_EMAIL");
                startAndConfigureSensors(enabledSensor);

                isCollectingData = true;
            }
        }
    }

    private void enableMockedSensors() throws IOException {
        System.out.println(TAG + " | enableMockedSensors");
        activeSensors.put(MockUpAppSensor.TYPE, new MockUpAppSensor());
        activeSensors.put(MockUpDeviceProtectionSensor.TYPE, new MockUpDeviceProtectionSensor());
        activeSensors.put(MockUpPackageSensor.TYPE, new MockUpPackageSensor());

        for (ISensor sensor : activeSensors.values()) {
            sensor.addContextListener(contextEventBus);
            sensor.enable();
        }
    }

    private void startAndConfigureSensors(List<String> enabledSensor) throws IOException {
//        enableMockedSensors();
        for (String sensorType : enabledSensor) {
            ISensor sensor;

            sensor = createSensor(sensorType);
            if(sensor == null) {
                continue;
            }

            sensor.enable();
            activeSensors.put(sensorType, sensor);
        }

        for (ISensor sensor : activeSensors.values()) {
            sensor.addContextListener(contextEventBus);
            sensor.enable();
        }
    }

    /**
     * Method to create a new instance of a sensor based on the sensorType
     * @param sensorType, variable to determine which concrete sensor should be created
     * @return ISensor
     */
    private ISensor createSensor(String sensorType) {
    	ISensor sensor = null;
    	if(sensorType.equals(AppSensor.TYPE)) {
			sensor = new AppSensor();
		}
		else if(sensorType.equals(ConnectivitySensor.TYPE)) {
			sensor = new ConnectivitySensor();
		}
		else if(sensorType.equals(DeviceProtectionSensor.TYPE)) {
			sensor = new DeviceProtectionSensor();
		}
		else if(sensorType.equals(PackageSensor.TYPE)) {
			sensor = new PackageSensor();
		}
		else if(sensorType.equals(FileAccessSensor.TYPE)) {
			sensor = new FileAccessSensor();
		}
		else if(sensorType.equals(RecursiveFileSensor.TYPE)) {
			sensor = new RecursiveFileSensor();
		}
		else if(sensorType.equals(EmailSensor.TYPE)) {
			sensor = new EmailSensor();
		}

    	return sensor;
    }

    /**
     * stops every enabled sensor
     */
    public void stopAllSensors() {
//        Log.d(MusesUtils.TEST_TAG, "SC - stopAllSensors()");
        for (ISensor sensor : activeSensors.values()) {
            sensor.removeContextListener(null);
            sensor.disable();
        }
        activeSensors.clear();
    }

    /**
     * Method that updates the configuration of the sensors
     * 1. stop sensors
     * 2. update configuration
     * 3. re-enable sensors
     */
    public void onSensorConfigurationChanged() throws IOException {
//        Log.d(MusesUtils.TEST_TAG, "SC - onSensorConfigurationChanged()");
    	// 1. stop sensors
    	if (activeSensors != null) {
//            Log.d(MusesUtils.TEST_TAG, "SC - onSensorConfigurationChanged(); activeSensors!=null");
            for (ISensor sensor : activeSensors.values()) {
            	sensor.disable();
            	activeSensors.remove(sensor.getSensorType());
            	sensor = null;
                isCollectingData = false;
            }
        }
    	/*
    	 *  2. update configuration
    	 *  3. re-enable sensors
    	 */
    	startSensors();
    }

    /**
     * Method that returns the last fired events of all enabled sensors
     * @return {@link List} of {@link ContextEvent}
     */
    public List<ContextEvent> getLastFiredEvents() {
        List<ContextEvent> contextEvents = new ArrayList<ContextEvent>();
        if (activeSensors != null) {
            for (ISensor sensor : activeSensors.values()) {
                ContextEvent contextEvent = sensor.getLastFiredContextEvent();
                if(contextEvent != null) { // just add the context event if there is already one fired
                    contextEvents.add(contextEvent);
                }
            }
        }

        return contextEvents;
    }
    
    public Map<String, ISensor> getActiveSensors() {
		return activeSensors;
	}

	/**
     * Inner class that gets notified when a new {@link ContextEvent}
     * is fired by a {@link ISensor}
     *
     * @author christophstanik
     */
    class ContextEventBus implements ContextListener {

        @Override
        public void onEvent(ContextEvent contextEvent) {
            System.out.println(TAG + " " + contextEvent.getType()+" --> "+contextEvent.getProperties() +" ||| event size="+lastFiredContextEvents.size());
            logger.debug(TAG + " " + contextEvent.getType() + " --> " + contextEvent.getProperties() + " ||| event size=" + lastFiredContextEvents.size());
        	/*
             * Workflow of creating an action and sending it to the server
             *
             * If a context event is fired:
             * 1. check if there are already enough context events to create an user action
             * 2. create an user action based on the current and the previous context events
             * 3. update the lastFiredContextEvent list
             * 4. send the action via the {@link eu.musesproject.client.contextmonitoring.UserContextMonitoringController}
             *      to the server
             */
            Action userAction = null;
            Map<String, String> properties = null;
            // 1. if lastFiredContextEvents.size() is 0 than it is the initial context event and no further processing
            // have to be done
            if(lastFiredContextEvents.size() > 0) {
                // 2. create an user action
                userAction = UserActionGenerator.createUserAction(contextEvent);
                properties = UserActionGenerator.createUserActionProperties(contextEvent);
            }

            // 3. update Map with the new context event
            if(lastFiredContextEvents.containsKey(contextEvent.getType())) {
                lastFiredContextEvents.remove(contextEvent.getType());
            }
            lastFiredContextEvents.put(contextEvent.getType(), contextEvent);

            // 4. send action to the UserContextMonitoringController
            if(userAction != null && properties != null) {
                UserContextMonitoringController.getInstance().sendUserAction(UISource.INTERNAL, userAction, properties);
            }
        }
    }
}