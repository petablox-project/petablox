#include <iostream>
#include "llvm/Support/raw_ostream.h"
#include "llvm/IR/InstrTypes.h"
#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/Module.h"
#include "translate/facts.h"
#include "type.h"

using namespace std;
using namespace llvm;

/*
 * translateOperand
 *
 * Extracts the following relations from an operand:
 * (1) declares the id as an operand
 * (2) declares the operand as either a constant or a 
 *     variable
 * (3) declares the type of the operand
 * (4) if the operand is a constant, declares the value
 */
void translateOperand(Value *operand) {
    // Declare this value as an operand
    unsigned long id = (unsigned long) operand;
    print_fact(OPERAND, id);

    // Get tye type of the operand
    string type = processType(operand->getType());

    // If the operand is a constant
    if (Constant *constant = dyn_cast<Constant>(operand)) {
        print_fact(CONSTANT, id);
        print_fact(CONSTANT_TYPE, id, type);

        // The constant can be either a integer
        if (ConstantInt *value = dyn_cast<ConstantInt>(constant)) {
            const APInt &val = value->getValue();
            print_fact(CONSTANT_VAL, id, val);
        }

        // or a floating point
        else if (ConstantFP *value = dyn_cast<ConstantFP>(constant)) {
            const APFloat &val = value->getValueAPF();
            print_fact(CONSTANT_VAL, id, val.convertToFloat());
        }

    }
    // Otherwise, the operand is a variable
    else {
        print_fact(VARIABLE, id);
        print_fact(VARIABLE_TYPE, id, type);
    }
}