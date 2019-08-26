package info.victor.jregex.grammar;

public class CharRangeRegexNode extends RegexNode {
    private Character from;
    private Character to;

    public CharRangeRegexNode(Character from, Character to) {
        this.from = from;
        this.to = to;
    }
}
