package edu.uci.eecs.compiler.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import edu.uci.eecs.compiler.model.Method;
import edu.uci.eecs.compiler.representation.SymbolTable;
import edu.uci.eecs.compiler.util.Constant;

public class Program {
	
	private HashMap<String, Method> methods;
	private SymbolTable table;
	
	public Program() {
		methods = new HashMap<String, Method>();
		table = new SymbolTable();
	}
	
	public Collection<Method> getMethods() {
		return methods.values();
	}
	
	public void addMethod(Method method) {
		methods.put(method.getName(), method);
	}
	
	public void addSymbol(String name, Integer lineNumber, List<Integer> dimentions) {
		if(dimentions == null) {
			this.table.insertVariable(name, lineNumber);
		} else {
			this.table.insertArray(name, lineNumber, dimentions);
		}
	}
	
	public Set<String> getSymbols() {
		return table.getSymbols();
	}
	
	public boolean hasVariable(String name) {
		return this.table.isDeclared(name);
	}
	
	public List<Integer> getDimention(String name) {
		return this.table.getDimension(name);
	}

	public String toGraph() {
		String content = "";
		for(Entry<String, Method> entry: methods.entrySet()) {
			content += entry.getValue().toGraph() + Constant.LINE_SEPARATER;
		}
		
		return content;
	}

}
