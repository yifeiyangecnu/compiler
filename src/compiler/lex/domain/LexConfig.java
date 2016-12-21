package compiler.lex.domain;

import java.util.Arrays;

import compiler.lex.Exception.MyException;

public class LexConfig {
		static String[] keywords={"else","if","int","real","then","while"};
		static String[] operators={"+","-","*","/","=","==","<","<=",">",">=","!="};
		static String[] delimiters={"(",")","{","}",";"};
		final static String IDREGEX="A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z"
				+ "|a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z"
				+ "a*b*c*d*e*f*g*h*i*j*k*l*m*n*o*p*q*r*s*t*u*v*w*x*y*z*"
				+ "A*B*C*D*E*F*G*H*I*J*K*L*M*N*O*P*Q*R*S*T*U*V*W*X*Y*Z*0*1*2*3*4*5*6*7*8*9*";
		final static String INT_REGEX="0|1|2|3|4|5|6|7|8|90*1*2*3*4*5*6*7*8*9*";
		final static String REAL_REGEX=INT_REGEX+"."+INT_REGEX;
		final static String EXPONET_REGEX=INT_REGEX+"E|e+|-|"+INT_REGEX;
		final static String REAL_WITH_LEFT_EXPONET_REGEX=EXPONET_REGEX+"."+INT_REGEX;
		final static String REAL_WITH_RIGHT_EXPONET_REGEX=INT_REGEX+"."+EXPONET_REGEX;
		final static String REAL_WITH_BOTH_EXPONET_REGEX=EXPONET_REGEX+"."+EXPONET_REGEX;
		public static boolean isSeparate(String word)
		{
			return isKeywordOrOperatorOrDelimeiter(word)||isIntOrRealOrIdentifier(word);
		}
		
		public static  boolean isKeyWord(String word)
		{
			Arrays.sort(keywords);
			return Arrays.binarySearch(keywords, word)>=0;
		}
		
		public static boolean isOpeartor(String word)
		{
			Arrays.sort(operators);
			return Arrays.binarySearch(operators, word)>=0;
		}
		
		public static boolean isDelimiter(String word)
		{
			Arrays.sort(delimiters);
			return Arrays.binarySearch(delimiters, word)>=0;
		}
		
		public static boolean isIdentifier(String word)
		{
			return 	DFA.match(word, IDREGEX);
		}
		public static boolean isInt(String word) 
		{
			return 	DFA.match(word, INT_REGEX);

		}
		
		public static boolean isReal(String word)
		{
			return DFA.match(word, REAL_REGEX)
					||DFA.match(word, REAL_WITH_BOTH_EXPONET_REGEX)
					||DFA.match(word, REAL_WITH_LEFT_EXPONET_REGEX)
					||DFA.match(word, REAL_WITH_RIGHT_EXPONET_REGEX);
		}
		
		public static boolean isKeywordOrOperatorOrDelimeiter(String word)
		{
			return isOpeartor(word)||isDelimiter(word)||isKeyWord(word);
		}
		public static boolean isIntOrRealOrIdentifier(String word) 
		{
			return (isInt(word)||isReal(word)||isIdentifier(word))&&!isKeywordOrOperatorOrDelimeiter(word);
		}
		public static void main(String args[]) throws MyException
		{
//			println(isSeparate("i;"));
//			println(isKeywordOrOperatorOrDelimeiter("i;"));
//			println(isIntOrRealOrIdentifier("i;"));
//			println(isInt("i;"));
//			//isKeyWord≤‚ ‘
//			println(isKeyWord("real"));
//			println(!isKeyWord("reall"));
//			println(isKeyWord("int"));
//			println(!isKeyWord("adsfd"));
//			//isOpeartor≤‚ ‘
//			println(isOpeartor("+"));
//			println(isOpeartor("-"));
//			println(!isOpeartor("++"));
//			//isDelimiter≤‚ ‘
//			println(isDelimiter(";"));
//			//isReal≤‚ ‘
			println(isIdentifier("a"));
//			println(isReal("1.5")); 
//			println(isReal("1555.005669"));
//			println(isReal("2E6.2"));
//			//isExponent≤‚ ‘
		}
		
		
		static void println(Object str)
		{
			System.out.println(str);

		}
}
