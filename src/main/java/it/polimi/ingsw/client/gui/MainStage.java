package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.GodCard;
import it.polimi.ingsw.client.ViewObserver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MainStage extends Application {

	private final static BlockingQueue<Object> lock = new LinkedBlockingQueue<>();
	private static List<ViewObserver> observerList = new ArrayList<>();

	private static Stage stage;

	//0 = name, 1 = number of Players, 2=PlayerID
	public static ArrayList<Object> playerData = new ArrayList<>();
	//stores the godPowers of the match
	public static List<GodCard> godPowers = new ArrayList<>();

	public static ArrayList<Object> getPlayerData(){return playerData;}
	public static List<ViewObserver> getObserverList(){return observerList;}
	public static List<GodCard> getGodPowers(){return godPowers;}

	public static Stage getStage() {
		return stage;
	}

	public static BlockingQueue<Object> getLock() { return lock; }

	public static void main() {
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		lock.add(new Object());

		//sets the primary stage
		this.stage = primaryStage;

		primaryStage.setTitle("Santorini");
		primaryStage.setMinHeight(774);
		primaryStage.setMinWidth(1386);

		//sets the first Scene as the Loading Scene
		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("LoginScene.fxml"));

		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});

	}



}
