package org.scijava.parse;

public class Main {

    public static void main(String... args) {
        String exp = "«слово && о || fd» / 5";
        SyntaxTree tree = new ExpressionParser().parseTree(exp);
        System.err.println(tree.toString());
    }
}
