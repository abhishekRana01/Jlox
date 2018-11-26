package Jlox;

class LoxInstance {
    LoxClass klaas;

    LoxInstance(LoxClass loxClass) {
        klaas = loxClass;
    }

    @Override
    public String toString() {
        return klaas.name + " instance";
    }
}
