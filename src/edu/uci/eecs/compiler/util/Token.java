package edu.uci.eecs.compiler.util;

import java.lang.String;

public enum Token {
	
	MAIN("main"),
	FUNCTION("function"),
	PROCEDURE("procedure"),
	ARRAY("array"),
	VAR("var"),
	RETURN("return"),
	WHILE("while"),
	IF("if"),
	CALL("call"),
	LET("let"),
	ELSE("else"),
	FI("fi"),
	DO("do"),
	OD("od"),
	THEN("then"),
	TIMES("*"),
	DIVIDE("/"),
	PLUS("+"),
	MINUS("-"),
	EQUAL("=="),
	NEQUAL("!="),
	LESS("<"),
	GREATER(">"),
	LEQUAL("<="),
	GEQUAL(">="),
	PERIOD("."),
	COMMA(","),
	OPENBRACKET("["),
	CLOSEBRACKET("]"),
	OPENPARENTHESES("("),
	CLOSEPARENTHESES(")"),
	ASSIGNMENT("<-"),
	SEMICOLON(";"),
	BEGIN("{"),
	END("}"),
	EOF("EOF"),
	NUMBER("NUMBER"),
	IDENTIFIER("IDENTIFIER");
	
	String val;
	private Token(String val) {
		this.val = val;
	}
	
	public boolean equals(Token object){
		return this.val == object.val;
	}
}
