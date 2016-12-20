package compiler.lex.domain;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Stack;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import java.util.Set;

public class LL 
{
    
    
	private LinkedHashMap<String,String> LLmap=new LinkedHashMap<String, String>();
	private Table<String,String,String>  LLanaTable = HashBasedTable.create();
	private Table<String,String,String>  predicLLanaTable = HashBasedTable.create();
    public ArrayList<String>nonterminals=new ArrayList<String>();//非终结符
    public List<String> terminals=new ArrayList<>();
    ArrayList<String>values = new ArrayList<String>();
    ArrayList<String>mothers = new ArrayList<String>();
    private ArrayList<String>saveout=new ArrayList<String>();
    public int row=0;
    public int column=0;
	
    
    public LL()
	{
		LLmap.put("program","compoundstmt");
		LLmap.put("stmt","ifstmt | whilestmt | assgstmt | compoundstmt");
		LLmap.put("compoundstmt","{ stmts }");
		LLmap.put("stmts","stmt stmts | ε");
		LLmap.put("ifstmt","if ( boolexpr ) then stmt else stmt");
		LLmap.put("whilestmt","while ( boolexpr ) stmt");
		LLmap.put("assgstmt","ID = arithexpr ;");
		LLmap.put("boolexpr","arithexpr boolop arithexpr");
		LLmap.put("boolop","< | > | <= | >= | ==");
		LLmap.put("arithexpr","multexpr arithexprprime");
		LLmap.put("arithexprprime","+ multexpr arithexprprime | - multexpr arithexprprime | ε");
		LLmap.put("multexpr","simpleexpr multexprprime");
		LLmap.put("multexprprime","* simpleexpr multexprprime | / simpleexpr multexprprime | ε");
		LLmap.put("simpleexpr","ID | NUM | ( arithexpr )");
		
		LLmap.put("decl", "type list;");
		LLmap.put("type", "int | real");
		LLmap.put("list", "ID list1");
		LLmap.put("list1","list |" );

		
		terminals.add("|");
		terminals.add("{");
		terminals.add("}");
//		terminals.add("ε");
		terminals.add("if");
		terminals.add("(");
		terminals.add(")");
		terminals.add("then");
		terminals.add("else");
		terminals.add("while");		
		terminals.add("ID");
		terminals.add("=");
		terminals.add("<");
		terminals.add(">");
		terminals.add(">=");
		terminals.add("<=");
		terminals.add("==");
		terminals.add("+");
		terminals.add("-");
		terminals.add("*");
		terminals.add("/");
		terminals.add("NUM");
	}
    //消除左递归
	public List<String> simplify()
	{
		List<String> simpProductions=new ArrayList<>();
		 simplify(LLmap);
		 Iterator<Map.Entry<String,String>>entries=LLmap.entrySet().iterator();
			logInfo("消除左递归后的产生式为:\n");
			while(entries.hasNext())
			{
			    Map.Entry<String,String>entry=entries.next();
			    logInfo(entry.getKey()+"-->"+entry.getValue()+"\n");
			    simpProductions.add(entry.getKey()+"-->"+entry.getValue()+"\n");
			}
			return simpProductions;
	}
	protected HashMap<String, String> simplify(LinkedHashMap<String, String> LLmap)
	{
		LinkedHashMap<String, String> simplifymap=new LinkedHashMap<String, String>();
		
		ArrayList<String> KeyList = new ArrayList<String>(); 
		
		Set<Entry<String, String>> set = LLmap.entrySet(); 
		Iterator<Entry<String, String>> i = set.iterator();
		while(i.hasNext())
		{
			Map.Entry<String, String> entry=(Map.Entry<String, String>)i.next();
			KeyList.add(entry.getKey());
		}
		for(int x=0;x<KeyList.size();x++)
		{
			String key=KeyList.get(x);
			String value=(String) LLmap.get(key).toString();
			if(!value.contains(key))continue;
			else
			{
				if(!value.contains("|"))
				{
					int tokenposition=value.indexOf(key);
					if(tokenposition!=0)continue;
					else
					{
						if(value.substring(key.length(), key.length()+1)!=" ")continue;//同样前缀字符串
						else 
						{
							String newkeyvalue,key_,value_,afterkey;
							afterkey=value.substring(key.length()+1,value.length()-1);
							newkeyvalue=key+"'";//key的新产生式
							key_=newkeyvalue;
							value_=afterkey+" "+newkeyvalue+" "+"|"+" "+"ε";
							LLmap.put(key, newkeyvalue);
							LLmap.put(key_, value_);//消除了key的左递归
						}
					}
				}
				else
				{
					ArrayList<String>production=new ArrayList<String>();//不含key和含key不在开头的产生式
					ArrayList<String>production_=new ArrayList<String>();//含key且在开头的产生式
					
					while(value.contains("|"))
					{
					    int linepos=value.indexOf("|");
					    if(linepos==0)break;
					    String cutout=value.substring(0,linepos);
					    if(!cutout.contains(key))
					    {
					    	production.add(cutout);
					    }
					    else
					    {
					    	int tokenposition=value.indexOf(key);
							if(tokenposition!=0)production.add(cutout);//第一产生式产生了空格
							else production_.add(cutout);
					    }
						value=value.substring(linepos+1,value.length())+"|";
					}

					for(int m=0;m<production.size();m++)
					{
						if(production_.size()!=0)
						production.set(m,production.get(m)+" "+key+"'");
					}
					for(int m=0;m<production_.size();m++)
					{
						production_.set(m,production_.get(m).substring(key.length(),production_.get(m).length())+" "+key+"'");
					}
					String finalproduction="",finalproduction_="",key_="";
					key_=key+"'";
					for(int m=0;m<production.size();m++)
					{
						finalproduction=finalproduction+production.get(m)+"|";
					}
					finalproduction=finalproduction.substring(0,finalproduction.length()-1);//从1开始截,消除了右部第一个产生式的空格
					if(production_.size()!=0)
					{
						for(int m=0;m<production_.size();m++)
							{
								finalproduction_=finalproduction_+production_.get(m)+"|";
							}
						finalproduction_=finalproduction_.substring(0,finalproduction_.length()-2);
						finalproduction_=finalproduction+" "+"|"+" "+"ε";
						LLmap.put(key_, finalproduction_);
					}					
					
					LLmap.put(key, finalproduction);				
				}
			}
		}
		simplifymap=LLmap;
		return (HashMap<String, String>) simplifymap;
	}
	
	public void first()
	{
		Set<Entry<String, String>> set = LLmap.entrySet(); 
		Iterator<Entry<String, String>> it = set.iterator();
		while(it.hasNext())
		{
			Map.Entry<String, String> entry=(Map.Entry<String, String>)it.next();
			nonterminals.add(entry.getKey());
		}	
		for(int i=0;i<nonterminals.size();i++)
		{
			
			String nonterminalelem=nonterminals.get(i);
			String production =  LLmap.get(nonterminalelem);
			logInfo("\n\n\n-----------开始计算"+nonterminalelem+"的first--------------\n");
			first(nonterminalelem,production,nonterminalelem+"-->"+production);
			
		}
		print();
	}
	
	protected void first(String start,String production,String father){
		 if (production.contains("|")){
			  for (String str :production.split("\\|")){
				 if (LLmap.get(start).contains(str))   //会出现进入递归后才需要判断“|”的情况，因此要分开
				  {
					 int nospace = 0;while(str.charAt(nospace)==' ') nospace++;
					 str=str.substring(nospace,str.length()); //此为写入LLanaTable的特殊情况，虽然在后续中会处理空格，此时需要且只能去掉首空格
					 first(start,str,start+"-->"+str);
				  }
				 else
				  first(start,str,father);
			 }
		}
	     else   //不包含|,即要么为单一生生成式，要么为|分解后的生成式
	       {
	    	 for (String str : production.split(" ")){
	    		 if (!str.equals("")){         //split空格有时会生成“”
	    			 if (!nonterminals.contains(str)){
	    				 LLanaTable.put(start, str, father);
	    				 break;
	    			 }
	    			 else                           {
	    			  String tmp_production = LLmap.get(str);
	    			  first(start,tmp_production,father);
	    			  break;
	    		     }
	    		 }  
	    	 }
	        }
		}
	public ArrayList<String> getfirst(String ntm)
	{
	    ArrayList<String> res = new ArrayList<>();
		Set<String>terminals = LLanaTable.columnKeySet();
		Iterator<String>i = terminals.iterator();
		 logInfo("该非终结符的first有\n");
	     while(i.hasNext())
	     {
	    	 String tmp = i.next();
	    	if (LLanaTable.get(ntm, tmp)!=null && !LLanaTable.get(ntm, tmp).equals(ntm+"-->ε") )
	    	{
	    		res.add(tmp);
	    		logInfo(tmp);
	    	}
	    	if(LLanaTable.get(ntm, "ε")!=null) 
	    	{
	    		res.add("ε");
	    		logInfo("ε");
	    	}
	     }
	     return res;
	}
	private void print()
	{
		
		///输出first
		logInfo("各非终结符的first以及对应生成式:\n");
		Set<Cell<String, String, String>> ana_Set  = LLanaTable.cellSet() ;
	    Iterator<Cell<String, String, String>> ana_iter = ana_Set.iterator();
	    while(ana_iter.hasNext()){
	    	logInfo(ana_iter.next());
	    	
	    }
	}
	public void follow()
	{
		logInfo("\n\n\n-----------开始计算follow--------------\n");
		Set<Entry<String, String>> set = LLmap.entrySet(); 
		Iterator<Entry<String, String>> it = set.iterator();
		while(it.hasNext())
		{
			
			Map.Entry<String, String> entry=(Map.Entry<String, String>)it.next();
			values.add(entry.getKey()+" "+entry.getValue());
		}	
		for (String str :nonterminals)
		{
			if (LLanaTable.get(str, "ε")!=null)
			{
				logInfo("\n\n\n\n\n"+str+"包含ε，开始计算follow");
				mothers = new ArrayList<String>();
			    follow(str,str);
			    print_follow(str);
			}		
		}
		
		/*****************************************/
		predicLLanaTable.putAll(LLanaTable);
		//if(predicLLanaTable.equals(LLanaTable))logInfo("赋值成功!!!!!!!!");
		for (String str :nonterminals)
		{
			if (predicLLanaTable.get(str, "ε")==null)
			{
				mothers = new ArrayList<String>();
			    predicfollow(str,str);
			}
		}	
		
		
		
		
	}
	
	
	private  void predicfollow (String str,String mother){
    	logInfo(mother+"\n");
		logInfo("\n\n\n\n\n" +"开始计算follow("+str+")");
		int count = 1;
       for (int i = 0;i < values.size() ;i++){ 
    	  //values数组包含了生成式全部
    	   String cut = values.get(i).substring(values.get(i).indexOf(" ")+1, values.get(i).length());
    	   //cut为右半部分
    	   boolean is_equal = false ;
    	   for (String tmp : cut.split(" "))
    	   {
    		   if (tmp.equals(str))
    		   {
    			   is_equal = true;
    			   break;
    		   }
    	   }
    	   if (is_equal)
    	   {  
    		   logInfo("\n"+str+"----相关生成式"+count++);   
    		   logInfo(cut);
    		   String father = values.get(i).substring(0, values.get(i).indexOf(" "));
    		   String[] parts = cut.split(" ");
       		   for (int j = 0 ;j<parts.length ; j++)
    		   {
    			   if (parts[j].equals(str))
    			   {
    				   
    				  if (j==parts.length-1 )
    				  {
    					  if (!father.equals(mother )&& !mothers.contains(father))
  				    	{
  				    		mothers.add(father);
  				    	 predicfollow(father,mother);
  				    	}
  				    	else
  				    	 logInfo("非终结符计算自我的follow，此迭代终结");
    				  }
    				  else 
    				  {  
    				    int p = j; //这里不直接使用j，因为非终结符可能出现多次;
    				    p++;       //为了方便，直接默认间隔为一个空格，如果后面有要求可以在输入时便将所有多个空格间隔改为单空格,也可在此处更改
    				    String tmp = parts[p];
    				    if (tmp.equals("|"))
    				    {
    				    	if (!father.equals(mother )&& !mothers.contains(father))
    				    	{
    				    		mothers.add(father);
    				    	 logInfo("后面为空，开始计算follow("+father+")");  
    				    	 predicfollow(father,mother);//此处在计算compoundstmt的follow时会陷入死循环，因为Mother一直是compoundstmt
    				    	}
    				    	else
    				    	 logInfo("非终结符计算自我的follow，此迭代终结");
    				    }
    				    else 
    				    {
    				    	if (nonterminals.contains(tmp))
    				    	{
    				    		logInfo("后面为非终结符，将first("+tmp+")加入follow");
    				    		ArrayList<String> firsts = getfirst(tmp);
    				    		 for (String first : firsts)
    				    		 {
    				    			 if (!predicLLanaTable.contains(mother, first))
    				    			 {
    				    		      if(!first.equals("ε"))
    				    		      {
    				    			    predicLLanaTable.put(mother, first, mother+"-->synch");
    				    			    logInfo("将"+mother+"-->synch"+"加入到("+mother+","+first+")中");
    				    		      }
    				    			 }
    				    			 else
        				    		 {
        				    			 logInfo("此follow值已计算");
        				    		 }
    				    		 }
    				    	}
    				    	else
    				    	{
    				    		 String first  = tmp ;
    				    		 if (!predicLLanaTable.contains(mother,first))
				    			 {
    				    			 if(!first.equals("ε"))
    				    			 {
				    			       predicLLanaTable.put(mother, first, mother+"-->synch");
				    			       logInfo("将"+mother+"-->synch"+"加入到("+mother+","+first+")中");
    				    			 }
				    			 }
    				    		 else
    				    		 {
    				    			 logInfo("此follow值已计算");
    				    		 }
    				    	}
    				       
    				    }
    				    
    				  }
    			   }
    			   
    		   }
    		   
    	   }
    	   
    	   
       }
       
	}
	
	//father主持迭代，mother记录源头
	private  void follow (String str,String mother){
		
		int count=1;
		for (int i = 0;i < values.size() ;i++){ 
	    	  //values数组包含了生成式全部
	    	   String cut = values.get(i).substring(values.get(i).indexOf(" ")+1, values.get(i).length());
	    	   //cut为右半部分

	/*******************************changed*************************/
	    	   boolean is_equal = false ;
	    	   for (String tmp : cut.split(" "))
	    	   {
	    		   if (tmp.equals(str))
	    		   {
	    			   is_equal = true;
	    			   break;
	    		   }
	    	   }
	    	   if (is_equal)
	    	   {  
	logInfo("\n"+str+"----相关生成式"+count++);   
	    		   logInfo(cut);
	    		   String father = values.get(i).substring(0, values.get(i).indexOf(" "));
	    		   String[] parts = cut.split(" ");
	       		   for (int j = 0 ;j<parts.length ; j++)
	    		   {
	    			   if (parts[j].equals(str))
	    			   {
	    				   
	    				  if (j==parts.length-1 )
	    				  {
	    				   if (!father.equals(mother )&& !mothers.contains(father))
	  				    	{
	  				    		mothers.add(father);
	  				    	    logInfo("后面为空，开始计算follow("+father+")");  
	  				    	    follow(father,mother);
	  				    	}
	  				    	else
	  				    	 logInfo("非终结符计算自我的follow，此迭代终结");
	    				  }
	    				  else 
	    				  {  
	    				    int p = j; //这里不直接使用j，因为非终结符可能出现多次;
	    				    p++;       //为了方便，直接默认间隔为一个空格，如果后面有要求可以在输入时便将所有多个空格间隔改为单空格,也可在此处更改
	    				    String tmp = parts[p];
	    				    if (tmp.equals("|") || tmp.equals("ε"))
	    				    {
	    				    	if (!father.equals(mother )&& !mothers.contains(father))//changed
	    				    	{
	    				    	   mothers.add(father);
	    				    	    logInfo("后面为空，开始计算follow("+father+")");    
	    				    	    follow(father,mother);
	    				    	}
	    				    	else
	    				    	 logInfo("非终结符计算自我的follow，此迭代终结");
	    				    }
	    				    else 
	    				    {
	    				    	if (nonterminals.contains(tmp) )
	    				    	{
	    				    		logInfo("后面为非终结符，将first("+tmp+")加入follow");
	    				    		ArrayList<String> firsts = getfirst(tmp);
	    				    		if (firsts.contains("ε")) 
	    				    		{
	    				    			if (!father.equals(mother )&& !mothers.contains(father))//changed
	    	    				    	{
	    	    				    	   mothers.add(father);
	    	    				    	    logInfo("后面为空，开始计算follow("+father+")");    
	    	    				    	    follow(father,mother);
	    	    				    	}
	    	    				    	else
	    	    				    	 logInfo("非终结符计算自我的follow，此迭代终结");
	    				    			firsts.remove("ε");
	    				    		}
	    				    		 for (String first : firsts)
	    				    		 {
	    				    			 if (!LLanaTable.contains(mother, first))
	    				    			 {
	    				    			  LLanaTable.put(mother, first, mother+"-->ε");
	    				    			  logInfo("将"+mother+"-->ε"+"加入到("+mother+","+first+")中");
	    				    			 }
	    				    			 else
	        				    		 {
	        				    			 logInfo("此follow值已计算");
	        				    		 }
	    				    		 }
	    				    	}
	    				    	else
	    				    	{
	    				    		 String first  = tmp ;
	    				    		 if (!LLanaTable.contains(mother,first))
					    			 {
					    			   LLanaTable.put(mother, first, mother+"-->ε");
					    			   logInfo("将"+mother+"-->ε"+"加入到("+mother+","+first+")中");
					    			 }
	    				    		 else
	    				    		 {
	    				    			 logInfo("此follow值已计算");
	    				    		 }
	    				    	}
	    				       
	    				    }
	    				    
	    				  }
	    			   }
	    			   
	    		   }
	    		   
	    	   }
	    	   
	    	   
	       }
	       
       
	}
    public void print_follow(String ntm)
    {
    	Map<String,String>tmp = LLanaTable.rowMap().get(ntm);
    	Set<Entry<String, String>> set = tmp.entrySet(); 
		Iterator<Entry<String, String>> it = set.iterator();
		String res = "";
		while(it.hasNext())
		{
	      Map.Entry<String,String>king=it.next();
		  if ( king.getValue().equals(ntm+"-->"+"ε") && !king.getKey().equals("ε"))
		   {
			  res = res+king.getKey()+",";
		   }
		}	
    	logInfo("follow("+ntm+")=    "+res.substring(0,res.length()-1)+"\n");
    	
    }
	public void print_predictable()
	{
		Set<String>nonterminalset=predicLLanaTable.rowKeySet();
		row=nonterminals.size();
		column=terminals.size();
		List<String> productions=new ArrayList<>();
		for(int i=0;i<row*column;++i)
		{
			productions.add("");
		}
//		for(String str:nonterminals)
//			System.err.println(str);
		Iterator<String>nonit=nonterminalset.iterator();
		
		while(nonit.hasNext())
		{
			String nonterminal=nonit.next();
			logInfo(nonterminal+"   ");
			Map<String,String>terminalAndproduction=predicLLanaTable.row(nonterminal);
			Set<Entry<String,String>>tp=terminalAndproduction.entrySet();
			Iterator<Entry<String,String>>tpit=tp.iterator();
			while(tpit.hasNext())
			{
				Map.Entry<String, String> entry=(Map.Entry<String, String>)tpit.next();
				String terminal=entry.getKey();
				String production=entry.getValue();
				if(!terminal.equals("ε"))
				{
					productions.set(nonterminals.indexOf(nonterminal)*column+terminals.indexOf(terminal), production);
				}
				logInfo(terminal+" PRODUCTION: "+production+"   ");
				int arrowpos=production.indexOf("-->");
				String productionelem=production.substring(arrowpos+3, production.length());
				//log(productionelem+"    2222");
				String temp[]=new String[productionelem.length()];
				temp=productionelem.split(" ");
				//for(int i=0;i<temp.length;i++)
					//log(temp[i]+"22211");
			}
			logInfo("\n");
		}
	}
	public List<String> print_table()
	{
		Set<String>nonterminalset=LLanaTable.rowKeySet();
		row=nonterminals.size();
		column=terminals.size();
		List<String> productions=new ArrayList<>();
		for(int i=0;i<row*column;++i)
		{
			productions.add("");
		}
//		for(String str:nonterminals)
//			System.err.println(str);
		Iterator<String>nonit=nonterminalset.iterator();
		
		while(nonit.hasNext())
		{
			String nonterminal=nonit.next();
			logInfo(nonterminal+"   ");
			Map<String,String>terminalAndproduction=LLanaTable.row(nonterminal);
			Set<Entry<String,String>>tp=terminalAndproduction.entrySet();
			Iterator<Entry<String,String>>tpit=tp.iterator();
			while(tpit.hasNext())
			{
				Map.Entry<String, String> entry=(Map.Entry<String, String>)tpit.next();
				String terminal=entry.getKey();
				String production=entry.getValue();
				if(!terminal.equals("ε"))
				{
					productions.set(nonterminals.indexOf(nonterminal)*column+terminals.indexOf(terminal), production);
				}
				logInfo(terminal+" PRODUCTION: "+production+"   ");
				int arrowpos=production.indexOf("-->");
				String productionelem=production.substring(arrowpos+3, production.length());
				//log(productionelem+"    2222");
				String temp[]=new String[productionelem.length()];
				temp=productionelem.split(" ");
				//for(int i=0;i<temp.length;i++)
					//log(temp[i]+"22211");
			}
			logInfo("\n");
		}
		return productions;
	}
	
	public void analysis() throws IOException
	{
		//输入系统
		Scanner s = new Scanner(System.in); 
	     logInfo("请输入字符串：\n"); 
	    int linecount=1;//统计行数
	    String sentence="";
		while (true) 
		{
			String currentstr=s.nextLine();
			if (currentstr.equals("q")) break; 
			sentence = sentence+currentstr+'\n';
			linecount++;
		}
		sentence = sentence+"$";
		logInfo("将要分析的句子为:\n ");
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(sentence.getBytes(Charset.forName("utf8"))),Charset.forName("utf8")));
		String line;//每一行的句子
		ArrayList<String>analysisstr=new ArrayList<String>();
		while((line=br.readLine())!=null)
		{
			String []a=new String[line.length()];
			a=line.split(" ");
			for(int i=0;i<a.length;i++)
			{
				if(!a[i].trim().isEmpty())
					analysisstr.add(a[i]);
			}
		}
		for(int i=0;i<analysisstr.size();i++)
			logInfo(analysisstr.get(i)+"\n");
		List<Integer>tokenlinepos=new ArrayList<Integer>();
		for(int i=0;i<100;i++)
			tokenlinepos.add(i);
		analysis(analysisstr, tokenlinepos, null);       
	}
	public List<String> analysis(List<String> analysisstr, List<Integer> tokenlinepos, List<StackMessage> stackMessages)
	{
		List<String> errors=new ArrayList<>();
		Stack<String>oldstrstack=new Stack<String>();
		Stack<String>strstack=new Stack<String>();
		strstack.push("$");
		strstack.push("program");//非终结符栈
		
		logInfo("栈:                                 "+"输入:                                   "+"动作:                              \n");
		int linecount=1;
		int analysisId=0;
		String analysisElem=analysisstr.get(analysisId);
		boolean pop=true;
		boolean recover=false;//1220
		HashMap<String,Integer>jumpon=new HashMap<String,Integer>();//1220
		while(analysisId!=analysisstr.size()-1)
		{
			if(strstack.empty())
			{
				if(recover)
				{
					if(jumpon.containsKey(analysisElem))
					{
						analysisId++;
						analysisElem=analysisstr.get(analysisId);
						strstack=(Stack<String>) oldstrstack.clone();
						recover=false;
						System.out.println("recover the stack");
						errors.add("恢复栈状态，同时跳过当前字符!"+'\n');
					}
					else break;
				}
				break;
			}
			StackMessage stackMessage=new StackMessage("", "", "");
			StringBuilder sb=new StringBuilder();
			for(int x=0;x<strstack.size();x++)
			{
				sb.append(logInfo(strstack.get(x)));
			}
			stackMessage.setStack(sb.toString());
			logInfo("                           ");
			
			//输入信息
			sb=new StringBuilder();
			for(int x=analysisId;x<analysisstr.size();x++)
			{
				sb.append(logInfo(analysisstr.get(x)));                          
			}
			stackMessage.setInput(sb.toString());
			logInfo("                           ");
			
			String nonterminal=strstack.peek();
			if(!predicLLanaTable.containsRow(nonterminal))//栈顶有匹配的终结符时，弹出，并继续下一个字符的分析，若该终结符不匹配，亦是弹出
		     {
			   if(nonterminal.equals(analysisElem))
			  {
				stackMessage.setAction(matchaction(analysisElem));
				
				analysisId++;
				analysisElem=analysisstr.get(analysisId);
				strstack.pop();	
				stackMessages.add(stackMessage);
				//if(!LLanaTable.containsRow(strstack.peek()))oldstrstack=(Stack<String>) strstack.clone();
				//if(nonterminal.equals(";")||nonterminal.equals("{")||nonterminal.equals("}"))linecount++;
				continue;
			  }
			   else
			   {
				   if(pop)
				   {
					   strstack.pop();
					   stackMessage.setAction(jumptopTerminal(nonterminal));
					   stackMessages.add(stackMessage);
					   continue;
				   }
				  /* else 
				   {
					   int linepos=tokenlinepos.get(analysisId);
					   logInfo("第"+linepos+"行缺少"+nonterminal+'\n');
					   strstack.pop();
					   jumptopTerminal(nonterminal);
					   pop=true;
					   continue;
				   }*/
			   }
		     }
			Map<String,String>terminalAndproduction=predicLLanaTable.row(nonterminal);
			Set<Entry<String,String>>tp=terminalAndproduction.entrySet();
			Iterator<Entry<String,String>>tpit=tp.iterator();
			HashMap<String,String>cur_terminal_pro=new HashMap<String,String>();
			while(tpit.hasNext())//遍历当前的非终结符的产生式
			{
				Map.Entry<String, String> entry=(Map.Entry<String, String>)tpit.next();
				String terminal=entry.getKey();//first,follow
				String production=entry.getValue();////first,follow
				cur_terminal_pro.put(terminal, production);
				int arrowpos=production.indexOf("-->");
				String productionelem=production.substring(arrowpos+3, production.length());//截取产生式
				if(!analysisElem.equals(terminal))
				{
					if(!tpit.hasNext())//当遍历完当前非终结符的产生式还没有对应的终结符时，报错，并跳过当前的终结符
					{
						if(analysisElem.equals("int")||analysisElem.equals("real"))
							{
							logInfo("变量声明: "+analysisElem+"\n");
							stackMessage.setAction("变量声明: "+analysisElem);
							 analysisId++;
							 analysisElem=analysisstr.get(analysisId);
							}
						else
						{
							if(!terminals.contains(analysisElem))
							{
								linecount=tokenlinepos.get(analysisId);
								error(tokenlinepos.get(analysisId),analysisElem, errors);
							    analysisId++;
							    analysisElem=analysisstr.get(analysisId);
							    break;
							}
							else
							{
									//pop=false;
									String stackelem="";
									for(int x=strstack.size()-1;x>0;x--)
										{
											stackelem=strstack.get(x);
											if(!predicLLanaTable.containsRow(stackelem)&&!stackelem.equals(analysisElem))
											{			
												tokenlinepos.add(analysisId,tokenlinepos.get(analysisId-1));
												analysisstr.add(analysisId, stackelem); 
												break;
											}
										}
									if(cur_terminal_pro.containsKey(stackelem)&&!cur_terminal_pro.get(stackelem).equals("synch"))
									{
										if(!recover)
										{//有可能在添加后一直跳不过去，此时就要恢复栈，并向前进来分析一个
										     jumpon.put(analysisElem, analysisId);
										     oldstrstack=(Stack<String>) strstack.clone();
										     recover=true;
										}//
										int linepos=tokenlinepos.get(analysisId);
										analysisElem=analysisstr.get(analysisId);
										logInfo("第"+linepos+"行缺少"+stackelem+'\n');
										errors.add("第"+linepos+"行缺少"+stackelem+'\n');
										//error(tokenlinepos.get(analysisId),analysisElem, errors);
										break;
										//analysisId++;
										//analysisElem=analysisstr.get(analysisId);
									}
								else
								{
									linecount=tokenlinepos.get(analysisId);
									error(tokenlinepos.get(analysisId),analysisElem, errors);
								    analysisId+=2;
								    analysisElem=analysisstr.get(analysisId);
								    break;
								}
							}
						}
				   }
				}
				else
				{
					String topNonterminal=strstack.peek();
					strstack.pop();
					if(!productionelem.equals("synch"))
					{
					   String rightstr[]=new String[productionelem.length()];
					   rightstr=productionelem.split(" ");
					   for(int l=rightstr.length;l>0;l--)
						{
						    if(rightstr[l-1].equals("ε"))continue;
						    else strstack.push(rightstr[l-1]);
						}
					   stackMessage.setAction(outputaction(production));	
					break;
				  }
					else 
					{
						stackMessage.setAction(jumptopNonterminal(topNonterminal));
						break;
					}
				}
			}
			stackMessages.add(stackMessage);
		}
		StringBuilder sb=new StringBuilder();
		StackMessage stackMessage=new StackMessage("","","");
		for(int x=0;x<strstack.size();x++)
		{
			sb.append(logInfo(strstack.get(x)));
		}
		stackMessage.setStack(sb.toString());
		logInfo("                           ");
		
		//输入信息
		sb=new StringBuilder();
		for(int x=analysisId;x<analysisstr.size();x++)
		{
			sb.append(logInfo(analysisstr.get(x)));
		}
		stackMessage.setInput(sb.toString());
		logInfo("                           ");
		stackMessages.add(stackMessage);
		return errors;

		}
	
	
	protected String jumptop(String str)
    {
    	logInfo("栈顶之下的一个字符匹配跳过当前栈顶字符: "+str+"\n");
    	return "栈顶之下的一个字符匹配跳过当前栈顶字符: "+str;
    }
	protected String outputaction(String str)
	{
		logInfo("输出 "+str);
		logInfo("                              \n");
		solve(str);
		return "输出 "+str;
	}
	protected String matchaction(String str)
	{
		logInfo("匹配 "+str);
		logInfo("                              \n");
		solve(str);
		return "匹配 "+str;
	}
    protected void error(int i ,String str, List<String> errors)
    {
    	logErr("第"+i+"行"+str+" 出错，跳过当前字符\n");
    	errors.add("第"+i+"行"+str+" 出错，跳过当前字符");
    }
    
    final String logPath="d:/log/";
    final String infoLogPath=logPath+"info/";
    final String errLogPath=logPath+"err/";
    String logInfo(Object object)
    {
    	System.out.print(object);
    	return object.toString();
//	 	log(infoLogPath,"logInfo_",object.toString());    	
    }
    
    void logErr(Object object)
    {
	 	System.err.print(object);
	// 	log(errLogPath,"logErr_",object.toString());
    }
    
    void log(String pathName,String logName,String str)
    {
    	Date date=new Date();
	 	String filename = logName+date.getTime()/60000+".txt";
		File[] files=new File(pathName).listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if(null==name)
					return false;
				return !name.equals(filename);
				
			}
		});
		for(File f:files)
		{
			f.delete();
		}
		try {
			FileWriter fileWriter=new FileWriter(new File(pathName+filename),true);
			fileWriter.write(str);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public ArrayList<String>getGraphicList()
	{
		return saveout;
	}
    private void solve(String str)
	{
		if(str.contains("-->"))
			{
			int pos=str.indexOf("-->");
			StringBuffer stringBuffer = new StringBuffer(str);
	        stringBuffer.insert(pos+3, " ");
	        saveout.add("输出 "+stringBuffer.toString());
			}
		else saveout.add("匹配 "+str);
	}   
    

protected String jumptopTerminal(String str)
    {
    	logInfo("栈顶终结符: "+str+"不匹配，跳过"+'\n');
    	return "栈顶终结符: "+str+"不匹配，跳过";
    }
    protected String jumptopNonterminal(String str)
    {
    	logInfo("弹出栈顶非终结符!"+'\n');
    	return "弹出栈顶非终结符!";
    }
}

