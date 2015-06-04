package eu.musesproject.windowsclient.view;

/*
 * #%L
 * windows_client
 * %%
 * Copyright (C) 2013 - 2015 Sweden Connectivity
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import eu.musesproject.windowsclient.contextmonitoring.IUserContextMonitoringController;
import eu.musesproject.windowsclient.contextmonitoring.UserContextMonitoringController;

public class LoginMain extends Application implements Observer {
	private Stage primaryStage;
	static Button loginBtn,logoutBtn;
	static TextField userTextField;
	static PasswordField passwordField;
	private Observable o;
	
    public static void main(String[] args) {
    	RMI.startRMI();
        launch(args);
    }

    private void regiterCallbacks() {
    	MusesUICallbacksHandler uiCallbacksHandler = new MusesUICallbacksHandler();
    	UserContextMonitoringController.registerCallbacks(uiCallbacksHandler);
        uiCallbacksHandler.addObserver(this);
        setObservable(uiCallbacksHandler);
	}
    public void setObservable(Observable o){
    	this.o = o;
    }
   
	@Override
    public void start(Stage primaryStage) {
    	this.primaryStage = primaryStage;
        primaryStage.setTitle(LabelsAndText.MUSES_TITLE);
        String musesIconURL = LoginMain.class.getClassLoader().getResource("muses_icon.png").toString();
        Image musesIcon = new Image(musesIconURL);
        primaryStage.getIcons().add(musesIcon);

        loginBtn = new Button(LabelsAndText.LOGIN_LABEL);
        loginBtn.setStyle("-fx-background-color: grey; -fx-text-fill: white;");
        
        logoutBtn = new Button(LabelsAndText.LOGOUT);
        logoutBtn.setStyle("-fx-background-color: grey; -fx-text-fill: white;");
        
        loginBtn.setOnAction(actionEventListener);
        logoutBtn.setOnAction(actionEventListener);
        setLoginView();
    	regiterCallbacks();

    }

	public void setLoginView() {
		GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text(LabelsAndText.LOGIN_LABEL);
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));
        scenetitle.setStroke(Color.PURPLE);
        scenetitle.setFill(Color.MAGENTA);
        grid.add(scenetitle, 0, 0, 2, 1);

        Label description = new Label(LabelsAndText.TITLE_SUBTEXT);
        description.setWrapText(true);
        grid.add(description, 0, 1);
        
        Text userName = new Text(LabelsAndText.USERNAME);
        userName.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        grid.add(userName, 0, 2);

        userTextField = new TextField();
        userTextField.setPromptText("Enter your username here..");
        userTextField.setMinHeight(20);;
        grid.add(userTextField, 0, 3);

        Text password = new Text(LabelsAndText.PASSWORD);
        password.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        grid.add(password, 0, 4);

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password here..");
        passwordField.setMinHeight(20);
        grid.add(passwordField, 0, 5);

        Text privacyPolicy = new Text(LabelsAndText.PRIVACY_POLICY_LABEL);
        privacyPolicy.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        grid.add(privacyPolicy, 0, 6);

        Label policy = new Label(LabelsAndText.PRIVACY_POLICY);
        policy.setWrapText(true);
        grid.add(policy, 0, 7);

        HBox hbBtn = new HBox(15);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.getChildren().add(loginBtn);
        grid.add(hbBtn, 0, 8);
        
        Scene loginScene = new Scene(grid, 600, 550);
        primaryStage.setScene(loginScene);
        primaryStage.show();
	}
	
	public void setLogoutView() {
		
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
		
        Text scenetitle = new Text(LabelsAndText.LOGIN_LABEL);
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));
        scenetitle.setStroke(Color.PURPLE);
        scenetitle.setFill(Color.MAGENTA);
        grid.add(scenetitle, 0, 0, 2, 1);
		
        Text userName = new Text(LabelsAndText.LOGGED_IN);
        userName.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        grid.add(userName, 0, 2);
        
        Text password = new Text(LabelsAndText.SERVER_STATUS);
        password.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        grid.add(password, 0, 4);
        
        HBox hbBtn = new HBox(15);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.getChildren().add(logoutBtn);
        grid.add(hbBtn, 0, 8);
        
        Scene logoutScene = new Scene(grid, 600, 550);
        primaryStage.setScene(logoutScene);
	}

	final EventHandler<ActionEvent> actionEventListener = new EventHandler<ActionEvent>(){

	    @Override
	    public void handle(final ActionEvent event) {
	        if (event.getSource() == loginBtn){
	        	if (!userTextField.getText().trim().equals("") && !passwordField.getText().trim().equals("")) {
	            	IUserContextMonitoringController iMonitoringController = new UserContextMonitoringController();
	            	iMonitoringController.login(userTextField.getText(), passwordField.getText());
	            }
//	        	RMI rmi;
//	    		try {
//	    			rmi = new RMI();
//	    			rmi.sendResponseToMusesAwareApp("accepted");;
//	    		} catch (RemoteException e) {
//	    			// TODO Auto-generated catch block
//	    			e.printStackTrace();
//	    		}
	        	showDialog();
	        	setLogoutView();
	        } else if(event.getSource() == logoutBtn) {
	        	setLoginView();
	        }
	    }
	};
	
    private void showDialog() {
    	Stage dialogStage = new Stage();
    	Button okBtn = new Button(LabelsAndText.OK);
    	Text warningTxt = new Text(LabelsAndText.WARNING);
    	Text feedbackTxt = new Text("This is some test feedback");
    	feedbackTxt.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
    	
    	dialogStage.initModality(Modality.WINDOW_MODAL);
    	dialogStage.setScene(new Scene(VBoxBuilder.create().
    													children(warningTxt, feedbackTxt, okBtn).
    													alignment(Pos.CENTER).
    													padding(new Insets(5)).build()));
    	dialogStage.show();
    	okBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				
			}
		});
    	
    }
    
//	private void placeInSystemTray() {
//		Check the SystemTray is supported
//        if (!SystemTray.isSupported()) {
//            System.out.println("SystemTray is not supported");
//            return;
//        }
//        final PopupMenu popup = new PopupMenu();
//        //Image musesImage = Toolkit.getDefaultToolkit().getImage(LoginMain.class.getClassLoader().getResource("muses_icon.png").toString());
//        Image musesImage = new ImageIcon(LoginMain.class.getClassLoader().getResource("muses_icon.png")).getImage();
//        final TrayIcon trayIcon = new TrayIcon(musesImage,"muses_icon",popup);
//        final SystemTray tray = SystemTray.getSystemTray();
//       
//        // Create a pop-up menu components
//        MenuItem aboutItem = new MenuItem("About");
//        CheckboxMenuItem cb1 = new CheckboxMenuItem("Set auto size");
//        CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
//        Menu displayMenu = new Menu("Display");
//        MenuItem errorItem = new MenuItem("Error");
//        MenuItem warningItem = new MenuItem("Warning");
//        MenuItem infoItem = new MenuItem("Info");
//        MenuItem noneItem = new MenuItem("None");
//        MenuItem exitItem = new MenuItem("Exit");
//       
//        //Add components to pop-up menu
//        popup.add(aboutItem);
//        popup.addSeparator();
//        popup.add(cb1);
//        popup.add(cb2);
//        popup.addSeparator();
//        popup.add(displayMenu);
//        displayMenu.add(errorItem);
//        displayMenu.add(warningItem);
//        displayMenu.add(infoItem);
//        displayMenu.add(noneItem);
//        popup.add(exitItem);
//       
//        trayIcon.setPopupMenu(popup);
//       
//        try {
//            tray.add(trayIcon);
//        } catch (AWTException e) {
//            System.out.println("TrayIcon could not be added.");
//        }				
//	}
    
    
    
	@Override
	public void update(Observable o, Object msg) {
		Properties properties = (Properties)msg;
		int actionResponse = Integer.parseInt( properties.getProperty("action_response") );
		if (this.o == o) {
			switch (actionResponse) {
			case MusesUICallbacksHandler.LOGIN_SUCCESSFUL:
				System.out.println("Login was successful..");
				//System.out.println(msg.getData().get(JSONIdentifiers.AUTH_MESSAGE).toString());
				//stopProgress();
				//setLogoutView(); FIXME
				break;
			case MusesUICallbacksHandler.LOGIN_UNSUCCESSFUL:
				System.out.println("Login was successful..");
				//setLoginView(); FIXME
				break;
			case MusesUICallbacksHandler.ACTION_RESPONSE_ACCEPTED:
				System.out.println("Action response accepted ..");
				break;
			case MusesUICallbacksHandler.ACTION_RESPONSE_DENIED:
				System.out.println("Action response denied ..");
				//decisionName = msg.getData().getString("name");
				//riskTextualDecp = msg.getData().getString("risk_textual_decp");
//				showResultDialog(riskTextualDecp,
//						MusesUICallbacksHandler.ACTION_RESPONSE_DENIED);
				break;
			case MusesUICallbacksHandler.ACTION_RESPONSE_MAY_BE:
				System.out.println("Action response maybe ..");
				//decisionName = msg.getData().getString("name");
				//riskTextualDecp = msg.getData().getString("risk_textual_decp");
//				showResultDialog(riskTextualDecp,
//						MusesUICallbacksHandler.ACTION_RESPONSE_MAY_BE);
				break;
			case MusesUICallbacksHandler.ACTION_RESPONSE_UP_TO_USER:
				System.out.println("Action response upToUser ..");
//				decisionName = msg.getData().getString("name");
//				riskTextualDecp = msg.getData().getString("risk_textual_decp");
//				showResultDialog(riskTextualDecp,
//						MusesUICallbacksHandler.ACTION_RESPONSE_UP_TO_USER);
				break;
				
			}
		}	
		
	}


}