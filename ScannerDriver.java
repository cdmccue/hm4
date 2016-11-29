import java.util.*;
/*

A class for testing the setScanner.
It creates a setScanner to read from standard in, and then 
enters a loop to fetch tokens and display them until it reaches
the EOF token.

*******************************************************/

public class ScannerDriver{

   public static void main(String[] args) throws Exception{

      // you can use i/o redirection to vary the source file
      // java ScannerDriver <src.txt
      setScanner sc = new setScanner(new Scanner(System.in));

      int i = 0;

      Token tk = null;

      do{
         tk = sc.lookahead();
         // with my skeletons, the next line throws an exception, because
         // the lookahead method returns a null value;
         System.out.println("Token #" + (++i) + ": " + tk);
         sc.consume();
      }while (tk.getTokenType() != Token.EOF);

      System.out.println("" + i + " tokens were read.");
   }
}
