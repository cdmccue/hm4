/*

A class to represent a token in the translation project.

It has data members

public static final

1. TOKEN_LABELS: an array of strings you are to use in issuing error messages
   when some particular set of tokens is expected and the next token is not
   in that set

2. PROGRAM, ..., a number of symbolic ints for naming the token types

instance specific private

1. tokenType: an int identifying the kind of token according to the
   scheme given below; note this is not the only scheme possible; we could
   have had BINARY_SET_OPERATOR as a type, for example, and grouped
   intersection, union, and set difference into one kind, using the string
   to distinguish them

2. lineNum: the int >= 1 identifying the line from which this token 
   was drawn; first line of the source is regarded as line 1; EOF should
   have a line number that is one greater than the line number of the
   last line of the file

3. tokenString: a String variable holding the actual token for the token
   types NATCONST, ID, and UNRECOGNIZED

Invariants

1. The following are each independently invalid calls of the constructor

   a. line number <= 0
   b. token type not in range 0 to TOKEN_LABELS.length - 1
   c. type is NATCONST, ID, or UNRECOGNIZED but no string is given or
      the given string is null or empty or begins with WS
   d. type is not NATCONST, ID, or UNRECOGNIZED and a string is given

   In any such case, lineNum will be set to -1 and tokenType will be
   set to UNRECOGNIZED and tokenString will be set to "invalidly constructed"

2. if lineNum >= 1 then the token constructed passed the tests above
   and if the token type is not NATCONST, ID, or UNRECOGNIZED, tokenString
   will be "".
   
NOTE ON EOF's LINE NUMBER

The EOF token instance that would be created if the consume method
reaches the end of the file w/o encountering any non-ws characters should
have the line number that is one greater than the last line of the file.
For example, the file with three lines

line1
line2
line3

would have three ID tokens, and the fourth would be EOF, with line number 4.
You can imagine that it sits on a line by itself after everything else in
the file.

********************************************************************************************/

public class Token implements Comparable<Token>{

   public static final String[] TOKEN_LABELS = { 

   "\"program\"",   "identifier", "\"var\"",         "\"begin\"",               "\"end\"",
   "\"if\"",        "\"else\"",   "\"endif\"",       "\"nat\"",                 "\"set\"",
   "natconstant",   "leftbrace",  "rightbrace",      "leftparen",               "rightparen", 
   "semicolon",     "period",     "comma",           "assignment operator(:=)", "subset(<=)",             
   "equals",        "\"not\"",    "intersection(*)", "union(+)",                "setdifference(\\)", 
   "complement(-)", "\"in\"",     "\"then\"",        "\"CMP\"",                 "eof",
   "unrecognized"};

   public static final int // for the token types
      PROGRAM = 0, //  "program"  a reserved word
      ID = 1, // [a-zA-Z]+[a-zA-Z0-9]* 
      VAR = 2, // "var"      a reserved word
      BEGIN = 3, // "begin"    a reserved word
      END = 4, // "end"      a reserved word
      IF = 5, //  "if"       a reserved word
      ELSE = 6, // "else"     a reserved word
      ENDIF = 7, // "endif"    a reserved word
      NAT = 8, //  "nat"      a reserved word
      SET = 9, // "set"      a reserved word
      NATCONST = 10, // 0|[1-9][0-9]*
      LEFTBRACE = 11,    // '{'
      RIGHTBRACE = 12,    // '}'
      LEFTPAREN = 13,    // '('
      RIGHTPAREN = 14,    // ')'
      SEMICOLON = 15,    // ';'
      PERIOD  =  16,    // '.'
      COMMA =  17,    // ','
      ASSIGN = 18, //  ":="  for assignment
      SUBSET = 19, //  "<="  for is subset of 
      EQUALS = 20,  // '='   for equality comparisons of sets
      NOT =  21, //  "not" for boolean negation; so "not" is reserved
      INTERSECTION = 22 ,  // '*'   for set intersection
      UNION = 23,  // '+'   for set union
      SETDIFFERENCE = 24, // '\'   for binary set difference
      COMPLEMENT = 25,  // '-'   for unary set complement
      IS_IN  = 26, //  "in"  for set membership; so "in" is reserved
      THEN = 27, // "then" a reserved word
      CMP = 28, // "CMP" a reserved word
      EOF = 29, // to stand for reaching the end of the file
      UNRECOGNIZED = 30; // for anything else

   private int
      lineNum,  // line of the source file where the token came from
      tokenType;  // uses the constants defined above to name the token

   private String tokenString; // required for nat constants, identifiers, and unrecognized

           
   public Token(int tkCode, int ln){
      if (tkCode < 0 || tkCode >= TOKEN_LABELS.length ||
         ln < 1 || tkCode == UNRECOGNIZED   || tkCode == ID || tkCode == NATCONST){
         tokenType = UNRECOGNIZED;
         tokenString = "invalidly constructed";
         lineNum = -1;
      }
      else{
         lineNum = ln;
         tokenType = tkCode; // enough for all but NATCONST, ID, and UNRECOGNIZED        
         tokenString = "";           
      }
   }

   // to handle NATCONST, ID, UNRECOGNIZED; should only be called with those
   // tokens
   public Token(int tkCode, int ln, String tkString){
      if (ln < 1 || tkString == null || tkString.length() == 0 || Character.isWhitespace(tkString.charAt(0))
         || (tkCode != UNRECOGNIZED && tkCode != ID && tkCode != NATCONST)){
         tokenType = UNRECOGNIZED;
         tokenString = "invalidly constructed";
         lineNum = -1;
      }
      else{
         tokenType = tkCode;
         tokenString = tkString;
         lineNum = ln;
      }
   }

   // getters

   public int getTokenType(){
      return tokenType;
   }

   public int getLineNum(){
      return lineNum;
   }

   public String getTokenString(){
      return tokenString;
   }

   public String toString(){

      StringBuilder bldr = new StringBuilder();

      bldr.append(TOKEN_LABELS[tokenType]);

      if (tokenType == NATCONST || tokenType == ID ||
         tokenType == UNRECOGNIZED){
         bldr.append("(\"");
         bldr.append(tokenString);
         bldr.append("\")");
      }
      bldr.append("[line: ");
      bldr.append(lineNum);
      bldr.append(']');
      return bldr.toString();

   } 

   public int compareTo(Token tk){
      return tokenType - tk.tokenType;
   }

   public boolean equals(Token tk){
      return tokenType == tk.tokenType;
   }

   public static void main(String[] args){

      int i;

      Token tk = null;

      // tests for valid tokens
      for (i = 0; i < TOKEN_LABELS.length; i++){
         if (i == NATCONST)
            tk = new Token(i,i+1, "123");
         else if (i == ID)
            tk = new Token(i,i+1, "abc123");
         else if (i == UNRECOGNIZED)
            tk = new Token(i,i+1, "?notatoken?");
         else
            tk = new Token(i,i+1);

         System.out.println("Token for i = " + i + ": " + tk);
      }
     
      // test for errors
      System.out.println("The rest should be invalid.");

      for (i = 0; i < TOKEN_LABELS.length; i++){
         if (i == NATCONST)
            ;
         else if (i == ID)
            ;
         else if (i == UNRECOGNIZED)
            ;
         else
            tk = new Token(i,i+1, "abc");

         System.out.println("Token for i = " + i + ": " + tk);
      }

      tk = new Token(THEN,0);
      System.out.println("" + tk);

      tk = new Token(TOKEN_LABELS.length, 1);
      System.out.println("" + tk);

      tk = new Token(ID,2);
      System.out.println("" + tk);

      tk = new Token(NATCONST,3);
      System.out.println("" + tk);

      tk = new Token(UNRECOGNIZED,4);
      System.out.println("" + tk);

      tk = new Token(ID,2, null);
      System.out.println("" + tk);

      tk = new Token(NATCONST,3, null);
      System.out.println("" + tk);

      tk = new Token(UNRECOGNIZED,4, null);
      System.out.println("" + tk);

      tk = new Token(ID,2, "");
      System.out.println("" + tk);

      tk = new Token(NATCONST,3, "");
      System.out.println("" + tk);

      tk = new Token(UNRECOGNIZED,4, "");
      System.out.println("" + tk);

      tk = new Token(ID,2, " ");
      System.out.println("" + tk);

      tk = new Token(NATCONST,3, "\t");
      System.out.println("" + tk);

      tk = new Token(UNRECOGNIZED,4, "\n");
      System.out.println("" + tk);
      
   }
}
      
