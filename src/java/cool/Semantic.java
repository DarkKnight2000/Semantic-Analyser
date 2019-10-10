package cool;

import java.util.*;

public class Semantic{
	private boolean errorFlag = false;
	private HashMap<String,AST.class_> classMap;
	private ArrayList<String> allClsNames, lateDeclared;
	private HashMap<String, ArrayList<String>> inherGraph;
	private ScopeTable<AST.ASTNode> scopeTable;
	private HashMap<String,Integer> depths;

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
		scopeTable = new ScopeTable<AST.ASTNode>();
		depths = new HashMap<String, Integer>();

		AST.class_ Object = new AST.class_("Object", "", "", new ArrayList<AST.feature>(Arrays.asList(
			(AST.feature) new AST.method("abort", new ArrayList<AST.formal>(), "Object",(AST.expression) new AST.no_expr(0), 0),
			(AST.feature) new AST.method("type_name", new ArrayList<AST.formal>(), "String",(AST.expression) new AST.no_expr(0), 0),
			(AST.feature) new AST.method("copy", new ArrayList<AST.formal>(), "SELF_TYPE",(AST.expression) new AST.no_expr(0), 0)
		)), 0);

		AST.class_ IO = new AST.class_("IO", "", "Object", new ArrayList<AST.feature>(Arrays.asList(
			new AST.method("out_string", new ArrayList<AST.formal>(Arrays.asList(new AST.formal("x", "String", 0))), "SELF_TYPE",(AST.expression) new AST.no_expr(0), 0),
			(AST.feature) new AST.method("out_int", new ArrayList<AST.formal>(Arrays.asList(new AST.formal("x", "Int", 0))), "SELF_TYPE",(AST.expression) new AST.no_expr(0), 0),
			(AST.feature) new AST.method("in_string", new ArrayList<AST.formal>(), "String",(AST.expression) new AST.no_expr(0), 0),
			(AST.feature) new AST.method("in_int", new ArrayList<AST.formal>(), "Int",(AST.expression) new AST.no_expr(0), 0)
		)), 0);
		IO.addParFeats(Object.methods, Object.attrs);

		AST.class_ Int = new AST.class_("Int", "", "Object", new ArrayList<AST.feature>(), 0);
		Int.addParFeats(Object.methods, Object.attrs);
		AST.class_ Bool = new AST.class_("Bool", "", "Object", new ArrayList<AST.feature>(), 0);
		Bool.addParFeats(Object.methods, Object.attrs);
		AST.class_ String = new AST.class_("String", "", "Object", new ArrayList<AST.feature>(), 0);
		String.addParFeats(Object.methods, Object.attrs);

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
			if(cl.parent.equals("Int")||cl.parent.equals("String")||cl.parent.equals("Bool")){
				reportError(cl.filename, cl.lineNo, "Class "+cl.name+" cannot inherit from "+cl.parent+"\n");
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
		classMap.put("Int",Int);
		classMap.put("Bool",Bool);
		classMap.put("String",String);
		for(String cln: allClsNames){
			AST.class_ cl = classMap.get(cln), cpl = classMap.get(cl.parent);
			String err = cl.getErrDecl();
			if(!err.equals("")) {System.out.print(err);errorFlag=true;}
			err = "";
			err += cl.addParFeats(cpl.methods, cpl.attrs);
			if(!err.equals("")) {System.out.print(err);errorFlag=true;}
			scopeTable.insert(cl.name, (AST.ASTNode) cl);
			scopeTable.enterScope();
			scopeTable.insert("self", (AST.ASTNode) cl);
			for(AST.attr a: cl.attrs)  scopeTable.insert(a.name, (AST.ASTNode) a);
			String errbody = "";
			for(AST.attr a: cl.attrs)  errbody += a.setType(cl.filename, scopeTable, classMap, depths);
			for(AST.method m: cl.methods){
				//errbody = "";
				scopeTable.insert(m.name, (AST.ASTNode) m);
				scopeTable.enterScope();
				for(AST.formal f: m.formals) scopeTable.insert(f.name, (AST.ASTNode) f);
				errbody += m.body.setType(cl.filename,scopeTable, classMap, depths);
				if(!m.body.type.equals("_no_type") && (!AST.expression.isAncestor(m.body.type, m.typeid, classMap) || (m.typeid.equals("SELF_TYPE") && !AST.expression.isAncestor(m.body.type, m.typeid, classMap)) || (m.body.type.equals("SELF_TYPE") && !AST.expression.isAncestor(m.name, m.typeid, classMap)))) reportError(cl.filename, m.lineNo, "In the definition of " + m.name + " inferred return type "+m.body.type+" does not conform to the declared return type "+m.typeid+"\n");
				scopeTable.exitScope();
			}
			if(!errbody.equals("")) {System.out.print(errbody); errorFlag = true;}
			scopeTable.exitScope();
			//errorFlag = true;
		}
	}

	private boolean checkCycles(){
		Queue<String> bfsQueue = new LinkedList<String>();
		ArrayList<String> orderCls = new ArrayList<String>();
		HashMap<String, Boolean> isDone = new HashMap<String,Boolean>();
		for(String clname: allClsNames) isDone.put(clname, false);
		int total = allClsNames.size();
		bfsQueue.addAll(Arrays.asList("IO","Object"));
		depths.put("Object", 1);
		depths.put("IO",2);
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
						depths.put(chld,depths.get(cl)+1);
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
