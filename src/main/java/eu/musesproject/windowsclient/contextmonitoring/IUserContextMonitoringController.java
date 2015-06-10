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

import java.util.Map;

public interface IUserContextMonitoringController {
    /**
     * Method that takes an {@link Action} and the
     * related properties of that action from a MUSES aware application. This method sends all information
     * to the {@link eu.musesproject.windowsclient.usercontexteventhandler.UserContextEventHandler}.
     * @param action {@link Action}. action received from a MUSES aware app
     * @param properties {@link Map}. properties related to the action
     */
    void sendUserAction(UISource src, Action action, Map<String, String> properties);
    
    /**
     * Method that takes an {@link Action}
	 * which contains the decision taken by the user on the MUSES UI.
     * @param action {@link Action}. action received from a MUSES aware app
     */
    void sendUserBehavior(Action action, int decisionId);

    /**
     * Method to change the settings / configuration of the sensors
     * ({@link eu.musesproject.windowsclient.contextmonitoring.sensors.ISensor})
     * 
     * loads sensor configuration from the database
     */
    void onSensorConfigurationChanged();

    /**
     * Method to forward the login data to the {@link eu.musesproject.windowsclient.usercontexteventhandler.UserContextEventHandler}
     * @param userName String. user name
     * @param password String. password
     */
    void login(String userName, String password);
}