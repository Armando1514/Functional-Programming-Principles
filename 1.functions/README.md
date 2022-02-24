# functional



### High-Order Functions

Functional languages treat functions as first-calss values. This means that, like any other value, a function can be passed as a parameter and returned as a result. **Functions that take other functions as parameters or that return functions as results are called higher order functions**.

#### Function types

The type A => B is the type of a function that takes an argument of type A and returns a result of type B. So, Int => Int is the type of functions that map integers to integers.

### Currying

Considering the following snippet:

```scala
def sum(f: Int => Int) : (Int, Int) => Int = 
	def sumF(a: Int, b: Int) : Int =
			if a > b then 0
			else f(a) + sumF(a + 1, b)
	sumF

def fact(n: Int): Int = 
	if(n == 0) then 1
	else n * fact(n-1)

def sumInts = sum(x => x)
def sumCubes = sum(x => x * x * x)
def sumFactorials = sum(fact)
```

We can write code like this:

```scala
sumCubes(1, 10) + sumFactorials(10, 20)
/*
For instance:
sumCubes = sum(x => x * x * x) = sumF(a = 1, b = 10) 
This because the first sum takes a function, but this function is equals to the sumF where I apply to a and b the passed function.
In short:
sumCubes(1, 10) = sumF(1, 10) with f = sum(x => x * x * x)
It is a substitution model
*/
```

But, we can avoid sumInts, sumCubes and cut out the middlemen.

```scala
sum (x => x * x * x) (1, 10)
/*
1. sum(cube) applies sum to cube and returns the sum of cubes functiion, so returns sumF(a: Int, b: Int) : Int = ... a * a * a + sumF(a + 1, b), therefore is equivalent to sumCubes.
2. This function is next applied to the arguments (1, 10).
*/
```

The definition of functions that return functions is so useful in functional programming that there is a special syntax for it in Scala. For instance, the following definition of sum is equivalent to the one with the nested sumF function, but shorter:

```scala
def sum(f: Int => Int)(a: Int, b: Int): Int =
	if a > b then 0 else f(a) + sum(f)(a + 1, b)
```

This style of definition and function application is called **currying**, named for its instigator, Haskell Brooks Curry (1900 - 1982). In an easy way, it means that a function that takes multiple arguments can be translated into a series of function calls that each take a single argument. For instance:

f1 = f(x)

f2 = f1(y)

result = f2(z).

is mathematically the same as:

result = f(x)(y)(z)

So you don't actually need to define all the functions, but you can create a sequence of anoynmous functions that each take a parameter.

### Functions and Data

We want to understand how to create and encapsulate data structures. Let's start from an example:

Problem: rational numbers, we want to design a package for doing rational arithmetic. A rational number x / y is represented by two integers:

- its numerator x.
- Its denominator y.

Suppose we want to implement the addition of two rational numbers. We can write something like this:

```scala
def addRationalNumerator(n1: Int, d1: Int, n2: Int, d2: Int): Int
def addRationalDenominator(n1: Int, d1: Int, n2: Int, d2: Int): Int
```

A better choice is to combine the numerator and denominator of a rational number in a data structure. In scala we do this by defining a **class**:

```scala
class Rational(x: Int, y: Int):
	def numer = x
	def denom = y
```

**NOTE**: Scala implicitly introduce a constructor, this one is called the primary constructor of the class. The primary constructor automatically executes all the statements in the class body ( not like in Java where executes only the one in the constructor). 

Scala allows the declaration of **auxilary constructors**, those are methods named this, e.g. :

```scala
class Rational(x: Int, y: Int):
	def this(x: Int) = this(x, 1)
```

This definition introduces two entities:

- A new type, named Rational.
- A constructor Rational to create elements of this type.

We call the elements of a class type **objects**. We create an object by calling the constructor of the class (e.g. Rational(1, 2)). We can define methods inside the class:

```scala
class Rational(x: Int, y: Int):
	def numer = x
	def denom = y
	def add(r: Rational) =
		Rational(numer * r.denom + r.numer * r.denom, denom * r.denom)
	override def toString = s"$numer/$denom"
```

**Preconditions**: Let's say in our Rational class we want to require that the denominator is positive. We can use a predefined function called "**require**". It takes a condition and an optional message string. If the condition passed is false, an 'IllegalArgumentException' is thrown with the given message string. E.g. :

```scala
class Rational(x: Int, y: Int):
	require( y > 0, "denominator must be positive")
```

We can use also "**assert**". Like require, it takes a condition and an optional message string as parameter. Like require, a failing assert will also throw an exception, but it's a different one: "AssertionError".

This reflects a different in itent:

- require is used to enforce a precondition on the caller of a function.
- assert is used as to check the code of the function itself.

**Extension Methods**: Having to define all methods that belong to a class inside the class itself can lead to very large classes, and is not very modular. Methods that do not need to access the internals of a class can alternatively be defined as extension methods outside the class. E.g. :

```scala
extension (r: Rational)
	def min(s: Rational): Boolean = if s.less(r) then s else r

// this can be called as it was an inside method
Rational(1/2).min(Rational(2/3))
```

### Operators

In principle, the rational numbers defined by Rational are as natural as integers.

But for the user of these abstraction, there is a noticeable difference:

- We write x + y, if x and y are integers, but
- We write r.add(s) if r and s are rational numbers.

In scala we can eliminate this difference because operator such as + or < count as identifiers in Scala ( x1, *, +=%, ... are valid identifiers in Scala).

Since operator are identifier is possible to use them as method names. E.g. :

```scala
extension (r: Rational)
	def + (s: Rational): Rational = r.add(s)
// we can even write infix to for a more convenient notation:
// infix def + (s: Rational): Rational = r.add(s)

// this allows rational number to be used like Int or Double
val x = Rational(1, 2)
val y = Rational(1, 2)
x + y // x.+(y)
```

### 