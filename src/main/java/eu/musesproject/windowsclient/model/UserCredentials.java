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
	@NamedQuery(name="UserCredentials.findAll", query="SELECT u FROM UserCredentials u"),
	@NamedQuery(name="UserCredentials.findByUsername", query="SELECT u FROM UserCredentials u where u.username = :username"),
	@NamedQuery(name="UserCredentials.deleteByUsername", query="delete FROM UserCredentials u where u.username = :username")
})
public class UserCredentials implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id",nullable=false)
	private int id;

	@Column(name="device_id")
	private String deviceId;

	private String password;

	private String username;

	public UserCredentials() {
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