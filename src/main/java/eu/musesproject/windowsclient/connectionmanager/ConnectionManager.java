package eu.musesproject.windowsclient.connectionmanager;

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


import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionManager implements IConnectionManager {
	
	private static String IP = "localhost";
	public static IConnectionCallbacks callBacks;
	
	public static final String CONNECT = "connect";
	public static final String POLL = "poll";
	public static final String DATA = "data";
	public static final String DISCONNECT = "disconnect";
	public static final String ACK = "ack";
	private static final String APP_TAG = "APP_TAG";
	private AtomicInteger mCommandOngoing = new AtomicInteger(0);
	
	
	public static int lastSentStatus = Statuses.OFFLINE;
	private String URL;
	private int POLL_INTERVAL;
	private int SLEEP_POLL_INTERVAL;
	
	public ConnectionManager() {
		SelfSignedCertWorkaround();
	}

	public void connect(String url, String cert, int pollInterval,
			int sleepPollInterval, IConnectionCallbacks iCallbacks) {
		URL = url;
		POLL_INTERVAL = pollInterval;
		SLEEP_POLL_INTERVAL = sleepPollInterval;
		callBacks = iCallbacks;
		
		if (new NetworkChecker().isInternetConnected()) {
			System.out.println(APP_TAG + " Internet Connected.");
		}
		System.out.println(APP_TAG +  " ConnManager=> connecting to server");
		Thread httpThread = new Thread(new HttpClient("connect", URL, "", POLL_INTERVAL, true));
		httpThread.start();
		
		// TBD
		// Set alarm, poll interval, sleep poll interval
	}


	public void sendData(String data) {
		System.out.println(APP_TAG +  " ConnManager=> send data to server: "+data);
		Thread httpThread = new Thread(new HttpClient("data", URL, "", POLL_INTERVAL, true));
		httpThread.start();
	}

	public void disconnect() {
		System.out.println(APP_TAG +  "Disconnecting.");
		// TBD
		// Cancel alarm
	}

	public void poll() {
		// If ongoing command, don't poll FIXME
		setCommandOngoing();
		Thread httpThread = new Thread(new HttpClient("poll", URL, "", POLL_INTERVAL, true));
		httpThread.start();
	}
	
	public void ack() {
		System.out.println(APP_TAG + "Sending ack..");
		Thread httpThread = new Thread(new HttpClient("ack", URL, "", POLL_INTERVAL, true));
		httpThread.start();	
	}
	
	public void setPollTimeOuts(int pollInterval, int sleepPollInterval) {
		// TBD 
		// Implement alarm or scheduler and set poll intervals for polling
	}
	
	public void setTimeout(int timeout) {
		// TBD
	}
	
	public void setPolling(int polling) {
		// TBD
	}
	
	public static void sendServerStatus(int status, int detailedStatus) {
		if (status == Statuses.OFFLINE || status == Statuses.ONLINE) {
			if (lastSentStatus != status ){
				ConnectionManager.callBacks.statusCb(status, detailedStatus);
				lastSentStatus = status;
				// For testing
				//DBG SweFileLog.write((status==Statuses.ONLINE?"ONLINE,,":"OFFLINE,,"));
				
			}
		}
		else {
			//DBG SweFileLog.write("Weird status: "+Integer.toString(status)+", , ");
		}
		
		
	}

	
	private void setCommandOngoing() {
		mCommandOngoing.set(1);	
	}
	
	private void setCommandNotOngoing() {	
		mCommandOngoing.set(0);	
	}
	
	
	/**
	 * Java doesn't accept self-signed certificates by default for obvious
	 * security reasons. This workaround is a temporarily solution only for
	 * development purposes and should absolutely not be used in production. If
	 * you don't happen to have a keystore for your server, follow this link for
	 * creating one
	 * https://stackoverflow.com/questions/11991284/how-to-bypass-certificate
	 * -checking-in-a-java-web-service-client
	 */
	private static void SelfSignedCertWorkaround() {
		javax.net.ssl.HttpsURLConnection
				.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {

					public boolean verify(String hostname,
							javax.net.ssl.SSLSession sslSession) {
						if (hostname.equals(IP)) {
							return true;
						}
						return false;
					}
				});
		System.setProperty("javax.net.ssl.trustStore", "./keystore.jks");
		System.setProperty("javax.net.ssl.keyStore", "./keystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
	}

	
}