/**
 * This is the main class of the lisp parser
 * Takes input lisp expression from user, 
 * creates the s-expression tree internally
 * and prints s-expression in the dot notation
 */
public class Main {
    public static void main(String[] args){
        System.out.println("**LISP PARSER** v1.0");
        IOHandler io = new IOHandler();
        String inputExpressions;
        SymbolTable symbolTable = new SymbolTable();
        while(true){
            Parser parser = new Parser();
            inputExpressions = io.inputExpression();
            
            if(io.getExitSignal()) {
                System.out.println("Bye!");
                break;
            }
            
            try {
                SExp parsedExpression =
                        parser.getParsedSExpressions(inputExpressions,
                                symbolTable);
                io.printSExpression(parsedExpression); //Output the sexp tree
                parsedExpression.eval(new Alist(), new Dlist());

            }
            catch(ParseError e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
