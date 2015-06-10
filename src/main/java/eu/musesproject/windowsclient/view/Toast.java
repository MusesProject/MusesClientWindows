package eu.musesproject.windowsclient.view;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;


public class Toast extends JDialog {
	private static final long serialVersionUID = -1602907470843951525L;
	
	public enum Style { NORMAL, SUCCESS, ERROR };
	
	public static final int LENGTH_SHORT = 3000;
	public static final int LENGTH_LONG = 6000;
	public static final Color BLACK = new Color(0, 0, 0);
	
	private final float MAX_OPACITY = 0.8f;
	private final float OPACITY_INCREMENT = 0.05f;
	private final int REF_RATE = 20;
	private final int TOAST_RADIUS = 7;
	private final int CHARACTER_LENGTH_MULTIPLIER = 7;
	private final int DISTANCE_FROM_TOP = 800;	
	private final int DISTANCE_FROM_LEFT = 800;
	
	private JFrame mainJFrame;
	private String toastTxt;
	private int toastDuration;
	private Color backgroundColor = Color.BLACK;
	private Color mainColor = Color.WHITE;
    
    public Toast(JFrame main){
    	super(main);
    	this.mainJFrame = main;
    }

    private void buildToast(){
        setLayout(new GridBagLayout());
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), TOAST_RADIUS, TOAST_RADIUS));
            }
        });
        
        setAlwaysOnTop(true);
        setUndecorated(true);
        setFocusableWindowState(false);
        setModalityType(ModalityType.MODELESS);
        setSize(toastTxt.length() * CHARACTER_LENGTH_MULTIPLIER, 25);
        getContentPane().setBackground(backgroundColor);
        
        JLabel label = new JLabel(toastTxt);
        label.setForeground(mainColor);
        add(label);
    }
	
	public void startToast() {
		final Timer timer = new Timer(REF_RATE, null);
		timer.setRepeats(true);
		timer.addActionListener(new ActionListener() {
			private float opacity = 0;
			@Override public void actionPerformed(ActionEvent e) {
				opacity += OPACITY_INCREMENT;
				setOpacity(Math.min(opacity, MAX_OPACITY));
				if (opacity >= MAX_OPACITY){
					timer.stop();
				}
			}
		});

		setOpacity(0);
		timer.start();
				
		setLocation(getToastLocation());		
		setVisible(true);
	}

	public void removeToast() {
		final Timer timer = new Timer(REF_RATE, null);
		timer.setRepeats(true);
		timer.addActionListener(new ActionListener() {
			private float opacity = MAX_OPACITY;
			@Override public void actionPerformed(ActionEvent e) {
				opacity -= OPACITY_INCREMENT;
				setOpacity(Math.max(opacity, 0));
				if (opacity <= 0) {
					timer.stop();
					setVisible(false);
					dispose();
				}
			}
		});

		setOpacity(MAX_OPACITY);
		timer.start();
	}
	
	private Point getToastLocation(){
		Point mainPointLoc = mainJFrame.getLocation();		
		int x = (int) (mainPointLoc.getX() + DISTANCE_FROM_LEFT); //((mainJFrame.getWidth() - this.getWidth()) / 2)); 
		int y = (int) (mainPointLoc.getY() + DISTANCE_FROM_TOP);
		return new Point(x, y);
	}
	
	public void setText(String text){
		toastTxt = text;
	}
	
	public void setDuration(int duration){
		toastDuration = duration;
	}
	
	@Override
	public void setBackground(Color backgroundColor){
		this.backgroundColor = backgroundColor;
	}
	
	@Override
	public void setForeground(Color foregroundColor){
		mainColor = foregroundColor;
	}
	
	public static Toast makeText(JFrame owner, String text){
		return makeText(owner, text, LENGTH_SHORT);
	}
	
	public static Toast makeText(JFrame owner, String text, Style style){
		return makeText(owner, text, LENGTH_SHORT, style);
	}
    
    public static Toast makeText(JFrame owner, String text, int duration){
    	return makeText(owner, text, duration, Style.NORMAL);
    }
    
    public static Toast makeText(JFrame mainJFrame, String text, int duration, Style style){
    	Toast toast = new Toast(mainJFrame);
    	toast.toastTxt = text;
    	toast.toastDuration = duration;
    	if (style == Style.NORMAL)
    		toast.backgroundColor = BLACK;
    	
    	return toast;
    }
        
    public void display(){
        new Thread(new Runnable() {
            @Override
            public void run() {
            	try{
            		buildToast();
            		startToast();
	                Thread.sleep(toastDuration);
	                removeToast();
            	}
            	catch(Exception ex){
            		ex.printStackTrace();
            	}
            }
        }).start();
    }

}
