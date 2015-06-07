package edu.uci.eecs.compiler.register;


import java.util.HashMap;

import edu.uci.eecs.compiler.model.Method;

public class AllocatedMethod{
	private Method method;
	private Location[] locations;
	private HashMap<Integer, Method> callStubMap;
	private int[] code;
	private int stackSlotSize;
	
	public AllocatedMethod(Method method) {
		this.method = method;
		this.locations = new Location[method.getInstructionSize() + 1]; // from 1 - n
		this.callStubMap = new HashMap<Integer, Method>();
	}
	
	public Location[] getLocations() {
		return this.locations;
	}
	
	public void setCode(int[] code) {
		this.code = code;
	}
	
	public void addCallStub(int position, Method call) {
		this.callStubMap.put(position, call);
	}
	
	public void setStackSlotSize(int size) {
		this.stackSlotSize = size;
	}
	
	public int getStackSlotSize() {
		return this.stackSlotSize;
	}
	
}
