package br.edu.ufcg.lsd.oursim.events.broker;

import java.util.Random;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.grid.Broker;
import br.edu.ufcg.lsd.oursim.entities.job.Job;
import br.edu.ufcg.lsd.oursim.entities.request.BrokerRequest;
import br.edu.ufcg.lsd.oursim.entities.request.RequestSpec;
import br.edu.ufcg.lsd.oursim.events.AbstractEvent;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.RequestWorkersEvent;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class BrokerLoggedEvent extends AbstractEvent {

	private String brokerId;

	public BrokerLoggedEvent(Long time, String brokerId) {
		super(time, Event.DEF_PRIORITY, null);
		this.brokerId = brokerId;
	}

	@Override
	public void process(OurSim ourSim) {
		Broker broker = ourSim.getGrid().getObject(brokerId);
		for (Job job : broker.getJobs()) {
			if (!SchedulerHelper.isJobSatisfied(job, ourSim)) {
				RequestSpec requestSpec = new RequestSpec();
				requestSpec.setBrokerId(brokerId);
				requestSpec.setId(Math.abs(new Random().nextLong()));
				requestSpec.setRequiredWorkers(job.getTasks().size()
						* ourSim.getIntProperty(Configuration.PROP_BROKER_MAX_REPLICAS));
				
				BrokerRequest request = new BrokerRequest(requestSpec);
				request.setJob(job);
				job.setRequest(request);
				broker.addRequest(request);
				
				ourSim.addNetworkEvent(new RequestWorkersEvent(getTime(), 
						broker.getPeerId(), request.getSpec(), false));
			}
		}
	}

}
