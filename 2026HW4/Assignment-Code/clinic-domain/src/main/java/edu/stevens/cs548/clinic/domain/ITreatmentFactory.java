package edu.stevens.cs548.clinic.domain;

public interface ITreatmentFactory {

	public DrugTreatment createDrugTreatment();

	public SurgeryTreatment createSurgeryTreatment();

	public RadiologyTreatment createRadiologyTreatment();

	public PhysiotherapyTreatment createPhysiotherapyTreatment();

}
