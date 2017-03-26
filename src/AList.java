/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author pravar
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
    static void addPairs(SExp params, SExp args) {
        if(aList.isNil())
            aList = new SExp(new SExp(params, args), aList);
        SExp node;
        for(node = aList; !node.cdr().isNil(); node=node.cdr());
        node.setRight(new SExp(new SExp(params, args), node));
    }
    
    @SuppressWarnings("empty-statement")
    static void destroy(SExp params) {
        assert !aList.isNil();
        boolean flag = destroyRecursively(aList, null, params);
        assert flag;
    }
    
    private static boolean destroyRecursively(SExp node, SExp parent, SExp params) {
        if(node.isNil()) return false;
        boolean flag = destroyRecursively(node.cdr(), node, params);
        if(flag) return flag;
        else if(node.car().getAtomAsString().equals(params.getAtomAsString()))
        {
            if(parent == null)
                aList = node;
            else
                parent.setRight(node.cdr());
            return true;
        }
        else return false;
    }
    
}
