class Main {
	i:String <- new String;
	i1:Bool <- false;
	i2:Maisn1 <- new Maisn1;
	i22:Maisn2 <- new Maisn2;
	i23:Maisn3 <- new Maisn3;
	i7:Int <- new Int;
	i8:IO <- new IO;
	main():Maisn1 {
		--i1.out_string("Hello world!\n")
		--{new Bool;new Bool;}
		--while true loop new Bool pool
		--if i1 then new Int else new Int fi
		--let i5:Int <- 0 in i8.out_int(i5)
		--i22@Maisn1.out_strings(i7)
		case new Bool of
			x:Int => i2.out_strings1(x);
			x:String => i23.out_strings3(x);
		esac
	};
};

class Maisn1 inherits IO{
	i:String <- new String;

	out_strings1(i:Int):Maisn1 {
		new Maisn1
	};
};

class Maisn3 inherits IO{
	i:String <- new String;
	out_strings3(i:String):Maisn2 {
		new Maisn2
	};
};

class Maisn2 inherits Maisn1{
	--i:String <- new String;
	out_strings2(i:Int):Maisn2 {
		new Maisn2
	};
};