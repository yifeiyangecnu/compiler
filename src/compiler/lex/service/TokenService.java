package compiler.lex.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import compiler.lex.Exception.LexException;
import compiler.lex.Exception.MyException;
import compiler.lex.domain.Token;
import compiler.lex.domain.LexError;
import compiler.lex.domain.Output;
import compiler.lex.domain.Symbol;

public interface TokenService {
	/**
	 * 处理输入文本
	 * @param source
	 * @param errors
	 * @param tokenLinePos TODO
	 * @return
	 * @throws FileNotFoundException
	 * @throws MyException
	 * @throws LexException
	 */
	public List<String>	getTokenListFromStr(String source,List<LexError> errors, List<Integer> tokenLinePos) throws FileNotFoundException, MyException, LexException;
	/**
	 *返回用于语法分析的token
	 * @param fileName
	 * @param tokenLinePos TODO
	 * @return
	 * @throws LexException 
	 * @throws MyException 
	 * @throws FileNotFoundException 
	 */
	public List<String>	getTokenListFromFile(String fileName,List<LexError> errors, List<Integer> tokenLinePos) throws FileNotFoundException, MyException, LexException;
/**
 * 输出词法分析阶段的symbol表，若有词法错误则返回null
 * @param filename
 * @return
 * @throws FileNotFoundException
 * @throws MyException
 * @throws LexException
 */
	public List<Symbol> getSymbolListFromStr(String source) throws FileNotFoundException, MyException, LexException;
	
	/**
 * 输出源文件词法分析阶段的symbol表，若有词法错误则返回null
 * @param filename
 * @return
 * @throws FileNotFoundException
 * @throws MyException
 * @throws LexException
 */
	public List<Symbol> getSymbolListFromFile(String filename) throws FileNotFoundException, MyException, LexException;
	
	public List<Output> outPutTokenFromStr(String source,List<LexError> errors) throws FileNotFoundException, MyException, LexException, IOException;
	
	/**用于输出及词法错误显示
	 *@author yangyifei 
	 * @param filename
	 * @param tokens TODO
	 * @throws LexException 
	 * @throws MyException 
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	public List<Output> outPutTokenFromFile(String filename,List<LexError> errors) throws FileNotFoundException, MyException, LexException, IOException;
	/**
	 * 
	 * @param outputFilename
	 * @param sourceFilename
	 * @throws IOException
	 * @throws LexException 
	 * @throws MyException 
	 */
	public void writeTokensToFile(String outputFilename, String sourceFilename) throws IOException, MyException, LexException;
	/**
	 * @author yangyifei
	 * @param filename
	 * @param errors
	 * @throws IOException 
	 */
	public void writeErrorToFile(String filename,List<LexError> errors) throws IOException;

	
	
}
