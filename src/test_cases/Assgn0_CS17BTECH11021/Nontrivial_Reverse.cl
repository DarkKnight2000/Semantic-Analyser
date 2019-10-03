class Main{
    i:IO <- new IO;

    reverse(a: String): Object{
        (let reverseStr: String in
        (let n: Int <- a.length() in
        {
                while 1 <= n loop{
                    i.out_string(a.substr(n-1, 1));
                    --i.out_int(reverseStr.length()).out_string("\n");
                    n <- n-1;
                    
                }
                pool;
                i.out_string("\n");
        }
        ))
        
    };

    main(): Object{{
        i.out_string("Enter an string to print the reversed string: ");
        let n: String <- i.in_string() in
        if n.length() < 0 then
            i.out_string("Invalid input\n")
        else
            reverse(n)
        fi;
    }};
};