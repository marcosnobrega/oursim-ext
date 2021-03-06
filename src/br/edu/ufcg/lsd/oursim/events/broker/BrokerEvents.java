package br.edu.ufcg.lsd.oursim.events.broker;

import java.util.HashMap;
import java.util.Map;

import br.edu.ufcg.lsd.oursim.events.Event;

public class BrokerEvents {

	
	/**
	 * Primary Events
	 */
	public static final String ADD_JOB = "ADD_JOB";
	public static final String BROKER_DOWN = "BROKER_DOWN";
	public static final String BROKER_UP = "BROKER_UP";
	
	
	/**
	 * Secondary Events
	 */
	public static final String BROKER_LOGGED = "BROKER_LOGGED";
	public static final String HERE_IS_EXECUTION_RESULT = "HERE_IS_EXECUTION_RESULT";
	public static final String HERE_IS_WORKER = "HERE_IS_WORKER";
	public static final String PEER_AVAILABLE = "PEER_AVAILABLE";
	public static final String PEER_FAILED = "PEER_FAILED";
	public static final String SCHEDULE = "SCHEDULE";
	public static final String WORKER_FAILED = "WORKER_FAILED";
	public static final String WORKER_RECOVERY = "WORKER_RECOVERY";
	
	public static Map<String, Class<? extends Event>> createEvents() {
		Map<String, Class<? extends Event>> events = new HashMap<String, Class<? extends Event>>();
		events.put(ADD_JOB, AddJobEvent.class);
		events.put(BROKER_DOWN, BrokerDownEvent.class);
		events.put(BROKER_UP, BrokerUpEvent.class);
		events.put(BROKER_LOGGED, BrokerLoggedEvent.class);
		events.put(HERE_IS_EXECUTION_RESULT, HereIsExecutionResultEvent.class);
		events.put(HERE_IS_WORKER, HereIsWorkerEvent.class);
		events.put(PEER_AVAILABLE, PeerAvailableEvent.class);
		events.put(PEER_FAILED, PeerFailedEvent.class);
		events.put(SCHEDULE, ScheduleEvent.class);
		events.put(WORKER_FAILED, WorkerFailedEvent.class);
		events.put(WORKER_RECOVERY, WorkerRecoveryEvent.class);
		
		return events;
	}
	
}
