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
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javafx.application.Application;
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

import javax.swing.JFrame;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import eu.musesproject.windowsclient.actuators.ActuatorController;
import eu.musesproject.windowsclient.connectionmanager.AlarmReceiver;
import eu.musesproject.windowsclient.contextmonitoring.UserContextMonitoringController;
import eu.musesproject.windowsclient.view.Toast.Style;

public class LoginMain extends Application implements Observer{
	private static final String APP_TAG = "APP_TAG";
	private Stage primaryStage;
	static Button loginBtn,logoutBtn;
	static TextField userTextField;
	static PasswordField passwordField;
	static CheckBox rememberCredentialsBox, agreeTermBox;

	boolean isPrivacyPolicyAgreementChecked = false;
	boolean isSaveCredentialsChecked = false;
	
	private Observable o;
	
	private UserContextMonitoringController userContextMonitoringController;


	public static void main(String[] args) {
		RMI.startRMI();
		launch(args);
	}

	private void regiterCallbacks() {
		MusesUICallbacksHandler uiCallbacksHandler = new MusesUICallbacksHandler();
    	ActuatorController.getInstance().registerCallback(uiCallbacksHandler);
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

		rememberCredentialsBox = new CheckBox(LabelsAndText.REMEMBER_MY_LOGIN);
		rememberCredentialsBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				isSaveCredentialsChecked = newValue;
			}
		});
		
		agreeTermBox = new CheckBox(LabelsAndText.AGREE_TO_POLICY);
		agreeTermBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				isPrivacyPolicyAgreementChecked = newValue;
			}
		});
		
		setLoginView();
		regiterCallbacks();
		userContextMonitoringController = UserContextMonitoringController.getInstance();
//		userContextMonitoringController.connectToServer(); // FIXME Until spring is not imeplemented
		initSchedulerForPolling();
	}

	private void initSchedulerForPolling() {
		try {
			JobDetail pollJob = JobBuilder.newJob(AlarmReceiver.class)
					.withIdentity("poll_job").build();
			Trigger pollTrigger = TriggerBuilder
					.newTrigger()
					.withSchedule(
							SimpleScheduleBuilder.simpleSchedule()
									.withIntervalInSeconds(AlarmReceiver.POLL_INTERVAL).repeatForever())
					.build();
			SchedulerFactory schedulerFactory = new StdSchedulerFactory();
			Scheduler scheduler = schedulerFactory.getScheduler();
			scheduler.start();
			scheduler.scheduleJob(pollJob, pollTrigger);

		} catch (SchedulerException e) {
			e.printStackTrace();
		}
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
		userTextField.setPromptText(LabelsAndText.PROMT_TXT_USERNAME);
		userTextField.setMinHeight(20);
		;
		grid.add(userTextField, 0, 3);

		Text password = new Text(LabelsAndText.PASSWORD);
		password.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
		grid.add(password, 0, 4);

		passwordField = new PasswordField();
		passwordField.setPromptText(LabelsAndText.PROMT_TXT_PASSWORD);
		passwordField.setMinHeight(20);
		grid.add(passwordField, 0, 5);


		grid.add(rememberCredentialsBox, 0, 6);

		Text privacyPolicy = new Text(LabelsAndText.PRIVACY_POLICY_LABEL);
		privacyPolicy.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
		grid.add(privacyPolicy, 0, 7);

		Label policy = new Label(LabelsAndText.PRIVACY_POLICY);
		policy.setWrapText(true);
		grid.add(policy, 0, 8);

		grid.add(agreeTermBox, 0, 9);

		HBox hbBtn = new HBox(15);
		hbBtn.setAlignment(Pos.CENTER);
		hbBtn.getChildren().add(loginBtn);
		grid.add(hbBtn, 0, 10);

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


	final EventHandler<ActionEvent> actionEventListener = new EventHandler<ActionEvent>() {

		@Override
		public void handle(final ActionEvent event) {
			if (event.getSource() == loginBtn) {
				if (isPrivacyPolicyAgreementChecked){
					doLogin(userTextField.getText(),passwordField.getText());
					//saveUserPasswordInPrefs();
				} else {
					System.out.println(LabelsAndText.MAKE_SURE_PRIVACY_POLICY_TOAST);
					toastMessage(LabelsAndText.MAKE_SURE_PRIVACY_POLICY_TOAST);
					//toastMessage(getResources().getString(R.string.make_sure_privacy_policy_read_txt));
				}
				
				// RMI rmi;
				// try {
				// rmi = new RMI();
				// rmi.sendResponseToMusesAwareApp("accepted");;
				// } catch (RemoteException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				showDialog();
				setLogoutView();
			} else if (event.getSource() == logoutBtn) {
				setLoginView();
			} 
		}

	};
	

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
			System.out.println(LabelsAndText.EMPTY_LOGIN_FIELD_TOAST);
			//toastMessage(getResources().getString(R.string.empty_login_fields_msg));
			
		}
	}

	private void showDialog() {
		Stage dialogStage = new Stage();
		Button okBtn = new Button(LabelsAndText.OK);
		Text warningTxt = new Text(LabelsAndText.WARNING);
		Text feedbackTxt = new Text("This is some test feedback");
		feedbackTxt.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));

		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.setScene(new Scene(VBoxBuilder.create()
				.children(warningTxt, feedbackTxt, okBtn).alignment(Pos.CENTER)
				.padding(new Insets(5)).build()));
		dialogStage.show();
		okBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				
			}
		});
	}

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
				System.out.println("Login was unsuccessful..");
				//setLoginView(); FIXME
				break;
			default:
				System.out.println(APP_TAG+" Unknown Error!, updating prefs..");
				break;
				
			}
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
//	
//	public void setUsernamePasswordIfSaved(){
//		if (prefs.contains(USERNAME)) {
//			userName = prefs.getString(USERNAME, "");
//			password = prefs.getString(PASSWORD, "");
//			userNameTxt.setText(userName);
//			passwordTxt.setText(password);
//			
//		} else {
//			userNameTxt.setText("");
//			passwordTxt.setText("");
//			Log.d(MusesUtils.LOGIN_TAG, "No username-pass found in preferences");
//		}
//		
//		// Set rememberCheckBox, if no choice done default to true
//		isSaveCredentialsChecked = prefs.getBoolean(SAVE_CREDENTIALS, false);
//		rememberCheckBox.setChecked(isSaveCredentialsChecked);
//		
//	}
//
//	private void saveUserPasswordInPrefs(){
//		userName = userNameTxt.getText().toString();
//		password = passwordTxt.getText().toString();
//		SharedPreferences.Editor prefEditor = prefs.edit();	
//		if (isSaveCredentialsChecked){
//			prefEditor.putString(USERNAME, userName);
//			prefEditor.putString(PASSWORD, password);
//			prefEditor.putBoolean(SAVE_CREDENTIALS, isSaveCredentialsChecked);
//			prefEditor.commit();
//			
//		}
//	}
}