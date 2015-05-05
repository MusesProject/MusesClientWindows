package eu.musesproject.windowsclient.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the decision database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Decision.findAll", query="SELECT d FROM Decision d"),
	@NamedQuery(name="Decision.findById", query="SELECT d FROM Decision d where d.id = :id"),
	@NamedQuery(name="Decision.findByNameAndCondition", query="SELECT d FROM Decision d where d.name = :name and d.condition = :condition")
})
public class Decision implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String condition;

	private Timestamp modification;

	private String name;

	public Decision() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCondition() {
		return this.condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Timestamp getModification() {
		return this.modification;
	}

	public void setModification(Timestamp modification) {
		this.modification = modification;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}