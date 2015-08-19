package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import application.adress.view.controller.RootController;

public class MainSSLScan extends Application {

	private RootController rootController;
	private Stage primaryStage;
	private BorderPane rootPane;

	@Override
	public void start(Stage primaryStage) {

		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("SSL-Scan");

		try {
			initRootLayout();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void initRootLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader();

		loader.setLocation(MainSSLScan.class
				.getResource("adress/view/Root.fxml"));

		rootPane = loader.load();
		rootController = loader.getController();

		Scene rootScene = new Scene(rootPane);
		primaryStage.setScene(rootScene);
		rootController.setMainApp(this);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}
}
