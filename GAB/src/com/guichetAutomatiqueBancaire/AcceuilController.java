package com.guichetAutomatiqueBancaire;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;


public class AcceuilController implements Initializable{
	
	
	@FXML 
	private AnchorPane acceuil;
	
	@FXML
	public  void handleCliquezCommencer(ActionEvent ev) {
		
		try{
			
			AnchorPane root = FXMLLoader.load(getClass().getResource("/ressources/fxml/InsererCarte.fxml")); //charger l'interface
			acceuil.getChildren().setAll(root); // remplacer la scene courante par root
			
        }catch(Exception e){
			
        	e.printStackTrace();
		}
	}

	//permet d'initialiser ce qui doit être initialiser
	public void initialize(URL location, ResourceBundle ressources){} // faire croire au compilateur que l'on l'a implémenté


}
