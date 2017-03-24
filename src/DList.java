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

public class DList extends SExp {
    public DList() {
        SExp(new SExp("NIL"), new SExp("NIL"));
    }

    public void addFunction(String funcName, SExp paramList, SExp body) {
        addFuncHelper(funcName, paramList, body, left);
    }

    private void addFuncHelper(String funcName, SExp paramList,
                                SExp body, SExp head) {
        if(head.isNil()) {
            SExp left = new SExp(funcName);
            SExp right = new SExp(paramList, body);
            head = new SExp(left, right);
        }
        else {
            addFuncHelper(funcName, paramList, body, right);
        }
    }

}
