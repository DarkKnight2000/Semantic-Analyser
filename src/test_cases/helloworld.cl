class Main {
	i:String <- new String;
	i1:Bool <- false;
	i2:Maisn1 <- new Maisn1;
	i23:Maisn3 <- new Maisn3;
	i7:Int <- new Int;
	i8:IO <- new IO;
	main():IO {
		--i8.out_string(i)
		--{new Bool;new Bool;}
		--while true loop new Bool pool
		--if i1 then new Int else new Int fi
		i23.out_strings3(i2)
		--i22@Maisn1.out_strings(i7)
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
	i21:Maisn1 <- new Maisn1;
	out_strings3(i:IO):IO {
		new IO
	};
};

class Maisn2 inherits Maisn1{
	--i:String <- new String;
	out_strings(i:String,i1:String):IO {
		new IO
	};
};