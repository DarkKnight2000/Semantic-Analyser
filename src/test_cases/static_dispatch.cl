class Main {
	i:String <- new String;
	i1:Bool <- false;
	i2:Maisn1 <- new Maisn1;
	i22:Maisn2 <- new Maisn2;
	i7:Int <- new Int;
	i8:IO <- new IO;
	main():IO {
		i22@Maisn1.out_strings(i7,i7)
	};
};

class Maisn1 inherits IO{
	i:String <- new String;
	out_strings(i:String,i1:String):IO {
		new IO
	};
};

class Maisn3 inherits IO{
	i:String <- new String;
	out_strings(i:String):IO {
		new IO
	};
};

class Maisn2{
	--i:String <- new String;
	out_strings(i:String,i1:String):IO {
		new IO
	};
};