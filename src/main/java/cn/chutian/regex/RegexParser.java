package cn.chutian.regex;

import cn.chutian.regex.grammar.*;
import cn.chutian.regex.grammar.charclass.AnyCharNotNewLine;
import cn.chutian.regex.grammar.CharRangeRegexNode;
import cn.chutian.regex.grammar.CharRegexNode;
import cn.chutian.regex.grammar.ComplementRegexNode;

public class RegexParser {
    /**
     * 正则字符串
     */
    private String regex;
    /**
     * 当前字符指针位置
     */
    int position;

    public RegexParser(String regex) {
        this.regex = regex;

    }

    // ++++++++++++++++++++++++++++ static ++++++++++++++++++++++++++++++

    /**
     * 构造 “分支” 语法节点
     * @param exp1
     * @param exp2
     * @return
     */
    static RegexNode onUnion(RegexNode exp1, RegexNode exp2) {
        return new UnionRegexNode(exp1,exp2);
    }

    /**
     * 构造 “连接” 语法节点
     * @param exp1
     * @param exp2
     * @return
     */
    static RegexNode onConcatenation(RegexNode exp1, RegexNode exp2) {
        return new ConcatenationRegexNode(exp1,exp2);
    }

    /**
     * 构造 “重复” 语法节点
     * @param exp
     * @param min 至少 min 次重复
     * @return
     */
    static RegexNode onRepeatMinly(RegexNode exp,int min){
        return new RepeatRegexNode(min,-1,exp);
    }

    /**
     * 构造 “重复” 语法节点，min <= max
     * @param exp
     * @param min 至少 min 次重复
     * @param max 至多 max 次重复
     * @return
     */
    static RegexNode onRepeat(RegexNode exp,int min,int max) {
        return new RepeatRegexNode(min,max,exp);
    }

    /**
     * 构造 字符组 a-z 0-9
     * @param from
     * @param to
     * @return
     */
    static RegexNode onCharRange(char from, char to) {
        return new CharRangeRegexNode(from,to);
    }

    /**
     * 构造 字符
     * @param c
     * @return
     */
    static RegexNode onChar(char c) {
        return new CharRegexNode(c);
    }

    /**
     * 构造 补集
     * @param exp
     * @return
     */
    static RegexNode onComplement(RegexNode exp) {
        return new ComplementRegexNode(exp);
    }

    /**
     * 构造 "." ,匹配除 "\n"外所有字符
     * @return
     */
    static RegexNode onAnyCharNotNewLine() {
        return new AnyCharNotNewLine();
    }

    // ++++++++++++++++++++++++++++ parser functions ++++++++++++++++++++++++

    /**
     * 处理语法:分支
     * @return
     */
    final RegexNode parseUnionExp() {
        RegexNode node = parseConcatExp();
        if (more() && match('|')){
            node = onUnion(node,parseUnionExp());
        }

        return node;
    }

    /**
     * 处理语法: 连接
     * @return
     */
    final RegexNode parseConcatExp() {
        RegexNode node = parseRepeatExp();
        if (more() && !peek(")|")){
            node = onConcatenation(node,parseConcatExp());
        }
        return node;
    }

    /**
     * 处理语法: 重复
     * @return
     */
    final RegexNode parseRepeatExp() {
        RegexNode node = parseCharClassExp();
        while (peek("?*+{")) {
            if (match('?'))
                node = onRepeat(node,0,1);
            else if (match('*'))
                node = onRepeatMinly(node,0);
            else if (match('+'))
                node = onRepeatMinly(node, 1);
            else if (match('{')) {
                int start = position;
                while (peek("0123456789")) {
                    next();
                }
                if (start == position)
                    throw new IllegalArgumentException("integer expected at position " + position);
                int min = Integer.parseInt(regex.substring(start, position));
                int max = -1;
                if (match(',')) {
                    start = position;
                    while (peek("0123456789")) {
                        next();
                    }
                    if (start != position)
                        max = Integer.parseInt(regex.substring(start, position));
                } else {
                    max = min;
                }
                if (!match('}'))
                    throw new IllegalArgumentException("expected '}' at position " + position);
                if (max == -1) {
                    node = onRepeatMinly(node, min);
                }
                else {
                    node = onRepeat(node, min, max);
                }
            }
        }
        return node;
    }

    /**
     * 处理语法: 字符
     * @return
     */
    final RegexNode parseCharClassExp(){
        if (match('[')) {
            boolean negate = false;
            if (match('^')) {
                negate = true;
            }
            RegexNode e = parseCharClasses();
            if (negate) {
                e =onComplement(e);
            }
            if (!match(']')) {
                throw new IllegalArgumentException("expected ']' at position " + position);
            }
            return e;
        } else
            return parseSimpleExp();
    }

    final RegexNode parseSimpleExp() throws IllegalArgumentException {
        if (match('.')){
            return onAnyCharNotNewLine();
        } else if (match('(')) {
            if (match(')')) {
                throw new IllegalArgumentException("expected regex expression before ')' at position " + position);
            }
            RegexNode e = parseUnionExp();
            if (!match(')'))
                throw new IllegalArgumentException("expected ')' at position " + position);
            return e;
        }else
            return onChar(parseCharExp());
    }

    /**
     * 处理语法: 字符类
     * @return
     */
    final RegexNode parseCharClasses() {
        RegexNode e = parseCharClass();
        while (more() && !peek("]")) {
            e = onUnion(e, parseCharClass());
        }
        return e;
    }

    /**
     * 处理字符类内部语法
     * @return
     */
    final RegexNode parseCharClass() {
        char c = parseCharExp();
        if (match('-')) {
            if (peek("]")) {
                return onUnion(onChar(c), onChar('-'));
            }
            else {
                return onCharRange(c, parseCharExp());
            }
        }
        else {
            return onChar(c);
        }
    }

    /**
     * 处理 普通字符，和转义字符
     * @return
     */
    final char parseCharExp() {
        match('\\');
        return next();
    }

    // +++++++++++++++++++++++++++ char reader function ++++++++++++++++++++++++++++
    /**
     * 判断此时的字符是否满足条件,若满足，指针前进
     * @param c
     * @return
     */
    private boolean match(char c) {
        if (position >= regex.length())
            return false;
        if (regex.charAt(position) == c) {
            position++;
            return true;
        }
        return false;
    }

    /**
     * 仍有未处理的字符
     * @return
     */
    private boolean more() {
        return position < regex.length();
    }

    /**
     * 判断此时的字符是否在情况中，指针不变
     * @param s
     * @return
     */
    private boolean peek(String s) {
        return more() && s.indexOf(regex.charAt(position)) != -1;
    }

    /**
     * 获取下一个字符，如果到达EOF，抛出异常 IllegalArgumentException
     * @return
     * @throws IllegalArgumentException
     */
    private char next() throws IllegalArgumentException {
        if (!more())
            throw new IllegalArgumentException("unexpected end-of-string");
        return regex.charAt(position++);
    }

}
