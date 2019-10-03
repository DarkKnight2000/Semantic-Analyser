class Main{
    i:IO <- new IO;

    pattern(a: Int): Object{
        (let n: Int <- 1 in
        (let j: Int <- 1 in
        {
                while n <= a loop{
                    j <- 1;
                    while j <= n loop{
                        i.out_string("*");
                        j <- j+1;
                    }
                    pool;
                    i.out_string("\n");
                    n <- n+1;
                    
                }
                pool;
        }
        ))
        
    };

    main(): Object{{
        i.out_string("Enter an integer to draw a increasing pattern: ");
        let n: Int <- i.in_int() in
        if n < 0 then
            i.out_string("Invalid input\n")
        else
            pattern(n)
        fi;
    }};
};