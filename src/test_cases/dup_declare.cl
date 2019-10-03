class Main {
	i:Maisn1 <- new Maisn1;
	main():IO {
		i.out_string("Hello world!\n")
	};
};

class Maisn1 inherits IO{
	i:IO <- new IO;
	out_string(i:String):SELF_TYPE {
		self
	};
};

class Maisn1 inherits IO{
	i:String <- new String;
	out_string(i:String):SELF_TYPE {
		self
	};
};
class Maisn1 inherits IO{
	i:String <- new String;
	out_string(i:String):SELF_TYPE {
		self
	};
};