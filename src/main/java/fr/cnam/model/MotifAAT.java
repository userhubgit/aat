package fr.cnam.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MOTIF")
public class MotifAAT implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "LIBELLE")
	private String libelle;

	@Column(name = "COMPLEMENT")
	private String complement;

	@Column(name = "ORIGINE_MOTIF")
	private String origine;

	@Column(name = "CLIC_LISTE_COMPLETE ")
	private String clicListeComplete;

	@Column(name = "CLIC_LES_PLUS_FREQUENTS")
	private String clicPlusFrequent;

	@Column(name = "CLIC_EN_CE_MOMENT")
	private String clicActuel;

	@Column(name = "RESULTAT_RECHERCHE")
	private String resultatRecherche;

	@Column(name = "COMMENTAIRES")
	private String commentaire;

	@Column(name = "SESSION_ID")
	private String sessionId;

	public MotifAAT() {

	}

	public MotifAAT(MotifAAT motif) {
		if (null != motif) {
			this.setSessionId(motif.getSessionId());
			this.setLibelle(motif.getLibelle());
			this.setClicActuel(motif.getClicActuel());
			this.setClicListeComplete(motif.getClicListeComplete());
			this.setClicPlusFrequent(motif.getClicPlusFrequent());
			this.setCommentaire(motif.getCommentaire());
			this.setComplement(motif.getComplement());
			this.setOrigine(motif.getOrigine());
			this.setResultatRecherche(motif.getResultatRecherche());
		}
	}

	public String getResultatRecherche() {
		return resultatRecherche;
	}

	public void setResultatRecherche(String resultatRecherche) {
		this.resultatRecherche = resultatRecherche;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getComplement() {
		return complement;
	}

	public void setComplement(String complement) {
		this.complement = complement;
	}

	public String getOrigine() {
		return origine;
	}

	public void setOrigine(String origine) {
		this.origine = origine;
	}

	public String getClicListeComplete() {
		return clicListeComplete;
	}

	public void setClicListeComplete(String clicListeComplete) {
		this.clicListeComplete = clicListeComplete;
	}

	public String getClicPlusFrequent() {
		return clicPlusFrequent;
	}

	public void setClicPlusFrequent(String clicPlusFrequent) {
		this.clicPlusFrequent = clicPlusFrequent;
	}

	public String getClicActuel() {
		return clicActuel;
	}

	public void setClicActuel(String clicActuel) {
		this.clicActuel = clicActuel;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

}
