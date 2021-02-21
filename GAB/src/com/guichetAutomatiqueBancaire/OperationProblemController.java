package com.guichetAutomatiqueBancaire;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class OperationProblemController implements Initializable {
	
	@FXML
	private AnchorPane operationProblem;
	@FXML
	private  Label messageDerreur;
	private static String message;
	private static String mode;
	
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		messageDerreur.setText(message);
	}
	
	public static String getMessage() {
		return message;
	}
	//mettre à jour le message d'erreur à afficher lors d'une  éventuel problème
	public static void setMessage(String message) {
		
		OperationProblemController.message=message;
	}
	
    public static String getMode() {

        return mode;

    }
    
    
    public static void setMode(String mode) {

    	OperationProblemController.mode = mode;

    }
    
    public void okButton(ActionEvent e) throws IOException {
    	AnchorPane retour;
    	 if(mode.equals("Retrait")){
    		 retour= FXMLLoader.load(getClass().getResource("/ressources/fxml/Retrait.fxml"));
            
         }else{
        	 retour= FXMLLoader.load(getClass().getResource("/ressources/fxml/Depot.fxml")); 
         }

    	 operationProblem.getChildren().setAll(retour);
    
    
    
    }


}
