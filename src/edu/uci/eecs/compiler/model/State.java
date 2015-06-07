package edu.uci.eecs.compiler.model;

import java.util.HashMap;
import java.util.Map.Entry;

import edu.uci.eecs.compiler.representation.SSAInstruction;


/**
 * Records the variables in a scope of one block, we need to store and restore the states
 * when enter or exit from a block.
 * 
 * Since DLX don't have hierarchical class definition, I don't distinguish local variables
 * with the field and parameters here. 
 * 
 * @author Peter
 *
 */
public class State {
	
	private HashMap<String, SSAInstruction> variables;
	
	public State() {
		variables = new HashMap<String, SSAInstruction>();
	}
	
	public SSAInstruction getVariable(String name) {
		return variables.get(name);
	}
	
	public void addVariable(String name) {
		variables.put(name, null);
	}
	
	public void addVariable(String name, SSAInstruction instruction) {
		variables.put(name, instruction);
	}
	
	public void setVariable(String name, SSAInstruction instruction) {
		variables.put(name, instruction);
	}
	
	public void deleteVariable(String name) {
		variables.remove(name);
	}
	
	//It is used for store and restore variable in different scope
	public State deepCopy() {
		
		State state = new State();
		for(Entry<String, SSAInstruction> entry: variables.entrySet()) {
			state.addVariable(entry.getKey(), entry.getValue());
		}
		
		return state;
	}
	
}
