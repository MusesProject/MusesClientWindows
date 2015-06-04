package eu.musesproject.windowsclient.model;

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

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the configuration database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Configuration.findAll", query="SELECT c FROM Configuration c"),
	@NamedQuery(name="Configuration.deleteById", query="delete FROM Configuration c where c.id = :id")
})
public class Configuration implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="client_certificate")
	private String clientCertificate;

	@Column(name="login_attempts")
	private int loginAttempts;

	@Column(name="poll_timeout")
	private int pollTimeout;

	@Column(name="polling_enabled")
	private int pollingEnabled;

	@Column(name="server_certificate")
	private String serverCertificate;

	@Column(name="server_context_path")
	private String serverContextPath;

	@Column(name="server_ip")
	private String serverIp;

	@Column(name="server_port")
	private String serverPort;

	@Column(name="server_servlet_path")
	private String serverServletPath;

	@Column(name="silent_mode")
	private int silentMode;

	@Column(name="sleep_poll_timeout")
	private int sleepPollTimeout;

	private int timeout;

	public Configuration() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getClientCertificate() {
		return this.clientCertificate;
	}

	public void setClientCertificate(String clientCertificate) {
		this.clientCertificate = clientCertificate;
	}

	public int getLoginAttempts() {
		return this.loginAttempts;
	}

	public void setLoginAttempts(int loginAttempts) {
		this.loginAttempts = loginAttempts;
	}

	public int getPollTimeout() {
		return this.pollTimeout;
	}

	public void setPollTimeout(int pollTimeout) {
		this.pollTimeout = pollTimeout;
	}

	public int getPollingEnabled() {
		return this.pollingEnabled;
	}

	public void setPollingEnabled(int pollingEnabled) {
		this.pollingEnabled = pollingEnabled;
	}

	public String getServerCertificate() {
		return this.serverCertificate;
	}

	public void setServerCertificate(String serverCertificate) {
		this.serverCertificate = serverCertificate;
	}

	public String getServerContextPath() {
		return this.serverContextPath;
	}

	public void setServerContextPath(String serverContextPath) {
		this.serverContextPath = serverContextPath;
	}

	public String getServerIp() {
		return this.serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public String getServerPort() {
		return this.serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public String getServerServletPath() {
		return this.serverServletPath;
	}

	public void setServerServletPath(String serverServletPath) {
		this.serverServletPath = serverServletPath;
	}

	public int getSilentMode() {
		return this.silentMode;
	}

	public void setSilentMode(int silentMode) {
		this.silentMode = silentMode;
	}

	public int getSleepPollTimeout() {
		return this.sleepPollTimeout;
	}

	public void setSleepPollTimeout(int sleepPollTimeout) {
		this.sleepPollTimeout = sleepPollTimeout;
	}

	public int getTimeout() {
		return this.timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}