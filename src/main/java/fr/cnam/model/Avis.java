package fr.cnam.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="AVIS")
public class Avis implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name="REPONSE_QUESTION_1")
	private String reponse1;
	
	@Column(name="REPONSE_QUESTION_2")
	private String reponse2;
	
	@Column(name="SESSION_ID")
	private String sessionId;
	
	@Column(name="LIBELLE_ASSOCIE")
	private String libelle;

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String liblle) {
		this.libelle = liblle;
	}

	public String getReponse1() {
		return reponse1;
	}

	public void setReponse1(String reponse1) {
		this.reponse1 = reponse1;
	}

	public String getReponse2() {
		return reponse2;
	}

	public void setReponse2(String reponse2) {
		this.reponse2 = reponse2;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Column(name="COMMENTAIRES")
	private String commentaire;


	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

}
