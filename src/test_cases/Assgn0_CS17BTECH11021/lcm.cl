class Main{
    i:IO <- new IO;
    gcd(a: Int, b: Int): Int{
        if a = 0 then 0 else{
            if b = 0 then 0
            else{
                if b < a then gcd(a-b, b)
                else{
                    if a < b then gcd(a, b-a)
                    else a
                    fi;
                }
                fi;
            } 
            fi;
        } 
        fi
    };
    main(): IO{{
        i.out_string("Enter integers to compute their GCD: \n");
        let n: Int <- i.in_int() in
            let m: Int <- i.in_int() in
            if m <= 0 then
                i.out_string("Invalid Input\n")
            else{
                if n <= 0 then
                    i.out_string("Invalid Input\n")
                else{
                    let gcd: Int <- gcd(n, m) in
                        i.out_string("GCD of ").out_int(n).out_string(" and ").out_int(m).out_string(" is ").out_int(gcd).out_string(" and LCM is ").out_int((m*n)/gcd);
                }

                fi;
            }
            fi;
    }};
};