class Main {
	i:String <- new String;
	i1:Bool <- false;
	i2:Maisn1 <- new Maisn1;
	i22:Maisn12 <- new Maisn2;
	i7:Int <- new Int;
	i8:IO <- new IO;
	main():IO {
		i2.out_strings(i22,i)
	};
};

class Maisn1 inherits IO{
	i:String <- new String;
	out_strings(i:String,i:String):Int {
		new Int
	};
};

class Maisn3 inherits Maisn2{
    out_strings(i:String,i1:String):Int {
		new Int
	};
};

class Maisn2 inherits Maisn1{
	--i:String <- new String;
	out_strings(i:String,i1:String):Int {
		new Int
	};
};