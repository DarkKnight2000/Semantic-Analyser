-- In dispatch if we pass objects of child class of actual formal type we shouldnt get error
class Main {
	i:Maisn1 <- new Maisn1;
	main():IO {
		i.out_strings(new Maisn1)
	};
};

class Maisn1 inherits IO{
    out_strings(a:IO):IO{
        new IO
    };
};