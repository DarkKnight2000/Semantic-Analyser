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
		classMap = new HashMap<String,AST.class_>(); // Map from classname to class
		allClsNames = new ArrayList<String>(); // List of all classes
		lateDeclared = new ArrayList<String>(); // List of classes declared after a child class
		inherGraph = new HashMap<String, ArrayList<String>>(); // Adjacency list of inheritance graph
		scopeTable = new ScopeTable<AST.ASTNode>(); // Scopetable object
		depths = new HashMap<String, Integer>(); // Distance from "Object" class in inheritance class

		/* Creating base classes with respective methods */
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

		AST.class_ Int = new AST.class_("Int", "", "Object", new ArrayList<AST.feature>(), 0);
		AST.class_ Bool = new AST.class_("Bool", "", "Object", new ArrayList<AST.feature>(), 0);
		AST.class_ String = new AST.class_("String", "", "Object", new ArrayList<AST.feature>(Arrays.asList(
			(AST.feature) new AST.method("length", new ArrayList<AST.formal>(), "Int",(AST.expression) new AST.no_expr(0), 0),
			(AST.feature) new AST.method("concat", new ArrayList<AST.formal>(Arrays.asList(new AST.formal("s", "String", 0))), "String",(AST.expression) new AST.no_expr(0), 0),
			(AST.feature) new AST.method("substr", new ArrayList<AST.formal>(Arrays.asList(new AST.formal("i", "Int", 0), new AST.formal("l", "Int", 0))), "String",(AST.expression) new AST.no_expr(0), 0)
		)),0);

		allClsNames.add(IO.name);
		classMap.put(IO.name, IO);
		allClsNames.add(Object.name);
		classMap.put(Object.name, Object);

		// Adding classes into inheritance graph--late declarations considered
		for(AST.class_ cl: program.classes){
			//Duplicate declaration
			if(allClsNames.contains(cl.name)){
				reportError(cl.filename, cl.lineNo, "Class "+cl.name+" was previously defined\n");
				continue;
			}
			//Cannot inherit these classes
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

		// Late declared not empty means some classes inherit from undefined classes
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
			reportError("", 0, "Class Main is not found\n");
		}
		else{
			boolean f1 = false;
			for(AST.method m: classMap.get("Main").methods)
				if(m.name.equals("main")) f1=true;
			if(!f1) reportError(classMap.get("Main").filename, classMap.get("Main").lineNo, "Class Main doesn't contain main method\n");
		}

		classMap.put("Int",Int);
		classMap.put("Bool",Bool);
		classMap.put("String",String);
		IO.addParFeats(Object.methods, Object.attrs, classMap, scopeTable, depths);
		String.addParFeats(Object.methods, Object.attrs, classMap, scopeTable, depths);
		Int.addParFeats(Object.methods, Object.attrs, classMap, scopeTable, depths);
		Bool.addParFeats(Object.methods, Object.attrs, classMap, scopeTable, depths);

		// Checking for inheritance errors -> Errors while adding inherited features
		for(String cln: allClsNames){
			if((Arrays.asList("IO","Object")).contains(cln)) continue;
			AST.class_ cl = classMap.get(cln), cpl = classMap.get(cl.parent);
			String err = "";
			ArrayList<AST.method> arr1 = new ArrayList<AST.method>();
			ArrayList<AST.attr> arr2 = new ArrayList<AST.attr>();
			arr1.addAll(cpl.methods);
			if(cpl.methods != null) arr1.addAll(cpl.parMethods);
			arr2.addAll(cpl.attrs);
			if(cpl.methods != null) arr2.addAll(cpl.parAttrs);
			err += cl.addParFeats(arr1, arr2, classMap, scopeTable, depths);
			if(!err.equals("")) {System.out.print(err);errorFlag=true;}
		}

		// Checking for errors in method bodies
		for(String cln: allClsNames){
			if((Arrays.asList("IO","Object")).contains(cln)) continue;
			AST.class_ cl = classMap.get(cln);
			String err = "";
			scopeTable.insert(cl.name, (AST.ASTNode) cl);
			scopeTable.enterScope();
			scopeTable.insert("self", (AST.ASTNode) cl);
			// Attributes and inherited attributes are added into scopetable before setting types
			for(AST.attr cms: cl.parAttrs) scopeTable.insert(cms.name, (AST.ASTNode) cms);
			for(AST.attr cms: cl.attrs) {scopeTable.insert(cms.name, (AST.ASTNode) cms);}
			// Methods are annotated 1st because attrs may use these functions as initialisation
			for(AST.method cms: cl.methods){
				scopeTable.enterScope();
				for(AST.formal f: cms.formals) scopeTable.insert(f.name, (AST.ASTNode) f);
				err += cms.body.setType(cl.filename,scopeTable, classMap, depths);

				// Body can return any child class of declared return type
				//Return error only if declared return type and inferred return type are of defined classes because in other cases they will be reported at their origins already
				if(!cms.body.type.equals("_no_type") && ((classMap.containsKey(cms.body.type) && classMap.containsKey(cms.typeid) && !AST.expression.isAncestor(cms.body.type, cms.typeid, classMap)) || (classMap.containsKey(cms.body.type) && cms.typeid.equals("SELF_TYPE") && !AST.expression.isAncestor(cms.body.type, cms.typeid, classMap)) || (cms.body.type.equals("SELF_TYPE") && !AST.expression.isAncestor(cl.name, cms.typeid, classMap)))) err += (cl.filename + ":" + cms.lineNo + ": In the definition of " + cms.name + " inferred return type "+cms.body.type+" does not conform to the declared return type "+cms.typeid+"\n");
				scopeTable.exitScope();
			}
			// Checking for methods with inheritance errors also
			for(AST.method cms: cl.delMethds){
				scopeTable.enterScope();
				for(AST.formal f: cms.formals) scopeTable.insert(f.name, (AST.ASTNode) f);
				err += cms.body.setType(cl.filename,scopeTable, classMap, depths);
				if(!cms.body.type.equals("_no_type") && ((classMap.containsKey(cms.body.type) && classMap.containsKey(cms.typeid) && !AST.expression.isAncestor(cms.body.type, cms.typeid, classMap)) || (classMap.containsKey(cms.body.type) && cms.typeid.equals("SELF_TYPE") && !AST.expression.isAncestor(cms.body.type, cms.typeid, classMap)) || (cms.body.type.equals("SELF_TYPE") && !AST.expression.isAncestor(cl.name, cms.typeid, classMap)))) err += (cl.filename + ":" + cms.lineNo + ": In the definition of " + cms.name + " inferred return type "+cms.body.type+" does not conform to the declared return type "+cms.typeid+"\n");
				scopeTable.exitScope();
			}
			// Checking for validity of initialisation of attrs
			for(AST.attr a: cl.attrs) {err += a.setType(cl.filename, scopeTable, classMap, depths);}
			if(!err.equals("")) {System.out.print(err);errorFlag=true;}
			scopeTable.exitScope();
		}
	}

	private boolean checkCycles(){
		Queue<String> bfsQueue = new LinkedList<String>();
		ArrayList<String> orderCls = new ArrayList<String>();
		HashMap<String, Boolean> isDone = new HashMap<String,Boolean>();// Track of visited nodes
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
			//System.out.println("x"+cl);
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
