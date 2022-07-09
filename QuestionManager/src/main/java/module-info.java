module schule.fdslimburg.quiz.qm {
	requires javafx.controls;
	requires javafx.fxml;

	requires org.controlsfx.controls;
	requires com.dlsc.formsfx;

	opens schule.fdslimburg.quiz.qm to javafx.fxml;
	exports schule.fdslimburg.quiz.qm;
}