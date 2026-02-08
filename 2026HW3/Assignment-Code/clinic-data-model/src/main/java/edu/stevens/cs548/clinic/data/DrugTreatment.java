package edu.stevens.cs548.clinic.data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import jakarta.persistence.Entity;


@Entity
public class DrugTreatment extends Treatment implements Serializable {
	@Serial
    private static final long serialVersionUID = 1L;

	private String drug;
	
	private float dosage;
	
	private LocalDate startDate;
	
	private LocalDate endDate;
	
	private int frequency;

	public String getDrug() {
		return drug;
	}

	public void setDrug(String drug) {
		this.drug = drug;
	}

	public float getDosage() {
		return dosage;
	}

	public void setDosage(float dosage) {
		this.dosage = dosage;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public DrugTreatment() {
		super();
	}

}
