// Generated from PrettyParser.g4 by ANTLR 4.13.2
// jshint ignore: start
import antlr4 from 'antlr4';
import PrettyParserListener from './PrettyParserListener.js';
const serializedATN = [4,1,19,97,2,0,7,0,2,1,7,1,2,2,7,2,2,3,7,3,2,4,7,4,
2,5,7,5,1,0,5,0,14,8,0,10,0,12,0,17,9,0,1,0,1,0,1,1,1,1,1,1,1,1,3,1,25,8,
1,1,2,1,2,1,2,1,2,1,3,1,3,1,3,1,3,5,3,35,8,3,10,3,12,3,38,9,3,1,3,1,3,1,
4,1,4,1,4,1,4,5,4,46,8,4,10,4,12,4,49,9,4,1,4,1,4,5,4,53,8,4,10,4,12,4,56,
9,4,3,4,58,8,4,1,4,1,4,1,5,1,5,1,5,1,5,3,5,66,8,5,1,5,1,5,1,5,1,5,1,5,1,
5,1,5,1,5,1,5,1,5,1,5,1,5,1,5,1,5,1,5,1,5,1,5,1,5,1,5,1,5,1,5,1,5,1,5,1,
5,5,5,92,8,5,10,5,12,5,95,9,5,1,5,0,1,10,6,0,2,4,6,8,10,0,0,108,0,15,1,0,
0,0,2,24,1,0,0,0,4,26,1,0,0,0,6,30,1,0,0,0,8,41,1,0,0,0,10,65,1,0,0,0,12,
14,3,2,1,0,13,12,1,0,0,0,14,17,1,0,0,0,15,13,1,0,0,0,15,16,1,0,0,0,16,18,
1,0,0,0,17,15,1,0,0,0,18,19,5,0,0,1,19,1,1,0,0,0,20,25,3,4,2,0,21,25,3,10,
5,0,22,25,3,6,3,0,23,25,3,8,4,0,24,20,1,0,0,0,24,21,1,0,0,0,24,22,1,0,0,
0,24,23,1,0,0,0,25,3,1,0,0,0,26,27,5,15,0,0,27,28,5,1,0,0,28,29,3,10,5,0,
29,5,1,0,0,0,30,31,5,2,0,0,31,32,3,10,5,0,32,36,5,3,0,0,33,35,3,2,1,0,34,
33,1,0,0,0,35,38,1,0,0,0,36,34,1,0,0,0,36,37,1,0,0,0,37,39,1,0,0,0,38,36,
1,0,0,0,39,40,5,4,0,0,40,7,1,0,0,0,41,42,5,5,0,0,42,43,3,10,5,0,43,47,5,
3,0,0,44,46,3,2,1,0,45,44,1,0,0,0,46,49,1,0,0,0,47,45,1,0,0,0,47,48,1,0,
0,0,48,57,1,0,0,0,49,47,1,0,0,0,50,54,5,6,0,0,51,53,3,2,1,0,52,51,1,0,0,
0,53,56,1,0,0,0,54,52,1,0,0,0,54,55,1,0,0,0,55,58,1,0,0,0,56,54,1,0,0,0,
57,50,1,0,0,0,57,58,1,0,0,0,58,59,1,0,0,0,59,60,5,4,0,0,60,9,1,0,0,0,61,
62,6,5,-1,0,62,66,5,15,0,0,63,66,5,16,0,0,64,66,5,17,0,0,65,61,1,0,0,0,65,
63,1,0,0,0,65,64,1,0,0,0,66,93,1,0,0,0,67,68,10,11,0,0,68,69,5,7,0,0,69,
92,3,10,5,12,70,71,10,10,0,0,71,72,5,8,0,0,72,92,3,10,5,11,73,74,10,9,0,
0,74,75,5,9,0,0,75,92,3,10,5,10,76,77,10,8,0,0,77,78,5,10,0,0,78,92,3,10,
5,9,79,80,10,7,0,0,80,81,5,11,0,0,81,92,3,10,5,8,82,83,10,6,0,0,83,84,5,
12,0,0,84,92,3,10,5,7,85,86,10,5,0,0,86,87,5,13,0,0,87,92,3,10,5,6,88,89,
10,4,0,0,89,90,5,14,0,0,90,92,3,10,5,5,91,67,1,0,0,0,91,70,1,0,0,0,91,73,
1,0,0,0,91,76,1,0,0,0,91,79,1,0,0,0,91,82,1,0,0,0,91,85,1,0,0,0,91,88,1,
0,0,0,92,95,1,0,0,0,93,91,1,0,0,0,93,94,1,0,0,0,94,11,1,0,0,0,95,93,1,0,
0,0,9,15,24,36,47,54,57,65,91,93];


const atn = new antlr4.atn.ATNDeserializer().deserialize(serializedATN);

const decisionsToDFA = atn.decisionToState.map( (ds, index) => new antlr4.dfa.DFA(ds, index) );

const sharedContextCache = new antlr4.atn.PredictionContextCache();

export default class PrettyParserParser extends antlr4.Parser {

    static grammarFileName = "PrettyParser.g4";
    static literalNames = [ null, "':='", "'while'", "'do'", "'end'", "'if'", 
                            "'else do'", "'*'", "'/'", "'+'", "'-'", "'=='", 
                            "'!='", "'>'", "'<'" ];
    static symbolicNames = [ null, null, null, null, null, null, null, null, 
                             null, null, null, null, null, null, null, "ID", 
                             "NUMBER", "STRING", "COMMENT", "WS" ];
    static ruleNames = [ "program", "stmt", "vardecl", "while", "cond", 
                         "expr" ];

    constructor(input) {
        super(input);
        this._interp = new antlr4.atn.ParserATNSimulator(this, atn, decisionsToDFA, sharedContextCache);
        this.ruleNames = PrettyParserParser.ruleNames;
        this.literalNames = PrettyParserParser.literalNames;
        this.symbolicNames = PrettyParserParser.symbolicNames;
    }

    sempred(localctx, ruleIndex, predIndex) {
    	switch(ruleIndex) {
    	case 5:
    	    		return this.expr_sempred(localctx, predIndex);
        default:
            throw "No predicate with index:" + ruleIndex;
       }
    }

    expr_sempred(localctx, predIndex) {
    	switch(predIndex) {
    		case 0:
    			return this.precpred(this._ctx, 11);
    		case 1:
    			return this.precpred(this._ctx, 10);
    		case 2:
    			return this.precpred(this._ctx, 9);
    		case 3:
    			return this.precpred(this._ctx, 8);
    		case 4:
    			return this.precpred(this._ctx, 7);
    		case 5:
    			return this.precpred(this._ctx, 6);
    		case 6:
    			return this.precpred(this._ctx, 5);
    		case 7:
    			return this.precpred(this._ctx, 4);
    		default:
    			throw "No predicate with index:" + predIndex;
    	}
    };




	program() {
	    let localctx = new ProgramContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 0, PrettyParserParser.RULE_program);
	    var _la = 0;
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 15;
	        this._errHandler.sync(this);
	        _la = this._input.LA(1);
	        while((((_la) & ~0x1f) === 0 && ((1 << _la) & 229412) !== 0)) {
	            this.state = 12;
	            this.stmt();
	            this.state = 17;
	            this._errHandler.sync(this);
	            _la = this._input.LA(1);
	        }
	        this.state = 18;
	        this.match(PrettyParserParser.EOF);
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	stmt() {
	    let localctx = new StmtContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 2, PrettyParserParser.RULE_stmt);
	    try {
	        this.state = 24;
	        this._errHandler.sync(this);
	        var la_ = this._interp.adaptivePredict(this._input,1,this._ctx);
	        switch(la_) {
	        case 1:
	            this.enterOuterAlt(localctx, 1);
	            this.state = 20;
	            this.vardecl();
	            break;

	        case 2:
	            this.enterOuterAlt(localctx, 2);
	            this.state = 21;
	            this.expr(0);
	            break;

	        case 3:
	            this.enterOuterAlt(localctx, 3);
	            this.state = 22;
	            this.while_();
	            break;

	        case 4:
	            this.enterOuterAlt(localctx, 4);
	            this.state = 23;
	            this.cond();
	            break;

	        }
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	vardecl() {
	    let localctx = new VardeclContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 4, PrettyParserParser.RULE_vardecl);
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 26;
	        this.match(PrettyParserParser.ID);
	        this.state = 27;
	        this.match(PrettyParserParser.T__0);
	        this.state = 28;
	        this.expr(0);
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	while_() {
	    let localctx = new WhileContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 6, PrettyParserParser.RULE_while);
	    var _la = 0;
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 30;
	        this.match(PrettyParserParser.T__1);
	        this.state = 31;
	        this.expr(0);
	        this.state = 32;
	        this.match(PrettyParserParser.T__2);
	        this.state = 36;
	        this._errHandler.sync(this);
	        _la = this._input.LA(1);
	        while((((_la) & ~0x1f) === 0 && ((1 << _la) & 229412) !== 0)) {
	            this.state = 33;
	            this.stmt();
	            this.state = 38;
	            this._errHandler.sync(this);
	            _la = this._input.LA(1);
	        }
	        this.state = 39;
	        this.match(PrettyParserParser.T__3);
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	cond() {
	    let localctx = new CondContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 8, PrettyParserParser.RULE_cond);
	    var _la = 0;
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 41;
	        this.match(PrettyParserParser.T__4);
	        this.state = 42;
	        this.expr(0);
	        this.state = 43;
	        this.match(PrettyParserParser.T__2);
	        this.state = 47;
	        this._errHandler.sync(this);
	        _la = this._input.LA(1);
	        while((((_la) & ~0x1f) === 0 && ((1 << _la) & 229412) !== 0)) {
	            this.state = 44;
	            this.stmt();
	            this.state = 49;
	            this._errHandler.sync(this);
	            _la = this._input.LA(1);
	        }
	        this.state = 57;
	        this._errHandler.sync(this);
	        _la = this._input.LA(1);
	        if(_la===6) {
	            this.state = 50;
	            this.match(PrettyParserParser.T__5);
	            this.state = 54;
	            this._errHandler.sync(this);
	            _la = this._input.LA(1);
	            while((((_la) & ~0x1f) === 0 && ((1 << _la) & 229412) !== 0)) {
	                this.state = 51;
	                this.stmt();
	                this.state = 56;
	                this._errHandler.sync(this);
	                _la = this._input.LA(1);
	            }
	        }

	        this.state = 59;
	        this.match(PrettyParserParser.T__3);
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}


	expr(_p) {
		if(_p===undefined) {
		    _p = 0;
		}
	    const _parentctx = this._ctx;
	    const _parentState = this.state;
	    let localctx = new ExprContext(this, this._ctx, _parentState);
	    let _prevctx = localctx;
	    const _startState = 10;
	    this.enterRecursionRule(localctx, 10, PrettyParserParser.RULE_expr, _p);
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 65;
	        this._errHandler.sync(this);
	        switch(this._input.LA(1)) {
	        case 15:
	            this.state = 62;
	            this.match(PrettyParserParser.ID);
	            break;
	        case 16:
	            this.state = 63;
	            this.match(PrettyParserParser.NUMBER);
	            break;
	        case 17:
	            this.state = 64;
	            this.match(PrettyParserParser.STRING);
	            break;
	        default:
	            throw new antlr4.error.NoViableAltException(this);
	        }
	        this._ctx.stop = this._input.LT(-1);
	        this.state = 93;
	        this._errHandler.sync(this);
	        var _alt = this._interp.adaptivePredict(this._input,8,this._ctx)
	        while(_alt!=2 && _alt!=antlr4.atn.ATN.INVALID_ALT_NUMBER) {
	            if(_alt===1) {
	                if(this._parseListeners!==null) {
	                    this.triggerExitRuleEvent();
	                }
	                _prevctx = localctx;
	                this.state = 91;
	                this._errHandler.sync(this);
	                var la_ = this._interp.adaptivePredict(this._input,7,this._ctx);
	                switch(la_) {
	                case 1:
	                    localctx = new ExprContext(this, _parentctx, _parentState);
	                    this.pushNewRecursionContext(localctx, _startState, PrettyParserParser.RULE_expr);
	                    this.state = 67;
	                    if (!( this.precpred(this._ctx, 11))) {
	                        throw new antlr4.error.FailedPredicateException(this, "this.precpred(this._ctx, 11)");
	                    }
	                    this.state = 68;
	                    this.match(PrettyParserParser.T__6);
	                    this.state = 69;
	                    this.expr(12);
	                    break;

	                case 2:
	                    localctx = new ExprContext(this, _parentctx, _parentState);
	                    this.pushNewRecursionContext(localctx, _startState, PrettyParserParser.RULE_expr);
	                    this.state = 70;
	                    if (!( this.precpred(this._ctx, 10))) {
	                        throw new antlr4.error.FailedPredicateException(this, "this.precpred(this._ctx, 10)");
	                    }
	                    this.state = 71;
	                    this.match(PrettyParserParser.T__7);
	                    this.state = 72;
	                    this.expr(11);
	                    break;

	                case 3:
	                    localctx = new ExprContext(this, _parentctx, _parentState);
	                    this.pushNewRecursionContext(localctx, _startState, PrettyParserParser.RULE_expr);
	                    this.state = 73;
	                    if (!( this.precpred(this._ctx, 9))) {
	                        throw new antlr4.error.FailedPredicateException(this, "this.precpred(this._ctx, 9)");
	                    }
	                    this.state = 74;
	                    this.match(PrettyParserParser.T__8);
	                    this.state = 75;
	                    this.expr(10);
	                    break;

	                case 4:
	                    localctx = new ExprContext(this, _parentctx, _parentState);
	                    this.pushNewRecursionContext(localctx, _startState, PrettyParserParser.RULE_expr);
	                    this.state = 76;
	                    if (!( this.precpred(this._ctx, 8))) {
	                        throw new antlr4.error.FailedPredicateException(this, "this.precpred(this._ctx, 8)");
	                    }
	                    this.state = 77;
	                    this.match(PrettyParserParser.T__9);
	                    this.state = 78;
	                    this.expr(9);
	                    break;

	                case 5:
	                    localctx = new ExprContext(this, _parentctx, _parentState);
	                    this.pushNewRecursionContext(localctx, _startState, PrettyParserParser.RULE_expr);
	                    this.state = 79;
	                    if (!( this.precpred(this._ctx, 7))) {
	                        throw new antlr4.error.FailedPredicateException(this, "this.precpred(this._ctx, 7)");
	                    }
	                    this.state = 80;
	                    this.match(PrettyParserParser.T__10);
	                    this.state = 81;
	                    this.expr(8);
	                    break;

	                case 6:
	                    localctx = new ExprContext(this, _parentctx, _parentState);
	                    this.pushNewRecursionContext(localctx, _startState, PrettyParserParser.RULE_expr);
	                    this.state = 82;
	                    if (!( this.precpred(this._ctx, 6))) {
	                        throw new antlr4.error.FailedPredicateException(this, "this.precpred(this._ctx, 6)");
	                    }
	                    this.state = 83;
	                    this.match(PrettyParserParser.T__11);
	                    this.state = 84;
	                    this.expr(7);
	                    break;

	                case 7:
	                    localctx = new ExprContext(this, _parentctx, _parentState);
	                    this.pushNewRecursionContext(localctx, _startState, PrettyParserParser.RULE_expr);
	                    this.state = 85;
	                    if (!( this.precpred(this._ctx, 5))) {
	                        throw new antlr4.error.FailedPredicateException(this, "this.precpred(this._ctx, 5)");
	                    }
	                    this.state = 86;
	                    this.match(PrettyParserParser.T__12);
	                    this.state = 87;
	                    this.expr(6);
	                    break;

	                case 8:
	                    localctx = new ExprContext(this, _parentctx, _parentState);
	                    this.pushNewRecursionContext(localctx, _startState, PrettyParserParser.RULE_expr);
	                    this.state = 88;
	                    if (!( this.precpred(this._ctx, 4))) {
	                        throw new antlr4.error.FailedPredicateException(this, "this.precpred(this._ctx, 4)");
	                    }
	                    this.state = 89;
	                    this.match(PrettyParserParser.T__13);
	                    this.state = 90;
	                    this.expr(5);
	                    break;

	                } 
	            }
	            this.state = 95;
	            this._errHandler.sync(this);
	            _alt = this._interp.adaptivePredict(this._input,8,this._ctx);
	        }

	    } catch( error) {
	        if(error instanceof antlr4.error.RecognitionException) {
		        localctx.exception = error;
		        this._errHandler.reportError(this, error);
		        this._errHandler.recover(this, error);
		    } else {
		    	throw error;
		    }
	    } finally {
	        this.unrollRecursionContexts(_parentctx)
	    }
	    return localctx;
	}


}

PrettyParserParser.EOF = antlr4.Token.EOF;
PrettyParserParser.T__0 = 1;
PrettyParserParser.T__1 = 2;
PrettyParserParser.T__2 = 3;
PrettyParserParser.T__3 = 4;
PrettyParserParser.T__4 = 5;
PrettyParserParser.T__5 = 6;
PrettyParserParser.T__6 = 7;
PrettyParserParser.T__7 = 8;
PrettyParserParser.T__8 = 9;
PrettyParserParser.T__9 = 10;
PrettyParserParser.T__10 = 11;
PrettyParserParser.T__11 = 12;
PrettyParserParser.T__12 = 13;
PrettyParserParser.T__13 = 14;
PrettyParserParser.ID = 15;
PrettyParserParser.NUMBER = 16;
PrettyParserParser.STRING = 17;
PrettyParserParser.COMMENT = 18;
PrettyParserParser.WS = 19;

PrettyParserParser.RULE_program = 0;
PrettyParserParser.RULE_stmt = 1;
PrettyParserParser.RULE_vardecl = 2;
PrettyParserParser.RULE_while = 3;
PrettyParserParser.RULE_cond = 4;
PrettyParserParser.RULE_expr = 5;

class ProgramContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = PrettyParserParser.RULE_program;
    }

	EOF() {
	    return this.getToken(PrettyParserParser.EOF, 0);
	};

	stmt = function(i) {
	    if(i===undefined) {
	        i = null;
	    }
	    if(i===null) {
	        return this.getTypedRuleContexts(StmtContext);
	    } else {
	        return this.getTypedRuleContext(StmtContext,i);
	    }
	};

	enterRule(listener) {
	    if(listener instanceof PrettyParserListener ) {
	        listener.enterProgram(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof PrettyParserListener ) {
	        listener.exitProgram(this);
		}
	}


}



class StmtContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = PrettyParserParser.RULE_stmt;
    }

	vardecl() {
	    return this.getTypedRuleContext(VardeclContext,0);
	};

	expr() {
	    return this.getTypedRuleContext(ExprContext,0);
	};

	while_() {
	    return this.getTypedRuleContext(WhileContext,0);
	};

	cond() {
	    return this.getTypedRuleContext(CondContext,0);
	};

	enterRule(listener) {
	    if(listener instanceof PrettyParserListener ) {
	        listener.enterStmt(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof PrettyParserListener ) {
	        listener.exitStmt(this);
		}
	}


}



class VardeclContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = PrettyParserParser.RULE_vardecl;
    }

	ID() {
	    return this.getToken(PrettyParserParser.ID, 0);
	};

	expr() {
	    return this.getTypedRuleContext(ExprContext,0);
	};

	enterRule(listener) {
	    if(listener instanceof PrettyParserListener ) {
	        listener.enterVardecl(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof PrettyParserListener ) {
	        listener.exitVardecl(this);
		}
	}


}



class WhileContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = PrettyParserParser.RULE_while;
    }

	expr() {
	    return this.getTypedRuleContext(ExprContext,0);
	};

	stmt = function(i) {
	    if(i===undefined) {
	        i = null;
	    }
	    if(i===null) {
	        return this.getTypedRuleContexts(StmtContext);
	    } else {
	        return this.getTypedRuleContext(StmtContext,i);
	    }
	};

	enterRule(listener) {
	    if(listener instanceof PrettyParserListener ) {
	        listener.enterWhile(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof PrettyParserListener ) {
	        listener.exitWhile(this);
		}
	}


}



class CondContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = PrettyParserParser.RULE_cond;
    }

	expr() {
	    return this.getTypedRuleContext(ExprContext,0);
	};

	stmt = function(i) {
	    if(i===undefined) {
	        i = null;
	    }
	    if(i===null) {
	        return this.getTypedRuleContexts(StmtContext);
	    } else {
	        return this.getTypedRuleContext(StmtContext,i);
	    }
	};

	enterRule(listener) {
	    if(listener instanceof PrettyParserListener ) {
	        listener.enterCond(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof PrettyParserListener ) {
	        listener.exitCond(this);
		}
	}


}



class ExprContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = PrettyParserParser.RULE_expr;
    }

	ID() {
	    return this.getToken(PrettyParserParser.ID, 0);
	};

	NUMBER() {
	    return this.getToken(PrettyParserParser.NUMBER, 0);
	};

	STRING() {
	    return this.getToken(PrettyParserParser.STRING, 0);
	};

	expr = function(i) {
	    if(i===undefined) {
	        i = null;
	    }
	    if(i===null) {
	        return this.getTypedRuleContexts(ExprContext);
	    } else {
	        return this.getTypedRuleContext(ExprContext,i);
	    }
	};

	enterRule(listener) {
	    if(listener instanceof PrettyParserListener ) {
	        listener.enterExpr(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof PrettyParserListener ) {
	        listener.exitExpr(this);
		}
	}


}




PrettyParserParser.ProgramContext = ProgramContext; 
PrettyParserParser.StmtContext = StmtContext; 
PrettyParserParser.VardeclContext = VardeclContext; 
PrettyParserParser.WhileContext = WhileContext; 
PrettyParserParser.CondContext = CondContext; 
PrettyParserParser.ExprContext = ExprContext; 
