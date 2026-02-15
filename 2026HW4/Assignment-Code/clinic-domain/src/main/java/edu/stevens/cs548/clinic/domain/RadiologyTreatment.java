package edu.stevens.cs548.clinic.domain;

import jakarta.persistence.*;
import java.io.Serial;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class RadiologyTreatment extends Treatment {

	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = -3656673416179492428L;

	@ElementCollection(fetch = FetchType.EAGER)
	@OrderBy
	protected List<LocalDate> treatmentDates;

	public void addTreatmentDate(LocalDate date) {
		treatmentDates.add(date);
	}

	@Override
	public <T> T export(ITreatmentExporter<T> visitor) {
		return visitor.exportRadiology(id,
				patient.getId(),
				patient.getName(),
				provider.getId(),
				provider.getName(),
				diagnosis,
				treatmentDates,
				() -> exportFollowupTreatments(visitor));
	}

	public RadiologyTreatment() {
		super();
		treatmentDates = new ArrayList<>();
	}

}
