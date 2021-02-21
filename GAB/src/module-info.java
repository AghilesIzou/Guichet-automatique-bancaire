module com.guichetBnacaire {
	
	requires transitive javafx.graphics;
	requires javafx.controls;
	requires javafx.base;
	requires javafx.fxml;
	requires java.smartcardio;
	exports com.guichetAutomatiqueBancaire;
	requires java.sql;
	requires itextpdf;
	requires java.desktop;
	
	
	
	
	opens com.guichetAutomatiqueBancaire to javafx.fxml;
	
	
	
}

