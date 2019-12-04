-- If there is error in let bound variables no need to report error in let body as it might lead to repeated errors
class Main {
	i:String <- new String;
	i1:Bool <- false;
	i7:Int <- new Int;
	i8:IO <- new IO;
	main():IO {{
		let i5:IO1,i6:IO <- new IO1 in i8.out_int(i7);
	}};
};
