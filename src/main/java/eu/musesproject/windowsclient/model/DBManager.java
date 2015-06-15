package eu.musesproject.windowsclient.model;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import eu.musesproject.windowsclient.view.MusesUtils;

public class DBManager {
	
	private static SessionFactory sessionFactory = null;
	private static ServiceRegistry serviceRegistry;
	private static final String MUSES_TAG = "MUSES_TAG";
	private static Logger logger = Logger.getLogger(DBManager.class.getName());
	
	public DBManager() {

	}

	private SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
			configuration.configure();
			serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
					configuration.getProperties()).build();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
			
		}
		return sessionFactory;
	}
	
	public void openDB() {
		// Does not do anything but called by Functional layer
	}
	
	public void closeDB() {
		// Does not do anything but called by Functional layer
	}
	
	
	public void persist(Object transientInstance) {
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    Transaction trans = null;
		    try {
		    	trans = session.beginTransaction();
		    	session.save(transientInstance);
		        trans.commit();
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}
	}

	// All CRUD (Create, retrieve, update and delete ) operations here

	public void insertSensorConfiguration(SensorConfiguration sensorConfiguration){
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    Transaction trans = null;
		    try {
		    	trans = session.beginTransaction();
		    	session.save(sensorConfiguration);
		        trans.commit();
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}
	}

	// check if an equal config item exists to avoid duplicate entries
	private boolean sensorConfigExists(SensorConfiguration sensorConfiguration) {
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("SensorConfiguration.findAll");
			if (query.list().size() != 0) {
				int noOfRows = query.list().size();
				if (noOfRows>0){
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return false;	
	}

	public boolean hasSensorConfig() {
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("SensorConfiguration.findAll");
			if (query.list().size() != 0) {
				int noOfRows = query.list().size();
				if (noOfRows>0){
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return false;	
	}

	public List<SensorConfiguration> getAllSensorConfiguration(){

		List<SensorConfiguration> configurationList = new ArrayList<SensorConfiguration>();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("SensorConfiguration.findAll");
			if (query.list().size() != 0) {
				configurationList = query.list();
				return configurationList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return configurationList;
	}

	public void inserRequiredAppList() {
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    Transaction trans = null;
		    try {
		    	trans = session.beginTransaction();
		    	RequiredApps avast = new RequiredApps();
		    	avast.setName("Avast");
		    	avast.setVersion("3.10");
		    	avast.setUniqueName("com.avast.security.antivirus");
		    	session.save(avast);
		        trans.commit();
		        
		        trans = session.beginTransaction();
		        RequiredApps anyConnect = new RequiredApps();
		        anyConnect.setName("AnyConnect VPN Client");
		    	anyConnect.setVersion("2.20");
		    	anyConnect.setUniqueName("com.anyconnect.vpn.client");
		    	session.save(anyConnect);
		        trans.commit();
		        
		        trans = session.beginTransaction();
		        RequiredApps lotus = new RequiredApps();
		    	lotus.setName("Lotus");
		    	lotus.setVersion("1.11");
		    	lotus.setUniqueName("com.lotus.email.client");
		    	session.save(lotus);
		        trans.commit();
		        
		        trans = session.beginTransaction();
		        RequiredApps encrypt = new RequiredApps();
		    	encrypt.setName("Encrypt Plus");
		    	encrypt.setVersion("1.08");
		    	encrypt.setUniqueName("com.secure.encryptplus");
		    	session.save(encrypt);
		        trans.commit();
		        
		        
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}
		

	}

	public List<RequiredApps> getRequiredAppList(){
		List<RequiredApps> appsList = new ArrayList<RequiredApps>();
		Query query = null;
		Session session = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("RequiredApps.findAll");
			if (query.list().size() != 0) {
				appsList = query.list();
				return appsList;
			} 
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return appsList;
	}

	public void insertCredentials(String deviceId, String userName, String password){
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    boolean isUserAuthenticated = isUserAuthenticated(deviceId, userName, password);
		    if (!isUserAuthenticated){
		    	Transaction trans = null;
		    	try {
		    		trans = session.beginTransaction();
		    		UserCredentials userCredential = new UserCredentials();
		    		userCredential.setUsername(userName);
		    		userCredential.setPassword(password);
		    		userCredential.setDeviceId(deviceId);
		    		session.save(userCredential);
		    		trans.commit();
		    		
		    	} catch (Exception e) {
		    		if (trans!=null) trans.rollback();
		    		e.printStackTrace(); 
		    	} finally {
		    		if (session!=null) session.close();
		    	}
		    } else {
		    	Transaction trans = null;
		    	try {
		    		trans = session.beginTransaction();
		    		UserCredentials userCredential = new UserCredentials();
		    		userCredential.setUsername(userName);
		    		userCredential.setPassword(password);
		    		userCredential.setDeviceId(deviceId);
		    		session.merge(userCredential);
		    		trans.commit();
		    		
		    	} catch (Exception e) {
		    		if (trans!=null) trans.rollback();
		    		e.printStackTrace(); 
		    	} finally {
		    		if (session!=null) session.close();
		    	}

		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}

	}

	public boolean isUserAuthenticated(String deviceId, String userName, String password) {
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("UserCredentials.findByUsername").setString("username", userName);
			if (query.list().size() != 0) {
				int noOfRows = query.list().size();
				if (noOfRows>0){
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return false;
	}

	public boolean isUserAuthenticated() {
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("UserCredentials.findAll");
			if (query.list().size() != 0) {
				int noOfRows = query.list().size();
				if (noOfRows>0){
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return false;
	}
	
	public UserCredentials getUserCredentials(){
		Session session = null;
		Query query = null;
		UserCredentials userCredentials = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("UserCredentials.findAll");
			if (query.list().size() != 0) {
				userCredentials = (UserCredentials) query.list().get(0);
				return userCredentials;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return userCredentials;
	}
	
	public void deleteUserCredentials(String userName) {
		Session session = null;
		try {
			session = getSessionFactory().openSession();
			session.getNamedQuery("UserCredentials.deleteByUsername").setString("username", userName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}

	}

	public String getDevId(){
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("UserCredentials.findAll");
			if (query.list().size() != 0) {
				List<UserCredentials> userCredList = query.list();
				for (UserCredentials d: userCredList) {
					return d.getDeviceId();
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;
	}

	public void insertConnectionProperties() {
		Configuration config = new Configuration();
		config.setServerIp(MusesUtils.getMusesConf());
		config.setServerPort("8443");
		config.setServerContextPath("/server");
		config.setServerServletPath("/commain");
		config.setServerCertificate(MusesUtils.getCertificateFromSDCard());
		config.setClientCertificate("");
		config.setTimeout(5000);
		config.setPollTimeout(60000);
		config.setSleepPollTimeout(60000);
		config.setPollingEnabled(1);
		config.setLoginAttempts(5);
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    Transaction trans = null;
		    try {
		    	trans = session.beginTransaction();
		    	session.save(config);
		        trans.commit();
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}
		
	}

	public void insertConfiguration(Configuration configuration){
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    Transaction trans = null;
		    try {
		    	trans = session.beginTransaction();
		    	session.save(configuration);
		        trans.commit();
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}
	}

	public void deleteConnectionProperties(int id){
    	Session session = null;
		try {
			session = getSessionFactory().openSession();
			session.getNamedQuery("Configuration.deleteById").setInteger("id", id);
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
	}

	public String getServerCertificate() {
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Configuration.findAll");
			if (query.list().size() != 0) {
				Configuration c = (Configuration) query.list().get(0);
				return c.getServerCertificate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;

	}

	public String getClientCertificate() {
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Configuration.findAll");
			if (query.list().size() != 0) {
				Configuration c = (Configuration) query.list().get(0);
				return c.getClientCertificate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;
	}

	public Configuration getConfigurations(){
		
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Configuration.findAll");
			if (query.list().size() > 0) {
				Configuration c = (Configuration) query.list().get(0);
				return c;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;

	}

	public List<Configuration> getConfiguration(){
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Configuration.findAll");
			if (query.list().size() != 0) {
				return query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;
	}


	public boolean isSilentModeActive() {
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Configuration.findAll");
			if (query.list().size() != 0) {
				Configuration c = (Configuration) query.list().get(0);
				return c.getSilentMode()==1?true:false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return false;

	}


	// Decision Maker related queries
	/**
	 * Adds decision table in the DB
	 * @param decisionTable
	 */

	public int addDecisionTable(Decisiontable decisionTable){
		int id = -1;
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    Transaction trans = null;
		    try {
		    	trans = session.beginTransaction();
		    	id = (Integer) session.save(decisionTable);
		        trans.commit();
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}
		return id;
	}

	/**
	 * Retrieve all decision tables
	 * @return list of Decision tables
	 */

	public List<Decisiontable> getAllDecisionTables(){

		List<Decisiontable> decisionTableList = new ArrayList<Decisiontable>();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Decisiontable.findAll");
			if (query.list().size() != 0) {
				decisionTableList = query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return decisionTableList;
	}


	/**
	 * Retrieve decision table from action_id
	 * @param action_id
	 * @return DecisionTable
	 */

	public Decisiontable getDecisionTableFromActionID(String action_id) {
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Decisiontable.findByActionId").setString("actionId", action_id);
			if (query.list().size() != 0) {
				return (Decisiontable) query.list().get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;	

	}

	/**
	 * Retrieve decision table from action_id and resource_id
	 * @param action_id
	 * @param resource_id
	 * @return DecisionTable
	 */

	public Decisiontable getDecisionTableFromActionAndResource(String action_id, String resource_id/*Action action, Resource resource*/) {
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Decisiontable.findByActionAndResource").setString("actionId", action_id).setString("resourceId",resource_id);
			if (query.list().size() != 0) {
				return (Decisiontable) query.list().get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;	
	}

	public Decisiontable getDecisionTableFromResourceId(String resource_id, String action_id) {

		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Decisiontable.findByResource").setString("resourceId", resource_id);
			if (query.list().size() != 0) {
				return (Decisiontable) query.list().get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;	
	}


	public Decisiontable getDecisionTableFromID(String decisiontable_id) {

		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Decisiontable.findByDecisiontableId").setString("decisionId", decisiontable_id);
			if (query.list().size() != 0) {
				return (Decisiontable) query.list().get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;	
	}

	/**
	 * Retrieve decision table from action_id and subject_id
	 * @param action_id
	 * @param subject_id
	 * @return DecisionTable
	 */

	public Decisiontable getDecisionTableFromActionAndSubject(String action_id, String subject_id/*Action action, Subject subject*/) {
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Decisiontable.findByActionAndSubject").setString("actionId", action_id).setString("subjectId", subject_id);
			if (query.list().size() != 0) {
				return (Decisiontable) query.list().get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;	
	}

	/**
	 * Retrieve decision table from action_id, resource_id and subject_id
	 * @param action_id
	 * @param resource_id
	 * @param subject_id
	 * @return DecisionTable
	 */

	public Decisiontable getDecisionTableFromActionAndRecourceAndSubject(String action_id, String resource_id, String subject_id/*Action action, Resource resource, Subject subject*/) {
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Decisiontable.findByActionResourceSubject").setString("actionId", action_id).setString("resourceId", resource_id).setString("subjectId", subject_id);
			if (query.list().size() != 0) {
				return (Decisiontable) query.list().get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;	
	}


	public void addAction(Action action){
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    Transaction trans = null;
		    try {
		    	trans = session.beginTransaction();
		    	session.save(action);
		        trans.commit();
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}
	}


	public int addOfflineAction(Action action){
		int id = -1;
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    Transaction trans = null;
		    try {
		    	trans = session.beginTransaction();
		    	id = (Integer) session.save(action);
		        trans.commit();
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}
		return id;
	}
	
	public void addActionProperty(ActionProperty actionProperty) {
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    Transaction trans = null;
		    try {
		    	trans = session.beginTransaction();
		    	session.save(actionProperty);
		        trans.commit();
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}
	}
	
	public int addOfflineActionProperty(ActionProperty actionProperty) {
		int id = -1;
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    Transaction trans = null;
		    try {
		    	trans = session.beginTransaction();
		    	id = (Integer) session.save(actionProperty);
		        trans.commit();
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}
		return id;
	}

	public List<ActionProperty> getActionPropertyList() {
		List<ActionProperty> actionPropertyList = new ArrayList<ActionProperty>();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("ActionProperty.findAll");
			if (query.list().size() != 0) {
				actionPropertyList = query.list();
				return actionPropertyList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}

		return actionPropertyList;
	}

	public List<ActionProperty> getOfflineActionPropertiesOfAction(int actionId) {
		List<ActionProperty> actionPropertyList = new ArrayList<ActionProperty>();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("ActionProperty.findByActionId").setInteger("actionId", actionId);
			if (query.list().size() != 0) {
				actionPropertyList = query.list();
				return actionPropertyList;	
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}

		return actionPropertyList;

	}

	public List<Action> getOfflineActionList() {
		List<Action> actionList = new ArrayList<Action>();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Action.findAll");
			if (query.list().size() != 0) {
				actionList = query.list();
				return actionList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}

		return actionList;
	}

	private Action getActionFromDescription(String description) {
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Action.findByDescription").setString("description", description);
			if (query.list().size() != 0) {
				return (Action) query.list().get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;

	}


	/**
	 * Inserts into riskTreatment table in the DB
	 * @param riskTreatment
	 */

	public void addRiskTreatment(Risktreatment riskTreatment){
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    Transaction trans = null;
		    try {
		    	trans = session.beginTransaction();
		    	session.save(riskTreatment);
		        trans.commit();
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}

	}


	private Risktreatment getRiskTreatmentFromDescription(String textualdescription) {
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Risktreatment.findByDescription").setString("textualdescription", textualdescription);
			if (query.list().size() != 0) {
				return (Risktreatment) query.list().get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}

		return null;

	}


	/**
	 * Inserts into resourceType table in the DB
	 * @param resourceType
	 */

	public void addResourceType(Resourcetype resourceType){
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    Transaction trans = null;
		    try {
		    	trans = session.beginTransaction();
		    	session.save(resourceType);
		        trans.commit();
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}
	}

	/**
	 * Inserts into resource table in the DB
	 * @param resource
	 */

	public void addResource(Resource resource){
		int size = controlDB("before");
		Resource resourceInDb = getResourceFromPathAndCondition(resource.getPath(), resource.getCondition());
		if (resourceInDb.getId()==0){
			
			try {
				logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
			    Session session = getSessionFactory().openSession();
			    Transaction trans = null;
			    try {
			    	trans = session.beginTransaction();
			    	session.save(resource);
			        trans.commit();
			    } catch (Exception e) {
			        if (trans!=null) trans.rollback();
			        e.printStackTrace(); 
			    } finally {
			    	if (session!=null) session.close();
			    }
			    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
			} catch (RuntimeException re) {
				logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
				throw re;
			}
			controlDB("after " );
		}else{
			logger.log(Level.INFO,"Resource found, returning the existing one..."+resourceInDb.getId());
			controlDB("after "+ resourceInDb.getId());
		}


	}

	public int controlDB(String control){
		List<Resource> allConditionResources = getAllResources();

		logger.log(Level.INFO,control +" Found..."+allConditionResources.size());

		for (Iterator iterator = allConditionResources.iterator(); iterator
				.hasNext();) {
			Resource resource = (Resource) iterator.next();
			if (resource.getCondition()!=null){
				logger.log(Level.INFO,"Condition:"+resource.getCondition());
			}
			if (resource.getPath()!=null){
				logger.log(Level.INFO, "Path:"+resource.getPath());
			}
			logger.log(Level.INFO,"	Id:"+resource.getId());

		}
		return allConditionResources.size();
	}


	/**
	 * Inserts into riskCommunication table in the DB
	 * @param riskCommunication
	 */

	public void addRiskCommunication(Riskcommunication riskCommunication){
		Riskcommunication riskCommunicationInDb = getRiskCommunicationFromTreatmentId(riskCommunication.getRisktreatmentId());
		if (riskCommunicationInDb.getId()==0){
			logger.log(Level.INFO, "RiskCommunication not found, inserting a new one...");
			try {
				logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
			    Session session = getSessionFactory().openSession();
			    Transaction trans = null;
			    try {
			    	trans = session.beginTransaction();
			    	session.save(riskCommunication);
			        trans.commit();
			    } catch (Exception e) {
			        if (trans!=null) trans.rollback();
			        e.printStackTrace(); 
			    } finally {
			    	if (session!=null) session.close();
			    }
			    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
			} catch (RuntimeException re) {
				logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
				throw re;
			}
		}else{
			logger.log(Level.INFO, "RiskCommunication found, returning the existing one...");
		}


	}

	private Riskcommunication getRiskCommunicationFromTreatmentId(int risktreatment_id) {
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Riskcommunication.findByRiskTreatmentId").setInteger("risktreatmentId",risktreatment_id);
			if (query.list().size() != 0) {
				return (Riskcommunication) query.list().get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}

		return null;
	}


	/**
	 * Inserts into role table in the DB
	 * @param role
	 */

	public void addRole(Role role){
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    Transaction trans = null;
		    try {
		    	trans = session.beginTransaction();
		    	session.save(role);
		        trans.commit();
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}
	}

	/**
	 * Inserts into subject table in the DB
	 * @param role
	 */

	public void addSubject(Subject subject){
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    Transaction trans = null;
		    try {
		    	trans = session.beginTransaction();
		    	session.save(subject);
		        trans.commit();
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}
	}


	// Policy related queries

	public void addDevicePolicy(Policy policy){
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    Transaction trans = null;
		    try {
		    	trans = session.beginTransaction();
		    	session.save(policy);
		        trans.commit();
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}
	}

	public void updateDevicePolicy(Policy policy){
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    Transaction trans = null;
		    try {
		    	trans = session.beginTransaction();
		    	session.save(policy);
		        trans.commit();
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}
	}

	public int getNoOfDevicePoliciesStored(){
		String selectQuery = "TBD";
		return 0;
	}


	public Policy getStoredDevicePolicy(int index){
		String selectQuery = "TBD";
		return new Policy();
	}

	public void deleteDevicePolicy(Policy policy){
		String deleteQuery = "TBD";
	}



	// Context Event related queries

	public int addContextEvent(ContextEvent event) {
		int id = -1;
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    Transaction trans = null;
		    try {
		    	trans = session.beginTransaction();
		    	id = (Integer) session.save(event);
		        trans.commit();
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}
		return id;
	}

	public int getNoOfContextEventsStored() {
			Session session = null;
			Query query = null;
			try {
				session = getSessionFactory().openSession();
				query = session.getNamedQuery("ContextEvent.findAll");
				if (query.list().size() != 0) {
					return query.list().size();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (session!=null) session.close();
			}
			return 0;
	}
	public List<Property> getPropertiesOfContextEvent(int contextevent_id) {
		List<Property> propertyList = new ArrayList<Property>();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Property.findByContextEvent").setInteger("contexteventId",contextevent_id);
			if (query.list().size() != 0) {
				propertyList = query.list();
				return propertyList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return propertyList;
	}

	public ContextEvent getStoredContextEvent(String id) {
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("ContextEvent.findById").setString("id", id);
			if (query.list().size() != 0) {
				return (ContextEvent) query.list().get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;

	}

	public List<ContextEvent> getStoredContextEventByActionId(int id) {
		List<ContextEvent> contextEvents = new ArrayList<ContextEvent>();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("ContextEvent.findByActionId").setInteger("actionId", id);
			if (query.list().size() != 0) {
				contextEvents = query.list();
				return contextEvents;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return contextEvents;
	}

	public void deleteStoredContextEvent(String id	){
    	Session session = null;
		try {
			session = getSessionFactory().openSession();
			session.getNamedQuery("ContextEvent.deleteById").setString("id", id);
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
	}

	public void addProperty(Property property) {
		try {
			logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
		    Session session = getSessionFactory().openSession();
		    Transaction trans = null;
		    try {
		    	trans = session.beginTransaction();
		    	session.save(property);
		        trans.commit();
		    } catch (Exception e) {
		        if (trans!=null) trans.rollback();
		        e.printStackTrace(); 
		    } finally {
		    	if (session!=null) session.close();
		    }
		    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
		} catch (RuntimeException re) {
			logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
			throw re;
		}
	}

	public List<ContextEvent> getAllStoredContextEvents() {
		List<ContextEvent> contextEventsList = new ArrayList<ContextEvent>();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("ContextEvent.findAll");
			if (query.list().size() != 0) {
				contextEventsList = query.list();
				return contextEventsList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return contextEventsList;
	}


	public List<Property> getAllProperties() {
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Property.findAll");
			if (query.list().size() != 0) {
				return query.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return null;
	}

    public void resetStoredContextEventTables(){
//        if(sqLiteDatabase == null) {
//            openDB();
//        }
//
//        sqLiteDatabase.delete(TABLE_OFFLINE_ACTION, null, null);
//        sqLiteDatabase.delete(TABLE_OFFLINE_ACTION_PROPERTY, null, null);
//        sqLiteDatabase.delete(TABLE_CONTEXT_EVENT, null, null);
//        sqLiteDatabase.delete(TABLE_PROPERTY, null, null);
    }

	/**
	 * Retrieve decision from id
	 * @param decision_id
	 * @return Decision
	 */

	public Decision getDecisionFromID(String decision_id) {
		Decision decision = new Decision();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Decision.findById").setString("id", decision_id);
			if (query.list().size() != 0) {
				decision = (Decision) query.list().get(0);
				return decision;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return decision;
	}

	/**
	 * Retrieve decision from id
	 * @param decision_id
	 * @return Decision
	 */

	public List<Decision> getAllDecisionsWithCondition() {
		List<Decision> decisionList = new ArrayList<Decision>();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Decision.findAllWithCondition");
			if (query.list().size() != 0) {
				decisionList = query.list();
				return decisionList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return decisionList;
	}



	/**
	 * Retrieve risk_communication from id
	 * @param risk_communication_id
	 * @return RiskCommunication
	 */

	public Riskcommunication getRiskCommunicationFromID(String risk_communication_id) {
		Riskcommunication comm = new Riskcommunication();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Riskcommunication.findById").setString("id", risk_communication_id);
			if (query.list().size() != 0) {
				comm =  (Riskcommunication) query.list().get(0);
				return comm;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return comm;
	}

	/**
	 * Retrieve risk_treatment from id
	 * @param risk_treatment_id
	 * @return RiskTreatment
	 */

	public Risktreatment getRiskTreatmentFromID(String risk_treatment_id) {
		Risktreatment treatment = new Risktreatment();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Risktreatment.findById").setString("id", risk_treatment_id);
			if (query.list().size() != 0) {
				treatment =  (Risktreatment) query.list().get(0);
				return treatment;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return treatment;
	}


	// Server and Client Certificates related query

	public boolean setServerCert(ServerCertificate serverCertificate ){
		return false;
	}

	// For future
	public ServerCertificate getServerCert(){
		return new ServerCertificate();
	}

	// For future
	public boolean setUserDeviceCert(UserDeviceCertificate userDeviceCertificate) {
		return false;
	}

	public UserDeviceCertificate getUserDeviceCert(){
		return new UserDeviceCertificate();
	}


	public void addDecision(Decision decision) {
		Decision decisionInDb = getDecisionFromNameAndCondition(decision.getName(), decision.getCondition());
		if (decisionInDb.getId()==0){
			logger.log(Level.INFO,"Decision not found, inserting a new one...");
			try {
				logger.log(Level.INFO, MUSES_TAG + ":persisting object instance");
			    Session session = getSessionFactory().openSession();
			    Transaction trans = null;
			    try {
			    	trans = session.beginTransaction();
			    	session.save(decision);
			        trans.commit();
			    } catch (Exception e) {
			        if (trans!=null) trans.rollback();
			        e.printStackTrace(); 
			    } finally {
			    	if (session!=null) session.close();
			    }
			    logger.log(Level.INFO, MUSES_TAG + ":persist successful");
			} catch (RuntimeException re) {
				logger.log(Level.ERROR, MUSES_TAG + ":persist failed"+ re);
				throw re;
			}

		}else{
			logger.log(Level.INFO, "Decision found, returning the existing one..."+decisionInDb.getId());
		}

	}

	private Decision getDecisionFromNameAndCondition(String name,
													 String condition) {
		Decision decision = new Decision();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Decision.findByNameAndCondition").setString("name",name).setString("condition", condition);
			if (query.list().size() != 0) {
				decision =  (Decision) query.list().get(0);
				return decision;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return decision;
	}


	public Resource getResourceFromPath(String path) {
		Resource resource = new Resource();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Resource.findByPath").setString("path", path);
			if (query.list().size() != 0) {
				resource =  (Resource) query.list().get(0);
				return resource;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return resource;

	}



	public Action getActionFromType(String type) {
		Action action = new Action();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Action.findByType").setString("actionType",type);
			if (query.list().size() != 0) {
				action =  (Action) query.list().get(0);
				return action;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return action;

	}

	public List<SensorConfiguration> getAllSensorConfigItemsBySensorType(String type) {
		List<SensorConfiguration> configurationList = new ArrayList<SensorConfiguration>();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("SensorConfiguration.findByType").setString("sensorType", type);
			if (query.list().size() != 0) {
				configurationList = query.list();
				return configurationList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return configurationList;
	}

	public List<String> getAllEnabledSensorTypes() {
		List<String> enabledSensors = new ArrayList<String>();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("SensorConfiguration.findByKey").setString("key","enabled");
			if (query.list().size() != 0) {
				List<SensorConfiguration> sensorList = (List<SensorConfiguration>) query.list();
				for (SensorConfiguration s: sensorList){
					enabledSensors.add(s.getSensorType());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return enabledSensors;
	}

	public List<ResourceProperty> getPropertiesFromResourceId(String resource_id) {
		List<ResourceProperty> properties = new ArrayList<ResourceProperty>();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("ResourceProperty.findByResourceId").setString("resourceId", resource_id);
			if (query.list().size() != 0) {
				properties =  query.list();
				return properties;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return properties;
	}

	public Resource getResourceFromCondition(String condition) {
		Resource resource = new Resource();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Resource.findByCondition").setString("condition", condition);
			if (query.list().size() != 0) {
				resource =  (Resource) query.list().get(0);
				return resource;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return resource;
	}

	public List<Resource> getAllResourcesWithCondition() {
		List<Resource> resourceList = new ArrayList<Resource>();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Resource.findAll");
			if (query.list().size() != 0) {
				resourceList = query.list();
				return resourceList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return resourceList;

	}

	public List<Resource> getAllResources() {
		List<Resource> resourceList = new ArrayList<Resource>();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Resource.findAll");
			if (query.list().size() != 0) {
				resourceList = query.list();
				return resourceList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return resourceList;
	}

	public Resource getResourceFromPathAndCondition(String path, String condition) {
		Resource resource = new Resource();
		Session session = null;
		Query query = null;
		try {
			session = getSessionFactory().openSession();
			query = session.getNamedQuery("Resource.findByPathAndCondition").setString("path", path).setString("condition", condition);
			if (query.list().size() != 0) {	
				resource = (Resource) query.list().get(0);
				return resource;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return resource;
	}
	
	
}
	
	