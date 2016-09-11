package main;

import java.io.FileInputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import misc.Tools;
import net.ChordNode;
import net.LookupPacket;

public class Main extends Application {


	private Inet4Address computerIp;

	public static TextField myIp = new TextField();
    public static ObservableList<String> receivedMessages = FXCollections.observableArrayList();
    public static ObservableList<String> sentMessages = FXCollections.observableArrayList();
    public static TextField sucessorID = new TextField();
    public static TextField sucessorIp = new TextField();
    public static TextField predecessorID = new TextField();
    public static TextField predecessorIp = new TextField();
    public static ChordNode localNode;
	
	public static void main(String[] args){
		Application.launch(args);
	}

	@Override
	public void start(Stage mainStage) throws Exception {
		mainStage.setTitle("LCast");
		
		//FileInputStream input_bg = new FileInputStream("resources/images/bg-v.png");
		Image bg_v_image = new Image(this.getClass().getResourceAsStream("/resources/images/bg-v.png"));
		Background bg_v = new Background(new BackgroundImage(bg_v_image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT));
		//input_bg = new FileInputStream("resources/images/bg-h.png");
		Image bg_h_image = new Image(this.getClass().getResourceAsStream("/resources/images/bg-h.png"));
		Background bg_h = new Background(new BackgroundImage(bg_h_image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT));

		//INICIO - SPLASH SCREEN
		StackPane splashScreen = new StackPane();
		splashScreen.setId("splashScreen");
		splashScreen.setBackground(bg_v);
		
		//FileInputStream input = new FileInputStream("resources/images/big-logo.png");
		Image image = new Image(this.getClass().getResourceAsStream("/resources/images/big-logo.png"));
		ImageView logo = new ImageView(image);
		splashScreen.getChildren().add(logo);
        Scene scene = new Scene(splashScreen, 338, 600);
        scene.getStylesheets().add("css/style.css");
        mainStage.setScene(scene);
        mainStage.centerOnScreen();
        //FIM - SPLASH SCREEN
        
        //INICIO - ENTRADA DO IP
		ImageView smallLogo = new ImageView(image);
		smallLogo.setFitWidth(160);
		smallLogo.setPreserveRatio(true);
		smallLogo.setId("menu-logo");
		
		Text labelIpField = new Text("DIGITE O SEU IP ABAIXO:");
		labelIpField.setFill(Color.WHITE);
		labelIpField.setFont(new Font(16));
		TextField ipField = new TextField();
		ipField.setMaxWidth(220);
		ipField.setMinHeight(30);
		ipField.setAlignment(Pos.CENTER);
		
		
		VBox ipFieldBox = new VBox(8, labelIpField, ipField);
		ipFieldBox.setAlignment(Pos.CENTER);
		
		//input = new FileInputStream("resources/images/confirm_button.png");
		image = new Image(this.getClass().getResourceAsStream("/resources/images/confirm_button.png"));
		ImageView confirmButton = new ImageView(image);
		confirmButton.setId("create-button");
		
		VBox ipInput = new VBox(50, ipFieldBox, confirmButton);	
		ipInput.setAlignment(Pos.CENTER);
        
        VBox ipBox = new VBox(70, smallLogo, ipInput);		//VBox com o logo e os botões
        ipBox.setFillWidth(true);
        ipBox.setAlignment(Pos.CENTER);
        ipBox.setId("chooseID");
        ipBox.setBackground(bg_v);

        Scene scene2 = new Scene(ipBox, 338, 600);
        scene2.getStylesheets().add("css/style.css");
        
        //FIM - ENTRADA DO IP
        
        //INICIO - MAIN MENU
        VBox vbox = new VBox(70);		//VBox com o logo e os botões
        vbox.setFillWidth(true);
        vbox.setAlignment(Pos.CENTER);
        vbox.setId("mainMenu");
        vbox.setBackground(bg_v);
        Scene scene3 = new Scene(vbox, 338, 600);
        scene3.getStylesheets().add("css/style.css");
        
        VBox buttons = new VBox(10);	//VBox com os dois botões
        buttons.setAlignment(Pos.CENTER);

		//input = new FileInputStream("resources/images/big-logo.png");
		image = new Image(this.getClass().getResourceAsStream("/resources/images/big-logo.png"));
		smallLogo = new ImageView(image);
		smallLogo.setFitWidth(160);
		smallLogo.setPreserveRatio(true);
		smallLogo.setId("menu-logo");
		vbox.getChildren().add(smallLogo);
		
		//input = new FileInputStream("resources/images/create_button.png");
		image = new Image(this.getClass().getResourceAsStream("/resources/images/create_button.png"));
		ImageView createButton = new ImageView(image);
		createButton.setId("create-button");
		buttons.getChildren().add(createButton);

		//input = new FileInputStream("resources/images/join_button.png");
		image = new Image(this.getClass().getResourceAsStream("/resources/images/join_button.png"));
		ImageView joinButton = new ImageView(image);
		joinButton.setId("join-button");
		buttons.getChildren().add(joinButton);
		vbox.getChildren().add(buttons);
		//FIM - MAIN MENU
		
		//INICIO - TELA PRINCIPAL
		//input = new FileInputStream("resources/images/big-logo.png");
		image = new Image(this.getClass().getResourceAsStream("/resources/images/big-logo.png"));
		ImageView mainLogo = new ImageView(image);
		mainLogo.setFitWidth(200);
		mainLogo.setPreserveRatio(true);
		

		Text labelMyIp = new Text("MEU IP");
		labelMyIp.setTextAlignment(TextAlignment.CENTER);
		labelMyIp.setFill(Color.WHITE);
		labelMyIp.setFont(new Font(20));
		myIp.setEditable(false);
		myIp.setAlignment(Pos.CENTER);
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
		sucessorID.setAlignment(Pos.CENTER);
		VBox sucessorIDBox = new VBox(sucessorID, labelSucessorID);
		sucessorIDBox.setAlignment(Pos.CENTER);
		Text labelSucessorIp = new Text("IP");
		labelSucessorIp.setTextAlignment(TextAlignment.CENTER);
		labelSucessorIp.setFill(Color.WHITE);
		labelSucessorIp.setFont(new Font(12));
		sucessorIp.setEditable(false);
		sucessorIp.setAlignment(Pos.CENTER);
		VBox sucessorIpBox = new VBox(sucessorIp, labelSucessorIp);
		sucessorIpBox.setPadding(new Insets(0,0,0,10));
		sucessorIpBox.setAlignment(Pos.CENTER);
		GridPane sucessorGrid = new GridPane();
		sucessorGrid.add(sucessorIDBox, 0, 0);
		sucessorGrid.setColumnSpan(sucessorIDBox, 1);
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
		predecessorID.setAlignment(Pos.CENTER);
		VBox predecessorIDBox = new VBox(predecessorID, labelPredecessorID);
		predecessorIDBox.setAlignment(Pos.CENTER);
		Text labelPredecessorIp = new Text("IP");
		labelPredecessorIp.setTextAlignment(TextAlignment.CENTER);
		labelPredecessorIp.setFill(Color.WHITE);
		labelPredecessorIp.setFont(new Font(12));
		predecessorIp.setEditable(false);
		predecessorIp.setAlignment(Pos.CENTER);
		VBox predecessorIpBox = new VBox(predecessorIp, labelPredecessorIp);
		predecessorIpBox.setPadding(new Insets(0,0,0,10));
		predecessorIpBox.setAlignment(Pos.CENTER);
		GridPane predecessorGrid = new GridPane();
		predecessorGrid.add(predecessorIDBox, 0, 0);
		predecessorGrid.setColumnSpan(predecessorIDBox, 1);
		predecessorGrid.add(predecessorIpBox, 4, 0);
		predecessorGrid.setColumnSpan(predecessorIpBox, 1);
		VBox predecessorBox = new VBox(5, labelPredecessor, predecessorGrid);
		predecessorBox.setAlignment(Pos.CENTER);
		
		VBox sucPredBox = new VBox(sucessorBox, predecessorBox);
		VBox fieldsBox = new VBox(40, myIdBox, sucPredBox);
		
		
	    //input = new FileInputStream("resources/images/leave_button.png");
		image = new Image(this.getClass().getResourceAsStream("/resources/images/leave_button.png"));
		ImageView leaveButton = new ImageView(image);
		
		//input = new FileInputStream("resources/images/lookup_button.png");
		image = new Image(this.getClass().getResourceAsStream("/resources/images/lookup_button.png"));
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
		outerBox.setBackground(bg_h);
		Scene scene4 = new Scene(outerBox, 1000, 600);
        scene4.getStylesheets().add("css/style.css");
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
        
        ipField.setOnKeyPressed(new EventHandler<KeyEvent>()
	    {
	        @Override
	        public void handle(KeyEvent ke)
	        {
	            if (ke.getCode().equals(KeyCode.ENTER))
	            {
	            	try{
			    		 computerIp = (Inet4Address)Inet4Address.getByName(ipField.getText().toString());
				         myIp.setText(computerIp.getHostAddress());
			    		 mainStage.setScene(scene3);
				         mainStage.centerOnScreen();
			    	 } catch  (Exception UnknownHostException){
			    		 Alert alert = new Alert(AlertType.INFORMATION);
			    		 alert.setTitle("IP Inválido");
			    		 alert.setHeaderText(null);
			    		 alert.setContentText("Ops, parece que você digitou um IP inválido. Tente novamente.");
			    		 alert.showAndWait();
			    	 } 
	            }
	        }
	    });
        
        confirmButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			//CLICOU NO BOTÃO DE "CONFIRMAR"
		     @Override
		     public void handle(MouseEvent event) {
		    	 try{
		    		 computerIp = (Inet4Address)Inet4Address.getByName(ipField.getText().toString());
			         myIp.setText(computerIp.getHostAddress());
		    		 mainStage.setScene(scene3);
			         mainStage.centerOnScreen();
		    	 } catch  (Exception UnknownHostException){
		    		 Alert alert = new Alert(AlertType.INFORMATION);
		    		 alert.setTitle("IP Inválido");
		    		 alert.setHeaderText(null);
		    		 alert.setContentText("Ops, parece que você digitou um IP inválido. Tente novamente.");
		    		 alert.showAndWait();
		    	 } 
		         event.consume();
		     }
		});
        
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
			         
			         try {
						ChordNode.joinRing(computerIp, (Inet4Address)Inet4Address.getByName(result.get()));
			        	 mainStage.setScene(scene4);
				         mainStage.centerOnScreen();
					} catch (UnknownHostException e) {
			    		 Alert alert = new Alert(AlertType.INFORMATION);
			    		 alert.setTitle("IP Inválido");
			    		 alert.setHeaderText(null);
			    		 alert.setContentText("Ops, parece que você digitou um IP inválido. Tente novamente.");
			    		 alert.showAndWait();
					}
		         }
		         event.consume();
		     }
		});
        
        createButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			//CLICOU NO BOTÃO DE "CRIAR REDE"
		     @Override
		     public void handle(MouseEvent event) {
		         mainStage.setScene(scene4);
		         mainStage.centerOnScreen();
		         
		         Main.localNode = ChordNode.createRing(computerIp);
		         
		         event.consume();
		     }
		});
        
        lookupButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			//CLICOU NO BOTÃO DE "FAZER LOOKUP"
		     @Override
		     public void handle(MouseEvent event) {
		    	 TextInputDialog dialog = new TextInputDialog();
		    	 dialog.setHeaderText("");
		         dialog.setTitle("Lookup na rede");
		         dialog.setContentText("Por favor, digite o ID a ser pesquisado:");
		         
		         
		         Optional<String> result = dialog.showAndWait();
		         if (result.isPresent()){
		               
		             try {
		            	 LookupPacket lp = new LookupPacket(Main.localNode.getID(),Main.localNode.getIp(),Main.localNode.getID());
						Main.localNode.sendPacket(lp,Inet4Address.getByName("192.168.1.3"));
					} catch (UnknownHostException e) {
						System.out.println("Morri na hora de enviar o pacote do lookup.");
						e.printStackTrace();
					}
		             
		         }
		         event.consume();
		     }
		});
        
        leaveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			//CLICOU NO BOTÃO DE "DEIXAR REDE"
		     @Override
		     public void handle(MouseEvent event) {
	             localNode.closeSocket();
	             System.exit(0);
		         event.consume();
		     }
		});
        
        mainStage.setScene(scene);
        mainStage.centerOnScreen();
        mainStage.show();
        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
            	try{
            		localNode.closeSocket();
            		System.exit(0);
            	} catch (NullPointerException npe ){
            		System.exit(0);
            	}
            }
        });    
        
        

		//input = new FileInputStream("resources/images/icon.png");
		Image icon = new Image(this.getClass().getResourceAsStream("/resources/images/icon.png"));
        mainStage.getIcons().add(icon);

		
	}
	
	public static void setSucessorUI(ChordNode cn){
		main.Main.sucessorID.setText(Integer.toHexString(cn.getID()).toUpperCase()); 
		main.Main.sucessorIp.setText(cn.getIp().getHostAddress()); 
	}
	
	public static void setPredecessorUI(ChordNode cn){
		main.Main.predecessorID.setText(Integer.toHexString(cn.getID()).toUpperCase()); 
		main.Main.predecessorIp.setText(cn.getIp().getHostAddress());
	}
}

