package org.ggp.base.player.gamer.statemachine;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;

public class CacheNode {
	public int bestValue;
	public int depth;
	public Move bestMove;
	public MachineState node;
	public enum type {exact, upper, lower};
	public type flag;

	public CacheNode(int bestValue, int depth, Move bestMove, MachineState node, type flag)
	{
		this.bestValue = bestValue;
		this.depth = depth;
		this.bestMove = bestMove;
		this.node = node;
		this.flag = flag;
	}

}
