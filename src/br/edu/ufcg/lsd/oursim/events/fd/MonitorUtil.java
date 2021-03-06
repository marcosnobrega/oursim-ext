package br.edu.ufcg.lsd.oursim.events.fd;

import java.util.Collection;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.ActiveEntity;
import br.edu.ufcg.lsd.oursim.entities.Monitor;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class MonitorUtil {

	public static void registerMonitored(OurSim ourSim, Long time, 
			String interested, String monitored, Event callbackAliveEvent, 
			Event callbackDownEvent) {
		registerMonitored(ourSim, time, interested, monitored, 
				callbackAliveEvent, callbackDownEvent, false);
	}
	
	public static Monitor registerMonitored(OurSim ourSim, Long time, 
			String interested, String monitored, Event callbackAliveEvent, 
			Event callbackDownEvent, boolean isUp) {
		
		ActiveEntity interestedObj = ourSim.getGrid().getObject(interested);
		ActiveEntity monitoredObj = ourSim.getGrid().getObject(monitored);
		
		Monitor monitor = new Monitor(
				monitoredObj, callbackAliveEvent, callbackDownEvent);
		monitor.setUp(isUp);
		interestedObj.addMonitor(monitor, time);
		
		if (ourSim.getBooleanProperty(
				Configuration.PROP_USE_FAILURE_DETECTOR)) {
			sendIsItAlive(ourSim, time, interested, monitored);
		}
		
		return monitor;
	}

	public static void checkLiveness(OurSim ourSim, Long time,
			String interested, String monitored) {

		ActiveEntity interestedObj = ourSim.getGrid().getObject(interested);
		Monitor monitor = interestedObj.getMonitor(monitored);
		
		if (interestedObj.isFailed(monitored, time) && monitor.isUp()) {
			monitor.setUp(false);
			Event callbackDownEvent = monitor.getCallbackDownEvent();
			if (callbackDownEvent != null) {
				callbackDownEvent.setTime(time);
				ourSim.addEvent(callbackDownEvent);
			}
		} else {
			Long livenessCheck = ourSim.getLongProperty(
					Configuration.PROP_LIVENESS_CHECK_INTERVAL);
			
			ourSim.addEvent(ourSim.createEvent(FailureDetectionEvents.LIVENESS_CHECK, 
					time + livenessCheck, interested, monitored));
		}
		
	}

	public static void sendIsItAlive(OurSim ourSim, Long time,
			String interested, String monitored) {
		
		ActiveEntity interestedObj = ourSim.getGrid().getObject(interested);
		Monitor monitor = interestedObj.getMonitor(monitored);
		if (monitor == null) {
			return;
		}
		
		Event isItAliveReceivedEvent = ourSim.createEvent(FailureDetectionEvents.IS_IT_ALIVE_RECEIVED, 
				time, interested, monitored, monitor.isCreatingConnection());
		interestedObj.isItAliveSent(monitored, time);
		ourSim.addNetworkEvent(isItAliveReceivedEvent);

		Event isItAliveSentEvent = ourSim.createEvent(FailureDetectionEvents.IS_IT_ALIVE_SENT, 
				time + interestedObj.getTimeToNextPing(monitored, time), 
				interested, monitored);
		
		ourSim.addEvent(isItAliveSentEvent);
	}

	public static void objectIsUp(OurSim ourSim, ActiveEntity object, Long time) {
		if (ourSim.getBooleanProperty(
				Configuration.PROP_USE_FAILURE_DETECTOR)) {
			return;
		}
		
		Collection<ActiveEntity> allObjects = ourSim.getGrid().getAllObjects();
		for (ActiveEntity activeEntity : allObjects) {
			Monitor monitor = activeEntity.getMonitor(object.getId());
			if (monitor == null) {
				continue;
			}
			monitor.setUp(true);
			
			Event callbackAliveEvent = monitor.getCallbackAliveEvent();
			if (callbackAliveEvent != null) {
				callbackAliveEvent.setTime(time);
				ourSim.addEvent(callbackAliveEvent);
			}
		}
	}
	
	public static void objectIsDown(OurSim ourSim, ActiveEntity object, Long time) {
		if (ourSim.getBooleanProperty(
				Configuration.PROP_USE_FAILURE_DETECTOR)) {
			return;
		}
		
		Collection<ActiveEntity> allObjects = ourSim.getGrid().getAllObjects();
		for (ActiveEntity activeEntity : allObjects) {
			Monitor monitor = activeEntity.getMonitor(object.getId());
			if (monitor == null) {
				continue;
			}
			monitor.setUp(false);
			
			Event callbackDownEvent = monitor.getCallbackDownEvent();
			if (callbackDownEvent != null) {
				callbackDownEvent.setTime(time);
				ourSim.addEvent(callbackDownEvent);
			}
		}
	}
}
