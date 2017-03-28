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

    private final SExpType type; //One of the enum SExpType
    private final int value; //Only if type is INT_ATOM
    private final String name; //Only if type is SYM_ATOM
    private final SExp left;
    private SExp right; //If type is NON_ATOM

    public enum SExpType {
        INT_ATOM, SYM_ATOM, NON_ATOM
    }
    
    //Public constructors
    
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
    
    String getAtomAsString() {
        assert(type != SExpType.NON_ATOM);
        if(type == SExpType.SYM_ATOM)
            return name; 
        else
            return Integer.toString(value);
    }
    
    //Evaluation functions start here
    
    public boolean isNil() {
        return this == SymbolTable.getSExpForAtom("NIL");
    }
    
    SExp cons(SExp rest) {
        return new SExp(this, rest);
    }

    void setRight(SExp right) {
        this.right = right;
    }
    boolean isAtom() {
        return isIntegerAtom() || isSymbolicAtom();
    }

    boolean isIntegerAtom() {
        return type == SExpType.INT_ATOM;
    }
    
    boolean isSymbolicAtom() {
        return type == SExpType.SYM_ATOM;
    }

    boolean isTrue() {
        return this == SymbolTable.getSExpForAtom("T");
    }
    
    public SExp car() {
        return left;
    }
    
    public SExp cdr() {
        return right;
    }
    
    public boolean equalsSymbol(String val) {
        return type == SExpType.SYM_ATOM && name.equals(val);
    }
    
    public boolean equalsSymbol(int val) {
        return type == SExpType.INT_ATOM && value == val;
    }
    
    public SExp equals(SExp other) {
        if(type != other.type)
            return boolToSExp(false);
        else if(type == SExpType.INT_ATOM)
            return boolToSExp(value == other.value);
        else if(type == SExpType.SYM_ATOM)
            return boolToSExp(name.equals(other.name));
        else
            return boolToSExp(false);
    }
    
    public static SExp boolToSExp(boolean flag) {
        if(flag)
            return SymbolTable.getSExpForAtom("T");
        else
            return SymbolTable.getSExpForAtom("NIL");
    }
    
    static SExp plus(SExp op1, SExp op2) {
        return new SExp(op1.value+op2.value);
    }

    static SExp times(SExp op1, SExp op2) {
        return new SExp(op1.value*op2.value);
    }

    static SExp minus(SExp op1, SExp op2) {
        return new SExp(op1.value-op2.value);
    }

    static SExp quotient(SExp op1, SExp op2) {
        return new SExp(op1.value/op2.value);
    }

    static SExp remainder(SExp op1, SExp op2) {
        return new SExp(op1.value%op2.value);
    }

    static SExp less(SExp op1, SExp op2) {
        return boolToSExp(op1.value < op2.value);
    }

    static SExp greater(SExp op1, SExp op2) {
        return boolToSExp(op1.value > op2.value);
    }
    
    //Convert S-Exp to a string recursively
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
}
