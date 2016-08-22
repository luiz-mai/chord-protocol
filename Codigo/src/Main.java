import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {


	
	public static void main(String[] args){
		Application.launch(args);
	}

	@Override
	public void start(Stage mainStage) throws Exception {
		mainStage.setTitle("LCast Splash Screen");
		
		StackPane root = new StackPane();
		root.setId("root");
		
		FileInputStream input = new FileInputStream("resources/images/big-logo.png");
		Image image = new Image(input);
		ImageView logo = new ImageView(image);
		
		root.getChildren().add(logo);
		
        Scene scene = new Scene(root, 338, 600);
        
        scene.getStylesheets().add("css/style.css");
        mainStage.setScene(scene);
        
        
        mainStage.show();
		
	}
}

