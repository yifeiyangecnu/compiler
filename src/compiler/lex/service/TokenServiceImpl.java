package compiler.lex.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.SeekableByteChannel;
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
		int lineNumber=1;//�кŴ�1��ʼ���
		int linePostion=0;//�ַ������е�λ�ô�0��ʼ
		List<Input> inputs=new ArrayList<Input>();
		for(int i=0;i<lines.length;++i)
		{
			String line=lines[i];
			if(line.isEmpty()||line.equals("\r"))
				continue;
			handleLine(errors, lineNumber, linePostion, inputs,
					line);
			++lineNumber;
		}
		return inputs;
	}

	private void handleLine(List<LexError> errors, int lineNumber,
			int linePostion, List<Input> inputs, String line)
			throws MyException {
		if(!isCommentLine(line)&&!line.isEmpty())
		{
//			String[] words=separate(line);
			List<String> words=separate(line);
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



	private List<String> separate(String line) {
		List<String> words=new ArrayList<String>();
		String tempWords[]=line.split("\\s+");
		for(String str:tempWords)
		{
			do{
			int length = str.length();
			int i=length;
			while(i>0)
			{
				String temp=str.substring(0, i);
				if(LexConfig.isSeparate(temp))
				{
					if(i<length)
					{
						words.add(temp);
						str=str.substring(i);
					}
					break;
				}
				i--;
			}
			if(i==0||i==length)
			{
				words.add(str);
				break;
			}
			}while(true);
		}
		for(String str:words)
		{
			System.err.println(str);
		}
		return words;
	}

	private List<Input> handleFileInput(String fileName,List<LexError> errors/*out*/) throws FileNotFoundException, MyException, LexException {
		
		Scanner scanner=new Scanner(new File(fileName));
		int lineNumber=1;//�кŴ�1��ʼ���
		int linePostion=0;//�ַ������е�λ�ô�0��ʼ
		List<Input> inputs=new ArrayList<Input>();
		while(scanner.hasNextLine())
		{
			String line=scanner.nextLine();
			if(line.isEmpty()||line.equals("\r"))
				continue;
			 handleLine(errors, lineNumber, linePostion, inputs,
					line);
			++lineNumber;
		}
		scanner.close();
		return inputs;
	}
	

	/**
	 * �ж�ĳ���Ƿ�Ϊע����
	 * @param line
	 * @return
	 * @throws MyException 
	 */
	public  boolean isCommentLine(String line) throws MyException
	{ 
		if(null==line)
			throw new MyException("lineΪnull");
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
		out.printf("����%d���ʷ�����:\n", errors.size());
		for(LexError error:errors)
		{
			out.printf("��%d��[%s]�дʷ����󣬲���ʶ��\n", error.getLineNumber(),error.getInvailedWord());
		}
		out.close();
	}

	@Override
	public void writeErrorToFile(String filename, List<LexError> errors) throws IOException {
		File file = new File(filename);
		if(!file.exists())
			file.createNewFile();
		PrintWriter out=new PrintWriter(file);
		out.printf("����%d���ʷ�����:\n", errors.size());
		for(LexError error:errors)
		{
			out.printf("��%d��[%s]�дʷ����󣬲���ʶ��\n", error.getLineNumber(),error.getInvailedWord());
		}
		out.close();
		System.out.print("�ѽ�������Ϣ�����"+filename);
	}

	@Override
	public List<String> getTokenListFromFile(String fileName,List<LexError> errors, List<Integer> tokenLinePos) throws FileNotFoundException, MyException, LexException {
		List<Input> inputs = handleFileInput(fileName,errors);
		inputs.forEach(input->tokenLinePos.add(input.getLineNumber()));
		return inputs.stream()
				.map(arg0->arg0.getTokenType()).collect(Collectors.toList());
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
	public List<String> getTokenListFromStr(String source, List<LexError> errors, List<Integer> tokenLinePos)
			throws FileNotFoundException, MyException, LexException {
		List<Input> inputs = handleSourceInput(source,errors);
		inputs.forEach(input->tokenLinePos.add(input.getLineNumber()));
		return inputs.stream()
				.map(arg0->arg0.getTokenType()).collect(Collectors.toList());
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
