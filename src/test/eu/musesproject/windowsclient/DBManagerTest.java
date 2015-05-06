package eu.musesproject.windowsclient;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.musesproject.windowsclient.model.DBManager;
import eu.musesproject.windowsclient.model.SensorConfiguration;


public class DBManagerTest {
	static DBManager dbmanager = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		dbmanager = new DBManager();
		dbmanager.openDB();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dbmanager.closeDB();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testGetAllSensorConfiguration() throws Exception {
		List<SensorConfiguration> list = dbmanager.getAllSensorConfiguration();
		if (list.size()>0){
			Iterator<SensorConfiguration> i = list.iterator();
			while(i.hasNext()){
				SensorConfiguration conf = i.next();
				assertNotNull(conf);
			}
		}else{
			fail("There is not any sensor config in the database");
		}
	}
	
	@Test
	public void testGetRequiredAppList() throws Exception {
		dbmanager.inserRequiredAppList();
//		if (dbmanager.getRequiredAppList().size() > 0){
//			assertTrue(true);
//		}else{
//			fail("Something went wrong inserting required apps..");
//		} 
			
	}
	
	@Test
	public void testIsUserAuthenticated() throws Exception {
//		dbmanager.insertCredentials("testdeviceid", "test", "test");
//		if (dbmanager.isUserAuthenticated("testdeviceid", "test", "test")) {
//			dbmanager.deleteUserCredentials("test");
//			assertTrue(true);
//		}else{
//			fail("user not authenticated..");
//		}
	}
	
	@Test
	public void testgetDeviceId() throws Exception {
//		String deviceId = dbmanager.getDevId();
//		if (deviceId != null){
//			assertNotNull(deviceId);
//		}else {
//			fail("Could not retreive device id..");
//		}
	}
	
	
	
	@Test
	public void testGetDecisionTableFromActionID() {
		dbmanager.getDecisionTableFromActionID("1");
	}
	
	@Test
	public void testGetDecisionTableFromActionAndResource() throws Exception {
		dbmanager.getDecisionTableFromActionAndResource("1", "1");
	}

}