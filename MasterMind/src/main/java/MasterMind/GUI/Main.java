package MasterMind.GUI;

import MasterMind.AI.AI;
import MasterMind.Game;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Game g = new Game();
        Controller c = new Controller(g);
        //AI ai = new AI(c,g);
        GUI gui = new GUI(c,g);
        Scene scene = new Scene(gui,640,480);
        primaryStage.setTitle("MasterMind");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
