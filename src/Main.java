/**
 * Link to symbol table
 * Empty list
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