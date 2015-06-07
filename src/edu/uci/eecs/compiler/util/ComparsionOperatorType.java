package edu.uci.eecs.compiler.util;

import java.lang.RuntimeException;

public enum ComparsionOperatorType {
	
	EQUAL("EQUAL"),
	NEQUAL("NEQUAL"),
	LESS("LESS"),
	LARGER("LARGER"),
	LEQ("LEQ"),
	GEQ("GEQ");
	
	String name;
	private ComparsionOperatorType(String name) {
		this.name = name;
	}
	
	private ComparsionOperatorType negtive() {
		ComparsionOperatorType type;
		switch(this) {
			case EQUAL:
				type = NEQUAL;
				break;
			case NEQUAL:
				type = EQUAL;
				break;
			case LESS:
				type = GEQ;
				break;
			case LARGER:
				type = LEQ;
				break;
			case LEQ:
				type = LARGER;
				break;
			case GEQ:
				type = LESS;
				break;
			default:
				throw new RuntimeException("Unexpected type");
		}
		return type;
		
	}
	
	@Override
	public String toString() {
		return " " + name + " ";
	}

}
