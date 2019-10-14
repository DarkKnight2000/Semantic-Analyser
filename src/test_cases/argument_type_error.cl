-- Method call using wrong argument types
-- That method is wrongly overriden in a child class,
-- checking which implementation is in play
-- Shouldnt give a error if child class override is considered

class Main {
	i:String <- new String;
	i1:Bool <- false;
	i2:Maisn1 <- new Maisn1;
	i22:Maisn12 <- new Maisn2;
	i7:Int <- new Int;
	i8:IO <- new IO;
	main():IO {
		i2.out_strings(i7,i)
	};
};

class Maisn1 inherits IO{
	i:String <- new String;
	out_strings(i:String1,i:String):IO {
		new IO
	};
};

class Maisn3 inherits IO{
	i:String <- new String;
	out_strings(i:String):IO {
		new Maisn3
	};
};

class Maisn2 inherits Maisn1{
	--i:String <- new String;
	out_strings(i:String,i1:String):IO {
		new IO
	};
};