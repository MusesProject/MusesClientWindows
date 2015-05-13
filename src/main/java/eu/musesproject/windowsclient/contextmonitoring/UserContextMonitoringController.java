package eu.musesproject.windowsclient.contextmonitoring;

import java.util.Map;

import org.json.JSONObject;

import eu.musesproject.client.model.contextmonitoring.UISource;
import eu.musesproject.client.model.decisiontable.Action;
import eu.musesproject.windowsclient.connectionmanager.ConnectionManager;
import eu.musesproject.windowsclient.connectionmanager.IConnectionCallbacks;
import eu.musesproject.windowsclient.connectionmanager.IConnectionManager;

public class UserContextMonitoringController implements IUserContextMonitoringController, IConnectionCallbacks{

	
	private static final String URL = "https://sweoffice.mooo.com:8443/server/commain";
	public static final String LOGIN_JSON = "{\"requesttype\":\"login\",\"username\":\"muses\",\"password\":\"muses\",\"device_id\":\"357864056646126\"}";
	
	static IUICallback callback;
	public UserContextMonitoringController() {

	}

	public void sendUserAction(UISource src, Action action,
			Map<String, String> properties) {
	}

	public void sendUserBehavior(Action action) {
	
	}

	public void onSensorConfigurationChanged() {
	
	}

	
	// IConnectionCallbacks
	
	public void login(String userName, String password) {
		IConnectionManager connectionManager = new ConnectionManager();
		connectionManager.springConnect(URL, "cert", LOGIN_JSON, 1, 10000, 10000, this);	
	}

	public int receiveCb(String receiveData) {	
		if ( (receiveData != null) && !receiveData.equals("") ) {
			if (JSONManager.getAuthResult(receiveData)){
				callback.onLogin(true, "");
				if (JSONManager.getAuthResult(receiveData)){
					//sendConfigSyncRequest();
				}
			}
		}
		return 0;
	}
	
	/**
	 * Method to request a configuration update
	 */

	private void sendConfigSyncRequest() {
		System.out.println("UCEH - sendConfigSyncRequest()");
		JSONObject configSyncRequest = JSONManager.createConfigSyncJSON("mac address of devive","muses");
		IConnectionManager connectionManager = new ConnectionManager();
		connectionManager.sendData(configSyncRequest.toString());	
		
	}

	public int statusCb(int status, int detailedStatus) {
		System.out.println("Status received from server: " + ( status == 1 ?  "ONLINE" : "OFFLINE" ) );
		return 0;
	}
	
	
	public static void registerCallbacks(IUICallback iuiCallback){
		callback = iuiCallback;
	}
}
