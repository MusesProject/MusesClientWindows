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
	@GeneratedValue(strategy=GenerationType.IDENTITY)
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