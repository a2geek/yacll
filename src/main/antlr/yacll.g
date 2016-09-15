header {
	package a2geek.yacll.antlr;
	import a2geek.yacll.interpreter.*;
	import java.util.*;
}

class YacllParser extends Parser;
options {
	buildAST = true;
}
tokens {
	// Structural root nodes:
	PGM;		TEST;		BLOCK;		PARAM;		UMINUS;
	CALL;		ASSIGN;		FNBLOCK;	FNPARAM;
	// Manually identified operations:
	LT;			LE;			GT;			GE;
}
{
	public List<String> errors = new ArrayList<String>();
	public List<String> warnings = new ArrayList<String>();
	public void reportError(RecognitionException ex) {
		errors.add(AntlrHelper.format(ex));
	}
	public void reportError(String error) {
		errors.add(error);
	}
	public void reportWarning(String message) {
		warnings.add(message);
	}	
}

program
	:	(useStatement)*
		(function)* 
		(statement)* 
		EOF!							{ ## = #([PGM,"program"], ##); }
	;
includedProgram
	:	(function)*
		EOF!							{ ## = #([PGM,"includedProgram"], ##); }
	;
useStatement
	:	USE^ STRING SEMI!
	;
statement
	:	(IDENT EQ) => assignment SEMI!
	|	(IDENT LPAREN) => functionCall SEMI!
	|	WHILE^ (condition) block
	|	FOR^ LPAREN! assignment SEMI! condition SEMI! assignment RPAREN! block
	|	REPEAT^ block UNTIL! LPAREN! condition RPAREN! SEMI!
	|	invokeStatement SEMI!
	;
invokeStatement
	:	INVOKE^ LPAREN! STRING (COMMA! idList)? RPAREN!
	;
condition
	:	expression						{ ## = #([TEST,"condition"], ##); }
	;
functionCall
	:	IDENT LPAREN! idList RPAREN!	{ ## = #([CALL,"call"], ##); }
	;
assignment
	:	IDENT EQ! expression			{ ## = #([ASSIGN,"="], ##); }
	;
expression
	:	comparativeExpression
	;
comparativeExpression
	:	additiveExpression ( (LT^|GT^|LE^|GE^|EQ^|NE^) additiveExpression )*
	;
additiveExpression
	:	multiplicativeExpression ( (PLUS^|MINUS^) multiplicativeExpression )*
	;
multiplicativeExpression
	:	unaryExpression ( (STAR^|SLASH^) unaryExpression)*
	;
unaryExpression
	:	atom
	|	MINUS! unaryExpression				{ ## = #([UMINUS,"unary-"], ##); }
	;
atom:	(IDENT LPAREN) => functionCall
	|	IDENT
	|	NUMBER
	|	STRING
	|	invokeStatement
	|	LPAREN! expression RPAREN!
	;
block
	:	statement							{ ## = #([BLOCK,"block"], ##); }
	|	LCURLY! (statement)+ RCURLY!		{ ## = #([BLOCK,"block"], ##); }
	;
function
	:	FUNCTION^ IDENT LPAREN! functionParameters RPAREN! functionBlock
	;
functionParameters
	:	IDENT ( COMMA! IDENT )*				{ ## = #([FNPARAM,"fnParameters"], ##); }
	|	/* empty */
	;
functionBlock
	:	LCURLY! 
			(statement)*
			(RETURN^ expression SEMI!)? 
		RCURLY!								{ ## = #([FNBLOCK,"fnBlock"], ##); }
	;
idList
	:	expression ( COMMA! expression )*	{ ## = #([PARAM,"parameters"], ##); }
	|	/* empty */
	;

class YacllLexer extends Lexer;
options {
	caseSensitive = false;
	k = 2;	// for comments
}
tokens {
	FOR = "for";
	FUNCTION = "function";
	INVOKE = "invoke";
	REPEAT = "repeat";
	RETURN = "return";
	UNTIL = "until";
	USE = "use";
	WHILE = "while";
}
{
	public List<String> errors = new ArrayList<String>();
	public List<String> warnings = new ArrayList<String>();
	public void reportError(RecognitionException ex) {
		errors.add(AntlrHelper.format(ex));
	}
	public void reportError(String error) {
		errors.add(error);
	}
	public void reportWarning(String message) {
		warnings.add(message);
	}	
}

protected LETTER : 'a'..'z' ;
protected DIGIT  : '0'..'9' ;

SEMI	:	";" ;
EQ		:	"=" ;
NE 		:	"!=" ;
LT_OR_LE_OR_NE
		:	("<>") => "<>"	{ $setType(NE); }
		|	("<=") => "<="	{ $setType(LE); }
		|	"<"				{ $setType(LT); }
		;
GT_OR_GE:	(">=") => ">="	{ $setType(GE); }
		|	">"				{ $setType(GT); }
		;
PLUS	:	"+" ;
MINUS 	:	"-" ;
STAR 	:	"*" ;
SLASH 	:	("//") => "//" (~('\n'))*
							{ $setType(Token.SKIP); }
		|	("/*") => "/*" ( options {greedy=false;} 
		                   : '\n' { newline(); } 
		                   | .
		                   )* "*/"
							{ $setType(Token.SKIP); }
		|	"/" ;
LPAREN 	:	"(" ;
RPAREN 	:	")" ;
LCURLY 	:	"{" ;
RCURLY 	:	"}" ;
COMMA 	:	"," ;
PERIOD 	:	"." ;

IDENT	:	LETTER (LETTER | DIGIT)* ;
NUMBER	:	(DIGIT)+ ( PERIOD (DIGIT)+ )? ;
STRING	:	'"'! (~('"'))* '"'! ;

WS	:	( " " 
		| "\t" 
		| "\r\n" { newline(); }
		| "\n"   { newline(); }
		)
		{ $setType(Token.SKIP); }
	;


class YacllGenerator extends TreeParser;
{
	public List<String> errors = new ArrayList<String>();
	public List<String> warnings = new ArrayList<String>();
	public void reportError(RecognitionException ex) {
		errors.add(AntlrHelper.format(ex));
	}
	public void reportError(String error) {
		errors.add(error);
	}
	public void reportWarning(String message) {
		warnings.add(message);
	}	
}

program returns [Program program]
{
	Statement s;
	FunctionDefinition f;
	UseStatement u;
	program = new Program(); 
}
	:	#(PGM 
			(u=useStatement	{ program.addUseStatement(u); })*
			(f=function		{ program.addFunction(f); })*
			(s=statement 	{ program.addStatement(s); })*
		 )
	;
useStatement returns [UseStatement useStatement]
{
	useStatement = null;
}
	:	#(USE name:STRING)					{ useStatement = new UseStatement(name.getText()); }
	;
function returns [FunctionDefinition function]
{
	CompositeStatement b;
	List<String> l = null;
	function = null;
}
	:	#(FUNCTION id:IDENT (l=fnParam)? b=block)
											{ function = new FunctionDefinition(id.getText(),l,b); }
	;
statement returns [Statement statement]
{
	CompositeStatement b;
	Statement s,c,a,i;
	statement = null;
}
	:	#(WHILE c=condition b=block)		{ statement = new WhileStatement(c,b); }
	|	#(REPEAT b=block c=condition)		{ statement = new RepeatStatement(b,c); }
	|	#(FOR a=assignment c=condition i=assignment b=block)
											{ statement = new ForStatement(a,c,b,i); }
	|	s=assignment						{ statement = s; }
	;
condition returns [Statement statement]
{
	Statement e;
	statement = null;
}
	:	#(TEST e=expression)				{ statement = e; }
	;
assignment returns [Statement statement]
{
	Statement e;
	statement = null;
}
	:	#(ASSIGN id:IDENT e=expression)		{ statement = new AssignmentStatement(id.getText(), e); }
	|	e=expression						{ statement = e; }
	;
expression returns [Statement statement]
{
	Statement a;
	Statement b;
	List<Statement> l = null;
	statement = null;
}
	:	#(LT a=expression b=expression)		{ statement = new BinaryOperator(Operation.LT, a, b); }
	|	#(GT a=expression b=expression) 	{ statement = new BinaryOperator(Operation.GT, a, b); }
	|	#(LE a=expression b=expression) 	{ statement = new BinaryOperator(Operation.LE, a, b); }
	|	#(GE a=expression b=expression) 	{ statement = new BinaryOperator(Operation.GE, a, b); }
	|	#(EQ a=expression b=expression) 	{ statement = new BinaryOperator(Operation.EQ, a, b); }
	|	#(NE a=expression b=expression) 	{ statement = new BinaryOperator(Operation.NE, a, b); }
	|	#(PLUS a=expression b=expression) 	{ statement = new BinaryOperator(Operation.ADD, a, b); }
	|	#(MINUS a=expression b=expression) 	{ statement = new BinaryOperator(Operation.SUB, a, b); }
	|	#(STAR a=expression b=expression) 	{ statement = new BinaryOperator(Operation.MUL, a, b); }
	|	#(SLASH a=expression b=expression) 	{ statement = new BinaryOperator(Operation.DIV, a, b); }
	|	#(UMINUS a=expression) 				{ statement = new UnaryOperator(Operation.MINUS, a); }
	|	#(CALL fn:IDENT (l=idList)? )		{ statement = new FunctionInvocationStatement(fn.getText(), l); }
	|	#(RETURN a=expression)				{ statement = new ReturnStatement(a); }
	|	#(INVOKE a=expression (l=idList)? )	{ statement = new InvokeStatement(a,l); }
	|	id:IDENT							{ statement = new VariableReference(id.getText()); }
	|	n:NUMBER							{ statement = new ConstantValue(Double.valueOf(n.getText())); }
	|	s:STRING							{ statement = new ConstantValue(s.getText()); }
	;
block returns [CompositeStatement statement]
{
	Statement s;
	statement = new CompositeStatement();
}
	:	#(BLOCK		(s=statement { statement.addStatement(s); })+ )
	|	#(FNBLOCK	(s=statement { statement.addStatement(s); })+ )
	;
idList returns [List<Statement> list]
{
	Statement e;
	list = new ArrayList<Statement>();
}
	:	#(PARAM (e=expression { list.add(e); })+ )
	;
fnParam returns [List<String> list]
{
	list = new ArrayList<String>();
}
	:	#(FNPARAM (id:IDENT { list.add(id.getText()); })+ )
	;
