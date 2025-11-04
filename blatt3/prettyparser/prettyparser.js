/**
 * usage:
 * - you need node
 * - go to blatt3/prettyparser
 * - npm i
 * - node prettyparser.js unpretty
 */
import fs from 'fs';
import antlr4 from 'antlr4';
import PrettyLexer from './js-gen/PrettyParserLexer.js';
import PrettyParser from './js-gen/PrettyParserParser.js';

/**
 * Strips comments from source code while preserving string literals.
 * Comments start with '#' and extend to the end of the line.
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

const getNodeText = node => node ? (typeof node.getText === 'function' ? node.getText() : node.symbol?.text ?? String(node)) : '';

const isTerminal = node => !node?.children || node.children.length === 0;

const isOperator = node => ['*', '/', '+', '-', '==', '!=', '>', '<'].includes(getNodeText(node));

/**
 * Formats a parse tree node as an inline expression without line breaks.
 *
 * @param {Object} node - ANTLR parse tree node
 * @returns {string} Inline formatted text
 */
function formatInline(node) {
    if (!node) return '';
    return prettyPrint(node, 0).replace(/\n/g, ' ').replace(/\s+/g, ' ').trim();
}

/**
 * Recursively formats a parse tree with proper indentation.
 *
 * @param {Object} node - ANTLR parse tree node
 * @param {number} indentLevel - Current indentation level (default: 0)
 * @returns {string} Formatted code
 */
function prettyPrint(node, indentLevel = 0) {
    const indent = ' '.repeat(indentLevel * 4);

    if (!node) return '';
    if (isTerminal(node)) {
        const text = getNodeText(node);
        // skip <EOF> in output
        if (text === '<EOF>') return '';
        return text;
    }

    const children = node.children || [];
    const contextName = node.constructor?.name?.toLowerCase() ?? '';

    // If
    if (contextName.includes('cond')) {
        const condition = formatInline(node.expr(0));
        const thenStmts = node.stmt();

        let result = `${indent}if ${condition} do`;

        // Then
        const elseIndex = children.findIndex(ch => getNodeText(ch) === 'else');
        const thenBlock = thenStmts.filter((stmt, idx) => {
            const stmtIndex = children.indexOf(stmt);
            return elseIndex === -1 ? true : stmtIndex < elseIndex;
        });

        if (thenBlock.length > 0) {
            result += '\n' + thenBlock.map(stmt => prettyPrint(stmt, indentLevel + 1)).join('\n');
        }

        // Else
        if (elseIndex !== -1) {
            const elseBlock = thenStmts.filter(stmt => {
                const stmtIndex = children.indexOf(stmt);
                return stmtIndex > elseIndex;
            });

            result += `\n${indent}else do`;
            if (elseBlock.length > 0) {
                result += '\n' + elseBlock.map(stmt => prettyPrint(stmt, indentLevel + 1)).join('\n');
            }
        }

        result += `\n${indent}end`;
        return result;
    }

    // While
    if (contextName.includes('while')) {
        const condition = formatInline(node.expr());
        const stmts = node.stmt();

        let result = `${indent}while ${condition} do`;
        if (stmts.length > 0) {
            result += '\n' + stmts.map(stmt => prettyPrint(stmt, indentLevel + 1)).join('\n');
        }
        result += `\n${indent}end`;
        return result;
    }

    // variable declaration
    if (contextName.includes('vardecl')) {
        const varName = getNodeText(node.ID());
        const value = formatInline(node.expr());
        return `${indent}${varName} := ${value}`;
    }

    // binary expressions
    if (contextName.includes('expr') && children.length === 3 && isOperator(children[1])) {
        const left = prettyPrint(children[0], 0);
        const operator = getNodeText(children[1]);
        const right = prettyPrint(children[2], 0);
        return `${left} ${operator} ${right}`;
    }

    // Program or statement lists
    if (contextName.includes('program') || contextName.includes('stmt')) {
        return children
            .map(child => prettyPrint(child, indentLevel))
            .filter(text => text.length > 0)
            .join('\n');
    }

    // Default: recursive children
    return children
        .map(child => prettyPrint(child, indentLevel))
        .filter(text => text.length > 0)
        .join(' ');
}

// ---------- Main ----------
const [filename] = process.argv.slice(2);

if (!filename || !fs.existsSync(filename)) {
    console.error('Usage: node prettyparser.js <inputfile>');
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

    const prettyOutput = parseTree ? prettyPrint(parseTree, 0) : '[Parsing failed - no tree]';

    const outputContent = originalContent + '\n\n------------------pretty------------\n\n' + prettyOutput + '\n';
    fs.writeFileSync(filename, outputContent, 'utf-8');

    console.log('Appended pretty output to', filename);
} catch (error) {
    console.error('Exception during parsing/printing:', error);
    process.exit(3);
}