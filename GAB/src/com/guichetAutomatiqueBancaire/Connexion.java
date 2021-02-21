package com.guichetAutomatiqueBancaire;
 import java.sql.Connection;


 import java.sql.DriverManager;
 import java.sql.SQLException;

public class Connexion {
	
public   String utilisateur, motDePasse, url;

	//constructeur afin d'initialiser les attributs d'instances
	
	public Connexion(String utilisateur, String motDePasse, String url) {
		
		this.utilisateur= utilisateur;
		this.motDePasse= motDePasse;
		this.url=url;
		
	}

// cette méthode permet de changer l'utilisateur 
	
	public void setUser(String utilisateur) {
		
		this.utilisateur=utilisateur;
	}
	
	// cette méthode retourne l'utilisateur
	
	public String getUtilisateur() {
		
		return this.utilisateur;
	}

	// cette méthode permet de changer le mot de passe d'un utilisateur de la base de données
	
	public void setMdp(String motDePasse) {
		
		this.motDePasse=motDePasse;
	}
	
	//cette méthode retourne le mot de passe d'un utilisateur de la base de données ayant des privilèges
	
	public String getMotDePasse() {
		
		return this.motDePasse;
	}

	//	cette méthode change le chemin vers la base de données.
	
	public  void setUrl(String url) {
		
		this.url = url;
	}
	
	//cette méthode retourne l'url 
	
	public String getUrl() {
		
		return this.url;
	}
	
	
	/*	méthode static connect (): cette méthode nous permet de nous connecter à la base de données dont l'url est passé en paramètre  .
	  ainsi que le nom d'utilisateur et  son mot de passe*/
	@SuppressWarnings("exports")
	
	public static Connection connect(String url, String utilisateur, String motDePasse) throws SQLException {
		
		return DriverManager.getConnection(url, utilisateur, motDePasse); //retourne un objet de type Connection

	}
	
	@SuppressWarnings("exports")
	public Connection connect() throws SQLException {
		
		// DriverManager : classe qui s'occupe de charger le driver et d'établir une connexion à la BDD
		
		return DriverManager.getConnection(this.url, this.utilisateur, this.motDePasse);
		
	}






}
