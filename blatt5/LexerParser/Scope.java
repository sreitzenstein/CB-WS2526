package LexerParser;

import java.util.*;

class Scope {
    Map<String, SymbolTableEntry> symbols = new HashMap<>();
    Scope parent;
    Scope parentRef;


    Scope(Scope parent) {
        this.parent = parent;
        this.parentRef = ScopeHolder.all;
        ScopeHolder.all = this;
    }


    boolean define(SymbolTableEntry entry) {
        if (symbols.containsKey(entry.name)) return false;
        symbols.put(entry.name, entry);
        return true;
    }

    SymbolTableEntry resolve(String name) {
        if (symbols.containsKey(name))
            return symbols.get(name);
        if (parent != null)
            return parent.resolve(name);
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        printScope(sb, 0);
        return sb.toString();
    }

    private void printScope(StringBuilder sb, int indent) {
        String pad = "  ".repeat(indent);
        sb.append(pad).append("Scope:\n");

        for (var entry : symbols.values()) {
            sb.append(pad)
                    .append("  - ")
                    .append(entry)
                    .append("\n");
        }

        for (Scope child : getChildren()) {
            child.printScope(sb, indent + 1);
        }
    }

    private List<Scope> getChildren() {
        List<Scope> list = new ArrayList<>();
        for (Scope s = ScopeHolder.all; s != null; s = s.parentRef) {
            if (s.parent == this)
                list.add(s);
        }
        return list;
    }

    static class ScopeHolder {
        static Scope all;
    }

}
