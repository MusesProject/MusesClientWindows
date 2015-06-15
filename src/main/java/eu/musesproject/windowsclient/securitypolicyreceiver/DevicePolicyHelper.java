package eu.musesproject.windowsclient.securitypolicyreceiver;


import java.sql.Timestamp;

import eu.musesproject.client.model.JSONIdentifiers;
import eu.musesproject.windowsclient.usercontexteventhandler.UserContextEventHandler;
import eu.musesproject.windowsclient.model.*;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;


public class DevicePolicyHelper {
	
	private static DevicePolicyHelper devicePolicyHelper = null;
	private static final String TAG = DevicePolicyHelper.class.getSimpleName();
	private static int decisionId;
	private static Logger logger = Logger.getLogger(DevicePolicyHelper.class);
	
	public static DevicePolicyHelper getInstance() {
        if (devicePolicyHelper == null) {
        	devicePolicyHelper = new DevicePolicyHelper();
        }
        return devicePolicyHelper;
    }
	
	
	
	public Decisiontable getDecisionTable(JSONObject filesJSON) {// Create decision table entry containing
		Decisiontable decisionTable = new Decisiontable();
		Action action = new Action();
		Resource resource = new Resource();
		Subject subject = new Subject();
		Riskcommunication riskCommunication = new Riskcommunication();
		DBManager dbManager = new DBManager();
        dbManager.openDB();
		try{
			//Action part
			String actionString = filesJSON.getString(JSONIdentifiers.POLICY_SECTION_ACTION);
			JSONObject actionJSON = new JSONObject(actionString);
			action = updateAction(actionJSON);
			//Resource part
			String resources = filesJSON.getString(JSONIdentifiers.POLICY_SECTION_ACTION);
			JSONObject resourcesJSON = new JSONObject(resources);
			resource = updateResourceAction(resourcesJSON);
			//Subject part
			//RiskCommunication part
			String communicationString = filesJSON.getString(JSONIdentifiers.POLICY_SECTION_ACTION);// TODO Use POLICY_SECTION_RISKCOMMUNICATION
			JSONObject commJSON = new JSONObject(communicationString);
			riskCommunication = updateRiskCommunication(commJSON);

		} catch (JSONException je) {
	           je.printStackTrace();
	    }
		if (action!=null){
			decisionTable.setActionId(action.getId());
		}
		if (resource!=null){
			decisionTable.setResourceId(resource.getId());
		}
		if (subject!=null){
			decisionTable.setSubjectId(subject.getId());
		}
		if (riskCommunication!=null){
			if (riskCommunication.getId()>0){
				decisionTable.setRiskcommunicationId(riskCommunication.getId());
				logger.debug( "Setting riskCommunication_id into decisiontable:"+decisionTable.getRiskcommunicationId());
			}else{
				logger.error( "RiskCommunication id:"+decisionTable.getRiskcommunicationId());
			}
		}else{
			logger.error( "RiskCommunication is null!");
		}
		
		decisionTable.setDecisionId(decisionId);

		//At the end, with all the inserted ids, update the decision table
		long indexDT = dbManager.addDecisionTable(decisionTable);
		decisionTable.setId((int)indexDT);
		logger.debug( "DecisionTable correctly created with index:"+indexDT);
		dbManager.closeDB();
		
		return decisionTable;
	}
	
	private Riskcommunication updateRiskCommunication(JSONObject commJSON) {
		
		Riskcommunication riskCommunication = new Riskcommunication();
		Risktreatment riskTreatment = new Risktreatment();
		DBManager dbManager = new DBManager();
        dbManager.openDB();
		try {
			if (commJSON.toString().contains("\""+JSONIdentifiers.POLICY_PROPERTY_ALLOW+"\"")){
				String allowAction = commJSON.getString(JSONIdentifiers.POLICY_PROPERTY_ALLOW);
				JSONObject allowActionJSON = new JSONObject(allowAction);
				if (allowAction.contains(JSONIdentifiers.POLICY_SECTION_RISKTREATMENT)){
					String riskTreatmentAction = allowActionJSON.getString(JSONIdentifiers.POLICY_SECTION_RISKTREATMENT);
					riskTreatment.setTextualdescription(riskTreatmentAction);
					logger.debug( "RiskTreatment:" + riskTreatment.getTextualdescription());
				}else{
					riskTreatment.setTextualdescription("The action is allowed");
				}
			}else if (commJSON.toString().contains("\""+JSONIdentifiers.POLICY_PROPERTY_DENY+"\"")) {
				String denyAction = commJSON.getString(JSONIdentifiers.POLICY_PROPERTY_DENY);
				JSONObject denyActionJSON = new JSONObject(denyAction);
				if (denyAction.contains(JSONIdentifiers.POLICY_SECTION_RISKTREATMENT)){
					String riskTreatmentAction = denyActionJSON.getString(JSONIdentifiers.POLICY_SECTION_RISKTREATMENT);
					riskTreatment.setTextualdescription(riskTreatmentAction);
					logger.debug( "RiskTreatment:" + riskTreatment.getTextualdescription());
				}else{
					riskTreatment.setTextualdescription("The action is not allowed ");
				}
			}else{
				String maybeAction = commJSON.getString(JSONIdentifiers.POLICY_PROPERTY_MAYBE);
				JSONObject maybeActionJSON = new JSONObject(maybeAction);
				if (maybeAction.contains(JSONIdentifiers.POLICY_SECTION_RISKTREATMENT)){
					String riskTreatmentAction = maybeActionJSON.getString(JSONIdentifiers.POLICY_SECTION_RISKTREATMENT);
					riskTreatment.setTextualdescription(riskTreatmentAction);
					logger.debug( "RiskTreatment:" + riskTreatment.getTextualdescription());
				}else{
					riskTreatment.setTextualdescription("The action is not allowed, unless you make some changes");
				}
			}
		} catch (JSONException je) {
			je.printStackTrace();
		}
		
		long indexRiskTreat = dbManager.addRiskTreatment(riskTreatment);
		logger.debug( "RiskTreatment index:"+ indexRiskTreat);

		riskCommunication.setCommunicationSequence(1);
		if (indexRiskTreat>0){
			riskCommunication.setRisktreatmentId((int)indexRiskTreat);
			long indexRiskComm = dbManager.addRiskCommunication(riskCommunication);
			logger.debug( "RiskCommunication index:"+indexRiskComm);
			if (indexRiskComm>0){
				riskCommunication.setId((int)indexRiskComm);
				logger.debug( "Setting riskCommunication.id:"+riskCommunication.getId());
			}
		}
	     dbManager.closeDB();

		return riskCommunication;
	}
	
	private Riskcommunication updateRiskCommunicationRiskCommSection(JSONObject commJSON) {
		
		Riskcommunication riskCommunication = new Riskcommunication();
		Risktreatment riskTreatment = new Risktreatment();
		DBManager dbManager = new DBManager();
        dbManager.openDB();
		try {
			String treatmentComm = commJSON.getString(JSONIdentifiers.POLICY_SECTION_RISKTREATMENT);
			JSONObject treatmentJSON = new JSONObject(treatmentComm);
			String descTreatment = treatmentJSON.getString("textualdescription");// TODO Include in JSONIdentifiers
			riskTreatment.setTextualdescription(descTreatment);
			
			//Database insertion: Check if treatment exists. If not, insert it and use its id for resource	        
	        long indexTreatment = dbManager.addRiskTreatment(riskTreatment);

	        riskCommunication.setRisktreatmentId((int)indexTreatment);
			String seqComm = commJSON.getString("communication_sequence");// TODO Include in JSONIdentifiers
			riskCommunication.setCommunicationSequence((Integer.valueOf(seqComm)));
			logger.debug( "Risk Communication info:" + seqComm + "-" + descTreatment);
		} catch (JSONException je) {
			je.printStackTrace();
		}
		
		//Insert riskCommunication in db, if it does not exist		
	     long index = dbManager.addRiskCommunication(riskCommunication);
	     dbManager.closeDB();
		
		riskCommunication.setId((int)index);
		return riskCommunication;
	}



	private Subject updateSubject(JSONObject subjectJSON) {
		Subject subject = new Subject();
		Role role = new Role();
		DBManager dbManager = new DBManager();
	    dbManager.openDB();
		try {
			String roleSubject = subjectJSON.getString(JSONIdentifiers.POLICY_SECTION_ROLE);
			JSONObject roleJSON = new JSONObject(roleSubject);
			String descRole = roleJSON.getString("description");// TODO Include in JSONIdentifiers
			role.setDescription(descRole);
			
			//TODO Check if role exists. If not, insert it and use its id for subject
		    long indexRole = dbManager.addRole(role);
		    			
		    subject.setRoleId((int)indexRole);
			String descSubject = subjectJSON.getString("description");// TODO Include in JSONIdentifiers
			subject.setDescription(descSubject);
			java.util.Date now = new java.util.Date();
			subject.setModification(new Timestamp(now.getTime()));
			logger.debug( "Subject info:"+descSubject+"-"+descRole);
		} catch (JSONException je) {
			je.printStackTrace();
		}

		//Insert or update subject in db

	    long indexSubject = dbManager.addSubject(subject);
	    dbManager.closeDB();
		subject.setId((int)indexSubject);
	    
		return subject;
	}



	public Action updateAction(JSONObject actionJSON){
		Action action = new Action();
		Decision decision = new Decision();
		Risktreatment riskTreatment = new Risktreatment();
		DBManager dbManager = new DBManager();
	    dbManager.openDB();
		try {
			//Decision id generated at the server side
			String serverDecisionId = actionJSON.getString("decision");
			logger.debug("Server decision id:"+serverDecisionId);
			decision.setDecision_id(serverDecisionId);
			if (actionJSON.toString().contains("\""+JSONIdentifiers.POLICY_PROPERTY_ALLOW+"\"")){
				String allowAction = actionJSON.getString(JSONIdentifiers.POLICY_PROPERTY_ALLOW);
				JSONObject allowActionJSON = new JSONObject(allowAction);
				//Solving risk treatment
				if (allowAction.contains("solving_risktreatment")){
					String solvingRiskTreatment = allowActionJSON.getString("solving_risktreatment");
					logger.debug( "Server solving risk treatment:" + solvingRiskTreatment);
					decision.setSolving_risktreatment(Integer.valueOf(solvingRiskTreatment));
				}
				
				String idResourceAllowed = allowActionJSON.getString("id");//TODO Include in JSONIdentifiers
				logger.debug( "Allowed:" + idResourceAllowed);
				decision.setName(JSONIdentifiers.POLICY_PROPERTY_ALLOW);
				String typeAction = actionJSON.getString(JSONIdentifiers.POLICY_PROPERTY_TYPE);
				action.setDescription(typeAction);
				logger.debug( "Action type:" + typeAction);
				if (allowAction.contains(JSONIdentifiers.POLICY_CONDITION)){
					String conditionAction = allowActionJSON.getString(JSONIdentifiers.POLICY_CONDITION);
					decision.setCondition(conditionAction);
					logger.debug( "Decision condition:" + conditionAction);
				}
			}else if (actionJSON.toString().contains("\""+JSONIdentifiers.POLICY_PROPERTY_DENY+"\"")){
				String denyAction = actionJSON.getString(JSONIdentifiers.POLICY_PROPERTY_DENY);
				JSONObject denyActionJSON = new JSONObject(denyAction);
				//Solving risk treatment
				if (denyAction.contains("solving_risktreatment")){
					String solvingRiskTreatment = denyActionJSON.getString("solving_risktreatment");
					logger.debug( "Server solving risk treatment:" + solvingRiskTreatment);
					decision.setSolving_risktreatment(Integer.valueOf(solvingRiskTreatment));
				}
				String idResourceAllowed = denyActionJSON.getString("id");//TODO Include in JSONIdentifiers
				logger.debug( "Denied:" + idResourceAllowed);
				String typeAction = actionJSON.getString(JSONIdentifiers.POLICY_PROPERTY_TYPE);
				action.setDescription(typeAction);
				decision.setName(JSONIdentifiers.POLICY_PROPERTY_DENY);
				logger.debug( "Action type:" + typeAction);
				if (denyAction.contains(JSONIdentifiers.POLICY_CONDITION)){
					String conditionAction = denyActionJSON.getString(JSONIdentifiers.POLICY_CONDITION);
					decision.setCondition(conditionAction);
					logger.debug( "Decision condition:" + conditionAction);
				}
			}else {
				String maybeAction = actionJSON.getString(JSONIdentifiers.POLICY_PROPERTY_MAYBE);
				JSONObject maybeActionJSON = new JSONObject(maybeAction);
				//Solving risk treatment
				if (maybeAction.contains("solving_risktreatment")){
					String solvingRiskTreatment = maybeActionJSON.getString("solving_risktreatment");
					logger.debug( "Server solving risk treatment:" + solvingRiskTreatment);
					decision.setSolving_risktreatment(Integer.valueOf(solvingRiskTreatment));
				}
				String idResourceAllowed = maybeActionJSON.getString("id");//TODO Include in JSONIdentifiers
				logger.debug( "Denied:" + idResourceAllowed);
				String typeAction = actionJSON.getString(JSONIdentifiers.POLICY_PROPERTY_TYPE);
				action.setDescription(typeAction);
				decision.setName(JSONIdentifiers.POLICY_PROPERTY_MAYBE);
				logger.debug( "Action type:" + typeAction);
				if (maybeAction.contains(JSONIdentifiers.POLICY_CONDITION)){
					String conditionAction = maybeActionJSON.getString(JSONIdentifiers.POLICY_CONDITION);
					decision.setCondition(conditionAction);
					logger.debug( "Decision condition:" + conditionAction);
				}
			}
		} catch (JSONException je) {
			je.printStackTrace();
		}
		
		//Insert action in db, if it does not exist
		long indexAction = dbManager.addAction(action);
		logger.debug( "Action index:"+ indexAction);
	    
		action.setId((int)indexAction);
		//TODO Insert decision in db with the same description, if it does not exist
		long indexDecision = dbManager.addDecision(decision);
		decisionId = (int)indexDecision;
		logger.debug( "Decision index:"+ indexDecision);
		
		
		dbManager.closeDB();
		
		return action;
	}
	
	public Resource updateResource(JSONObject resourceJSON){
		Resource resource = new Resource();
		Resourcetype resourceType = new Resourcetype();
		DBManager dbManager = new DBManager();
	    dbManager.openDB();
		try {
			String typeResource = resourceJSON.getString(JSONIdentifiers.POLICY_PROPERTY_RESOURCETYPE);
			//TODO Check if resourcetype exists
			resourceType.setName(typeResource);
			long indexResourceType = dbManager.addResourceType(resourceType);
		    
			resource.setResourcetype((int)indexResourceType);
			
			String idResource = resourceJSON.getString("id");//TODO Include in JSONIdentifiers
			String descResource = resourceJSON.getString(JSONIdentifiers.POLICY_PROPERTY_DESCRIPTION);
			String pathResource = resourceJSON.getString(JSONIdentifiers.POLICY_PROPERTY_PATH);
			logger.debug( "Resource info:"+idResource+"-"+descResource+"-"+pathResource+"-"+typeResource);

		} catch (JSONException je) {
			je.printStackTrace();
		}
		
		//TODO Insert resource in db, if it does not exist
		long indexResource = dbManager.addResource(resource);
		dbManager.closeDB();
		logger.debug( "Resource index:"+ indexResource);
		resource.setId((int)indexResource);

		return resource;
	}
	
	public Resource updateResourceAction(JSONObject actionJSON){
		logger.debug( "updateResourceAction");
		Resource resource = new Resource();
		resource.setName("resourceName");
		resource.setSeverity("severity");
		resource.setType("type");
		DBManager dbManager = new DBManager();
	    dbManager.openDB();
		try {
			if (actionJSON.toString().contains("\""+JSONIdentifiers.POLICY_PROPERTY_ALLOW+"\"")){
				String allowAction = actionJSON.getString(JSONIdentifiers.POLICY_PROPERTY_ALLOW);
				JSONObject allowActionJSON = new JSONObject(allowAction);
				String idResourceAllowed = allowActionJSON.getString("path");//TODO Include in JSONIdentifiers
				logger.debug( "Allowed:" + idResourceAllowed);
				resource.setPath(idResourceAllowed);
				resource.setDescription(idResourceAllowed);
				if (allowAction.contains(JSONIdentifiers.POLICY_CONDITION)){
					String conditionAction = allowActionJSON.getString(JSONIdentifiers.POLICY_CONDITION);
					resource.setCondition(conditionAction);
					logger.debug( "Resource condition:" + conditionAction);
				}
			}else if (actionJSON.toString().contains("\""+JSONIdentifiers.POLICY_PROPERTY_DENY+"\"")) {
				String denyAction = actionJSON.getString(JSONIdentifiers.POLICY_PROPERTY_DENY);
				JSONObject denyActionJSON = new JSONObject(denyAction);
				String idResourceDenied = denyActionJSON.getString("path");//TODO Include in JSONIdentifiers
				logger.debug( "Denied:" + idResourceDenied);
				resource.setPath(idResourceDenied);
				resource.setDescription(idResourceDenied);
				if (denyAction.contains(JSONIdentifiers.POLICY_CONDITION)){
					String conditionAction = denyActionJSON.getString(JSONIdentifiers.POLICY_CONDITION);
					resource.setCondition(conditionAction);
					logger.debug( "Resource condition:" + conditionAction);
				}
			}else{
				String maybeAction = actionJSON.getString(JSONIdentifiers.POLICY_PROPERTY_MAYBE);
				JSONObject maybeActionJSON = new JSONObject(maybeAction);
				String idResource = maybeActionJSON.getString("path");//TODO Include in JSONIdentifiers
				logger.debug( "Maybe:" + idResource);
				resource.setPath(idResource);
				resource.setDescription(idResource);
				if (maybeAction.contains(JSONIdentifiers.POLICY_CONDITION)){
					String conditionAction = maybeActionJSON.getString(JSONIdentifiers.POLICY_CONDITION);
					resource.setCondition(conditionAction);
					logger.debug( "Resource condition:" + conditionAction);
				}
			}
		} catch (JSONException je) {
			je.printStackTrace();
		}
		
		//Insert resource in db, if it does not exist
		long indexResource = dbManager.addResource(resource);
	    dbManager.closeDB();
		resource.setId((int)indexResource);
		logger.debug( "Resource index:"+ indexResource);
		logger.debug( "Resource name:"+ resource.getName());
		//TODO Insert decision in db with the same description, if it does not exist
		
		
		return resource;
	}

}
