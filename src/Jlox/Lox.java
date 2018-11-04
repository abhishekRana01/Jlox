package Jlox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

    static boolean hadError  = false;
    static boolean hadRuntimeError = false;

    private static Interpreter interpreter = new Interpreter();

    public static void main(String[] args) throws IOException {

        Lox lox = new Lox();

        if(args.length < 1) {
            lox.runPrompt();
        }
        else if(args.length == 1) {
            lox.runFile(args[0]);
        }

    }

    private void runPrompt() throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        while (true) {
            System.out.print("> ");
            run(bufferedReader.readLine());
        }

    }

    private void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if(hadError) System.exit(65);
        if(hadRuntimeError) System.exit(70);

    }

    private void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Jlox.Parser(tokens);
        List<Stmt> stmts = parser.parse();

        if(hadError) return;

//        System.out.println(new AstPrinter().print(expr));

        interpreter.interpret(stmts);
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void report(int line, String where, String message) {
        System.err.println("Line [" + line + "] Error " + where + ": " + message);
        hadError = true;
    }

    static void runtimeError(RuntimeError e) {
        System.err.println(e.getMessage() + " \nLine " + e.token.line + " ]");
        hadRuntimeError = true;
    }


}
