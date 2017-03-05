import java.util.Scanner;

/**
 * This class provides routines for input and output of S-Expressions.
 * The entire input is read into a string and returned to the caller.
 * When provided with a tree like SExp object, the output routine
 * prints in memory ojbect in the full dot notation.
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
    
    public String inputExpression() {
        StringBuilder inputString = new StringBuilder();
        Scanner inputReader = new Scanner(System.in);
        String line = "";
        while(!line.equals("$")){
            line = inputReader.nextLine();
            inputString.append(line);
            if(line.equals("$$")) {
                exitSignal = true;
                break;
            }
        } 
        return inputString.toString();
    }
}
