# JRegex-DFA

java-written regex engine use DFA to match.

## EBNF


```bnf
regexp          =    unionexp

unionexp        =    concatexp | unionexp (union)	
                |    concatexp
          
concatexp       =    repeatexp concatexp	(concatenation)	
                |    repeatexp
                
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
java-written regex engine use DFA to match.