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

import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.windowsclient.contextmonitoring.ContextListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author alirezaalizadeh
 *
 * Class to collect information about sent email by IBM Notes Client.
 * The class collects information about:
 *  - email sunject
 *  - email recievers
 *  - email cc list
 *  - email bcc list
 *  - email attachments list
 *
 */

public class EmailSensor implements ISensor {
    private static final String TAG = EmailSensor.class.getSimpleName();

    // sensor identifier
    public static final String TYPE = "CONTEXT_SENSOR_EMAIL";

    // time in milliseconds when the sensor polls information
    private static int OBSERVATION_INTERVALL = 1000;

    // maximal number of how many background services are stored if a context event is fired
    private static final int MAX_SHOWN_BACKGROUND_SERVICES = 1;

    // context property keys
    public static final String PROPERTY_KEY_ID 					= "id";
    public static final String PROPERTY_KEY_SUBJECT    		    = "subject";
    public static final String PROPERTY_KEY_RECEIVERS    		= "receivers";
    public static final String PROPERTY_KEY_CC		        	= "cc";
    public static final String PROPERTY_KEY_BCC                 = "bcc";
    public static final String PROPERTY_KEY_ATTACHMENTS         = "attachments";

    private ContextListener listener;

    // stores all fired context events of this sensor
    private List<ContextEvent> contextEventHistory;


    // holds a value that indicates if the sensor is enabled or disabled
    private boolean sensorEnabled;

    public EmailSensor() {
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
     * @param emailInfo new sensed sent email information
     */
    private void createContextEvent( Map<String, String> emailInfo) {
        // create the context event
        ContextEvent contextEvent = new ContextEvent();
        contextEvent.setType(TYPE);
        contextEvent.setTimestamp(System.currentTimeMillis());
        contextEvent.addProperty(PROPERTY_KEY_SUBJECT, emailInfo.get(PROPERTY_KEY_SUBJECT));
        contextEvent.addProperty(PROPERTY_KEY_RECEIVERS, emailInfo.get(PROPERTY_KEY_RECEIVERS));
        contextEvent.addProperty(PROPERTY_KEY_CC, emailInfo.get(PROPERTY_KEY_CC));
        contextEvent.addProperty(PROPERTY_KEY_BCC, emailInfo.get(PROPERTY_KEY_BCC));
        contextEvent.addProperty(PROPERTY_KEY_ATTACHMENTS, emailInfo.get(PROPERTY_KEY_ATTACHMENTS));
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

    @Override
    public void enable() throws IOException {
        if (!sensorEnabled) {
            sensorEnabled = true;
            new EmailObserver().backgroundProcess.start();
        }
    }

    @Override
    public void disable() {
        if(sensorEnabled) {
            sensorEnabled = false;
        }
    }

    /**
     * Observes the users application usage. Creates a context event whenever a new application is started.
     */
    private class EmailObserver {

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


            Map<String,String> previousEmailInfo = null;

            while (sensorEnabled) {
                // request for sensor information from sensors REST service
                Map<String, String> emailInfo = RESTController.requestSensorInfo(TYPE);

                try {
                    // create a context event when new sent email sensed
                    if(!emailInfo.equals(previousEmailInfo)) {
                        createContextEvent(emailInfo);
                        previousEmailInfo = emailInfo;
                    }

                    Thread.sleep(OBSERVATION_INTERVALL);
                }
                catch (InterruptedException e) {
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