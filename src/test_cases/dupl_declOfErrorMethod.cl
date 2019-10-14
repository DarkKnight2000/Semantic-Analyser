class Main {
	i:Maisn1 <- new Maisn1;
	main():IO {
		i.out_string("Hello world!\n",0)
	};
};

class Maisn1 inherits IO{
	i:String <- new String;
	out_string(i1:String,i3:Int):IO {
		i.out_string(i1)
	};
    out_string(i:String):IO{
        new IO
    };
};