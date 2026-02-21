package edu.stevens.cs548.clinic.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonTypeName("PHYSIOTHERAPY")
public final class PhysiotherapyTreatmentDto extends TreatmentDto {
	
	@JsonProperty("physiotherapy-dates")
	private List<LocalDate> treatmentDates;

	public List<LocalDate> getTreatmentDates() {
		return treatmentDates;
	}

	public void setTreatmentDates(List<LocalDate> treatmentDates) {
		this.treatmentDates = treatmentDates;
	}
	
	public PhysiotherapyTreatmentDto() {
		this.treatmentDates = new ArrayList<>();
	}

}
