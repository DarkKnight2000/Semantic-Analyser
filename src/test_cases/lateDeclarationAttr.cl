-- Attr i is declared after j but is used in initialisatio of j
-- Method newInt is used to intialise i
-- Shldn't return error
class Main {
	--i:String <- new String;
	j:Int <- i;
	i:Int <- newInt();
	main():Int {
		newInt()
	};
    newInt():Int{
        new Int
    };
};