package controllers.NovTea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import core.game.StateObservation;

public class GraphNode {

    public StateObservation state;
    public GraphNode parent;
    public double game_score;
    public double value;
    public int depth;
    public ACTIONS firstAction;
    public ACTIONS actionApplied;
    public boolean win = false;
    public boolean lose = false;
	
	public GraphNode(StateObservation stateObs, GraphNode parent, ACTIONS actionApplied2) {
		this.actionApplied = actionApplied2;
		double gamma = 0.995;
		this.state = stateObs;
		this.parent = parent;
		this.game_score = stateObs.getGameScore();
		if (parent == null){
			this.depth = 0;
			this.value = 0;
			this.firstAction = null;
		}
		else{
			this.depth = parent.depth + 1;
			this.value = parent.value + Math.pow(gamma, this.depth) * (this.game_score - parent.game_score);
			if (parent.firstAction == null) this.firstAction = actionApplied;
			else this.firstAction = parent.firstAction;
		}
		
	    boolean gameOver = stateObs.isGameOver();
	    if (gameOver){
	       	Types.WINNER winState = stateObs.getGameWinner();
	        if(winState == Types.WINNER.PLAYER_LOSES){
	        	this.value -= 9000;
	        	lose = true;
	        }
	        if(winState == Types.WINNER.PLAYER_WINS){
	        	this.value += 9000;
	        	win = true;
	        }
	    }
	}
	
	public ACTIONS expand(ElapsedCpuTimer elapsedTimer, StateGraph stateGraph) {
		int i;
		double maxValue = -1;
		ACTIONS bestFirstAction = null;
		
		int maxD = 0;
		
		//NoveltyChecker noveltyChecker = new NoveltyChecker(this.state);
		NoveltyChecker32 noveltyChecker = new NoveltyChecker32(this.state); // To use IW(3/2)

		Queue<GraphNode> qFront = new LinkedList<GraphNode>();
		qFront.add(this);

		FASelector faSel = new FASelector(this.state);
		boolean firstIter;
		if (faSel.shouldCheck(2)) firstIter = true;
		else firstIter = true;
		
		double tIni, tElap, tMax = 4;
		
		do{
			tIni = System.nanoTime();
			
			GraphNode nodeExpand = qFront.poll();
			StateObservation actualState = nodeExpand.state;
			nodeExpand.state = null;
			ArrayList<ACTIONS> actions;
			
			if (firstIter){
				firstIter = false;
				ArrayList<Integer> firstActions = faSel.getFirstPossibleActions(elapsedTimer); // returns a list with the best first actions
				actions = this.getFirstActions(firstActions, actualState);
			}
			else{
				actions = actualState.getAvailableActions();
			}
			
			int num_actions = actions.size();
			ArrayList<Integer> actInd = this.obtainRandomOrder(num_actions);
			for (i = 0; i < num_actions; i++){
				if (elapsedTimer.remainingTimeMillis() < tMax) break;
				ACTIONS act = actions.get(actInd.get(i));
				StateObservation stateNext = actualState.copy();
				stateNext.advance(act);
				
				if (noveltyChecker.shouldExpand(stateNext)){
					GraphNode nodeSon = new GraphNode(stateNext, nodeExpand, act);
					if (!nodeSon.lose && !nodeSon.win) qFront.add(nodeSon);
					if (nodeSon.depth > maxD) maxD = nodeSon.depth;
					if (nodeSon.value >= maxValue){
						maxValue = nodeSon.value;
						bestFirstAction = nodeSon.firstAction;
					}
				}
			}
			if (maxValue > 500)	break;
			
			tElap = (System.nanoTime() - tIni) / 1e6;
			if (tElap > tMax) tMax = tElap;
			
		}while (elapsedTimer.remainingTimeMillis() > tMax && !qFront.isEmpty());
		//System.out.println(maxD);
		//System.out.println(Agent.addDepth(maxD));
		
		return bestFirstAction;
	}

	private ArrayList<ACTIONS> getFirstActions(ArrayList<Integer> firstActions, StateObservation state) {
		ArrayList<ACTIONS> availableActions = state.getAvailableActions();
		ArrayList<ACTIONS> firstRecActions = new ArrayList<ACTIONS>();
		int numRecActions = firstActions.size();
		
		for (int i = 0; i < numRecActions; i++){
			firstRecActions.add(availableActions.get(firstActions.get(i)));
		}
		
		return firstRecActions;
	}

	private ArrayList<Integer> obtainRandomOrder(int num) {
		ArrayList<Integer> sequence = new ArrayList<Integer>();
		
		for (int i = 0; i < num; i++) sequence.add(i);
		Collections.shuffle(sequence);
		return sequence;
	}

}
