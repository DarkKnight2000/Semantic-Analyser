# Semantic analyzer

1. Semantic.java contains following variables:
    classMap -> class name as key and corresponding AST.class_ object as value
    inherGraph -> Adjacency list for inheritance
    lateDeclared -> Names of classes which are declared after their children
    depths -> Keys are class name and values are corresponding distance of each node from Object class + 1
    allClsNames -> List of all classes which are defined in the file

2. While adding classes into respective lists in the order they appear we keep track of classes which are declared after their children class. After reading all the classes, we will check if there are classes left undeclared and print the relevant errors.

3. If the lateDeclared list is empty, then we call checkCycles function. This function uses BFS to check for cycles( stored in the variable inherGraph ). Before this function is called the allClsNames list contains class names in the order they appear in the program. But after the function call we modify it to contain classes in topological sort order(i.e., no child class of a class occurs before it in the list).

4. With the sorted list, we loop over each class and do the following steps.

5. Check for inheritance errors( like declaring attributes of a parent class or changing parameter/return types of parent class methods). We achieve this by using the function addParFeats. This function returns all the error messages in a string variable. In the function first we check for redefinition of attributes(Attributes shouldn't be redfined). Then we check if there is a redefinition of a method and compare their parameter types and return types. For this we use getErr(), a static function of the AST.method class which returns an error message for redefined methods. This function takes two AST.method objects as input and returns the following values for each respective case:
    > If the method names are not same return "" (because they are unrelated)
    -
    >