package edu.stevens.cs548.clinic.domain;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PatientFactory implements IPatientFactory {

	@Override
	public Patient createPatient() throws IPatientDao.PatientExn {
		Patient p = new Patient();
		return p;
	}

}
