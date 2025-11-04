import fs from 'fs';
import antlr4 from 'antlr4';
import PrettyLexer from './js-gen/PrettyParserLexer.js';
import PrettyParser from './js-gen/PrettyParserParser.js';

// AST Node Classes

class ASTNode {
    constructor(type) {
        this.type = type;
    }
}

class Program extends ASTNode {
    constructor(statements) {
        super('Program');
        this.statements = statements;
    }
}

class Assignment extends ASTNode {
    constructor(variable, expression) {
        super('Assignment');
        this.variable = variable;
        this.expression = expression;
    }
}

class IfStatement extends ASTNode {
    constructor(condition, thenBlock, elseBlock = null) {
        super('IfStatement');
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }
}

class WhileLoop extends ASTNode {
    constructor(condition, body) {
        super('WhileLoop');
        this.condition = condition;
        this.body = body;
    }
}

class BinaryOp extends ASTNode {
    constructor(operator, left, right) {
        super('BinaryOp');
        this.operator = operator;
        this.left = left;
        this.right = right;
    }
}

class Variable extends ASTNode {
    constructor(name) {
        super('Variable');
        this.name = name;
    }
}

class NumberLiteral extends ASTNode {
    constructor(value) {
        super('NumberLiteral');
        this.value = value;
    }
}

class StringLiteral extends ASTNode {
    constructor(value) {
        super('StringLiteral');
        this.value = value;
    }
}

// Parse Tree to AST Builder

/**
 * Builds an AST from a parse tree context node.
 *
 * @param {Object} ctx - ANTLR parse tree context
 * @returns {ASTNode} Corresponding AST node
 */
function buildAST(ctx) {
    if (!ctx) return null;

    const contextName = ctx.constructor?.name?.toLowerCase() ?? '';

    // Program: list of statements
    if (contextName.includes('program')) {
        const statements = ctx.stmt()
            .map(stmt => buildAST(stmt))
            .filter(s => s !== null);
        return new Program(statements);
    }

    // Statement: delegates to child
    if (contextName.includes('stmt') && !contextName.includes('while') && !contextName.includes('cond')) {
        // stmt wraps vardecl, expr, while, or cond - just recurse
        if (ctx.vardecl()) return buildAST(ctx.vardecl());
        if (ctx.while_()) return buildAST(ctx.while_());
        if (ctx.cond()) return buildAST(ctx.cond());
        if (ctx.expr()) return buildAST(ctx.expr());
        return null;
    }

    // Variable declaration (assignment)
    if (contextName.includes('vardecl')) {
        const variable = ctx.ID().getText();
        const expression = buildAST(ctx.expr());
        return new Assignment(variable, expression);
    }

    // While loop
    if (contextName.includes('while')) {
        const condition = buildAST(ctx.expr());
        const body = ctx.stmt()
            .map(stmt => buildAST(stmt))
            .filter(s => s !== null);
        return new WhileLoop(condition, body);
    }

    // If statement
    if (contextName.includes('cond')) {
        const condition = buildAST(ctx.expr(0));

        // Find where 'else do' keyword appears in children
        const children = ctx.children;
        const elseDoneIndex = children.findIndex(ch => {
            const text = ch.getText ? ch.getText() : '';
            return text === 'elsedo' || text === 'else do';
        });

        const allStmts = ctx.stmt();
        let thenBlock = [];
        let elseBlock = null;

        if (elseDoneIndex === -1) {
            // No else block - all statements are in then block
            thenBlock = allStmts.map(stmt => buildAST(stmt)).filter(s => s !== null);
        } else {
            // Has else block - split based on 'else do' position
            allStmts.forEach(stmt => {
                const stmtIndex = children.indexOf(stmt);
                if (stmtIndex < elseDoneIndex) {
                    thenBlock.push(buildAST(stmt));
                } else {
                    if (!elseBlock) elseBlock = [];
                    elseBlock.push(buildAST(stmt));
                }
            });

            thenBlock = thenBlock.filter(s => s !== null);
            if (elseBlock) {
                elseBlock = elseBlock.filter(s => s !== null);
            }
        }

        return new IfStatement(condition, thenBlock, elseBlock);
    }

    // Expression
    if (contextName.includes('expr')) {
        const children = ctx.children;

        if (!children || children.length === 0) {
            return null;
        }

        // Binary operation: left op right (3 children)
        if (children.length === 3) {
            const left = buildAST(children[0]);
            const operator = children[1].getText();
            const right = buildAST(children[2]);
            return new BinaryOp(operator, left, right);
        }

        // Terminal: ID, NUMBER, or STRING (1 child)
        if (children.length === 1) {
            const child = children[0];
            const text = child.getText();

            if (ctx.ID()) {
                return new Variable(text);
            } else if (ctx.NUMBER()) {
                return new NumberLiteral(parseInt(text));
            } else if (ctx.STRING()) {
                return new StringLiteral(text);
            }
        }

        return null;
    }

    return null;
}

// AST Pretty Printer

/**
 * Formats an AST node with proper indentation.
 *
 * @param {ASTNode} node - AST node to format
 * @param {number} indentLevel - Current indentation level
 * @returns {string} Formatted code
 */
function prettyPrintAST(node, indentLevel = 0) {
    const indent = ' '.repeat(indentLevel * 4);

    if (!node) return '';

    switch (node.type) {
        case 'Program':
            return node.statements
                .map(stmt => prettyPrintAST(stmt, indentLevel))
                .join('\n');

        case 'Assignment':
            const expr = prettyPrintExpression(node.expression);
            return `${indent}${node.variable} := ${expr}`;

        case 'IfStatement':
            const condition = prettyPrintExpression(node.condition);
            let result = `${indent}if ${condition} do`;

            if (node.thenBlock.length > 0) {
                result += '\n' + node.thenBlock
                    .map(stmt => prettyPrintAST(stmt, indentLevel + 1))
                    .join('\n');
            }

            if (node.elseBlock && node.elseBlock.length > 0) {
                result += `\n${indent}else do\n`;
                result += node.elseBlock
                    .map(stmt => prettyPrintAST(stmt, indentLevel + 1))
                    .join('\n');
            }

            result += `\n${indent}end`;
            return result;

        case 'WhileLoop':
            const whileCond = prettyPrintExpression(node.condition);
            let whileResult = `${indent}while ${whileCond} do`;

            if (node.body.length > 0) {
                whileResult += '\n' + node.body
                    .map(stmt => prettyPrintAST(stmt, indentLevel + 1))
                    .join('\n');
            }

            whileResult += `\n${indent}end`;
            return whileResult;

        default:
            return '';
    }
}

/**
 * Formats an expression node as inline text.
 *
 * @param {ASTNode} node - Expression AST node
 * @returns {string} Inline expression text
 */
function prettyPrintExpression(node) {
    if (!node) return '';

    switch (node.type) {
        case 'BinaryOp':
            const left = prettyPrintExpression(node.left);
            const right = prettyPrintExpression(node.right);
            return `${left} ${node.operator} ${right}`;

        case 'Variable':
            return node.name;

        case 'NumberLiteral':
            return String(node.value);

        case 'StringLiteral':
            return node.value;

        default:
            return '';
    }
}

// AST Tree Visualizer

/**
 * Visualizes the AST structure as a tree.
 *
 * @param {ASTNode} node - AST node to visualize
 * @param {string} prefix - Prefix for tree lines
 * @param {boolean} isLast - Whether this is the last child
 * @returns {string} Tree visualization
 */
function visualizeAST(node, prefix = '', isLast = true) {
    if (!node) return '';

    const connector = isLast ? '└── ' : '├── ';
    const newPrefix = prefix + (isLast ? '    ' : '│   ');

    let result = prefix + connector;

    switch (node.type) {
        case 'Program':
            result += 'Program\n';
            node.statements.forEach((stmt, idx) => {
                result += visualizeAST(stmt, newPrefix, idx === node.statements.length - 1);
            });
            break;

        case 'Assignment':
            result += `Assignment: ${node.variable}\n`;
            result += visualizeAST(node.expression, newPrefix, true);
            break;

        case 'IfStatement':
            result += 'IfStatement\n';
            result += newPrefix + '├── Condition\n';
            result += visualizeAST(node.condition, newPrefix + '│   ', true);
            result += newPrefix + '├── Then\n';
            node.thenBlock.forEach((stmt, idx) => {
                result += visualizeAST(stmt, newPrefix + '│   ', idx === node.thenBlock.length - 1);
            });
            if (node.elseBlock && node.elseBlock.length > 0) {
                result += newPrefix + '└── Else\n';
                node.elseBlock.forEach((stmt, idx) => {
                    result += visualizeAST(stmt, newPrefix + '    ', idx === node.elseBlock.length - 1);
                });
            }
            break;

        case 'WhileLoop':
            result += 'WhileLoop\n';
            result += newPrefix + '├── Condition\n';
            result += visualizeAST(node.condition, newPrefix + '│   ', true);
            result += newPrefix + '└── Body\n';
            node.body.forEach((stmt, idx) => {
                result += visualizeAST(stmt, newPrefix + '    ', idx === node.body.length - 1);
            });
            break;

        case 'BinaryOp':
            result += `BinaryOp: ${node.operator}\n`;
            result += visualizeAST(node.left, newPrefix, false);
            result += visualizeAST(node.right, newPrefix, true);
            break;

        case 'Variable':
            result += `Variable: ${node.name}\n`;
            break;

        case 'NumberLiteral':
            result += `Number: ${node.value}\n`;
            break;

        case 'StringLiteral':
            result += `String: ${node.value}\n`;
            break;

        default:
            result += `Unknown: ${node.type}\n`;
    }

    return result;
}

// Comment Stripper

/**
 * Strips comments from source code while preserving string literals.
 *
 * @param {string} text - Source code to clean
 * @returns {string} Text without comments
 */
function stripCommentsPreserveStrings(text) {
    return text.split(/\r?\n/).map(line => {
        let result = '';
        let inString = false;

        for (let i = 0; i < line.length; i++) {
            const char = line[i];
            if (char === '"' && (i === 0 || line[i-1] !== '"')) {
                inString = !inString;
            }
            if (!inString && char === '#') break;
            result += char;
        }
        return result;
    }).join('\n');
}

//Main

const [filename] = process.argv.slice(2);

if (!filename || !fs.existsSync(filename)) {
    console.error('Usage: node ast-main.js <inputfile>');
    process.exit(1);
}

const originalContent = fs.readFileSync(filename, 'utf-8');
const sanitizedContent = stripCommentsPreserveStrings(originalContent);

try {
    const inputStream = new antlr4.InputStream(sanitizedContent);
    const lexer = new PrettyLexer(inputStream);
    const tokenStream = new antlr4.CommonTokenStream(lexer);
    const parser = new PrettyParser(tokenStream);

    parser.buildParseTrees = true;
    const parseTree = parser.program();

    // Build AST from Parse Tree
    const ast = buildAST(parseTree);

    // print the AST
    const prettyOutput = ast ? prettyPrintAST(ast, 0) : '[AST building failed]';

    // Visualize AST tree structure
    const astTree = ast ? visualizeAST(ast) : '[AST building failed]';

    const outputContent = originalContent +
        '\n\n------------------pretty------------\n\n' + prettyOutput +
        '\n\n------------------AST tree--------------\n\n' + astTree + '\n';
    fs.writeFileSync(filename, outputContent, 'utf-8');

    console.log('Appended AST pretty output and tree to', filename);
} catch (error) {
    console.error('Exception during parsing/AST building:', error);
    process.exit(3);
}