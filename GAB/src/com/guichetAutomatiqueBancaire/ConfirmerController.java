package com.guichetAutomatiqueBancaire;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.central.Central;
import com.central.Routage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class ConfirmerController  implements Initializable {
	
	@FXML
	private AnchorPane ap;
	
    private static String labelValue;

    private static String modeOperation;

 

    public static String getValue() {

        return labelValue;

    }

    public static void setValue(String textOperation) {

        labelValue = textOperation;

    }

 
    //retourne le mode d'opération
    
    public static String getModeOperation() {

        return modeOperation;
    }

 
    public static void setModeOperation(String modeOperation) {

        ConfirmerController.modeOperation = modeOperation;

    }


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
	
	public void oui(ActionEvent e) throws SQLException, IOException {
		
		if(modeOperation.equals("Retrait")) {
			
			if(InsererCarteController.getTypeCLient().equals("Interne")) {
			
				if(Central.getSoldeInterne(InsererCarteController.numeroCarte)<RetraitController.getMontantRetrait()){
					OperationProblemController.setMessage("Votre  solde  est  insuffisant ! ");
					OperationProblemController.setMode("Retrait");
					AnchorPane root= FXMLLoader.load(getClass().getResource("/ressources/fxml/OperationProblem.fxml"));
					ap.getChildren().setAll(root);
				
			
			
				}else if (RetraitController.getMontantRetrait()>Central.getSeuilQuotidien(InsererCarteController.numeroCarte)) {
				
					OperationProblemController.setMessage("Il  vous  reste  à  retirer "+Central.getSeuilQuotidien(InsererCarteController.numeroCarte)+"  euros  pour  aujourd'hui");
					OperationProblemController.setMode("Retrait");
					AnchorPane root2= FXMLLoader.load(getClass().getResource("/ressources/fxml/OperationProblem.fxml"));
					ap.getChildren().setAll(root2);
				
				}else if(RetraitController.getMontantRetrait()>Central.getSeuilHebdomadaire(InsererCarteController.numeroCarte)) {
				
					OperationProblemController.setMessage("Il vous  reste  à  retirer  "+Central.getSeuilHebdomadaire(InsererCarteController.numeroCarte)+"  euros   pour   cette   semaine");
					OperationProblemController.setMode("Retrait");
					AnchorPane root1= FXMLLoader.load(getClass().getResource("/ressources/fxml/OperationProblem.fxml"));
					ap.getChildren().setAll(root1);
				
				
				}else {
				
				Central.retraitInterne(InsererCarteController.numeroCarte, RetraitController.getMontantRetrait()); //mise à jour du solde interne après avoir effectué une opération 
				AnchorPane p = FXMLLoader.load(getClass().getResource("/ressources/fxml/Ticket.fxml"));
				ap.getChildren().setAll(p);
				
				}
			}else {
				
				if(Routage.getSoldeExterne(InsererCarteController.numeroCarte)<RetraitController.getMontantRetrait()){
					OperationProblemController.setMessage("Votre  solde  est  insuffisant ! ");
					OperationProblemController.setMode("Retrait");
					AnchorPane rout= FXMLLoader.load(getClass().getResource("/ressources/fxml/OperationProblem.fxml"));
					ap.getChildren().setAll(rout);
				
			
			
				}else if (RetraitController.getMontantRetrait()>Routage.getSeuilQuotidien(InsererCarteController.numeroCarte)) {
				
					OperationProblemController.setMessage("Il  vous  reste  à  retirer "+Routage.getSeuilQuotidien(InsererCarteController.numeroCarte)+"  euros  pour  aujourd'hui");
					OperationProblemController.setMode("Retrait");
					AnchorPane rout2= FXMLLoader.load(getClass().getResource("/ressources/fxml/OperationProblem.fxml"));
					ap.getChildren().setAll(rout2);
				
				}else if(RetraitController.getMontantRetrait()>Routage.getSeuilHebdomadaire(InsererCarteController.numeroCarte)) {
				
					OperationProblemController.setMessage("Il vous  reste  à  retirer  "+Routage.getSeuilHebdomadaire(InsererCarteController.numeroCarte)+"  euros   pour   cette   semaine");
					OperationProblemController.setMode("Retrait");
					AnchorPane rout3= FXMLLoader.load(getClass().getResource("/ressources/fxml/OperationProblem.fxml"));
					ap.getChildren().setAll(rout3);
				
				
				}else {
				
				Routage.retraitExterne(InsererCarteController.numeroCarte, RetraitController.getMontantRetrait());//mise à jour du solde externe après avoir effectué une opération
				AnchorPane p = FXMLLoader.load(getClass().getResource("/ressources/fxml/Ticket.fxml"));
				ap.getChildren().setAll(p);
				
				}
				
			
			
			
			}
		
		
		}else {
			Central.depotInterne(InsererCarteController.numeroCarte, DepotController.getMontantDepot());
			AnchorPane p = FXMLLoader.load(getClass().getResource("/ressources/fxml/Ticket.fxml"));
			ap.getChildren().setAll(p);
		}
	
	 	
		
		
	}
	
	
	public void non(ActionEvent e ) throws IOException {
		
		if(modeOperation.equals("Retrait")) {
			
			AnchorPane n = FXMLLoader.load(getClass().getResource("/ressources/fxml/Retrait.fxml"));
			ap.getChildren().setAll(n);
			
		}else {
			
			AnchorPane ni = FXMLLoader.load(getClass().getResource("/ressources/fxml/Depot.fxml"));
			ap.getChildren().setAll(ni);
		}
		
	}

}
