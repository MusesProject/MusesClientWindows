package eu.musesproject.windowsclient.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the contextevent database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Contextevent.findAll", query="SELECT c FROM Contextevent c"),
	@NamedQuery(name="Contextevent.findById", query="SELECT c FROM Contextevent c where c.id = :id"),
	@NamedQuery(name="Contextevent.deleteById", query="delete FROM Contextevent c where c.id = :id"),
	@NamedQuery(name="Contextevent.findByActionId", query="SELECT c FROM Contextevent c where c.actionId = :actionId")
})
public class Contextevent implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Column(name="action_id")
	private int actionId;

	private Timestamp timestamp;

	private String type;

	public Contextevent() {
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

	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}