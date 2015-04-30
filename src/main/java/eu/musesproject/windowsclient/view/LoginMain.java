package eu.musesproject.windowsclient.view;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.ImageIcon;

public class LoginMain extends Application {

    public static void main(String[] args) {
    	RMI.startRMI();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(LabelsAndText.MUSES_TITLE);
        String musesIconURL = LoginMain.class.getClassLoader().getResource("muses_icon.png").toString();
        //Image musesIcon = new Image(musesIconURL);
        //primaryStage.getIcons().add(musesIcon);
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

        TextField userTextField = new TextField();
        userTextField.setPromptText("Enter your username here..");
        userTextField.setMinHeight(20);;
        grid.add(userTextField, 0, 3);

        Text password = new Text(LabelsAndText.PASSWORD);
        password.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        grid.add(password, 0, 4);

        PasswordField pwBox = new PasswordField();
        pwBox.setPromptText("Enter your password here..");
        pwBox.setMinHeight(20);
        grid.add(pwBox, 0, 5);

        Text privacyPolicy = new Text(LabelsAndText.PRIVACY_POLICY_LABEL);
        privacyPolicy.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        grid.add(privacyPolicy, 0, 6);

        Label policy = new Label(LabelsAndText.PRIVACY_POLICY);
        policy.setWrapText(true);
        grid.add(policy, 0, 7);

        Button loginBtn = new Button(LabelsAndText.LOGIN_LABEL);
        loginBtn.setStyle("-fx-background-color: grey; -fx-text-fill: white;");
        HBox hbBtn = new HBox(15);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.getChildren().add(loginBtn);
        grid.add(hbBtn, 0, 8);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
         
        loginBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                //actiontarget.setFill(Color.FIREBRICK);
                //actiontarget.setText("Sign in button pressed");
                //showDialog();
                //placeInSystemTray();
                
            }

        });

        Scene scene = new Scene(grid, 600, 550);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
	private void placeInSystemTray() {
		//Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        //Image musesImage = Toolkit.getDefaultToolkit().getImage(LoginMain.class.getClassLoader().getResource("muses_icon.png").toString());
        Image musesImage = new ImageIcon(LoginMain.class.getClassLoader().getResource("muses_icon.png")).getImage();
        final TrayIcon trayIcon = new TrayIcon(musesImage,"muses_icon",popup);
        final SystemTray tray = SystemTray.getSystemTray();
       
        // Create a pop-up menu components
        MenuItem aboutItem = new MenuItem("About");
        CheckboxMenuItem cb1 = new CheckboxMenuItem("Set auto size");
        CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
        Menu displayMenu = new Menu("Display");
        MenuItem errorItem = new MenuItem("Error");
        MenuItem warningItem = new MenuItem("Warning");
        MenuItem infoItem = new MenuItem("Info");
        MenuItem noneItem = new MenuItem("None");
        MenuItem exitItem = new MenuItem("Exit");
       
        //Add components to pop-up menu
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(cb1);
        popup.add(cb2);
        popup.addSeparator();
        popup.add(displayMenu);
        displayMenu.add(errorItem);
        displayMenu.add(warningItem);
        displayMenu.add(infoItem);
        displayMenu.add(noneItem);
        popup.add(exitItem);
       
        trayIcon.setPopupMenu(popup);
       
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }				
	}

    
    private void showDialog() {
    	Stage dialogStage = new Stage();
    	Button okBtn = new Button("Ok");
    	dialogStage.initModality(Modality.WINDOW_MODAL);
    	dialogStage.setScene(new Scene(VBoxBuilder.create().
    	    children(new Text("Warnig"), okBtn).
    	    alignment(Pos.CENTER).padding(new Insets(5)).build()));
    	dialogStage.show();
    	okBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				
			}
		});
    	
    }
}