package eu.musesproject.windowsclient.connectionmanager;	
/*
 * #%L
 * MUSES Client
 * %%
 * Copyright (C) 2013 - 2014 Sweden Connectivity
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


/**
 * Handles http response and updates the other module using callbacks
 * 
 * @author Yasir Ali
 * @version Jan 27, 2014
 */
public class HttpResponseHandler {

	private static final String APP_TAG = "APP_TAG";
	private static final int MINIMUM_POLL_AFTER_REQUEST = 10000;
	private String receivedHttpResponseData = null;
	private HttpResponse httpResponse = null;
	private boolean isNewSession = false;
	private String requestType;
	private int dataId;
	private int sessionUpdateReason = 0;
	

	/**
	 * Constructor initialise with httpResponse and request type (connect,data,poll etc)
	 * @param httpResponse
	 * @param requestType
	 * @return void
	 */
	public HttpResponseHandler(HttpResponse httpResponse, String requestType, int dataId) {
		this.httpResponse = httpResponse;
		this.requestType = requestType;
		this.dataId = dataId;
	}
	
	
	/**
	 * Constructor initialise with httpResponse and request type (connect,data,poll etc)
	 * @param httpResponse
	 * @param requestType
	 * @return void
	 */
	public HttpResponseHandler(String requestType, int dataId) {
		this.requestType = requestType;
		this.dataId = dataId;
	}
	

	/**
	 * Check response and call appropriate callback methods
	 * @return void
	 */
	public synchronized void checkHttpResponse(){
		if (httpResponse != null) {
			switch(getStatusCodeResponse(httpResponse)){
			case DetailedStatuses.SUCCESS:
				// Only send if Success in connection
				int detailedOnlineStatus = DetailedStatuses.SUCCESS;
				Statuses.CURRENT_STATUS = Statuses.ONLINE;

				if (isNewSession){
					isNewSession = false;
					if (Statuses.CURRENT_STATUS == Statuses.ONLINE){
						setServerStatusAndCallBack(Statuses.NEW_SESSION_CREATED, sessionUpdateReason, dataId);
					}
				}

				Statuses.CURRENT_STATUS = Statuses.ONLINE;
				
				if (isPollRequest(requestType)) {
					setServerStatusAndCallBack(Statuses.ONLINE, detailedOnlineStatus,dataId);
					if (isPayloadInData(httpResponse)) {
						System.out.println(APP_TAG + " ConnManager=> Server responded with JSON: " + receivedHttpResponseData);
						sendDataToFunctionalLayer();
						sendAcktoServer();
					}
					if (httpResponse.isMorePackets()){
						doPollForAnExtraPacket();
					}
				} else if (isSendDataRequest(requestType)){
					setServerStatusAndCallBack(Statuses.ONLINE, detailedOnlineStatus, dataId);
					setServerStatusAndCallBack(Statuses.DATA_SEND_OK, DetailedStatuses.SUCCESS, dataId);
					if (isPayloadInData(httpResponse)) {
						System.out.println(APP_TAG+ " ConnManager=> Server responded with JSON: " + receivedHttpResponseData);
						sendDataToFunctionalLayer();
					}

					if (httpResponse.isMorePackets()/* FIXME || AlarmReceiver.getCurrentPollInterval()>MINIMUM_POLL_AFTER_REQUEST*/){
						doPollForAnExtraPacket();
					}
				} else if (isAckRequest(requestType)) {
					setServerStatusAndCallBack(Statuses.ONLINE, detailedOnlineStatus, dataId);
					System.out.println(APP_TAG + " ConnManager=> Server responded with JSON: " + receivedHttpResponseData);
					System.out.println(APP_TAG+ " Ack by the server");
				} else if (isConnectRequest(requestType)){
					setServerStatusAndCallBack(Statuses.ONLINE, detailedOnlineStatus, dataId);
					setServerStatusAndCallBack(Statuses.CONNECTION_OK, DetailedStatuses.SUCCESS, dataId);
					if (isPayloadInData(httpResponse)) {
						System.out.println(APP_TAG+  " ConnManager=> Server responded with JSON: " + receivedHttpResponseData);
						//sendDataToFunctionalLayer();
					} 
				} else if (isDisonnectRequest(requestType)){
					setServerStatusAndCallBack(Statuses.DISCONNECTED, DetailedStatuses.SUCCESS, dataId);
					if (isPayloadInData(httpResponse)) {
						System.out.println(APP_TAG + " ConnManager=> Server responded with JSON: " + receivedHttpResponseData);
						//sendDataToFunctionalLayer();
					} 
				}

				/* FIXME AlarmReceiver.resetExponentialPollTime(); */
				break;
			case DetailedStatuses.INCORRECT_URL:
				Statuses.CURRENT_STATUS = Statuses.OFFLINE;
				System.out.println(APP_TAG + " Server is OFFLINE .. Incorrect URL");

				if (isSendDataRequest(requestType)){
					setServerStatusAndCallBack(Statuses.DATA_SEND_FAILED, DetailedStatuses.INCORRECT_URL, dataId);
				}
				
				setServerStatusAndCallBack(Statuses.OFFLINE, DetailedStatuses.INCORRECT_URL, dataId);
				
				/* FIXME AlarmReceiver.increasePollTime(); */
				break;
			case DetailedStatuses.NOT_ALLOWED_FROM_SERVER_UNAUTHORIZED:
				Statuses.CURRENT_STATUS = Statuses.OFFLINE;
				System.out.println(APP_TAG +  " Server is OFFLINE .. Request not allowed from Server..");
				
				if (isSendDataRequest(requestType)){
					setServerStatusAndCallBack(Statuses.DATA_SEND_FAILED, DetailedStatuses.NOT_ALLOWED_FROM_SERVER_UNAUTHORIZED, dataId);
				}

				setServerStatusAndCallBack(Statuses.OFFLINE, DetailedStatuses.NOT_ALLOWED_FROM_SERVER_UNAUTHORIZED, dataId);
				/* FIXME AlarmReceiver.increasePollTime(); */
				break;
			case DetailedStatuses.NOT_FOUND:
				Statuses.CURRENT_STATUS = Statuses.OFFLINE;
				System.out.println(APP_TAG + " Server is OFFLINE .. Error: Not found");
				
				if (isSendDataRequest(requestType)){
					setServerStatusAndCallBack(Statuses.DATA_SEND_FAILED, DetailedStatuses.NOT_FOUND, dataId);
				}
				
				setServerStatusAndCallBack(Statuses.OFFLINE, DetailedStatuses.NOT_FOUND, dataId);
				/* FIXME AlarmReceiver.increasePollTime(); */
				break;
			case DetailedStatuses.INTERNAL_SERVER_ERROR:
				Statuses.CURRENT_STATUS = Statuses.OFFLINE;
				System.out.println(APP_TAG + " Server is OFFLINE .. Server not available");
				if (isSendDataRequest(requestType)){
					setServerStatusAndCallBack(Statuses.DATA_SEND_FAILED, DetailedStatuses.SERVER_NOT_AVAIABLE, dataId);
				}
				
				setServerStatusAndCallBack(Statuses.OFFLINE, DetailedStatuses.SERVER_NOT_AVAIABLE, dataId);
				/* FIXME AlarmReceiver.increasePollTime(); */
				break;	
			case DetailedStatuses.SERVER_NOT_AVAIABLE:
				Statuses.CURRENT_STATUS = Statuses.OFFLINE;
				System.out.println(APP_TAG + "Server is OFFLINE .. Server not available..");
				
				if (isSendDataRequest(requestType)){
					setServerStatusAndCallBack(Statuses.DATA_SEND_FAILED, DetailedStatuses.SERVER_NOT_AVAIABLE, dataId);
				}
				
				setServerStatusAndCallBack(Statuses.OFFLINE, DetailedStatuses.SERVER_NOT_AVAIABLE, dataId);
				/* FIXME AlarmReceiver.increasePollTime(); */
				break;
			default:
				Statuses.CURRENT_STATUS = Statuses.OFFLINE;
				System.out.println(APP_TAG +  " Server is OFFLINE .. Unknown Error:"+getStatusCodeResponse(httpResponse));
				if (isSendDataRequest(requestType)){
					setServerStatusAndCallBack(Statuses.DATA_SEND_FAILED, DetailedStatuses.UNKNOWN_ERROR, dataId);
				}
				setServerStatusAndCallBack(Statuses.OFFLINE, DetailedStatuses.UNKNOWN_ERROR, dataId);
				/* FIXME AlarmReceiver.increasePollTime(); */
				break;
			}


		} else {
			Statuses.CURRENT_STATUS = Statuses.OFFLINE ;
			setServerStatusAndCallBack(Statuses.OFFLINE, DetailedStatuses.UNKNOWN_ERROR, dataId);
			if (isSendDataRequest(requestType)){
				setServerStatusAndCallBack(Statuses.DATA_SEND_FAILED, DetailedStatuses.UNKNOWN_ERROR, dataId);
			} 
			if (isConnectRequest(requestType)){
				setServerStatusAndCallBack(Statuses.CONNECTION_FAILED, DetailedStatuses.UNKNOWN_ERROR, dataId);
				/* Depending on error Polling shall be stopped, but difficult to know if error is recoverable */
			}
			System.out.println(APP_TAG + " Server is OFFLINE, HttpResponse is null, check network connectivity or address of server!");
		}
	}

	private void doPollForAnExtraPacket(){
		ConnectionManager connectionManager = new ConnectionManager();
		connectionManager.poll();
	}
	
	/**
	 * Send data to functional module 
	 * @return void
	 */
	
	private void sendDataToFunctionalLayer(){
		System.out.println(APP_TAG + " Info SS, Sending JSON to MusACS");
		ConnectionManager.callBacks.receiveCb(receivedHttpResponseData);
	}
	
	/**
	 * Calls the connection manager ack method to send the ack to the server
	 */
	private void sendAcktoServer() {
		ConnectionManager connectionManager = new ConnectionManager();
		connectionManager.ack();
	}

	/**
	 * Check if the http request is for polling
	 * @param requestType
	 * @return
	 */
	private boolean isPollRequest(String requestType) {
		return requestType.equalsIgnoreCase(ConnectionManager.POLL);
	}
	
	/**
	 * Check if the http request is for data
	 * @param requestType
	 * @return
	 */
	
	private boolean isSendDataRequest(String requestType) {
		return requestType.equalsIgnoreCase(ConnectionManager.DATA);
	}
	
	/**
	 * Check if the http request is for acknowledgment for the last data sent
	 * @param requestType
	 * @return
	 */
	
	private boolean isAckRequest(String requestType) {
		return requestType.equalsIgnoreCase(ConnectionManager.ACK);
	}
	
	/**
	 * Check if the http request is was a connect request
	 * @param requestType
	 * @return
	 */
	private boolean isConnectRequest(String requestType) {
		return requestType.equalsIgnoreCase(ConnectionManager.CONNECT);
	}
	
	/**
	 * Check if the http request is was a disconnect request
	 * @param requestType
	 * @return
	 */
	private boolean isDisonnectRequest(String requestType) {
		return requestType.equalsIgnoreCase(ConnectionManager.DISCONNECT);
	}
	
	/**
	 * Check if the http response has any payload of data
	 * @param HttpResponse
	 * @return
	 */
	
	private boolean isPayloadInData(HttpResponse httpResponse){
		String data = httpResponse.getResponseMessage();
		receivedHttpResponseData = data;
		return !(data == null || data.equalsIgnoreCase(""));
	}

	/**
	 * Sets curent status of the Server in terms of ONLINE/OFFLINE using callbacks
	 * @param status
	 * @return void
	 */
	private void setServerStatusAndCallBack(int status, int detailedStatus, int dataId) {
		
		if (status == Statuses.OFFLINE || status == Statuses.ONLINE)
		{
			ConnectionManager.sendServerStatus(status, detailedStatus, dataId);
			
			
		}
		else
		{	
			ConnectionManager.callBacks.statusCb(status, detailedStatus, dataId);
		}
		
	}
		
	/**
	 * Get response code from status code
	 * @param httpResponse
	 * @return void
	 */
	
	private int getStatusCodeResponse(HttpResponse httpResponse){
		int statusCode = httpResponse.getResponseCode();
		switch(statusCode){
		case 200 :
			return DetailedStatuses.SUCCESS;
		case 400 :
			return DetailedStatuses.INCORRECT_URL;
		case 401 :
			return DetailedStatuses.NOT_ALLOWED_FROM_SERVER_UNAUTHORIZED;
		case 404 :
			return DetailedStatuses.NOT_FOUND;
		case 500 :
			return DetailedStatuses.INTERNAL_SERVER_ERROR;
		case 503 :
			return DetailedStatuses.SERVER_NOT_AVAIABLE;
		default :
			return DetailedStatuses.UNKNOWN_ERROR;
		}
	}

	public void setNewSession(boolean isNewSession, int reason) {
		this.isNewSession = isNewSession;
		sessionUpdateReason = reason;
	}

	public void setResponse(HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
	}

	public String getRequestType() {
		return requestType;
	}
	
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	
	public String getDataLength() {
		String dataLength = "";
		if (httpResponse != null) {
			String message = httpResponse.getResponseMessage();
			if (message!=null) {
				dataLength = Integer.toString(message.length());
			}
		}
		return dataLength;
	}
	
}