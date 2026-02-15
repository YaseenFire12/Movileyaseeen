package edu.stevens.cs548.clinic.domain;

import edu.stevens.cs548.clinic.domain.ITreatmentDao.TreatmentExn;
import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Entity implementation class for Entity: Patient
 *
 */
@NamedQueries({
		@NamedQuery(name = "SearchPatientByPatientId", query = "select p from Patient p where p.id = :patientId"),
		@NamedQuery(name = "SearchPatientWithTreatmentsByPatientId", query = "select p from Patient p left join fetch p.treatments where p.id = :patientId"),
		@NamedQuery(name = "CountPatientByPatientId", query = "select count(p) from Patient p where p.id = :patientId"),
		@NamedQuery(name = "SearchAllPatients", query = "select p from Patient p"),
		@NamedQuery(name = "RemoveAllPatients", query = "delete from Patient p")
})

@Entity
public class Patient implements Serializable {

	@Serial
	private static final long serialVersionUID = -4512912599605407549L;

	@Id
	private UUID id;

	private String name;

	private LocalDate dob;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	@OneToMany(cascade = CascadeType.PERSIST, mappedBy = "patient")
	private Collection<Treatment> treatments;

	@Transient
	private ITreatmentDao treatmentDao;

	public void setTreatmentDao(ITreatmentDao tdao) {
		this.treatmentDao = tdao;
	}

	/**
	 * This should only be called from Provider ITreatmentImporter methods
	 */
	void addTreatment(Treatment t) {
		// Set forward and backward links
		treatments.add(t);
		t.setPatient(this);
	}

	public <T> T exportTreatment(UUID tid, ITreatmentExporter<T> visitor) throws TreatmentExn {
		// Export a treatment without violating Aggregate pattern
		// Check that the exported treatment is a treatment for this patient.
		Treatment t = treatmentDao.getTreatment(tid);
		if (t.getPatient() != this) {
			throw new TreatmentExn("Inappropriate treatment access: patient = " + id + ", treatment = " + tid);
		}
		return t.export(visitor);
	}

	/**
	 * Map the treatment exporter over all of the treatments for this patient
	 */
	public <T> List<T> exportTreatments(ITreatmentExporter<T> visitor) throws TreatmentExn {
		List<T> exports = new ArrayList<T>();

		for (Treatment t : treatments) {
			exports.add(t.export(visitor));
		}

		return exports;
	}

	public Patient() {
		super();
		treatments = new ArrayList<Treatment>();
	}

}
