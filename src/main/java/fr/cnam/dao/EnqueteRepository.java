package fr.cnam.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import fr.cnam.model.Enquete;

@Repository
public interface EnqueteRepository extends CrudRepository<Enquete, Long> {

	List<Enquete> findByNavigateur(String navigateur);

	List<Enquete> findByTransmission(String transmission);

	Enquete findByIdentifiant(String identifiant);
}
