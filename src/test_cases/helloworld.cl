class Main {
	i:IO <- new IO;
	i1:IO <- new IO;
	main():IO {
		i.out_string("Hello world!\n")
		--i1.out_string("Hello world!\n")
	};
};