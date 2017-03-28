/**
 * This class defines the declaration list.
 * That is, it contains all the functions which have been defined using DEFUN,
 * along with params and function body. The D-List has been implemented as 
 * an s-expression and all the operations - addition, modification and searching
 * are done recursively on the s-expression.
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
