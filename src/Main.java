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
        while(true){
            Parser parser = new Parser();
            inputExpressions = io.inputExpression();
            
            if(io.getExitSignal()) {
                System.out.println("Bye!");
                break;
            }
            
            try {
                SExp parsedExpression =
                        parser.getParsedSExpressions(inputExpressions);
                io.printSExpression(parsedExpression); //Output the sexp tree
                Evaluator.eval(parsedExpression);

            }
            catch(ParseError err) {
                System.err.println(err.getMessage());
            }
            catch(EvaluationError err) {
                System.err.println(err.getMessage());
            }
        }
    }
}
