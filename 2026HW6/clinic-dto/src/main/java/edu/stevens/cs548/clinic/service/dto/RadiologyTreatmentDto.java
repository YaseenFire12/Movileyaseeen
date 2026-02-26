package edu.stevens.cs548.clinic.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonTypeName("RADIOLOGY")
public final class RadiologyTreatmentDto extends TreatmentDto {
	
	@JsonProperty("radiology-dates")
	private List<LocalDate> treatmentDates;

	public List<LocalDate> getTreatmentDates() {
		return treatmentDates;
	}

	public void setTreatmentDates(List<LocalDate> treatmentDates) {
		this.treatmentDates = treatmentDates;
	}
	
	public RadiologyTreatmentDto() {
		this.treatmentDates = new ArrayList<>();
	}

}
