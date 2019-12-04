class Main {
	--i:String <- new String;
	j:Int <- i;
	i:Int <- 0;
	main():IO {
		case new Bool of
			x:String => new IO.out_string(x);
			x:Int => new A.out_int(x);
		esac
	};
};

class A inherits IO{
};