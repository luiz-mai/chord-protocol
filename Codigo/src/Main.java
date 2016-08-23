import java.io.FileInputStream;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {


	
	public static void main(String[] args){
		Application.launch(args);
	}

	@Override
	public void start(Stage mainStage) throws Exception {
		mainStage.setTitle("LCast");
		
		//INICIO - SPLASH SCREEN
		StackPane splashScreen = new StackPane();
		splashScreen.setId("splashScreen");
		
		FileInputStream input = new FileInputStream("resources/images/big-logo.png");
		Image image = new Image(input);
		ImageView logo = new ImageView(image);
		splashScreen.getChildren().add(logo);
        Scene scene = new Scene(splashScreen, 338, 600);
        scene.getStylesheets().add("css/style.css");
        mainStage.setScene(scene);
        //FIM - SPLASH SCREEN
        
        
        //INICIO - MAIN MENU
        VBox vbox = new VBox(70);		//VBox com o logo e os botões
        vbox.setFillWidth(true);
        vbox.setAlignment(Pos.CENTER);
        vbox.setId("mainMenu");
        Scene scene2 = new Scene(vbox, 338, 600);
        scene2.getStylesheets().add("css/style.css");
        
        VBox buttons = new VBox(10);	//VBox com os dois botões
        buttons.setAlignment(Pos.CENTER);
        
		ImageView smallLogo = new ImageView(image);
		smallLogo.setFitWidth(160);
		smallLogo.setPreserveRatio(true);
		smallLogo.setId("menu-logo");
		vbox.getChildren().add(smallLogo);
		
		input = new FileInputStream("resources/images/create_button.png");
		image = new Image(input);
		ImageView createButton = new ImageView(image);
		createButton.setId("create-button");
		createButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			//CLICOU NO BOTÃO DE "CRIAR REDE"
		     @Override
		     public void handle(MouseEvent event) {
		         System.out.println("Criou rede");
		         event.consume();
		     }
		});
		buttons.getChildren().add(createButton);

		input = new FileInputStream("resources/images/join_button.png");
		image = new Image(input);
		ImageView joinButton = new ImageView(image);
		joinButton.setId("join-button");
		joinButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			//CLICOU NO BOTÃO DE "ENTRAR NA REDE"
		     @Override
		     public void handle(MouseEvent event) {
		    	 TextInputDialog dialog = new TextInputDialog();
		    	 dialog.setHeaderText("");
		         dialog.setTitle("Entrar em uma rede");
		         dialog.setContentText("Por favor, digite o IP de um dos membros da rede:");
		         
		         
		         Optional<String> result = dialog.showAndWait();
		         if (result.isPresent()){
		             System.out.println("IP digitado: " + result.get());
		         }
		         event.consume();
		     }
		});
		buttons.getChildren().add(joinButton);
		vbox.getChildren().add(buttons);
		//FIM - MAIN MENU
		
		//INICIO - TELA PRINCIPAL
		input = new FileInputStream("resources/images/big-logo.png");
		image = new Image(input);
		ImageView mainLogo = new ImageView(image);
		mainLogo.setFitWidth(200);
		mainLogo.setPreserveRatio(true);
		

		Text labelIds = new Text("MEMBROS DA REDE");
		labelIds.setTextAlignment(TextAlignment.CENTER);
		labelIds.setFill(Color.WHITE);
		labelIds.setFont(new Font(20));
		ScrollPane idList = new ScrollPane();
		ListView<String> list = new ListView<String>();
	    ObservableList<String> items = FXCollections.observableArrayList(
	            "Single", "Double", "Suite", "Family App");
	    list.setItems(items);
	    idList.prefWidthProperty().bind(list.widthProperty());
	    idList.prefHeightProperty().bind(list.heightProperty());
	    idList.setContent(list);
	    idList.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
	    idList.setMaxHeight(220);
	    idList.setMinHeight(220);

		VBox idsBox = new VBox(labelIds, idList);
		idsBox.setAlignment(Pos.CENTER);
		

		VBox sidebar = new VBox(50, mainLogo, idsBox);
		sidebar.setId("sidebar");
		sidebar.setMaxWidth(300);
		sidebar.setPadding(new Insets(30));
		sidebar.setAlignment(Pos.TOP_CENTER);
		
		VBox mainContent = new VBox();
		HBox outerBox = new HBox(50, sidebar, mainContent);
		outerBox.setId("outerbox");
		Scene scene3 = new Scene(outerBox, 1000, 600);
        scene3.getStylesheets().add("css/style.css");
		//FIM - TELA PRINCIPAL
        
		//INICIO - TRANSIÇÃO DA SPLASH SCREEN PARA MAIN MENU
	/*	Timeline timeline = new Timeline();
		timeline.setDelay(new Duration(2000));
        KeyFrame key = new KeyFrame(Duration.millis(800),
                       new KeyValue (logo.opacityProperty(), 0)); 
        timeline.getKeyFrames().add(key);   
        timeline.setOnFinished((ae) -> 
        							mainStage.setScene(scene2)); 
        timeline.play();*/
		//FIM - TRANSIÇÃO DA SPLASH SCREEN PARA MAIN MENU
        
        mainStage.setScene(scene3);
        mainStage.show();
	}
}

