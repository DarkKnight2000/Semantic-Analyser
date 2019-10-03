package cool;

import java.util.*;

public class Semantic{
	private boolean errorFlag = false;
	private HashMap<String,AST.class_> classMap;
	private ArrayList<String> allClsNames, lateDeclared;
	private HashMap<String, ArrayList<String>> inherGraph;

	public void reportError(String filename, int lineNo, String error){
		errorFlag = true;
		System.err.print(filename+":"+lineNo+": "+error);
	}
	public boolean getErrorFlag(){
		return errorFlag;
	}

/*
	Don't change code above this line
*/
	public Semantic(AST.program program){
		//Write Semantic analyzer code here
		classMap = new HashMap<String,AST.class_>();
		allClsNames = new ArrayList<String>();
		lateDeclared = new ArrayList<String>();
		inherGraph = new HashMap<String, ArrayList<String>>();

		AST.class_ IO = new AST.class_("IO", "", "", new ArrayList<AST.feature>(Arrays.asList(
			(AST.feature) new AST.method("out_string", new ArrayList<AST.formal>(Arrays.asList(new AST.formal("x", "String", 0))), "SELF_TYPE",(AST.expression) new AST.no_expr(0), 0),
			(AST.feature) new AST.method("out_int", new ArrayList<AST.formal>(Arrays.asList(new AST.formal("x", "Int", 0))), "SELF_TYPE",(AST.expression) new AST.no_expr(0), 0),
			(AST.feature) new AST.method("in_string", new ArrayList<AST.formal>(), "String",(AST.expression) new AST.no_expr(0), 0),
			(AST.feature) new AST.method("in_int", new ArrayList<AST.formal>(), "Int",(AST.expression) new AST.no_expr(0), 0)
		)), 0);

		AST.class_ Object = new AST.class_("Object", "", "", new ArrayList<AST.feature>(Arrays.asList(
			(AST.feature) new AST.method("abort", new ArrayList<AST.formal>(), "Object",(AST.expression) new AST.no_expr(0), 0),
			(AST.feature) new AST.method("type_name", new ArrayList<AST.formal>(), "String",(AST.expression) new AST.no_expr(0), 0),
			(AST.feature) new AST.method("copy", new ArrayList<AST.formal>(), "SELF_TYPE",(AST.expression) new AST.no_expr(0), 0)
		)), 0);

		allClsNames.add(IO.name);
		classMap.put(IO.name, IO);
		allClsNames.add(Object.name);
		classMap.put(Object.name, Object);

		// Adding classes into inheritance graph--late declarations considered
		for(AST.class_ cl: program.classes){
			if(allClsNames.contains(cl.name)){
				reportError(cl.filename, cl.lineNo, "Class "+cl.name+" was previously defined\n");
				continue;
			}
			allClsNames.add(cl.name);
			classMap.put(cl.name, cl);
			lateDeclared.remove(cl.name);
			if(!inherGraph.containsKey(cl.parent)){
				inherGraph.put(cl.parent, new ArrayList<String>());
			}
			inherGraph.get(cl.parent).add(cl.name);
			if(!allClsNames.contains(cl.parent)){
				lateDeclared.add(cl.parent);
			}
		}
		if(getErrorFlag()) return;

		lateDeclared.removeAll(Arrays.asList("","IO","Object"));
		if(!lateDeclared.isEmpty()){
			for(String clpar: lateDeclared){
				for(String child: inherGraph.get(clpar)){
					AST.class_ c = classMap.get(child);
					reportError(c.filename, c.lineNo, "Class "+c.name+" inherits from an undefined class "+c.parent+"\n");
				}
			}
			return;
		}

		if(!checkCycles()) return;
		if(!allClsNames.contains("Main")){
			reportError("", 0, "Class Main is not found");
			return;
		}

		allClsNames.removeAll(Arrays.asList("IO","Object"));
		for(String cln: allClsNames){
			System.out.println(cln);
			AST.class_ cl = classMap.get(cln), cpl = classMap.get(cl.parent);
			String err = cl.addParFeats(cpl.methods, cpl.attrs);
			if(!err.equals("")) reportError(cl.filename, cl.lineNo, err);
		}
	}

	private boolean checkCycles(){
		Queue<String> bfsQueue = new LinkedList<String>();
		ArrayList<String> orderCls = new ArrayList<String>();
		HashMap<String, Boolean> isDone = new HashMap<String,Boolean>();
		for(String clname: allClsNames) isDone.put(clname, false);
		int total = allClsNames.size();
		bfsQueue.addAll(Arrays.asList("IO","Object"));
		int visited = bfsQueue.size();
		while(!bfsQueue.isEmpty()){
			String cl = bfsQueue.peek();
			bfsQueue.remove();
			allClsNames.remove(cl);
			orderCls.add(cl);
			isDone.put(cl, true);
			System.out.println("x"+cl);
			if(inherGraph.containsKey(cl)){
				for(String chld: inherGraph.get(cl)){
					if(!isDone.get(chld)){
						visited++;
						bfsQueue.add(chld);
					}
				}
			}
		}
		if(visited!=total){
			for(String chld: allClsNames){
				reportError(classMap.get(chld).filename, classMap.get(chld).lineNo, "Class "+chld+", or an ancestor is involved in an inheritance cycle\n");
			}
			return false;
		}
		allClsNames.clear();
		allClsNames.addAll(orderCls);
		return true;
	}
}