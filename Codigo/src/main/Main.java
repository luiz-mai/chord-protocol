package main;

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
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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


    public static ObservableList<String> receivedMessages = FXCollections.observableArrayList();
    public static ObservableList<String> sentMessages = FXCollections.observableArrayList();
    public static TextField sucessorID = new TextField();
    public static TextField sucessorIp = new TextField();
    public static TextField predecessorID = new TextField();
    public static TextField predecessorIp = new TextField();
	
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
		

		Text labelMyIp = new Text("MEU IP");
		labelMyIp.setTextAlignment(TextAlignment.CENTER);
		labelMyIp.setFill(Color.WHITE);
		labelMyIp.setFont(new Font(20));
		TextField myIp = new TextField();
		myIp.setEditable(false);
		VBox myIdBox = new VBox(labelMyIp, myIp);
		myIdBox.setAlignment(Pos.CENTER);
		

		Text labelSucessor = new Text("SUCESSOR:");
		labelSucessor.setTextAlignment(TextAlignment.CENTER);
		labelSucessor.setFill(Color.WHITE);
		labelSucessor.setFont(new Font(20));
		labelSucessor.minWidth(300);
		Text labelSucessorID = new Text("ID");
		labelSucessorID.setTextAlignment(TextAlignment.CENTER);
		labelSucessorID.setFill(Color.WHITE);
		labelSucessorID.setFont(new Font(12));
		sucessorID.setEditable(false);
		VBox sucessorIDBox = new VBox(sucessorID, labelSucessorID);
		sucessorIDBox.setAlignment(Pos.CENTER);
		Text labelSucessorIp = new Text("IP");
		labelSucessorIp.setTextAlignment(TextAlignment.CENTER);
		labelSucessorIp.setFill(Color.WHITE);
		labelSucessorIp.setFont(new Font(12));
		sucessorIp.setEditable(false);
		VBox sucessorIpBox = new VBox(sucessorIp, labelSucessorIp);
		sucessorIpBox.setPadding(new Insets(0,0,0,10));
		sucessorIpBox.setAlignment(Pos.CENTER);
		GridPane sucessorGrid = new GridPane();
		sucessorGrid.add(sucessorIDBox, 0, 0);
		sucessorGrid.setColumnSpan(sucessorIDBox, 3);
		sucessorGrid.add(sucessorIpBox, 4, 0);
		sucessorGrid.setColumnSpan(sucessorIpBox, 1);
		VBox sucessorBox = new VBox(5, labelSucessor, sucessorGrid);
		sucessorBox.setAlignment(Pos.CENTER);

		Text labelPredecessor = new Text("PREDECESSOR:");
		labelPredecessor.setTextAlignment(TextAlignment.CENTER);
		labelPredecessor.setFill(Color.WHITE);
		labelPredecessor.setFont(new Font(20));
		labelPredecessor.minWidth(300);
		Text labelPredecessorID = new Text("ID");
		labelPredecessorID.setTextAlignment(TextAlignment.CENTER);
		labelPredecessorID.setFill(Color.WHITE);
		labelPredecessorID.setFont(new Font(12));
		predecessorID.setEditable(false);
		VBox predecessorIDBox = new VBox(predecessorID, labelPredecessorID);
		predecessorIDBox.setAlignment(Pos.CENTER);
		Text labelPredecessorIp = new Text("IP");
		labelPredecessorIp.setTextAlignment(TextAlignment.CENTER);
		labelPredecessorIp.setFill(Color.WHITE);
		labelPredecessorIp.setFont(new Font(12));
		predecessorIp.setEditable(false);
		VBox predecessorIpBox = new VBox(predecessorIp, labelPredecessorIp);
		predecessorIpBox.setPadding(new Insets(0,0,0,10));
		predecessorIpBox.setAlignment(Pos.CENTER);
		GridPane predecessorGrid = new GridPane();
		predecessorGrid.add(predecessorIDBox, 0, 0);
		predecessorGrid.setColumnSpan(predecessorIDBox, 3);
		predecessorGrid.add(predecessorIpBox, 4, 0);
		predecessorGrid.setColumnSpan(predecessorIpBox, 1);
		VBox predecessorBox = new VBox(5, labelPredecessor, predecessorGrid);
		predecessorBox.setAlignment(Pos.CENTER);
		
		VBox sucPredBox = new VBox(sucessorBox, predecessorBox);
		VBox fieldsBox = new VBox(40, myIdBox, sucPredBox);
		
		
	    input = new FileInputStream("resources/images/leave_button.png");
		image = new Image(input);
		ImageView leaveButton = new ImageView(image);
		
		input = new FileInputStream("resources/images/lookup_button.png");
		image = new Image(input);
		ImageView lookupButton = new ImageView(image);
		
		VBox buttonsBox = new VBox(7, lookupButton, leaveButton);
		

		VBox sidebar = new VBox(60, mainLogo, fieldsBox, buttonsBox );
		sidebar.setId("sidebar");
		sidebar.setMaxWidth(300);
		sidebar.setPadding(new Insets(30));
		sidebar.setAlignment(Pos.TOP_CENTER);
		
		Text receivedMessagesLabel = new Text("MENSAGENS RECEBIDAS");
		receivedMessagesLabel.setTextAlignment(TextAlignment.CENTER);
		receivedMessagesLabel.setFill(Color.WHITE);
		receivedMessagesLabel.setFont(new Font(20));
		ScrollPane receivedMessagesScroll = new ScrollPane();
		ListView<String> receivedMessagesList = new ListView<String>();
	    receivedMessagesList.setItems(receivedMessages);
	    receivedMessagesList.setMinHeight(525);
	    receivedMessagesScroll.prefWidthProperty().bind(receivedMessagesList.widthProperty());
	    receivedMessagesScroll.prefHeightProperty().bind(receivedMessagesList.heightProperty());
	    receivedMessagesScroll.setContent(receivedMessagesList);
	    receivedMessagesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
	    receivedMessagesScroll.setMaxHeight(525);
	    receivedMessagesScroll.setMinHeight(525);
		receivedMessagesScroll.maxWidth(400);	
		receivedMessagesScroll.setMaxWidth(300);
		receivedMessagesScroll.setMinWidth(300);
		receivedMessagesScroll.setFitToWidth(true);
		VBox receivedMessagesBox = new VBox(receivedMessagesLabel, receivedMessagesScroll);
		receivedMessagesBox.setPadding(new Insets(20,15, 20, 10));
		receivedMessagesBox.setAlignment(Pos.CENTER);
		
		Text sentMessagesLabel = new Text("MENSAGENS ENVIADAS");
		sentMessagesLabel.setTextAlignment(TextAlignment.CENTER);
		sentMessagesLabel.setFill(Color.WHITE);
		sentMessagesLabel.setFont(new Font(20));
		ScrollPane sentMessagesScroll = new ScrollPane();
		ListView<String> sentMessagesList = new ListView<String>();
	    sentMessagesList.setItems(sentMessages);
	    sentMessagesList.setMinHeight(525);
	    sentMessagesScroll.prefWidthProperty().bind(sentMessagesList.widthProperty());
	    sentMessagesScroll.prefHeightProperty().bind(sentMessagesList.heightProperty());
	    sentMessagesScroll.setContent(sentMessagesList);
	    sentMessagesScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
	    sentMessagesScroll.setMaxHeight(525);
	    sentMessagesScroll.setMinHeight(525);
		sentMessagesScroll.maxWidth(400);	
		sentMessagesScroll.setMaxWidth(300);
		sentMessagesScroll.setMinWidth(300);
		sentMessagesScroll.setFitToWidth(true);
		VBox sentMessagesBox = new VBox(sentMessagesLabel, sentMessagesScroll);
		sentMessagesBox.maxWidth(400);
		sentMessagesBox.minWidth(400);
		sentMessagesBox.setPadding(new Insets(20,15, 20, 10));
		sentMessagesBox.setAlignment(Pos.CENTER);
		
		HBox mainContent = new HBox(receivedMessagesBox, sentMessagesBox);

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
        
        lookupButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			//CLICOU NO BOTÃO DE "DEIXAR REDE"
		     @Override
		     public void handle(MouseEvent event) {
		    	 TextInputDialog dialog = new TextInputDialog();
		    	 dialog.setHeaderText("");
		         dialog.setTitle("Lookup na rede");
		         dialog.setContentText("Por favor, digite o ID de um dos membros da rede:");
		         
		         
		         Optional<String> result = dialog.showAndWait();
		         if (result.isPresent()){
		             System.out.println("ID digitado: " + result.get());
		         }
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
        
        mainStage.setScene(scene);
        mainStage.centerOnScreen();
        mainStage.show();
        

		input = new FileInputStream("resources/images/icon.png");
		Image icon = new Image(input);
        mainStage.getIcons().add(icon);
        
	
        byte[] buffer = new byte[]{(byte) 0b1110_1010, 0b0101_1100, (byte) 0b1111_0011, 0b0000_0010, 0b0111_1010};
		JoinPacket jp = new JoinPacket(buffer,0);
		System.out.println(jp.toString());
	}
}

