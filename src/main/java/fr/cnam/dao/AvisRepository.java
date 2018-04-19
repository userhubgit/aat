package fr.cnam.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import fr.cnam.model.Avis;

@Repository
public interface AvisRepository extends CrudRepository<Avis, Long> {

}
