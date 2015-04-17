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
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Base64;

public class HttpClient implements Runnable {

	private String type;
	private String url;
	private HttpsURLConnection con;
	private String data;
	private int poll_interval;
	private boolean keepCookie;
	public static String cookieHeader;


	public HttpClient(String type, String url, String data, int poll_interval, 
		boolean keepCookie) {
		this.type = type;
		this.url = url;
		this.data = data;
		this.poll_interval = poll_interval;
		this.keepCookie = keepCookie;
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
		int responseCode = 0;
		String requestMethod = "";
		String headerResponse = "";
		HttpResponseHandler serverResponse = new HttpResponseHandler();
		try {	
			con = (HttpsURLConnection) new URL(url).openConnection();
			con.setConnectTimeout(10000);
			if (type.equalsIgnoreCase("connect")){
				con.setRequestProperty("connection-type", "data");
				String authStringEnc = new String(Base64.encodeBase64("muses:muses".getBytes()));
				con.setRequestProperty("Authorization", "Basic " + authStringEnc);
				serverResponse.setRequestType("data");
			}else {
				con.setRequestProperty("connection-type", type);
			}
			con.setRequestProperty("poll-interval", Integer.toString(poll_interval));
			if (cookieHeader!=null) {
				con.setRequestProperty("Cookie", cookieHeader);
				
			}
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(data);
			wr.flush();
			wr.close();
			responseCode = con.getResponseCode();
			requestMethod = con.getRequestMethod();
			
			// Retrieve the received payload from server. Usually the HTML/JSP
			// file
			StringBuffer payloadResponse = new StringBuffer();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				payloadResponse.append(inputLine);
			}
			in.close();
			
			boolean isMorePackets = checkIsMorePackets(con);
			HttpResponse httpResponse = new HttpResponse(responseCode, requestMethod, 
														 payloadResponse.toString(), headerResponse, isMorePackets);
			serverResponse.setResponse(httpResponse);
			grabCookie(con);
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
		            System.out.println("secure=true");
		          } else if (fields[j].indexOf('=') > 0) {
		        	  String[] f = fields[j].split("=");
		            if ("expires".equalsIgnoreCase(f[0])) {
		            	responseCookie.setExpires(new Date(f[1]));
		                System.out.println("expires"+ f[1]);
		            } else if ("domain".equalsIgnoreCase(f[0])) {
		            	responseCookie.setDomain(f[1]);
		                System.out.println("domain"+ f[1]);
		            } else if ("path".equalsIgnoreCase(f[0])) {
		            	responseCookie.setPath(f[1]);
		                System.out.println("path"+ f[1]);
		            }
		          }
		        }
		      }
		    }
    }
		
//		StringBuilder sb = new StringBuilder();
//		List<String> cookies = con.getHeaderFields().get("Set-Cookie");
//		if (cookies != null) {
//			for (String cookie : cookies) {
//				if (sb.length() > 0) {
//					sb.append("; ");
//				}
//				String value = cookie.split(";")[0];
//				sb.append(value);
//			}
//		}
//		cookieHeader = sb.toString();

	
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
	
	
}
