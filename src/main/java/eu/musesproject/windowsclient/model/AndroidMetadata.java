package eu.musesproject.windowsclient.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the android_metadata database table.
 * 
 */
@Entity
@Table(name="android_metadata")
@NamedQuery(name="AndroidMetadata.findAll", query="SELECT a FROM AndroidMetadata a")
public class AndroidMetadata implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	@Lob
	private String locale;

	public AndroidMetadata() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLocale() {
		return this.locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

}