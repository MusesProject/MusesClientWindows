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

import eu.musesproject.client.model.JSONIdentifiers;
import eu.musesproject.windowsclient.actuators.IUICallback;

import java.util.Observable;
import java.util.Properties;

public class MusesUICallbacksHandler extends Observable implements IUICallback {


	// CallBack messages
	public static final int LOGIN_SUCCESSFUL = 0;
	public static final int LOGIN_UNSUCCESSFUL = 1;
	public static final int ACTION_RESPONSE_ACCEPTED = 2;
	public static final int ACTION_RESPONSE_DENIED = 3;
	public static final int ACTION_RESPONSE_MAY_BE = 4;
	public static final int ACTION_RESPONSE_UP_TO_USER = 5;
	
	
	@Override
	public void onLogin(boolean result, String msg, int errorCode) {
		System.out.println("onLogin result: " + result);
        Properties bundle = new Properties();
        if(result){
        	bundle.setProperty("action_response", Integer.toString(LOGIN_SUCCESSFUL));
		} else {
			bundle.setProperty("action_response", Integer.toString(LOGIN_UNSUCCESSFUL));
		}
        bundle.setProperty(JSONIdentifiers.AUTH_MESSAGE, msg);
        setChanged();
        notifyObservers(bundle);
	}
	
}
