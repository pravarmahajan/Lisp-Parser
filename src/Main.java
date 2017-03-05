/**
 * TODO.:
 * (1) 14AB
 * (2) Does not handle $$ yet
 * (3) Handle multiline IO
 * (4) Appropriate exceptions
 * @author pravar
 */
public class Main {
    public static void main(String[] args){
        IOHandler io = new IOHandler();
        String inputExpressions;
        
        while(true){
            Parser parser = new Parser();
            inputExpressions = io.inputExpression();
            if(io.getExitSignal()) {
                System.out.println("Bye!");
                break;
            }
            //inputExpressions = "(2 . ((3 . 4) . ((5 . 6) . NIL)))$";
            try {
                SExp parsedExpression =
                        parser.getParsedSExpressions(inputExpressions);
                io.printSExpression(parsedExpression);
            }
            catch(ParseError e) {
                System.err.println(e.getMessage());
            }
        }
    }
}