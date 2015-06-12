/*
 * MUSES High-Level Object Oriented Model
 * Copyright MUSES project (European Commission FP7) - 2013 
 */
package eu.musesproject.windowsclient.decisionmaker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import eu.musesproject.windowsclient.contextmonitoring.sensors.ConnectivitySensor;
import eu.musesproject.windowsclient.contextmonitoring.sensors.PackageSensor;
import eu.musesproject.windowsclient.model.Action;
import eu.musesproject.windowsclient.model.Decisiontable;
import eu.musesproject.windowsclient.model.Resource;
import eu.musesproject.windowsclient.model.Riskcommunication;
import eu.musesproject.windowsclient.model.Risktreatment;
import eu.musesproject.windowsclient.model.DBManager;
import eu.musesproject.client.model.decisiontable.ActionType;
import eu.musesproject.client.model.decisiontable.Decision;
import eu.musesproject.client.model.decisiontable.Request;
import eu.musesproject.windowsclient.usercontexteventhandler.UserContextEventHandler;
import eu.musesproject.contextmodel.ContextEvent;


/**
 * The Class LocalPolicySelector.
 * 
 * @author Sergio Zamarripa (S2)
 * @version 26 sep 2013
 */
public class DecisionMaker {

    private static final String TAG = DecisionMaker.class.getSimpleName();
    private static final String APP_TAG = "APP_TAG";
    
    /**
	 * Info DC
	 * 
	 *  Method to notify the decision maker about an incoming request
	 * 
	 * @param request
	 * 
	 * 
	 * @return 
	 */
	
	public void notifyActionRequest(Request request){

	
	}	
	private String getConditionType(String condition) {
		if (condition.contains("installedApps")){
			return "event";
		}else{
			return "property";
		}
	}
	public Decision manageDecision(Request request, List<ContextEvent> eventList, Map<String, String> properties){
		
		Decision resultDecision = null;
		Map<String,String> conditions = new HashMap<String,String>();
		Map<String,String> eventProperties = new HashMap<String,String>();
		String condition = null;
		eu.musesproject.windowsclient.model.Decision entityDecision = null;
		eu.musesproject.windowsclient.model.Decisiontable dt = null;
		eu.musesproject.windowsclient.model.Riskcommunication comm = null;
		eu.musesproject.windowsclient.model.Risktreatment treatment = null;
		boolean match = false;
		
		DBManager dbManager = new DBManager();
        dbManager.openDB();
        
       
        List<eu.musesproject.windowsclient.model.Decision> list = dbManager.getAllDecisionsWithCondition();

        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
        	eu.musesproject.windowsclient.model.Decision decision = (eu.musesproject.windowsclient.model.Decision) iterator.next();
        	condition = decision.getCondition();
        	if  (getConditionType(condition).equals("event")){
        		conditions.put(decision.getCondition(), String.valueOf(decision.getId()));
        	}			
		}
        
        // Now, check if any ContextEvent in the eventList satisfies such conditions
        
        for (Map.Entry<String, String> entry : conditions.entrySet())
        {
            System.out.println("1. Decision condition to be checked: "+entry.getKey() + "/" + entry.getValue());
            System.out.println("Event List size:"+eventList.size());
            //Iterate over eventList
            for (Iterator iterator = eventList.iterator(); iterator.hasNext();) {
				ContextEvent contextEvent = (ContextEvent) iterator.next();
				//Get properties of such contextEvent
				eventProperties= contextEvent.getProperties();
				//Iterate over the event properties to check if the condition is in place
				for (Map.Entry<String, String> propEntry : eventProperties.entrySet()){
					String propKey = propEntry.getKey();
					 System.out.println("2. Property event to be checked: "+propEntry.getKey() + "/" + propEntry.getValue());
					 if (entry.getKey().toLowerCase().contains(propKey.toLowerCase())){
						 String value = entry.getKey()
									.substring(
											entry.getKey()
													.indexOf(":") + 2,
													entry.getKey()
													.length() - 2);
						 System.out.println("2.1 Value: "+value);
						 if ((propKey.contains("installedapps"))&&(!propEntry.getValue().contains(value))){
							System.out.println("3.installedapps Match!");
							match = true;
						 }else if (propEntry.getValue().contains(value)){
							 System.out.println("3.Match!");
							 match = true;
						 }
					 }
				}
			}
            if (match){
            	entityDecision = dbManager.getDecisionFromID(entry.getValue());
				dt = dbManager.getDecisionTableFromID(String.valueOf(entityDecision.getId()));
				comm= dbManager.getRiskCommunicationFromID(String.valueOf(dt.getRiskcommunicationId()));
		        	if (comm != null){
		        		treatment = dbManager.getRiskTreatmentFromID(String.valueOf(comm.getRisktreatmentId()));
		        	}
		        	resultDecision = composeDecision(entityDecision, comm, treatment);
				 return resultDecision;
            }else{
            	return null;
            }
        }
 
		
		return resultDecision;
		
	}


	/**
	 * Info DC
	 * 
	 *  Method to process the decision regarding a request
	 * 
	 * @param request
	 * 
	 * 
	 * @return 
	 */
	
	public Decision makeDecision(Request request, List<ContextEvent> eventList, Map<String, String> properties){
		
		Decision priorDecision = manageDecision(request, eventList, properties);
		if (priorDecision != null){
			Logger.getLogger(TAG).log(Level.WARNING, "Policy Device Decision: " + priorDecision.getName());
			return priorDecision;
		}
		
		//Log.d(APP_TAG, "Info DC, DecisionMaker=> Making decision with request and events");
        System.out.println( "called: makeDecision(Request request, List<ContextEvent> eventList)");
        String resourceCondition = null;

        eu.musesproject.windowsclient.model.Decision decision = new eu.musesproject.windowsclient.model.Decision();
        eu.musesproject.windowsclient.model.Riskcommunication comm = new eu.musesproject.windowsclient.model.Riskcommunication();
        eu.musesproject.windowsclient.model.Risktreatment treatment = new eu.musesproject.windowsclient.model.Risktreatment();
        Resource resourceInPolicy = null;
        Action actionInPolicy = new Action();
        Riskcommunication riskCommInPolicy = new Riskcommunication();
        Risktreatment riskTreatInPolicy = new Risktreatment();
        Decision resultDecision = new Decision();
        Decisiontable decisionTable = null;
        
        System.out.println( "Action type:"+request.getAction().getActionType());
        System.out.println( "Action description:"+request.getAction().getDescription());
        System.out.println( "Action id:"+request.getAction().getId());
        System.out.println( "Action timestamp:"+request.getAction().getTimestamp());
        
        System.out.println( "Resource description:"+request.getResource().getDescription());
        
        for (Iterator iterator = eventList.iterator(); iterator.hasNext();) {
			ContextEvent contextEvent = (ContextEvent) iterator.next();
			System.out.println( "Event list:"+contextEvent.getType());
		}
        
        System.out.println( "Resource:"+request.getResource());
        System.out.println( "Resource path:"+request.getResource().getPath());
        
        DBManager dbManager = new DBManager();
        dbManager.openDB();
        
        
        if ((request.getAction()!=null)&&(request.getResource()!=null)){

        		System.out.println( "Find resource by condition properties..." );
        		List<Resource> allConditionResources = dbManager.getAllResources();
        		System.out.println( "Found..."+allConditionResources.size());
        		
        		for (Iterator iterator = allConditionResources.iterator(); iterator
						.hasNext();) {
					Resource resource = (Resource) iterator.next();
					System.out.println( "Id:"+resource.getId());
					if (resource.getCondition()!=null){
						System.out.println( "Condition:"+resource.getCondition());
						
						System.out.println( "Resource properties:");
		        		for (Map.Entry<String, String> entry : properties.entrySet())
		                {        			
		        			String comparisonString = null;
		        			if (entry.getKey().contains("path")||entry.getKey().contains("resource")||entry.getKey().contains("packagename")){
		        				comparisonString = "{\""+entry.getKey()+"\":\""+entry.getValue()+"\"}";
		        			}else{
		        				comparisonString = "{\""+entry.getKey()+"\":"+entry.getValue()+"}";
		        			}
		        			
		                    System.out.println( "	"+comparisonString);
		                    
		                    
		                    if(resource.getCondition().contains("\\/")){
		                    	resourceCondition = resource.getCondition().replace("\\/","/");
		                    }else{
		                    	resourceCondition = resource.getCondition();
		                    }

		                    if (resourceCondition.toLowerCase().equals(comparisonString.toLowerCase())){
		                    	 System.out.println("	Match!");
		                    	resourceInPolicy = resource;//No break, since the last one should have priority over older ones
		                    	break;
							} else {
								System.out.println( "	No Match!" + comparisonString);

								try{
									if (resourceCondition.contains(":")) {
									String property = resourceCondition
											.substring(
													0,
													resourceCondition
															.indexOf(":") - 1);
									System.out.println("property:" + property);
									if (property.contains(entry.getKey())) {
										int intValue = -1;
										String value = resource
												.getCondition()
												.substring(
														resourceCondition
																.indexOf(":") + 1,
														resourceCondition
																.length() - 1);
										System.out.println("value:" + value);
										try {
											intValue = Integer.valueOf(value);
										} catch (NumberFormatException e) {
											System.out.println("value " + value
													+ " is not a number");
										}

										if (intValue != -1) {
											int currentValue = -1;
											System.out.println(
													"Current value:"
															+ entry.getValue());
											try {
												currentValue = Integer
														.valueOf(entry
																.getValue());
											} catch (NumberFormatException e) {
												System.out.println("current value "
														+ entry.getValue()
														+ " is not a number");
											}
											if (currentValue != -1) {
												if (currentValue < intValue) {
													System.out.println("Current value "
															+ currentValue
															+ " is less than "
															+ intValue);
													System.out.println("Allow");
													dbManager.closeDB();
													return getConditionNotSatisfiedDecision();
												} else {
													System.out.println(
															"Current value "
																	+ currentValue
																	+ " is greater or equal than "
																	+ intValue);
												}
											}
										}

									}
								}
								}catch (Exception e){
									System.out.println(e.getMessage());
								}	
							}
		                    
		                    //Connectivity condition
		                    
		                    if (resourceCondition.contains("wifi")){
		                    	for (Iterator iterator1 = eventList.iterator(); iterator1.hasNext();) {
		                			ContextEvent contextEvent = (ContextEvent) iterator1.next();
		                			System.out.println( "Event list:"+contextEvent.getType());
		                			if (contextEvent.getType().equals(ConnectivitySensor.TYPE)){
		                				System.out.println( "resourcecondition:"+resourceCondition);
		                				
		                				for (Map.Entry<String, String> connEntry : contextEvent.getProperties().entrySet())
		        		                { 
		                					String currentProperty = "{\""+connEntry.getKey()+"\":\""+connEntry.getValue()+"\"}";
		                					System.out.println("WIFI     "+currentProperty);
		                					
		                					if (resourceCondition.toLowerCase().equals(currentProperty.toLowerCase())){		                						
		                						System.out.println( "	Environment Match!");
		                						
		                						if (request.getResource().getPath()!=null){
		                			        		System.out.println( "Request path:" + request.getResource().getPath() );
		                			        		System.out.println( "Resource:" + resource.getPath() );
		                			        		if (resource.getPath().equals(request.getResource().getPath())){
		                			        			System.out.println( "	Path Match!");
		                			        			resourceInPolicy = resource;
				                						break;
		                			        		}else{
		                			        			System.out.println( "	No Path Match!");
		                			        		}
		                			        		
		                						}else{
		                							System.out.println( "	Path for resource is null!");
		                						}
		                						
		                					} else {
		                						System.out.println( "	No EnvironmentMatch!" + currentProperty);
		                					}
		        		                }
		                				
		                				
		                				
		                			}
		                		}
		                    }else if (resourceCondition.contains("package")){
		                    	for (Iterator iterator1 = eventList.iterator(); iterator1.hasNext();) {
		                			ContextEvent contextEvent = (ContextEvent) iterator1.next();
		                			System.out.println( "Event list:"+contextEvent.getType());
		                			if (contextEvent.getType().equals(PackageSensor.TYPE)){
		                				System.out.println( "resourcecondition:"+resourceCondition);
		                				
		                				for (Map.Entry<String, String> connEntry : contextEvent.getProperties().entrySet())
		        		                { 
		                					String currentProperty = "{\""+connEntry.getKey()+"\":\""+connEntry.getValue()+"\"}";
		                					System.out.println("PACKAGE     "+currentProperty);
		                					
		                					if (resourceCondition.toLowerCase().equals(currentProperty.toLowerCase())){		                						
		                						System.out.println( "	Package Match!");
		                						
		                						resourceInPolicy = resource;
		                						System.out.println( " resourceInPolicy:"+ resourceInPolicy.getPath());
		                						break;
		                						
		                					} else {
		                						System.out.println( "	No EnvironmentMatch!" + currentProperty);
		                					}
		        		                }
		                				
		                				
		                				
		                			}
		                		}
		                    }
		                    
		                }
		        		
					}else{
						System.out.println( "Condition null");
					}
				}
        		
        	
        	if (resourceInPolicy == null){

        		System.out.println( "Looking for resource by description" );
        		resourceInPolicy = dbManager.getResourceFromPath(request.getResource().getDescription());
        		if ((resourceInPolicy == null)||(resourceInPolicy.getPath()==null)||(resourceInPolicy.getId()==0)){
        			dbManager.closeDB();
        			return null;
        		}
        	}else{
        		System.out.println( "resourceInPolicy not null" );
        	}
        	
        	actionInPolicy = dbManager.getActionFromType(request.getAction().getActionType());        	
        	System.out.println( "Resource in table:" + resourceInPolicy.getPath() + " Id:" +  resourceInPolicy.getId());
        	System.out.println( "Action in table:" + actionInPolicy.getDescription() + " Id:" +  actionInPolicy.getId());
        	decisionTable = dbManager.getDecisionTableFromResourceId(String.valueOf(resourceInPolicy.getId()),String.valueOf(actionInPolicy.getId()));
        	System.out.println( "DT in table: Id:" +  decisionTable.getId());
        	if (decisionTable.getId()==0){
        		dbManager.closeDB();
        		return null;
        	}
        	System.out.println( "Retrieving riskCommunication associated to id:" +  String.valueOf(decisionTable.getRiskcommunicationId()));
        	riskCommInPolicy = dbManager.getRiskCommunicationFromID(String.valueOf(decisionTable.getRiskcommunicationId()));
        	System.out.println( "RiskComm in table: Id:" +  riskCommInPolicy.getId());
        	if (riskCommInPolicy != null){
        		System.out.println( "Retrieving riskTreatment associated to id:" +  String.valueOf(riskCommInPolicy.getRisktreatmentId()));
        		riskTreatInPolicy = dbManager.getRiskTreatmentFromID(String.valueOf(riskCommInPolicy.getRisktreatmentId()));
        		System.out.println( "RiskTreat in table:" + riskTreatInPolicy.getTextualdescription() + " Id:" +  riskTreatInPolicy.getId());
        		
        	}
        }
        
        if (decisionTable != null){
        	decision = dbManager.getDecisionFromID(String.valueOf(decisionTable.getDecisionId()));
        	
        	if (decision!=null){
        	String condition = decision.getCondition();
        	if ((decision.getName()!=null)&&(decision.getName().equals("maybe"))){
    			if ((condition!=null)&&(!condition.equals("any"))){
        			if (condition.contains("wifiencryption")){// TODO This should be managed by a ConditionHelper, to be implemented
        				for (Iterator iterator = eventList.iterator(); iterator.hasNext();) {
							ContextEvent contextEvent = (ContextEvent) iterator.next();
							if (contextEvent.getType().equals(ConnectivitySensor.TYPE)){
								if (contextEvent.getProperties()!=null){
									Map<String,String> map = contextEvent.getProperties();
									for(Map.Entry<String, String> entry : map.entrySet()){
										if (entry.getKey().contains("wifiencryption")){
											Logger.getLogger(TAG).log(Level.WARNING, "Condition with wifiencryption");
											condition = condition.substring(("wifiencryption").length());
											Logger.getLogger(TAG).log(Level.WARNING, "" + condition);
											if (condition.startsWith("!=")){
												String comparisonValue = condition.substring(2);
												System.out.println( "comparisonValue:"+comparisonValue);
												if (!entry.getValue().contains(comparisonValue)){
													//Deny
													Logger.getLogger(TAG).log(Level.WARNING, "Condition satisfied: MUSES should say maybe, explaining the risk treatment");
													resultDecision.setName(Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS);
													eu.musesproject.server.risktrust.RiskTreatment [] riskTreatments = new eu.musesproject.server.risktrust.RiskTreatment[1];												
													eu.musesproject.server.risktrust.RiskTreatment riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(riskTreatInPolicy.getTextualdescription());
													eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
													riskTreatments[0] = riskTreatment;	
													riskCommunication.setRiskTreatment(riskTreatments);
													resultDecision.setRiskCommunication(riskCommunication); 
													dbManager.closeDB();
													return resultDecision;
												}else{
													//Allow
													Logger.getLogger(TAG).log(Level.WARNING, "Condition not satisfied: "+comparisonValue+".MUSES should allow");
													resultDecision.setName(Decision.GRANTED_ACCESS);
													dbManager.closeDB();
													return resultDecision;
												}
											}
										}
									}
									
								}
							}
						}
        			}
        		}
        	}else if ((decision.getName()!=null)&&(decision.getName().equals("allow"))){
        		if ((condition!=null)&&(!condition.equals("any"))){
        			System.out.println( "Allow decision with a concrete condition");
        		}else{
        			System.out.println( "Allow decision with any condition");
					resultDecision.setName(Decision.GRANTED_ACCESS);
        			

        			eu.musesproject.server.risktrust.RiskTreatment [] riskTreatments = new eu.musesproject.server.risktrust.RiskTreatment[1];				
					eu.musesproject.server.risktrust.RiskTreatment riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(riskTreatInPolicy.getTextualdescription());
					eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
					riskTreatments[0] = riskTreatment;
					System.out.println( "RiskTreatment inserted for feedback:"+ riskTreatment.getTextualDescription());
					riskCommunication.setRiskTreatment(riskTreatments);
					resultDecision.setRiskCommunication(riskCommunication); 

        		
					
					eu.musesproject.server.risktrust.RiskTreatment[] r = resultDecision.getRiskCommunication().getRiskTreatment();// TODO Remove: Simple log
					System.out.println( "RiskTreat for feedback:"+ resultDecision.getRiskCommunication().getRiskTreatment());
					if (r[0].getTextualDescription() != null) {
						String textualDecp = r[0].getTextualDescription();
						System.out.println( "RiskTreatment:"+textualDecp);
					}else{
						System.out.println( "RiskTreatment textualDescription null. Array length:"+r.length);
					}
					
					dbManager.closeDB();
					return resultDecision;
        		}
    		}else if ((decision.getName()!=null)&&(decision.getName().equals("deny"))){
        		if ((condition!=null)&&(!condition.equals("any"))){
        			System.out.println( "Deny with condition");
        		}else{
        			System.out.println( "Deny");
        			resultDecision.setName(Decision.STRONG_DENY_ACCESS);
        			eu.musesproject.server.risktrust.RiskTreatment [] riskTreatments = new eu.musesproject.server.risktrust.RiskTreatment[1];				
					eu.musesproject.server.risktrust.RiskTreatment riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(riskTreatInPolicy.getTextualdescription());
					eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
					riskTreatments[0] = riskTreatment;
					System.out.println( "RiskTreatment inserted for feedback:"+ riskTreatment.getTextualDescription());
					riskCommunication.setRiskTreatment(riskTreatments);
					resultDecision.setRiskCommunication(riskCommunication); 

        		
					
					eu.musesproject.server.risktrust.RiskTreatment[] r = resultDecision.getRiskCommunication().getRiskTreatment();// TODO Remove: Simple log
					System.out.println( "RiskTreat for feedback:"+ resultDecision.getRiskCommunication().getRiskTreatment());
					if (r[0].getTextualDescription() != null) {
						String textualDecp = r[0].getTextualDescription();
						System.out.println( "RiskTreatment:"+textualDecp);
					}else{
						System.out.println( "RiskTreatment textualDescription null. Array length:"+r.length);
					}
					
					dbManager.closeDB();
					return resultDecision;
        		}
    		}else if ((decision.getName()!=null)&&(decision.getName().equals("up-to-you"))){
        		if ((condition!=null)&&(!condition.equals("any"))){
        			System.out.println( "Up to user with condition");
        		}else{
        			System.out.println( "Up to user");
        			resultDecision.setName(Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION);
        			eu.musesproject.server.risktrust.RiskTreatment [] riskTreatments = new eu.musesproject.server.risktrust.RiskTreatment[1];				
					eu.musesproject.server.risktrust.RiskTreatment riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(riskTreatInPolicy.getTextualdescription());
					eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
					riskTreatments[0] = riskTreatment;
					System.out.println( "RiskTreatment inserted for feedback:"+ riskTreatment.getTextualDescription());
					riskCommunication.setRiskTreatment(riskTreatments);
					resultDecision.setRiskCommunication(riskCommunication); 

        		
					
					eu.musesproject.server.risktrust.RiskTreatment[] r = resultDecision.getRiskCommunication().getRiskTreatment();// TODO Remove: Simple log
					System.out.println( "RiskTreat for feedback:"+ resultDecision.getRiskCommunication().getRiskTreatment());
					if (r[0].getTextualDescription() != null) {
						String textualDecp = r[0].getTextualDescription();
						System.out.println( "RiskTreatment:"+textualDecp);
					}else{
						System.out.println( "RiskTreatment textualDescription null. Array length:"+r.length);
					}
					
					dbManager.closeDB();
					return resultDecision;
        		}
    		}
        }

        	comm= dbManager.getRiskCommunicationFromID(String.valueOf(decisionTable.getRiskcommunicationId()));
        	if (comm != null){
        		treatment = dbManager.getRiskTreatmentFromID(String.valueOf(comm.getRisktreatmentId()));
        	}
        	resultDecision = composeDecision(decision, comm, treatment);
        }else{
        	System.out.println("Decision table is null");
        	dbManager.closeDB();
        	return null;
        }
        

        
        dbManager.closeDB();
		return resultDecision;

	}	
	
	private Decision composeDecision(
			eu.musesproject.windowsclient.model.Decision decision,
			eu.musesproject.windowsclient.model.Riskcommunication comm,
			eu.musesproject.windowsclient.model.Risktreatment treatment) {
		
		Decision resultDecision = new Decision();
		eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
		eu.musesproject.server.risktrust.RiskTreatment riskTreatment = null;
		eu.musesproject.server.risktrust.RiskTreatment[] arrayTreatment = null;
		
		if (decision != null){
			if (decision.getDecision_id()!=null){
				resultDecision.setDecision_id(decision.getDecision_id());
				Logger.getLogger(TAG).log(Level.INFO, "Server decision id set to:"+decision.getDecision_id());
			}
			resultDecision.setSolving_risktreatment(decision.getSolving_risktreatment());
			Logger.getLogger(TAG).log(Level.INFO, "Server solving risk treatment set to:"+decision.getSolving_risktreatment());
			if (decision.getName() != null){
				if (decision.getName().equals("deny")){
					resultDecision.setName(Decision.STRONG_DENY_ACCESS);
				}else if (decision.getName().equals("maybe")){
					resultDecision.setName(Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS);
				}else if (decision.getName().equals("allow")){
					resultDecision.setName(Decision.GRANTED_ACCESS);
				}else if (decision.getName().equals("up-to-you")){
					resultDecision.setName(Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION);
				}
				
				Logger.getLogger(TAG).log(Level.WARNING, "Policy Device Decision: " + decision.getName());
			}else{
				Logger.getLogger(TAG).log(Level.WARNING, "No decision is found. Hence, MUSES sets default decision");
				resultDecision.setName(Decision.STRONG_DENY_ACCESS);//Default decision is deny
			}
		}else {
			Logger.getLogger(TAG).log(Level.WARNING, "No decision is found. Hence, MUSES sets default decision");
			resultDecision.setName(Decision.STRONG_DENY_ACCESS);//Default decision is deny
		}
		riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(treatment.getTextualdescription());
		arrayTreatment = new eu.musesproject.server.risktrust.RiskTreatment[]{riskTreatment};
		riskCommunication.setRiskTreatment(arrayTreatment);
		resultDecision.setRiskCommunication(riskCommunication);
		Logger.getLogger(TAG).log(Level.WARNING, "Result decision: " + resultDecision.getName());
		Logger.getLogger(TAG).log(Level.WARNING, "Risk treatment: " + treatment.getTextualdescription());
		
		return resultDecision;
	}

	/**
	 * Info DC
	 * 
	 *  Method to push the decision associated to a request, including RiskTreatment and RiskCommunication
	 * 
	 * @param request
	 * 
	 * 
	 * @return Decision
	 */
	
	public Decision pushDecisionToEventHandler(Request request){
		
		return null;

	}
	
	public Decision makeDummyDecision(Request request, List<ContextEvent> eventList){
		
		Decision decision = new Decision();

		eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
		eu.musesproject.server.risktrust.RiskTreatment riskTreatment = null;
		eu.musesproject.server.risktrust.RiskTreatment[] arrayTreatment = new eu.musesproject.server.risktrust.RiskTreatment[]{riskTreatment};
		
	
		if(request.getAction() != null) {
		    if (request.getAction().getActionType().equals(ActionType.ACCESS)){
		        decision.setName(Decision.GRANTED_ACCESS);
		        riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment("No additional treatment is needed");
		    }else if (request.getAction().getActionType().equals(ActionType.OPEN)){
		        decision.setName(Decision.MAYBE_ACCESS_WITH_RISKTREATMENTS);
		        riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment("Requested action will be allowed with the user connects to an encrypted connection");
		    }else if (request.getAction().getActionType().equals(ActionType.RUN)){
		        decision.setName(Decision.STRONG_DENY_ACCESS);
		        riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment("Requested action is not allowed, no matter the settings");//TODO: Us
		    }else if (request.getAction().getActionType().equals(ActionType.INSTALL)){
		        decision.setName(Decision.UPTOYOU_ACCESS_WITH_RISKCOMMUNICATION);
		        riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment("This action is potentially unsecure.You might continue with the action under your own risk");
		    } else {
		    	decision.setName(Decision.STRONG_DENY_ACCESS);
		    	riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment("Requested action is not allowed, no matter the settings");
		    }
		}
		
		riskCommunication.setRiskTreatment(arrayTreatment);
		decision.setRiskCommunication(riskCommunication);
		return decision;
	}
	
	public Decision getDefaultDecision() {

		System.out.println("Returning default decision...");
		Decision defaultDecision = new Decision();
		defaultDecision.setName(Decision.DEFAULT_DENY_ACCESS);
		eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
		eu.musesproject.server.risktrust.RiskTreatment riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(
				"Decision denied by default (since no concrete local device policies apply)");
		eu.musesproject.server.risktrust.RiskTreatment[] arrayTreatment = new eu.musesproject.server.risktrust.RiskTreatment[] { riskTreatment };
		riskCommunication.setRiskTreatment(arrayTreatment);
		defaultDecision.setRiskCommunication(riskCommunication);
		return defaultDecision;
	}
	
	public Decision getDefaultDecision(eu.musesproject.client.model.decisiontable.Action action) {

		System.out.println("Returning default decision based on action ...");
		Decision defaultDecision = new Decision();
		eu.musesproject.server.risktrust.RiskTreatment riskTreatment = null;
		eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
		//TODO These default decisions should be pre-loaded when the client is connected to the server, and will be accessed from the local database
		if (action.getActionType().equals(ActionType.ACCESS)){
			defaultDecision.setName(Decision.DEFAULT_DENY_ACCESS);
			riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(	"Decision denied by default, according to action: " + action.getActionType());
		}else if (action.getActionType().equals(ActionType.CANCEL)){
			defaultDecision.setName(Decision.GRANTED_ACCESS);
			riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(	"You are allowed to go on, under your own responsibility" + action.getActionType());
		}else if (action.getActionType().equals(ActionType.DELETE)){
			defaultDecision.setName(Decision.DEFAULT_DENY_ACCESS);
			riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(	"Decision denied by default, according to action: " + action.getActionType());
		}else if (action.getActionType().equals(ActionType.ENCRYPT_EVENT)){
			defaultDecision.setName(Decision.GRANTED_ACCESS);
			riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(	"You are allowed to go on, under your own responsibility" + action.getActionType());
		}else if (action.getActionType().equals(ActionType.FILE_ATTACHED)){
			defaultDecision.setName(Decision.GRANTED_ACCESS);
			riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(	"You are allowed to go on, under your own responsibility" + action.getActionType());
		}else if (action.getActionType().equals(ActionType.INSTALL)){
			defaultDecision.setName(Decision.DEFAULT_DENY_ACCESS);
			riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(	"Decision denied by default, according to action: " + action.getActionType());
		}else if (action.getActionType().equals(ActionType.OK)){
			defaultDecision.setName(Decision.GRANTED_ACCESS);
			riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(	"You are allowed to go on, under your own responsibility" + action.getActionType());
		}else if (action.getActionType().equals(ActionType.OPEN)){
			defaultDecision.setName(Decision.GRANTED_ACCESS);
			riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(	"You are allowed to go on, under your own responsibility" + action.getActionType());
		}else if (action.getActionType().equals(ActionType.OPEN_APPLICATION)){
			defaultDecision.setName(Decision.GRANTED_ACCESS);
			riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(	"You are allowed to go on, under your own responsibility" + action.getActionType());
		}else if (action.getActionType().equals(ActionType.OPEN_ASSET)){
			defaultDecision.setName(Decision.GRANTED_ACCESS);
			riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(	"You are allowed to go on, under your own responsibility" + action.getActionType());
		}else if (action.getActionType().equals(ActionType.SAVE_ASSET)){
			defaultDecision.setName(Decision.GRANTED_ACCESS);
			riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(	"You are allowed to go on, under your own responsibility" + action.getActionType());
		}else if (action.getActionType().equals(ActionType.SECURITY_PROPERTY_CHANGED)){
			defaultDecision.setName(Decision.DEFAULT_DENY_ACCESS);
			riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(	"Decision denied by default, according to action: " + action.getActionType());
		}else if (action.getActionType().equals(ActionType.SEND)){
			defaultDecision.setName(Decision.DEFAULT_DENY_ACCESS);
			riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(	"Decision denied by default, according to action: " + action.getActionType());
		}else if (action.getActionType().equals(ActionType.SEND_MAIL)){
			defaultDecision.setName(Decision.GRANTED_ACCESS);
			riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(	"You are allowed to go on, under your own responsibility" + action.getActionType());
		}else if (action.getActionType().equals(ActionType.UNINSTALL)){
			defaultDecision.setName(Decision.DEFAULT_DENY_ACCESS);
			riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(	"Decision denied by default, according to action: " + action.getActionType());
		}else if (action.getActionType().equals(ActionType.UPDATE)){
			defaultDecision.setName(Decision.DEFAULT_DENY_ACCESS);
			riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(	"Decision denied by default, according to action: " + action.getActionType());
		}else if (action.getActionType().equals(ActionType.VIRUS_FOUND)){
			defaultDecision.setName(Decision.DEFAULT_DENY_ACCESS);
			riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(	"Decision denied by default, according to action: " + action.getActionType());
		}
		
		eu.musesproject.server.risktrust.RiskTreatment[] arrayTreatment = new eu.musesproject.server.risktrust.RiskTreatment[] { riskTreatment };
		riskCommunication.setRiskTreatment(arrayTreatment);
		defaultDecision.setRiskCommunication(riskCommunication);
		return defaultDecision;

	}
	
	public Decision getConditionNotSatisfiedDecision() {

		System.out.println("Returning allow decision due to condition not satisfied...");
		Decision defaultDecision = new Decision();
		defaultDecision.setName(Decision.GRANTED_ACCESS);
		eu.musesproject.server.risktrust.RiskCommunication riskCommunication = new eu.musesproject.server.risktrust.RiskCommunication();
		eu.musesproject.server.risktrust.RiskTreatment riskTreatment = new eu.musesproject.server.risktrust.RiskTreatment(
				"Decision allowed");
		eu.musesproject.server.risktrust.RiskTreatment[] arrayTreatment = new eu.musesproject.server.risktrust.RiskTreatment[] { riskTreatment };
		riskCommunication.setRiskTreatment(arrayTreatment);
		defaultDecision.setRiskCommunication(riskCommunication);
		return defaultDecision;
	}
	
	public Decision getDefaultDecision(
			eu.musesproject.client.model.decisiontable.Action action,
			Map<String, String> actionProperties,
			List<ContextEvent> contextEvents) {

		return getDefaultDecision(action);
	}
}