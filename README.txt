COS 360   Homework Exercise #4

Lexical Analyzer Code Due Date:  11/5/16 @ 8 AM

Translater Code Due Date: 11/28/16 @ 8 AM

This project involves writing a translator from a made up 
imperative language for programs that use CofinFin sets to
Java code that performs the operations of the language.  
You will code the solution in two parts.  The first part is
to code a lexical analyzer

setScanner.java

that breaks up the input file into a stream of tokens.  A class for
representing the tokens is provided in Token.java and some initial code
and a more detailed spec for the lexical analyzer is in 
setScannerTemplate.txt

The second part is to code the predictive parser and code generator in
a Java class

setTranslator.java

that uses the setScanner and Token classes to convert a source code
file in the language defined by the grammar below into Java source
code.

Included in this kit are 

1. the discussion of the problem, README.txt, the file you are reading
2. a class for the tokens, Token.java
3. a skeleton for the lexical analyzer, setScannerTemplate.txt
4. a driver program for testing the setScanner class, ScannerDriver.java
5. a skeleton for the translator, setTranslatorTemplate.txt
6. some test data files for both the lexical analyzer and the translator

I will provide correct results for these test files later.  I may work up
some more test files.


This is a substantial coding project.  I want you to work in teams
of at least two persons, possibly three, for what you can learn 
from each other and to give you some team programming experience.
You can form up into teams on your own, and inform me of them by
Wednesday, 10/26.  Anyone who is not part of a team by then will be
assigned to a team by me.

I suggest some ideas for the lexical analyzer in the template, but 
you may chart your own course, as long as the code you develop does break
up the source  into the appropriate sequence of tokens and does 
provide lookahead w/o advancing as well as advancing/consuming
according to the specs given in setScannerTemplate.txt. Predictive 
parsing requires those operations.

IMPORTANT NOTE: If you use Java's Scanner class, it defaults
to use ws as token separators so a line like

s:={1,2,3};

would come in as a single token, where for this grammar it
would actually be ten tokens.

s := { 1 , 2 , 3 } ;

In the template I suggest reading each line into a character array and
scanning the characters of the array to determine the tokens according
to their definitions in Token.java.

The translator should check String[] args length.  If it is 0, it should read
the source code for translations from standard in.  If the length is > 0, it
should get the name of the file to translate from args[0], throwing an
exception if it cannot create a Scanner from a file with that name.

When you process an input source program in this language, you
maintain within your overall translator program a number of global
(in Java, that means static) data variables and structures that record 
information you've seen as you scan the source so that you can
refer to it later.  I discuss these below as they come up.

The translator will emit code for the result of the translation.  Our target
language is Java, so what you emit needs to be correct Java code.

There are only two kinds of variables in the source language:
natural number variables and set variables.  The former will be
declared as int variables in the target language(Java) and the latter 
will be declared as CofinFin variables.  We do have expressions 
of type boolean and the one boolean operator, negation, but no 
variables of type boolean and no other boolean operators.  

We also do not have any arithmetic operators.  The only expressions
we have for natural numbers are variables and constants.

The notion of the source language is that it calculates CofinFin 
values and at the very end one such value will be printed using the
toString method of that class.  The shortest legal program is

program s
var
begin
end
{}.

which should generate a Java class source codefile, s.java,
as something like the following.

public class s{

public static void main(String[] args){

   int[] $iv1 = {};
   CofinFin $sv1 = new CofinFin(false,$iv1);
   System.out.println($sv1.toString());
}
}

Actually, I don't expect you to do any indentation, but you
should have each Java statement begin a new line and insert
blank lines to break up the major sections of the result.

Everything but the <if> statement rules is worth 90% of the
value of the translator part of the project.  The <if> 
statement part is worth the final 10 points of the translator part.


Grammar rules below are terminated by a ;, but of course, that
semicolon is not part of the last rhs.

Grammar variables are enclosed in angle brackets.

Tokens(terminals) as they occur in the productions below are
named by upper case identifiers.  The Token.java class explains 
what strings and patterns those identifiers stand for.  The 
identifiers themselves are declared as static final int values in
Token.java, and serve to name the kind of the token.

The complete token list is as follows, with the name of the token
first as an upper case identifier, then what it is as a character
string or regular expression pattern, and possibly a brief 
discussion at the end.


PROGRAM  "program"  a reserved word, the first word of any 
                    valid program
ID   [a-zA-Z][a-zA-Z0-9]*   except for reserved words;
                    identifiers must be declared to be either
                    a set type or a natural number type, and
                    when you see the declarations you will record
                    the declared type in a symbol table that the
                    translator uses for type checking
VAR      "var"      a reserved word
BEGIN    "begin"    a reserved word
END      "end"      a reserved word
IF       "if"       a reserved word
ELSE     "else"     a reserved word
ENDIF    "endif"    a reserved word
NAT      "nat"      a reserved word
SET      "set"      a reserved word
NATCONST 0|[1-9][0-9]*  a string for a nonnegative literal
                        with no extraneous leading 0's
LEFTBRACE '{'
RIGHTBRACE '}'
LEFTPAREN '('
RIGHTPAREN ')'
SEMICOLON ';'
PERIOD  '.'
COMMA ','
ASSIGN ":="  for assignment
SUBSET "<="  for is subset of; a comparison operator for sets
EQUALS '='   for equality comparisons of sets
NOT "not" for boolean negation; so "not" is reserved
INTERSECTION '*'   for set intersection, a binary set
                   operator
UNION '+'   for set union, a binary set operator
SETDIFFERENCE '\'   for set difference, a binary set operator
COMPLEMENT '-'   for unary set complement
IS_IN  "in"  for set membership; so "in" is reserved
THEN    "then"    a reserved word
CMP    "CMP"      a reserved word for the cofinite literals
EOF a special token that the lexical analyzer sends when it has 
    encountered the end of file
UNRECOGNIZED  for anything else, so the lexical analyzer can
              signal the parser that it encountered something 
              invalid

See Token.java for more.

The language is case sensitive, so as identifiers, xyz, Xyz, xYz, and
xyZ are all distinct.

Below is an example program.  The overall structure has nine parts.

program id
var
natDeclarations(optional)
setDeclaratins(optional)
begin
statementList
end
setExpression
.


program p17

var

nat i,j,k;

set w,s,t,v;

begin
   s := {};
   t := s;
   i := 1;
   w := s * t \ CMP {} + -CMP {1,2};
   v := w;
   j := i;

   if 0 in s then
      t := {1}
   endif;

   if i in CMP{0,1,2,3} then
      s := s +{2}
   endif;

   if {} <= s then
      t := t * {5};
      w := w \ t
   endif;

   if w <= CMP{} then
      s := s + {4}
   endif;

   if {} = s then
      t := t + {9}
   endif;

   if w = CMP{} then
      s := t \ {8}
   else
      if CMP{} = {0,1} then
        t := t + {11}
      endif
   endif;

   w := t \ - s + t \ CMP {0} + {10,20,30,2}
end

s + t \ w * {1,2,3,4}
.


Note that ; is a separator for a list of statements, but is not required
after the last statement in a list.  In particular, there are no semicolons
before else, endif, or end.  Statement lists are bracketed by one of the 
pairs begin-end, then-endif, then-else, or else-endif.

Predictive parsing translation typically has a function/method for each
grammar variable.  The method will be called when the leftmost derivation
that is in process has its variable as the next symbol and the lookahead
matches one of its rhs's lookahead sets.  

Generally, whenever the method V() for grammar variable V is called, the
parser should first check that the lookahead is in the the lookahead sets
for one of the rhs's of V.  If that is not the case, then the parser should
throw an exception with an error message of the form

[line ln]: tokenlist expected.

where ln is the line number from which the invalid token came, and 
tokenList is a comma separated list of the labels for the acceptable tokens,
which may be a single token.  For example, a program should begin with 
the reserved word "program" and no other token.  The labels are given in
TOKEN_LABELS, a public static final array of String in Token.java.

If this test is performed BEFORE the variables method V() is called, then
that method can assume that the lookahead token is acceptable for one of
its rhs's.

Similarly, if the next parsing operation is a matching operation with a specific
token tk, and the lookahead token does NOT match tk, then the parser should
throw an exception with the message

[line ln]: tk's label as you obtain it from TOKEN_LABELS is expected.

There are other error conditions that you are asked to test for below, and
specific error messages are asked for.  Generally, you should add the
"[line ln]: " text to the beginning of those messages, and use for ln the 
getLineNumber() result from the last token that revealed the error, whether it
has been consumed or is still a lookahead token.

If an exception is thrown, that ends the parse.


Here's the grammar.

"" is used in the rules for the empty string.  I put comments below the 
rule explaining its  meaning, indicating the lookaheads,  and giving suggestions for 
implementing the method for the variable on the lhs.


<program> ::= PROGRAM ID VAR <dec> BEGIN <st list> END <out>;

/***

All programs begin with the reserved word and a name, followed by
a reserved word introducing some declarations(possibly empty), 
followed by a sequence of statements(possibly empty) bracketed 
by two reserved words that appear nowhere else, followed by the
output set result from the program.

The intended translation will be a Java class definition on a
file named id.java, where id is the specific identifier that matches ID.
The template declares a static PrintStream object for the destination of
emitted code, and you should create such an object from a File object that
you name id.java.

The structure of the emitted code will be(the comments are intended to
explain the result translation and are not to be placed in that
result translation)

public class id{  // same as the specific identifier after PROGRAM
                  // which will be in that token's tokenString data member

// private static declarations to deal with the variable
// declarations of nat and set variables, if there are any

public static void main (String[] args){

// sequenced statements to achieve the calculations of
// <st list>
// followed by
System.out.println(exp.toString());
// exp will be determined from the <out> statement;
// and may require other declarations of variables and
// statements to evaluate the expression.
// The result of your translation should be correct
// Java syntax so the program can be compiled and run,
// but you only need to do the error checking that I 
// explicitly ask for.  You don't need to check that
// a Java reserved word has been used for an identifier,
// for example.
}
}

The translator creates the PrintStream and emits the code 

public class id{

to the PrintStream when it consumes ID.

It emits the code for the private static declarations of
int and CofinFin variables of the <dec> variable in the course
of processing it, as described below.

It emits the code

public static void main (String[] args){

when it consumes BEGIN.

It emits Java statements for <st list> in the course of processing
it as described below, and also statements to evaluate the 
<out> expression, finishing with 

System.out.println(exp.toString());
}
}
// Parsing completed successfully.


where exp will depend on where the final result of evaluating the
<out> expression lies.  The two final right curly braces are to
close the main method and the class definition.

You will need to create a File with the name id.java, and a
PrintStream object from the file.

The only nullable variables in the grammar are

<nat dec>, <set dec>, <st list>, <nat list>

to accommodate empty lists.

***/

<dec> ::= <nat dec> <set dec>;

/***

the lookahead set is

NAT, SET, BEGIN

The declarations declare natural number variables
first, then set variables.

In addition to generating the code as described below,
the translator should create a symbol table to record the types
of the declared variables so you can check that they are
used appropriately in expressions.  For example,

<nat exp> IS_IN <set exp>

might be realized in the source code by

n in s

and you would want to check that n has been declared as
a nat and s has been declared as a set.  There are a number
of declaration errors that you should test for, as described
below.

A Java Map object is appropriate for the symbol table, with
a String for the key.  It should be able to answer the following
for any String s

Is s declared as a variable?
Is s declared as a nat variable?
Is s declared as a set variable?

***/


<nat dec> ::= "" | NAT <ne var list> SEMICOLON ;

/***

the lookahead set for the first alternative is

SET, BEGIN

and for the second is

NAT

there may not be any natural number declarations, or
there will be a non empty list of variables, all
of which should be declared in the destination file
to be static and of type int.  Each should be initialized
to 0, so if the code were

nat n, m, k;

you should translate it to

private static int n;
private static int m;
private static int k;


That's what should be emitted to the target file, but
the actual emission will take place in neVarList().

ERROR CHECKING(again, this would actually take place
in neVarList()). There should be no duplicates in the list, so

nat n,m,k,n;

should throw a parsing exceptions with the message

"n is a duplicate nat variable declaration."

when the translator goes to install the second n.  So
the second n is the token that reveals the error.

Here and below, we will assume that the lookahead has been
examined and is appropriate for the grammar variable, which 
means it is in the union of the lookahead sets for the rhs's
of the variable. That should be tested before the method for
the variable is called, and it it fails, an exception should
be thrown that the those tokens are expected.  Here that is

if (lookahead is not one of NAT, BEGIN, SET)
   throw an exception with message "\"nat\", \"set\", or \"begin\" expected.";
else
   natDec() ;


and more generally, for a grammar variable V

if (lookahead is not one of the members of the union of the rhs's
    of V)
   throw an exception with the message "... expected." where ...
   lists the TOKEN_LABELS string for the tokens of that union;
   the labels put double quotes around the reserved words
else
   V();

The actual code for natDec() would go something like

if (lookahead = NAT){
   consume();
   if (lookahead is not ID)
      throw exception with message "identifier expected.";
   else
      set();
   if (lookahead = SEMICOLON)
      consume();
   else
      throw an exception with message
      "semicolon expected."
}
else // no nat declarations
   return;
}

***/


<set dec> ::=  "" | SET <ne var list> SEMICOLON ;

/***

The lookahead set for the first alternative is 

BEGIN

and for the second is 

SET

This is handled just like the nat declarations, except that when
you install the variables in the symbol table and emit the code
to declare them, they should be installed as set variables and
declared as private static CofinFin variables.

You can accomplish this distinction by using a static boolean 
variable inNatDecs that you initialize to true and set to false
when you see SET.  The code in neVarList() can examine that static
variable to determine whether to install a variable as a nat or
set variable and whether to declare them as int or CofinFin
variables.  All nat variables are declared before all set variables,
so the same neVarList code can handle each.

Emitted code is similar to the nat declarations, except here the 
variables are to be declared CofinFin and initialized to empty, so for

set  s,t,v;

private static CofinFin s = new CofinFin();
private static CofinFin t = new CofinFin();
private static CofinFin v = new CofinFin();

would be appropriate. 

ERROR CHECKING

Like for nat declarations, but also none of the set
variables should have occurred in the nat declarations.

nat n;
set n;

should throw an exception with message

"Set variable n previously declared as nat."

Duplicates should throw an exception with a message.
For example,

set s,u,v,s;

should yield

"s is a duplicate set variable declaration."


***/

<ne var list> ::= ID | ID COMMA <ne var list>;

/***

You will note that that the lookahead sets for these two rhs's
are the same, but we can get around that with Arden's lemma,
which reworks this to

<ne var list>  =  (ID COMMA)* ID = ID (COMMA ID)*

So your neVarList() code is going to look like(we are assuming
that the lookahead is indeed ID).


get ID and consume it out of the token stream;
attempt to install ID in symbol table
and emit the Java declaration for it, but
check if it's a duplicate and throw an
exception if there is a problem.

while (lookahead = COMMA){
   consume the COMMA out of the token stream;
   if (next token is ID){
      get ID and consume it out of the token stream;
      attempt to install ID in symbol table
      and generate the Java declaration for it,
      throwing an exception if there is a problem;
   }
   else
      throw an exception with the message
      "Identifier expected."
}

Note that when you get the ID value, you will need
to see if it has already been installed, and throw
an exception if it has been.  If you are processing
set declarations and it was installed as a nat, then
the message is a little different from if had been 
installed as a set.

You use inNatDecs to tell which kind of declaration
you are processing.



***/

<st list> ::= "" | <ne st list>;

/***

the statement list might be empty.

The lookahead set for the first alternative is 

ELSE, ENDIF, END

and for the second is

ID, IF

The work of emitting code is done in neStList().

the code here is roughly

if (lookahead is ID or IF)
  neStList();


***/


<ne st list> ::=  <st> |  <st>  SEMICOLON <ne st list>;

/***

a non empty statement list should generate code to
realize the effects of the statements of the list
in the order given.  We will only deal with 
assignment statements and if statements, and their
translation shouldn't be too difficult, but you do need
to emit the code in the right order.

Using Arden's lemma, this particular rule can be rewritten to 

<ne st list> = (<st> SEMICOLON)* <st>  which is equal
to  <st> ( SEMICOLON <st>)* as a language, so something like

neStList(){  // assumes lookahead is IF or ID

st();  // should emit all the code needed for the statement
while (lookahead = SEMICOLON){
   consume the SEMICOLON
   if (lookahead is ID or IF)
      st();
   else
      throw an exception with the message 
      "identifier or if expected."
}


***/

<st> ::= <asgn> | <if> ;

/***

an assignment always begins with a declared variable and an
if  with the if reserved word, so there is no problem determining
which alternative to call.  The code is roughly

if (lookahead = ID)
   asgn();
else if (lookahead = IF)
   if();

***/



<asgn> ::= <set asgn> | <nat asgn>; 

/***

The lookahead is the same for both alternatives: ID.

I've written the grammar to enforce type restrictions
so that you don't have to test for that except where variables 
occur in expressions, but it does mean you have to examine the
variable to see if it's declared to be a nat or a set.
The code will be roughly(note this only gets called when ID
is the next token)

asgn(){ // assumes lookahead is ID

  Token id = lookahead; // DO NOT CONSUME

   if (id has not been declared)
      throw an exception with message
      "Identifier not declared."
   else  if (ID has been declared a nat)
      natAsgn();
   else  // the only other alternative is declared as a set
      setAsgn();
}

***/

<set asgn> ::= ID ASSIGN <set exp>

/***

Evaluating expressions is the most complicated part of the
problem, so we discuss it generally here and give an example of
one of the level methods below.

The relevant part of the grammar rules is

<set asgn> ::= ID ASSIGN <set exp>
<set exp> ::= <set exp> SETDIFFERENCE <set level 2> | <set level 2>;
<set level 2> ::= <set level 2> UNION <set level 1> | <set level 1>;
<set level 1> ::= <set level 1> INTERSECTION <set level 0> | <set level 0>;
<set level 0> = COMPLEMENT <set level 0> | <set atomic>
<set atomic> ::= ID | <set const> | LEFTPAREN <set exp> RIGHTPAREN;
<set const> ::= <complemented> | <set literal>;
<complemented> ::= CMP <set literal>;
<set literal> ::= LEFTBRACE <nat list> RIGHTBRACE;

where <nat list> can be empty, or a sequence of nat constants separated by commas.

For set expressions, the precedence from high to low is

- (unary) for complement
* for intersection
+ for union
\ for set difference(note that A\B =  A*(-B) so you can translate this
  into your basic CofinFin operators in this manner)

All binary operators associate to the left. The grammar is set up to enforce
those precedences and associativities.

The general idea will be to have the methods for each level variable
emit code for evaluating the expression they cover, and save the name of the
Java program variable that HOLDS the result in static String variable dedicated
to that level.  Thus, the translator needs to declare

private static String
  setExpResultVariable,
  setLevel2ResultVariable,
  setLevel1ResultVariable,
  setLevel0ResultVariable,
  setAtomicResultVariable,
  setConstResultVariable,
  setComplementedLiteralResultVariable,
  setLiteralResultVariable;

In addition, when we have an expression like

{100,101} + {2} + {3} + {4,5}

We will need to generate temporary variables to hold intermediate
results, since we can only union up two sets at a time.  The code for
obtaining a CofinFin variable for a literal like {100,101} will be

int[] $iv1 = { 100, 101};
CofinFin $sv1 = new CofinFin(false, $iv1);

so we will also need to generate temporaries for int[] variables.

For the purpose of generating temporaries, it is convenient to use
the following

private static String setTempPrefix = "$sv";
private static String natArrTempPrefix = "$iv";
private static int 
    nextNatArrTempSuffix,
    usedSetTemps,// tracks the set temp variables that are 
                 // currently in use
    decSetTemps; // tracks the set temp variables that have been
                 // declared in the current scope;
                 // discussed below; generally, usedSetTemps <= decSetTemps

and use

static String nextTemp(boolean isSet){

   if (isSet){
      return setTempPrefix + (++decSetTemps);
   }
   else
      return natArrTempPrefix + (++natArrTempSuffix);
}

to obtain fresh temp variables.  Because ID variables must begin with
a letter, there is no chance for a name clash in the resulting Java file.

Now, 

<set asgn> ::= ID ASSIGN <set exp>

is handled by(assuming the lookahead is ID)

setAsgn(){

   get ID and consume it; suppose id is its tokenString;
   if it is not declared as a set variable, then throw
   an exception with the message id + " not declared as a set variable."

   if lookahead is not ASSIGN then
      throw an exception with the message "assignment operator(:=),  expected."
   else{
      consume the lookahead;
      if (lookahead is not one of CMP, LEFTBRACE, ID, LEFTPAREN, 
         COMPLEMENT)
         throw an exception with the message that lists them out as
         expected tokens, using TOKEN_LABELS
      else{
         setExp();
         emit code id + " = " + setExpResultVariable + ";\n" to
         the output file;
      }
   }
}

We look at one of the level variable methods in detail below.  

***/


<set exp> ::= <set exp> SETDIFFERENCE <set level 2> | <set level 2>;

/***

Note, the lookahead set for <set exp> is

CMP, LEFTBRACE, ID, LEFTPAREN, COMPLEMENT

and we assume that has been tested already.

As with <ne st list> above, the whole <set exp> productions 
can be reworked to 

<set level 2> (SETDIFFERENCE <set level 2>) *
 
and the method for this goes

setExp(){
   boolean needANewTemp;
   String res; // string identifying the variable holding the result
   setLevel2();
   Token tk = lookahead;
   int tkN = tk.getTokenId();
   if (slevel2ResultVar.charAt(0) == '$'  || tkN != Token.SETDIFFERENCE)
      res = slevel2ResultVar;
   else{
      // the result is a program variable and we will need to perform an op

      needANewTemp = usedSetTemps == decSetTemps;

      if (needANewTemp){
         res = nextTemp(true);
         usedSetTemps++;
      }
      else
         res = setVarTempPrefix + (++usedSetTemps);

      // if it's new, we have to declare it;
      emit code ((needANewTemp? "CofinFin " : "") + res + " = " + slevel2ResultVar + ';');
   }


   while (tkN = Token.SETDIFFERENCE){
      lex.consume();
      tk = lookahead;
      tkN = tk.getTokenType();
      if (tkN != Token.CMP && tkN != Token.LEFTBRACE
         && tkN != Token.ID && tkN != Token.LEFTPAREN && 
         tkN != Token.COMPLEMENT)
         throw exception with message listing the expected tokens
      else{
         slevel2();
         emit code for res = res.intersect(sLevel2ResultVar.complement());
         tk = lookahead;
         tkN = tk.getTokenType();
      }

   }
   setExpResultVariable = res;
}

Note, we finish by placing the res string in setExpResultVariable, so
subsequent code will be able to find it.

We want to explain the code that selects the result variable res.  We could always
generate a fresh temporary variable for that purpose, but a statement like

sv1 := (((((((((( sv2 ))))))))))

or

sv1 := {100,101} \ {2} + {3} * {4,5}

would generate many more temporaries than are actually needed.  The code is written
to that we will REUSE the variable from the higher precedence level if we can.
Since our loop for processing the \ occurrences will have a side effect to the
res variable

res = res.intersect(...);

we cannot reuse the variable if we were to take a turn through the loop body and
the result variable were in fact one of the declared set variables of the program.
The first test

if (slevel2ResultVar.charAt(0) == '$'  || tkN != Token.SETDIFFERENCE)

is true when either the higher precedence level result variable is NOT a 
declared set variable, but is instead a temp variable, or we will not be
executing a turn through the loop.  If that test is false, then it is a
declared set variable of the source program and we are executing the loop,
so we do not want to modify it and consequently will use a temp variable to
hold the result.

The second test

needANewTemp = usedSetTemps == decSetTemps;

if (needANewTemp){

allows us to reuse temp variables.  Suppose we have two assignments in a
row.

sv1 := {100,101} \ {2} \ {3} \ {4,5};
sv2 := sv1 * sv2 + {100,101} \ {2,3,4,5};

We will use temp variables for the first one, but once it is completed,
we can reuse them in the second.  The usedSetTemps int variable can be set
back to 0 when we complete the first assignment statement, allowing us to
just reuse the temp set variables that have already been declared.

These patterns should be used for the other level variables to keep the
number of declared set temp variables from ballooning up.

***/

<set level 2> ::= <set level 2> UNION <set level 1> | <set level 1>;

/*** 

Again, this becomes

<set level 2> =  <set level 1> (UNION <set level 1>)*

The lookahead for <set level 1>  is same as for <set exp>.

You should be able to adapt the approach given for setExp() to
this situation.


***/

<set level 1> ::= <set level 1> INTERSECTION <set level 0> | <set level 0>;

/***

Again, this yields

<set level 1> = <set level 0> (INTERSECTION <set level 0>)*

The lookahead set is same as for <set exp>.


You can adapt the code for setExp().

***/


<set level 0> ::= COMPLEMENT <set level 0> | <set atomic> ;

/***

This time it's

<set level 0> = COMPLEMENT* <set atomic>

The lookahead set is same as for <set exp>.

There is a wrinkle here in that if you count the number
of COMPLEMENT signs you see, and they are even, you don't need
to perform the operation at all.  If they are odd, you just need
to perform it once.  So the loop is something like

int compCount = 0;

while (lookahead is COMPLEMENT){
   consume;
   compCount++;
}
if (lookahead is one of ID, LEFTBRACE, CMP, LEFTPAREN){
   sAtomic();
   // do the rest of the processing for this method
   // like for the other levels;
   ...
}
else
   throw an exception with the message that those tokens
   were expected;

if the count is even, just make the level 0 result variable
the same as the level atomic result variable.  If it's odd,
then you will be able to reuse it if it is a temp, but if it's
a program set variable, then you will need to make the res
variable a temp variable, calculate it from the atomic variable,
like

res = atomicVariable.complement();

The pattern is pretty much the same except because you don't know if
you will actually perform the operation until you have read ALL
of the COMPLEMENT's, you have to delay testing whether you can
reuse the atomic level variable until below the loop.

***/

<set atomic> ::= ID | <set const> | LEFTPAREN <set exp> RIGHTPAREN;

/***

the lookahead sets for these three alternatives are, in order,

ID
LEFTBRACE, CMP
LEFTPAREN

so the code for setAtomic() is, assuming the lookahead is one of those,

if (lookahead == ID){
   let str be ID's string
   if (ID is declared to be of type set)
      setatomicResultVariable = str;
   else if (ID declared to be of type nat)
      throw an exception with message
      str is declared as nat, not set."
   else
      throw an exception with message
      str is not declared
}
else if (lookahead == LEFTPAREN){
   consume the LEFTPAREN;
   if (lookahead is not one of <set exp>'s lookaheads)
      throw the exception indicating what tokens are expected
   else{
      setExp();
      if (lookahead = RIGHTPAREN){
         consume the RIGHTPAREN;
         setAtomicResultVariable = setExpResultVariable;
      }   
      else
         throw an Exception with message
         rightparen expected.
   }
}
else{ // lookahead is either LEFTBRACE or CMP
   setConst();
   setAatomicResultVariable = setConstResultVariable;
}

***/


<set const> ::= <complemented> | <set literal>;

/***

the lookahead sets are

CMP
 
for the first alternative and for the second

LEFTBRACE

Recall, you have global String variables

private static String setConstResultVariable;
private static String setComplementedLiteralResultVariable;
private static String setLiteralResultVariable;

to hold the names of the temporary variables that 
house the values.  Assuming the lookaheads are right the
code is roughly

setConst(){

if (lookahead is CMP){
   complemented();
   setConstResultVariable = setComplementedLiteralResultVariable;
}
else{
   setLiteral();
   setConstResultVariable = setLiteralResultVariable;
}
}



***/

<complemented> ::= CMP <set literal>;

/***

assuming the lookahead is tested, the code goes

complemented(){

   consume CMP;
   setLiteral();

   emit code for
   setLiteralResultVariable = setLiteralResultVariable.complement();

   and finally 
   setComplementedLiteralResultVariable = setLiteralResultVariable;

   to leave the result where it can be found.
}
***/

<set literal> ::= LEFTBRACE <nat list> RIGHTBRACE;

<nat list> ::=  "" | <ne nat list>;

<ne nat list> ::=  NATCONST | NATCONST COMMA <ne nat list>;


/***

We can replace these three productions with a single EBNF
definition of <set literal> as


LEFTBRACE ("" | NATCONST (COMMA NATCONST)*) RIGHTBRACE


Suppose $ivN is the next temp variable for int arrays, and
$svM is the next free temp for sets(you can use the usedSetTemps == decSetTemps
test to see if you can reuse one that is already declared).
We want to emit two lines of code.

int[] $ivN = { contents of the nat list };
CofinFin $svM = new CofinFin(false, $ivN);

or

int[] $ivN = { contents nat list };
$svM = new CofinFin(false, $ivN);

if $svM has already been declared.  We then 
leave $svM in setLiteralResultVariable.

You need to write the code here to do that.  Assuming the lookahead
is indeed the LEFTBRACE, the code is roughly,

construct the first part of the string to emit for the first
line, the part up through and including LEFTBRACE, using a 
StringBuffer or StringBuilder object;

while (lookahead is not RIGHTBRACE){
   if (lookahead is NATCONST){
      append the nat to the string being created;
      consume the NATCONST;
      if (lookahead is COMMA){
         consume the COMMA;
         add ',' to the string to be emitted
         if (lookahead is not NATCONST)
            throw an exception with message
            "natconstant expected."
      }
      else
         if (lookahead is not RIGHTBRACE)
            throw an exception with message
            "comma or rightbrace expected."
   }
   else
      throw an exception with message
      "natconstant or rightbrace expected."
}
consume the RIGHTBRACE
add "};\n" to the string of code to be emitted.

Using the temp variables for the int array and the next
set variable, add the second line to the code to be emitted,
and emit it.

***/
      


<nat asgn> ::= ID ASSIGN <nat exp>;

/***

Before <nat asgn> is chosen, the ID should have been checked to
see that it is declared as a nat.

If you have

private String natExpResult;

you can set it to the appropriate variable or literal in
natExp() and here emit code for

id = natExpResult;

where id is the token string for the ID token.

***/


<nat exp> ::= ID | NATCONST;  // no arithmetic operators

/***

The lookaheads are just ID and NATCONST, so anything else
should lead to an exception.

We only have nat variables and nat literals so it should
be easy to adapt the plans for the the sets to this situation.

Roughly, just check that the variable has been declared to be
a nat(throwing an appropriate exception if not).  Set natExpResult to
either the ID value or the NATCONST value as a literal.

***/

/***

The grammar rules relevant to the if statement are

<test>  ::=   NOT <test> | <test atomic>;

lookahead for first rhs is NOT.  The lookahead for the
second is the union of the lookaheads for <set exp> and <nat exp>,
LEFTBRACE, CMP, COMPLEMENT, ID, NATCONST, LEFTPAREN


<test atomic> ::= <set exp> <set test suffix> | <nat exp> IS_IN <set exp> ;

the lookahead for the first is
LEFTBRACE, CMP, COMPLEMENT, ID, LEFTPAREN
and for the second
ID, NATCONST

so in the code, if the lookahead is ID, you will need to look it up in
the symbol table and handle as with the <asgn> productions above.


<set test suffix> ::= EQUALS <set exp> | SUBSET <set exp> 

You will need to emit the code to evaluate the expressions BEFORE emitting
the if, so you will need to save the string that tells what variable holds the
result of the first set expression of the set test in a distinct static string
variable to you can access it after you have generated the code for the second
set expression. The complete string for the test is described below.

<if> ::= <if prefix> <if suffix>;

lookahead is IF

<if prefix> ::= IF <test> THEN <st list>;

<if suffix> ::= ENDIF | ELSE <st list> ENDIF;


You can use

private static String testAtomicString;
private static String testString;

to calculate the appropriate code for the test of the Java if statement.

We won't go into great detail on how to handle these, but here are some
things to bear in mind.  You should be able to adapt the approaches used
for the other parts of the language to dealing with the if statements.

1. the code to evaluate any set expressions that occur in the test should
   be emitted prior to the actual if (test) of the emitted Java statement,
   during the processing of the <set exp> grammar variable.

2. the testAtomicString code string for the if test in the Java code should be one of

   setVar.equals(setVar)

   setVar.isSubsetOf(setVar)

   setVar.isIn(natExp)

   where the variables (or nat expression) depends on the actual tests and the
   code to evaluate the expressions, and may be temp variables for the sets.

   if {} <= {1,2,3} then

   is legal source code syntax

   and both sets would be represented by temp variables in the emitted code.

   if not 3 in s then

   can be translated as

   if (!s.contains(3)){
  

3. if the atomic test is negated an odd number of times, a '!' can be added 
   to the front of testAtomicCodeString to get testString, otherwise testString
   is just the same as testAtomicString.

4. the emitted code for <if prefix> will be structured

   code to evaluate the expressions occurring in the test
   if (testString){
      code for <st list>
   }

5. the emitted code for the ENDIF variation of <if suffix> is nothing, since the }
   has been emitted already in ifPrefix()

6. the emitted code for the other alternative of <if suffix> is
   
   else{
      code for <st list>
   }

7. because variables declared in nested scopes will not be visible outside
   of those scopes, you will need to maintain a stack to hold the decSetTemps
   and usedSetTemps values prior to entering the nested scope.  Suppose that we
   have a stack for pairs of ints, Stck.  Before entering the <st list>, the
   translator should push the current values of usedSetTemps and decSetTemps,
   and upon leaving the nested scope, it should pop the top value pair off 
   Stck back into usedSetTemps and decSetTemps.  You need to push both of them,
   because usedSetTemps <= decSetTemps should always be true.
   
***/


// the last rule is



<out> ::= <set exp> PERIOD;

/***

The lookahead is

CMP, LEFTBRACE, LEFTPAREN, COMPLEMENT, ID

Anything else is an error with those tokens expected.

An ID that is undeclared is an error with "id not declared."
An ID that is declared as a nat, is "id not declared as a set"
In both cases, id should be the actual token string for the identifier.

Otherwise, call setExp(), and emit the code

System.out.println( setExpResultVar.toString());

and finish with 

if (lookahead is not PERIOD)
   throw an exception "period expected.")
else{
   consume the '.'
   emit the code to finish off the Java program(should
   be "\n}\n}\n"
}

We will ignore any text after the period, so you do not need
to see if the period is the last token before EOF.

***/