import java.util.Scanner;

/**
 * This class provides routines for input and output of S-Expressions.
 * The entire input is read into a string and returned to the caller.
 * When provided with a tree like SExp object, the output routine
 * prints in memory ojbect in the full dot notation.
 * @author: Pravar Mahajan
 */
public class IOHandler {
    
    boolean exitSignal;
    
    public IOHandler() {
        exitSignal = false;
    }
    
    public boolean getExitSignal() {
        return exitSignal;
    }
    
    public void printSExpression(SExp sExpression) {
        if(sExpression != null)
            System.out.println("> " + sExpression.getDottedNotation());
    }
    
    /**
     * Read expression from the console.
     * The expression can span multiple lines, however it should always end
     * with a new line consisting of '$' character.
     * If a new line with '$$' is encountered, it singals exit flag which
     * is captured by the main function and the program quits.
     * @return concatenated expression.
     */
    public String inputExpression() {
        StringBuilder inputString = new StringBuilder();
        Scanner inputReader = new Scanner(System.in);
        String line = "";
        System.out.print("? ");
        while(true) {
            if(inputReader.hasNextLine())
                line = inputReader.nextLine();
            if(line.equals("$"))
                break;
            else if(line.equals("$$")) {
                exitSignal = true;
                break;
            }
            inputString.append(line).append('\n');
        }
        inputString.append("$\n");
        return inputString.toString().trim();
    }
}