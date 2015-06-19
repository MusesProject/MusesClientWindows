package eu.musesproject.windowsclient.view;

import eu.musesproject.windowsclient.actuators.ActuatorController;

import javax.swing.*;
import java.awt.event.*;

public class SimpleFeedbackDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonDetails;
    private JButton buttonCancel;
    private JTextArea dialogBodyView;
    private JLabel dialogTitleView;
    private String[] splitBody;
    private String decisionId;

    public SimpleFeedbackDialog(String title, String body, String decisionId) {
        System.out.println("SimpleFeedbackDialog(String title, String body, String decisionId)");
        this.decisionId = decisionId;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonDetails);
        this.setLocationRelativeTo(null);
        this.setLocation((int) (getLocation().getX() - (100)), (int) (getLocation().getY() - (100)));

        buttonDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onDetails();
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        dialogBodyView.setEditable(false);
        dialogBodyView.setBackground(dialogTitleView.getBackground());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        fillWithContent(title, body);
    }

    private void fillWithContent(String title, String body){
        dialogTitleView.setText(title);

        // split the text for the main and the details view
        try {
            splitBody = body.split("\\n");
        } catch (NullPointerException e) {
            splitBody = new String[2];
            splitBody[0] = body;
            splitBody[1] = body;
        }

        dialogBodyView.setText(splitBody[0]);
    }

    private void onDetails() {
        dialogBodyView.setText(splitBody[1]);
        dialogBodyView.setLineWrap(true);
        buttonDetails.hide();

        this.setSize(getWidth(), getHeight() * 2);
    }

    private void onCancel() {
        ActuatorController.getInstance().removeFeedbackFromQueue();
//        UserContextMonitoringController.getInstance().sendUserBehavior(action, decisionId);
        dispose();
    }
}
