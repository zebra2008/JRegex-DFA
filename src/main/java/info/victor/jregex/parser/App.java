package info.victor.jregex.parser;

import info.victor.jregex.grammar.RegexNode;

public class App {
    public static void main(String[] args) {
        String regex  = "[^a]b+|c";
        RegexParser parser  = new RegexParser(regex);
        RegexNode node = parser.parseUnionExp();
        System.out.println("dsds");
    }
}
