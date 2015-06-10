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

	private static String URL;
	private static String certificate;
	
	private int POLL_INTERVAL;
	private int SLEEP_POLL_INTERVAL;
	
	private static String IP = "sweoffice.mooo.com";
	public static IConnectionCallbacks callBacks;
	
	public static final String CONNECT = "connect";
	public static final String POLL = "poll";
	public static final String DATA = "data";
	public static final String DISCONNECT = "disconnect";
	public static final String ACK = "ack";
	private static final String APP_TAG = "APP_TAG";
	
	private static Boolean isServerConnectionSet = false; 
	private AtomicInteger mCommandOngoing = new AtomicInteger(0);
	
	public static int lastSentStatus = Statuses.OFFLINE;
	private AlarmReceiver alarmReceiver;
	public static boolean isNewSession = true;
	
	
	
	public ConnectionManager() {
		SelfSignedCertWorkaround(); // FIXME Fix this asap
		alarmReceiver = new AlarmReceiver();
		AlarmReceiver.setManager(this);
	}

	public void connect(String url, String cert, int pollInterval,	int sleepPollInterval, IConnectionCallbacks iCallbacks) {
		
		/* FIXME, temporary fix for dual calls */
		synchronized (this){
			if (isServerConnectionSet){
				System.out.println(APP_TAG+ " connect: More then one more connect call!");
				return;
			}
			isServerConnectionSet = true;
		}
		
		URL = url;
		POLL_INTERVAL = pollInterval;
		SLEEP_POLL_INTERVAL = sleepPollInterval;
		callBacks = iCallbacks;
		
		/* Check that cert is ok, spec length */
		/* FIXME which Length.. */
		if (cert.isEmpty() || cert.length() < 1000){
			callBacks.statusCb(Statuses.CONNECTION_FAILED, DetailedStatuses.INCORRECT_CERTIFICATE, 0);
			System.out.println(APP_TAG+ " Connect: Incorrect certificate!");
			return;
		} 
		
		if (NetworkChecker.isInternetConnected()) {
			System.out.println(APP_TAG + " Internet Connected.");
		}
		
        setCommandOngoing();
		
		System.out.println(APP_TAG +  " ConnManager=> connecting to server");
		
        startHttpThread(CONNECT,URL, pollInterval,"", "");
		
		// TBD
		// Set alarm, poll interval, sleep poll interval
		alarmReceiver.setPollInterval(pollInterval, sleepPollInterval);
		alarmReceiver.setDefaultPollInterval(pollInterval, sleepPollInterval);
	}

	@Override
	public void springConnect(String url, String cert, String data, int dataId,int pollInterval, int sleepPollInterval,IConnectionCallbacks iCallbacks) {
		/* FIXME, temporary fix for dual calls */
		synchronized (this){
			if (isServerConnectionSet){
				System.out.println(APP_TAG+ " connect: More then one more connect call!");
				return;
			}
			isServerConnectionSet = true;
		}
		
		URL = url;
		POLL_INTERVAL = pollInterval;
		SLEEP_POLL_INTERVAL = sleepPollInterval;
		callBacks = iCallbacks;
		
		/* Check that cert is ok, spec length */
		/* FIXME which Length.. */
		// FIXME commented temporarily
//		if (cert.isEmpty() || cert.length() < 1000){
//			callBacks.statusCb(Statuses.CONNECTION_FAILED, DetailedStatuses.INCORRECT_CERTIFICATE, 0);
//			System.out.println(APP_TAG+ " Connect: Incorrect certificate!");
//			return;
//		} 
		
		if (NetworkChecker.isInternetConnected()) {
			System.out.println(APP_TAG + " Internet Connected.");
		}
		
        setCommandOngoing();
		
		System.out.println(APP_TAG +  " ConnManager=> connecting to server");
		
        startHttpThread(CONNECT,URL, pollInterval,"", "");
		
		// TBD
		// Set alarm, poll interval, sleep poll interval
		alarmReceiver.setPollInterval(pollInterval, sleepPollInterval);
		alarmReceiver.setDefaultPollInterval(pollInterval, sleepPollInterval);

	}
	
	@Override
	public void sendData(String data, int dataId) {
		String dataIdStr = "";
		dataIdStr = Integer.toString(dataId);
		setCommandOngoing();
		System.out.println(APP_TAG + " ConnManager=> send data to server with request type: "/*+JSONManager.getRequestType(data) FIXME update this after Christoph commit*/);
		startHttpThread(DATA, URL, 	POLL_INTERVAL, data, dataIdStr); 
	}

	@Override
	public void disconnect() {
		// As we are disconnecting we need to stop the polling 
		System.out.println(APP_TAG+ " Disconnecting ..");
		synchronized (this){
			isServerConnectionSet = false;
		}
		
		System.out.println(APP_TAG +  " ConnManager=> disconnecting session to server");
		startHttpThread(DISCONNECT, URL, POLL_INTERVAL, "", "");
			
		//callBacks.statusCb(Statuses.DISCONNECTED, Statuses.DISCONNECTED);  // FIXME
				
		// TBD
		alarmReceiver.cancelAlarm();
	}
	
	@Override
	public void setPollTimeOuts(int pollInterval, int sleepPollInterval) {
		// TBD 
		// Implement alarm or scheduler and set poll intervals for polling
		alarmReceiver.setPollInterval(pollInterval, sleepPollInterval);
		alarmReceiver.setDefaultPollInterval(pollInterval, sleepPollInterval);
	}
	
	@Override
	public void setTimeout(int timeout) {
		HttpConnectionsHelper.CONNECTION_TIMEOUT = timeout;
	}
	
	@Override
	public void setPolling(int polling) {
		HttpConnectionsHelper.POLLING_ENABLED = polling;
	}
	

	/**
	 * Starts to poll with the server either in sleep/active mode
	 * @return void
	 */
	
	public void periodicPoll() {
		//Log.d(APP_TAG, "Polling !!");
		// If ongoing command, don't poll
		if (mCommandOngoing.get()==0){
			poll();
		}
		
	}
	
	
	public void poll() {
		// If ongoing command, don't poll FIXME
		setCommandOngoing();
		startHttpThread(POLL, URL, POLL_INTERVAL, "", "");
	}
	
	public void ack() {
		System.out.println(APP_TAG + "Sending ack..");
		startHttpThread(ACK, URL, POLL_INTERVAL, "", "");
	}
	
	public static void sendServerStatus(int status, int detailedStatus, int dataId) {
		if (status == Statuses.OFFLINE || status == Statuses.ONLINE) {
			if (lastSentStatus != status ){
				ConnectionManager.callBacks.statusCb(status, detailedStatus, dataId);
				lastSentStatus = status;
			}
		}
		else {
			//DBG SweFileLog.write("Weird status: "+Integer.toString(status)+", , ");
		}
		
		
	}

	private void startHttpThread(String cmd, String url, int pollInterval, String data, String dataId) { /* FIXME Put cert  and dataid here*/
		Request request = new Request(cmd,url, Integer.toString(pollInterval), data, "", dataId);
		HttpConnectionsHelper httpClient = new HttpConnectionsHelper(request);
		Thread httpThread = new Thread(httpClient);
		httpThread.start();
	}
	
	
	private void setCommandOngoing() {
		mCommandOngoing.set(1);	
	}
	
	private void setCommandNotOngoing() {	// FIXME Check how it is done in MusesClient should be called after request
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