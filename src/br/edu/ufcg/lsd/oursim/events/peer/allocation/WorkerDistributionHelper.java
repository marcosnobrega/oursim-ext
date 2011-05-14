package br.edu.ufcg.lsd.oursim.events.peer.allocation;

import br.edu.ufcg.lsd.oursim.OurSim;
import br.edu.ufcg.lsd.oursim.entities.allocation.Allocation;
import br.edu.ufcg.lsd.oursim.entities.grid.Peer;
import br.edu.ufcg.lsd.oursim.entities.request.PeerRequest;
import br.edu.ufcg.lsd.oursim.events.Event;
import br.edu.ufcg.lsd.oursim.events.peer.PeerEvents;
import br.edu.ufcg.lsd.oursim.events.peer.WorkerState;
import br.edu.ufcg.lsd.oursim.events.worker.WorkerEvents;
import br.edu.ufcg.lsd.oursim.util.Configuration;

public class WorkerDistributionHelper {

	public static void redistributeWorker(long time, Peer peer, String workerId, OurSim ourSim) {
		
		Allocation workerAllocation = peer.getAllocation(workerId);
		
		if (workerAllocation == null) {
			return;
		}
		
		PeerRequest request = workerAllocation.getRequest();
		
		peer.setWorkerState(workerId, WorkerState.IDLE);
		workerAllocation.setRequest(null);
		workerAllocation.setConsumer(null);
		workerAllocation.setLastAssign(time);
		
		if (workerAllocation.isWorkerLocal()) {
			redistributeLocalWorker(time, peer, workerId, ourSim);
		} else {
			
		}
		
		if (request != null && !request.isPaused() && request.getNeededWorkers() > 0) {
			Event requestWorkersEvent = ourSim.createEvent(PeerEvents.REQUEST_WORKERS, 
					time + ourSim.getLongProperty(
							Configuration.PROP_REQUEST_REPETITION_INTERVAL), 
					peer.getId(), request.getSpec(), true);
			ourSim.addEvent(requestWorkersEvent);
		}
		
	}

	private static void redistributeLocalWorker(long time, Peer peer,
			String workerId, OurSim ourSim) {
		
		PeerRequest suitableRequestForWorker = AllocationHelper.getDownBalancedRequest(peer);
		
		if (suitableRequestForWorker == null) {
			ourSim.addNetworkEvent(ourSim.createEvent(WorkerEvents.STOP_WORK, 
					time, workerId));
		} else {
			allocateRequestToIdleWorker(time, peer, workerId, suitableRequestForWorker, ourSim);
		}
	}

	private static void allocateRequestToIdleWorker(long time, Peer peer, String workerId,
			PeerRequest request, OurSim ourSim) {

		Allocation allocation = peer.getAllocation(workerId);
		
		allocation.setRequest(request);
		allocation.setConsumer(request.getConsumer());
		allocation.setLastAssign(time);
		
		peer.setWorkerState(allocation.getWorker(), WorkerState.IN_USE);
		request.addAllocatedWorker(allocation.getWorker());
		
		ourSim.addNetworkEvent(ourSim.createEvent(WorkerEvents.WORK_FOR_BROKER, 
				time, request.getConsumer(), request.getSpec(), workerId));
		
		if (request.isPaused() || request.getNeededWorkers() <= 0) {
			request.setPaused(true);
		}
	}
	
}