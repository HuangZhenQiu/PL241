package edu.uci.eecs.compiler.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.uci.eecs.compiler.representation.Block;
import edu.uci.eecs.compiler.representation.SymbolTable;
import edu.uci.eecs.compiler.util.Constant;

public class Method {
	private String name;
	private boolean isFucntion;
	private Block beginBlock;
	private Block exitBlock;
	private InferenceGraph inferenceGraph;
	private SymbolTable symbolTable;
	private List<Block> blocks;
	private DominatorTree dominatorTree;
	
	//It is for building inference graph at register allocation stage.
	private List<Block> reverseOrderBlocks;
	
	public Method(String name, boolean isFunction) {
		this.name = name;
		this.isFucntion = isFunction;
		this.inferenceGraph = new InferenceGraph();
		this.symbolTable = new SymbolTable();
		this.blocks = new ArrayList<Block>();
		this.reverseOrderBlocks = new ArrayList<Block>();
	}
	
	
	public String getName() {
		return this.name;
	}
	
	public List<Integer> getDimension(String name) {
		return this.symbolTable.getDimension(name);
	}
	
	public void addSymbol(String name, int lineNumber, ArrayList<Integer> dimentions) {
		if(dimentions != null) {
			this.symbolTable.insertArray(name, lineNumber, dimentions);
		} else {
			this.symbolTable.insertVariable(name, lineNumber);
		}
	}
	
	public int getInstructionSize() {
		int exitBlockSize = exitBlock.getInstructions().size();
		int maxInstructionId = exitBlock.getInstruction(exitBlockSize - 1).getInstructionId();
		return maxInstructionId;
	}
	
	public DominatorTree getDominatorTree() {
		if (dominatorTree == null) {
			return this.dominatorTree;
		} else {
			return buildDominatorTree();
		}
	}
	
	public DominatorTree buildDominatorTree() {
		this.dominatorTree = new DominatorTree(this);
		if (this.dominatorTree == null) {
			for (int i = 0; i < blocks.size(); i++) {
				Block block = blocks.get(i);
				if (block.getDominator() != null) {
					dominatorTree.addDominatePair(block.getDominator(), block);
				}
			}
		}
		
		return this.dominatorTree;
	}
	
	public String toGraph() {
		
		//create clusters
		String methodContent = "digraph " + name + " {" + Constant.LINE_SEPARATER;
		for(Block block: blocks) {
			methodContent += block.toGraph() + Constant.LINE_SEPARATER;
		}
		
		
		//create links
		for(Block block:blocks) {
			for(Block successor: block.getSubsequentBlocks()) {
				methodContent += "\"end" + block.getId() +"\" -> \"start" + successor.getId()
						+ "\" [ltail=cluster" + block.getId() + " lhead=cluster"
						+ successor.getId() +"];" + Constant.LINE_SEPARATER;
			}
		}
		
		methodContent += "}" + Constant.LINE_SEPARATER;
		return methodContent;
	}
	
	public boolean hasSymbol(String name) {
		return this.symbolTable.isDeclared(name);
	}
	
	public Set<String> getSymbols() {
		return this.symbolTable.getSymbols();
	}
	
	public void addBlock(Block block) {
		blocks.add(block);
	}
	
	public void removeBlock(Block block) {
		blocks.remove(block);
	}
	
	public List<Block> getBlocks() {
		return this.blocks;
	}


	public void setBeginBlock(Block beginBlock) {
		this.beginBlock = beginBlock;
	}


	public void setExitBlock(Block exitBlock) {
		this.exitBlock = exitBlock;
	}
	
	public Block getExitBlock() {
		return this.exitBlock;
	}
	
	public boolean isFunction() {
		return this.isFucntion;
	}
}
