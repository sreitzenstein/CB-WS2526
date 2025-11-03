// Requires: npm i, start: node main.js unpretty in blatt3/prettyparser
import fs from 'fs';
import antlr4 from 'antlr4';
import PrettyLexer from './js-gen/PrettyParserLexer.js';
import PrettyParser from './js-gen/PrettyParserParser.js';

const INDENT_SIZE = 4;
const INDENT = (lvl) => ' '.repeat(lvl * INDENT_SIZE);

function stripCommentsPreserveStrings(text) {
    return text.split(/\r?\n/).map(line => {
        let out = '', inStr = false;
        for (let i = 0; i < line.length; ++i) {
            const ch = line[i];
            if (ch === '"' && (i === 0 || line[i-1] !== '\\')) { inStr = !inStr; out += ch; }
            else if (!inStr && ch === '#') break;
            else out += ch;
        }
        return out;
    }).join('\n');
}

function nodeText(n) {
    try { if (!n) return ''; if (typeof n.getText === 'function') return n.getText(); if (n.symbol) return n.symbol.text; return String(n); }
    catch { return ''; }
}
function isTerminal(n) { return !n.children || n.children.length === 0; }
function isEof(n) {
    try { return !!(n && n.symbol && n.symbol.type === antlr4.Token.EOF); } catch { return false; }
}
function compactChildren(node) {
    return (node.children || []).filter(c => !isEof(c));
}
function joinInline(node, from=0, to=null) {
    const ch = compactChildren(node);
    to = to === null ? ch.length : to;
    const parts = [];
    for (let i = from; i < to && i < ch.length; ++i) {
        const c = ch[i];
        parts.push(isTerminal(c) ? nodeText(c) : prettyPrintSimple(c, 0).replace(/\n/g, ' '));
    }
    return parts.join(' ').replace(/\s+/g,' ').trim();
}
function isOpToken(n) {
    const t = nodeText(n);
    return ['*','/','+','-','==','!=','>','<'].includes(t);
}

/**
 *
 * @param node
 * @param indentLevel
 * @returns {*|string|string}
 */
function prettyPrintSimple(node, indentLevel = 0) {
    if (!node) return '';
    if (isTerminal(node)) return nodeText(node);

    const children = compactChildren(node);
    if (children.length === 0) return '';

    const first = nodeText(children[0]).toLowerCase();

    // if / cond
    if (first === 'if') {
        // find first 'do'
        let doIdx = children.findIndex((c,i) => i>0 && nodeText(c) === 'do');
        if (doIdx === -1) doIdx = 2;
        const cond = joinInline(node, 1, doIdx);
        const thenNode = (doIdx+1 < children.length) ? children[doIdx+1] : null;

        // find else (optional)
        const elseIdx = children.findIndex((c,i) => i>doIdx && nodeText(c) === 'else');
        let elseBody = null;
        if (elseIdx !== -1) {
            // prefer 'else do stmt'
            let elseDo = children.findIndex((c,i) => i>elseIdx && nodeText(c) === 'do');
            if (elseDo !== -1 && elseDo+1 < children.length) elseBody = children[elseDo+1];
            else if (elseIdx+1 < children.length) elseBody = children[elseIdx+1];
        }

        let out = '';
        out += INDENT(indentLevel) + 'if ' + cond + ' do\n';
        if (thenNode) out += prettyPrintSimple(thenNode, indentLevel+1) + '\n';
        if (elseBody) {
            out += INDENT(indentLevel) + 'else do\n';
            out += prettyPrintSimple(elseBody, indentLevel+1) + '\n';
        }
        out += INDENT(indentLevel) + 'end';
        return out;
    }

    // while
    if (first === 'while') {
        let doIdx = children.findIndex((c,i) => i>0 && nodeText(c) === 'do');
        if (doIdx === -1) doIdx = 2;
        const cond = joinInline(node, 1, doIdx);
        const body = (doIdx+1 < children.length) ? children[doIdx+1] : null;
        let out = INDENT(indentLevel) + 'while ' + cond + ' do\n';
        if (body) out += prettyPrintSimple(body, indentLevel+1) + '\n';
        out += INDENT(indentLevel) + 'end';
        return out;
    }

    // assignment (search for ':=')
    const assignIdx = children.findIndex(c => nodeText(c) === ':=');
    if (assignIdx !== -1) {
        const left = joinInline(node, 0, assignIdx);
        const right = joinInline(node, assignIdx+1, children.length);
        return INDENT(indentLevel) + left + ' := ' + right;
    }

    // binary expr inline
    if (children.length === 3 && isOpToken(children[1])) {
        const l = prettyPrintSimple(children[0], 0).trim();
        const op = nodeText(children[1]);
        const r = prettyPrintSimple(children[2], 0).trim();
        return l + ' ' + op + ' ' + r;
    }

    // program root or generic: print all child statements each on own line
    // (we assume top-level children are statements)
    const parts = children.map(ch => {
        const txt = prettyPrintSimple(ch, indentLevel);
        return txt && txt.trim() !== '' ? txt : null;
    }).filter(Boolean);
    if (parts.length === 1) return parts[0];
    return parts.join('\n');
}

// ---------- Main ----------
const argv = process.argv.slice(2);
if (argv.length !== 1) { console.error('Usage: node main.js <inputfile>'); process.exit(1); }
const filename = argv[0];
if (!fs.existsSync(filename)) { console.error('File not found:', filename); process.exit(2); }

const original = fs.readFileSync(filename, 'utf8');
const sanitized = stripCommentsPreserveStrings(original);

try {
    const chars = new antlr4.InputStream(sanitized);
    const lexer = new PrettyLexer(chars);
    const tokens = new antlr4.CommonTokenStream(lexer);
    const parser = new PrettyParser(tokens);
    parser.buildParseTrees = true;
    const tree = parser.program();
    const pretty = tree ? prettyPrintSimple(tree, 0) : '[parsing failed]';
    const result = original + '\n\n------------------pretty------------\n\n' + pretty + '\n';
    fs.writeFileSync(filename, result, 'utf8');
    console.log('Appended pretty output to', filename);
} catch (e) {
    console.error('Exception:', e);
    process.exit(3);
}
