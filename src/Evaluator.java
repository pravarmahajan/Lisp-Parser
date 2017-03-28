/**
 * This class contains functions for evaluation of the s-expression tree.
 * All the functions are private except eval, which accepts an s-expression
 * as input.
 * A separate class EvaluationError handles and generates exceptions related to
 * evaluation.
 */

/** Class: Evaluator.
 * This class evaluates an s-expression and gives simplified answer as output.
 * eval is a static function which is the only public function in the class.
 * Other functions are helper functions. applyFun applies a function on the args,
 * evallist recursively evaluates a list of arguments by calling eval. evcond
 * evaulates a condition statement.
 * Special handling has been provided for DEFUN, QUOTE and COND since these
 * functions require the arguments to not be evaluated.
 * Following are the primitive functions which have been implemented in applyFun:
 * CAR, CDR, CONS, ATOM, EQ, NULL, INT, PLUS, MINUS, TIMES, QUOTIENT, REMAINDER,
 * LESS, GREATER
 */
public class Evaluator {

    private static SExp applyFun(SExp func, SExp args) throws EvaluationError {
        assert func.isSymbolicAtom();
        if(func.equalsSymbol("CAR")) {
            checkValidNumArgs(func, args, 2);
            if(args.car().isAtom())
                throw new EvaluationError(args.car().getAtomAsString()
                        + " is an atom");
            return args.car().car();
        }
        else if(func.equalsSymbol("CDR")) {
            checkValidNumArgs(func, args, 1);
            if(args.car().isAtom())
                throw new EvaluationError(args.car().getAtomAsString()
                        + " is an atom");
            return args.car().cdr();
        }
        else if(func.equalsSymbol("CONS")) {
            checkValidNumArgs(func, args, 2);  
            return args.car().cons(args.cdr().car());
        }
        else if(func.equalsSymbol("ATOM")) {
            checkValidNumArgs(func, args, 1);
            return SExp.boolToSExp(args.car().isAtom());
        }
        else if(func.equalsSymbol("NULL")) {
            checkValidNumArgs(func, args, 1);
            return SExp.boolToSExp(args.car().isNil());
        }
        else if(func.equalsSymbol("EQ")) {
            checkValidNumArgs(func, args, 2);
            return args.car().equals(args.cdr().car());
        }
        else if(func.equalsSymbol("INT")) {
            checkValidNumArgs(func, args, 1);
            return SExp.boolToSExp(args.car().isIntegerAtom());
        }
        else if(func.equalsSymbol("PLUS")) {
            checkValidNumArgs(func, args, 2);
            checkIntegerAtoms(args.car(), args.cdr().car());
            return SExp.plus(args.car(), args.cdr().car());
        }
        else if(func.equalsSymbol("MINUS")) {
            checkValidNumArgs(func, args, 2);
            checkIntegerAtoms(args.car(), args.cdr().car());
            return SExp.minus(args.car(), args.cdr().car());
        }
        else if(func.equalsSymbol("TIMES")) {
            checkValidNumArgs(func, args, 2);
            checkIntegerAtoms(args.car(), args.cdr().car());
            return SExp.times(args.car(), args.cdr().car());
        }
        else if(func.equalsSymbol("QUOTIENT")) {
            checkValidNumArgs(func, args, 2);
            checkIntegerAtoms(args.car(), args.cdr().car());
            return SExp.quotient(args.car(), args.cdr().car());
        }
        else if(func.equalsSymbol("REMAINDER")) {
            checkValidNumArgs(func, args, 2);
            checkIntegerAtoms(args.car(), args.cdr().car());
            return SExp.remainder(args.car(), args.cdr().car());           
        }
        else if(func.equalsSymbol("LESS")) {
            checkValidNumArgs(func, args, 2);
            checkIntegerAtoms(args.car(), args.cdr().car());
            return SExp.less(args.car(), args.cdr().car());            
        }
        else if(func.equalsSymbol("GREATER")) {
            checkValidNumArgs(func, args, 2);
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

    private static void checkValidNumArgs(SExp func, SExp args, int n)
            throws EvaluationError {
        int count = 0;
        while(!args.isNil()) {
            args = args.cdr();
            count++;
        }
        if(count < n)
            throw new EvaluationError("Too few arguments to "
                    + func.getAtomAsString());
        else if(count > n)
            throw new EvaluationError("Too many arguments to "
                    + func.getAtomAsString());
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