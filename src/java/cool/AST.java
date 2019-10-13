package cool;
import java.util.*;
public class AST{
	public static class ASTNode {
		int lineNo;
	}
	public static String sp = "  ";

        static String escapeSpecialCharacters(String text) {
                return
                        text
                                .replaceAll("\\\\", "\\\\\\\\")
                                .replaceAll("\n", "\\\\n")
                                .replaceAll("\t", "\\\\t")
                                .replaceAll("\b", "\\\\b")
                                .replaceAll("\f", "\\\\f")
                                .replaceAll("\"", "\\\\\"")
                                .replaceAll("\r", "\\\\015")
                                .replaceAll("\033","\\\\033")
                                .replaceAll("\001","\\\\001")
                                .replaceAll("\002","\\\\002")
                                .replaceAll("\003","\\\\003")
                                .replaceAll("\004","\\\\004")
                                .replaceAll("\022","\\\\022")
                                .replaceAll("\013","\\\\013")
                                .replaceAll("\000", "\\\\000")
                                ;
        }

	
	public static class expression extends ASTNode {
		String type;
		public expression(){
			type = "_no_type";
		}
		String getString(String space){
			return "";
		};
		/**
		 * 	Will be implemented in all child classes to set 'type' attribute accordingly
		**/
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			return ("****Not Implemented****\n");
		}

		/**
		 * 	Connection from objects to class is seen as singly linked list, move to equal distance from "Object" class,
		 * 	and parallely move both pointers towards Object until they are same
		**/
		public static String join(String a, String b, HashMap<String,class_> cMap, HashMap<String, Integer> dMap){
			//System.out.println(a+b);
			if(!cMap.containsKey(a)||!cMap.containsKey(b)) return "Object";
			int da = (dMap.get(a)).intValue(), db = dMap.get(b).intValue();
			if(da>=db){
				for(int i=0;i<da-db;++i) a = cMap.get(a).parent;
			}
			else{
				for(int i=0;i<db-da;++i) b = cMap.get(b).parent;
			}
			//System.out.println("--"+a+b);
			while(a!="Object" && b!= "Object" && !a.equals(b)){
				b = cMap.get(b).parent;
				a = cMap.get(a).parent;
			}
			return a;
		}

		/** 
		 * 	Checks if 2nd argument is a ancestor of 1st argument
		**/
		public static boolean isAncestor(String a, String b, HashMap<String, class_> cMap){
			if(b.equals("Object")) return true;
			if(!cMap.containsKey(a) || !cMap.containsKey(b)) return false;
			a = new String(a);
			while(!a.equals("Object")&&!a.equals(b)){a = cMap.get(a).parent;/*System.out.println("cls-"+a);*/}
			//System.out.println("x-"+a);
			if(a.equals("Object")) return false;
			return true;
		}  
	}
	public static class no_expr extends expression {
		public no_expr(int l){
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_no_expr\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			return "";
		}
	}
	public static class bool_const extends expression{
		public boolean value;
		public bool_const(boolean v, int l){
			value = v;
			lineNo = l;
			type = "Bool";
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_bool\n"+space+sp+(value?"1":"0")+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			return "";
		}
	}
	public static class string_const extends expression{
		public String value;
		public string_const(String v, int l){
			value = v;
			lineNo = l;
			type = "String";
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_string\n"+space+sp+"\""+escapeSpecialCharacters(value)+"\""+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			return "";
		}
	}

	public static class int_const extends expression{
		public int value;
		public int_const(int v, int l){
			value = v;
			lineNo = l;
			type = "Int";
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_int\n"+space+sp+value+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			return "";
		}
	}

	public static class object extends expression{
		public String name;
		public object(String v, int l){
			name = v;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_object\n"+space+sp+name+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			//System.out.println("searching "+name);
			/* We might check a object of following cases in scopetable */
			ASTNode o = st.lookUpGlobal(name);
			if(o==null) return sp+":"+lineNo+": Undeclared identifier "+name+"\n";
			else if(o instanceof attr)type = ((attr)o).typeid;
			else if(o instanceof formal)type = ((formal)o).typeid;
			else if(o instanceof object) type = ((object)o).type;
			else if(o instanceof class_) type= ((class_)o).name;
			//System.out.println("vvvv-- "+type);
			return "";
		}
	}
	public static class comp extends expression{
		public expression e1;
		public comp(expression v, int l){
			e1 = v;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_comp\n"+e1.getString(space+sp)+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = e1.setType(sp, st, cMap, dMap);
			type = e1.type;
			if(!e1.type.equals("Bool")) err += sp+":"+lineNo+": Argument of \'not\' function has type "+e1.type+" instead of Bool type\n";
			return err;
		}
	}
	public static class eq extends expression{
		public expression e1;
		public expression e2;
		public eq(expression v1, expression v2, int l){
			e1=v1;
			e2=v2;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_eq\n"+e1.getString(space+sp)+"\n"+e2.getString(space+sp)+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = e1.setType(sp, st, cMap, dMap);
			err += e2.setType(sp, st, cMap, dMap);
			if(!expression.isAncestor(e1.type, e2.type, cMap) && !expression.isAncestor(e2.type, e1.type, cMap)) err += sp+":"+lineNo+": Illegal comparision between types "+e1.type+" and "+e2.type+"\n";
			type = "Bool";
			return err;
		}
	}
	

	public static class leq extends expression{
		public expression e1;
		public expression e2;
		public leq(expression v1, expression v2, int l){
			e1 = v1;
			e2 = v2;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_leq\n"+e1.getString(space+sp)+"\n"+e2.getString(space+sp)+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = e1.setType(sp, st, cMap, dMap);
			err += e2.setType(sp, st, cMap, dMap);
			type = "Bool";
			if(!(e1.type.equals("Int") && e2.type.equals("Int"))) err += sp+":"+lineNo+": Arguments of '<=' operation cannot have types "+e1.type+" ,"+e2.type+"\n";
			return err;
		}
	}

	public static class lt extends expression{
		public expression e1;
		public expression e2;
		public lt(expression v1, expression v2, int l){
			e1 = v1;
			e2 = v2;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_lt\n"+e1.getString(space+sp)+"\n"+e2.getString(space+sp)+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = e1.setType(sp, st, cMap, dMap);
			err += e2.setType(sp, st, cMap, dMap);
			type = "Bool";
			if(!(e1.type.equals("Int") && e2.type.equals("Int"))) err += sp+":"+lineNo+": Arguments of '<' operation cannot have types "+e1.type+" ,"+e2.type+"\n";
			return err;
		}
	}
	public static class neg extends expression{
		public expression e1;
		public neg(expression v, int l){
			e1 = v;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_neg\n"+e1.getString(space+sp)+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = e1.setType(sp, st, cMap, dMap);
			type = "Int";
			if(!e1.type.equals("Int")) err += sp+":"+lineNo+": Argument of \'~\' function has type "+e1.type+" instead of Int type\n";
			return err;
		}
	}
	public static class divide extends expression{
		public expression e1;
		public expression e2;
		public divide(expression v1, expression v2, int l){
			e1 = v1;
			e2 = v2;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_divide\n"+e1.getString(space+sp)+"\n"+e2.getString(space+sp)+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = e1.setType(sp, st, cMap, dMap);
			err += e2.setType(sp, st, cMap, dMap);
			type = "Int";
			if(!(e1.type.equals("Int") && e2.type.equals("Int"))) err += sp+":"+lineNo+": Arguments of '/' operation cannot have types "+e1.type+" ,"+e2.type+"\n";
			return err;
		}
	}
	public static class mul extends expression{
		public expression e1;
		public expression e2;
		public mul(expression v1, expression v2, int l){
			e1 = v1;
			e2 = v2;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_mul\n"+e1.getString(space+sp)+"\n"+e2.getString(space+sp)+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = e1.setType(sp, st, cMap, dMap);
			err += e2.setType(sp, st, cMap, dMap);
			type = "Int";
			if(!(e1.type.equals("Int") && e2.type.equals("Int"))) err += sp+":"+lineNo+": Arguments of '*' operation cannot have types "+e1.type+" ,"+e2.type+"\n";
			return err;
		}
	}
	public static class sub extends expression{
		public expression e1;
		public expression e2;
		public sub(expression v1, expression v2, int l){
			e1 = v1;
			e2 = v2;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_sub\n"+e1.getString(space+sp)+"\n"+e2.getString(space+sp)+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = e1.setType(sp, st, cMap, dMap);
			err += e2.setType(sp, st, cMap, dMap);
			type = "Int";
			if(!(e1.type.equals("Int") && e2.type.equals("Int"))) err += sp+":"+lineNo+": Arguments of '-' operation cannot have types "+e1.type+" ,"+e2.type+"\n";
			return err;
		}
	}
	public static class plus extends expression{
		public expression e1;
		public expression e2;
		public plus(expression v1, expression v2, int l){
			e1 = v1;
			e2 = v2;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_plus\n"+e1.getString(space+sp)+"\n"+e2.getString(space+sp)+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = e1.setType(sp, st, cMap, dMap);
			err += e2.setType(sp, st, cMap, dMap);
			type = "Int";
			if(!(e1.type.equals("Int") && e2.type.equals("Int"))) err += sp+":"+lineNo+": Arguments of '+' operation cannot have types "+e1.type+" ,"+e2.type+"\n";
			return err;
		}
	}
	public static class isvoid extends expression{
		public expression e1;
		public isvoid(expression v, int l){
			e1 = v;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_isvoid\n"+e1.getString(space+sp)+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = e1.setType(sp, st, cMap, dMap);
			type = "Bool";
			return err;
		}
	}
	public static class new_ extends expression{
		public String typeid;
		public new_(String t, int l){
			typeid = t;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_new\n"+space+sp+typeid+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = "";
			if(!cMap.containsKey(typeid)){ err += sp+":"+lineNo+": Unknown type "+typeid+" for new\n";type="Object";}
			else type = typeid;
			return err;
		}
	}
	public static class assign extends expression{
		public String name;
		public expression e1;
		public assign(String n, expression v1, int l){
			name = n;
			e1 = v1;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_assign\n"+space+sp+name+"\n"+e1.getString(space+sp)+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = e1.setType(sp,st,cMap, dMap);
			object o = new object(name, lineNo);
			err += o.setType(sp, st, cMap, dMap);
			type = e1.type;
			/* We can assign a object to any of its ancestor class object */
			if(!expression.isAncestor(type, o.type, cMap)) err += sp+":"+lineNo+": Illegal assignment of type "+type+" to variable of type "+o.type+"\n";
			return err;
		}
	}
	public static class block extends expression{
		public List<expression> l1;
		public block(List<expression> v1, int l){
			l1 = v1;
			lineNo = l;
		}
		String getString(String space){
			String str = space+"#"+lineNo+"\n"+space+"_block\n";
			for (expression e1 : l1){
				str += e1.getString(space+sp)+"\n";
			}
			str+=space+": "+type;
			return str;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = "";
			for(expression e: l1){
				err += e.setType(sp, st, cMap, dMap);
			}
			type = l1.get(l1.size()-1).type;
			return err;
		}
	}
	public static class loop extends expression{
		public expression predicate;
		public expression body;
		public loop(expression v1, expression v2, int l){
			predicate = v1;
			body = v2;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_loop\n"+predicate.getString(space+sp)+"\n"+body.getString(space+sp)+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = "";
			err += predicate.setType(sp, st, cMap, dMap);
			if(!predicate.type.equals("Bool")) err += sp+":"+lineNo+": Predicate of while loop doesn't have static type Bool\n";
			err += body.setType(sp, st, cMap, dMap);
			type = "Object";
			return err;
		}
	}
	public static class cond extends expression{
		public expression predicate;
		public expression ifbody;
		public expression elsebody;
		public cond(expression v1, expression v2, expression v3, int l){
			predicate = v1;
			ifbody = v2;
			elsebody = v3;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_cond\n"+predicate.getString(space+sp)+"\n"+ifbody.getString(space+sp)+"\n"+elsebody.getString(space+sp)+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = "";
			err += predicate.setType(sp, st, cMap, dMap);
			if(!predicate.type.equals("Bool")) err += sp+":"+lineNo+": Predicate of if conditional doesn't have static type Bool\n";
			err += ifbody.setType(sp, st, cMap, dMap);
			err += elsebody.setType(sp, st, cMap, dMap);
			type = expression.join(ifbody.type, elsebody.type,cMap,dMap);
			return err;
		}
	}
	public static class let extends expression{
		public String name;
		public String typeid;
		public expression value;
		public expression body;
		public let(String n, String t, expression v, expression b, int l){
			name = n;
			typeid = t;
			value = v;
			body = b;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_let\n"+space+sp+name+"\n"+space+sp+typeid+"\nval\n"+value.getString(space+sp)+"\nbody\n"+body.getString(space+sp)+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = "";
			object o = new object(name, lineNo);
			st.enterScope();
			st.insert(name,o);
			if(!cMap.containsKey(typeid)){ err += sp+":"+lineNo+": Unknown type "+typeid+" for 'let' variable\n";o.type="Object";}
			else{
				o.type = typeid;
				err += value.setType(sp, st, cMap, dMap);
				if(!expression.isAncestor(value.type, typeid, cMap) && !value.type.equals("_no_type")) err += sp+":"+lineNo+": Invalid initialization of identifier "+name+" of type "+typeid+" with "+value.type+" type\n";
			}
			err += body.setType(sp, st, cMap, dMap);
			st.exitScope();
			type = body.type;
			return err;
		}
	}
	public static class dispatch extends expression{
		public expression caller;
		public String name;
		public List<expression> actuals;
		public dispatch(expression v1, String n, List<expression> a, int l){
			caller = v1;
			name = n;
			actuals = a;
			lineNo = l;
		} 
		String getString(String space){
			String str;
			str = space+"#"+lineNo+"\n"+space+"_dispatch\n"+caller.getString(space+sp)+"\n"+space+sp+name+"\n"+space+sp+"(\n";
			for ( expression e1 : actuals ) {
				str += e1.getString(space+sp)+"\n";	
			}
			str+=space+sp+")\n"+space+": "+type;
			return str;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = "";
			err = err.concat(caller.setType(sp,st,cMap, dMap));
			for ( expression e1 : actuals ) err = err.concat(e1.setType(sp,st,cMap, dMap));	
			for ( expression e1 : actuals ) if(e1.type == "_no_type") e1.type = "Object";
			/* Any errors while assigning type to actuals and caller are returned here */
			if(!err.equals("")) return err;
			class_ callerClass = cMap.get(caller.type);
			int found=0;
			if(callerClass != null){
				ArrayList<method> arr1=new ArrayList<method>();
				arr1.addAll(callerClass.methods);
				if(callerClass.parMethods != null) arr1.addAll(callerClass.parMethods);
				/* Checking for the method in both caller class's methods and inherited methods */
				for(method m: arr1){
					if(m.name.equals(name)){
						found=1;
						if(m.formals.size() != actuals.size()) {err += sp+":"+lineNo+": Dispatch with wrong number of arguments\n";found=2;}
						else {
							for(int i=0;i<m.formals.size();i++){
								/* Checking for errors during function call, ignoring if formal has unknown type error */
								if(!expression.isAncestor(actuals.get(i).type, m.formals.get(i).typeid, cMap) && cMap.containsKey(actuals.get(i).type) && cMap.containsKey(m.formals.get(i).typeid)) {
									err = err.concat(sp+":"+lineNo+": In call of method "+m.name+" passed argument of type "+actuals.get(i).type+" does not conform to declared type "+m.formals.get(i).typeid +" of argument "+m.formals.get(i).name+"\n");
									found = 2;
								}
							}
						}
						/* If there is no error in calling function */
						if(found==1){
							if(m.typeid == "SELF_TYPE") type = caller.type;
							else type = m.typeid;
							return "";
						}
						break;
					}
				}
			}
			/* Method not found */
			if(found==0) {
				err = err.concat(sp+":"+lineNo+": Calling Unknown function "+name+" through dispatch\n");
				type = "Object";
			}

			return err;
		}
	}
	public static class static_dispatch extends expression{
		public expression caller;
		public String typeid;
		public String name;
		public List<expression> actuals;
		public static_dispatch(expression v1, String t, String n, List<expression> a, int l){
			caller = v1;
			typeid = t;
			name = n;
			actuals = a;
			lineNo = l;
		}
		String getString(String space){
			String str;
			str = space+"#"+lineNo+"\n"+space+"_static_dispatch\n"+caller.getString(space+sp)+"\n"+space+sp+typeid+"\n"+space+sp+name+"\n"+space+sp+"(\n";
			for ( expression e1 : actuals ) {
					str += e1.getString(space+sp)+"\n";     
			}
			str+=space+sp+")\n"+space+": "+type;
			return str;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = "";
			err += (caller.setType(sp,st,cMap, dMap));
			for ( expression e1 : actuals ) err += (e1.setType(sp,st,cMap, dMap));	
			for ( expression e1 : actuals ) if(e1.type == "_no_type") e1.type = "Object";
			class_ callerClass = cMap.get(typeid);
			String calName = caller.type;
			/* Checking if typeid exists */
			if(!cMap.containsKey(typeid)) {err += sp+":"+lineNo+": Static dispatch to undefined class "+typeid+"\n";type="Object";return err;}
			/* Checking if typeid is related to caller class */
			else if(!expression.isAncestor(calName, typeid, cMap)){
				err += (sp+":"+lineNo+": Static dispatch to unrelated class "+callerClass.name+"\n");
			}
			int found=0;
			if(callerClass != null){
				List<method> arr1=new ArrayList<method>();
				arr1.addAll(callerClass.methods);
				if(callerClass.parMethods != null) arr1.addAll(callerClass.parMethods);
				/* Checking for the method in both typeid class's methods and inherited methods */
				for(method m: arr1){
					if(m.name.equals(name)){
						found=1;
						if(m.formals.size() != actuals.size()) {err += sp+":"+lineNo+": Static dispatch with wrong number of arguments\n";found=2;}
						else {
							for(int i=0;i<m.formals.size();i++){
								/* Checking for errors during function call, ignoring if formal has unknown type error */
								if(!expression.isAncestor(actuals.get(i).type, m.formals.get(i).typeid, cMap) && cMap.containsKey(actuals.get(i).type) && cMap.containsKey(m.formals.get(i).typeid)) {
									err += (sp+":"+lineNo+": In call of method "+m.name+" passed argument of type "+actuals.get(i).type+" does not conform to declared type "+m.formals.get(i).typeid +" of argument "+m.formals.get(i).name+"\n");
									found = 2;
								}
							}
						}
						/* If there is no error in calling function */
						if(found==1){
							if(m.typeid == "SELF_TYPE") type = typeid;
							else if(cMap.containsKey(m.typeid)) type = m.typeid;
							return err;
						}
						break;
					}
				}
			}
			/* Method not found */
			if(found==0) {
				err = err.concat(sp+":"+lineNo+": Calling Unknown function "+name+" through static dispatch\n");
				type = "Object";
			}
			return err;
		}
    }
	public static class typcase extends expression{
		public expression predicate;
		public List<branch> branches;
		public typcase(expression p, List<branch> b, int l){
			predicate = p;
			branches = b;
			lineNo = l;
		}
		String getString(String space){
			String str = space+"#"+lineNo+"\n"+space+"_typcase\n"+predicate.getString(space+sp)+"\n";
			for ( branch b1 : branches ) {
				str += b1.getString(space+sp)+"\n";
			}
			str += space+": "+type;
			return str;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = "";
			for(branch b: branches) err += b.setType(sp, st, cMap, dMap);
			type = branches.get(0).typeid;
			for(branch b: branches) type = expression.join(type, b.typeid, cMap, dMap);
			return err;
		}
	}
	public static class branch extends ASTNode {
		public String name;
		public String type;
		public String typeid;
		public expression value;
		public branch(String n, String t, expression v, int l){
			name = n;
			type = t;
			value = v;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_branch\n"+space+sp+name+"\n"+space+sp+type+"\n"+value.getString(space+sp);
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = "";
			st.enterScope();
			object o =new object(name, lineNo);
			if(!cMap.containsKey(type)) {err += sp+":"+lineNo+": Unknown type "+typeid+" for 'branch' variable\n";type="Object";}
			else o.type = type;
			st.insert(name, (ASTNode) o);
			err += value.setType(sp, st, cMap, dMap);
			typeid = value.type;
			st.exitScope();
			return err;
		}
	}
	public static class formal extends ASTNode {
		public String name;
		public String typeid;
		public formal(String n, String t, int l){
			name = n;
			typeid = t;
			lineNo = l;
		}

		/* Compares two formals for error
		*	Return "\n" if typeid match
		* 	Returns error message otherwise
		*/
		public static String getErr(formal f1, formal f2){
			if(f1.typeid.equals(f2.typeid))return "";
			else return f1.lineNo+": Type "+f1.typeid+" of parameter "+f1.name+" is different from the type " +f2.typeid+" declared in a ancestor\n";
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_formal\n"+space+sp+name+"\n"+space+sp+typeid;
		}
	}
	public static class feature extends ASTNode {
		// Updated to contain name and typeid fields
		// typeid of a class is equal to its name
		public String name;
		public String typeid;
		public feature(){
		}
		String getString(String space){
			return "";
		}

	}
	public static class method extends feature {
		public List<formal> formals;
		public expression body;
		public method(String n, List<formal> f, String t, expression b, int l){
			name = n;
			formals = f;
			typeid = t;
			body = b;
			lineNo = l;
		}

		/* Returns errors in declaring undefined return types and formal types, duplicate formal names */
		public String getErrDecl(String sp, HashMap<String,class_> cMap){
			ArrayList<String> fname = new ArrayList<String>();
			String err = "";
			if(!typeid.equals("SELF_TYPE") && !cMap.containsKey(typeid)) err += sp+":"+lineNo+": Unknown return type for "+typeid+" method "+name+"\n";
			for(formal f: formals){
				if(!cMap.containsKey(f.typeid)){ err += sp+":"+lineNo+": Unknown type "+f.typeid+" for formal variable "+f.name+"\n";}
				if(fname.contains(f.name)) err += sp+":"+lineNo+": Formal name "+f.name+" is already used\n";
				fname.add(f.name);
				//System.out.println("-"+f.name);
			}
			return err;
		}

		/* Compares two functions
		*	Names are not equal then they are not related
		*	Names are equal but unequal no. of formals or corresponding formal types not matching shld return error
		* 	If everything matches return "\n"
		*/
		public static String getErr(method m1, method m2, String pre){
			if(!m1.name.equals(m2.name)) return "";
			else if(!m1.typeid.equals(m2.typeid)) return pre+":"+m1.lineNo+": In redefintion of method "+m1.name+" return types do not match\n";
			else if(m1.formals.size() != m2.formals.size()) return pre+":"+m1.lineNo+": Different number of parameters in redefinition of inherited function\n";
			else{
				String err = "";
				for (int i = 0; i < m1.formals.size(); i++) {
					err = err.concat(formal.getErr(m1.formals.get(i), m2.formals.get(i)));
					if(!err.equals("")) return pre+":"+err;
				}
				return "\n";
			}
		}

		String getString(String space){
			String str = space+"#"+lineNo+"\n"+space+"_method\n"+space+sp+name+"\n";
			for ( formal f : formals ) {
				str += f.getString(space+sp)+"\n";
			}
			str += space+sp+typeid+"\n"+body.getString(space+sp)+"\n";
			return str;
		}
	}
	public static class attr extends feature {
		public expression value;
		public attr(String n, String t, expression v, int l){
			name = n;
			typeid = t;
			value = v;
			lineNo = l;
		}

		/* 	Is called only between two related classes
		*	If names dont match then they are not related
		*	but if they match it is a redefinition so return error		
		*/
		public static String getErr(attr a1, attr a2, String pre){
			if(!a1.name.equals(a2.name)) return "";
			else return pre+":"+a1.lineNo+": Attribute "+a1.name+" is already an attribute of an inherited class\n";
		}
		public String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_attr\n"+space+sp+name+"\n"+space+sp+typeid+"\n"+value.getString(space+sp);
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			String err = value.setType(sp, st, cMap, dMap);
			//System.out.println(value.type+"xxx"+typeid+"\n");
			if(cMap.containsKey(typeid) && !expression.isAncestor(value.type, typeid, cMap) && !value.type.equals("_no_type")) err += sp+":"+lineNo+": Invalid intialization of attribute "+name+" of type "+typeid+" with type "+value.type+"\n";
			return err;
		}
	}
	public static class class_ extends ASTNode {
		public String name;
		public String filename;
		public String parent;
		//delMthds is for methods with error, they are stored to check for errors in their body too
		public ArrayList<method> methods, parMethods, delMethds;
		public ArrayList<attr> attrs, parAttrs;
		//public List<feature> features;

		/* Is updated to save attributes and methods separately */
		public class_(String n, String f, String p, List<feature> fs, int l){
			name = n;
			filename = f;
			parent = p;
			//features = fs;
			attrs = new ArrayList<attr>();
			/* Inherited attributes */
			parAttrs = new ArrayList<attr>();
			methods = new ArrayList<method>();
			/* Inherited methods */
			parMethods = new ArrayList<method>();
			for(feature f1: fs){
				if(f1 instanceof method) methods.add((method) f1);
				else if(f1 instanceof attr) attrs.add((attr) f1);
			}
			lineNo = l;
		}

		/* Takes inherited features as arguments and checks for inheritance errors
		*/
		public String addParFeats(List<method> parm, List<attr> para, HashMap<String,class_> cMap, ScopeTable<ASTNode> scopeTable, HashMap<String,Integer> depths){
			String err = "";
			ArrayList<method> delBufferM = new ArrayList<method>();
			ArrayList<attr> delBuffA = new ArrayList<attr>();
			ArrayList<String> fname = new ArrayList<String>();
			for(attr cms: attrs){
				err += cms.setType(filename, scopeTable, cMap, depths);
				boolean found = false;
				for(attr pms: para){
					String smerr = attr.getErr(cms, pms, filename);
					if(!smerr.equals("")){
						// Attribute defined in an ancestor
						err = err.concat(smerr);
						found = true;
					}
				}
				if(!found){
					// Newly defined
					if(!cMap.containsKey(cms.typeid)){
						err += filename+":"+cms.lineNo+": Unknown type "+cms.typeid+" for attribute "+cms.name+"\n";
						fname.add(cms.name);
					}
					else if(fname.contains(cms.name)){
						err += filename+":"+cms.lineNo+": Class already contains definition of attribute with name "+cms.name+" \n";
						//attrs.remove(m);
						delBuffA.add(cms);
					}
					else{
						// Keeping track of all new attributes to check for duplicate definitions in same class
						fname.add(cms.name);
					}
				}
				else{
					// Deleting attributes with error
					// Attributes with undefined types are deleted for error handling
					delBuffA.add(cms);
				}
			}	 
			parAttrs.addAll((ArrayList<attr>) para);
			attrs.removeAll(delBuffA);
			fname.clear();	
			// Checking for inheritance and declaration errors in methods
			for(method cms: methods){
				if(fname.contains(cms.name)){
					err = err.concat(filename+":"+cms.lineNo+": Class already contains definition of method with name "+cms.name+" \n");
					err += cms.getErrDecl(filename, cMap);
					delBufferM.add(cms);// Has error in it so it is ready to be deleted
				}
				else{
					err += cms.getErrDecl(filename, cMap);
					fname.add(cms.name);
				}
				// Checking only for inheritance errors
				for(method pms: parm){
					String smerr = method.getErr(cms, pms, filename);
					if(smerr.equals("\n")){
						// Correctly overrides a inherited method
					}
					else if(!smerr.equals("")){
						// Error in overriding
						delBufferM.add(cms);
						err = err.concat(smerr);
						parMethods.add(pms); // Actual parent's method is added
					}
				}
			}
			methods.removeAll(delBufferM);
			delMethds = delBufferM; // All methods with errors are added here to check for errors in body later
			return err;
		}

		/* Has been updated to print attributes and methods */
		String getString(String space){
			String str;
			str = space+"#"+lineNo+"\n"+space+"_class\n"+space+sp+name+"\n"+space+sp+parent+"\n"+space+sp+"\""+filename+"\""+"\n"+space+sp+"(\n";
			for ( attr f : attrs ) {
				str += f.getString(space+sp)+"\n";
			}
			for ( method f : methods ) {
				str += f.getString(space+sp)+"\n";
			}
			str += space+sp+")";
			return str;
		}
	}
	public static class program extends ASTNode {
		public List<class_> classes;
		public program(List<class_> c, int l){
			classes = c;
			lineNo = l;
		}
		String getString(String space){
			String str;
			str = space+"#"+lineNo+"\n"+space+"_program";
			for ( class_ c : classes ) {
				str += "\n"+c.getString(space+sp);
			}
			
			return str;
		}
	}
}
