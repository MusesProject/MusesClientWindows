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


public class HttpResponse {

	private int responseCode;
	private String requestMethod;
	private String responseMessage;
	private String headerResponse;
	private boolean isMorePackets;
	
	public HttpResponse(int responseCode, String requestMethod,
			String responseMessage, String headerResponse, boolean isMorePackets) {
		super();
		this.responseCode = responseCode;
		this.requestMethod = requestMethod;
		this.responseMessage = responseMessage;
		this.headerResponse = headerResponse;
		this.isMorePackets = isMorePackets;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getHeaderResponse() {
		return headerResponse;
	}

	public void setHeaderResponse(String headerResponse) {
		this.headerResponse = headerResponse;
	}

	public boolean isMorePackets() {
		return isMorePackets;
	}

	public void setMorePackets(boolean isMorePackets) {
		this.isMorePackets = isMorePackets;
	}
	
}
