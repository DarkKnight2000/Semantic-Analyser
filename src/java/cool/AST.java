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
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap){
			return ("****Not Implemented****\n");
		}
	}
	public static class no_expr extends expression {
		public no_expr(int l){
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_no_expr\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap){
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
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap){
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
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap){
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
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap){
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
		String setType(String sp, ScopeTable<AST.ASTNode> st, HashMap<String,class_> cMap){
			System.out.println("searching "+name);
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
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap){
			String err = e1.setType(sp, st, cMap);
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
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap){
			String err = e1.setType(sp, st, cMap);
			err += e2.setType(sp, st, cMap);
			if(!e1.type.equals(e2.type)) err += sp+":"+lineNo+": Illegal comparision between types "+e1.type+" and "+e2.type+"\n";
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
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap){
			String err = e1.setType(sp, st, cMap);
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
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap){
			String err = e1.setType(sp, st, cMap);
			err += e2.setType(sp, st, cMap);
			type = "Int";
			if(!(e1.type.equals("Int") && e2.type.equals("Int"))) err += sp+":"+lineNo+": Arguments of '/' operation have types "+e1.type+" ,"+e2.type+"\n";
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
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap){
			String err = e1.setType(sp, st, cMap);
			err += e2.setType(sp, st, cMap);
			type = "Int";
			if(!(e1.type.equals("Int") && e2.type.equals("Int"))) err += sp+":"+lineNo+": Arguments of '*' operation have types "+e1.type+" ,"+e2.type+"\n";
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
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap){
			String err = e1.setType(sp, st, cMap);
			err += e2.setType(sp, st, cMap);
			type = "Int";
			if(!(e1.type.equals("Int") && e2.type.equals("Int"))) err += sp+":"+lineNo+": Arguments of '-' operation have types "+e1.type+" ,"+e2.type+"\n";
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
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap){
			String err = e1.setType(sp, st, cMap);
			err += e2.setType(sp, st, cMap);
			type = "Int";
			if(!(e1.type.equals("Int") && e2.type.equals("Int"))) err += sp+":"+lineNo+": Arguments of '+' operation have types "+e1.type+" ,"+e2.type+"\n";
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
	}
	public static class new_ extends expression{
		public String typeid;
		public new_(String t, int l){
			typeid = t;
			lineNo = l;
		}
		String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_new\n"+space+sp+typeid+"\n"+space+": bb "+type;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap){
			type = typeid;
			return "";
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
			return space+"#"+lineNo+"\n"+space+"_assign\n"+space+sp+name+"aaa\n"+e1.getString(space+sp)+"\n"+space+": "+type;
		}
		String setType(String sp, ScopeTable<AST.ASTNode> st, HashMap<String,class_> cMap){
			String err = e1.setType(sp,st,cMap);
			//st.lookUpGlobal(name);
			//TODO: Should we change the value of variable in scopetable?????
			type = e1.type;
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
		String setType(String sp, ScopeTable<AST.ASTNode> st, HashMap<String,class_> cMap){
			String err = "";
			for(expression e: l1){
				err += e.setType(sp, st, cMap);
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
		String setType(String sp, ScopeTable<AST.ASTNode> st, HashMap<String,class_> cMap){
			String err = "";
			err += predicate.setType(sp, st, cMap);
			if(!predicate.type.equals("Bool")) err += sp+":"+lineNo+": Predicate of while loop doesn't have static type Bool\n";
			err += body.setType(sp, st, cMap);
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
		String setType(String sp, ScopeTable<AST.ASTNode> st, HashMap<String,class_> cMap){
			String err = "";
			err += predicate.setType(sp, st, cMap);
			if(!predicate.type.equals("Bool")) err += sp+":"+lineNo+": Predicate of if conditional doesn't have static type Bool\n";
			err += ifbody.setType(sp, st, cMap);
			err += elsebody.setType(sp, st, cMap);
			//FIXME: assign type based on body types
			type = "Object";
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
		String setType(String sp, ScopeTable<AST.ASTNode> st, HashMap<String,class_> cMap){
			String err = "";
			object o = new object(name, lineNo);
			st.enterScope();
			st.insert(name,o);
			//o.setType(sp, st, cMap);
			o.type = typeid;
			err += value.setType(sp, st, cMap);
			if(!typeid.equals(value.type)) err += sp+":"+lineNo+": Invalid initialization of identifier "+name+" of type "+typeid+" with "+value.type+" type\n";
			err += body.setType(sp, st, cMap);
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
			str = space+"#"+lineNo+"\n"+space+"_dispatch\n"+caller.getString(space+sp)+"\nyyy\n"+space+sp+name+"\n"+space+sp+"(\n";
			for ( expression e1 : actuals ) {
				str += e1.getString(space+sp)+"\nzxc\n";	
			}
			str+=space+sp+")\n"+space+": "+type;
			return str;
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap){
			String err = "";
			err = err.concat(caller.setType(sp,st,cMap));
			for ( expression e1 : actuals ) err = err.concat(e1.setType(sp,st,cMap));	
			for ( expression e1 : actuals ) if(e1.type == "_no_type") e1.type = "Object";
			//System.out.println("claeer type: "+caller.type);
			class_ callerClass = cMap.get(caller.type);
			int found=0;
			if(callerClass != null){
				for(method m: callerClass.methods){
					if(m.name.equals(name)){
						found=1;
						for(int i=0;i<m.formals.size();i++){
							if(!m.formals.get(i).typeid.equals(actuals.get(i).type)) {
								err = err.concat(sp+lineNo+": In call of method "+m.name+" passed argument of type "+actuals.get(i).type+" does not conform to declared type "+m.formals.get(i).typeid +" of argument "+m.formals.get(i).name+"\n");
								found = 2;
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
				err = err.concat(sp+lineNo+": Calling Unknown function "+name+"\n");
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
	}
	public static class branch extends ASTNode {
		public String name;
		public String type;
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
			else return f1.lineNo+": Type "+f1.typeid+" of parameter "+f1.name+" is different from original type " +f2.typeid+"\n";
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
			else return pre+":"+a1.lineNo+": Attribute "+a1.name+" is an attribute of an inherited class\n";
		}
		public String getString(String space){
			return space+"#"+lineNo+"\n"+space+"_attr\n"+space+sp+name+"\n"+space+sp+typeid+"\n"+value.getString(space+sp);
		}
		String setType(String sp, ScopeTable<ASTNode> st, HashMap<String,class_> cMap){
			String err = value.setType(sp, st, cMap);
			if(!value.type.equals(typeid)) err += sp+":"+lineNo+": Intialization of attribute "+name+" doesnot match with declared type "+value.type+"\n";
			return err;
		}
	}
	public static class class_ extends ASTNode {
		public String name;
		public String filename;
		public String parent;
		public ArrayList<method> methods;
		public ArrayList<attr> attrs;
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

		public String addParFeats(List<method> parm, List<attr> para){
			String err = "";
			ArrayList<method> presms = new ArrayList<method>();
			ArrayList<attr> presas = new ArrayList<attr>();
			for(attr pms: para){
				System.out.println("yyy--"+pms.name);
				attr rem = new attr("", "", new expression(), 0);
				for(attr cms: attrs){
					String smerr = attr.getErr(cms, pms, filename);
					System.out.println("yy--"+cms.name);
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
			attrs.addAll(presas);		
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
						//What to do if signs dont match-----------------to be tested
						//methods.remove(cms);
						presms.add(pms);
						err = err.concat(smerr);
						rem = cms;
					}
				}
				if(rem.name.equals("")) presms.add(pms);
				methods.remove(rem);
			}
			methods.addAll(presms);
			return err;
		}

		String getString(String space){
			String str;
			str = space+"#"+lineNo+"\n"+space+"_class\n"+space+sp+name+"\n"+space+sp+parent+"\n"+space+sp+"\""+filename+"\""+"\n"+space+sp+"(\n";
			for ( method f : methods ) {
				str += f.getString(space+sp)+"\n";
			}
			for ( attr f : attrs ) {
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
