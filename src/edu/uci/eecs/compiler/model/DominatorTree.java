package edu.uci.eecs.compiler.model;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map.Entry;

import edu.uci.eecs.compiler.representation.Block;
import edu.uci.eecs.compiler.util.Constant;

public class DominatorTree {
	private Method method;
	private HashMap<Block, List<Block>> dominatorMap;
	
	public DominatorTree(Method method) {
		this.method = method;
		this.dominatorMap = new HashMap<Block, List<Block>>();
	}
	
	public void addDominatePair(Block dominator, Block block) {
		
		if(dominatorMap.get(dominator)!=null) {
			dominatorMap.get(dominator).add(block);
		} else {
			List<Block> blocks = new ArrayList<Block>();
			blocks.add(block);
			dominatorMap.put(dominator, blocks);
		}
	}
	
	public boolean isDominator(Block dominator, Block block) {
		
		Block parent = block.getDominator();
		while(parent!=null) {
			if(parent.equals(dominator)) {
				return true;
			}
			parent = parent.getDominator();
		}
		return false;
	}
	
	public String toGraph() {
		
		//create clusters
		String methodContent = "digraph " + method.getName() + "_dominator_tree {" + Constant.LINE_SEPARATER;
		for(Block block: method.getBlocks()) {
			methodContent += block.toGraph() + Constant.LINE_SEPARATER;
		}
		
		
		for(Entry<Block, List<Block>> entry : dominatorMap.entrySet()) {
			Block begin = entry.getKey();
			for(Block end : entry.getValue()) {
				methodContent += "\"end" + begin.getId() +"\" -> \"start" + end.getId()
						+ "\" [ltail=cluster" + begin.getId() + " lhead=cluster"
						+ end.getId() +"];" + Constant.LINE_SEPARATER;
			}
		}
		
		methodContent += "}" + Constant.LINE_SEPARATER;
		return methodContent;
		
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
