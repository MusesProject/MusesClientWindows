package eu.musesproject.windowsclient.view;

/*
 * #%L
 * windows_client
 * %%
 * Copyright (C) 2013 - 2015 Sweden Connectivity
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

import java.util.Observable;
import java.util.Properties;

import eu.musesproject.client.model.JSONIdentifiers;
import eu.musesproject.client.model.decisiontable.Decision;
import eu.musesproject.server.risktrust.RiskTreatment;
import eu.musesproject.windowsclient.contextmonitoring.IUICallback;

public class MusesUICallbacksHandler extends Observable implements IUICallback{


	// CallBack messages
	public static final int LOGIN_SUCCESSFUL = 0;
	public static final int LOGIN_UNSUCCESSFUL = 1;
	public static final int ACTION_RESPONSE_ACCEPTED = 2;
	public static final int ACTION_RESPONSE_DENIED = 3;
	public static final int ACTION_RESPONSE_MAY_BE = 4;
	public static final int ACTION_RESPONSE_UP_TO_USER = 5;
	
	public MusesUICallbacksHandler() {
	}
	
	@Override
	public void onLogin(boolean result, String detailedMsg) {
		System.out.println("onLogin result: " + result);
        Properties bundle = new Properties();
        if(result){
        	bundle.setProperty("action_response", Integer.toString(LOGIN_SUCCESSFUL));
		} else {
			bundle.setProperty("action_response", Integer.toString(LOGIN_UNSUCCESSFUL));
		}
        bundle.setProperty(JSONIdentifiers.AUTH_MESSAGE, detailedMsg);
        setChanged();
        notifyObservers(bundle);
    }

	@Override
	public void onAccept() {
		System.out.println("onAccept: ");
        Properties bundle = new Properties();
    	bundle.setProperty("action_response",Integer.toString(ACTION_RESPONSE_ACCEPTED));
		notifyObservers(bundle);		
	}

	@Override
	public void onDeny(Decision decision) {
		System.out.println("onDeny: " + decision.toString());
		String textualDecp = "this is a test risk treatment ...";
		RiskTreatment[] r = decision.getRiskCommunication().getRiskTreatment();
		if (r[0].getTextualDescription() != null) {
			textualDecp = r[0].getTextualDescription();
		}
    	Properties bundle = new Properties();
    	bundle.setProperty("action_response",Integer.toString(ACTION_RESPONSE_DENIED));
		bundle.setProperty("name",decision.getName());
		bundle.setProperty("risk_textual_decp", textualDecp);
		notifyObservers(bundle);
	}

	@Override
	public void onMaybe(Decision decision) {	
		System.out.println("onMaybe: " + decision.toString());
		String textualDecp = "this is a test risk treatment ...";
		RiskTreatment[] r = decision.getRiskCommunication().getRiskTreatment();
		// FIXME For brussels
		if (r != null){
			if (r[0].getTextualDescription() != null) {
				textualDecp = r[0].getTextualDescription();
			}
	    	Properties bundle = new Properties();
	    	bundle.setProperty("action_response",Integer.toString(ACTION_RESPONSE_MAY_BE));
			bundle.setProperty("name",decision.getName());
			bundle.setProperty("risk_textual_decp", textualDecp);
			notifyObservers(bundle);
		}
	}

	@Override
	public void onUpToUser(Decision decision) {
		System.out.println("onUpToUser: " + decision.toString());
		String textualDecp = "this is a test risk treatment ...";
		RiskTreatment[] r = decision.getRiskCommunication().getRiskTreatment();
		if (r[0].getTextualDescription() != null) {
			textualDecp = r[0].getTextualDescription();
		}    	
    	Properties bundle = new Properties();
    	bundle.setProperty("action_response",Integer.toString(ACTION_RESPONSE_UP_TO_USER));
		bundle.setProperty("name",decision.getName());
		bundle.setProperty("risk_textual_decp", textualDecp);
		notifyObservers(bundle);
	}

	@Override
	public void onError() {
		
	}

}
