-- The parameter types have to be 
-- exactly same type in child class they cannot be replaced by any child/parent class


class Main {
	i:String <- new String;
	i1:Bool <- false;
	i2:Maisn1 <- new Maisn1;
	i23:Maisn3 <- new Maisn3;
	i7:Int <- new Int;
	i8:IO <- new IO;
	main():IO {
		i23.out_strings3(i2)
	};
};

class Maisn1 inherits IO{
	i:String <- new String;
	out_strings1(i:Int):Maisn1 {
		new Maisn1
	};
	out_strings(i:Maisn1,i1:String):IO {
		new IO
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
	out_strings(i:IO,i1:String):IO {
		new IO
	};
};