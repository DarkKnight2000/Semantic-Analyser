class Main {
	i:Maisn1 <- new Maisn1;
	mains():IO {
		i.out_strings("Hello world!\n",0)
	};
};

class Maisn1 inherits Maisn2{
	--i:IO <- new IO;
	out_strings(i1:String,i3:Int):IO {
		i.out_string(i1)
	};
};

class Maisn2 inherits IO{
	i:String <- new String;
	j:IO <- new IO;
	out_strings(i4:Int,i3:String):IO {
		j.out_string(i3)
	};
};