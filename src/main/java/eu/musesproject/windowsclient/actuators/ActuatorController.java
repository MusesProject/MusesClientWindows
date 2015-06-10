package eu.musesproject.windowsclient.actuators;

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

import eu.musesproject.client.model.actuators.ActuationInformationHolder;
import eu.musesproject.client.model.decisiontable.Decision;
import eu.musesproject.windowsclient.model.DBManager;
import eu.musesproject.windowsclient.usercontexteventhandler.UserContextEventHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author christophstanik
 *
 * Class that manages the lifecycle of the actuators
 */
public class ActuatorController implements IActuatorController {
    private static final String TAG = ActuatorController.class.getSimpleName();

    private static ActuatorController actuatorController = null;

    private IUICallback callback;
    private final UserContextEventHandler uceHandler = UserContextEventHandler.getInstance();

    private FeedbackActuator feedbackActuator;
    /** Integer = decisionId*/
    private Map<Integer, ActuationInformationHolder> holderMap;


    private DBManager dbManager;

    public ActuatorController() {
        this.feedbackActuator = new FeedbackActuator();
        this.dbManager = new DBManager();
        this.holderMap = new HashMap<Integer, ActuationInformationHolder>();
    }

    public static ActuatorController getInstance() {
        if (actuatorController == null) {
            actuatorController = new ActuatorController();
        }
        return actuatorController;
    }

    public void showFeedback(ActuationInformationHolder holder) {
        holderMap.put(1/*holder.getDecision().getID()*/, holder);

        //check for silent mode
        dbManager.closeDB();
        dbManager.openDB();
        boolean isSilentModeActive = dbManager.isSilentModeActive();
        dbManager.closeDB();
		if(holder.getDecision() != null) {
            if(isSilentModeActive) {
                holder.getDecision().setName(Decision.GRANTED_ACCESS);
            }
            // show feedback
            feedbackActuator.showFeedback(holder.getDecision());
        }
    }

    public void showCurrentTopFeedback() {
        feedbackActuator.showCurrentTopFeedback();
    }

    public void removeFeedbackFromQueue() {
        feedbackActuator.removeFeedbackFromQueue();
    }

    public void sendFeedbackToMUSESAwareApp(Decision decision) {
        feedbackActuator.sendFeedbackToMUSESAwareApp(decision);
    }

    public void sendLoginResponse(boolean loginResponse, String msg, int detailedStatus) {
        feedbackActuator.sendLoginResponseToUI(loginResponse, msg, detailedStatus);
    }

    @Override
    public void registerCallback(IUICallback callback) {
        this.callback = callback;
        feedbackActuator.registerCallback(callback);
    }

    @Override
    public void unregisterCallback(IUICallback callback) {
        this.callback = callback;
        feedbackActuator.unregisterCallback(callback);
    }

    @Override
    public void perform(int decisionID) {
        // not handled in this version
    }
}