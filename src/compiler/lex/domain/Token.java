package compiler.lex.domain;
/**
 * @author yang
 * @version 1.0
 * @created 13-11ÔÂ-2016 9:29:55
 */
public class Token {

	private int lineNumber;
	private int linePostion;
	private String value;
	private String tokenName;
	
	
	public Token(int lineNumber, int linePostion, String tokenType) {
		super();
		this.lineNumber = lineNumber;
		this.linePostion = linePostion;
		this.tokenName = tokenType;
	}

	public Token(int lineNumber, int linePostion, String value, String tokenType) {
		super();
		this.lineNumber = lineNumber;
		this.linePostion = linePostion;
		this.value = value;
		this.tokenName = tokenType;
	}

	public String getTokenType() {
		return tokenName;
	}

	public void setTokenType(String tokenType) {
		this.tokenName = tokenType;
	}

	public Token(){

	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public int getLinePostion() {
		return linePostion;
	}

	public void setLinePostion(int linePostion) {
		this.linePostion = linePostion;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


}