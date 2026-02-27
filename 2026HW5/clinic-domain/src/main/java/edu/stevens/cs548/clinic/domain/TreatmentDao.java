package edu.stevens.cs548.clinic.domain;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

@ApplicationScoped
@Transactional
public class TreatmentDao implements ITreatmentDao {

	private final Logger logger;

	private final EntityManager em;

	public TreatmentDao(Logger logger, EntityManager em) {
		this.logger = logger;
		this.em = em;
	}

	@Override
	public Treatment getTreatment(UUID id) throws TreatmentExn {
		/*
		 * Retrieve treatment using external key
		 */
		TypedQuery<Treatment> query = em.createNamedQuery("SearchTreatmentByTreatmentId", Treatment.class)
				.setParameter("treatmentId", id);
		List<Treatment> treatments = query.getResultList();

		if (treatments.size() > 1) {
			throw new TreatmentExn("Duplicate treatment records: treatment id = " + id);
		} else if (treatments.isEmpty()) {
			throw new TreatmentExn("Treatment not found: treatment id = " + id);
		} else {
			Treatment t = treatments.getFirst();
			/*
			 * Refresh from database or we will never see new follow-up treatments.
			 */
			em.refresh(t);
			return t;
		}
	}

	@Override
	public void addTreatment(Treatment t) {
		em.persist(t);
	}

}
