/*
 * MUSES High-Level Object Oriented Model
 * Copyright MUSES project (European Commission FP7) - 2013 
 */
package eu.musesproject.windowsclient.securitypolicyreceiver;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import eu.musesproject.client.model.JSONIdentifiers;
import eu.musesproject.windowsclient.model.*;
import eu.musesproject.client.model.decisiontable.PolicyDT;

/**
 * The Class RemotePolicyReceiver.
 * 
 * @author Sergio Zamarripa (S2)
 * @version 2 avr. 2013
 */
public class RemotePolicyReceiver {
	
	private static RemotePolicyReceiver remotePolicyReceiver = null;	
	private static final String TAG = RemotePolicyReceiver.class.getSimpleName();
	public static final int SUCCESSFUL_RECEPTION = 0;
	public static final int FAILED_RECEPTION = -1;
	private static Logger logger = Logger.getLogger(RemotePolicyReceiver.class);
	
	public static RemotePolicyReceiver getInstance() {
        if (remotePolicyReceiver == null) {
        	remotePolicyReceiver = new RemotePolicyReceiver();
        }
        return remotePolicyReceiver;
    }
	
	/**
	 * Info D
	 * 
	 *  This method allows the reception of policy DT (coming from the server side)
	 * 
	 * @param policyDT
	 * 
	 * 
	 * @return int as status of the reception
	 */
	
	public int receivePolicyDT( PolicyDT policy){
		
		return 0;

	}
	
	public int updateJSONPolicy(String jsonPolicy){
		logger.debug( "[receiveJSONPolicy]");
		String policy = null;
		Decisiontable decisionTableElement = null;
		
		
		PolicyDT policyDT = new PolicyDT();
		policyDT.setRawPolicy(jsonPolicy);
				
        if((jsonPolicy != null) && (!jsonPolicy.equals(""))) {
    		try{		
    			JSONObject rootJSON = new JSONObject(jsonPolicy);		
    			policy = rootJSON.getString(JSONIdentifiers.DEVICE_POLICY);
    			
    			JSONObject policyJSON = new JSONObject(policy);
    			
    			//Create decision table entry containing action, resource, subject and decision
    			String files = policyJSON.getString(JSONIdentifiers.POLICY_SECTION_FILES);    			
    			JSONObject filesJSON = new JSONObject(files);
    			
    			decisionTableElement = DevicePolicyHelper.getInstance().getDecisionTable(filesJSON);
    			
    		} catch (JSONException je) {
    	           je.printStackTrace();
    	           logger.debug( "[receiveJSONPolicy]: Exception while parsing JSON Policy:" + je.getMessage());
    	           return FAILED_RECEPTION;
    	    }
        }

        
        if (decisionTableElement != null){
            
            if (decisionTableElement.getId()>0){
            	logger.debug( "[receiveJSONPolicy]: Decision table element has been correctly added:"+decisionTableElement.getId());
            	logger.debug( "[receiveJSONPolicy]: Action_id:"+decisionTableElement.getActionId()+"-Resource:"+decisionTableElement.getResourceId()+"-Decision:"+decisionTableElement.getDecisionId()+"-RiskComm:"+decisionTableElement.getRiskcommunicationId());
            	
        		return SUCCESSFUL_RECEPTION;
            }else{
            	logger.debug( "[receiveJSONPolicy]: Decision table element id is negative:" + decisionTableElement.getId());
            	return FAILED_RECEPTION;
            }
        }else{
        	logger.debug( "[receiveJSONPolicy]: Decision table element not found");
        	return FAILED_RECEPTION;
        }
		
	}
	
		


}
