package edu.stevens.cs548.clinic.data;

import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * Entity implementation class for Entity: Patient
 *
 */
@NamedQueries({
	@NamedQuery(
		name="SearchProviderByProviderId",
		query="select p from Provider p where p.id = :providerId"),
	@NamedQuery(
		name="CountProviderByProviderId",
		query="select count(p) from Provider p where p.id = :providerId"),
	@NamedQuery(
		name = "RemoveAllProviders", 
		query = "delete from Provider p")
})
// TODO

public class Provider implements Serializable {
		
	@Serial
    private static final long serialVersionUID = -876909316791083094L;

    // TODO PK (Do NOT auto-generate)
    private UUID id;
	
	private String npi;

	private String name;

    public UUID getId() { return id; }

    public void setId(UUID id) {
        this.id = id;
    }

	public String getNpi() {
		return npi;
	}

	public void setNpi(String npi) {
		this.npi = npi;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// TODO JPA annotations (propagate persist of provider to treatments)
	private Collection<Treatment> treatments;

	/*
	 * Addition and deletion of treatments should be done here.
	 */

	public boolean administers(Treatment t) {
		return treatments.contains(t);
	}
	
	public void addTreatment (Treatment t) {
		/*
		 * TODO complete this operation (see patient entity)
		 */

	}


	public Provider() {
		super();
		treatments = new ArrayList<>();
	}

}
