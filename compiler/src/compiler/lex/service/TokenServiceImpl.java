package compiler.lex.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import compiler.lex.Exception.LexException;
import compiler.lex.Exception.MyException;
import compiler.lex.domain.Input;
import compiler.lex.domain.LexConfig;
import compiler.lex.domain.LexError;
import compiler.lex.domain.Output;
import compiler.lex.domain.Symbol;
import compiler.lex.domain.TokenColorType;
import compiler.lex.domain.TokenType;

public class TokenServiceImpl implements TokenService{
	
	
	private List<Input> handleSourceInput(String source,List<LexError> errors/*out*/) throws MyException
	{
		String lines[]=source.split("\n");
		int lineNumber=1;//行号从1开始编号
		int linePostion=0;//字符在行中的位置从0开始
		List<Input> inputs=new ArrayList<Input>();
		for(int i=0;i<lines.length;++i)
		{
			String line=lines[i];
			handleLine(errors, lineNumber, linePostion, inputs,
					line);
			++lineNumber;
		}
		return inputs;
	}

	private void handleLine(List<LexError> errors, int lineNumber,
			int linePostion, List<Input> inputs, String line)
			throws MyException {
		if(!isCommentLine(line))
		{
			String[] words=line.split("\\s+");
			for(String word:words)
			{
				if(word.isEmpty())
					continue;
				if(LexConfig.isKeywordOrOperatorOrDelimeiter(word))
				{
					inputs.add(new Input(lineNumber,linePostion,word,TokenType.getTokenNameByToken(word)));
				}
				else if(LexConfig.isInt(word)||LexConfig.isReal(word))
				{
					inputs.add(new Input(lineNumber,linePostion,word,TokenType.NUM.getTokenName()));
				}
				else if(LexConfig.isIdentifier(word))
				{
					inputs.add(new Input(lineNumber,linePostion,word,TokenType.IDENTIFIER.getTokenName()));
				}
				else
				{
					inputs.add(new Input(lineNumber,linePostion,word,TokenType.UNFAIR.getTokenName()));
					errors.add(new LexError(lineNumber,linePostion,word));
				}
				linePostion+=word.length();
			}
		}
	}



	private List<Input> handleFileInput(String fileName,List<LexError> errors/*out*/) throws FileNotFoundException, MyException, LexException {
		
		Scanner scanner=new Scanner(new File(fileName));
		int lineNumber=1;//行号从1开始编号
		int linePostion=0;//字符在行中的位置从0开始
		List<Input> inputs=new ArrayList<Input>();
		while(scanner.hasNextLine())
		{
			String line=scanner.nextLine();
			 handleLine(errors, lineNumber, linePostion, inputs,
					line);
			++lineNumber;
		}
		scanner.close();
		return inputs;
	}
	

	/**
	 * 判断某行是否为注释行
	 * @param line
	 * @return
	 * @throws MyException 
	 */
	public  boolean isCommentLine(String line) throws MyException
	{ 
		if(null==line)
			throw new MyException("line为null");
		return line.matches("\\s*//.*");
	}



	@Override
	public void writeTokensToFile(String outputFilename, String sourceFilename) throws IOException, MyException, LexException {
		List<LexError> errors=new ArrayList<LexError>();
		List<Input> tokens=handleFileInput(sourceFilename, errors);
		File file = new File(outputFilename);
		if(!file.exists())
			file.createNewFile();
		PrintWriter out=new PrintWriter(file);
		for(Input input:tokens)
		{
			out.printf("%s\t", input.getTokenType());
		}
		out.printf("共有%d处词法错误:\n", errors.size());
		for(LexError error:errors)
		{
			out.printf("第%d行[%s]有词法错误，不能识别\n", error.getLineNumber(),error.getInvailedWord());
		}
		out.close();
	}

	@Override
	public void writeErrorToFile(String filename, List<LexError> errors) throws IOException {
		File file = new File(filename);
		if(!file.exists())
			file.createNewFile();
		PrintWriter out=new PrintWriter(file);
		out.printf("共有%d处词法错误:\n", errors.size());
		for(LexError error:errors)
		{
			out.printf("第%d行[%s]有词法错误，不能识别\n", error.getLineNumber(),error.getInvailedWord());
		}
		out.close();
		System.out.print("已将错误信息输出到"+filename);
	}

	@Override
	public List<String> getTokenListFromFile(String fileName,List<LexError> errors) throws FileNotFoundException, MyException, LexException {
		return handleFileInput(fileName,errors).stream().map(arg0->arg0.getTokenType()).collect(Collectors.toList());
	}

	@Override
	public List<Output> outPutTokenFromFile(String filename,List<LexError> errors) throws MyException, LexException, IOException {
		List<Input> inputs = this.handleFileInput(filename,errors);
		return outPutTokenFromInputs(errors, inputs);
	}
	@Override
	public List<Output> outPutTokenFromStr(String source, List<LexError> errors)
			throws FileNotFoundException, MyException, LexException,
			IOException {
		List<Input> inputs = this.handleSourceInput(source, errors);
		return outPutTokenFromInputs(errors, inputs);
		
	}
	


	@Override
	public List<Symbol> getSymbolListFromFile(String filename) throws FileNotFoundException, MyException, LexException {
		List<LexError> errors=new ArrayList<LexError>();
		List<Input> inputs=handleFileInput(filename, errors);
		return getSymbolListFromInputs(errors, inputs);
	}

	@Override
	public List<String> getTokenListFromStr(String source, List<LexError> errors)
			throws FileNotFoundException, MyException, LexException {
		return handleSourceInput(source,errors).stream().map(arg0->arg0.getTokenType()).collect(Collectors.toList());
	}
	@Override
	public List<Symbol> getSymbolListFromStr(String source)
	
			throws FileNotFoundException, MyException, LexException {
		List<LexError> errors=new ArrayList<LexError>();
		List<Input> inputs=handleSourceInput(source, errors);
		return getSymbolListFromInputs(errors, inputs);
	}
	private List<Symbol> getSymbolListFromInputs(List<LexError> errors,
			List<Input> inputs) {
		if(!errors.isEmpty())
		{
			return null;
		}

		return inputs.stream().filter(input->LexConfig.isIntOrRealOrIdentifier(input.getValue()))
		.map
		(input->
		new Symbol(input.getValue(),input.getLineNumber(),input.getLinePostion(),input.getTokenType()))
		.collect(Collectors.toList());
	}




	private List<Output> outPutTokenFromInputs(List<LexError> errors,
			List<Input> inputs) throws IOException {
		List<Output> output=inputs.stream().map(input->
		{
			return new Output(input.getValue(),this.getTokenColor(input.getValue()),
					input.getLineNumber());
			}
		).collect(Collectors.toList());
		if(!errors.isEmpty())
			writeErrorToFile("error.txt", errors);
		return output;
	}
	
	private TokenColorType getTokenColor(String tokenName) 
	{
		
		if(LexConfig.isKeyWord(tokenName))
			return TokenColorType.BLUE;
		else if(LexConfig.isDelimiter(	tokenName)||LexConfig.isOpeartor(tokenName))
			return TokenColorType.BLACK;
		else if(LexConfig.isIntOrRealOrIdentifier(tokenName))
		{
			return TokenColorType.GREEN;
		}
		else 
			return TokenColorType.RED;
	}
}
