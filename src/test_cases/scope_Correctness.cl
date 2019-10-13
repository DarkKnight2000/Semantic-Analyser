class Main {
	--i:String <- new String;
	i1:Bool <- false;
	i2:Maisn1 <- new Maisn1;
	i22:Maisn2 <- new Maisn2;
	i23:Maisn3 <- new Maisn3;
	i7:Int <- new Int;
	i8:IO <- new IO;
	main():IO {
		i8.out_string(i)
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