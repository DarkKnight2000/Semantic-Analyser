class Main {
	i:String <- new String;
	i1:Bool <- false;
	i2:Maisn1 <- new Maisn1;
	i22:Maisn12 <- new Maisn2;
	i7:Int <- new Int;
	i8:IO <- new IO;
	main():IO {
		--i1.out_string("Hello world!\n")
		--{new Bool;new Bool;}
		--while true loop new Bool pool
		--if i1 then new Int else new Int fi
		--let i5:Int <- 0 in i8.out_int(i5)
		i2@Maisn1.out_strings(i7,i)
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