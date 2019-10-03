(* This code produces error due to a non escaped new line error in out_string statement *) 
class Main{

    main(): IO{{
        i.out_string("Enter an integer: ");
        let n: Int <- i.in_int() in
            i.out_string("You 
            entered ").out_int(n).out_string("\n")
    }};
};