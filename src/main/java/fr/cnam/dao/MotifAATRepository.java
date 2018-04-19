package fr.cnam.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import fr.cnam.model.MotifAAT;

@Repository
public interface MotifAATRepository extends CrudRepository<MotifAAT, Long> {

	List<MotifAAT> findByLibelle(String libelle);

	List<MotifAAT> findByOrigine(String origine);

	List<MotifAAT> findByClicPlusFrequent(String plusFrequent);

	List<MotifAAT> findByClicListeComplete(String listeComplete);

	List<MotifAAT> findByClicActuel(String clicActuel);

	List<MotifAAT> findByCommentaire(String comment);

	List<MotifAAT> findByComplement(String complement);

	List<MotifAAT> findByResultatRecherche(String resultat);
}
