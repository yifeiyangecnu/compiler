package compiler.lex.domain;

import java.util.HashMap;
import java.util.Map;

public class DFA {
	final static String ID_REGEX="A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z"
			+ "|a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z"
			+ "a*b*c*d*e*f*g*h*i*j*k*l*m*n*o*p*q*r*s*t*u*v*w*x*y*z*"
			+ "A*B*C*D*E*F*G*H*I*J*K*L*M*N*O*P*Q*R*S*T*U*V*W*X*Y*Z*0*1*2*3*4*5*6*7*8*9*";
	final static String INT_REGEX="0|1|2|3|4|5|6|7|8|90*1*2*3*4*5*6*7*8*9*";
	final static String REAL_REGEX=INT_REGEX+"."+INT_REGEX;
	final static String EXPONET_REGEX=INT_REGEX+"E|e+|-|"+INT_REGEX;
	final static String REAL_WITH_LEFT_EXPONET_REGEX=EXPONET_REGEX+"."+INT_REGEX;
	final static String REAL_WITH_RIGHT_EXPONET_REGEX=INT_REGEX+"."+EXPONET_REGEX;
	final static String REAL_WITH_BOTH_EXPONET_REGEX=EXPONET_REGEX+"."+EXPONET_REGEX;
	final static int STATES_OFFSET=1000;//用于有括号的闭包，此时只是为保证状态编号唯一
		public static void main(String[] args)
		{
			log(match("ababc","(ab)*c"));
			log(match("ababc","(ab)*c(ab)*"));
			log(match("abc","abc"));
			log(match("abdgh","ab*c*d|e|fgh"));
			log(match("abc",ID_REGEX));
			log(match("a1Dbc1",ID_REGEX));
			log(!match("1abc",ID_REGEX));
			log(match("123",INT_REGEX));
			log(!match("a123",INT_REGEX));
			log(match("12.3",REAL_REGEX));
			log(!match("123",REAL_REGEX));
			log(match("2e+3",EXPONET_REGEX));
			log(!match("2e+-3",EXPONET_REGEX));
			log(match("2e+3.3",REAL_WITH_LEFT_EXPONET_REGEX));
			log(match("2e+3.3E-4",REAL_WITH_BOTH_EXPONET_REGEX));
			log(match("23.3E-4",REAL_WITH_RIGHT_EXPONET_REGEX));
		}
		/**
		 * 正则匹配
		 * 可匹配 形如： abc   a|b|c   a*b* 三种的任意组合形式， 例如：a|bc*d 但不支持括号
		 * 同时支持形如 (abc)*的特殊处理，此时括号不能嵌套，输出的状态编号不准确，但是符合自定义规则
		 * @param str
		 * @param regex
		 * @return
		 */
		public static boolean match(String str,String regex)
		{
			Map<StateTrans,Integer> dfa=new HashMap<>();
			int startState=constructDfa(regex, dfa);
			int statecount=-startState;
			int terminalState=0;
			for(int i=0;i<str.length();++i)
			{
				StateTrans stateTrans=new StateTrans(str.charAt(i),startState);
				if(!dfa.containsKey(stateTrans))
				{
					return false;
				}
				else
				{
					startState=dfa.get(stateTrans);
				}
			}
			if(startState==terminalState)
			{
				printDfa(dfa, statecount);
				return true;
			}
			else
			{
				return false;
			}
		}
		
		
		private static void printDfa(Map<StateTrans,Integer> dfa, int statecount)
		{
			System.out.printf("开始状态为 0 结束状态为 %d\n",statecount);
			dfa.keySet().stream().sorted().forEach(arg0->{
				System.out.printf("从状态 %d 输入 %c 到达 状态 %d \n", statecount+arg0.state,arg0.input,statecount+dfa.get(arg0).intValue());
			});
		}
		private static int constructDfa(String regex,Map<StateTrans,Integer> dfa)
		{
			int state=0;
			int len=regex.length();
			if(regex.charAt(len-1)=='|')
			{
				return -1;//非法的正则
			}
			for(int pos=regex.length()-1;pos>=0;--pos)
			{
				if(regex.charAt(pos)=='*')
				{
					if(--pos<0)
					{
						return -1;
					}
					if(regex.charAt(pos)==')')
					{
						int temp=state+STATES_OFFSET;
						int count=0;//括号内字符数
						while(regex.charAt(--pos)!='(')
						{
							++count;
						}
						dfa.put(new StateTrans(regex.charAt(pos+1), state),(temp));
						for(int i=2;i<count;i++)
						{
							dfa.put(new StateTrans(regex.charAt(pos+i), temp),(temp++));
						}
						if(count>=2)
						{
							dfa.put(new StateTrans(regex.charAt(pos+count),temp),state);
						}
					}
					else
					{
						dfa.put(new StateTrans(regex.charAt(pos), state), state);
					}
				}
				else if(regex.charAt(pos)=='|')
				{
					++state;
				}
				else
				{
					dfa.put(new StateTrans(regex.charAt(pos), state-1), state--);
				}
			}
			return state;
		}
		public static void log(Object object)
		{
			System.out.println(object);
		}
}
