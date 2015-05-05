package eu.musesproject.windowsclient.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the required_apps database table.
 * 
 */
@Entity
@Table(name="required_apps")
@NamedQuery(name="RequiredApps.findAll", query="SELECT r FROM RequiredApps r")
public class RequiredApps implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String name;

	@Column(name="unique_name")
	private String uniqueName;

	private String version;

	public RequiredApps() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUniqueName() {
		return this.uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}