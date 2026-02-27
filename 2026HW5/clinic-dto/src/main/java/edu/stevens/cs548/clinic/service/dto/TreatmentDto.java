package edu.stevens.cs548.clinic.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "treatment-type")

public sealed abstract class TreatmentDto
		permits DrugTreatmentDto, PhysiotherapyTreatmentDto, RadiologyTreatmentDto, SurgeryTreatmentDto {

	private UUID id;

	@JsonProperty("patient-id")
	private UUID patientId;

	/*
	 * Use this to display patient name in list of treatments without N+1 problem.
	 */
	@JsonProperty("patient-name")
	private String patientName;

	@JsonProperty("provider-id")
	private UUID providerId;

	/*
	 * Use this to display provider name in list of treatments without N+1 problem.
	 */
	@JsonProperty("provider-name")
	private String providerName;

	private String diagnosis;

	@JsonProperty("followup-treatments")
	private Collection<TreatmentDto> followupTreatments;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getPatientId() {
		return patientId;
	}

	public void setPatientId(UUID patientId) {
		this.patientId = patientId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public UUID getProviderId() {
		return providerId;
	}

	public void setProviderId(UUID providerId) {
		this.providerId = providerId;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public Collection<TreatmentDto> getFollowupTreatments() {
		return followupTreatments;
	}

	public void setFollowupTreatments(Collection<TreatmentDto> treatments) {
		this.followupTreatments = treatments;
	}

	public TreatmentDto() {
		this.followupTreatments = new ArrayList<>();
	}

}
