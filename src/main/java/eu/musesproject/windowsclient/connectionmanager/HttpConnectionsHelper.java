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


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

public class HttpConnectionsHelper extends ConnectionManager implements Runnable {

	private HttpsURLConnection urlConnection;
	private Request request;
	private static CookieManager cookieManager;
	private static String cookieHeader = "";
	
	public static final boolean ENABLE_COOKIE = true;
	private static final String APP_TAG = "APP_TAG";
	private URL url;
	private URI uri;

	public static int CONNECTION_TIMEOUT = 5000;
	public static int POLLING_ENABLED = 1;

	public HttpConnectionsHelper(Request request) {
		this.request = request;
		try {
			url = new URL(request.getUrl());
			uri = url.toURI();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		cookieManager = new CookieManager();
	}


	public void run() {
		HttpResponseHandler httpResponseHandler = doPost();
		httpResponseHandler.checkHttpResponse();
	}

	/**
	 * This method will create a URLConnection and set the necessary parameters
	 * like for instance the values in the header and what kind of request it
	 * should be. Worth mentioning that the HTTP request is not sent until you
	 * flush the stream. HTTP POST request is used in order to send a payload
	 * body. The payload will be a JSON object containing credentials for login
	 * or something else. Worth noting that the client will have a cookie after
	 * the request has been sent. The content of the jsp on the server will be
	 * put inside a string. The jsp at the path /server/commain is empty at the
	 * moment so the response will be empty in this case.
	 */
	private HttpResponseHandler doPost() {
		String requestMethod = "";
		int responseCode = 0;
		String responseCookie = "";
		String responseHeader = "";
	
		HttpResponse httpResponse = null;
		HttpResponseHandler serverResponse = new HttpResponseHandler(
				request.getType(), request.getDataId());
		try{	
			urlConnection = (HttpsURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setRequestProperty("connection-type", request.getType());
			urlConnection.setRequestProperty("poll-interval", getInStringSeconds(request.getPollInterval()));

			if (ENABLE_COOKIE) {
				urlConnection.setRequestProperty("Cookie", cookieHeader);
			}
			
			urlConnection.setRequestMethod("POST");
			requestMethod = urlConnection.getRequestMethod();
			urlConnection.connect();

			DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
			wr.writeBytes(request.getData());
			wr.flush();
			wr.close();
			
			responseCode = urlConnection.getResponseCode();
			requestMethod = urlConnection.getRequestMethod();
			
			if (ENABLE_COOKIE) {
				cookieManager.put(uri, urlConnection.getHeaderFields());
			} else {
				cookieManager.getCookieStore().removeAll();
			}
			
			responseCookie = cookieManager.getCookieStore().get(uri).toString();
			System.out.println("ResponseCoookie===>"+responseCookie);
			if (!responseCookie.equalsIgnoreCase("[]")) {
				serverResponse.setNewSession(true,DetailedStatuses.SUCCESS_NEW_SESSION);
				cookieHeader = responseCookie;
			}else{
				serverResponse.setNewSession(false,DetailedStatuses.SESSION_UPDATED);
			}
			responseHeader = urlConnection.getHeaderFields().toString();
			
			StringBuffer payloadResponse = new StringBuffer();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				payloadResponse.append(inputLine);
			}
			in.close();
			
			boolean isMorePackets = checkIsMorePackets(urlConnection);
			httpResponse = new HttpResponse(responseCode, requestMethod, 
														 payloadResponse.toString(), responseHeader, isMorePackets);
			serverResponse.setResponse(httpResponse);
			setCommandNotOngoing();
			return serverResponse;

		} catch (Exception e) {
			System.out.println(APP_TAG + " doSecurePost: " + e.toString());
			e.printStackTrace();
		}
		return serverResponse;
	}


	private boolean checkIsMorePackets(HttpsURLConnection con) {
		String headerResponse;
		if (con.getHeaderFields().get("more-packets") != null) {
			headerResponse = con.getHeaderFields().get("more-packets").toString();
			if (headerResponse.contains("YES")) {
				return true;
			}
		}
		return false;
	}
	
	public static String getInStringSeconds(String pollInterval) {
		long pollIntervalInSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(pollInterval)) ;
		return Long.toString(pollIntervalInSeconds);
	}
}
