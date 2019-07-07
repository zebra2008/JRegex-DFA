package cn.chutian.regex.grammar;

public class RepeatRegexNode extends RegexNode{

    /** exp     min   max
     *  ?        0     1
     *  +        1     -1 (without ending)
     *  *        0     -1 (without ending)
     *  {n}      n     n
     *  {n,m}    n     m
     *  {n,}     n     -1 (without ending)
     */
    private int min;
    private int max;
    private RegexNode inner;

    public RepeatRegexNode(int min, int max, RegexNode inner) {
        this.min = min;
        this.max = max;
        this.inner = inner;
    }
}
