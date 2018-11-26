package Jlox;

import java.util.List;

class LoxClass implements LoxCallable {
    String name;

    LoxClass(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public int arity() {
        return 0;
    }

    public Object call(Interpreter interpreter, List<Object> args) {
        LoxInstance instance = new LoxInstance(this);
        return instance;
    }
}
