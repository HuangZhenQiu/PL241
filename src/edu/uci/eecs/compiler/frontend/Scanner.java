package edu.uci.eecs.compiler.frontend;

import java.io.StreamTokenizer;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;

import edu.uci.eecs.compiler.util.Token;

public class Scanner {

	private StreamTokenizer tokenizer;
	private Token currentToken;
	
	public Scanner(String fileName) {
		try{
			FileReader reader = new FileReader(new File(fileName));
			StringBuffer buffer = new StringBuffer("");
			new StreamTokenizer(new StringReader(""));
			tokenizer = new StreamTokenizer(reader);
			tokenizer.ordinaryChar('*');
			tokenizer.ordinaryChar('/');
			tokenizer.ordinaryChar('+');
			tokenizer.ordinaryChar('-');
			tokenizer.ordinaryChar('=');
			tokenizer.ordinaryChar('[');
			tokenizer.ordinaryChar('>');
			tokenizer.ordinaryChar('<');
			tokenizer.ordinaryChar(']');
			tokenizer.ordinaryChar('.');
			tokenizer.ordinaryChar(',');
			tokenizer.ordinaryChar('(');
			tokenizer.ordinaryChar('{');
			tokenizer.ordinaryChar('}');
			tokenizer.ordinaryChar(';');
			tokenizer.commentChar('#');
			tokenizer.slashStarComments(true);
			tokenizer.slashSlashComments(true);
			tokenizer.eolIsSignificant(false);
			//tokenizer.whitespaceChars(' ', ' ');
			
			nextToken();
		} catch(Exception e){
			System.out.println(e.toString());
		}
		
	}
	
	public void nextToken() {
		
		try {
			if(this.tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
				
				if(tokenizer.ttype == StreamTokenizer.TT_WORD) {
					
					if(tokenizer.sval.equals("main")) {
						currentToken = Token.MAIN;
					} else if (tokenizer.sval.equals("function")) {
						currentToken = Token.FUNCTION;
					} else if (tokenizer.sval.equals("if")) {
						currentToken = Token.IF;
					} else if (tokenizer.sval.equals("fi")) {
						currentToken = Token.FI;
					} else if (tokenizer.sval.equals("else")) {
						currentToken = Token.ELSE;
					} else if (tokenizer.sval.equals("do")) {
						currentToken = Token.DO;
					} else if (tokenizer.sval.equals("od")) {
						currentToken = Token.OD;
					} else if (tokenizer.sval.equals("let")) {
						currentToken = Token.LET;
					} else if (tokenizer.sval.equals("call")) {
						currentToken = Token.CALL;
					} else if (tokenizer.sval.equals("while")) {
						currentToken = Token.WHILE;
					} else if (tokenizer.sval.equals("return")) {
						currentToken = Token.RETURN;
					} else if (tokenizer.sval.equals("var")) {
						currentToken = Token.VAR;
					} else if (tokenizer.sval.equals("array")) {
						currentToken = Token.ARRAY;
					} else if (tokenizer.sval.equals("then")) {
						currentToken = Token.THEN;
					} else if (tokenizer.sval.equals("procedure")) {
						currentToken = Token.PROCEDURE;
					} else {
						currentToken = Token.IDENTIFIER;
					}
					
				} else if(tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
					
					currentToken = Token.NUMBER;
					
				} else {
					
					switch (tokenizer.ttype) {
						
						case '(':
							currentToken = Token.OPENPARENTHESES;
							break;
						case ')':
							currentToken = Token.CLOSEPARENTHESES;
							break;
						case '[':
							currentToken = Token.OPENBRACKET;
							break;
						case ']':
							currentToken = Token.CLOSEBRACKET;
							break;
						case '{':
							currentToken = Token.BEGIN;
							break;
						case '}':
							currentToken = Token.END;
							break;
						case '*':
							currentToken = Token.TIMES;
							break;
						case '/':
							currentToken = Token.DIVIDE;
							break;
						case '+':
							currentToken = Token.PLUS;
							break;
						case '-':
							currentToken = Token.MINUS;
							break;
						case ',':
							currentToken = Token.COMMA;
							break;
						case ';':
							currentToken = Token.SEMICOLON;
							break;
						case '.':
							currentToken = Token.PERIOD;
							break;
						case '<':
							tokenizer.nextToken();
							if(tokenizer.ttype == '=') {
								currentToken = Token.LEQUAL;
							} else if (tokenizer.ttype == '-') {
								currentToken = Token.ASSIGNMENT;
							} else {
								tokenizer.pushBack();
								currentToken = Token.LESS;
							}
							break;
						case '>':
							tokenizer.nextToken();
							if(tokenizer.ttype == '=') {
								currentToken = Token.GEQUAL;
							} else {
								tokenizer.pushBack();
								currentToken = Token.LESS;
							}
							break;
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	
	public boolean expectToken(Token token) {
		
		if(token!= null && currentToken.equals(token))
			return true;
		
		return false;
	}
	
	public void pushBack() {
		this.tokenizer.pushBack();
	}
	
	public int getCurrentNumber() {
		return (int)this.tokenizer.nval;
	}
	
	public String getCurrentContent() {
		return this.tokenizer.sval;
	}
	
	public int getLineNumber() {
		return this.tokenizer.lineno();
	}
	
	public void print() {
		nextToken();
		while(currentToken != Token.EOF) {
			if(currentToken == Token.NUMBER) {
				
				System.out.println(currentToken.name() + ": " + tokenizer.nval);
			} else {
				
				System.out.println(currentToken.name() + ": " + tokenizer.sval);
			}
			nextToken();
		}
	}
	

	public static void main(String[] args) {
	}
	
}
