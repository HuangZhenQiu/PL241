package edu.uci.eecs.compiler.model;


import java.util.ArrayList;

import edu.uci.eecs.compiler.representation.SSAInstruction;


public class UsageList {
	
	private SSAInstruction originalInstruction;
	protected SSAInstruction newInstruction;
	private ArrayList<SSAInstruction> usedInstructions;
	
	public UsageList(SSAInstruction instruction) {
		this.originalInstruction = instruction;
		this.usedInstructions = new ArrayList<SSAInstruction>();
	}
	
	public void addUsage(SSAInstruction instruction) {
		this.usedInstructions.add(instruction);
	}
	
	public void removeUsage(SSAInstruction instruction) {
		this.usedInstructions.remove(instruction);
	}
	
	public boolean isEmpty() {
		return usedInstructions.isEmpty();
	}
	
	/**
	 * Notify all the instructions that use the original instruction as parameter
	 * to replace it with new instruction
	 * @param newInstruction
	 */
	public void notifyUsages(SSAInstruction newInstruction) {
		this.originalInstruction = newInstruction;
		for(int i= 0; i < usedInstructions.size(); i++) {
			usedInstructions.get(i).replaceUsage(originalInstruction, newInstruction);
		}
	}
}
