package cn.chutian.regex.grammar;


public class ComplementRegexNode extends RegexNode {
    private RegexNode regexNode;

    public ComplementRegexNode(RegexNode regexNode) {
        this.regexNode = regexNode;
    }
}
