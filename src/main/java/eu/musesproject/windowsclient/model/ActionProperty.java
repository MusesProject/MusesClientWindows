package eu.musesproject.windowsclient.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the action_property database table.
 * 
 */
@Entity
@Table(name="action_property")
@NamedQueries({
	@NamedQuery(name="ActionProperty.findAll", query="SELECT a FROM ActionProperty a"),
	@NamedQuery(name="ActionProperty.findByActionId", query="SELECT a FROM ActionProperty a where a.actionId = :actionId")
})
public class ActionProperty implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="action_id")
	private int actionId;

	private String key;

	private String value;

	public ActionProperty() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getActionId() {
		return this.actionId;
	}

	public void setActionId(int actionId) {
		this.actionId = actionId;
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