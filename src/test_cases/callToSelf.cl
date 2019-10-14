class Main {
	i:Maisn1 <- new Maisn1;
	main():IO {
		i.out_strings("Hello world!\n",0)
	};
};

class Maisn1 inherits IO{
	--i:IO <- new IO;
	i:String <- new String;
	out_strings(i1:String,i3:Int):IO1 {
		self.out_string(i1)
	};
};