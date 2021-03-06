package petablox.analyses.syntax;

import petablox.program.visitors.IExprVisitor;
import petablox.project.Petablox;
import petablox.project.analyses.ProgramRel;
import soot.PrimType;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.internal.JimpleLocal;

@Petablox(name="VarExpr", sign="EXPR0,Var0:EXPR0_Var0")
public class RelVarExpr extends ProgramRel implements IExprVisitor {
    @Override
    public void visit(Unit q) {	}

    @Override
    public void visit(SootMethod m) { }

    @Override
    public void visit(SootClass c) { }

    @Override
    public void visit(Value e) {
        if (e instanceof JimpleLocal) {
            add(e,(JimpleLocal) e);
        }
    }
}
