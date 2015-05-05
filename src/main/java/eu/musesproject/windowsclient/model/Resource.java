package eu.musesproject.windowsclient.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the resource database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name="Resource.findAll", query="SELECT r FROM Resource r"),
	@NamedQuery(name="Resource.findByCondition", query="SELECT r FROM Resource r where r.condition = :condition"),
	@NamedQuery(name="Resource.findByPath", query="SELECT r FROM Resource r where r.path = :path"),
	@NamedQuery(name="Resource.findByPathAndCondition", query="SELECT r FROM Resource r where r.path = :path and r.condition = :condition")
})
public class Resource implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String condition;

	private String description;

	private Timestamp modification;

	private String name;

	private String path;

	private int resourcetype;

	private String severity;

	private String type;

	public Resource() {
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

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getResourcetype() {
		return this.resourcetype;
	}

	public void setResourcetype(int resourcetype) {
		this.resourcetype = resourcetype;
	}

	public String getSeverity() {
		return this.severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}