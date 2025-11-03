
import fs from 'fs';
import antlr4 from 'antlr4';
import PrettyLexer from './js-gen/PrettyParserLexer.js';
import PrettyParser from './js-gen/PrettyParserParser.js';

function stripCommentsPreserveStrings(text) {
    const lines = text.split(/\r?\n/);
    return lines
        .map(line => {
            let out = '';
            let inString = false;
            for (let i = 0; i < line.length; ++i) {
                const ch = line[i];
                if (ch === '"' && (i === 0 || line[i - 1] !== '\\')) {
                    inString = !inString;
                    out += ch;
                } else if (!inString && ch === '#') {
                    break;
                } else {
                    out += ch;
                }
            }
            return out;
        })
        .join('\n');
}

function nodeText(n) {
    try {
        if (!n) return '';
        if (typeof n.getText === 'function') return n.getText();
        if (n.symbol && typeof n.symbol.text === 'string') return n.symbol.text;
        return String(n);
    } catch (e) {
        return '';
    }
}

function isTerminal(n) {
    return !n.children || n.children.length === 0;
}

function isOperatorToken(node) {
    if (!node) return false;
    const t = nodeText(node);
    const ops = ['*','/','+','-','==','!=','>','<'];
    return ops.includes(t);
}

// join children range into a single-line string (collapse internal newlines)
function joinChildTextRangeInline(node, from, to) {
    if (!node || !node.children) return '';
    const parts = [];
    for (let i = from; i < to && i < node.children.length; ++i) {
        const ch = node.children[i];
        if (isTerminal(ch)) {
            parts.push(nodeText(ch));
        } else {
            // produce inline representation of child (no leading indentation)
            const txt = prettyPrint(ch, 0);
            parts.push(txt.replace(/\n/g, ' ').trim());
        }
    }
    return parts.join(' ').replace(/\s+/g, ' ').trim();
}

/**
 * prettyPrint(node, indentLevel)
 * - ALWAYS returns text whose non-empty lines start with indentLevel*4 spaces.
 * - For inline expressions, call prettyPrint(child, 0) and trim.
 */
function prettyPrint(node, indentLevel = 0) {
    const baseIndent = ' '.repeat(indentLevel * 4);
    if (!node) return '';

    // Terminal node -> return text without additional indentation (indent applied by caller)
    if (isTerminal(node)) {
        // terminal should be returned as-is; caller will apply baseIndent if needed
        return nodeText(node);
    }

    // Helper: safe accessor call
    const maybeGet = (name) => {
        if (!node) return null;
        if (typeof node[name] === 'function') {
            try { return node[name](); } catch (e) { return null; }
        }
        return null;
    };

    const ctor = node && node.constructor && node.constructor.name ? node.constructor.name : '';
    const children = node.children || [];
    const texts = children.map(c => nodeText(c));

    // ------- If / Cond -------
    if (ctor.toLowerCase().includes('cond') || (children.length > 0 && nodeText(children[0]).toLowerCase() === 'if')) {
        // Try accessor-based
        const exprAcc = maybeGet('expr');
        const stmtAcc = maybeGet('stmt');

        let condTextInline = '';
        let thenText = '';
        let elseText = '';

        if (exprAcc) {
            const condNode = Array.isArray(exprAcc) ? exprAcc[0] : exprAcc;
            condTextInline = condNode ? prettyPrint(condNode, 0).replace(/\n/g, ' ').trim() : '';
        } else {
            // fallback: children between 'if' and first 'do'
            let doIdx = texts.findIndex((t, i) => i > 0 && t === 'do');
            if (doIdx === -1) doIdx = 2;
            condTextInline = joinChildTextRangeInline(node, 1, doIdx);
        }

        // then
        if (stmtAcc) {
            const thenNode = Array.isArray(stmtAcc) ? stmtAcc[0] : stmtAcc;
            thenText = thenNode ? prettyPrint(thenNode, indentLevel + 1) : '';
        } else {
            // fallback: node after first 'do'
            let doIdx = texts.findIndex((t, i) => i > 0 && t === 'do');
            if (doIdx !== -1 && doIdx + 1 < children.length) {
                thenText = prettyPrint(children[doIdx + 1], indentLevel + 1);
            }
        }

        // else: try accessor, or scan
        if (stmtAcc && Array.isArray(stmtAcc) && stmtAcc.length >= 2) {
            elseText = prettyPrint(stmtAcc[1], indentLevel + 1);
        } else {
            const elseIdx = texts.findIndex((t, i) => i > 0 && t === 'else');
            if (elseIdx !== -1) {
                // prefer pattern else do stmt
                let elseDoIdx = texts.findIndex((t, i) => i > elseIdx && t === 'do');
                if (elseDoIdx !== -1 && elseDoIdx + 1 < children.length) {
                    elseText = prettyPrint(children[elseDoIdx + 1], indentLevel + 1);
                } else if (elseIdx + 1 < children.length) {
                    elseText = prettyPrint(children[elseIdx + 1], indentLevel + 1);
                }
            }
        }

        // assemble with proper leading indentation
        let out = '';
        out += baseIndent + 'if ' + condTextInline + ' do\n';
        if (thenText) out += thenText + '\n';
        if (elseText) {
            out += baseIndent + 'else do\n';
            out += elseText + '\n';
        }
        out += baseIndent + 'end';
        return out;
    }

    // ------- While -------
    if (ctor.toLowerCase().includes('while') || (children.length > 0 && nodeText(children[0]).toLowerCase() === 'while')) {
        const exprAcc = maybeGet('expr');
        const stmtAcc = maybeGet('stmt');

        let condTextInline = '';
        let bodyText = '';

        if (exprAcc) {
            const condNode = Array.isArray(exprAcc) ? exprAcc[0] : exprAcc;
            condTextInline = condNode ? prettyPrint(condNode, 0).replace(/\n/g, ' ').trim() : '';
        } else {
            const doIdx = texts.findIndex((t, i) => i > 0 && t === 'do');
            const upto = doIdx === -1 ? 2 : doIdx;
            condTextInline = joinChildTextRangeInline(node, 1, upto);
        }

        if (stmtAcc) {
            const bodyNode = Array.isArray(stmtAcc) ? stmtAcc[0] : stmtAcc;
            if (bodyNode) bodyText = prettyPrint(bodyNode, indentLevel + 1);
        } else {
            const doIdx = texts.findIndex((t, i) => i > 0 && t === 'do');
            if (doIdx !== -1 && doIdx + 1 < children.length) bodyText = prettyPrint(children[doIdx + 1], indentLevel + 1);
        }

        let out = '';
        out += baseIndent + 'while ' + condTextInline + ' do\n';
        if (bodyText) out += bodyText + '\n';
        out += baseIndent + 'end';
        return out;
    }

    // ------- Assignment / Vardecl -------
    const idAcc = maybeGet('ID');
    const exprAcc = maybeGet('expr');
    if (idAcc && exprAcc) {
        const idText = Array.isArray(idAcc) ? nodeText(idAcc[0]) : nodeText(idAcc);
        const exprNode = Array.isArray(exprAcc) ? exprAcc[0] : exprAcc;
        const exprTextInline = exprNode ? prettyPrint(exprNode, 0).replace(/\n/g, ' ').trim() : '';
        return baseIndent + idText + ' := ' + exprTextInline;
    }
    // fallback: search for ':=' token
    const assignIdx = texts.findIndex(t => t === ':=');
    if (assignIdx !== -1) {
        const left = joinChildTextRangeInline(node, 0, assignIdx);
        const right = joinChildTextRangeInline(node, assignIdx + 1, children.length);
        return baseIndent + left + ' := ' + right;
    }

    // ------- Binary expression (inline) -------
    if (children.length === 3 && isOperatorToken(children[1])) {
        const left = prettyPrint(children[0], 0).trim();
        const op = nodeText(children[1]);
        const right = prettyPrint(children[2], 0).trim();
        // inline representation; caller will indent if needed
        return left + ' ' + op + ' ' + right;
    }

    // ------- Program root / sequence of statements -------
    if (ctor.toLowerCase().includes('program') || ctor.toLowerCase().includes('file') || children.length > 0) {
        // For program root, produce concatenated lines; child statements already include indentation
        // We treat each child that yields non-empty text as a top-level line (no extra indentation)
        const lines = [];
        for (const ch of children) {
            const txt = prettyPrint(ch, indentLevel);
            if (txt && txt.trim() !== '') lines.push(txt);
        }
        return lines.join('\n');
    }

    // Default: join children inline
    const parts = children.map(ch => {
        // if child looks like a statement (has 'if'/'while'/'end'), render with same indent level,
        // otherwise inline with indentLevel (but as one line)
        if (ch.children && ch.children.length > 0 && ['if','while','end','else'].includes(nodeText(ch.children[0]).toLowerCase())) {
            return prettyPrint(ch, indentLevel);
        } else {
            // inline
            return prettyPrint(ch, 0).replace(/\n/g, ' ').trim();
        }
    }).filter(Boolean);

    if (parts.length === 0) return '';
    // produce single-line and apply baseIndent
    return baseIndent + parts.join(' ').replace(/\s+/g, ' ').trim();
}

// ---------- Main ----------
const argv = process.argv.slice(2);
if (argv.length !== 1) {
    console.error('Usage: node main.js <inputfile>');
    process.exit(1);
}
const filename = argv[0];

if (!fs.existsSync(filename)) {
    console.error('File not found:', filename);
    process.exit(2);
}

const original = fs.readFileSync(filename, 'utf-8');
const sanitized = stripCommentsPreserveStrings(original);

try {
    const chars = new antlr4.InputStream(sanitized);
    const lexer = new PrettyLexer(chars);
    const tokens = new antlr4.CommonTokenStream(lexer);
    const parser = new PrettyParser(tokens);
    parser.buildParseTrees = true;

    const tree = parser.program();

    let pretty = '';
    if (tree) {
        pretty = prettyPrint(tree, 0);
    } else {
        pretty = '[Parsing failed - no tree]';
    }

    const result = original + '\n\n------------------pretty------------\n\n' + pretty + '\n';
    fs.writeFileSync(filename, result, 'utf-8');
    console.log('Appended pretty output to', filename);
} catch (e) {
    console.error('Exception during parsing/printing:', e);
    console.error(e.stack || e);
    process.exit(3);
}