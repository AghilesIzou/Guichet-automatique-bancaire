package com.guichetAutomatiqueBancaire;

import java.io.IOException;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class RetraitController implements Initializable {
	
	@FXML
	private AnchorPane tr;
	
	private static int montantRetrait;
	
    public static int getMontantRetrait() {

        return montantRetrait;

    }

    //mettre à jour le montant choisi par le client
    public static void setMontantRetrait(int montantRetrait) {

        RetraitController.montantRetrait = montantRetrait;

    }


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
	
	public void retrait10(ActionEvent e) throws IOException {
       
		ConfirmerController.setValue("Retrait de 10 ? ");

        ConfirmerController.setModeOperation("Retrait");
        montantRetrait=10; //montant choisi par le client
       
        AnchorPane r = FXMLLoader.load(getClass().getResource("/ressources/fxml/ConfirmerOperation.fxml"));
        tr.getChildren().setAll(r);
        

	}
	
	public void retrait20(ActionEvent e) throws IOException {
	       
		ConfirmerController.setValue("Retrait de 20 ? ");

        ConfirmerController.setModeOperation("Retrait");
         montantRetrait=20;
       
        AnchorPane r = FXMLLoader.load(getClass().getResource("/ressources/fxml/ConfirmerOperation.fxml"));
        tr.getChildren().setAll(r);
        

	}
	
	public void retrait30(ActionEvent e) throws IOException {
	       
		ConfirmerController.setValue("Retrait de 30 ? ");

        ConfirmerController.setModeOperation("Retrait");
         montantRetrait=30;
       
        AnchorPane r = FXMLLoader.load(getClass().getResource("/ressources/fxml/ConfirmerOperation.fxml"));
        tr.getChildren().setAll(r);
        

	}
	

	public void retrait50(ActionEvent e) throws IOException {
	       
		ConfirmerController.setValue("Retrait de 50 ? ");

        ConfirmerController.setModeOperation("Retrait");
         montantRetrait=50;
       
        AnchorPane r = FXMLLoader.load(getClass().getResource("/ressources/fxml/ConfirmerOperation.fxml"));
        tr.getChildren().setAll(r);
        

	}
	

	public void retrait100(ActionEvent e) throws IOException {
	       
		ConfirmerController.setValue("Retrait de 100 ? ");

        ConfirmerController.setModeOperation("Retrait");
        montantRetrait=100;
       
        AnchorPane r = FXMLLoader.load(getClass().getResource("/ressources/fxml/ConfirmerOperation.fxml"));
        tr.getChildren().setAll(r);
        

	}
	
	public void autreMontant(ActionEvent e) throws IOException {
	       
		AnchorPane r = FXMLLoader.load(getClass().getResource("/ressources/fxml/AutreMontant.fxml"));
        tr.getChildren().setAll(r);
   }
	
	//retour à l'interface choix d'opération
	public void exit(ActionEvent e) throws IOException {
		
		switch(InsererCarteController.getTypeCLient()) {
		
			case "Interne" :
				
				AnchorPane exit = FXMLLoader.load(getClass().getResource("/ressources/fxml/ChoixOperationInt.fxml"));
				tr.getChildren().setAll(exit);
				break;
		
			case "Externe" :
				
				AnchorPane ext = FXMLLoader.load(getClass().getResource("/ressources/fxml/ChoixOperationExt.fxml"));
				tr.getChildren().setAll(ext);
				break;
		}
		
	}
	
	
}
