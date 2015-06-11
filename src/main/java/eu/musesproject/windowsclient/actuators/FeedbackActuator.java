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

import eu.musesproject.client.model.actuators.ResponseInfoAP;
import eu.musesproject.client.model.decisiontable.Decision;
import eu.musesproject.windowsclient.view.SimpleFeedbackDialog;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by christophstanik on 4/15/14.
 *
 * Class to send feedback to MUSES UI
 * Feedback types:
 *  - login successful
 *  - login unsuccessful
 *  - show feedback based on a {@link Decision}
 */
public class FeedbackActuator implements IFeedbackActuator {
    private static IUICallback callback;
    private Queue<Decision> decisionQueue;

    public FeedbackActuator() {
        decisionQueue = new LinkedList<Decision>();
    }

    @Override
    public void showFeedback(Decision decision) {
        if(decision != null && decision.getName() != null) {
            try {
                // check if the decision is already in the queue for the feedback dialogs,
                // in order to avoid duplicate entries
                for (Decision bufferedDecision: decisionQueue) {
                    if(bufferedDecision.getRiskCommunication().getRiskTreatment()[0].getTextualDescription().equals(
                            decision.getRiskCommunication().getRiskTreatment()[0].getTextualDescription())) {
                        return; // do not add a duplicate feedback
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            decisionQueue.add(decision);

            // just show a new dialog if there is no other currently displayed
            if (decisionQueue.size() == 1) {
                createSimpleFeedbackDialog(decision);
            }
        }
    }

    private void showNextFeedback(Decision decision) {
        createSimpleFeedbackDialog(decision);
    }

    private void createSimpleFeedbackDialog(Decision decision) {
        try {
            String dialogBody = decision.getRiskCommunication().getRiskTreatment()[0].getTextualDescription();
            new SimpleFeedbackDialog(decision.getName(), dialogBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendFeedbackToMUSESAwareApp(Decision decision) {
        try {
            eu.musesproject.server.risktrust.RiskTreatment riskTreatment[] = decision.getRiskCommunication().getRiskTreatment();
            ResponseInfoAP infoAP;
            if(decision.getName().equals(Decision.STRONG_DENY_ACCESS) || decision.getName().equals(Decision.DEFAULT_DENY_ACCESS) ) {
                infoAP = ResponseInfoAP.DENY;
            }
            else {
                infoAP = ResponseInfoAP.ACCEPT;
            }

        } catch (Exception e) {
            e.printStackTrace();
            // no risk treatment
        }
    }

    @Override
    public void removeFeedbackFromQueue() {
        // removes the last feedback dialog
        if(decisionQueue != null) {
            try {
                decisionQueue.remove();
            } catch (Exception e) {
            }
            if(!decisionQueue.isEmpty()) {
                // triggers to show the next feedback dialog if there is any
                showNextFeedback(decisionQueue.element());

            }
        }
    }

    @Override
    public void showCurrentTopFeedback() {
        if(decisionQueue != null && decisionQueue.size() > 0) {
            createSimpleFeedbackDialog(decisionQueue.peek());
        }
    }

    public void sendLoginResponseToUI(boolean result, String msg, int detailedStatus) {
        if(callback!=null) {
            callback.onLogin(result, msg, detailedStatus);
        }
    }

    public void registerCallback(IUICallback iUICallback) {
        callback = iUICallback;
    }

    public void unregisterCallback(IUICallback iUICallback) {
        callback = iUICallback;
    }
}