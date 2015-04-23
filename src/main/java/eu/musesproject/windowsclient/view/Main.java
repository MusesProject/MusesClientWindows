package eu.musesproject.windowsclient.view;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JFrame;

import eu.musesproject.windowsclient.contextmonitoring.IUserContextMonitoringController;
import eu.musesproject.windowsclient.contextmonitoring.UserContextMonitoringController;

public class Main extends JFrame{

	private static final long serialVersionUID = 1L;
    private javax.swing.JTextField userNameTextField;
    private javax.swing.JLabel loginJLabel;
    private javax.swing.JLabel usernameJLabel;
    private javax.swing.JLabel passwordJLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField passwordTextField;
    private javax.swing.JButton loginButton;

    public Main() {
		initComponents();
	}
	
	public static void main (String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Main().setVisible(true);
            }
        });
	}
	
    private void initComponents() {
    	startRMI();
        loginJLabel = new javax.swing.JLabel();
        usernameJLabel = new javax.swing.JLabel();
        userNameTextField = new javax.swing.JTextField();
        passwordJLabel = new javax.swing.JLabel();
        passwordTextField = new javax.swing.JTextField();
        loginButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        loginJLabel.setText("Login");

        usernameJLabel.setText("Username");	

        passwordJLabel.setText("Password");

        loginButton.setText("Login");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(usernameJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(userNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(loginJLabel)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(passwordJLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(passwordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(loginButton)))))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(loginJLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usernameJLabel)
                    .addComponent(loginButton)
                    .addComponent(passwordJLabel)
                    .addComponent(passwordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );
   
        pack();
    }
	
    private void startRMI() {
    	try {
    		
            LocateRegistry.createRegistry(1099); 
            System.out.println("java RMI registry created.");
    		
			RMI rmi = new RMI();
			Naming.rebind("//localhost/RMI",rmi);
			System.out.println("Rmi started..");
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}    	
	}

	private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	if (!userNameTextField.getText().trim().equals("") && !passwordTextField.getText().trim().equals("")) {
        	IUserContextMonitoringController iMonitoringController = new UserContextMonitoringController();
        	iMonitoringController.login(userNameTextField.getText(), passwordTextField.getText());
        }
    	RMI rmi;
		try {
			rmi = new RMI();
			rmi.sendResponseToMusesAwareApp("accepted");;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}
