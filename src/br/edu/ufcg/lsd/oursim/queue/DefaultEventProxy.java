package br.edu.ufcg.lsd.oursim.queue;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import br.edu.ufcg.lsd.oursim.events.EventSpec;
import br.edu.ufcg.lsd.oursim.util.LineParser;

public class DefaultEventProxy implements EventProxy {

	private Scanner scanner;
	private EventSpec nextEvent;
	
	/**
	 * @param inputStream
	 */
	public DefaultEventProxy(InputStream inputStream) {
		this.scanner = new Scanner(inputStream);
	}

	@Override
	public List<EventSpec> nextEventPage(int pageSize) {
		
		List<EventSpec> eventPage = new LinkedList<EventSpec>();
		
		if (!scanner.hasNextLine()) {
			return eventPage;
		}
		
		if (nextEvent == null) {
			nextEvent = parseEvent(scanner.nextLine());
		}
		
		for (int i = 0; i < pageSize; i++) {
			eventPage.add(nextEvent);
			if (scanner.hasNextLine()) {
				nextEvent = parseEvent(scanner.nextLine());
			} else {
				nextEvent = null;
				break;
			}
		}
		
		return eventPage;
	}

	@Override
	public Long nextEventTime() {
		if (nextEvent == null) {
			return null;
		}
		return nextEvent.getTime();
	}

	private static EventSpec parseEvent(String line) {
		String[] split = line.split("\\s+");
		
		if (line.length() < 2) {
			throw new IllegalArgumentException(
					"Stream de eventos mal formatado.");
		}
		
		LineParser lineParser = new LineParser(line);
		
		String type = lineParser.next();
		Long time = Long.parseLong(lineParser.next());
		
		String data = split.length >= 3 ? lineParser.restOfLine() : null;
		
		return data == null ? new EventSpec(type, time)
				: new EventSpec(type, time, data);
	}
}
