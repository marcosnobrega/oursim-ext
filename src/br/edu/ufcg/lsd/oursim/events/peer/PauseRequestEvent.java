package br.edu.ufcg.lsd.oursim.events.peer;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;

public class PauseRequestEvent extends AbstractEvent {

	private final String peerId;
	private final RequestSpec requestSpec;

	public PauseRequestEvent(Long time, RequestSpec requestSpec, String peerId) {
		super(time, Event.DEF_PRIORITY, null);
		this.requestSpec = requestSpec;
		this.peerId = peerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Peer peer = ourSim.getGrid().getObject(peerId);
		peer.getRequest(requestSpec.getId()).setPaused(true);
	}

}
