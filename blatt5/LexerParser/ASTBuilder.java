package LexerParser;

import org.antlr.v4.runtime.tree.*;

import java.util.ArrayList;
import java.util.List;

class ASTBuilder extends MiniCBaseVisitor<Object> {
    
    // program: stmt+ EOF → Liste[Stmt]
    @Override
    public List<Stmt> visitProgram(MiniCParser.ProgramContext ctx) {
        List<Stmt> stmts = new ArrayList<>();
        for (MiniCParser.StmtContext stmtCtx : ctx.stmt()) {
            stmts.add((Stmt) visit(stmtCtx));
        }
        return stmts;
    }
    
    // stmt: verschiedene Alternativen
    @Override
    public Stmt visitStmt(MiniCParser.StmtContext ctx) {
        if (ctx.vardecl() != null) {
            return (Stmt) visit(ctx.vardecl());
        } else if (ctx.assign() != null) {
            return (Stmt) visit(ctx.assign());
        } else if (ctx.fndecl() != null) {
            return (Stmt) visit(ctx.fndecl());
        } else if (ctx.expr() != null) {
            Expr expr = (Expr) visit(ctx.expr());
            return new ExprStmt(expr);
        } else if (ctx.block() != null) {
            return (Stmt) visit(ctx.block());
        } else if (ctx.while_() != null) {
            return (Stmt) visit(ctx.while_());
        } else if (ctx.cond() != null) {
            return (Stmt) visit(ctx.cond());
        } else if (ctx.return_() != null) {
            return (Stmt) visit(ctx.return_());
        }
        throw new RuntimeException("Unknown stmt type");
    }
    
    // vardecl: type ID ('=' expr)? ';'
    @Override
    public Stmt visitVardecl(MiniCParser.VardeclContext ctx) {
        PrimType type = convertType(ctx.type().getText());
        String name = ctx.ID().getText();
        Expr initializer = null;
        if (ctx.expr() != null) {
            initializer = (Expr) visit(ctx.expr());
        }
        return new VarDecl(type, name, initializer);
    }
    
    // assign: ID '=' expr ';'
    @Override
    public Stmt visitAssign(MiniCParser.AssignContext ctx) {
        String name = ctx.ID().getText();
        Expr value = (Expr) visit(ctx.expr());
        return new Assign(name, value);
    }
    
    // fndecl: type ID '(' params? ')' block
    @Override
    public Stmt visitFndecl(MiniCParser.FndeclContext ctx) {
        PrimType returnType = convertType(ctx.type().getText());
        String name = ctx.ID().getText();
        List<Param> params = new ArrayList<>();
        if (ctx.params() != null) {
            params = (List<Param>) visit(ctx.params());
        }
        Block body = (Block) visit(ctx.block());
        return new FnDecl(returnType, name, params, body);
    }
    
    // params: type ID (',' type ID)*
    @Override
    public List<Param> visitParams(MiniCParser.ParamsContext ctx) {
        List<Param> params = new ArrayList<>();
        List<MiniCParser.TypeContext> types = ctx.type();
        List<TerminalNode> ids = ctx.ID();
        for (int i = 0; i < types.size(); i++) {
            PrimType type = convertType(types.get(i).getText());
            String name = ids.get(i).getText();
            params.add(new Param(type, name));
        }
        return params;
    }
    
    // return: 'return' expr ';'
    @Override
    public Stmt visitReturn(MiniCParser.ReturnContext ctx) {
        Expr value = (Expr) visit(ctx.expr());
        return new ReturnStmt(value);
    }
    
    // block: '{' stmt* '}'
    @Override
    public Block visitBlock(MiniCParser.BlockContext ctx) {
        List<Stmt> stmts = new ArrayList<>();
        for (MiniCParser.StmtContext stmtCtx : ctx.stmt()) {
            stmts.add((Stmt) visit(stmtCtx));
        }
        return new Block(stmts);
    }
    
    // while: 'while' '(' expr ')' block
    @Override
    public Stmt visitWhile(MiniCParser.WhileContext ctx) {
        Expr condition = (Expr) visit(ctx.expr());
        Block body = (Block) visit(ctx.block());
        return new WhileStmt(condition, body);
    }
    
    // cond: 'if' '(' expr ')' block ('else' block)?
    @Override
    public Stmt visitCond(MiniCParser.CondContext ctx) {
        Expr condition = (Expr) visit(ctx.expr());
        Block thenBranch = (Block) visit(ctx.block(0));
        Block elseBranch;
        if (ctx.block().size() > 1) {
            elseBranch = (Block) visit(ctx.block(1));
        } else {
            elseBranch = new Block(new ArrayList<>());
        }
        return new IfStmt(condition, thenBranch, elseBranch);
    }
    
    // expr: verschiedene Alternativen
    @Override
    public Expr visitExpr(MiniCParser.ExprContext ctx) {
        // fncall
        if (ctx.fncall() != null) {
            return (Expr) visit(ctx.fncall());
        }
        
        // Binary operations
        if (ctx.getChildCount() == 3 && ctx.getChild(1) instanceof TerminalNode) {
            Expr left = (Expr) visit(ctx.expr(0));
            Expr right = (Expr) visit(ctx.expr(1));
            String opText = ctx.getChild(1).getText();
            Operator op = convertOperator(opText);
            return new Binary(left, op, right);
        }
        
        // ID
        if (ctx.ID() != null) {
            return new Variable(ctx.ID().getText());
        }
        
        // NUMBER
        if (ctx.NUMBER() != null) {
            return new IntLiteral(Integer.parseInt(ctx.NUMBER().getText()));
        }
        
        // STRING
        if (ctx.STRING() != null) {
            String text = ctx.STRING().getText();
            // Entferne Anführungszeichen
            String unquoted = text.substring(1, text.length() - 1);
            return new StringLiteral(unquoted);
        }
        
        // 'T'
        if (ctx.getText().equals("T")) {
            return new BoolLiteral(true);
        }
        
        // 'F'
        if (ctx.getText().equals("F")) {
            return new BoolLiteral(false);
        }
        
        // '(' expr ')'
        if (ctx.getChildCount() == 3 && ctx.getChild(0).getText().equals("(")) {
            return (Expr) visit(ctx.expr(0));
        }
        
        throw new RuntimeException("Unknown expr type: " + ctx.getText());
    }
    
    // fncall: ID '(' args? ')'
    @Override
    public Expr visitFncall(MiniCParser.FncallContext ctx) {
        String name = ctx.ID().getText();
        List<Expr> args = new ArrayList<>();
        if (ctx.args() != null) {
            args = (List<Expr>) visit(ctx.args());
        }
        return new Call(name, args);
    }
    
    // args: expr (',' expr)*
    @Override
    public List<Expr> visitArgs(MiniCParser.ArgsContext ctx) {
        List<Expr> args = new ArrayList<>();
        for (MiniCParser.ExprContext exprCtx : ctx.expr()) {
            args.add((Expr) visit(exprCtx));
        }
        return args;
    }
    
    // Hilfsmethoden
    private PrimType convertType(String typeStr) {
        switch (typeStr) {
            case "int": return PrimType.INT;
            case "string": return PrimType.STRING;
            case "bool": return PrimType.BOOL;
            default: throw new RuntimeException("Unknown type: " + typeStr);
        }
    }
    
    private Operator convertOperator(String opStr) {
        switch (opStr) {
            case "*": return Operator.MUL;
            case "/": return Operator.DIV;
            case "+": return Operator.PLUS;
            case "-": return Operator.MINUS;
            case ">": return Operator.GT;
            case "<": return Operator.LT;
            case "==": return Operator.EQ;
            case "!=": return Operator.NEQ;
            default: throw new RuntimeException("Unknown operator: " + opStr);
        }
    }
}