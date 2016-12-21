package compiler.lex.domain;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import compiler.lex.Exception.LexException;
import compiler.lex.Exception.MyException;
import compiler.lex.service.TokenService;
import compiler.lex.service.TokenServiceImpl;



public class Main 
{

	static void testList()
	{
		List<String> lists=new ArrayList<>();
		for(int i=0;i<10;++i)
			lists.add("");
		lists.set(3, "3");
		lists.set(5, "5");
		System.out.println(lists.size());
		for(String str:lists)
			System.out.println(str);
	}
	public static void main(String[] args) throws FileNotFoundException, MyException, LexException 
	{
		LL test=new LL();
	    test.simplify();
		test.first();
        test.follow();
		try {
			test.analysis();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	}
//		testList();
//		TokenService tokenService=new TokenServiceImpl();
//		List<LexError> errors=new ArrayList<>();
//		List<String> tokens=tokenService.getTokenListFromFile("d:/test.toy", errors, null);
//		if(errors.isEmpty())
//		{
//			LL test=new LL();
//			List<String> simpProductions=test.simplify();
//			for(String str:simpProductions)
//			{
//				System.err.print(str);
//			}
//			test.first();
//			test.follow();
//			List<String> productions=test.print_table();
//			test.analysis(tokens, null);
//			
//			
//			System.out.println();			
//			for(int i=0;i<test.column;++i)
//			{
//				System.out.print("\t"+test.terminals.get(i));
//			}
//			System.out.println();			
//			for(int i=0;i<test.row;++i)
//			{
//				System.out.print(test.nonterminals.get(i)+"\t");
//				for(int j=0;j<test.column;++j)
//				{
//					System.out.print(productions.get(i*test.column+j)+"\t");
//				}
//				System.out.println();
//			}
//		}
//		else{
//				System.out.println("ÓÐ´Ê·¨´íÎó");
//			}
//	}

