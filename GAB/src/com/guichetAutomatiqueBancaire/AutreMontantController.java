package com.guichetAutomatiqueBancaire;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class AutreMontantController  implements Initializable{

	@FXML
	private AnchorPane p; 
    @FXML

    private TextField montantRetire;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		montantRetire.setText("");
	}
	
		public void non(ActionEvent e ) throws IOException {
		
			AnchorPane n = FXMLLoader.load(getClass().getResource("/ressources/fxml/Retrait.fxml"));
			p.getChildren().setAll(n);
		}
		
		public void valider(ActionEvent e ) throws IOException {
			
			if(!montantRetire.getText().equals("")) { // vérifier si aucun montant n'a été saisi
            	int valeurRetrait = Integer.parseInt(montantRetire.getText());
            	if(valeurRetrait<=0) {
            		OperationProblemController.setMessage("Vous  avez  saisi  un  montant  négatif  ou  nul ! "); // message d'erreure
            		OperationProblemController.setMode("Retrait");
            		AnchorPane roott= FXMLLoader.load(getClass().getResource("/ressources/fxml/OperationProblem.fxml"));
                    p.getChildren().setAll(roott);
            		
            	}else if(valeurRetrait <10 && valeurRetrait>0) { //vérfier si le montant saisi est inférieur à 10 en étant positif
            		
            		OperationProblemController.setMessage("Veuillez  saisir  un  montant  supérieur  à  10  euros ! ");
            		OperationProblemController.setMode("Retrait");
            		AnchorPane roott1= FXMLLoader.load(getClass().getResource("/ressources/fxml/OperationProblem.fxml"));
                    p.getChildren().setAll(roott1);
            		
            	}else if(valeurRetrait % 10 !=0) { // vérfier si le montant saisi est un multiple de 10
            		
            		OperationProblemController.setMessage("Veuillez  saisir  un  montant  multiple de 10 ! ");
            		OperationProblemController.setMode("Retrait");
            		AnchorPane roo= FXMLLoader.load(getClass().getResource("/ressources/fxml/OperationProblem.fxml"));
                    p.getChildren().setAll(roo);
            		
                	
                }else {
            	
            		RetraitController.setMontantRetrait(valeurRetrait);
            		ConfirmerController.setModeOperation("Retrait");
            		ConfirmerController.setValue("Retrait de "+valeurRetrait+" ?");
            		AnchorPane root =FXMLLoader.load(getClass().getResource("/ressources/fxml/ConfirmerOperation.fxml"));
            		p.getChildren().setAll(root);

            	}
            }else {
            	
            	OperationProblemController.setMessage("Vous  avez  saisi  aucun   montant !");
            	OperationProblemController.setMode("Retrait");
            	AnchorPane  root1 =FXMLLoader.load(getClass().getResource("/ressources/fxml/OperationProblem.fxml"));
            	p.getChildren().setAll(root1);

          }
			
		}

}
