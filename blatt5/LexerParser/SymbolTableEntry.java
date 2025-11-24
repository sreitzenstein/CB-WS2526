package LexerParser;

import java.util.List;

class SymbolTableEntry {
    enum Child { VARIABLE, FUNCTION }

    Child child;
    PrimType type;      
    String name;

    List<Param> params;
    Block body;

    Object astNode; 

    SymbolTableEntry(Child child, PrimType type, String name) {
        this.child = child;
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return child + " " + name + " : " + type;
    }
}
