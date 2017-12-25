package controllers.NovTea;

import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import core.game.StateObservation;

public class StateGraph {

	public GraphNode root;
	
	public StateGraph() { }

	public void setNewRoot(StateObservation stateObs) {
		root = new GraphNode(stateObs, null, null);
	}
	
	public ACTIONS simulate(ElapsedCpuTimer elapsedTimer) {
		ACTIONS aux = root.expand(elapsedTimer, this);
		return aux;
	}

}
