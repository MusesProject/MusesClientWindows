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
 * Callback interface implemented by other modules
 * 
 * @author Yasir Ali
 * @version Jan 27, 2014
 */

public interface IConnectionManager {

	void connect (String url, String cert, int pollInterval, int sleepPollInterval, IConnectionCallbacks callbacks);
	void setPollTimeOuts (int pollInterval, int sleepPollInterval);
	void setTimeout(int timeout);
	void setPolling(int polling);
	void sendData (String data);
	void disconnect ();
}