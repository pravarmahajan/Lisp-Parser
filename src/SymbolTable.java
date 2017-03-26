/**
 * This class creates abstractions for creation and maintainance of symbol
 * tables.
 * The implementation is hashmap based, ie it is a mapping from the variable
 * name to the corresponding SExp object. If a new symbol is defined, then its
 * value is updated into the symbol table. If a symbol is accessed without
 * definition, then an exception is thrown.
 * Predefined symbols like CAR, CDR, QUOTE, DEFUN, CONST, COND etc are stored
 * in the symbol table as it is initialized first time.
 */

import java.util.*;

public class SymbolTable {
    
    private static Map<String, SExp> symbolTableMap = null;
    
    
    /**Constructor for the symbol table.
     * It initializes the symbol table as a hashmap, mapping symbol strings
     * to corresponding SExp object.
     */
    private SymbolTable() {}
    
    /** Overloaded function getSExpressionForAtom.
     * Creates and returns new SExp object for an atom, if it is an integer or
     * a new symbolic atom. If it's a pre-existing symbol, then the corresponding
     * entry for the symbol is returned from the symbol table
     * @param atom: int or String, representing the atom
     * @return SExp: SExpression object corresponding to the atom 
     */
    public static SExp getSExpForAtom(int atom) {
        return new SExp(atom);
    }
    
    /**
     *
     * @param atom
     * @return
     */
    public static SExp getSExpForAtom(String atom) {
        
        if(symbolTableMap == null)
            symbolTableMap = new HashMap<>();
        
        if(symbolTableMap.containsKey(atom))
            return symbolTableMap.get(atom);
        
        SExp sExpression = new SExp(atom);
        symbolTableMap.put(atom, sExpression);
        return sExpression;
    }
}