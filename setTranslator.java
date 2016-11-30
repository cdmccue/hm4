import java.util.*;
import java.io.*;

/********************
 * Georgi Steev Robert Mugabe Christian McCue
 * 
 * COS 360 Assignment 4 - Part 2
 ********************/
/*
 * 
 * A template for the translator part of the project.
 * 
 * You will need to add more static variables and also methods for each of the
 * grammar variables, and modify the program method below.
 * 
 * Modified 11/20/16
 * 
 * 1. changed PrintStream to PrintWriter 2. changed to have the main method only
 * print the exception's error message to standard error when an exception is
 * thrown.
 */
public class setTranslator {

    private static setScanner sc;
    private static PrintWriter dest;
    private static PrintWriter err;
    private static FileWriter errorWrite;
    private static File sourceFile;

    private static String setExpResultVariable, setLevel2ResultVariable,
            setLevel1ResultVariable, setLevel0ResultVariable,
            setAtomicResultVariable, setConstResultVariable,
            setComplementedLiteralResultVariable, setLiteralResultVariable,
            natExpResultVariable;

    private static String setTempPrefix = "$sv";
    private static String natArrTempPrefix = "$iv";
    private static int natArrTempSuffix, 
                       nextNatArrTempSuffix, 
                       usedSetTemps,    // tracks the set temp variables that are currently 
                                        // in use
                       decSetTemps;     // tracks the set temp variables that have been 
                                        // declared in the current scope;
                                        // discussed below; generally, usedSetTemps <=
                                        // decSetTemps

    private static String testAtomicString;
    private static String testString;
    
    // to track if a variable should be installed as NAT or SET 
    private static boolean inNatDecs = true;
    
    // to track if a set should be complement
    private static boolean complement = false;
    
    // the symbol table for installing NAT and SET variables
    private static HashMap symbolTable;

    private static void program() throws IOException {
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
        // attempts the translation of the source file
        // into a Java source file.
        sc.consume();
        Token tk = sc.lookahead();
        int tkType = tk.getTokenType();
        
        if (tkType != Token.ID) {
            try {
                throw new Exception("[line " + tk.getLineNum() + "]: identifier expected.");
            } catch (Exception e) {
                err = new PrintWriter(errorWrite);
                err.println(e.getMessage()+"\n");
                err.close();
            }
        } else {
            sc.consume();
            sourceFile = new File(tk.getTokenString() + ".java");
            dest = new PrintWriter(sourceFile);
            dest.println("public class " + tk.getTokenString() + "{\n");
            
            // get the next token
            tk = sc.lookahead();
            tkType = tk.getTokenType();
        }
        // skip over the VAR token
        if (tkType == Token.VAR) {
            while (tkType == Token.VAR) {
                sc.consume();
                tk = sc.lookahead();
                tkType = tk.getTokenType();
            }
        } else {
            try {
                throw new Exception("[line " + tk.getLineNum() + "]: \"var\" expected.");
            } catch (Exception e) {
                err = new PrintWriter(errorWrite);
                err.println(e.getMessage()+"\n");
                err.close();
                dest.close();
                sourceFile.delete();
                System.exit(1);
            }
        }
        // check for NAT and SET variables that need to be declared and the BEGIN 
        if (tkType == Token.NAT || tkType == Token.SET || tkType == Token.BEGIN) {
            dec();
            tk = sc.lookahead();
            tkType = tk.getTokenType();
        } else {
            try {
                throw new Exception("[line " + tk.getLineNum() + "]: \"nat\", \"set\", or \"begin\" expected.");
            } catch (Exception e) {
                err = new PrintWriter(errorWrite);
                err.println(e.getMessage()+"\n");
                err.close();
                dest.close();
                sourceFile.delete();
                System.exit(1);
            }
        }
        if (tkType == Token.ID || tkType == Token.END) {
            if(tkType == Token.ID)
                stList();
            out();
        } else {
            try {
                throw new Exception("[line " + tk.getLineNum() + "]: identifier, or \"end\" expected.");
            } catch (Exception e) {
                err = new PrintWriter(errorWrite);
                err.println(e.getMessage()+"\n");
                err.close();
                dest.close();
                sourceFile.delete();
                System.exit(1);
            }
        }

    }
    
    // method for Declaration
    public static void dec() {
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
        // DONE
        symbolTable = new HashMap();
        
        natDec();
        setDec();
        Token tk = sc.lookahead();
        int tkN = tk.getTokenType();
        if (tkN != Token.BEGIN) {
            try {
                throw new Exception("[line " + tk.getLineNum() + "]: \"begin\" expected.");
            } catch (Exception e) {
                err = new PrintWriter(errorWrite);
                err.println(e.getMessage()+"\n");
                err.close();
                dest.close();
                sourceFile.delete();
                System.exit(1);
            }
        } else {
            sc.consume();
            dest.println("\tpublic static void main(String[] args){\n");
        }
    }
    // method for natDec
    public static void natDec() {
        /***
        if (lookahead = NAT){
         consume();
         if (lookahead is not ID)
             throw exception with message "identifier expected.";
         else
             neVarList();
         if (lookahead = SEMICOLON)
             consume();
         else
             throw an exception with message
             "semicolon expected."
          }
          else // no nat declarations
           return;
          }***/
        // DONE
        Token tk = sc.lookahead();
        int tkN = tk.getTokenType();
       
        if (tkN == Token.NAT){
            sc.consume();
            tk = sc.lookahead();
            tkN = tk.getTokenType();
            if (tkN != Token.ID)
                try {
                    throw new Exception("[line " + tk.getLineNum() + "]: identifier expected.");
                } catch (Exception e) {
                    err = new PrintWriter(errorWrite);
                    err.println(e.getMessage()+"\n");
                    err.close();
                    dest.close();
                    sourceFile.delete();
                    System.exit(1);
                }
            else
                inNatDecs = true;
                neVarList();
                tk = sc.lookahead();
                tkN = tk.getTokenType();
            if (tkN == Token.SEMICOLON)
                sc.consume();
            else
                try {
                    throw new Exception("[line " + tk.getLineNum() + "]: semicolon expected.");
                } catch (Exception e) {
                    err = new PrintWriter(errorWrite);
                    err.println(e.getMessage()+"\n");
                    err.close();
                    dest.close();
                    sourceFile.delete();
                    System.exit(1);
                }
            }
        else // no nat declarations
            return;       
    }
    
    // method for setDec
    public static void setDec() {
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
        // DONE
        Token tk = sc.lookahead();
        int tkN = tk.getTokenType();
       
        if (tkN == Token.SET){
            sc.consume();
            tk = sc.lookahead();
            tkN = tk.getTokenType();
            if (tkN != Token.ID)
                try {
                    throw new Exception("[line " + tk.getLineNum() + "]: identifier expected.");
                } catch (Exception e) {
                    err = new PrintWriter(errorWrite);
                    err.println(e.getMessage()+"\n");
                    err.close();
                    dest.close();
                    sourceFile.delete();
                    System.exit(1);
                }
            else
                inNatDecs = false;
                neVarList();
                tk = sc.lookahead();
                tkN = tk.getTokenType();
            if (tkN == Token.SEMICOLON)
                sc.consume();
            else
                try {
                    throw new Exception("[line " + tk.getLineNum() + "]: semicolon expected.");
                } catch (Exception e) {
                    err = new PrintWriter(errorWrite);
                    err.println(e.getMessage()+"\n");
                    err.close();
                    dest.close();
                    sourceFile.delete();
                    System.exit(1);
                }
            }
        else // no nat declarations
            return;       
    }

    // method for neVarList
    public static void neVarList() {
        /*** 
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
         ***/
        // DONE
        // THIS METHOD GENERATES EVERYTHING FROM PROGRAM ID, UP TO THE CALL FOR BEGIN
        // That is, it is used by setDec() and natDec() and dec() to generate the 
        // symbol table and to declare the variables 
        
        Token tk = sc.lookahead();
        int tkN = tk.getTokenType();
        while(tkN != Token.SEMICOLON && tkN != Token.BEGIN) {
            if (tkN == Token.ID) {
                // DONE
                // install the ID in the symbol table
                if(inNatDecs) {
                    if (!symbolTable.containsKey(tk.getTokenString())) {
                        symbolTable.put(tk.getTokenString(),"int");
                        dest.println("\tprivate static int " + tk.getTokenString() + ";\n");
                    } else {
                        try {
                            String value = (String) symbolTable.get(tk.getTokenString());
                            String decl = "";
                            if (value.equals("int"))
                                decl = "nat";
                            else
                                decl = "set";
                            throw new Exception("[line " + tk.getLineNum() + "]: identifier "
                                + tk.getTokenString() + " is declared as "+ decl +".");
                        } catch (Exception e) {
                            err = new PrintWriter(errorWrite);
                            err.println(e.getMessage()+"\n");
                            err.close();
                            dest.close();
                            sourceFile.delete();
                            System.exit(1);
                        }
                    }
                }
                else {
                    if (!symbolTable.containsKey(tk.getTokenString())) {
                        symbolTable.put(tk.getTokenString(),"CofinFin");
                        dest.println("\tprivate static CofinFin " + tk.getTokenString() + " = new CofinFin();\n");
                    } else {
                        try {
                            String value = (String) symbolTable.get(tk.getTokenString());
                            String decl = "";
                            if (value.equals("int"))
                                decl = "nat";
                            else
                                decl = "set";
                            throw new Exception("[line " + tk.getLineNum() + "]: identifier "
                                + tk.getTokenString() + " is declared as " + decl +".");
                        } catch (Exception e) {
                            err = new PrintWriter(errorWrite);
                            err.println(e.getMessage()+"\n");
                            err.close();
                            dest.close();
                            sourceFile.delete();
                            System.exit(1);
                        }
                    }
                }
                sc.consume();
                tk = sc.lookahead();
                tkN = tk.getTokenType();
            } else {
                try {
                    throw new Exception("[line " + tk.getLineNum() + "]: identifier expected.");
                } catch (Exception e) {
                    err = new PrintWriter(errorWrite);
                    err.println(e.getMessage()+"\n");
                    err.close();
                    dest.close();
                    sourceFile.delete();
                    System.exit(1);
                }
            }
            
            while (tkN == Token.COMMA) {
                sc.consume();
                tk = sc.lookahead();
                tkN = tk.getTokenType();
                if (tkN == Token.ID) {
                    // DONE
                    // install the ID in the symbol table
                    if(inNatDecs) {
                        if (!symbolTable.containsKey(tk.getTokenString())) {
                            symbolTable.put(tk.getTokenString(),"int");
                            dest.println("private static int " + tk.getTokenString() + ";\n");
                        } else {
                            try {
                                String value = (String) symbolTable.get(tk.getTokenString());
                                String decl = "";
                                if (value.equals("int"))
                                    decl = "nat";
                                else
                                    decl = "set";
                                throw new Exception("[line " + tk.getLineNum() + "]: identifier "
                                    + tk.getTokenString() + " is declared as "
                                        + decl +".");
                            } catch (Exception e) {
                                err = new PrintWriter(errorWrite);
                                err.println(e.getMessage()+"\n");
                                err.close();
                                dest.close();
                                sourceFile.delete();
                                System.exit(1);
                            }
                        }
                    }
                    else {
                        if (!symbolTable.containsKey(tk.getTokenString())) {
                            symbolTable.put(tk.getTokenString(),"CofinFin");
                            dest.println("\tprivate static CofinFin " + tk.getTokenString() + " = new CofinFin();\n");
                        } else {
                            try {
                                String value = (String) symbolTable.get(tk.getTokenString());
                                String decl = "";
                                if (value.equals("int"))
                                    decl = "nat";
                                else
                                    decl = "set";
                                throw new Exception("[line " + tk.getLineNum() + "]: identifier "
                                    + tk.getTokenString() + " is declared as "
                                        + decl +".");
                            } catch (Exception e) {
                                err = new PrintWriter(errorWrite);
                                err.println(e.getMessage()+"\n");
                                err.close();
                                dest.close();
                                sourceFile.delete();
                                System.exit(1);
                            }
                        }
                    }
                    sc.consume();
                    tk = sc.lookahead();
                    tkN = tk.getTokenType();
                } else {
                    try {
                        throw new Exception("[line " + tk.getLineNum() + "]: identifier expected.");
                    } catch (Exception e) {
                        err = new PrintWriter(errorWrite);
                        err.println(e.getMessage()+"\n");
                        err.close();
                        dest.close();
                        sourceFile.delete();
                        System.exit(1);
                    }
                }
            }
        }        
    }

    // method for statement list 
    public static void stList() {
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
        // DONE
        Token tk = sc.lookahead();
        int tkN = tk.getTokenType();
        if (tkN == Token.ID || tkN == Token.IF)
            neStList();
        
    }
    // method for non empty statement list
    public static void neStList() {
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
        // DONE
        st();
        Token tk = sc.lookahead();
        int tkN = tk.getTokenType();
        while(tkN == Token.SEMICOLON) {
            sc.consume();
            tk = sc.lookahead();
            tkN = tk.getTokenType();
            if (tkN == Token.ID || tkN == Token.IF) {
                st();
                tk = sc.lookahead();
                tkN = tk.getTokenType();
            }
            else {
                try {
                    throw new Exception("[line " + tk.getLineNum() + "]: identifier or if expected.");
                } catch (Exception e) {
                    err = new PrintWriter(errorWrite);
                    err.println(e.getMessage()+"\n");
                    err.close();
                    dest.close();
                    sourceFile.delete();
                    System.exit(1);
                }
            }
        }   
    }
    
    // method for statements
    public static void st() {
        /***

        an assignment always begins with a declared variable and an
        if  with the if reserved word, so there is no problem determining
        which alternative to call.  The code is roughly

        if (lookahead = ID)
           asgn();
        else if (lookahead = IF)
           if();

        ***/
        
        // DONE
        Token tk = sc.lookahead();
        int tkN = tk.getTokenType();
        if (tkN == Token.ID)
            asgn();
        else if (tkN == Token.IF)
            iF();
        
    }
    // method for ASSIGN
    public static void asgn() {
        /***
         asgn(){ // assumes lookahead is ID

              Token id = lookahead; // DO NOT CONSUME

               if (id has not been declared)
                  throw an exception with message
                  "Identifier not declared."
               else  if (ID has been declared a nat)
                  natAsgn();
               else  // the only other alternative is declared as a set
                  setAsgn();
            }***/
        // DONE
        
        Token id = sc.lookahead();
        if (!symbolTable.containsKey(id.getTokenString())) {
            try {
                throw new Exception("[line " + id.getLineNum() + "]: Identifier not declared.");
            } catch (Exception e) {
                err = new PrintWriter(errorWrite);
                err.println(e.getMessage()+"\n");
                err.close();
                dest.close();
                sourceFile.delete();
                System.exit(1);
            }
            //DONE
        } else {
            String value = (String) symbolTable.get(id.getTokenString());
            if (value.equals("int"))
                natAsgn();
            else
                setAsgn();
        }
        
    }
    
    // method for setAsgn
    public static void setAsgn() {
        /***
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
        }***/
        // TODO
        Token id = sc.lookahead();
        sc.consume();
        Token tk = sc.lookahead();
        int tkN = tk.getTokenType();
        
        // check if  the lookahead is an assigment operator
        if(tkN != Token.ASSIGN) {
            try {
                throw new Exception("[line " + tk.getLineNum() + "]: assignment operator(:=) expected.");
            } catch (Exception e) {
                err = new PrintWriter(errorWrite);
                err.println(e.getMessage()+"\n");
                err.close();
                dest.close();
                sourceFile.delete();
                System.exit(1);
            }
        } else {
            sc.consume();
            tk = sc.lookahead();
            tkN = tk.getTokenType();
            if (tkN != Token.CMP && tkN != Token.LEFTBRACE && tkN != Token.ID
                    && tkN != Token.LEFTPAREN && tkN != Token.COMPLEMENT) {
                try {
                    throw new Exception("[line " + tk.getLineNum() + "]: \"CMP\", leftbrace, identifier, leftparen, or complement(-) expected.");
                } catch (Exception e) {
                    err = new PrintWriter(errorWrite);
                    err.println(e.getMessage()+"\n");
                    err.close();
                    dest.close();
                    sourceFile.delete();
                    System.exit(1);
                }
            } else {
                setExp();
                dest.println("\t\t"+id.getTokenString() + " = " +setExpResultVariable + ";\n");
                usedSetTemps--;
            }
        }
    }
    
    // method to generate set expressions
    public static void setExp() {
        boolean needANewTemp;
        String res; // string identifying the variable holding the result
        setLevel2();
        Token tk = sc.lookahead();
        int tkN = tk.getTokenType();
        if (setLevel2ResultVariable.charAt(0) == '$'
                || tkN != Token.SETDIFFERENCE)
            res = setLevel2ResultVariable;
        else {
            // the result is a program variable and we will need to perform an
            // op
            needANewTemp = usedSetTemps == decSetTemps;
    
            if (needANewTemp) {
                res = nextTemp(true);
                usedSetTemps++;
            } else
                res = setTempPrefix + (++usedSetTemps);
    
            // if it's new, we have to declare it;
            dest.println((needANewTemp ? "\t\tCofinFin " : "\t\t") + res + " = "
                    + setLevel2ResultVariable + ';');
        }
        while (tkN == Token.SETDIFFERENCE) {
            sc.consume();
            tk = sc.lookahead();
            tkN = tk.getTokenType();
            if (tkN != Token.CMP && tkN != Token.LEFTBRACE && tkN != Token.ID
                    && tkN != Token.LEFTPAREN && tkN != Token.COMPLEMENT)
                try {
                    throw new Exception("[line " +tk.getLineNum() +"]: \"CMP\", leftbrace, identifier, leftparen, or complement(-) expected.");
                } catch (Exception e) {
                    err = new PrintWriter(errorWrite);
                    err.println(e.getMessage()+"\n");
                    err.close();
                    dest.close();
                    sourceFile.delete();
                    System.exit(1);
                }
            else {
                setLevel2();
                dest.println("\t\t"+res + " = " + res + ".intersect("
                        + setLevel2ResultVariable + ".complement());");
                tk = sc.lookahead();
                tkN = tk.getTokenType();
            }
    
        }
        setExpResultVariable = res;
    }

    // method for setLevel2
    public static void setLevel2() {
        /*** 
    
        Again, this becomes
    
        <set level 2> =  <set level 1> (UNION <set level 1>)*
    
        The lookahead for <set level 1>  is same as for <set exp>.
    
        You should be able to adapt the approach given for setExp() to
        this situation.
    
    
        ***/
        // DONE
        boolean needANewTemp;
        String res; // string identifying the variable holding the result
        setLevel1();
        Token tk = sc.lookahead();
        int tkN = tk.getTokenType();
        if (setLevel1ResultVariable.charAt(0) == '$'
                || tkN != Token.UNION)
            res = setLevel1ResultVariable;
        else {
            // the result is a program variable and we will need to perform an
            // op
            needANewTemp = usedSetTemps == decSetTemps;
    
            if (needANewTemp) {
                res = nextTemp(true);
                usedSetTemps++;
            } else
                res = setTempPrefix + (++usedSetTemps);
    
            // if it's new, we have to declare it;
            dest.println((needANewTemp ? "\t\tCofinFin " : "\t\t") + res + " = "
                    + setLevel1ResultVariable + ';');
        }
        while (tkN == Token.UNION) {
            sc.consume();
            tk = sc.lookahead();
            tkN = tk.getTokenType();
            if (tkN != Token.CMP && tkN != Token.LEFTBRACE && tkN != Token.ID
                    && tkN != Token.LEFTPAREN && tkN != Token.COMPLEMENT)
                try {
                    throw new Exception("[line " +tk.getLineNum() +"]: \"CMP\", leftbrace, identifier, leftparen, or complement(-) expected.");
                } catch (Exception e) {
                    err = new PrintWriter(errorWrite);
                    err.println(e.getMessage()+"\n");
                    err.close();
                    dest.close();
                    sourceFile.delete();
                    System.exit(1);
                }
            else {
                setLevel1();
                dest.println("\t\t"+res + " = " + res + ".union("
                        + setLevel1ResultVariable+");");
                tk = sc.lookahead();
                tkN = tk.getTokenType();
            }
    
        }
        setLevel2ResultVariable = res;
    }

    // method for setLevel1
    public static void setLevel1() {
        /***
    
        Again, this yields
    
        <set level 1> = <set level 0> (INTERSECTION <set level 0>)*
    
        The lookahead set is same as for <set exp>.
    
    
        You can adapt the code for setExp().
    
        ***/
        //DONE
        boolean needANewTemp;
        String res; // string identifying the variable holding the result
        setLevel0();
        Token tk = sc.lookahead();
        int tkN = tk.getTokenType();
        if (setLevel0ResultVariable.charAt(0) == '$'
                || tkN != Token.INTERSECTION)
            res = setLevel0ResultVariable;
        else {
            // the result is a program variable and we will need to perform an
            // op
            needANewTemp = usedSetTemps == decSetTemps;
    
            if (needANewTemp) {
                res = nextTemp(true);
                usedSetTemps++;
            } else
                res = setTempPrefix + (++usedSetTemps);
    
            // if it's new, we have to declare it;
            dest.println((needANewTemp ? "\t\tCofinFin " : "\t\t") + res + " = "
                    + setLevel0ResultVariable + ';');
        }
        while (tkN == Token.INTERSECTION) {
            sc.consume();
            tk = sc.lookahead();
            tkN = tk.getTokenType();
            if (tkN != Token.CMP && tkN != Token.LEFTBRACE && tkN != Token.ID
                    && tkN != Token.LEFTPAREN && tkN != Token.COMPLEMENT)
                try {
                    throw new Exception("[line " +tk.getLineNum() +"]: \"CMP\", leftbrace, identifier, leftparen, or complement(-) expected.");
                } catch (Exception e) {
                    err = new PrintWriter(errorWrite);
                    err.println(e.getMessage()+"\n");
                    err.close();
                    dest.close();
                    sourceFile.delete();
                    System.exit(1);
                }
            else {
                setLevel0();
                dest.println("\t\t"+res + " = " + res + ".intersect("
                        + setLevel0ResultVariable+");");
                tk = sc.lookahead();
                tkN = tk.getTokenType();
            }
    
        }
        setLevel1ResultVariable = res;
        
    }

    // method for set level 0
    public static void setLevel0() {
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
        // DONE
        boolean needANewTemp;
        String res; // string identifying the variable holding the result
        Token tk = sc.lookahead();
        int tkN = tk.getTokenType();

        // we need to keep track whether or not comp is odd or even
        // and read through all of the comps before we can proceed 
        int compCount = 0;

        while (tkN == Token.COMPLEMENT) {
            sc.consume();
            tk = sc.lookahead();
            tkN = tk.getTokenType();
            compCount++;
        }

        //if compCount is even level0 result variable is set atomic result variable
        //else we continue the same pattern now that we have counted the comps
        if ((compCount % 2) == 0 && tkN != Token.COMPLEMENT){
            setAtomic();
            res = setAtomicResultVariable;
        }
        else {
            // the result is a program variable and we will need to perform an
            // op
            needANewTemp = usedSetTemps == decSetTemps;
    
            if (needANewTemp) {
                res = nextTemp(true);
                usedSetTemps++;
            } else
                res = setTempPrefix + (++usedSetTemps);
    
            // if it's new, we have to declare it;
            dest.println((needANewTemp ? "\t\tCofinFin " : "\t\t") + res + " = "
                    + setAtomicResultVariable + ';');

            //now we can perform the operation
            if (tkN == Token.CMP || tkN == Token.LEFTBRACE || tkN == Token.ID
                    || tkN == Token.LEFTPAREN || tkN == Token.COMPLEMENT) {
                setAtomic();
                dest.println("\t\t"+res + " = " + setAtomicResultVariable + ".complement();");
            }
            else {
                try {
                    throw new Exception("[line " +tk.getLineNum() +"]: \"CMP\", leftbrace, identifier, leftparen, or complement(-) expected.");
                } catch (Exception e) {
                    err = new PrintWriter(errorWrite);
                    err.println(e.getMessage()+"\n");
                    err.close();
                    dest.close();
                    sourceFile.delete();
                    System.exit(1);
                }
            }
        }
        setLevel0ResultVariable = res;                        
    }
    // method for setAtomic
    public static void setAtomic() {
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
        // TODO
        Token tk = sc.lookahead();
        int tkN = tk.getTokenType();
        String str = tk.getTokenString();

        if (tkN == Token.ID){
           if (symbolTable.containsKey(str))
           {
               String value = (String) symbolTable.get(str);
               if (value.equals("CofinFin")){//ID is declared to be of type set 
                  setAtomicResultVariable = str;
                  sc.consume();
               }
               else //ID declared to be of type nat
                  try {
                        throw new Exception("[line " +tk.getLineNum() +"]:" + str + "is declared as nat, not set");
                    } catch (Exception e) {
                        err = new PrintWriter(errorWrite);
                        err.println(e.getMessage()+"\n");
                        err.close();
                        dest.close();
                        sourceFile.delete();
                        System.exit(1);
                    }
                }
           else
              try {
                    throw new Exception("[line " +tk.getLineNum() +"]:" + str + "is not declared.");
                } catch (Exception e) {
                    err = new PrintWriter(errorWrite);
                    err.println(e.getMessage()+"\n");
                    err.close();
                    dest.close();
                    sourceFile.delete();
                    System.exit(1);
                }
        }
        else if (tkN == Token.LEFTPAREN)
        {
            sc.consume();
            if (tkN != Token.CMP && tkN != Token.LEFTBRACE && tkN != Token.ID
                    && tkN != Token.LEFTPAREN && tkN != Token.COMPLEMENT)
                try {
                    throw new Exception("[line " +tk.getLineNum() +"]: \"CMP\", leftbrace, identifier, leftparen, or complement(-) expected.");
                } catch (Exception e) {
                    err = new PrintWriter(errorWrite);
                    err.println(e.getMessage()+"\n");
                    err.close();
                    dest.close();
                    sourceFile.delete();
                    System.exit(1);
                }
            else{
                setExp();
                tk = sc.lookahead();
                tkN = tk.getTokenType();
                if (tkN == Token.RIGHTPAREN){
                    sc.consume();
                    setAtomicResultVariable = setExpResultVariable;
                }   
                else
                    try {
                        throw new Exception("[line " +tk.getLineNum() +"]: rightparen expected");
                    } catch (Exception e) {
                        err = new PrintWriter(errorWrite);
                        err.println(e.getMessage()+"\n");
                        err.close();
                        dest.close();
                        sourceFile.delete();
                        System.exit(1);
                    }
            }
        }
        else{ // lookahead is either LEFTBRACE or CMP
           setConst();
           setAtomicResultVariable = setConstResultVariable;
        }
        
    }
    
    // method for setConst
    public static void setConst() {
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
        
        // DONE
        Token tk = sc.lookahead();
        int tkN = tk.getTokenType();
        if (tkN == Token.CMP){
            complemented();
            setConstResultVariable = setComplementedLiteralResultVariable;
         }
         else{
            setLiteral();
            setConstResultVariable = setLiteralResultVariable;
         }
    }
    
    // method for complemented
    public static void complemented() {
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
        
        // DONE
        complement = true;
        sc.consume();
        setLiteral();
        setComplementedLiteralResultVariable = setLiteralResultVariable;
        
    }

    // method for setLiteral
    public static void setLiteral() {
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
        // TODO
        String res = "";
        String tempNatVar = "";
        String tempSetVar = "";
        boolean needANewTemp = nextNatArrTempSuffix == natArrTempSuffix;
        
        // get new temp variable for the int arrays
        if (needANewTemp) {
            tempNatVar = nextTemp(false);
            nextNatArrTempSuffix++;
        } else
            tempNatVar = natArrTempPrefix + (++nextNatArrTempSuffix);
        
        dest.print((needANewTemp ? "\t\tint[] ": "\t\t") +tempNatVar + " = "+ (!needANewTemp ? "new int[]": "")+"{");
        
        sc.consume();
        Token tk = sc.lookahead();
        int tkN = tk.getTokenType();
        while (tkN != Token.RIGHTBRACE){
            if (tkN == Token.NATCONST){
                dest.print(tk.getTokenString());
                sc.consume();
                tk = sc.lookahead();
                tkN = tk.getTokenType();
                if (tkN == Token.COMMA){
                    sc.consume();
                    tk = sc.lookahead();
                    tkN = tk.getTokenType();
                    dest.print(", ");
                    if (tkN != Token.NATCONST)
                     //throw an exception with message
                     //"natconstant expected."
                        try {
                            throw new Exception("[line " +tk.getLineNum() +"]: natconstant expected");
                        } catch (Exception e) {
                            err = new PrintWriter(errorWrite);
                            err.println(e.getMessage()+"\n");
                            err.close();
                            dest.close();
                            sourceFile.delete();
                            System.exit(1);
                        }
                }
                else if (tkN != Token.RIGHTBRACE)
                    // throw an exception with message
                    //"comma or rightbrace expected."
                    try {
                        throw new Exception("[line " +tk.getLineNum() +"]: rightparen expected");
                    } catch (Exception e) {
                        err = new PrintWriter(errorWrite);
                        err.println(e.getMessage()+"\n");
                        err.close();
                        dest.close();
                        sourceFile.delete();
                        System.exit(1);
                    }
            }
            else
                // throw an exception with message
                //"natconstant or rightbrace expected."
                try {
                    throw new Exception("[line " +tk.getLineNum() +"]: natconstant or rightbrace expected");
                } catch (Exception e) {
                    err = new PrintWriter(errorWrite);
                    err.println(e.getMessage()+"\n");
                    err.close();
                    dest.close();
                    sourceFile.delete();
                    System.exit(1);
                }
        }
        sc.consume();
        dest.println("};\n");
        
        // add the CofinFin line of code to the res string
        needANewTemp = usedSetTemps == decSetTemps;
        
        if (needANewTemp) {
            tempSetVar = nextTemp(true);
            usedSetTemps++;
        } else {
            tempSetVar = setTempPrefix + (++usedSetTemps);
        }
        
        // check if the result should be a complement
        if (!complement)
            dest.println((needANewTemp ? "\t\tCofinFin " : "\t\t") + tempSetVar + " = new CofinFin(false, " + tempNatVar +");\n");
        else {
            complement = false;
            dest.println((needANewTemp ? "\t\tCofinFin " : "\t\t") + tempSetVar + " = new CofinFin(true, " + tempNatVar +");\n");
        }
        nextNatArrTempSuffix--;
        setLiteralResultVariable = tempSetVar;
        
        
    }
    // method for nat asgn
    public static void natAsgn() {
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
        // TODO
        Token id = sc.lookahead();
        sc.consume();
        Token tk = sc.lookahead();
        int tkN = tk.getTokenType();
        
        // check if  the lookahead is an assigment operator
        if(tkN != Token.ASSIGN) {
            try {
                throw new Exception("[line " + tk.getLineNum() + "]: assignment operator(:=) expected.");
            } catch (Exception e) {
                err = new PrintWriter(errorWrite);
                err.println(e.getMessage()+"\n");
                err.close();
                dest.close();
                sourceFile.delete();
                System.exit(1);
            }
        } else {
            // TODO
            sc.consume();
            tk = sc.lookahead();
            tkN = tk.getTokenType();
            if (tkN != Token.ID && tkN != Token.NATCONST) {
                try {
                    throw new Exception("[line " + tk.getLineNum() + "]: identifier or natconst expected.");
                } catch (Exception e) {
                    err = new PrintWriter(errorWrite);
                    err.println(e.getMessage()+"\n");
                    err.close();
                    dest.close();
                    sourceFile.delete();
                    System.exit(1);
                }
            } else {
                natExp();
                dest.println(id.getTokenString() + " = " +natExpResultVariable + ";\n");
            }
        }
    }
    
    // method for nat exp
    public static void natExp() {
        /***

        The lookaheads are just ID and NATCONST, so anything else
        should lead to an exception.

        We only have nat variables and nat literals so it should
        be easy to adapt the plans for the the sets to this situation.

        Roughly, just check that the variable has been declared to be
        a nat(throwing an appropriate exception if not).  Set natExpResult to
        either the ID value or the NATCONST value as a literal.

        ***/
        // TODO 
        String res = "";
        Token tk = sc.lookahead();
        int tkN = tk.getTokenType();
        
        if (tkN == Token.ID && symbolTable.containsKey(tk.getTokenString())) {
            res = (String) symbolTable.get(tk.getTokenString());
            if (res.equals("int"))
                natExpResultVariable = res;
            else {
                try {
                    throw new Exception("[line " + tk.getLineNum() + "]: identifier declared as set.");
                } catch (Exception e) {
                    err = new PrintWriter(errorWrite);
                    err.println(e.getMessage()+"\n");
                    err.close();
                    dest.close();
                    sourceFile.delete();
                    System.exit(1);
                }
            }
        } else {
            res = tk.getTokenString();
            natExpResultVariable = res;
        }
            
    }
    /*** METHODS FOR IF GRAMMAR RULES ***/
    // method for test
    public static void test() {
        // TO DO
        
    }
    // method for testAtomic
    public static void testAtomic() {
        // TO DO
        
    }
    // method for setTestSuffix
    public static void setTestSuffix() {
        // TO DO
        
    }
    // method for IF
    public static void iF() {
        // TO DO
        
    }
    // method for if prefix
    public static void ifPrefix() {
        // TO DO
        
    }
    // method for if suffix
    public static void ifSuffix() {
        // TO DO
        
    }
    /*** END OF METHODS FOR IF GRAMMAR RULES 
     * @throws Exception ***/
    
    // method for out
    public static void out() {
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
        sc.consume();
        Token tk = sc.lookahead();
        int tkN = tk.getTokenType();
        // check that the lookahead is correct
        if (tkN != Token.CMP && tkN != Token.LEFTBRACE
                && tkN != Token.LEFTPAREN 
                && tkN != Token.COMPLEMENT
                && tkN != Token.ID) {
            // throw the exception
            try {
                throw new Exception("[line " + tk.getLineNum() + "]: \"CMP\", leftbrace, "
                        + "identifier, leftparen, or complement(-) expected");
            } catch (Exception e) {
                err = new PrintWriter(errorWrite);
                err.println(e.getMessage()+"\n");
                err.close();
                dest.close();
                sourceFile.delete();
                System.exit(1);
            } 
        } else {
            String res = "";
            if (tkN == Token.ID) {
                if (symbolTable.containsKey(tk.getTokenString())) {
                    String check = (String) symbolTable.get(tk.getTokenString());
                    if (check.equals("int")) {
                        try {
                            throw new Exception("[line " + tk.getLineNum() + "]: " 
                                    + tk.getTokenString() +" not declared as set");
                        } catch (Exception e) {
                            err = new PrintWriter(errorWrite);
                            err.println(e.getMessage()+"\n");
                            err.close();
                            dest.close();
                            sourceFile.delete();
                            System.exit(1);
                        }
                    }
                } else {
                    try {
                        throw new Exception("[line " + tk.getLineNum() + "]: "
                                + tk.getTokenString() + " not declared");
                    } catch (Exception e) {
                        err = new PrintWriter(errorWrite);
                        err.println(e.getMessage()+"\n");
                        err.close();
                        dest.close();
                        sourceFile.delete();
                        System.exit(1);
                    }
                }
            }
            
            setExp();
            dest.println("\t\tSystem.out.println("+setExpResultVariable+".toString());");
        }
        tk = sc.lookahead();
        tkN = tk.getTokenType();
        if (tkN != Token.PERIOD)
            try {
                throw new Exception("[line " + tk.getLineNum() + "]: period expected.");
            } catch (Exception e) {
                err = new PrintWriter(errorWrite);
                err.println(e.getMessage()+"\n");
                err.close();
                dest.close();
                sourceFile.delete();
                System.exit(1);
            }
        else {
            sc.consume();
            dest.print("\n\t}\n}\n");
        }
        
    }

    // helper method to create a Temp variable
    static String nextTemp(boolean isSet) {

        if (isSet) {
            return setTempPrefix + (++decSetTemps);
        } else {
            return natArrTempPrefix + (++natArrTempSuffix);
        }
    }

    // you should not need to modify the main method
    public static void main(String[] args) throws Exception {

        if (args.length == 0)
            sc = new setScanner(new Scanner(System.in));
        else
            sc = new setScanner(new Scanner(new File(args[0])));

        Token currTok = sc.lookahead();

        // file writer for the error file
        File errorFile = new File("myErrorTestResults.txt");
        errorWrite = new FileWriter(errorFile, true);
        
        // adding a test for null so I can compile and run this
        try {
            if (currTok != null && currTok.getTokenType() == Token.PROGRAM)
                program();
            else {
                throw new Exception("[line " + currTok.getLineNum() + "]: \"program\" expected.");
            }
            // add a comment indicating a successful parse
            if (dest != null) {
                dest.println("\n// Parsing completed successfully.");
                dest.close();
                
                // no errors so close the writer and delete the error file
                errorWrite.close();
                errorFile.delete();
            }
        } catch (Exception e) {
            err = new PrintWriter(errorWrite);
            err.println(e.getMessage()+"\n");
            err.close();
            }
        }
    }
