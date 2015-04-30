package eu.musesproject.windowsclient.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the riskcommunication database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Riskcommunication.findAll", query="SELECT r FROM Riskcommunication r"),
	@NamedQuery(name="Riskcommunication.findById", query="SELECT r FROM Riskcommunication r where r.id = :id"),
	@NamedQuery(name="Riskcommunication.findByRiskTreatmentId", query="SELECT r FROM Riskcommunication r where r.risktreatmentId = :risktreatmentId")
})
public class Riskcommunication implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Column(name="communication_sequence")
	private int communicationSequence;

	@Column(name="risktreatment_id")
	private int risktreatmentId;

	public Riskcommunication() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCommunicationSequence() {
		return this.communicationSequence;
	}

	public void setCommunicationSequence(int communicationSequence) {
		this.communicationSequence = communicationSequence;
	}

	public int getRisktreatmentId() {
		return this.risktreatmentId;
	}

	public void setRisktreatmentId(int risktreatmentId) {
		this.risktreatmentId = risktreatmentId;
	}

}