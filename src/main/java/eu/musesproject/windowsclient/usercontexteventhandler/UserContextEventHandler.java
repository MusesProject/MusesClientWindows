/*
 * MUSES High-Level Object Oriented Model
 * Copyright MUSES project (European Commission FP7) - 2013
 */
package eu.musesproject.windowsclient.usercontexteventhandler;

/*
 * #%L
 * musesclient
 * %%
 * Copyright (C) 2013 - 2014 HITEC
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms stoof the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import eu.musesproject.client.model.RequestType;
import eu.musesproject.client.model.actuators.ActuationInformationHolder;
import eu.musesproject.client.model.decisiontable.Action;
import eu.musesproject.client.model.decisiontable.Decision;
import eu.musesproject.client.model.decisiontable.Request;
import eu.musesproject.client.model.decisiontable.Resource;
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.windowsclient.actuators.ActuatorController;
import eu.musesproject.windowsclient.connectionmanager.*;
import eu.musesproject.windowsclient.contextmonitoring.JSONManager;
import eu.musesproject.windowsclient.contextmonitoring.UserContextMonitoringController;
import eu.musesproject.windowsclient.contextmonitoring.sensors.SettingsSensor;
import eu.musesproject.windowsclient.decisionmaker.DecisionMaker;
import eu.musesproject.windowsclient.model.*;
import eu.musesproject.windowsclient.view.LabelsAndText;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class UserContextEventHandler. Singleton
 *
 * @author Christoph
 * @version 28 feb 2014
 */
public class UserContextEventHandler implements RequestTimeoutTimer.RequestTimeoutHandler {
	private static final String TAG = UserContextEventHandler.class.getSimpleName();
	public static final String TAG_RQT = "REQUEST_TIMEOUT";
	public static final String TAG_RQT2 = "REQUEST_TIMEOUT2";
	public static final String TAG_DB = "DATABASE_TEST_CODE";
	public static final String APP_TAG = "APP_TAG";

	private static UserContextEventHandler userContextEventHandler = null;
	public static final String PREF_KEY_USER_AUTHENTICATED = "PREF_KEY_USER_AUTHENTICATED";
	public static final String PREF_KEY_USER_AUTHENTICATED_REMOTELY = "PREF_KEY_USER_AUTHENTICATED_REMOTELY";

	// connection fields
	private ConnectionManager connectionManager;
	private IConnectionCallbacks connectionCallback;
	public int serverStatus;
	private int serverDetailedStatus;

    private boolean isAuthenticatedRemotely;
    private boolean isUserAuthenticated;

	private DecisionMaker decisionMaker;

	private static Map<Integer, RequestHolder> mapOfPendingRequests;//String key is the hashID of the request object
	private static Map<Integer, JSONObject> pendingJSONRequest;// to be able to handle 'data send failed'... so we can resend data
	private static Map<Integer, JSONObject> failedJSONRequest;// to be able to handle 'data send failed'... so we can resend data

	private String macAddress;
	private String userName;
	private String tmpLoginUserName;
	private String tmpLoginPassword;
	private DBManager dbManager;

	private static Logger logger = Logger.getLogger(UserContextEventHandler.class);

	private UserContextEventHandler() {
		connectionManager = new ConnectionManager();
		connectionCallback = new ConnectionCallback();

		serverStatus = Statuses.CURRENT_STATUS;
		serverDetailedStatus = Statuses.OFFLINE;
		isAuthenticatedRemotely = false;
		isUserAuthenticated = false;

		decisionMaker = new DecisionMaker();

		mapOfPendingRequests = new HashMap<Integer, RequestHolder>();
        pendingJSONRequest = new HashMap<Integer, JSONObject>();
        failedJSONRequest = new HashMap<Integer, JSONObject>();
	}

	/**
	 * Method to get the current server status (online, offline)
	 *
	 * @return int. {@link eu.musesproject.windowsclient.connectionmanager.Statuses} online: 1; offline:0
	 */
	public int getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(int serverStatus) {
		this.serverStatus = serverStatus;
	}

	public static UserContextEventHandler getInstance() {
		if (userContextEventHandler == null) {
            userContextEventHandler = new UserContextEventHandler();
		}
		return userContextEventHandler;
	}

	/**
	 * connects to the MUSES server
	 */
	public void connectToServer() {
		logger.debug("connect to server");

		Configuration config = getServerConfigurationFromDB();
		String url = "https://" + config.getServerIp() + ":" + config.getServerPort() + config.getServerContextPath() + config.getServerServletPath();

		connectionManager.connect(
				url,
				config.getServerCertificate(),
				config.getPollTimeout(),
				config.getSleepPollTimeout(),
				connectionCallback
		);
		/* No need to do now, but shall be done if it changes.. */
		connectionManager.setPollTimeOuts(config.getPollTimeout(), config.getSleepPollTimeout());
	}

	private Configuration getServerConfigurationFromDB() {
		logger.debug("get server configuration from db");
		if(dbManager == null) {
			dbManager = new DBManager();
		}

		dbManager.openDB();
		Configuration config = dbManager.getConfigurations();
		/* Check if config is available */
		if (config == null) {
			/* DB Connection config is empty, set initial config */
			// TODO not used?
			dbManager.insertConnectionProperties();
		}
		config = dbManager.getConfigurations();
		dbManager.closeDB();
		return config;
	}

	/**
	 *  Method to first check the local decision maker for a decision to the corresponding
	 *      {@link Action}
	 *
	 *  Sends request to the server if there is no local stored decision /
	 *      perform default procedure if the server is not reachable
	 *
	 * @param action {@link Action}
	 * @param properties {@link Map}<String, String>
	 * @param contextEvents {@link ContextEvent}
	 */
	public void send(Action action, Map<String, String> properties, List<ContextEvent> contextEvents) {
		logger.debug("send(Action action, Map<String, String> properties, List<ContextEvent> contextEvents)");
		Decision decision = retrieveDecision(action, properties, contextEvents);

		if(decision != null) { // local decision found
			logger.debug("local decision found");

			ActuatorController.getInstance().showFeedback(new ActuationInformationHolder(decision, action, properties));
            if(action.isRequestedByMusesAwareApp() && action.isMusesAwareAppRequiresResponse()) {
				logger.debug("send feedback to MUSES aware app");

			    ActuatorController.getInstance().sendFeedbackToMUSESAwareApp(decision);
            }
		}
		else { // if there is no local decision, send a request to the server
			logger.debug("no local decision found");
			if(serverStatus == Statuses.ONLINE && isUserAuthenticated) { // if the server is online, request a decision
				// temporary store the information so that the decision can be made after the server responded with
				// an database update (new policies are sent from the server to the client and stored in the database)
				// In addition, add a timeout to every request
				final RequestHolder requestHolder = new RequestHolder(action, properties, contextEvents);
				requestHolder.setRequestTimeoutTimer(new RequestTimeoutTimer(UserContextEventHandler.this, requestHolder.getId()));
				requestHolder.getRequestTimeoutTimer().start();
				mapOfPendingRequests.put(requestHolder.getId(), requestHolder);
				// create the JSON request and send it to the server
				JSONObject requestObject = JSONManager.createJSON(getMacAddress(), getUserName(), requestHolder.getId(), RequestType.ONLINE_DECISION, action, properties, contextEvents);
				sendRequestToServer(requestObject);
			}
			else if(serverStatus == Statuses.ONLINE && !isUserAuthenticated) {
				storeContextEvent(action, properties, contextEvents);
			}
			else if(serverStatus == Statuses.OFFLINE && isUserAuthenticated) {
                // TODO do not show the default policies at this point
				//ActuatorController.getInstance().showFeedback(new DecisionMaker().getDefaultDecision());
				storeContextEvent(action, properties, contextEvents);
			}
			else if(serverStatus == Statuses.OFFLINE && !isUserAuthenticated) {
				storeContextEvent(action, properties, contextEvents);
			}
		}
	}

	/**
	 * Method that handles the necessary steps to retrieve a decision from the decision maker
	 * 1. Generate the {@link Resource}
	 * 2. Generate the {@link Request}
	 * 3. Make sure that the {@link DecisionMaker} is initialised
	 * 4. Request a decision from the {@link DecisionMaker}
	 *
	 * @param action
	 * @param properties
	 * @param contextEvents
	 * @return
	 */
	private Decision retrieveDecision(Action action, Map<String, String> properties, List<ContextEvent> contextEvents) {
		if(action == null || properties == null || contextEvents == null) {
			return null;
		}
		Resource resource = ResourceCreator.create(action, properties);
		Request request = new Request(action, resource);
		if(decisionMaker == null) {
			decisionMaker = new DecisionMaker();
		}
		return decisionMaker.makeDecision(request, contextEvents, properties);
	}

	/**
	 * Method that takes an {@link Action}
	 * which contains the decision taken by the user on the MUSES UI.
	 * This behavior will be send to the server
	 *
	 * @param action
	 */
	public void sendUserBehavior(Action action, int decisionId) {
        // display next Feedback dialog if there is any
        ActuatorController.getInstance().removeFeedbackFromQueue();

        // send the current user feedback to the server
		if(serverStatus == Statuses.ONLINE && isUserAuthenticated) {
			JSONObject userBehaviorJSON = JSONManager.createUserBehaviorJSON(getMacAddress(), getUserName(), action.getActionType(), decisionId);
			sendRequestToServer(userBehaviorJSON);
		}
		else {
			// TODO store it offline
		}
	}

	/**
	 * Method to log in to MUSES.
	 * Necessary to establish server communication and for
	 * using this application
	 *
	 * @param userName
	 * @param password
	 */
	public void login(String userName, String password) {
		this.userName = userName;
		tmpLoginUserName = userName;
		tmpLoginPassword = password;

		if(dbManager == null) {
			dbManager = new DBManager();
		}

		String deviceId;
		dbManager.openDB();
		if((deviceId = dbManager.getDevId()) == null || deviceId.isEmpty()) {
			deviceId = getMacAddress();
		}
		dbManager.closeDB();

		if(serverStatus == Statuses.ONLINE) {
			JSONObject requestObject = JSONManager.createLoginJSON(tmpLoginUserName, tmpLoginPassword, deviceId);
			sendRequestToServer(requestObject);
		}
		else {
			dbManager.openDB();
			isUserAuthenticated = dbManager.isUserAuthenticated(getMacAddress(), tmpLoginUserName, tmpLoginPassword);
			dbManager.closeDB();
			ActuatorController.getInstance().sendLoginResponse(isUserAuthenticated, LabelsAndText.LOGIN_LOCAL_TOAST, -1);
			if (isUserAuthenticated){
				sendConfigSyncRequest();
			}
		}

		// start monitoring
		manageMonitoringComponent();
	}

	/**
	 * Method to try to login with existing credentials in the database
	 */
	public void autoLogin() {
		// TODO how are they stored now? Since we don't have shared preferences
//		if(userName.isEmpty() || password.isEmpty()) {
//			return; // user wasn't logged in before
//		}
		String password = "dummy"; // todo remove

        // try to log in locally
		dbManager.openDB();
		isUserAuthenticated = dbManager.isUserAuthenticated(getMacAddress(), userName, password);
		boolean sensorConfigExists = dbManager.hasSensorConfig();
		dbManager.closeDB();

        //try to log in remotely
        if(serverStatus == Statuses.ONLINE) {
        	tmpLoginUserName = userName;
        	tmpLoginPassword = password;
        	JSONObject requestObject = JSONManager.createLoginJSON(userName, password, getMacAddress());
            sendRequestToServer(requestObject);
        }

		updateServerOnlineAndUserAuthenticated();

        // decide, whether to start the context monitoring or to request for a proper configuration
		if(sensorConfigExists) {
			manageMonitoringComponent();
		}
		else {
			sendConfigSyncRequest();
		}
	}

	public void manageMonitoringComponent() {
		if(isUserAuthenticated) {
			UserContextMonitoringController.getInstance().startContextObservation();
		}
	}

	/**
	 * Method to request a configuration update
	 */
	private void sendConfigSyncRequest() {
		JSONObject configSyncRequest = JSONManager.createConfigSyncJSON(getMacAddress(), SettingsSensor.getOSVersion(), getUserName());
		sendRequestToServer(configSyncRequest);
	}

	/**
	 * Method to logout the user, so that no more events are send to the server in his/her name
	 */
	public void logout() {
		if(serverStatus == Statuses.ONLINE) {
			JSONObject logoutJSON = JSONManager.createLogoutJSON(getUserName(), getMacAddress());
			sendRequestToServer(logoutJSON);
		}
		else {
			// we cannot logout to the server, so send a logout response for the GUI immediately
			ActuatorController.getInstance().sendLoginResponse(false, LabelsAndText.LOGGED_OUT_SUCCESSFULLY_TOAST, -1);
		}

		isUserAuthenticated = false;
        isAuthenticatedRemotely = false;
		updateServerOnlineAndUserAuthenticated();
	}

	/**
	 * Method to store a context event in the database if
	 * there is no connection to the server
	 *
	 * @param action {@link Action}
	 * @param properties {@link Map}<String, String>
	 * @param contextEvents {@link ContextEvent}
	 */
	public void storeContextEvent(Action action, Map<String, String> properties, List<ContextEvent> contextEvents) {
		if((action != null) && (properties != null) && (contextEvents != null)) {
			if(dbManager == null) {
				dbManager = new DBManager();
			}
			dbManager.openDB();

			int actionId = (int) dbManager.addOfflineAction(DBEntityParser.transformActionToEntityAction(action));
			for(Map.Entry<String, String> entry : properties.entrySet()) {
				// transform to action property
				ActionProperty actionProperty = new ActionProperty();
				actionProperty.setActionId(actionId);
				actionProperty.setKey(entry.getKey());
				actionProperty.setValue(entry.getValue());

				dbManager.addOfflineActionProperty(actionProperty);
			}

			for(ContextEvent contextEvent : contextEvents){
				long contextEventId = dbManager.addContextEvent(DBEntityParser.transformContextEvent(actionId, contextEvent));
				for(Property property : DBEntityParser.transformProperty(contextEventId, contextEvent)) {
					dbManager.addProperty(property);
				}
			}
			dbManager.closeDB();
		}
	}

	/**
	 * Method to send locally stored data to the server
	 */
	public void sendOfflineStoredContextEventsToServer() {
		/*
		 * 1. check if the user is authenticated
		 * 2. check if the dbManager object is null
		 * 3. get a list of all stored actions
		 * 4. for each action do:
		 * 4.1 get all related properties of that action
		 * 4.2 get all context events of that action
		 * 4.3 for each context event:
		 * 4.3.1 get all related properties to that action
		 * 4.4. create a json for
		 * 4.5. send this json to the server
		 */

		//1. check if the user is authenticated
		if(isUserAuthenticated) {
			// 2. check if the dbManager object is null
			if (dbManager == null) {
				dbManager = new DBManager();
			}

			// 3. get a list of all stored actions
            dbManager.openDB();
			for (eu.musesproject.windowsclient.model.Action entityAction : dbManager.getOfflineActionList()) {
				Action action = DBEntityParser.transformAction(entityAction);

				//  4.1 get all related properties of that action
				List<ActionProperty> entityActionProperties = dbManager.getOfflineActionPropertiesOfAction(entityAction.getId());
				Map<String, String> actionProperties = DBEntityParser.transformActionPropertyToMap(entityActionProperties);

				//4.2 get all context events of that action
				List<ContextEvent> contextEvents = new ArrayList<ContextEvent>();
				for(eu.musesproject.windowsclient.model.ContextEvent dbContextEvent : dbManager.getStoredContextEventByActionId(entityAction.getId())) {
					ContextEvent contextEvent = DBEntityParser.transformEntityContextEvent(dbContextEvent);

					// 4.3.1 get all related properties to that action
					for(Property property: dbManager.getPropertiesOfContextEvent(contextEvent.getId())) {
						contextEvent.addProperty(property.getKey(), property.getValue());
					}

					contextEvents.add(contextEvent);
				}

				// 4.4. create a json for
				JSONObject requestObject = JSONManager.createJSON(getMacAddress(), getUserName(), -1, RequestType.ONLINE_DECISION, action, actionProperties, contextEvents);

				// 4.5. send this json to the server
				sendRequestToServer(requestObject);
			}
            dbManager.closeDB();
		}
	}

	/**
	 * Info SS
	 *
	 * Method to send a request to the server
	 *
	 * @param requestJSON {@link JSONObject}
	 */
	public void sendRequestToServer(JSONObject requestJSON) {
		if (requestJSON != null && !requestJSON.toString().isEmpty()) {
			if(serverStatus == Statuses.ONLINE) {
				logger.debug("send request to server: " + requestJSON.toString());
                String sendData  = requestJSON.toString();

                int jsonRequestID = sendData.hashCode();
                pendingJSONRequest.put(jsonRequestID, requestJSON);
                connectionManager.sendData(sendData, jsonRequestID);
            }
		}
	}


	public void updateServerOnlineAndUserAuthenticated() {
		logger.debug("update authentication status");
		isUserAuthenticated = isAuthenticatedRemotely;

//		SharedPreferences.Editor prefEditor = prefs.edit();
//		prefEditor.putBoolean(PREF_KEY_USER_AUTHENTICATED, isUserAuthenticated);
//		prefEditor.putBoolean(PREF_KEY_USER_AUTHENTICATED_REMOTELY, isAuthenticatedRemotely);
//		prefEditor.commit();
	}

	private class ConnectionCallback implements IConnectionCallbacks {
		@Override
		public int receiveCb(String receivedData) {
			logger.debug("called: receiveCb(String receivedData) receivedData:"+receivedData);
			if((receivedData != null) && (!receivedData.equals(""))) {
				if(dbManager == null) {
					dbManager = new DBManager();
				}

				// identify the request type
				String requestType = JSONManager.getRequestType(receivedData);
				logger.debug("receiveCb(); requestType=" + requestType);

				if(requestType.equals(RequestType.UPDATE_POLICIES)) {
//					RemotePolicyReceiver.getInstance().updateJSONPolicy(receivedData, context);

					// look for the related request
					int requestId = JSONManager.getRequestId(receivedData);
					logger.debug("request_id from the json is " + requestId);

					if(mapOfPendingRequests != null && mapOfPendingRequests.containsKey(requestId)) { // this should ne
						RequestHolder requestHolder = mapOfPendingRequests.get(requestId);
						requestHolder.getRequestTimeoutTimer().cancel();
						mapOfPendingRequests.remove(requestId);

						logger.debug("Removing action: " + JSONManager.getActionType(receivedData) + " from the pending requests");
						System.out.println(TAG + "| Removing action: " + JSONManager.getActionType(receivedData));
						send(requestHolder.getAction(), requestHolder.getActionProperties(), requestHolder.getContextEvents());
					}
				}
				else if(requestType.equals(RequestType.AUTH_RESPONSE)) {
					isAuthenticatedRemotely = JSONManager.getAuthResult(receivedData);
                    isUserAuthenticated = isAuthenticatedRemotely;
                    String authMessage = JSONManager.getAuthMessage(receivedData);
                    updateServerOnlineAndUserAuthenticated();
					if(isAuthenticatedRemotely) {
						dbManager.openDB();
						dbManager.insertCredentials(getMacAddress(), tmpLoginUserName, tmpLoginPassword);
						dbManager.closeDB();
						// clear the credentials in the fields
						userName = tmpLoginUserName;
						tmpLoginUserName = "";
						tmpLoginPassword = "";

						setServerStatus(Statuses.ONLINE);
						sendOfflineStoredContextEventsToServer();
                        resendFailedJSONRequests();
						updateServerOnlineAndUserAuthenticated();
						sendConfigSyncRequest();
					}
					ActuatorController.getInstance().sendLoginResponse(isAuthenticatedRemotely, authMessage, -1);
				}
				else if(requestType.equals(RequestType.LOGOUT_RESPONSE)) {
					String authMessage = JSONManager.getAuthMessage(receivedData);
					ActuatorController.getInstance().sendLoginResponse(false, authMessage, -1);
				}
				else if(requestType.equals(RequestType.CONFIG_UPDATE)) {
                	/*
                	 *  trials configuration
                	 *  2.1 load config from JSON
                	 *  2.2 insert into the database
                	 */
					// 2.1
					boolean isSilentModeActivated = JSONManager.isSilentModeActivated(receivedData);

                	/*
                	 *  connection configuration
                	 *  3.1 load config from JSON
                	 *  3.2 insert new config in the db
                	 *  3.3 update the connection manager
                	 */
					// 3.1 load config from JSON
					Configuration connectionConfig = JSONManager.getConnectionConfiguration(receivedData);
					connectionConfig.setSilentMode(isSilentModeActivated ? 1 : 0);
					// 2.2 & 3.2 insert new config in the db
					if(!connectionConfig.toString().isEmpty()) {
						dbManager.openDB();
						dbManager.insertConfiguration(connectionConfig);
						dbManager.closeDB();
						// 3.3 update the connection manager
						connectionManager.setTimeout(connectionConfig.getTimeout());
						connectionManager.setPolling(connectionConfig.getPollingEnabled());
						connectionManager.setPollTimeOuts(connectionConfig.getPollTimeout(), connectionConfig.getSleepPollTimeout());
					}
				}
			}
			return 0;
		}

		@Override
		public int statusCb(int status, int detailedStatus, int dataId) {
			logger.debug("called: statusCb(int status, int detailedStatus)" + status + ", " + detailedStatus);
			// detect if server is back online after an offline status
			if(status == Statuses.ONLINE && detailedStatus == DetailedStatuses.SUCCESS) {
				if(serverStatus == Statuses.OFFLINE) {
					logger.debug("Server back to ONLINE, sending offline stored events to server");
                    setServerStatus(status);
                    updateServerOnlineAndUserAuthenticated();
                    sendOfflineStoredContextEventsToServer();
                    resendFailedJSONRequests();
                }
			}
            else if(status == Statuses.ONLINE && detailedStatus == DetailedStatuses.SUCCESS_NEW_SESSION) {
				if(serverStatus == Statuses.OFFLINE) {
					setServerStatus(status);
    				}
				// Since new session not authenticated remotely
				isAuthenticatedRemotely = false;
				updateServerOnlineAndUserAuthenticated();
                autoLogin();
            }
			else if(status == Statuses.OFFLINE) {
				setServerStatus(status);
				// Can still be authenticated, but server not reachable.
				// Depends on new session or not when ONLINE
				updateServerOnlineAndUserAuthenticated();
			}
            else if(status == Statuses.DATA_SEND_OK) {
                if(detailedStatus == DetailedStatuses.SUCCESS) {
                    dbManager.openDB();
                    dbManager.resetStoredContextEventTables();
                    dbManager.closeDB();

                    pendingJSONRequest.remove(dataId);
					logger.debug("data send ok.removed item from pendingJSONRequest, size=" + pendingJSONRequest.size());
                }
            }
            else if(status == Statuses.DATA_SEND_FAILED) {
                failedJSONRequest.put(dataId, pendingJSONRequest.get(dataId));
				logger.debug(" data send failed.failedJSONRequest size=" + failedJSONRequest.size());
            }
            else if(status == Statuses.NEW_SESSION_CREATED && detailedStatus == DetailedStatuses.SUCCESS_NEW_SESSION) {
                isAuthenticatedRemotely = false;
				updateServerOnlineAndUserAuthenticated();
                autoLogin();
            }
			else if(status == Statuses.CONNECTION_FAILED && detailedStatus == DetailedStatuses.NO_INTERNET_CONNECTION) {
				setServerStatus(status);
				updateServerOnlineAndUserAuthenticated();
			}
			else if(status == Statuses.DISCONNECTED && detailedStatus == DetailedStatuses.NO_INTERNET_CONNECTION) {
				setServerStatus(status);
				updateServerOnlineAndUserAuthenticated();
			}

            if(status == Statuses.ONLINE) {
				setServerStatus(status);
                updateServerOnlineAndUserAuthenticated();
            }

			if(detailedStatus == DetailedStatuses.UNKNOWN_ERROR) {
				logger.debug("UNKNOWN_ERROR callback");
				// fires the unknown error feedback
				ActuatorController.getInstance().showFeedback(new ActuationInformationHolder());
			}

			// if the user tries to login and the server responses with an error, this error code will be send in the
			// login callback
			try {
				if (pendingJSONRequest.size() > 0 && JSONManager.getRequestType(pendingJSONRequest.get(dataId).toString()).equals(RequestType.LOGIN)) {
					if ((detailedStatus == DetailedStatuses.INCORRECT_CERTIFICATE)
							|| (detailedStatus == DetailedStatuses.INCORRECT_URL)
							|| (detailedStatus == DetailedStatuses.INTERNAL_SERVER_ERROR)
							|| (detailedStatus == DetailedStatuses.NO_INTERNET_CONNECTION)
							|| (detailedStatus == DetailedStatuses.UNKNOWN_ERROR)
							|| (detailedStatus == DetailedStatuses.NOT_ALLOWED_FROM_SERVER_UNAUTHORIZED)
							|| (detailedStatus == DetailedStatuses.NOT_FOUND)) {
						ActuatorController.getInstance().sendLoginResponse(false, "", detailedStatus);
					}
				}
			} catch (NullPointerException e) {
				// json string is empty
			}

			serverDetailedStatus = detailedStatus;

			return 0;
		}
	}

    private void resendFailedJSONRequests() {
		logger.debug("resendFailedJSONRequests failedJSONRequest, size=" + failedJSONRequest.size());
        List<JSONObject> tmpList = new ArrayList<JSONObject>(failedJSONRequest.values());
        failedJSONRequest.clear();
        for (JSONObject jsonObject : tmpList) {
            sendRequestToServer(jsonObject);
        }
    }

    public String getMacAddress() {
		if(macAddress == null || macAddress.equals("")) {
			this.macAddress = SettingsSensor.getMacAddress();
		}
		return macAddress;
	}

    public boolean isUserAuthenticated() {
        return isUserAuthenticated;
    }

	public String getUserName() {
		if(isUserAuthenticated) {
			if(userName == null || userName.equals("")) {
				return userName;
			}
			else {
				return "unknown";
			}
		}
		else {
			return "unknown";
		}
	}

	public void removeRequestById(int requestId) {
		logger.debug(" 6. removeRequestById map size: " + mapOfPendingRequests.size());
		if(mapOfPendingRequests != null && mapOfPendingRequests.containsKey(requestId)) {
			logger.debug("Removing action with id: " + requestId);
			mapOfPendingRequests.remove(requestId);
			logger.debug(" 7. removeRequestById map size afterwards: " + mapOfPendingRequests.size());
		}
	}

	@Override
	public void handleRequestTimeout(int requestId) {
		logger.debug("5. handleRequestTimeout to id: " + requestId);
		// 1. store object temporary
		// 2. remove object from the map that holds all RequestHolder
		// 3. perform default decision

		// -> 1.
		RequestHolder requestHolder = mapOfPendingRequests.get(requestId);
		// -> 2.
		removeRequestById(requestId);
		// -> 3.
		if(decisionMaker == null) {
			decisionMaker = new DecisionMaker();
		}
	}
}