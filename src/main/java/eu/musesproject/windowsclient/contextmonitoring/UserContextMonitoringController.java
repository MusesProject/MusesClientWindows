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
import eu.musesproject.windowsclient.usercontexteventhandler.UserContextEventHandler;

import java.io.IOException;
import java.util.Map;

/**
 * @author Christoph
 * @version 28 feb 2014
 *
 * Class to control the workflow of the user context monitoring architecture
 * component. The class is able to:
 *  - start the context observation
 *  - stop the context observation
 *  - send sensor settings/configuration coming from the MUSES UI to the sensor controller
 *  - handle incoming information from MUSES aware apps
 *  - handle incoming information from the MUSES UI
 */
public class UserContextMonitoringController implements
        IUserContextMonitoringController {
    private static final String INTERNAL_SENSOR_TAG = "INTERNAL_SENSOR_TAG";
	private static UserContextMonitoringController ucmController = null;
    private final UserContextEventHandler uceHandler = UserContextEventHandler.getInstance();


    private UserContextMonitoringController() {
        uceHandler.connectToServer();
    }

    public static UserContextMonitoringController getInstance() {
        if (ucmController == null) {
            ucmController = new UserContextMonitoringController();
        }
        return ucmController;
    }

    /**
     * starts every sensor for the context observation
     */
    public void startContextObservation() {
        try {
            SensorController.getInstance().startSensors();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * stops the context observation by
     * disabling every enabled sensor
     */
    public void stopContextObservation() {
        SensorController.getInstance().stopAllSensors();
    }

    @Override
    public void sendUserAction(UISource src, Action action, Map<String, String> properties) {
        if(src == UISource.MUSES_AWARE_APP_UI) {
            Action musesAwareAction = action;
            musesAwareAction.setRequestedByMusesAwareApp(true);
            uceHandler.send(musesAwareAction, properties, SensorController.getInstance().getLastFiredEvents());
        }
        else if(src == UISource.INTERNAL) {
            uceHandler.send(action, properties, SensorController.getInstance().getLastFiredEvents());
        }
    }

    @Override
    public void sendUserBehavior(Action action, int decisionId) {
        uceHandler.sendUserBehavior(action, decisionId);
    }

    @Override
    public void onSensorConfigurationChanged() {
        try {
            SensorController.getInstance().onSensorConfigurationChanged();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void login(String userName, String password) {
        uceHandler.login(userName, password);
    }
}