package compiler.lex.domain;

public class StackMessage {
	String stack;
	String input;
	String action;
	
	public StackMessage(String stack, String input, String action) {
		super();
		this.stack = stack;
		this.input = input;
		this.action = action;
	}
	public String getStack() {
		return stack;
	}
	public void setStack(String stack) {
		this.stack = stack;
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
}
