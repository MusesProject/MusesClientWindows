package eu.musesproject.windowsclient.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the property database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Property.findAll", query="SELECT p FROM Property p"),
	@NamedQuery(name="Property.findByContextEvent", query="SELECT p FROM Property p where p.contexteventId = :contexteventId")
})
public class Property implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="contextevent_id")
	private int contexteventId;

	private String key;

	private String value;

	public Property() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getContexteventId() {
		return this.contexteventId;
	}

	public void setContexteventId(int contexteventId) {
		this.contexteventId = contexteventId;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}