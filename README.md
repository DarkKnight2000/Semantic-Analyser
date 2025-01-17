# Semantic analyzer

1. Semantic.java contains following variables:
    classMap -> class name as key and corresponding AST.class_ object as value
    inherGraph -> Adjacency list for inheritance
    lateDeclared -> Names of classes which are declared after their children
    depths -> Keys are class name and values are corresponding distance of each node from Object class + 1
    allClsNames -> List of all classes which are defined in the file

2. While adding classes into respective lists in the order they appear we keep track of classes which are declared after their child class. After reading all the classes, we will check if there are classes left undeclared and print the relevant errors.

3. If the lateDeclared list is empty, then we call checkCycles function. This function uses BFS to check for cycles( stored in the variable inherGraph ). Before this function is called the allClsNames list contains class names in the order they appear in the program. But after the function call we modify it to contain classes in topological sort order(i.e., no child class of a class occurs before it in the list).

4. The constructor method of AST.class_ is updated to store attributes and methods in separate variables( attrs and methods respectively ). Similarly getString method is also updated.

5. With the sorted list allClsNames, we loop over each class and do the following steps.

    * Check for inheritance errors( like declaring attributes of a parent class or changing parameter/return types of parent class methods). We achieve this by using the function addParFeats. This function returns all the error messages in a string variable. In the function first we check for redefinition of attributes(Attributes shouldn't be redfined). Then we check if there is a redefinition of a method and compare their parameter types and return types. For this we use getErr(), a static function of the AST.method class which returns an error message for redefined methods. This function takes two AST.method objects as input and returns the following values for each respective case:
        * If the method names are not same return "" (because they are unrelated)
        * If return types donot match return corresponding error
        * If the names are same then we check if corresponding formal types are same (this is done by static method getErr is AST.formal class), if not return an error msg (we know that err msg != "" && err msg != "\n")
        * If all formal types match between two methods and their return types are same, return "\n"

        We get a different return value for each case and use if-else statements to check for the case. If returned value is equal to "\n" then both methods have same signature, so there is no need to report error. If there is an error we store in a buffer( delBuffer ) to delete them at the end. All the parents attributes and methods are stored in parAttrs and parMethods respectively. This helps to check for dispatch calls to these methods etc,.

    * Then we check for errors in declarations of attributes and methods. This includes checking the types of attributes and all formals and return types in every method for undefined classes. The getErrDecl method returns all the errors as a single string. This method takes classMap as argument and checks if a typeid is present in keys of classMap and returns an error if it is not present. 

    * As we loop on the sorted list(parent occurs before child) the error in parent class are already reported, we don't need to report them here again.

    * We add each attribute into scopetable and for each method we add formals into scopetable and check type of body

    * This is done by setType method in expression class. This method is overrrided in all of its child classes as each class has a separate way of assigning type. This method takes in scopatable, classmap and depthmap as arguments and sets the attribute 'type' of expression to respective typeid an object belongs to. It returns all error messages encoutered during the process. 

    * For classes like plus,minus etc., where they have expressions as a part of them we call this function recursively, collect all error messages, check if the types of these parts satisfy the property of the class (for eg., for plus class both e1 and e2 should be Int types), add any new errors and return all the errors so far.

    * For AST.let class we need to update scopetable. We added new variables introduced in let expression and pass this scopetable to setType function of letbody. We added the variables into scopetable by creating a new AST.object class object and adding it into scopetable. After this we exitscope in the let and return error messages if any.

    * For AST.dispatch we called setType recursively on caller and actuals and collect all error messages. Using classmap we access class of caller, check for matching function and assign type to this dispatch call accordingly. All the errors like less/more arguments supplied, types of actual not matching the corresponding formal are added into a variable and is returned at the end. Incase of error we assign "Object" to it.

    * Static dispatch was also the same but it included an extrastep, checking if typeid is an ancestor of the caller. This was achieved by isAncestor, static function in expression.

    * If-else and typcase needed least common ancestor of two classes. So we created a static function "join" to get it. This function uses depths of classes. The ancestors of class form a singly linked list( As cool supports only single inheritance, it is a linked list because a class contains name of parent which can be taken as pointer ), so the problem is reduced to finding starting point of common part between two singly linked lists.


6. We used the test cases to see that:
    * A function can return any child class object of the declared return type.
    * A dispatch is valid even if a child class object  of original formal type is passed.
    * Name of formal doesnt need to be same while redefining.
    * Any error inside let body need not be reported if there is error in let-bound variable declaration.
    * A attribute initialisation can use any method or attribute inside the same class even if they are declared after it. So we checked types of initialisation after adding all attributes into scopetable in a separate for-loop.

7. We passed the filename of class as argument to all error returning methods, to add it into errors in subsequent calls of setType inside method body and make the errors informative.

8. We looped over all classes 2 times. The 1st time we add inherited features into a class, divide all features into valid and invalid( stored in delMthds in AST.class_, this contains methods with inheritance errors only), and check which definition of a method is in play(inherited or new one). In the 2nd pass we check for inferred body types and return any errors(declared and inferred return types not matching error, undeclared variable used in body error etc,). This decision of looping 2 times is taken because a method in class A might be calling dispatch to a method in an unrelated class B(i.e., least common ancestor is "Object") which might be:
    * An incorrect override of method in ancestor(paramter type not matching etc.,) - We should match the dispatch's actual types with the valid implementation from its ancestors 
    * Undefined Formal type or return type - We shouldn't match for dispatch's actual types 
    In all these cases the method in B is not the implementation that should be called. So in the 1st pass we categorize the features, store inherited features from all ancestors accordingly and in 2nd pass we check for the errors in body expressions and return errors. We also check bodies of methods with declaration errors too. 