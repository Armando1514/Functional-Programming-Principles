# Type-Directed Programming

If he write 'val x = 42', the compiler infers that x is Int, because the type of 42 is Int. The compiler is even able to do the opposite, namely to infer values from types. When there is exactly one "obvious" value for a type, the compiler can find this value and provide it to you.

**Why is useful**?

For instance:

```scala
def sort[A](xs: List[A])(ord: Ordering[A]): List[A] = ...
```

Needs ord to understand the order types of the List, but passing around Ordering arguments is cumberstone:

```scala
sort(xs)(Ordering.Int)
sort(ys)(Ordering.Int)
sort(strings)(Ordering.String) 
```

We will se how we can make the compiler pass the ordering argument for us.

## Implicit Parameters

The first step consist in indicating that we want the compiler to supply the ord argument by making it as implicit:

```scala
def sort[A](xs: List[A])(implicit ord: Ordering[A]): List[A] = ...
```

Then, calls to sort can omit the ord parameter and the compiler will try to infer it for us:

```scala
sort(xs)
sort(ys)
sort(strings)
```

The compiler infers the argument value based on its *expected type*.

**Reasoning**:

Let’s detail the steps that the compiler goes through, in order to infer the implicit parameter. Consider the following expression:

```scala
sort(xs)
```

Since xs has type List[Int], the compiler fixes the type parameter A of sort to Int:

```scala
sort[Int](xs)
```

As a consequence, this also fixes the expected type of the ord parameter to Ordering[Int].

The compiler looks for *candidate definitions* that match the  expected type Ordering[Int]. In our case, the only matching candidate is the scala.math.Ordering.Int  definition. Thus, the compiler passes the value  Ordering.Int to the method sort:

```scala
sort[Int](xs)(Ordering.Int)
```

Where does the compiler look for *candidate definitions* when it tries to infer an implicit parameter of type T?

The compiler searches for definitions that:

- have type T,
- are marked implicit,
- are visible at the point of the function call, or are defined in a companion object *associated* with T.

If there is a single (most specific) definition, it will be taken as the actual argument for the implicit parameter. Otherwise an error is reported.

## Type classes

Type classes provide a form of polymorphism: they can be used to implement algorithms that can be applied to various types. The compiler selects the type class implementation for a specific type at compile-time.

A type class definition is a trait that takes type parameters and defines operations that apply to these types. Generally, a type class definition is accompanied by laws, which describe properties that instances must satisfy, and that users of type classes can rely on.

For instance:

In mathematics, a **ring** is one of the fundamental algebraic structures used in abstract algebra. It consists of a set equipped with two binary operations that generalize the arithmetic operations of addition and multiplication. Through this generalization, theorems from arithmetic are extended to non-numerical objects such as polynomials, series, matrices and functions.

This structure is so common that, by abstracting over the ring structure, developers could write programs that could then be applied to various domains (arithmetic, polynomials, series, matrices and functions).

```scala
trait Ring[A] {
  def plus(x: A, y: A): A
  def mult(x: A, y: A): A
  def inverse(x: A): A
  def zero: A
  def one: A
}
```

This is how we would define a function that checks that the + associativity law is satisfied by a given Ring instance

```scala
def plusAssociativity[A](x: A, y: A, z: A)(implicit ring: Ring[A]): Boolean =
  ring.plus(ring.plus(x, y), z) == ring.plus(x, ring.plus(y, z))
```

Here is how we define an instance of Ring[Int]:

```scala
object Ring {
  implicit val ringInt: Ring[Int] = new Ring[Int] {
    def plus(x: Int, y: Int): Int = x + y
    def mult(x: Int, y: Int): Int = x * y
    def inverse(x: Int): Int = -x
    def zero: Int = 0
    def one: Int = 1
  }
}
```

**Note**:

- implicit definitions can also take implicit parameters,
- an arbitrary number of implicit definitions can be chained until a terminal definition is reached.

## Contextual Abstractions

Context = Con-Text = what comes with the text, but is not in the text.

Code becomes more modular if it can abstract over context. That is, functions and classes can be written without knowing in detail the context in which they will be called or instantiated.

**How is context represented?**

- Global values = no abstraction - this is often too rigit.
- Global mutable variable = what if different modules need different settings ? interference can be dangerous.
- "Monkey Patching" = It is something similar to global values, but instead of changing global variables,there is a root class and has less footprints of the global values, because you just change property of the inheritant class of the root calss.
- Dependency injection frameworks (e.g. Spring, Guice) = outside the language, rely on bytecode rewriting -> harder to understand and debug.

**Functional Context Representation**

In a functional programming, the natural way to abstract over context is with function parameters.

- Flexible.
- Types are checked.
- Not relying on side effects.

But sometimes this is too much of a good thing, it can lead to:

- Many function arguments, which hardly ever change.
- Repetitive, errors are hard to spot.

