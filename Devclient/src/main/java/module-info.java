module schule.fdslimburg.quiz.devclient.devclient {
	requires javafx.controls;
	requires javafx.fxml;

	requires org.controlsfx.controls;
	requires com.dlsc.formsfx;

	opens schule.fdslimburg.quiz.devclient.devclient to javafx.fxml;
	exports schule.fdslimburg.quiz.devclient.devclient;
}