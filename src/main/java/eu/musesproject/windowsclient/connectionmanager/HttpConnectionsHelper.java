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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import javax.net.ssl.HttpsURLConnection;

public class HttpConnectionsHelper implements Runnable {

	private HttpsURLConnection urlConnection;
	public static String cookieHeader;
	private Request request;

	
	public static int CONNECTION_TIMEOUT = 5500;
	private static final int SOCKET_TIMEOUT = 5500;
	private static final int MCC_TIMEOUT = 5500;
	public static int POLLING_ENABLED = 1;

	public HttpConnectionsHelper(Request request) {
		this.request = request;
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
		
		HttpResponse httpResponse = null;
		HttpResponseHandler serverResponse = new HttpResponseHandler(
				request.getType(), request.getDataId());
		
		int responseCode = 0;
		String requestMethod = "";
		String headerResponse = "";
		try {	
			urlConnection = (HttpsURLConnection) new URL(request.getUrl()).openConnection();
			urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
//			if (request.getType().equalsIgnoreCase("connect")){
//				urlConnection.setRequestProperty("connection-type", "data");
//				String authStringEnc = new String(Base64.encodeBase64("muses:muses".getBytes()));
//				urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
//				serverResponse.setRequestType("data");
//			}else {
//				urlConnection.setRequestProperty("connection-type", request.getType());
//			}
			urlConnection.setRequestProperty("connection-type", request.getType());
			urlConnection.setRequestProperty("poll-interval", getInStringSeconds(request.getPollInterval()));
			if (cookieHeader!=null) {
				urlConnection.setRequestProperty("Cookie", cookieHeader);
				
			}
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
			wr.writeBytes(request.getData());
			wr.flush();
			wr.close();
			responseCode = urlConnection.getResponseCode();
			requestMethod = urlConnection.getRequestMethod();
			
			// Retrieve the received payload from server. Usually the HTML/JSP
			// file
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
														 payloadResponse.toString(), headerResponse, isMorePackets);
			serverResponse.setResponse(httpResponse);
			serverResponse.setNewSession(true,DetailedStatuses.SUCCESS_NEW_SESSION); // FIXME should be handled with cookie
			grabCookie(urlConnection);
			return serverResponse;

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return null;
	}



	/**
	 * After HTTP request has been sent, the connection will contain a cookie.
	 * This method will retrieve the cookie from the header.
	 */
	@SuppressWarnings("deprecation")
	public void grabCookie(HttpsURLConnection con) {
		System.out.println("grabing cookie..");
		ResponseCookie responseCookie = new ResponseCookie();
		for (int i = 0;; i++) {
		      String headerName = con.getHeaderFieldKey(i);
		      String headerValue = con.getHeaderField(i);

		      if (headerName == null && headerValue == null) {
		        break;
		      }
		      if ("Set-Cookie".equalsIgnoreCase(headerName)) {
		        String[] fields = headerValue.split(";\\s*");
		        for (int j = 1; j < fields.length; j++) {
		          if ("secure".equalsIgnoreCase(fields[j])) {
		        	  responseCookie.setSecure(Boolean.parseBoolean(fields[i]));
		          } else if (fields[j].indexOf('=') > 0) {
		        	  String[] f = fields[j].split("=");
		            if ("expires".equalsIgnoreCase(f[0])) {
		            	responseCookie.setExpires(new Date(f[1]));
		            } else if ("domain".equalsIgnoreCase(f[0])) {
		            	responseCookie.setDomain(f[1]);
		            } else if ("path".equalsIgnoreCase(f[0])) {
		            	responseCookie.setPath(f[1]);
		            }
		          }
		        }
		      }
		    }
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
		int pollIntervalInSeconds = (Integer.parseInt(pollInterval) / 1000) % 60;
		return Integer.toString(pollIntervalInSeconds);
	}
}
