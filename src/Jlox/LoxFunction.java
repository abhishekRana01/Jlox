package Jlox;

import java.util.List;

public class LoxFunction implements LoxCallable {

    private final Stmt.Fun declaration;
    private final Environment closure;

    LoxFunction(Stmt.Fun declaration, Environment environment) {
        this.declaration = declaration;
        this.closure     = environment;
    }

    public Object call(Interpreter interpreter, List<Object> args) {
        Environment environment = new Environment(closure);

        for(int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme, args.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
        }
        catch (Return returnValue) {
            return returnValue.value;
        }

        return null;
    }

    public int arity() {
        return declaration.params.size();
    }

    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }

}
