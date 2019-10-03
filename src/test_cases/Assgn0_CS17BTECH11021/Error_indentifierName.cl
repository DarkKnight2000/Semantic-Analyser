(* This code produces error as a indentifier name starts with a capital letter *) 
class Main{
    i:IO <- new IO;

    main(): IO{{
        i.out_string("Enter an integer to compute its factorial: ");
        let N: Int <- i.in_int() in
            let m: Int <- i.in_int() in
                if n = m then
                    i.out_string("Both are equal\n")
                else{
                    if m < n then
                        i.out_string("Greatest of ").out_int(n).out_string(" and ").out_int(m).out_string(" is ").out_int(n).out_string("\n")
                    else
                        i.out_string("Greatest of ").out_int(n).out_string(" and ").out_int(m).out_string(" is ").out_int(m).out_string("\n")
                    fi;
                }
                fi;
    }};
};