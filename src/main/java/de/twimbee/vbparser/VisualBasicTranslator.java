package de.twimbee.vbparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisualBasicTranslator {

    private static final String LINE_BREAK = System.getProperty("line.separator");

    private Map<String, String> replaces = new HashMap<>();
    private List<Character> noSemicolon = new ArrayList<>();
    private Map<String, Translator> translator = new HashMap<>();

    public VisualBasicTranslator() {
        replaces.put(" New ", " new ");
        replaces.put("End Class", "}");
        replaces.put("End Sub", "}");
        replaces.put("End Function", "}");
        replaces.put("End Namespace", "");
        replaces.put("Public ", "public ");
        replaces.put("Private ", "private ");
        replaces.put("Class ", "class ");
        replaces.put(" Property ", " ");
        replaces.put("Dim ", "");
        replaces.put("Return ", "return");
        replaces.put("Throw ", "throw ");
        replaces.put(" & ", " + ");
        replaces.put("Imports ", "import ");
        noSemicolon.add('{');
        noSemicolon.add('}');
        translator.put("Public Class ", new Translator() {
            @Override
            public String translate(String line) {
                return line + " {";
            }
        });
        translator.put(" As New ", new Translator() {
            @Override
            public String translate(String line) {
                String[] firstSplit = line.split(" As New ");
                String type = firstSplit[1].substring(0, firstSplit[1].indexOf("("));
                String variableName = firstSplit[0].trim();
                return type + " " + variableName + " = new " + firstSplit[1];
            }
        });
        translator.put(" As ", new Translator() {
            @Override
            public String translate(String line) {
                String[] firstSplit = line.split(" As ");
                String type = firstSplit[1].split("[ (]")[0];
                String variableName = firstSplit[0].trim();
                String modifiers = "";
                if (variableName.contains(" ")) {
                    int splitPoint = variableName.lastIndexOf(" ");
                    variableName = toCamelCase(variableName.substring(splitPoint).trim());
                    modifiers = firstSplit[0].trim().substring(0, splitPoint).trim() + " ";
                }
                String rest = firstSplit[1];
                if (rest.contains(" = New")) {
                    String[] restParts = rest.split(" = New");
                    rest = restParts[0].replace(type, "") + " = New" + restParts[1];
                } else {
                    rest = rest.replace(type, "");
                }
                return modifiers + type + " " + variableName + rest;
            }
        });
    }

    protected String toCamelCase(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    public String translateClass(String visualBasicClazz) throws IOException {
        StringBuilder javaClazz = new StringBuilder();
        String[] lines = visualBasicClazz.split(LINE_BREAK);
        for (String line : lines) {
            javaClazz.append(translateLine(line)).append(LINE_BREAK);
        }
        return javaClazz.toString();
    }

    public String translateLine(String line) {
        int whiteSpaceCount = line.indexOf(line.trim());
        String translated = line;
        for (String match : translator.keySet()) {
            if (translated.contains(match)) {
                translated = translator.get(match).translate(translated);
            }
        }
        for (String search : replaces.keySet()) {
            String replace = replaces.get(search);
            translated = translated.replace(search, replace);
        }
        return prependWhitespace(whiteSpaceCount, addSemicolon(translated));
    }

    private String prependWhitespace(int whiteSpaceCount, String string) {
        return String.join("", Collections.nCopies(whiteSpaceCount, " ")) + string;
    }

    private String addSemicolon(String line) {
        if (line.length() > 0) {
            if (noSemicolon.contains(line.charAt(line.length() - 1))) {
                return line;
            } else {
                return line + ";";
            }
        } else {
            return line;
        }
    }

}
