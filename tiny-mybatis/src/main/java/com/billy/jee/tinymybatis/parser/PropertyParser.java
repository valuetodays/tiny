package com.billy.jee.tinymybatis.parser;

import java.util.Map;


public class PropertyParser {

    private static String parse0(String string, String start, String end, Map<String, Object> variables) {
        VariableTokenHandler handler = new VariableTokenHandler(variables, start, end);
        GenericTokenParser parser = new GenericTokenParser(start, end, handler);
        return parser.parse(string);
    }


    public static String parseSharp(String string, Map<String, Object> variables) {
        return parse0(string, "#{", "}", variables);
    }

    public static String parse$(String string, Map<String, Object> variables) {
        return parse0(string, "${", "}", variables);
    }


}

class VariableTokenHandler {
    final private Map<String, Object> variables;
    final private String start;
    final private String end;

    public VariableTokenHandler(Map<String, Object> variables, String start, String end) {
        this.variables = variables;
        this.start = start;
        this.end = end;
    }

    public String handleToken(String content) {
        if (variables != null && variables.containsKey(content)) {
            Object o = variables.get(content);
            return o == null ? null : o.toString();
        }
        return start + content + end;
    }
}
