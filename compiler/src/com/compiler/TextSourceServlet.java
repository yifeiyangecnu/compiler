package com.compiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import compiler.lex.domain.LL;
import compiler.lex.domain.LexError;
import compiler.lex.domain.Output;
import compiler.lex.domain.Symbol;
import compiler.lex.service.TokenService;
import compiler.lex.service.TokenServiceImpl;

/**
 * Servlet implementation class TextSourceServlet
 */
@WebServlet("/TextSourceServlet")
public class TextSourceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TextSourceServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			request.setCharacterEncoding("UTF-8");
			String source=request.getParameter("source");
			try{
			   TokenService tokenService=new TokenServiceImpl();
               List<Symbol> symbols=tokenService.getSymbolListFromStr(source);
               List<LexError> errors=new ArrayList<>();
               List<String> tokens=tokenService.getTokenListFromStr(source, errors);
               errors.clear();
               List<Output> outputs=tokenService.outPutTokenFromStr(source, errors);
               setAttribute(request, symbols, errors, outputs, tokens);
			}catch (Exception ex) {
	            request.setAttribute("message",
	                    "������Ϣ: " + ex.getMessage());
	        }
			
	        getServletContext().getRequestDispatcher("/compiler.jsp").forward(
	                request, response);
	}

	static void setAttribute(HttpServletRequest request, List<Symbol> symbols,
			List<LexError> errors, List<Output> outputs, List<String> tokens) {
			if(errors.isEmpty())
			{
				LL test=new LL();
				test.simplify();
				test.first();
				test.follow();
				List<String> productions=test.print_table();
				List<String> gramErrors=test.analysis(tokens);
				System.out.println();			
				for(int i=0;i<test.column;++i)
				{
					System.out.print("\t"+test.terminals.get(i));
				}
				System.out.println();			
				for(int i=0;i<test.row;++i)
				{
					System.out.print(test.nonterminals.get(i)+"\t");
					for(int j=0;j<test.column;++j)
					{
						System.out.print(productions.get(i*test.column+j)+"\t");
					}
					System.out.println();
				}
				request.setAttribute("terminals", test.terminals);
				request.setAttribute("nonterminals", test.nonterminals);
				request.setAttribute("productions", productions);
				request.setAttribute("row", test.row);
				request.setAttribute("column", test.column);
				request.setAttribute("gramErrors", gramErrors);
			}
			else{
					System.err.println("�дʷ�����");
				}
			request.setAttribute("symbols",
					symbols);
			request.setAttribute("outputs",
					outputs);
			request.setAttribute("errors",
					errors);
	}
}
