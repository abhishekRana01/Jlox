package tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {

    public static void main(String[] args) throws IOException{
        if(args.length != 1) {
            System.err.println("Usage : generate_ast <ouput dir>");
            System.exit(1);
        }

        String outputDir = args[0];

        defineAst(outputDir, "Expr", Arrays.asList(
            "Binary   : Expr left, Token operator, Expr right",
            "Unary    : Token operator, Expr right",
            "Grouping : Expr expr",
            "Literal  : Object value",
            "Variable : Token name",
            "Assign   : Token name, Expr value",
            "Logical  : Expr left, Token operator, Expr right"
        ));

        defineAst(outputDir, "Stmt", Arrays.asList(
                "Print      : Expr expression",
                "Expression : Expr expression",
                "Var        : Token name, Expr initializer",
                "Block      : List<Stmt> statements",
                "If         : Expr condition, Stmt thenBranch, Stmt elseBranch",
                "While      : Expr condition, Stmt body"
        ));

    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter printWriter = new PrintWriter(path, "UTF-8");

        printWriter.println("package Jlox;");
        printWriter.println("import java.util.List;");
        printWriter.println();
        printWriter.println("abstract class "+ baseName + " {");
        printWriter.println();

        defineVisitor(printWriter, baseName, types);

        printWriter.println();
        printWriter.println("    abstract <R> R accept(Visitor<R> visitor);");

        for(String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(printWriter, className, fields, baseName);
        }

        printWriter.println("}");

        printWriter.close();
    }

    private static void defineVisitor(PrintWriter printWriter, String basename, List<String> types) {
        printWriter.println("    interface Visitor<R> {");

        for(String type : types) {
            String typeName = type.split(":")[0].trim();
            printWriter.println("        R visit" + typeName + basename + "(" + typeName + " " +
                    typeName.toLowerCase() + basename + ");");
        }

        printWriter.println("    }");
    }

    private static void defineType(PrintWriter printWriter, String className, String fields, String baseName) {
        printWriter.println();
        printWriter.println("    static class " + className + " extends " + baseName + " {");
        printWriter.println("        " + className + "(" + fields + ") {");

        String[] fieldArray = fields.split(",");

        for(String field : fieldArray) {
            field = field.trim();
            field = field.split(" ")[1].trim();
            printWriter.println("            " + "this." + field + " = " + field + ";");
        }

        printWriter.println("        }");
        printWriter.println();

        for(String field : fieldArray) {
            field = field.trim();
            printWriter.println("        final " + field + ";");
        }

        printWriter.println();
        printWriter.println("        <R> R accept(Visitor<R> visitor) {");
        printWriter.println("            return visitor.visit" + className + baseName + "(this);");
        printWriter.println("        }");

        printWriter.println("    }");

    }
}
