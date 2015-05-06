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
import eu.musesproject.contextmodel.PackageStatus;
import eu.musesproject.windowsclient.contextmonitoring.ContextListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author alirezaalizadeh
 *
 * Class to collect information about app installation status of the device.
 * The class collects information about:
 *  - new app installed
 *  - already installed app updated
 *  - app deleted
 *  - list of all installed apps
 */
public class PackageSensor implements ISensor {
    private static final String TAG = PackageSensor.class.getSimpleName();

    // sensor identifier
    public static final String TYPE = "CONTEXT_SENSOR_PACKAGE";

    // time in milliseconds when the sensor polls information
    private static int OBSERVATION_INTERVALL = 10000;

    private static final String INITIAL_STARTUP 				= "init";

    // context property keys
    public static final String PROPERTY_KEY_ID 					= "id";
    public static final String PROPERTY_KEY_PACKAGE_STATUS	    = "packagestatus";
    //public static final String PROPERTY_KEY_PACKAGE_NAME		= "packagename";
    public static final String PROPERTY_KEY_APP_NAME 			= "appname";
    public static final String PROPERTY_KEY_APP_VERSION			= "appversion";
    public static final String PROPERTY_KEY_INSTALLED_APPS   	= "installedapps";

    private boolean sensorEnabled;

    private ContextListener listener;

    // stores the context events before the latest at position 0 and the latest at position 1
    private List<ContextEvent> contextEventHistory;

    // broadcast receiver fields
    //final PackageBroadcastReceiver packageReceiver = new PackageBroadcastReceiver();
    private boolean initialContextEventFired;

    private List<String> installedApplications = new ArrayList<String>();
    private List<String> newInstalledApplications = new ArrayList<String>();
    private List<String> uninstalledApplications = new ArrayList<String>();

    public PackageSensor() {
        init();
    }
    
	private void init() {
        sensorEnabled = false;
        contextEventHistory = new ArrayList<ContextEvent>(CONTEXT_EVENT_HISTORY_SIZE);
        initialContextEventFired = false;
    }
	
	private void createInitialContextEvent() {
        // create a list of installed ups and hold it
		if(!initialContextEventFired) {
			new CreateContextEventAsync().backgroundProcess.start();
			initialContextEventFired = true;
		}
	}

    @Override
    public void enable() {
        if(!sensorEnabled) {
            //Log.d(TAG, "start package sensor");
            sensorEnabled = true;
            createInitialContextEvent();
        }
    }

    @Override
    public void disable() {
        if(sensorEnabled) {
            //Log.d(TAG, "stop package sensor");
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
        if(contextEventHistory.size() > 0) {
            return contextEventHistory.get(contextEventHistory.size() - 1);
        }
        else {
            return null;
        }
    }

    private void createContextEvent(String appName, String appVersion,PackageStatus packageStatus) throws IOException {
        String packageStatusName = packageStatus != null ? packageStatus.toString() : "unknown";
        ContextEvent contextEvent = new ContextEvent();
        contextEvent.setType(TYPE);
        contextEvent.setTimestamp(System.currentTimeMillis());
        contextEvent.addProperty(PROPERTY_KEY_ID, String.valueOf(contextEventHistory != null ? (contextEventHistory.size() + 1) : -1));
        contextEvent.addProperty(PROPERTY_KEY_PACKAGE_STATUS, packageStatusName);
        contextEvent.addProperty(PROPERTY_KEY_APP_NAME, appName);
        contextEvent.addProperty(PROPERTY_KEY_APP_VERSION, appVersion);
        contextEvent.addProperty(PROPERTY_KEY_INSTALLED_APPS, getInstalledApps());
        contextEvent.generateId();

        // add context event to the context event history
        contextEventHistory.add(contextEvent);
        if(contextEventHistory.size() > CONTEXT_EVENT_HISTORY_SIZE) {
            contextEventHistory.remove(0);
        }

        if(listener != null) {
            listener.onEvent(contextEvent);
        }
    }

    private String getInstalledApps() throws IOException {
        return RESTController.requestSensorInfo(TYPE).get(PROPERTY_KEY_INSTALLED_APPS);
    }

    private void processApplicationChanges(String installedAppsStr){
        List<String> appsList= new ArrayList<String>();
        String[]  appsTemp = installedAppsStr.split(";");
        for (String app:appsTemp){
            appsList.add(app);
        }

        if (installedApplications.isEmpty()){
            installedApplications = appsList;
            return;
        }

        //identify new installed applications
        for (String app : appsList){
            if(installedApplications.indexOf(app) == -1){
                newInstalledApplications.add(app);
            }
        }

        //identify uninstalled applications
        for (String app : installedApplications){
            if(appsList.indexOf(app) == -1){
                uninstalledApplications.add(app);
            }
        }

        //update apps list
        installedApplications = appsList;
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
                PackageStatus packageStatus = null;
                if (installedApplications.isEmpty()){
                    processApplicationChanges(getInstalledApps());
                    createContextEvent(INITIAL_STARTUP, INITIAL_STARTUP, packageStatus);
                }
                else {
                    processApplicationChanges(getInstalledApps());
                    for (String app : newInstalledApplications){
                        packageStatus =  PackageStatus.INSTALLED;
                        String appName = app.split(",")[0];
                        String appVersion = app.split(",")[1];
                        createContextEvent(appName, appVersion, packageStatus);
                    }
                    for (String app : uninstalledApplications){
                        packageStatus =  PackageStatus.REMOVED;
                        String appName = app.split(",")[0];
                        String appVersion = app.split(",")[1];
                        createContextEvent(appName, appVersion, packageStatus);
                    }
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(OBSERVATION_INTERVALL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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