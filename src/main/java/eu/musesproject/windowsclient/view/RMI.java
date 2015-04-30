package eu.musesproject.windowsclient.view;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import eu.musesproject.awareappinterface.AwareAction;
import eu.musesproject.awareappinterface.IMusesService;
import eu.musesproject.awareappinterface.IMusesServiceCallback;
import eu.musesproject.awareappinterface.Properties;

public class RMI extends UnicastRemoteObject implements Serializable, IMusesService{

	private static final long serialVersionUID = 1L;
	private static IMusesServiceCallback callback;
	public RMI() throws RemoteException {
		super(0);
	}

	@Override
	public void sendUserAction(AwareAction action, Properties properties)
			throws RemoteException {
 		System.out.println("reuqest came with action: "+action.toString() + " and properties: "+properties.toString());
		if(action.getType().equals("action")) {
			
		}
		else {
		}
	}

	@Override
	public void registerCallback(IMusesServiceCallback cback)
			throws RemoteException {
		callback = cback;
		System.out.println("callback registered..");
	}

	@Override
	public void unregisterCallback(IMusesServiceCallback cback)
			throws RemoteException {
		callback = cback;
		System.out.println("callback unregistered..");
	}
	
	public void sendResponseToMusesAwareApp(String response){
		try {
			callback.onAccept(response);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

    public static void startRMI() {
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

	
}
