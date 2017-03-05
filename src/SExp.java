/**
 * This is class defines represents the parsed, in-memory lisp expression.
 * The enum SExpType specifies the type of S-Expression: integer atom,
 * symbolic atom or a non atom. The type variable can be one and only one of
 * these 3 types.
 * In case the type is an integer atom, the value should of the corresponding
 * integer is stored in int val. For symbolic atoms, the name variable contains
 * the variable name. If it's a non atomic S-Expression, then left and right
 * variables point to the left and the right children nodes respectively,
 * otherwise they are null.
 * 
 */

public class SExp {
    public enum SExpType {
        INT_ATOM, SYM_ATOM, NON_ATOM
    }
    
    private SExpType type; //One of the enum SExpType
    private int value; //Only if type is INT_ATOM
    private String name; //Only if type is SYM_ATOM
    private SExp left, right; //If type is NON_ATOM
    
    String getDottedNotation() {
        StringBuilder dottedNotation = new StringBuilder();
        switch (type) {
        case INT_ATOM:
            dottedNotation.append(value);
            break;
        case SYM_ATOM:
            dottedNotation.append(name);
            break;
        default:
            dottedNotation.append("(");
            dottedNotation.append(left.getDottedNotation());
            dottedNotation.append(" . ");
            dottedNotation.append(right.getDottedNotation());
            dottedNotation.append(")");
            break;
        }
        
        return dottedNotation.toString();
    }
    
    public SExp(int number) {
        type = SExpType.INT_ATOM;
        value = number;
        name = null;
        left = null;
        right = null;
    }
    
    public SExp(String symbolName) {
        type = SExpType.SYM_ATOM;
        value = 0;
        name = symbolName;
        left = null;
        right = null;
    }
    
    public SExp(SExp leftExpr, SExp rightExpr) {
        type = SExpType.NON_ATOM;
        value = 0;
        name = null;
        left = leftExpr;
        right = rightExpr;
    }
}