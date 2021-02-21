package com.guichetAutomatiqueBancaire;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class ChoixOperationExtController  implements Initializable{
	
	@FXML
	private AnchorPane opExt;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
	
	//affichage de l'interface Retrait
	public void  retrait(ActionEvent e) throws IOException {
		
		AnchorPane r = FXMLLoader.load(getClass().getResource("/ressources/fxml/Retrait.fxml"));
		opExt.getChildren().setAll(r);
		
	}
	//aller à l'nterface de fin
	public void  recupCarte(ActionEvent e) throws IOException {
		AnchorPane re = FXMLLoader.load(getClass().getResource("/ressources/fxml/Fin.fxml"));
		opExt.getChildren().setAll(re);
		
	}
	
	

}
