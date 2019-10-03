class Main{
    i:IO <- new IO;

    main(): IO{{
        i.out_string("Enter an integer to compute its floor value: ");
        let n: Int <- i.in_int() in
            -- While taking input itself the decimal part is removed so we get the floor
            if n < 0 then
                i.out_string("Floor is ").out_int(n-1).out_string("\n")
            else
                i.out_string("Floor is ").out_int(n).out_string("\n")
            fi;
    }};
};