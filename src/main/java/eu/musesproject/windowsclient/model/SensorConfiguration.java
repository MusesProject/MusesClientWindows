package eu.musesproject.windowsclient.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the sensor_configuration database table.
 * 
 */
@Entity
@Table(name="sensor_configuration")
@NamedQueries({
	@NamedQuery(name="SensorConfiguration.findAll", query="SELECT s FROM SensorConfiguration s"),
	@NamedQuery(name="SensorConfiguration.findByType", query="SELECT s FROM SensorConfiguration s where s.sensorType = :sensorType"),
	@NamedQuery(name="SensorConfiguration.findByKey", query="SELECT s FROM SensorConfiguration s where s.key = :key")
})
public class SensorConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private String key;

	@Column(name="sensor_type")
	private String sensorType;

	private String value;

	public SensorConfiguration() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSensorType() {
		return this.sensorType;
	}

	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}