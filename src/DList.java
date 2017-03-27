/**
 * This class defines interface to the declaration list,
 * that is, the list that contains all the functions and
 * corresponding definitions.
 * It is convenient to define the D-List as an S-Expression
 * therefore our class inherits from the SExp class and
 * adds a couple of methods specific to D-List alone.
 * The declaration list is a list of elements of the form
 * (funcName.(paramsList.body)) where the dot separates 
 * the left and right child of the parent s-expression
 */

public class DList{
    
    private static SExp dList = SymbolTable.getSExpForAtom("NIL");
    private DList(){}
   
    public static void addFunction(String funcName, SExp paramList, SExp body) {
        
        if(dList.isNil()) {
            SExp left = SymbolTable.getSExpForAtom(funcName);
            SExp right = new SExp(paramList, body);
            dList = new SExp(new SExp(left, right), dList);
        }
        else addFuncHelper(funcName, paramList, body, dList);
    }

    private static void addFuncHelper(String funcName, SExp paramList,
                                SExp body, SExp node) {
        if(node.car().car().getAtomAsString().equals(funcName)) {
            node.car().setRight(new SExp(paramList, body));
        }
        else if(!node.cdr().isNil()) {
            addFuncHelper(funcName, paramList, body, dList.cdr());
        }
        else {
            SExp left = SymbolTable.getSExpForAtom(funcName);
            SExp right = new SExp(paramList, body);
            node.setRight(new SExp(new SExp(left, right), node.cdr()));
        }
    }

    static SExp getFuncParams(SExp funcName) throws EvaluationError {
        return findFuncDefRecursively(funcName, dList).car();
    }
    
    private static SExp findFuncDefRecursively(SExp funcName, SExp node)
            throws EvaluationError {
        if(node.isNil())
            throw new EvaluationError("Undefined function "
                    + funcName.getAtomAsString());
        else if(node.car().car().equalsSymbol(funcName.getAtomAsString()))
            return node.car().cdr();
        else return findFuncDefRecursively(funcName, node.cdr());
    }
    
    static SExp getFuncBody(SExp funcName) throws EvaluationError {
        return findFuncDefRecursively(funcName, dList).cdr();
    }

}
