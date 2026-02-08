package edu.stevens.cs548.clinic.data;

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;


/**
 * Entity implementation class for Entity: Treatment
 *
 */
@NamedQueries({
	@NamedQuery(
		name="SearchTreatmentByTreatmentId",
		query="select t from Treatment t where t.id = :treatmentId"),
	@NamedQuery(
			name="CountTreatmentByTreatmentId",
			query="select count(t) from Treatment t where t.id = :treatmentId"),
	@NamedQuery(
		name = "RemoveAllTreatments", 
		query = "delete from Treatment t")
})

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Treatment implements Serializable {
	
	@Serial
    private static final long serialVersionUID = 1L;

    @Id
    private UUID id;
	
	protected String diagnosis;
	
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	@ManyToOne(cascade = CascadeType.PERSIST)
	protected Patient patient;

	public Patient getPatient() {
		return patient;
	}

	void setPatient(Patient patient) {
		this.patient = patient;
		/*
		 * Make sure the patient also links back to this treatment.
		 */
		if (!patient.receives(this)) {
			patient.addTreatment(this);
		}
	}

	@ManyToOne(cascade = CascadeType.PERSIST)
	protected Provider provider;

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
		if (!provider.administers(this)) {
			provider.addTreatment(this);
		}
	}

	@OneToMany(cascade = CascadeType.PERSIST)
	protected Collection<Treatment> followupTreatments;
	
	public void addFollowupTreatment(Treatment t) {
		followupTreatments.add(t);
	}


	public Treatment() {
		super();
		followupTreatments = new ArrayList<>();
	}
}
