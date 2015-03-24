package eu.musesproject.windowsclient.contextmonitoring;

import java.util.Map;

import eu.musesproject.client.model.contextmonitoring.UISource;
import eu.musesproject.client.model.decisiontable.Action;
import eu.musesproject.windowsclient.connectionmanager.ConnectionManager;
import eu.musesproject.windowsclient.connectionmanager.IConnectionCallbacks;
import eu.musesproject.windowsclient.connectionmanager.IConnectionManager;
import eu.musesproject.windowsclient.connectionmanager.Statuses;

public class UserContextMonitoringController implements IUserContextMonitoringController, IConnectionCallbacks{

	
	private static final String URL = "https://localhost:8443/server/commain";

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
		connectionManager.connect(URL, "", 10000, 10000, this);
	}

	public int receiveCb(String receiveData) {
		System.out.println("Data received from server: "+receiveData);
		return 0;
	}

	public int statusCb(int status, int detailedStatus) {
		System.out.println("Status received from server: " + ( status == 1 ?  "ONLINE" : "OFFLINE" ) );
		if (status == Statuses.ONLINE) {
			IConnectionManager connectionManager = new ConnectionManager();
			connectionManager.sendData("{\"requesttype\":\"login\",\"username\":\"muses\",\"password\":\"muses\",\"device_id\":\"357864056646126\"}");
		}
		return 0;
	}

}
