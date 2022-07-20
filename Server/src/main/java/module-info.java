module schule.fdslimburg.quiz.server {
	requires javafx.controls;
	requires javafx.fxml;

	requires org.controlsfx.controls;
	requires com.dlsc.formsfx;
	requires org.kordamp.bootstrapfx.core;
	requires com.google.gson;

	opens schule.fdslimburg.quiz.server to javafx.fxml;
	exports schule.fdslimburg.quiz.server;
	exports schule.fdslimburg.quiz.server.backend.data;
	exports schule.fdslimburg.quiz.server.scenes.tableHelper;
}