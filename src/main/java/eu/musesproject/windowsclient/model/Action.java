package eu.musesproject.windowsclient.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the action database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Action.findAll", query="SELECT a FROM Action a"),
	@NamedQuery(name="Action.findByType", query="SELECT a FROM Action a where a.actionType = :actionType"),
	@NamedQuery(name="Action.findByDescription", query="SELECT a FROM Action a where a.description = :description")
})
public class Action implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Column(name="action_type")
	private String actionType;

	private String description;

	private Timestamp timestamp;

	public Action() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getActionType() {
		return this.actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

}