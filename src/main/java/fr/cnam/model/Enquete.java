package fr.cnam.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Enquete {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String userInput;

	public String getUserInput() {
		return userInput;
	}

	public void setUserInput(String userInput) {
		this.userInput = userInput;
	}
}
