package edu.uci.eecs.compiler.util;

public enum ComputationOperatorType {
	
	ADD("ADD"),
	MINUS("MINUS"),
	TIMES("TIMES"),
	DIVIDE("DIVIDE");
	
	String name;
	private ComputationOperatorType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return " " + name + " ";
	}
}
