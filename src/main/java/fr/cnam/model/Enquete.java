package fr.cnam.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ENQUETE_AAT")
public class Enquete implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	// Identifiant fonctionnel (la session d'un PS)
	@Column(name="IDENTIFIANT")
	private String identifiant;
	
	// Navigateur du testeur
	@Column(name="NAVIGATEUR")
	private String navigateur;

	// Heure du demarrage du test
	@Column(name="HORODATAGE_1")
	private Date horodateur1;
	
	// Heure de fin de recherche
	@Column(name="HORODATAGE_2")
	private Date horodateur2;
	
	// Heure fin de l'enquete
	@Column(name="HORODATAGE_3")
	private Date horodateur3;
		
	// Heure fin de l'enquete
	@Column(name="HORODATAGE_4")
	private Date horodateur4;

	
	public String getIdentifiant() {
		return identifiant;
	}

	public void setIdentifiant(String identifiant) {
		this.identifiant = identifiant;
	}

	public String getNavigateur() {
		return navigateur;
	}

	public void setNavigateur(String navigateur) {
		this.navigateur = navigateur;
	}

	public Date getHorodateur1() {
		return horodateur1;
	}

	public void setHorodateur1(Date horodateur1) {
		this.horodateur1 = horodateur1;
	}

	public Date getHorodateur2() {
		return horodateur2;
	}

	public void setHorodateur2(Date horodateur2) {
		this.horodateur2 = horodateur2;
	}

	public Date getHorodateur3() {
		return horodateur3;
	}

	public void setHorodateur3(Date horodateur3) {
		this.horodateur3 = horodateur3;
	}

	public Date getHorodateur4() {
		return horodateur4;
	}

	public void setHorodateur4(Date horodateur4) {
		this.horodateur4 = horodateur4;
	}
	
}
