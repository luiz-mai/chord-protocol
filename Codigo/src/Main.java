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
import net.JoinPacket;

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
        mainStage.centerOnScreen();
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
		buttons.getChildren().add(createButton);

		input = new FileInputStream("resources/images/join_button.png");
		image = new Image(input);
		ImageView joinButton = new ImageView(image);
		joinButton.setId("join-button");
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
		ScrollPane idScroll = new ScrollPane();
		idScroll.setFitToWidth(true);		
		ListView<String> idList = new ListView<String>();
	    ObservableList<String> ids = FXCollections.observableArrayList(
	            "ID 1", "ID 2", "ID 3", "ID 4");
	    idList.setItems(ids);
	    idScroll.prefWidthProperty().bind(idList.widthProperty());
	    idScroll.prefHeightProperty().bind(idList.heightProperty());
	    idScroll.setContent(idList);
	    idScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
	    idScroll.setMaxHeight(220);
	    idScroll.setMinHeight(220);

		VBox idsBox = new VBox(labelIds, idList);
		idsBox.setAlignment(Pos.CENTER);
		

	    input = new FileInputStream("resources/images/leave_button.png");
		image = new Image(input);
		ImageView leaveButton = new ImageView(image);
		

		VBox sidebar = new VBox(80, mainLogo, idsBox, leaveButton );
		sidebar.setId("sidebar");
		sidebar.setMaxWidth(300);
		sidebar.setPadding(new Insets(30));
		sidebar.setAlignment(Pos.TOP_CENTER);
		
		Text messagesLabel = new Text("MENSAGENS:");
		messagesLabel.setTextAlignment(TextAlignment.CENTER);
		messagesLabel.setFill(Color.WHITE);
		messagesLabel.setFont(new Font(20));
		ScrollPane messagesScroll = new ScrollPane();
		messagesScroll.setFitToWidth(true);		
		ListView<String> messagesList = new ListView<String>();
	    ObservableList<String> messages = FXCollections.observableArrayList(
	            "Mensagem 1", "Mensagem 2", "Mensagem 3", "Mensagem 4");
	    messagesList.setItems(messages);
	    messagesList.setMinHeight(525);
	    messagesScroll.prefWidthProperty().bind(messagesList.widthProperty());
	    messagesScroll.prefHeightProperty().bind(messagesList.heightProperty());
	    messagesScroll.setContent(messagesList);
	    messagesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
	    messagesScroll.setMaxHeight(525);
	    messagesScroll.setMinHeight(525);
	    messagesScroll.setMinWidth(620);
		VBox mainContent = new VBox(messagesLabel, messagesScroll);
		mainContent.setPadding(new Insets(20,15, 20, 10));

		HBox outerBox = new HBox(25, sidebar, mainContent);
		outerBox.setId("outerbox");
		Scene scene3 = new Scene(outerBox, 1000, 600);
        scene3.getStylesheets().add("css/style.css");
		//FIM - TELA PRINCIPAL
        
		//INICIO - TRANSIÇÃO DA SPLASH SCREEN PARA MAIN MENU
		Timeline timeline = new Timeline();
		timeline.setDelay(new Duration(2000));
        KeyFrame key = new KeyFrame(Duration.millis(800),
                       new KeyValue (logo.opacityProperty(), 0)); 
        timeline.getKeyFrames().add(key);   
        timeline.setOnFinished((ae) -> 
        							mainStage.setScene(scene2)); 
       								mainStage.centerOnScreen();
        timeline.play();
		//FIM - TRANSIÇÃO DA SPLASH SCREEN PARA MAIN MENU
        
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
        
        createButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			//CLICOU NO BOTÃO DE "CRIAR REDE"
		     @Override
		     public void handle(MouseEvent event) {
		         mainStage.setScene(scene3);
		         mainStage.centerOnScreen();
		         event.consume();
		     }
		});
        
        leaveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			//CLICOU NO BOTÃO DE "DEIXAR REDE"
		     @Override
		     public void handle(MouseEvent event) {
	             System.out.println("Deixou a rede.");
	             mainStage.setScene(scene2);
	             mainStage.centerOnScreen();
		         event.consume();
		     }
		});
        
        //mainStage.setScene(scene);
        //mainStage.centerOnScreen();
        //mainStage.show();
	
        byte[] buffer = new byte[]{(byte) 0b1110_1010, 0b0101_1100, (byte) 0b1111_0011, 0b0000_0010, 0b0111_1010};
		JoinPacket jp = new JoinPacket(buffer,0);
		System.out.println(jp.toString());
	}
}

