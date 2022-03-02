# Lazy Evaluation

## Lazy lists

We want to avoid computing elements of a sequence until they are needed for the evaluation result (which might be never). The idea is implemented in a new class, the LazyList. **Lazy lists are similar to lists, but their elements are evaluated only on demand.** How to define it:

```scala
val xs = LazyList.cons(1, LazyList.cons(2, LazyList.empty))
// or
LazyList(1, 2, 3)
// or the to(LazyList) method on a collection will turn a collection into a lazy list
(1 to 1000).to(LazyList)
```

Note:

- listRange(start, end) will produce a list with end - start elements and return it.
- lazyRange(start, end) returns a single object of type LazyList. The elements are only computed when they are needed, where "needed" means that someone calls head or tail on the lazy list.

LazyList supports almost all methods of List. But x :: xs always produces a list, never a lazy list. #:: can be used in expressions as well as patterns, so x #:: xs.

## Lazy Evaluation

In a pure function we can store the result of the first evaluation and re-using the stored result instead of recomputing it (dynamic programming). We call this scheme lazy evaluation ( as opposed to by-name evaluation in the case where everything is recomputed, and strict evaluation for normal parameters and val definitions). This optimization is still purely functional as an expression produces the same result each time is evaluated (based on same input).

Haskell is a functional programming language that uses lazy evaluation by default. Scala uses strict evaluation by default, but allows lazy evaluation of value definitions with the lazy val form (lazy val is evaluated lazily and only once. It is evaluated only once when we use it for first time. It is not evaluated at the time of definition. It is not evaluated every-time we access it.):

```scala
lazy val x = expr
```

Note: `val` won't change but will be computed right away.

## Computing with infinite sequences

You saw that elements of a lazy list are computed only when they are needed to produce a result. This opens up the possibility to define infinite lists. 

For instance this is the (lazy) list of all integers starting from a given number:

```scala
def from(n: Int): n#:: from(n+1)
```

The list of all natural numbers:

```scala
val nats = from(0)
```

