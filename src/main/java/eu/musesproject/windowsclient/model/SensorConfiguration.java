package eu.musesproject.windowsclient.model;

/*
 * #%L
 * windows_client
 * %%
 * Copyright (C) 2013 - 2015 Sweden Connectivity
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String key;

	@Column(name="sensor_type")
	private String sensorType;

	private String value;

	public SensorConfiguration() {
	}

	public SensorConfiguration(String sensorType, String key, String value) {
		setSensorType(sensorType);
		setKey(key);
		setValue(value);
	}

	public SensorConfiguration(int id, String sensorType, String key, String value) {
		setId(id);
		setSensorType(sensorType);
		setKey(key);
		setValue(value);
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