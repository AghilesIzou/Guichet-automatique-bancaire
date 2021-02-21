package com.guichetAutomatiqueBancaire;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class CarteInvalideController implements Initializable  {
	
	@FXML
	private AnchorPane carteInvalide;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
	
	// aller à l'interface de fin
	
	public void recuperezBouton(ActionEvent e) throws IOException {
			AnchorPane rec = FXMLLoader.load(getClass().getResource("/ressources/fxml/Fin.fxml"));
			carteInvalide.getChildren().setAll(rec);
	}

}
