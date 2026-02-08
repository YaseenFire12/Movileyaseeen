package edu.stevens.cs548.clinic.data;

import java.io.Serial;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//TODO JPA annotations

public class RadiologyTreatment extends Treatment {

	@Serial
    private static final long serialVersionUID = -3656673416179492428L;

	/*
	 * TODO Order by date.
	 */
	protected List<LocalDate> treatmentDates;

	public void addTreatmentDate(LocalDate date) {
		treatmentDates.add(date);
	}

	public RadiologyTreatment() {
		super();
		treatmentDates = new ArrayList<>();
	}
	
}
