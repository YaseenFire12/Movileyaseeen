package edu.stevens.cs548.clinic.data;

import jakarta.persistence.*;
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

@Entity
public class Provider implements Serializable {
		
	@Serial
    private static final long serialVersionUID = -876909316791083094L;

    @Id
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

	@OneToMany(cascade = CascadeType.PERSIST, mappedBy = "provider")
	private Collection<Treatment> treatments;

	public boolean administers(Treatment t) {
		return treatments.contains(t);
	}

	public void addTreatment (Treatment t) {
		treatments.add(t);
		if (t.getProvider() != this) {
			t.setProvider(this);
		}
	}


	public Provider() {
		super();
		treatments = new ArrayList<>();
	}

}
