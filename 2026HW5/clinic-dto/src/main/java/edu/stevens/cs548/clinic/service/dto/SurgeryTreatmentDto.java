package edu.stevens.cs548.clinic.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.LocalDate;

@JsonTypeName("SURGERY")
public final class SurgeryTreatmentDto extends TreatmentDto {

	@JsonProperty("surgery-date")
	private LocalDate surgeryDate;

	@JsonProperty("discharge-instructions")
	private String dischargeInstructions;

	public LocalDate getSurgeryDate() {
		return surgeryDate;
	}

	public void setSurgeryDate(LocalDate surgeryDate) {
		this.surgeryDate = surgeryDate;
	}

	public String getDischargeInstructions() {
		return dischargeInstructions;
	}

	public void setDischargeInstructions(String dischargeInstructions) {
		this.dischargeInstructions = dischargeInstructions;
	}

}
