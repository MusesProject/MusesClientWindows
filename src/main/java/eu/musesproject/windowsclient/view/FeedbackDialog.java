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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import eu.musesproject.windowsclient.actuators.ActuatorController;



/**
 * Created by ali.alizadeh on 10/30/2015.
 */
public class FeedbackDialog extends JDialog{
    private static final long serialVersionUID = 1L;
    private String[] splitBody;
    private String decisionId;

    private JTextPane textPane;
    private JButton buttonDetail;
    private JButton buttonCancel;
    private JPanel buttonPane;
    private JLabel titleLabel ;

    public FeedbackDialog(JFrame parent, String title, String message, String decisionId) {
        super(parent, "");
        this.decisionId = decisionId;

        // set the position of the window
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);

        titleLabel = new JLabel(title);
        titleLabel .setHorizontalAlignment(SwingConstants.CENTER);
        getContentPane().add(titleLabel, BorderLayout.BEFORE_FIRST_LINE);

        // split and fill message
        fillTextPaneMessage(message);
        JScrollPane scrollPane = new JScrollPane(textPane);
        getContentPane().add(scrollPane);

        // Create buttons
        buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonCancel = new JButton("Cancel");
        buttonDetail = new JButton("Details");
        buttonPane.add(buttonDetail);
        buttonPane.add(buttonCancel);

        // set action listener on the buttons
        buttonCancel.addActionListener(new CancelActionListener());
        buttonDetail.addActionListener(new DetailActionListener());

        getContentPane().add(buttonPane, BorderLayout.PAGE_END);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        pack();
        setSize(300, 200);
    }

    private void fillTextPaneMessage(String message) {
        textPane= new JTextPane();
        try {
            splitBody = message.split("\\n");
        } catch (NullPointerException e) {
            splitBody = new String[2];
            splitBody[0] = message;
            splitBody[1] = message;
        }
        textPane.setText(splitBody[0]);
        textPane.setEditable(false);
    }

    // override the createRootPane inherited by the JDialog, to create the rootPane.
    // create functionality to close the window when "Escape" button is pressed
    public JRootPane createRootPane() {
        JRootPane rootPane = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        Action action = new AbstractAction() {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                System.out.println("escaping..");
                setVisible(false);
                dispose();
            }
        };
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", action);
        return rootPane;
    }

    // an action listener to be used when an action is performed
    class CancelActionListener implements ActionListener {

        //close and dispose of the window.
        public void actionPerformed(ActionEvent e) {
            System.out.println("disposing the window..");
            ActuatorController.getInstance().removeFeedbackFromQueue();
            setVisible(false);
            dispose();
        }
    }

    class DetailActionListener implements ActionListener {

        //show details and dispose of the window.
        public void actionPerformed(ActionEvent e) {
            textPane.setText(splitBody[1]);
            buttonDetail.hide();
        }
    }

//    public static void main(String[] args) {
//        String msg = "This is the general message \n this is the detail message";
//        new FeedbackDialog(new JFrame(), "title", msg, "1").setVisible(true);
//    }
}
