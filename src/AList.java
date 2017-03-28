/** Class Alist.
 * This class forms the association list, ie, the mapping of symbols to corresponding
 * values when passed in the form of an argument. The variables/values pairs
 * reside only through the lifetime of the function and vanish once the function
 * disappears, being in spirit with LISP's functional programming paradigm. 
 * The variables/values pairs are stored as s-expression, in the following way:
 * ((var_one.val_one) (var_two.val_two) ..... (var_n.val_n))
 * Since the list itself has been implemented as an s-expression list, it's 
 * convenient to do search, deleting, adding etc recursively. Hence for every
 * operation n the A-list, a corresponding recursive helper function has been
 * provided, which is private in scope.
 * @author: Pravar Mahajan
 */

class AList{
    private static SExp aList = SymbolTable.getSExpForAtom("NIL");
    
    private AList() {}
    
    static boolean contains(String symbol) {
        return containsRecursive(aList, symbol);
    }
    
    private static boolean containsRecursive(SExp node, String symbol) {
        if(node.isNil())
            return false;
        else if(node.car().car().equalsSymbol(symbol))
            return true;
        else return containsRecursive(node.cdr(), symbol);
    }

    static SExp getValue(String symbol) {
        SExp value = getValueRecursive(aList, symbol);
        assert value!=null;
        return value;
    }
    
    private static SExp getValueRecursive(SExp node, String symbol) {
        if(node.isNil())
            return null;
        SExp value = getValueRecursive(node.cdr(), symbol);
        if(value != null)
            return value;
        else if(symbol.equals(node.car().car().getAtomAsString()))
            return node.car().cdr(); 
        else return null;
    }
    
    @SuppressWarnings("empty-statement")
    static void addPairs(SExp params, SExp args) throws EvaluationError {
        SExp node;
        if(aList.isNil()) {
            aList = new SExp(new SExp(params.car(), args.car()), aList);
            params = params.cdr();
            args = args.cdr();
        }
        for(node = aList; !node.cdr().isNil(); node=node.cdr());
        addPairsRecursively(node, params, args);
    }
    
    private static void addPairsRecursively(SExp node, SExp params, SExp args)
            throws EvaluationError {
        assert node.cdr().isNil();
        if(params.isNil() && args.isNil())
            return;
        else if(params.isNil())
            throw new EvaluationError("Too many arguments!");
        else if(args.isNil())
            throw new EvaluationError("Too few arguments!");
        node.setRight(new SExp(new SExp(params.car(), args.car()), node.cdr()));
        addPairsRecursively(node.cdr(), params.cdr(), args.cdr());
    }
    
    @SuppressWarnings("empty-statement")
    static void destroy(SExp params) {
        assert !aList.isNil() && !params.isNil();
        while(!params.isNil()) {
            boolean flag = destroyRecursively(aList, null, params.car());
            assert flag;
            params = params.cdr();
        }
    }
    
    private static boolean destroyRecursively(SExp node, SExp parent, SExp param) {
        if(node.isNil()) return false;
        boolean flag = destroyRecursively(node.cdr(), node, param);
        if(flag) return flag;
        else if(node.car().car().getAtomAsString().equals(param.getAtomAsString()))
        {
            if(parent == null)
                aList = node.cdr();
            else
                parent.setRight(node.cdr());
            return true;
        }
        else return false;
    }
    
}
