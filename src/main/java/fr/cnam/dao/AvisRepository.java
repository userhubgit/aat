package fr.cnam.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import fr.cnam.model.Avis;

@Repository
public interface AvisRepository extends CrudRepository<Avis, Long> {

	List<Avis> findByLibelle(final String libelle);
	
	List<Avis> findBySessionId(final String libelle);
}
