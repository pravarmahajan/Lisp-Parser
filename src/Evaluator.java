/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author pravar
 */
public class Evaluator {

    private static SExp applyFun(SExp func, SExp args) throws EvaluationError {
        if(func.isAtom()) {
            if(func.equalsSymbol("CAR"))
                return args.car().car();
            else if(func.equalsSymbol("CDR")) 
                return args.car().cdr();
            else if(func.equalsSymbol("CONS"))
                return args.car().cons(args.cdr());
            else if(func.equalsSymbol("ATOM"))
                return SExp.boolToSExp(args.isAtom());
            else if(func.equalsSymbol("NULL"))
                if(args.car().isNil())
                return SExp.boolToSExp(!args.isAtom());
            else if(func.equalsSymbol("EQ"))
                return args.car().equals(args.cdr().car());
            else {
                SExp params = DList.getFuncParams(func);
                AList.addPairs(params, args);
                SExp res = eval(DList.getFuncBody(func));
                AList.destroy(params);
                return res;
            }
        }
        else throw new EvaluationError("Unbound function!");
        throw new EvaluationError("This statement should never be reached");
    }
    
    //Private constructor to ensure no objects of this class are created
    private Evaluator() {}
    
    public static final SExp evlist(SExp list) throws EvaluationError {
        if(list.isNil()) {
            return list;
        }
        else {
            SExp head, rest;
            head = eval(list.car());
            rest = evlist(list.cdr());
            return head.cons(rest);
        }
    }
    
    public static final SExp eval(SExp sExp) throws EvaluationError {
        if(sExp.isAtom()) {
            if(sExp.isIntegerAtom() || sExp.isTrue() || sExp.isNil())
                return sExp;
            else if(AList.contains(sExp.getAtomAsString()))
                return AList.getValue(sExp.getAtomAsString());
            else throw new EvaluationError("unbound variable!");
        }
        else if(sExp.car().isAtom()) {
                SExp car = sExp.car();
                SExp cdr = sExp.cdr();
                if(car.equalsSymbol("QUOTE"))
                    return cdr.car();
                else if(car.equalsSymbol("COND"))
                    return evcon(cdr);
                else if(car.equalsSymbol("DEFUN")) {
                    String funcName = cdr.car().getAtomAsString();
                    SExp params = cdr.cdr().car();
                    SExp body = cdr.cdr().cdr().car();
                    DList.addFunction(funcName, params, body);
                    return SymbolTable.getSExpForAtom("T");
                }
                else {
                    return applyFun(sExp.car(), evlist(sExp.cdr()));
                }
            }
        else {
            throw new EvaluationError("Can't apply function!");
        }
    }
    
    public static final SExp evcon(SExp binExpr) throws EvaluationError {
        if(binExpr.isNil()) return binExpr;
        else if(!eval(binExpr.car().car()).isNil()) {
            return eval(binExpr.car().cdr().car());
        }
        else {
            return evcon(binExpr.cdr());
        }
    }


}

class EvaluationError extends Exception {
    
    private final StringBuilder errorMessage;
    
    public EvaluationError(String error) {
        errorMessage = new StringBuilder();
        errorMessage.append(error);
    }
    
    @Override
    public String getMessage() {
        return errorMessage.toString();
    }    
}