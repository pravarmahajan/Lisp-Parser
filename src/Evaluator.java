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
        assert func.isSymbolicAtom();
        if(func.equalsSymbol("CAR"))
            return args.car().car();
        else if(func.equalsSymbol("CDR")) 
            return args.car().cdr();
        else if(func.equalsSymbol("CONS"))
            return args.car().cons(args.cdr().car());
        else if(func.equalsSymbol("ATOM"))
            return SExp.boolToSExp(args.isAtom());
        else if(func.equalsSymbol("NULL"))
            return SExp.boolToSExp(!args.car().isNil());
        else if(func.equalsSymbol("EQ")) {
            return args.car().equals(args.cdr().car());
        }
        else if(func.equalsSymbol("INT")) {
            checkIntegerAtoms(args.car(), args.cdr().car());                
            return SExp.boolToSExp(args.isIntegerAtom());
        }
        else if(func.equalsSymbol("PLUS")) {
            checkIntegerAtoms(args.car(), args.cdr().car());
            return SExp.plus(args.car(), args.cdr().car());
        }
        else if(func.equalsSymbol("MINUS")) {
            checkIntegerAtoms(args.car(), args.cdr().car());
            return SExp.minus(args.car(), args.cdr().car());
        }
        else if(func.equalsSymbol("TIMES")) {
            checkIntegerAtoms(args.car(), args.cdr().car());
            return SExp.times(args.car(), args.cdr().car());
        }
        else if(func.equalsSymbol("QUOTIENT")) {
            checkIntegerAtoms(args.car(), args.cdr().car());
            return SExp.quotient(args.car(), args.cdr().car());
        }
        else if(func.equalsSymbol("REMAINDER")) {
            checkIntegerAtoms(args.car(), args.cdr().car());
            return SExp.remainder(args.car(), args.cdr().car());           
        }
        else if(func.equalsSymbol("LESS")) {
            checkIntegerAtoms(args.car(), args.cdr().car());
            return SExp.less(args.car(), args.cdr().car());            
        }
        else if(func.equalsSymbol("GREATER")) {
            checkIntegerAtoms(args.car(), args.cdr().car());
            return SExp.greater(args.car(), args.cdr().car());            
        }
        else {
            SExp params = DList.getFuncParams(func);
            assert !params.isNil();
            AList.addPairs(params, args);
            SExp res = eval(DList.getFuncBody(func));
            AList.destroy(params);
            return res;
        }
        
    }

    private static void checkIntegerAtoms(SExp op1, SExp op2)
            throws EvaluationError {
        if(!op1.isIntegerAtom() || !op2.isIntegerAtom())
            throw new EvaluationError("Operands must be integers!");
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
            else throw new EvaluationError("unbound variable: "
                    + sExp.getAtomAsString());
        }
        else if(sExp.car().isSymbolicAtom()) {
                SExp car = sExp.car();
                SExp cdr = sExp.cdr();
                if(car.equalsSymbol("QUOTE")) {
                    if(!cdr.isNil() && cdr.cdr().isNil())
                        return cdr.car();
                    else if(cdr.isNil())
                        throw new EvaluationError("Too few params for QUOTE");
                    else throw new EvaluationError("Too many params for QUOTE");
                }
                else if(car.equalsSymbol("COND")) {
                    if(cdr.isNil())
                        throw new EvaluationError("Too few params for COND");
                    return evcon(cdr);
                }
                else if(car.equalsSymbol("DEFUN")) {
                    if(cdr.isNil())
                        throw new EvaluationError("Cannot form function with NIL");
                    else if(!cdr.car().isSymbolicAtom())
                        throw new EvaluationError("Name of the function should be symbol");
                    String funcName = cdr.car().getAtomAsString();
                    SExp params = cdr.cdr().car();
                    if(params.isAtom())
                        throw new EvaluationError("Params list should be a list"
                         + ", not an atom");
                    SExp body = cdr.cdr().cdr().car();
                    DList.addFunction(funcName, params, body);
                    return SymbolTable.getSExpForAtom(funcName);
                }
                else {
                    if(sExp.cdr().isSymbolicAtom())
                        throw new EvaluationError("Error evaluating "
                                        + sExp.getDottedNotation());
                    return applyFun(sExp.car(), evlist(sExp.cdr()));
                }
            }
        else {
            throw new EvaluationError(sExp.car().getDottedNotation()
                    + " is not a function");
        }
    }
    
    public static final SExp evcon(SExp condExpressions) throws EvaluationError {
        if(condExpressions.isNil())
            throw new EvaluationError("No cases matched!");
        else if(condExpressions.car().isAtom())
            throw new EvaluationError(condExpressions.car().getAtomAsString()
                + " should be a list");
        else if(condExpressions.car().car().equalsSymbol("QUOTE"))
            throw new EvaluationError("QUOTE has no value");
        else if(!eval(condExpressions.car().car()).isNil()) {
            return eval(condExpressions.car().cdr().car());
        }
        else {
            return evcon(condExpressions.cdr());
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