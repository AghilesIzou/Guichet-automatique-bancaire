package com.guichetAutomatiqueBancaire;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class CarteBloqueController implements Initializable {
	
	@FXML
	AnchorPane carteBloque;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
	
	// afficher l'interface de fin
	public void exit(ActionEvent e ) throws IOException {
		AnchorPane exit = FXMLLoader.load(getClass().getResource("/ressources/fxml/Fin.fxml"));
		carteBloque.getChildren().setAll(exit);
		
	}

}
