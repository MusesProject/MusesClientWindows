package eu.musesproject.windowsclient.connectionmanager;

/*
 * #%L
 * MUSES Client
 * %%
 * Copyright (C) 2013 - 2014 Sweden Connectivity
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

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;


/**
 * Class Alarm receiver listens for systems notifications like screen on/off , wakelock
 * 
 * @author Yasir Ali
 * @version Jan 27, 2014
 */

public class AlarmReceiver implements Job {
	
	private static final String TAG = "AlarmReceiver";
	public static int POLL_INTERVAL = 60000; // Default value
	public static int SLEEP_POLL_INTERVAL = 60000; // Default value
	private static int exponentialCounter = 4;
	private static int DEFAULT_POLL_INTERVAL = 60000;
	private static int DEFAULT_SLEEP_POLL_INTERVAL = 60000;
	private static int CURRENT_POLL_INTERVAL;
	private static boolean POLL_INTERVAL_UPDATED = false;
	private static boolean SLEEP_MODE_ACTIVE;
	
	private static ConnectionManager CONNECTIONMANAGER= null;
	private static JobExecutionContext jobExecutionContext;
	
	public static void setManager(ConnectionManager connectionManager) {
		CONNECTIONMANAGER = connectionManager;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		jobExecutionContext = context;
		if (CONNECTIONMANAGER != null) 
		{
			CONNECTIONMANAGER.periodicPoll();
		} else {
			System.out.println(TAG + " ConnectionManager is null!!");
		}
		
		/* Check if timeouts have changed, if so update */
		applyTimeoutChanges();
		System.out.println(TAG + " Alarm..");
		
	}

	/**
	 * Increase poll time
	 * @return void 
	 */
	
	public static void increasePollTime(){
		if (exponentialCounter > 0) {
			POLL_INTERVAL = POLL_INTERVAL*2;
			SLEEP_POLL_INTERVAL = SLEEP_POLL_INTERVAL*2;
			exponentialCounter--;
			POLL_INTERVAL_UPDATED = true;
		} 
	}
	
	/**
	 * Resets poll time
	 * @return void
	 */
	public static void resetExponentialPollTime(){
		if (POLL_INTERVAL != DEFAULT_POLL_INTERVAL){
			POLL_INTERVAL_UPDATED = true;
		}
		POLL_INTERVAL = DEFAULT_POLL_INTERVAL;
		SLEEP_POLL_INTERVAL = DEFAULT_SLEEP_POLL_INTERVAL;
		exponentialCounter = 3;
		
	}
	
	/**
	 * If poll interval has been changed, setAlarm
	 * @param context
	 * @return void
	 */
	 private void applyTimeoutChanges(){
		 if (POLL_INTERVAL_UPDATED){
			 POLL_INTERVAL_UPDATED = false;
			 //cancelAlarm(); // FIXME not working right, not on priority
			 setAlarm();
		 }
	 }
	 
	 
	 
	
	
	/**
	 * Set alarm to current poll interval according to the phone mode sleep/active
	 * @param context
	 * @return void
	 */
	
    public void setAlarm() {
    	if (SLEEP_MODE_ACTIVE) {
    		CURRENT_POLL_INTERVAL = SLEEP_POLL_INTERVAL;
    	}else {
    		CURRENT_POLL_INTERVAL = POLL_INTERVAL;
    	}
    	
    	int interval = Integer.parseInt(HttpConnectionsHelper.getInStringSeconds(Integer.toString(AlarmReceiver.POLL_INTERVAL)));
    	
		try {
			Trigger updateTrigger = TriggerBuilder
					.newTrigger()
					.withSchedule(
							SimpleScheduleBuilder.simpleSchedule()
									.withIntervalInSeconds(interval).repeatForever())
					.build();
		    Trigger oldTrigger = jobExecutionContext.getTrigger();
		    Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		    scheduler.rescheduleJob(oldTrigger.getKey(), updateTrigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
    }

    /**
     * Cancels alarm
     * @param context
     * @return void
     */
    
    public void cancelAlarm() {
    	if (jobExecutionContext != null){
    		try {
    			jobExecutionContext.getScheduler().shutdown();
    		} catch (SchedulerException e) {
    			e.printStackTrace();
    		}
    	}
    }

	public void setPollInterval(int pollInterval, int sleepPollInterval) {
		POLL_INTERVAL = pollInterval;
		SLEEP_POLL_INTERVAL = sleepPollInterval;
		POLL_INTERVAL_UPDATED = true;
	}

	

	public void setDefaultPollInterval(int pollInterval,
			int sleepPollInterval) {
		DEFAULT_POLL_INTERVAL = pollInterval;
		DEFAULT_SLEEP_POLL_INTERVAL = sleepPollInterval;
		
	}

	public static int getCurrentPollInterval() {
		
		return CURRENT_POLL_INTERVAL;
	}

	public static void setPollMode(boolean sleepModeActive) {
		
		SLEEP_MODE_ACTIVE = sleepModeActive;
		POLL_INTERVAL_UPDATED = true;
	}
    
   
}
