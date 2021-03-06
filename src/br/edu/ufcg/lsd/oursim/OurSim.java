package br.edu.ufcg.lsd.oursim;

import java.util.Properties;
import java.util.Random;

import br.edu.ufcg.lsd.oursim.entities.grid.Grid;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.factories.EventFactory;
import br.edu.ufcg.lsd.oursim.factories.GridFactory;
import br.edu.ufcg.lsd.oursim.network.Network;
import br.edu.ufcg.lsd.oursim.queue.EventProxy;
import br.edu.ufcg.lsd.oursim.queue.EventQueue;
import br.edu.ufcg.lsd.oursim.trace.TraceCollector;

/**
 *
 */
public class OurSim {

	private static final int SEED = 123455677;
	
	private final EventQueue queue;
	private final Grid grid;
	private final Network network;
	private final Properties properties;
	private final TraceCollector traceCollector;
	private final Random random = new Random(SEED);
	
	private boolean running = true;
	private final EventFactory eventFactory = new EventFactory();
	
	public OurSim(EventProxy eventProxy, GridFactory gridFactory, 
			Properties properties, Network network, TraceCollector traceCollector) {
		this.traceCollector = traceCollector;
		this.properties = properties;
		this.network = network;
		this.queue = new EventQueue(eventProxy, eventFactory);
		this.grid = gridFactory.createGrid();
	}
	
	public Random getRandom() {
		return random;
	}
	
	public Grid getGrid() {
		return grid;
	}

	public void addEvent(Event event) {
		queue.add(event);
	}
	
	public void addNetworkEvent(Event event) {
		event.setTime(event.getTime() + network.generateDelay());
		queue.add(event);
	}
	
	public Event createEvent(String type, long time, Object... params) {
		return eventFactory.createEvent(type, time, params);
	}
	
	public void run() {
		while (!queue.isEmpty() && running) {
			Event ev = queue.poll();
			ev.process(this);
		}
	}
	
	public Long getLongProperty(String key) {
		String property = properties.getProperty(key);
		return property == null ? null : Long.valueOf(property);
	}
	
	public Integer getIntProperty(String key) {
		String property = properties.getProperty(key);
		return property == null ? null : Integer.valueOf(property);
	}
	
	public Boolean getBooleanProperty(String key) {
		String property = properties.getProperty(key);
		return property == null ? null : Boolean.valueOf(property);
	}
	
	public String getStringProperty(String key) {
		return properties.getProperty(key);
	}

	public TraceCollector getTraceCollector() {
		return traceCollector;
	}
	
	public void halt() {
		this.running = false;
	}
}
