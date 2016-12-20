package compiler.lex.domain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.omg.CORBA.portable.InputStream;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;


 /*temp.add("Êä³ö program--> compoundstmt");
   temp.add("Êä³ö compoundstmt--> { stmts }");
   temp.add("Æ¥Åä {");
   temp.add("Êä³ö stmts--> stmt stmts");
   temp.add("Êä³ö stmt--> assgstmt");
   temp.add("Êä³ö assgstmt--> ID = arithexpr ;");
   temp.add("Æ¥Åä ID");
   temp.add("Æ¥Åä =");
   temp.add("Êä³ö arithexpr--> multexpr arithexprprime");
   temp.add("Êä³ö multexpr--> simpleexpr multexprprime");
   temp.add("Êä³ö simpleexpr--> NUM");
   temp.add("Æ¥Åä NUM");
   temp.add("Êä³ö arithexprprime--> #");
   temp.add("Æ¥Åä ;");
   temp.add("Êä³ö stmts-->#");
   temp.add("Æ¥Åä }");*/

public class Graphic
{  
  public Graphic(String path_exe ,ArrayList<String> temp )
  {
	  this.temp = temp;
	  this.path_exe = path_exe;
  }
  private static ArrayList<String> temp;
  private static String path_exe;
  public static String path;
  public static FileOutputStream out ;
  public static ArrayList<node>nodes = new ArrayList<node>();
  public static class node
  {
	  int no;
	  String name;
	  node(int A,String B)
	  {
		  no = A;
		  name = B;
		  
	  }
  }
  public static void getmain() throws IOException
  {
	  File directory = new File(".");
	   path = directory.getCanonicalPath();
	   path +="\\LLtree.txt";
	   File file =new File(path);  
	   if  (!file .exists()  && !file .isDirectory())    
	   {     
	       file.createNewFile();
	   }
	   out = new FileOutputStream(path, true);
	   {//Çå¿Õ
	    FileOutputStream clear = new  FileOutputStream(path, false) ;
	    StringBuffer sb = new StringBuffer();
		sb.append("" + System.getProperty("line.separator"));
		clear.write(sb.toString().getBytes("utf-8")); 
	   }
	  fw("digraph g {    ");
   nodes.add(new node(0,"program"));
   fwnode(0,"program");
   int node_count = 1;
   Queue<Integer> back = new  LinkedList<Integer>();
   int max = 0;
   int now = 0;
   boolean  leafover = false ;
   for (int i = 0 ; i < temp.size() ; i++)
   {
	    String str = temp.get(i);
	    String[] tmp = str.split(" ");
	    int tokens = tmp.length;
	
	    if (str.contains("Êä³ö"))
	    {
	    	
	      for (int j = 2 ; j < tokens ; j++)
	      {
	    	  fwnode(node_count,tmp[j]);
	    	  nodes.add(new node(node_count++,tmp[j]));
	    	 
	      }
	    
	    }  
	    else if(str.contains("Æ¥Åä"))
	    {
	    	 fwnode(node_count,tmp[1]);
	    	 nodes.add(new node(node_count++,tmp[1]));
	    }
	   
   }
   for (int i = 0 ; i < temp.size() ; i++)
   {
	    String str = temp.get(i);
	    String[] tmp = str.split(" ");
	    int tokens = tmp.length;
	    if (str.contains("Êä³ö"))
	    {
	    	tmp[1]=tmp[1].substring(0, tmp[1].length()-3);
	  
	    	int no = gn_del(tmp[1]);
	  
	    	 for (int j = 2 ; j < tokens ; j++)
		      {
	    		   fwline(no,gn_keep(tmp[j]));
		      }
	    	
	    
	    }  
	    else if(str.contains("Æ¥Åä"))
	    {
	      int tint = gn_del(tmp[1]);
	      
	      int tint2 = gn_del(tmp[1]);

	      fwline(tint,tint2);
	      
	    }
	   
   }
  
   
  fw("}"); 
  String location[] = {path_exe,path};
  jout(path_exe);
  
  Process process = Runtime.getRuntime().exec(location);
  
  }
  public static int gn_del(String str)
  {
	  int Nlength = nodes.size();
	  
	  for (int i = 0 ; i < Nlength ; i++)
	  {
		  if (nodes.get(i).name.equals(str))
		  {
			  int res = nodes.get(i).no;
			  nodes.remove(i);
			  return res;
			  
			  
		  }
	  }
	  return -1;
  }
  public static int gn_keep(String str)
  {
	  int Nlength = nodes.size();
	  
	  for (int i = 0 ; i < Nlength ; i++)
	  {
		  if (nodes.get(i).name.equals(str))
		  {
			  int res = nodes.get(i).no;
			  return res;		  		  
		  }
	  }
	  return -1;
  }
  public static void fw(String cmd ) throws UnsupportedEncodingException, IOException
  {
	    StringBuffer sb = new StringBuffer();
		sb.append(cmd + System.getProperty("line.separator"));
		out.write(sb.toString().getBytes("utf-8")); 
	   
  }
  public static void fwnode(int no ,String name) throws UnsupportedEncodingException, IOException
  {
	  String str = "node"+no+"[label = "+"\""+name+"\""+"];";
	  fw(str);
  }
  
  public static void fwline(int no1 ,int no2) throws UnsupportedEncodingException, IOException
  {
	String str  ="\"node"+no1+"\" -> \"node"+no2+"\";";        
	fw(str);
  }
  public static void jout(Object a){
		  System.out.println(a);
	  }
  
 
}
