package eu.musesproject.windowsclient.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the risktreatment database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Risktreatment.findAll", query="SELECT r FROM Risktreatment r"),
	@NamedQuery(name="Risktreatment.findById", query="SELECT r FROM Risktreatment r where r.id = :id"),
	@NamedQuery(name="Risktreatment.findByDescription", query="SELECT r FROM Risktreatment r where r.textualdescription = :textualdescription"),
})
public class Risktreatment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private String textualdescription;

	public Risktreatment() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTextualdescription() {
		return this.textualdescription;
	}

	public void setTextualdescription(String textualdescription) {
		this.textualdescription = textualdescription;
	}

}