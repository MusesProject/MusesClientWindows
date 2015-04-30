package eu.musesproject.windowsclient.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the decisiontable database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Decisiontable.findAll", query="SELECT d FROM Decisiontable d"),
	@NamedQuery(name="Decisiontable.findByActionId", query="SELECT d FROM Decisiontable d where d.actionId = :actionId"),
	@NamedQuery(name="Decisiontable.findByActionAndResource", query="SELECT d FROM Decisiontable d where d.actionId = :actionId and d.resourceId = :resourceId"),
	@NamedQuery(name="Decisiontable.findByResource", query="SELECT d FROM Decisiontable d where d.resourceId = :resourceId"),
	@NamedQuery(name="Decisiontable.findByDecisiontableId", query="SELECT d FROM Decisiontable d where d.decisionId = :decisionId"),
	@NamedQuery(name="Decisiontable.findByActionAndSubject", query="SELECT d FROM Decisiontable d where d.actionId = :actionId and d.subjectId = :subjectId"),
	@NamedQuery(name="Decisiontable.findByActionResourceSubject", query="SELECT d FROM Decisiontable d where d.actionId = :actionId and d.resourceId = :resourceId and d.subjectId = :subjectId")
})
public class Decisiontable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Column(name="action_id")
	private int actionId;

	@Column(name="decision_id")
	private int decisionId;

	private Timestamp modification;

	@Column(name="resource_id")
	private int resourceId;

	@Column(name="riskcommunication_id")
	private int riskcommunicationId;

	@Column(name="subject_id")
	private int subjectId;

	public Decisiontable() {
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

	public int getDecisionId() {
		return this.decisionId;
	}

	public void setDecisionId(int decisionId) {
		this.decisionId = decisionId;
	}

	public Timestamp getModification() {
		return this.modification;
	}

	public void setModification(Timestamp modification) {
		this.modification = modification;
	}

	public int getResourceId() {
		return this.resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public int getRiskcommunicationId() {
		return this.riskcommunicationId;
	}

	public void setRiskcommunicationId(int riskcommunicationId) {
		this.riskcommunicationId = riskcommunicationId;
	}

	public int getSubjectId() {
		return this.subjectId;
	}

	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}

}