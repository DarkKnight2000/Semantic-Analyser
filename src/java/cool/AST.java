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
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap, HashMap<String,Integer> dMap){
			return ("****Not Implemented****\n");
		}
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
			//System.out.println("join-",expression.join(""));
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
			//o.setType(sp, st, cMap);
			if(!cMap.containsKey(typeid)){ err += sp+":"+lineNo+": Unknown type "+typeid+" for 'let' variable\n";o.type="Object";}
			else{
				o.type = typeid;
				err += value.setType(sp, st, cMap, dMap);
				if(!expression.isAncestor(value.type, typeid, cMap) && !value.type.equals("_no_type")) err += sp+":"+lineNo+": Invalid initialization of identifier "+name+" of type "+typeid+" with "+value.type+" type\n";
			}
			err += body.setType(sp, st, cMap, dMap);
			//System.out.println("xxx  "+st.lookUpGlobal(name));
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
			//System.out.println("claeer type: "+caller.type);
			if(!err.equals("")) return err;
			class_ callerClass = cMap.get(caller.type);
			int found=0;
			if(callerClass != null){
				List<method> arr1=new ArrayList<method>();
				arr1.addAll(callerClass.methods);
				if(callerClass.parMethods != null) arr1.addAll(callerClass.parMethods);
				for(method m: arr1){
					if(m.name.equals(name)){
						found=1;
						//if(!m.getErrDecl(sp, cMap).equals("")) return err;
						if(m.formals.size() != actuals.size()) {err += sp+lineNo+": Dispatch with wrong number of arguments\n";found=2;}
						else {
							for(int i=0;i<m.formals.size();i++){
								if(!m.formals.get(i).typeid.equals(actuals.get(i).type) && cMap.containsKey(m.formals.get(i).typeid) && cMap.containsKey(actuals.get(i).type)) {
									err = err.concat(sp+":"+lineNo+": In call of method "+m.name+" passed argument of type "+actuals.get(i).type+" does not conform to declared type "+m.formals.get(i).typeid +" of argument "+m.formals.get(i).name+"\n");
									found = 2;
								}
							}
						}
						if(found==1){
							if(m.typeid == "SELF_TYPE") type = caller.type;
							else type = m.typeid;
							return "";
						}
						break;
					}
				}
			}
			if(found==0) {
				err = err.concat(sp+lineNo+": Calling Unknown function "+name+" through dispatch\n");
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
			//System.out.println("claeer type: "+caller.type);
			class_ callerClass = cMap.get(typeid);
			String calName = caller.type;
			if(!cMap.containsKey(typeid)) {err += sp+":"+lineNo+": Static dispatch to undefined class "+typeid+"\n";type="Object";return err;}
			else if(!expression.isAncestor(calName, typeid, cMap)){
				err += (sp+":"+lineNo+": Static dispatch to unrelated class "+callerClass.name+"\n");
			}
			int found=0;
			if(callerClass != null){
				List<method> arr1=new ArrayList<method>();
				arr1.addAll(callerClass.methods);
				if(callerClass.parMethods != null) arr1.addAll(callerClass.parMethods);
				for(method m: arr1){
					if(m.name.equals(name)){
						//if(!m.getErrDecl(sp, cMap).equals("")) return err;
						found=1;
						if(m.formals.size() != actuals.size()) {err += sp+lineNo+": Static dispatch with wrong number of arguments\n";found=2;}
						else {
							for(int i=0;i<m.formals.size();i++){
								if(!m.formals.get(i).typeid.equals(actuals.get(i).type) && cMap.containsKey(actuals.get(i).type) && cMap.containsKey(m.formals.get(i).typeid)) {
									err += (sp+":"+lineNo+": In call of method "+m.name+" passed argument of type "+actuals.get(i).type+" does not conform to declared type "+m.formals.get(i).typeid +" of argument "+m.formals.get(i).name+"\n");
									found = 2;
								}
							}
						}
						if(found==1){
							if(m.typeid == "SELF_TYPE") type = caller.type;
							else type = m.typeid;
							return err;
						}
						break;
					}
				}
			}
			if(found==0) {
				err = err.concat(sp+lineNo+": Calling Unknown function "+name+" through static dispatch\n");
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
		public static String getErr(formal f1, formal f2){
			if(f1.typeid.equals(f2.typeid))return "";
			else return f1.lineNo+": Type "+f1.typeid+" of parameter "+f1.name+" is different from the type " +f2.typeid+" declared in a ancestor\n";
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_formal\n"+space+sp+name+"\n"+space+sp+typeid;
		}
	}
	public static class feature extends ASTNode {
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
		public String getErrDecl(String sp, HashMap<String,class_> cMap){
			ArrayList<String> fname = new ArrayList<String>();
			String err = "";
				if(!cMap.containsKey(typeid)) err += sp+":"+lineNo+": Unknown return type for "+typeid+" method "+name+"\n";
			for(formal f: formals){
				if(!cMap.containsKey(f.typeid)){ err += sp+":"+lineNo+": Unknown type "+f.typeid+" for formal variable "+f.name+"\n"; }
				if(fname.contains(f.name)) err += sp+":"+lineNo+": Formal name "+f.name+" is already used\n";
				fname.add(f.name);
			}
			return err;
		}
		public static String getErr(method m1, method m2, String pre){
			if(!m1.name.equals(m2.name)) return "";
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
		public ArrayList<method> methods, parMethods;
		public ArrayList<attr> attrs, parAttrs;
		//public List<feature> features;
		public class_(String n, String f, String p, List<feature> fs, int l){
			name = n;
			filename = f;
			parent = p;
			//features = fs;
			attrs = new ArrayList<attr>();
			methods = new ArrayList<method>();
			for(feature f1: fs){
				if(f1 instanceof method) methods.add((method) f1);
				else if(f1 instanceof attr) attrs.add((attr) f1);
			}
			lineNo = l;
		}
		public String getErrDecl(HashMap<String,class_> cMap){
			ArrayList<String> fname = new ArrayList<String>();
			ArrayList<method> delBuff = new ArrayList<method>();
			String err = "";
			for(method m: methods){
				//System.out.println("asd"+err+"\n");
				if(fname.contains(m.name)){
					err = err.concat(filename+":"+m.lineNo+": Class already contains definition of method with name "+m.name+" \n");
					//methods.remove(m);
					err += m.getErrDecl(filename, cMap);
					delBuff.add(m);
				}
				else{
					err += m.getErrDecl(filename, cMap);
					fname.add(m.name);
				}
			}
			methods.removeAll(delBuff);
			fname.clear();
			delBuff.clear();
			ArrayList<attr> delBuffA = new ArrayList<attr>();
			for(attr m: attrs){
				if(!cMap.containsKey(m.typeid)) err += filename+":"+m.lineNo+": Unknown type "+m.typeid+" for attribute "+m.name+"\n";
				else if(fname.contains(m.name)){
					err += filename+":"+m.lineNo+": Class already contains definition of attribute with name "+m.name+" \n";
					//attrs.remove(m);
					delBuffA.add(m);
				}
				else{
					//err += m.getErrDecl(name);
					fname.add(m.name);
				}
			}
			attrs.removeAll(delBuffA);
			return err;
		}

		public String addParFeats(List<method> parm, List<attr> para){
			String err = "";
			ArrayList<method> presms = new ArrayList<method>();
			ArrayList<attr> presas = new ArrayList<attr>();
			for(attr pms: para){
				//System.out.println("yyy--"+pms.name);
				attr rem = new attr("", "", new expression(), 0);
				for(attr cms: attrs){
					String smerr = attr.getErr(cms, pms, filename);
					//System.out.println("yy--"+cms.name);
					if(!smerr.equals("")){
						//attrs.remove(cms);
						//System.out.println(smerr);
						rem = cms;
						err = err.concat(smerr);
					}
				}
				attrs.remove(rem);
				presas.add(pms);
			}	
			//attrs.addAll(presas);	
			parAttrs = presas;	
			for(method pms: parm){
				method rem = new method("", new ArrayList<formal>(Arrays.asList(new formal("", "", 0))), "", new expression(), 0);
				for(method cms: methods){
					String smerr = method.getErr(cms, pms, filename);
					if(smerr.equals("\n")){
						//methods.remove(cms);
						rem = cms;
						presms.add(cms);
						break;
					}
					else if(!smerr.equals("")){
						//methods.remove(cms);
						presms.add(pms);
						err = err.concat(smerr);
						rem = cms;
					}
				}
				if(rem.name.equals("")) presms.add(pms);
				methods.remove(rem);
			}
			//methods.addAll(presms);
			parMethods = presms;
			return err;
		}

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
