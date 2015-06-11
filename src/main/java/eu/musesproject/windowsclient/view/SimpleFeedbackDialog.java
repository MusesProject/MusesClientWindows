package eu.musesproject.windowsclient.view;

import eu.musesproject.windowsclient.actuators.ActuatorController;

import javax.swing.*;
import java.awt.event.*;

public class SimpleFeedbackDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonDetails;
    private JButton buttonCancel;
    private JLabel dialogBodyView;
    private JLabel dialogTitleView;
    private String[] splitBody;

    public SimpleFeedbackDialog(String title, String body) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonDetails);

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
        buttonDetails.hide();
    }

    private void onCancel() {
        ActuatorController.getInstance().removeFeedbackFromQueue();
//        UserContextMonitoringController.getInstance().sendUserBehavior(action, decisionId);
        dispose();
    }

    public static void main(String[] args) {
        SimpleFeedbackDialog dialog = new SimpleFeedbackDialog("STRONG_DENY","test text \n test details");
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
