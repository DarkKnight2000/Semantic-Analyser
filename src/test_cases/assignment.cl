class Main {
	i:String <- new String;
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
	i21:Maisn1 <- new Maisn1;
	out_strings3(i:String):IO {
		i21 <- new IO
	};
};