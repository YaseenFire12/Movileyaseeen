package edu.stevens.cs548.clinic.data;

public class TreatmentFactory implements ITreatmentFactory {
	
	/*
	 * Patient and provider fields are set when the treatment is added (see Provider).
	 * Id field is set when the treatment entity is synced with the database (see TreatmentDAO).
	 */

	@Override
	public DrugTreatment createDrugTreatment() {
		return new DrugTreatment();
	}

	public SurgeryTreatment createSurgeryTreatment() {
		return new SurgeryTreatment();
	}

	public RadiologyTreatment createRadiologyTreatment() {
		return new RadiologyTreatment();
	}

	public PhysiotherapyTreatment createPhysiotherapyTreatment() {
		return new PhysiotherapyTreatment();
	}
}
