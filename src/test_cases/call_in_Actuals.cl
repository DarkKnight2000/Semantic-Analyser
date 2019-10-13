class Main {
	i:Maisn1 <- new Maisn1;
    j:S;
	main():IO {
		i.out_strings(j,0)
	};
};

class Maisn1 inherits IO{
	i:IO <- new IO;
	out_strings(i1:String,i3:Int):IO {
		i.out_string(i1)
	};
};