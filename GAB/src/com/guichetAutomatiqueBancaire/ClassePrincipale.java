package com.guichetAutomatiqueBancaire;

import javax.smartcardio.CardException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClassePrincipale extends Application {
	

	@Override
	public void start(Stage interfaceAcceuil) throws Exception {
		
		
		try {
		
		Parent root = FXMLLoader.load(getClass().getResource("/ressources/fxml/Acceuil.fxml")); 
		Scene scene = new Scene(root);
		interfaceAcceuil.setTitle("Guichet Automatique Bancaire");
		interfaceAcceuil.setScene(scene);
		interfaceAcceuil.setResizable(false);
		interfaceAcceuil.show();
	
		}catch(Exception e) {
			
			e.printStackTrace();
		
		}
		
	}
	
	public static void main(String[] args) throws CardException {
		
       launch(args); 
    }
	
}


