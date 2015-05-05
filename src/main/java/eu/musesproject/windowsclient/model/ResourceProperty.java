package eu.musesproject.windowsclient.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the resource_property database table.
 * 
 */
@Entity
@Table(name="resource_property")
@NamedQueries({
	@NamedQuery(name="ResourceProperty.findAll", query="SELECT r FROM ResourceProperty r"),
	@NamedQuery(name="ResourceProperty.findByResourceId", query="SELECT r FROM ResourceProperty r where r.resourceId = :resourceId ")
})
public class ResourceProperty implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String key;

	@Column(name="resource_id")
	private int resourceId;

	private String value;

	public ResourceProperty() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getResourceId() {
		return this.resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}