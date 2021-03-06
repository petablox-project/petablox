package petablox.analyses.alias;

import java.util.HashSet;
import java.util.Set;

import soot.Local;
import soot.SootField;
import soot.Unit;
import petablox.bddbddb.Rel.PairIterable;
import petablox.bddbddb.Rel.RelView;
import petablox.bddbddb.Rel.TrioIterable;
import petablox.project.Petablox;
import petablox.project.ClassicProject;
import petablox.project.analyses.JavaAnalysis;
import petablox.project.analyses.ProgramRel;
import petablox.util.SetUtils;
import petablox.util.graph.MutableLabeledGraph;
import petablox.util.tuple.object.Pair;
import petablox.util.tuple.object.Trio;

/**
 * Context-insensitive points-to analysis.
 * 
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Petablox(
    name = "cipa-java",
    consumes = { "VH", "FH", "HFH" }
)
public class CIPAAnalysis extends JavaAnalysis {
    private ProgramRel relVH;
    private ProgramRel relFH;
    private ProgramRel relHFH;
    private MutableLabeledGraph<Object, Object> graphedHeap = null;
    
    public void run() {
        relVH  = (ProgramRel) ClassicProject.g().getTrgt("VH");
        relFH  = (ProgramRel) ClassicProject.g().getTrgt("FH");
        relHFH = (ProgramRel) ClassicProject.g().getTrgt("HFH");
    }
    /**
     * Provides the abstract object to which a given local variable may point.
     * 
     * @param var A local variable.
     * 
     * @return The abstract object to which the given local variable may point.
     */
    public CIObj pointsTo(Local var) {
        if (!relVH.isOpen())
            relVH.load();
        RelView view = relVH.getView();
        view.selectAndDelete(0, var);
        Iterable<Unit> res = view.getAry1ValTuples();
        Set<Unit> pts = SetUtils.newSet(view.size());
        for (Unit inst : res)
            pts.add(inst);
        view.free();
        return new CIObj(pts);
    }
    /**
     * Provides the abstract object to which a given static field may point.
     * 
     * @param field A static field.
     * 
     * @return The abstract object to which the given static field may point.
     */
    public CIObj pointsTo(SootField field) {
        if (!relFH.isOpen())
            relFH.load();
        RelView view = relFH.getView();
        view.selectAndDelete(0, field);
        Iterable<Unit> res = view.getAry1ValTuples();
        Set<Unit> pts = SetUtils.newSet(view.size());
        for (Unit inst : res)
            pts.add(inst);
        view.free();
        return new CIObj(pts);
    }
    /**
     * Provides the abstract object to which a given instance field of a given abstract object may point.
     * 
     * @param obj   An abstract object.
     * @param field An instance field.
     * 
     * @return The abstract object to which the given instance field of the given abstract object may point.
     */
    public CIObj pointsTo(CIObj obj, SootField field) {
        if (!relHFH.isOpen())
            relHFH.load();
        Set<Unit> pts = new HashSet<Unit>();
        for (Unit site : obj.pts) {
            RelView view = relHFH.getView();
            view.selectAndDelete(0, site);
            view.selectAndDelete(1, field);
            Iterable<Unit> res = view.getAry1ValTuples();
            for (Unit inst : res)
                pts.add(inst);
            view.free();
        }
        return new CIObj(pts);
    }
    
    public boolean doesAliasExist(Unit q){
    	int aliases = 0;
    	{	
    		if (!relFH.isOpen())
    			relFH.load();
    		RelView view = relFH.getView();
    		view.selectAndDelete(1, q);
    		aliases = view.size();
    		view.free();
    		if(aliases > 1) return true;
    	}
    	{
    		if (!relVH.isOpen())
    			relVH.load();
    		RelView view = relVH.getView();
    		view.selectAndDelete(1, q);
    		aliases += view.size();
    		view.free();
    		if(aliases > 1) return true;
    	}
    	{
    		if (!relHFH.isOpen())
    			relHFH.load();
    		RelView view = relHFH.getView();
    		view.selectAndDelete(2, q);
    		aliases += view.size();
    		view.free();
    		if(aliases > 1) return true;
    	}
    	
    	return false;
    }
    
    /**
     * Generates a mutable labeled graph from the VH, FH & HFH relations
     * @return Labeled heap graph
     */
    public MutableLabeledGraph<Object, Object> getGraphedHeap(){
    	if(graphedHeap == null)
    		graphHeap();
    		
    	return graphedHeap;
    }
    
    private void graphHeap(){
    	graphedHeap = new MutableLabeledGraph<Object, Object>();
    	Object emptyRootNode = new Object();
    	graphedHeap.insertRoot(emptyRootNode);
    	if (!relFH.isOpen())
            relFH.load();
    	if (!relVH.isOpen())
            relVH.load();
    	if (!relHFH.isOpen())
            relHFH.load();
    	
    	PairIterable<SootField, Unit> itrFH = relFH.getAry2ValTuples();
    	for(Pair<SootField, Unit> p : itrFH)
    		graphedHeap.insertLabel(emptyRootNode, p.val1, p.val0);
    	
    	PairIterable<Local, Unit> itrVH = relVH.getAry2ValTuples();
    	for(Pair<Local, Unit> p : itrVH)
    		graphedHeap.insertLabel(emptyRootNode, p.val1, p.val0);
    	
    	TrioIterable<Unit, SootField, Unit> itrHFH = relHFH.getAry3ValTuples();
    	for(Trio<Unit, SootField, Unit> t : itrHFH)
    		graphedHeap.insertLabel(t.val0, t.val2, t.val1);
    }
    
    /**
     * Frees relations used by this program analysis if they are in memory.
     * <p>
     * This method must be called after clients are done exercising the interface of this analysis.
     */
    public void free() {
        if (relVH.isOpen())
            relVH.close();
        if (relFH.isOpen())
            relFH.close();
        if (relHFH.isOpen())
            relHFH.close();
    }
}
