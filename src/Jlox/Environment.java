package Jlox;

import java.util.HashMap;

class Environment {
    private final HashMap<String, Object> values = new HashMap<String, Object>();
    private final Environment enclosing;

    Environment() {
        enclosing = null;
    }

    Environment(Environment environment) {
        this.enclosing = environment;
    }

    void define(String string, Object object) {
        values.put(string, object);
    }

    Object get(Token name) {
        if(values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if(enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name, "Undefined variable " + name.lexeme);

    }

    void assign(Token name, Object value) {
        if(values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if(enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable " + name.lexeme);

    }

    void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme, value);
    }

    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    Environment ancestor(int distance) {
        Environment environment = this;

        while(distance >=0 ) {
            environment = environment.enclosing;
            distance--;
        }

        return environment;
    }

}
