package edu.stevens.cs548.clinic.data;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.persistence.OrderBy;
import java.io.Serial;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// TODO

public class PhysiotherapyTreatment extends Treatment {

	@Serial
    private static final long serialVersionUID = 5602950140629148756L;

	/*
	 * Order by date.
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@OrderBy
	protected List<LocalDate> treatmentDates;

	public void addTreatmentDate(LocalDate date) {
		treatmentDates.add(date);
	}

	public PhysiotherapyTreatment() {
		super();
		treatmentDates = new ArrayList<>();
	}

}
