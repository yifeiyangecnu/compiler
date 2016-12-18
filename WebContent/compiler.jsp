<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
  import="java.util.List"    
  import="compiler.lex.domain.Output"
  import="compiler.lex.domain.TokenColorType"
  import="compiler.lex.domain.LexError"
  import="compiler.lex.domain.Symbol"
  import="compiler.lex.domain.StackMessage"
  %>
<!-- <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">  -->
<!-- <html lang="en"    class="no-js" > -->
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>编译器</title>
<link rel="stylesheet" href="css/reset.css">
<link rel="stylesheet" href="css/style.css">
<style type="text/css">
table.gridtable {
	font-family: verdana,arial,sans-serif;
	font-size:11px;
	color:#333333;
	border-width: 1px;
	border-color: #666666;
	border-collapse: collapse;
}
table.gridtable th {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #666666;
	background-color: #dedede;
}
table.gridtable td {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #666666;
	background-color: #ffffff;
}
</style>
<script src="js/modernizr.js"></script>
					  <%List<Symbol> symbols=(List<Symbol>)request.getAttribute("symbols") ;
    List<Output> outputs=(List<Output>)request.getAttribute("outputs");
    List<LexError> errors=(List<LexError>)request.getAttribute("errors");
    List<String> productions=(List<String>)request.getAttribute("productions");
    List<String> terminals=(List<String>)request.getAttribute("terminals");
    List<String> nonterminals=(List<String>)request.getAttribute("nonterminals");
    List<String> gramErrors=(List<String>)request.getAttribute("gramErrors");
    List<StackMessage> stackMessage=(List<StackMessage>)request.getAttribute("stackMessage");
    Integer row=(Integer)request.getAttribute("row");
    Integer column=(Integer)request.getAttribute("column");
%>
</head>
<body>
<main>
	<div class="cd-image-block">
		<ul class="cd-images-list">
			<li class="is-selected">
				<a href="#0">
					<h2>词法分析</h2>
					 
    <br><br>
    	   <%
    int i=0;
    for(Output output:outputs)
    {%>
    <%if(i!=output.getLineNumber()) {%>
    <br><br>
    <span><%= output.getLineNumber()+"\t" %></span><%} %>
    <span  style=<%="\"color:"+output.getTokenColor().getColorStr()+"\""  %>><%= output.getTokenName() %></span>
<%   
	i=output.getLineNumber();
    }%>
    	
    <%if(!errors.isEmpty()) %>
    <%{ %>
    	<h3>共有<span><%=errors.size() %></span>处词法错误</h3>
    	<%} %>
    <% for(LexError error:errors)
    {%>
			<h3>第<span><%=error.getLineNumber() %></span>行<span><%=error.getInvailedWord() %></span>有词法错误，不能识别</h3>
   <% }%> 
				</a>
			</li>
			<li>
				<a href="#0">
					<h2>语法分析</h2>
	<%
     i=0;
    for(Output output:outputs)
    {%>
    <%if(i!=output.getLineNumber()) {%>
    <br><br>
    <span><%= output.getLineNumber()+"\t" %></span><%} %>
    <span  style=<%="\"color:"+output.getTokenColor().getColorStr()+"\""  %>><%= output.getTokenName() %></span>
<%   
	i=output.getLineNumber();
    }%>
    <br><br>
				</a>
			
			</li>
		
			
		</ul>
	</div>
	<div class="cd-content-block">
		<ul>
			<li class="is-selected"  display:block>
				<div>
					<h2>编译器之词法分析</h2>
					<p>
						<div style="text-align:center;margin:50px 0; font:normal 14px/24px 'MicroSoft YaHei';">
							<p>嵌入式三友</p>
						</div>
					</p>
					<p>
					词法分析阶段	Symbol如下：
					</p>
					<p>

    <center>
<%if(null!=symbols)
{%>
<table  class="gridtable"   >
<tr><th >类型</th><th>值</th><th>行号</th><th>列号</th></tr>
     <%for(Symbol symbol:symbols){
    %>
       <tr>
       <td><%=symbol.getTokenType()%></td>
       <td><%=symbol.getAttributevalue() %></td>
       <td><%=symbol.getLineNumber() %></td>
       <td><%=symbol.getLinePostion() %></td>
        <%} %>
        </tr>
 </table>
<%} else{%>
<h3>有词法错误</h3>
<%} %>
    </center>

				</div> 
			</li>
			<li>
				<div >
    <%if(!gramErrors.isEmpty()) %>
    <%{ %>
    	<h3>共有<span><%=gramErrors.size() %></span>处语法错误</h3>
    	<%} %>
    <% for(String error:gramErrors)
    {%>
			<h3><%=error %></h3>
   <% }%>
				</div> 
			</li>
			
		</ul>
		<button class="cd-close">关闭</button>
	</div>
	<ul class="block-navigation">
		<li><button class="cd-prev inactive">上一页</button></li>
		<li><button class="cd-next">下一页</button></li>
	</ul>
</main>
<table class="gridtable">
	<tr>
	<th>非终结符</th>
	<%for(int c=0;c<column;++c)
			{ %>
				<th><%=terminals.get(c) %></th>
			<%} %>
	</tr>
			<%for(int r=0;r<row;++r)
			{ %>
				<tr>
				<th><%=nonterminals.get(r) %></th>;
				<%for(int c=0;c<column;++c)
				{ %>
					<th><%=productions.get(r*column+c) %></th>;
				<%} %>
				</tr>
			<%} %>
			</table>	
			<h2>语法栈:</h2>
<%if(null!=stackMessage)
{%>
<table  class="gridtable"   >
<tr><th>栈</th><th>输入</th><th>动作</th></tr>
     <%for(StackMessage message:stackMessage){
    %>
       <tr>
       <td><%=message.getStack()%></td>
       <td><%=message.getInput() %></td>
       <td><%=message.getAction() %></td>
        <%} %>
        </tr>
 </table>
<%} %>
<script src="js/jquery-2.1.4.js"></script>
<script src="js/main.js"></script>
<script src="adv-banners/ads.js"></script>
</body>
</html>
