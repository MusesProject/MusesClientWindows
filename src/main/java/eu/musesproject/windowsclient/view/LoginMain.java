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

import java.awt.Dimension;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.JFrame;

import eu.musesproject.client.model.JSONIdentifiers;
import eu.musesproject.windowsclient.actuators.ActuatorController;
import eu.musesproject.windowsclient.connectionmanager.Statuses;
import eu.musesproject.windowsclient.contextmonitoring.UserContextMonitoringController;
import eu.musesproject.windowsclient.contextmonitoring.sensors.SettingsSensor;
import eu.musesproject.windowsclient.model.DBManager;
import eu.musesproject.windowsclient.model.UserCredentials;
import eu.musesproject.windowsclient.usercontexteventhandler.UserContextEventHandler;
import eu.musesproject.windowsclient.view.Toast.Style;

public class LoginMain extends Application implements Observer{
	private static final String APP_TAG = "APP_TAG";
	private Stage primaryStage;
	static Button loginBtn,logoutBtn;
	static TextField userTextField;
	static PasswordField passwordField;
	static CheckBox rememberCredentialsBox, agreeTermBox;

	public static boolean isLoggedIn = false;
	
	boolean isPrivacyPolicyAgreementChecked = false;
	boolean isSaveCredentialsChecked = false;
	
	private Observable o;
	
	public static String language;
	public static String country;
	private static Locale currentLocale;
	private static ResourceBundle messages;
	private UserContextMonitoringController userContextMonitoringController;


	public static void main(String[] args) {
		setupLocale(args);
		RMI.startRMI();
		launch(args);
	}

	private static void setupLocale(String[] args) {
        if (args.length != 2) {
            language = new String("en");
            country = new String("US");
        } else {
            language = new String(args[0]);
            country = new String(args[1]);
        }
	}

	public void setObservable(Observable o){
    	this.o = o;
    }
   
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		primaryStage.setTitle(MusesUtils.getResourceBundle().getString("app_name"));
		
		String musesIconURL = LoginMain.class.getClassLoader().getResource("muses_icon.png").toString();
		Image musesIcon = new Image(musesIconURL);
		primaryStage.getIcons().add(musesIcon);

		loginBtn = new Button(MusesUtils.getResourceBundle().getString("login_button_txt"));
		loginBtn.setStyle("-fx-background-color: grey; -fx-text-fill: white;");

		logoutBtn = new Button(MusesUtils.getResourceBundle().getString("logout_button_txt"));
		logoutBtn.setStyle("-fx-background-color: grey; -fx-text-fill: white;");

		loginBtn.setOnAction(actionEventListener);
		logoutBtn.setOnAction(actionEventListener);

		rememberCredentialsBox = new CheckBox(MusesUtils.getResourceBundle().getString("remember_my_login_txt"));
		rememberCredentialsBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				isSaveCredentialsChecked = newValue;
			}
		});
		
		agreeTermBox = new CheckBox(MusesUtils.getResourceBundle().getString("agree_term_condition_txt"));
		agreeTermBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				isPrivacyPolicyAgreementChecked = newValue;
			}
		});
		
		setLoginView();
		regiterCallbacks();
		userContextMonitoringController = UserContextMonitoringController.getInstance();
//		initSchedulerForPolling();
		onResume();
		
	}

	private void onResume() {
		DBManager dbManager = new DBManager();
		dbManager.openDB();
		boolean isActive = dbManager.isSilentModeActive();
		dbManager.closeDB();
		if (!isActive) {
			// FIXME what should be done here??
			
//			topLayout.removeAllViews();
//			topLayout.addView(loginView);
//			topLayout.addView(securityQuizView);
		}
		
	}

	final EventHandler<ActionEvent> actionEventListener = new EventHandler<ActionEvent>() {

		@Override
		public void handle(final ActionEvent event) {
			if (event.getSource() == loginBtn) {
				if (isPrivacyPolicyAgreementChecked){
					doLogin(userTextField.getText(),passwordField.getText());
					if (isSaveCredentialsChecked){
						saveUserPasswordInDB(userTextField.getText(),passwordField.getText());
					}
				} else {
					toastMessage(MusesUtils.getResourceBundle().getString("make_sure_privacy_policy_read_txt"));
				}
			} else if (event.getSource() == logoutBtn) {
				UserContextEventHandler.getInstance().logout();
			} 
		}

	};
	
	public void setLoginView() {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Text scenetitle = new Text(MusesUtils.getResourceBundle().getString("login_button_txt"));
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));
		scenetitle.setStroke(Color.PURPLE);
		scenetitle.setFill(Color.MAGENTA);
		grid.add(scenetitle, 0, 0, 2, 1);

		Label description = new Label(MusesUtils.getResourceBundle().getString("login_detail_view_txt"));
		description.setWrapText(true);
		grid.add(description, 0, 1);

		Text userName = new Text(MusesUtils.getResourceBundle().getString("username_txt"));
		userName.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
		grid.add(userName, 0, 2);

		userTextField = new TextField();
		userTextField.setPromptText(MusesUtils.getResourceBundle().getString("hint_txt_userid"));
		userTextField.setMinHeight(20);

		grid.add(userTextField, 0, 3);

		Text password = new Text(MusesUtils.getResourceBundle().getString("password_txt"));
		password.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
		grid.add(password, 0, 4);

		passwordField = new PasswordField();
		passwordField.setPromptText(MusesUtils.getResourceBundle().getString("hint_txt_password"));
		passwordField.setMinHeight(20);
		grid.add(passwordField, 0, 5);


		grid.add(rememberCredentialsBox, 0, 6);

		Text privacyPolicy = new Text(MusesUtils.getResourceBundle().getString("privacy_policy_view_txt"));
		privacyPolicy.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
		grid.add(privacyPolicy, 0, 7);

		Label policy = new Label(MusesUtils.getResourceBundle().getString("privacy_policy_detail_txt"));
		ScrollPane policyScrollPane = new ScrollPane();
		policyScrollPane.setContent(policy);
		policyScrollPane.setPrefSize(240, 300);
		grid.add(policyScrollPane, 0, 8);

		grid.add(agreeTermBox, 0, 9);

		HBox hbBtn = new HBox(15);
		hbBtn.setAlignment(Pos.CENTER);
		hbBtn.getChildren().add(loginBtn);
		grid.add(hbBtn, 0, 10);

		setUsernamePasswordIfSaved();
		
		Scene loginScene = new Scene(grid, 600, 650);
		primaryStage.setScene(loginScene);
		primaryStage.show();
	}
	
	public void setLogoutView() {
		
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
		
        Text scenetitle = new Text(MusesUtils.getResourceBundle().getString("login_button_txt"));
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));
        scenetitle.setStroke(Color.PURPLE);
        scenetitle.setFill(Color.MAGENTA);
        grid.add(scenetitle, 0, 0, 2, 1);
		
        Text userName = new Text(MusesUtils.getResourceBundle().getString("logged_in_info_txt") + userTextField.getText());
        userName.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        grid.add(userName, 0, 2);
        
        String serverStatusText = "";
        if (Statuses.CURRENT_STATUS == Statuses.ONLINE){
        	serverStatusText = MusesUtils.getResourceBundle().getString("current_com_status_pre") + MusesUtils.getResourceBundle().getString("current_com_status_2");
        }else {
        	serverStatusText = MusesUtils.getResourceBundle().getString("current_com_status_pre") + MusesUtils.getResourceBundle().getString("current_com_status_3");
        }
        Text password = new Text(serverStatusText);
        password.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        grid.add(password, 0, 4);
        
        HBox hbBtn = new HBox(15);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.getChildren().add(logoutBtn);
        grid.add(hbBtn, 0, 8);
        
        Scene logoutScene = new Scene(grid, 600, 550);
        primaryStage.setScene(logoutScene);
	}
	

	@Override
	public void update(Observable o, Object msg) {
		Properties properties = (Properties)msg;
		int actionResponse = Integer.parseInt( properties.getProperty("action_response") );
		if (this.o == o) {
			switch (actionResponse) {
			case MusesUICallbacksHandler.LOGIN_SUCCESSFUL:
				System.out.println(properties.getProperty(JSONIdentifiers.AUTH_MESSAGE));
				//stopProgress();
				isLoggedIn = true;
				Platform.runLater(new Runnable() {
			        @Override
			        public void run() {
			          //javaFX operations should go here
			        	setLogoutView();
			        }
				});
				toastMessage(properties.getProperty(JSONIdentifiers.AUTH_MESSAGE));
				
				break;
			case MusesUICallbacksHandler.LOGIN_UNSUCCESSFUL:
				System.out.println(properties.getProperty(JSONIdentifiers.AUTH_MESSAGE));
				//stopProgress();
				isLoggedIn = false;
				//updateLoginInPrefs(false);
				
				Platform.runLater(new Runnable() {
			        @Override
			        public void run() {
			          //javaFX operations should go here
			        	setLoginView();
			        }
				});
				toastMessage(properties.getProperty(JSONIdentifiers.AUTH_MESSAGE));
				break;
			default:
				System.out.println(APP_TAG+" Unknown Error!, updating prefs..");
				//stopProgress();
				isLoggedIn = false;
				//updateLoginInPrefs(false);
				Platform.runLater(new Runnable() {
			        @Override
			        public void run() {
			          //javaFX operations should go here
			        	setLoginView();
			        }
				});
				toastMessage(LabelsAndText.UNKNOW_ERROR_TOAST);
				break;
			}
		}	
		
	}
	
	/**
	 * Starts the progress bar when user try to login
	 */
	private void startProgress(){
//		progressDialog = new ProgressDialog(MainActivity.this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
//		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		progressDialog.setTitle(getResources().getString(
//				R.string.logging_in));
//		progressDialog.setMessage(getResources().getString(
//				R.string.wait));
//		progressDialog.setCancelable(true);
//		progressDialog.show();
	}
	
	/**
	 * Stops the progress bar when a reply is received from server
	 */
	
	private void stopProgress(){
//		if (progressDialog != null){
//			progressDialog.dismiss();
//		}
	}
	
	private void regiterCallbacks() {
		MusesUICallbacksHandler uiCallbacksHandler = new MusesUICallbacksHandler();
    	ActuatorController.getInstance().registerCallback(uiCallbacksHandler);
		uiCallbacksHandler.addObserver(this);
		setObservable(uiCallbacksHandler);
	}
	
	private void toastMessage(String makeSurePrivacyPolicyToast) {
    	final JFrame frame = new JFrame();
    	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	frame.setSize(new Dimension(500, 300));
    	Toast.makeText(frame, makeSurePrivacyPolicyToast, Style.SUCCESS).display();
	}

	/**
	 * Check the login fields and Then tries login to the server
	 */

	public void doLogin(String userName, String password) {
		if (checkLoginInputFields(userName, password)) {
			//startProgress();
			userContextMonitoringController.login(userName, password);
		} else {
			toastMessage(MusesUtils.getResourceBundle().getString("empty_login_fields_msg"));
			
		}
	}
	
	/**
	 * Check input fields are not empty before sending it for authentication
	 * 
	 * @param userName
	 * @param password
	 * @return
	 */

	private boolean checkLoginInputFields(String userName, String password) {
		if (userName != null || password != null) { 
			if (userName.equals("") || password.equals("") )
				return false;								// FIXME need some new checking in future
		} else return false;
		return true;
	}

	
	public void setUsernamePasswordIfSaved(){
		DBManager dbManager = new DBManager();
		dbManager.openDB();
		if (dbManager.isUserAuthenticated()){
			UserCredentials userCredentials = dbManager.getUserCredentials();
			if (userCredentials != null){
				userTextField.setText(userCredentials.getUsername());
				passwordField.setText(userCredentials.getPassword());
			}
		}
		
//		// Set rememberCheckBox, if no choice done default to true
//		isSaveCredentialsChecked = prefs.getBoolean(SAVE_CREDENTIALS, false);
//		rememberCheckBox.setChecked(isSaveCredentialsChecked);
		
	}

	private void saveUserPasswordInDB(String userName, String password){
		DBManager dbManager = new DBManager();
		dbManager.openDB();
		dbManager.insertCredentials(SettingsSensor.getMacAddress(), userName, password);
	}
}