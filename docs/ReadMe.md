# JRegex-DFA

java-written regex engine use DFA to match.

## EBNF


```bnf
regexp          =    unionexp
                ;

unionexp        =    concatexp | unionexp
                |    concatexp
                ;
          
concatexp       =    repeatexp concatexp
                |    repeatexp
                ;
                
repeatexp       =	charclassexp ?	(zero or one occurrence)	
                |	charclassexp *	(zero or more occurrences)	
                |	charclassexp +	(one or more occurrences)	
                |	charclassexp {n}	(n occurrences)	
                |	charclassexp {n,}	(n or more occurrences)	
                |	charclassexp {n,m}	(n to m occurrences, including both)	
                |	charclassexp
               
charclassexp    =	[ charclasses ]	(character class)	
                |	[^ charclasses ]	(negated character class)	
                |	simpleexp	
                
charclasses	    =	charclass charclasses		
                |	charclass		

charclass	    =	charexp - charexp	(character range, including end-points)	
                |	charexp		

simpleexp	    =	( unionexp )	(precedence override)
                |	.	(any single character，except \n)	
                |   charexp
               
charexp	        =	<Unicode character>	(a single non-reserved character)	
                |	\ <Unicode character> 	(a single character)
``` 

```bnf

<RE>	::=	<union> | <simple-RE>
<union>	::=	<RE> "|" <simple-RE>
<simple-RE>	::=	<concatenation> | <basic-RE>
<concatenation>	::=	<simple-RE> <basic-RE>
<basic-RE>	::=	<star> | <plus> | <elementary-RE>
<star>	::=	<elementary-RE> "*"
<plus>	::=	<elementary-RE> "+"
<elementary-RE>	::=	<group> | <any> | <eos> | <char> | <set>
<group>	::=	"(" <RE> ")"
<any>	::=	"."
<eos>	::=	"$"
<char>	::=	any non metacharacter | "\" metacharacter
<set>	::=	<positive-set> | <negative-set>
<positive-set>	::=	"[" <set-items> "]"
<negative-set>	::=	"[^" <set-items> "]"
<set-items>	::=	<set-item> | <set-item> <set-items>
<set-items>	::=	<range> | <char>
<range>	::=	<char> "-" <char>
```

## 参考资料

1. [perl regex bnf](https://www2.cs.sfu.ca/~cameron/Teaching/384/99-3/regexp-plg.html)