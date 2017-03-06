/**
 * Defines methods to read a lisp code and convert it to corresponding SExp obj.
 * Uses recursive descent method to parse the S-Expression
 * Error handling is done and appropriate error messages are returned in case
 * parser fails to read the expression fully.
 * Rules of grammar for parsing the expressions are as follows:
 * <s-exp> ::= <atom> | () | (<s-exp>.<s-exp>) | (<s-exp><rest>
 * <rest>  ::= ) | <space><s-exp> <rest>
 * <atom>  ::= <number> | <symbol>
 */

import java.util.regex.*;

/**
 * This is the parser class converts the input s-expression and generates
 * the corresponding to s-expression.
 * The rules of grammar for parsing the s-expression are as defined above.
 */
public class Parser {
    public SExp getParsedSExpressions(String inputStream) throws ParseError {
        TokenAnalyser tokenAnalyser = new TokenAnalyser(inputStream);
        SExp terminalExpression = parseNextSExpression(tokenAnalyser);
        tokenAnalyser.skipWhitespaces();
        if(!tokenAnalyser.isEndOfExpression())
            throw new ParseError(tokenAnalyser);
        return terminalExpression;
    }
    
    private SExp parseNextSExpression(TokenAnalyser tokenAnalyser) 
            throws ParseError {
        //<s-exp> ::= <atom>
        tokenAnalyser.skipWhitespaces();
        if(tokenAnalyser.isIntegerAtom()) {
            return parseIntAtom(tokenAnalyser);
        }
        else if(tokenAnalyser.isSymbolicAtom()) {
            return parseSymbolicAtom(tokenAnalyser);
        }
        else if(tokenAnalyser.isLeftParanthesis()) {
            tokenAnalyser.skipToken();
            tokenAnalyser.skipWhitespaces();
            //<s-exp> ::= ()
            if(tokenAnalyser.isRightParanthesis())
                return new SExp("NIL");
            SExp left = parseNextSExpression(tokenAnalyser);
            tokenAnalyser.skipWhitespaces();
            if(tokenAnalyser.isDot()) {
                //<s-exp> ::= (<s-exp> . <s-exp>)
                tokenAnalyser.skipToken(); //Skipping the dot
                tokenAnalyser.skipWhitespaces();
                SExp right = parseNextSExpression(tokenAnalyser);
                tokenAnalyser.skipWhitespaces();
                if(tokenAnalyser.isRightParanthesis()) {
                    tokenAnalyser.skipToken();
                    return new SExp(left, right);
                }
                else { //Error case
                    throw new ParseError(tokenAnalyser);
                }
            }
            else{
                //<sexp> ::= (<sexp <rest>
                SExp rest = parseRest(tokenAnalyser);
                return new SExp(left, rest);
            }
        }
        else { //Error case
            throw new ParseError(tokenAnalyser);
        }
    }

    private SExp parseIntAtom(TokenAnalyser tokenAnalyser) throws ParseError {
        return tokenAnalyser.getInteger();
    }

    private SExp parseRest(TokenAnalyser tokenAnalyser) throws ParseError {
        //<sexp> ::= )
        tokenAnalyser.skipWhitespaces();
        if(tokenAnalyser.isRightParanthesis()) {
            tokenAnalyser.skipToken();
            return new SExp("NIL");
        }
        //<sexp> ::= <space><sexp><rest>        
        tokenAnalyser.skipWhitespaces();
        SExp left = parseNextSExpression(tokenAnalyser);
        SExp right = parseRest(tokenAnalyser);
        return new SExp(left, right);
    }

    private SExp parseSymbolicAtom(TokenAnalyser tokenAnalyser) {
        return tokenAnalyser.getIdentifier();
    }
}

/**
 * This class has been provided to do exception handling and generating
 * appropriate error message.
 * Based on the current token under consideration, the type of error is
 * determined.
 * @author pravar
 */
class ParseError extends Exception {
    StringBuilder errorMessage;
    public ParseError(TokenAnalyser tokenAnalyser) {
        errorMessage = new StringBuilder(">");
        if(tokenAnalyser.isDot())
            errorMessage.append("**Unexpected Dot");
        else if(tokenAnalyser.isErrorStream()
                || tokenAnalyser.isSymbolicAtom())
            errorMessage.append("**Unexpected Character: ").append(
                    tokenAnalyser.getTokenError());
        else if(tokenAnalyser.isIntegerAtom())
            errorMessage.append("**Ill formed integer atom: ");
        else if(tokenAnalyser.isLeftParanthesis())
            errorMessage.append("**Unexpected '('");
        else if(tokenAnalyser.isRightParanthesis())
            errorMessage.append("**Unexpected ')'");
        else if(tokenAnalyser.isIllegalDollar())
            errorMessage.append("**Unexpected '$'");
        else if(tokenAnalyser.isUnexpectedEOS())
            errorMessage.append("**Unexpected end of expression");
        else
            errorMessage.append("**Illegal Expression");
        errorMessage.append(" at location ").append(tokenAnalyser.getLocation());
        errorMessage.append("**");
    }
    
    public ParseError(String error) {
        errorMessage = new StringBuilder();
        errorMessage.append(error);
    }
    
    @Override
    public String getMessage() {
        return errorMessage.toString();
    }
}
/**
 * This class provides methods to extract tokens from the input stream.
 * The constructor requires the string object to work on as input. The object
 * maintains an additional variable called pointer, which points to the current
 * character of the input stream under consideration.
 * The function checkNextToken determines the type of the next token without
 * advancing the pointer. The type have been defined in the enum TokenType
 * To extract the tokens, the methods provided are getIdentifier and getInteger
 * which work on Identifier and Integer symbols respectively. They move the
 * pointer to the location after the end of the identifier. Regular expressions
 * have been defined for matching identifier and integer types.
 */
class TokenAnalyser {
    String inputBuffer;
    int pointer;
    Pattern symbolPattern, intPattern;
    Matcher symbolMatcher, intMatcher;
    
    public TokenAnalyser(String input) {
        inputBuffer = input;
        pointer = 0;
        symbolPattern = Pattern.compile("[A-Za-z]\\w*");
        intPattern = Pattern.compile("([-+]?\\d+)([\\s+\\.\\)])");
        symbolMatcher = symbolPattern.matcher(inputBuffer);
        intMatcher = intPattern.matcher(inputBuffer);
    }

    void skipToken() {
        pointer++;
    }

    boolean isRightParanthesis() {
        return checkNextToken() == TokenType.R_BRAC;
    }

    boolean isDot() {
        return checkNextToken() == TokenType.DOT;
    }

    boolean isWhiteSpace() {
        return checkNextToken() == TokenType.WHITESPACE;
    }

    void skipWhitespaces() {
        while(isWhiteSpace())
            pointer++;
    }

    StringBuilder getTokenError() {
        StringBuilder tokenerr = new StringBuilder().append(
                inputBuffer.charAt(pointer));
        
        return tokenerr;
    }

    int getLocation() {
        return pointer + 1;
    }

    boolean isIllegalDollar() {
        return checkNextToken() == TokenType.DOLLAR && !isEndOfExpression();
    }
    
    enum TokenType {
        L_BRAC,      //The left paranthesis '('
        R_BRAC,      //The right paranthesis ')'
        DOT,        
        WHITESPACE,
        IDENTIFIER, 
        INTEGER,
        END,         //End of Expression
        ERROR,       //Unacceptable character or unexpected end of stream
        DOLLAR,        
        ERROR_EOS    //Unexpected end of stream error
    }

    private TokenType checkNextToken() {
        if(pointer >= inputBuffer.length())
            return TokenType.ERROR_EOS;
        switch(inputBuffer.charAt(pointer)) {
            case '(': return TokenType.L_BRAC;
            case ')': return TokenType.R_BRAC;
            case '.': return TokenType.DOT;
            case '$': return TokenType.DOLLAR;
            case ' ': case '\n': case '\t': case '\r': return TokenType.WHITESPACE;
            default : return atomType(inputBuffer.charAt(pointer));
        }
    }
    
    public boolean isEndOfExpression() {
        return pointer == inputBuffer.length() - 1;
    }
    
    public boolean isErrorStream() {
        return checkNextToken() == TokenType.ERROR;
    }
    
    public boolean isUnexpectedEOS() {
        return checkNextToken() == TokenType.ERROR_EOS;
    }
    public boolean isLeftParanthesis() {
        return checkNextToken() == TokenType.L_BRAC;
    }
    
    public boolean isIntegerAtom() {
        return checkNextToken() == TokenType.INTEGER;
    }
    
    public boolean isSymbolicAtom() {
        return checkNextToken() == TokenType.IDENTIFIER;
    }

    public TokenType atomType(char ch) {
        if(Character.isDigit(ch) || ch=='+' || ch == '-')
            return TokenType.INTEGER;
        else if(Character.isLetter(ch))
            return TokenType.IDENTIFIER;
        else
            return TokenType.ERROR;
    }
    
    public SExp getIdentifier() {
        symbolMatcher.find(pointer);
        String identifier = symbolMatcher.group();
        pointer = symbolMatcher.end();
        return new SExp(identifier);
    }
    
    public SExp getInteger() throws ParseError { 
        intMatcher.find(pointer);
        int number;
        try {
            number = Integer.parseInt(intMatcher.group(1));
            pointer += intMatcher.group(1).length();
            return new SExp(number);
        }
        catch(IllegalStateException exception) {
            throw new ParseError(this);
        }
    }
}