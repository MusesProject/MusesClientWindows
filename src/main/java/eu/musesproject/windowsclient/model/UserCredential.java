package eu.musesproject.windowsclient.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the user_credentials database table.
 * 
 */
@Entity
@Table(name="user_credentials")
@NamedQueries ({
	@NamedQuery(name="UserCredential.findAll", query="SELECT u FROM UserCredential u"),
	@NamedQuery(name="UserCredential.findByUsername", query="SELECT u FROM UserCredential u where u.username = :username")
})
public class UserCredential implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id",nullable=false)
	private int id;

	@Column(name="device_id")
	private String deviceId;

	private String password;

	private String username;

	public UserCredential() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}