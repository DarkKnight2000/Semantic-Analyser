class Main{
    i:IO <- new IO;
    factorial(a: Int): Int{
        if a = 0 then 1 
        else a*factorial(a-1) 
        fi
    };
    main(): IO{{
        i.out_string("Enter an integer to compute its factorial: ");
        let n: Int <- i.in_int() in
        if n < 0 then
            i.out_string("The number doesn't have a factorial\n")
        else
            i.out_string("Factorial of ").out_int(n).out_string(" is ").out_int(factorial(n)).out_string("\n")
        fi;
    }};
};