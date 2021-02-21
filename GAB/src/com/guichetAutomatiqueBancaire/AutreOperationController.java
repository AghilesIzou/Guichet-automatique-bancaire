package com.guichetAutomatiqueBancaire;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class AutreOperationController implements Initializable {
	@FXML
	private AnchorPane autre;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
	}
	// actionnez le bouton oui 
	
	public void ouiButton(ActionEvent e ) throws IOException {
		
		if(InsererCarteController.getTypeCLient().equals("Interne")) {
			
			AnchorPane rt= FXMLLoader.load(getClass().getResource("/ressources/fxml/ChoixOperationInt.fxml"));//chargement du fichier fxml
			autre.getChildren().setAll(rt);// remplacement par la nouvelle scène
		
		}else {
			
			AnchorPane rtt= FXMLLoader.load(getClass().getResource("/ressources/fxml/ChoixOperationExt.fxml"));
			autre.getChildren().setAll(rtt);
		}
	
	}
	
	// actionnez le bouton non
	
	public void nonButton(ActionEvent e ) throws IOException {
		
		AnchorPane rt1= FXMLLoader.load(getClass().getResource("/ressources/fxml/Fin.fxml"));
		autre.getChildren().setAll(rt1);
		
	}

}
