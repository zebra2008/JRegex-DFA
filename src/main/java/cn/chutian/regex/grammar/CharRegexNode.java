package cn.chutian.regex.grammar;

public class CharRegexNode extends CharClassRegexNode {
    private Character character;

    public CharRegexNode(Character character) {
        this.character = character;
    }
}
