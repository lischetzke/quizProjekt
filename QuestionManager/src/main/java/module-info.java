module schule.fdslimburg.quiz.qm {
	requires javafx.controls;
	requires javafx.fxml;

	requires org.controlsfx.controls;
	requires com.dlsc.formsfx;
	requires com.google.gson;

	opens schule.fdslimburg.quiz.qm to javafx.fxml;
	exports schule.fdslimburg.quiz.qm;
	exports schule.fdslimburg.quiz.qm.data;
	opens schule.fdslimburg.quiz.qm.data to javafx.fxml;
}