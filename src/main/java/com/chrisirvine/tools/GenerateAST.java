package com.chrisirvine.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GenerateAST {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");

            System.exit(64);
        }

        var outputDirectory = args[0];

        defineAST(outputDirectory, "Expr", Arrays.asList(
                "Binary : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal : Object value",
                "Unary : Token operator, Expr right"
        ));
    }

    private static void defineAST(String outputDirectory, String baseName, List<String> types) throws IOException {
        var path = String.format("%s/%s.java", outputDirectory, baseName);

        try (var writer = new PrintWriter(path, StandardCharsets.UTF_8)) {
            writer.println("package com.chrisirvine.lox;");
            writer.println();
            writer.println("import java.util.List;");
            writer.println();
            writer.println(String.format("abstract class %s {", baseName));

            for (String type : types) {
                var segments = type.split(":");
                var className = segments[0].trim();
                var fields = segments[1].trim();

                defineType(writer, baseName, className, fields);
            }

            writer.println("}");
        }
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fields) {
        writer.println(String.format("\tstatic class %s extends %s {", className, baseName));
        writer.println(String.format("\t\t%s(%s) {", className, fields));

        var allFields = fields.split(", ");

        for (var field : allFields) {
            var name = field.split( " ")[1];
            writer.println(String.format("\t\t\tthis.%s = %s;", name, name));
        }

        writer.println("\t\t}");

        writer.println();

        for (var field : allFields) {
            writer.println(String.format("\t\tfinal %s;", field));
        }

        writer.println("\t}");

        writer.println();
    }
}
