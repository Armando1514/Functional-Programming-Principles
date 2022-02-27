## Other Sequences

We have seen that lists are linear. Access to the first element is much faster than access to the middle or end of a list. The scala library also defines an alternative sequence implementation, **vector. This one has more evenly balanced access patterns than a List.** Because based on the functional paradigm, every time there is a change in a List, you have to create a new one, and to read an element at the end of the list, you have to scroll all the list.

**Idea**:

- The idea is that vector is a tree-based data structure. For instance if the vector is small (up to 32 elements), than is just an array).            

  ![justAnArray](./justAnArray.png)

- If the vector grows beyond 32 elements, it becomes an array of array which each of them has 32 elements (In total 32 * 32 = 1024).

  ![arrayOfArray](./arrayOfArray.png)

- If it grows again, each of the sub-array, will spawn a 32-cell array. This can grow till a depth of 5 ( 2^25 elements max size).

  ![arrayOfArrayOfArray](./arrayOfArrayOfArray.png)

- If you have to edit an element, let's say in the example in the last children based the above image. All the acenstors (parent and parent of the parent) need to change (in red in the image below the changes). This is still better than changing all the list.

  ![changeToElement](./changeToElement.png)

  

```scala
val num = Vector(1, 2, 3, 4, 5)
```

They support the same operations as list, with the exception of '::', instead of x :: xs, there is:

- x +: xs  = Create a new vector with leading element x, followed by all elements of xs.
- xs :+ x = Create a new vector with trailing element x, preceded by all elements of xs.

### Collection Hierarchy

![changeToElement](./collectionHierarchy.png)

A common base class of List and Vector is Seq, the class of all sequences. Seq itself is a subclass of Iterable. **Array is defined in java, so can't be a directly subclass of Sequence. What happens instead is that array is a separate class but the conversion is automatically to a subclass of Sequence. So Arrays and Strings support the same operations as Seq and can implicitly be converted to sequence when needed.**

```scala
val xs: Array[Int] = Array(1, 2, 3)
xs.map(x => 2 * x)

val ys: String = "Hello world"
ys.filter(_.isUpper)
```

**Another simple kind of sequence is the range. It represents a sequence of evenly spaced integers. Three operators**:

- Until (exclusive)

```scala
val r: Range = 1 until 5 // 1, 2, 3, 4
```

- To (inclusive)

```scala
val s: Range = 1 to 5 // 1, 2, 3, 4, 5
```

- By (to determine step value)

```scala
1 to 10 by 3 // 1, 4, 7, 10
6 to 1 by -2 // 6, 4, 2
```

**More sequence operations**:

- xs.exists(p) = true if there is an element of x of xs such that p(x) holds, false otherwise.
- xs.forall(p)  = true if p(x) holds for all elements x of xs, false otherwise.
- xs.zip(ys) = A sequence of pairs drawn from corresponding elements of sequences xs and ys

```scala
List(1, 2, 3).zip(Vector("a", "b")) // List((1, "a"), (2, "b"))
```

- xs.unzip = Splits a sequence of pairs xs into two sequences consisting of first, respectively second halves of all pairs.
- xs.flatMap(f) = Applies collection-valued function f to all elements of xs and concatenates the results.

```scala
// general rule xs.flatMap(f) = xs.map(f).flatten 
// the above expression can be simplified to
(1 until n).flatMap(i =>  (1 until i).map(j => (i, j)))
```

- xs.sum  = The sum of all elements of this numeric collection.
- xs.product = The product of all elements of this numeric collection.
- xs.max = The maximum of all elements of this collection (an Ordering must exist).
- xs.min = The minimum of all elements of this collection.

## For-Expressions (For comprehension)

High-order functions such as map, flatMap or filter provide powerful constructs for manipulating lists. But sometimes the level of abstraction required by these function make the program difficult to understand. In this case, Scala's for expression notation can help.

E.G. : 

Let persons be a list of elements of class Person, with fields name and age.

```scala
case class Person(name: String, age: Int)
```

To obtain the names of persons over 20 years old, you can write:

```scala
for p <- persons if p.age > 20 yield p.name
// which is equivalent to 
persons.filter(p => p.age > 20).map(p => p.name)
```

A scala for expression can contain the following three types of expressions:

- **Generators**: they have this general form "p <- persons". In this expression the value p iterates over all of the elements contained in persons. They can be more complicated but generally:
  - Every for expression begins with a generator.
  - For expressions can have multiple generators
  - The left side of a generator can also be a pattern
- **Definition**:  A definition binds the pattern on the left to the value of expression on the right, for instance: "n = p.name". The variable n is bound to the value p.name. That statement has the same effect as writing this code outside of a for comprehension: "val n = p.name".
- **Filters**: For comprehension filters have the general form if( expression).

```scala
for {
  p <- persons // generator
  n = p.name // definition
  if ( n startsWith "To") // filter
} yield p //.map(p => p) returns the p satisfying the for expression
```

### Set

Sets are another basic abstraction in the Scala collections. A set is written analogously to a sequence:

```scala
val fruit = Set("apple", "banana", "pear")
val s = (1 to 6).toSet
```

Most operations on sequences are also available on sets.

The principal difference between sets and sequences are:

1. Sets are unordered; the elements of a set do not have a predefined order in which they appear in the set.
2. Sets do not have duplicate elements.
3. The fundamental operation on sets is contains (s.contains(5) for instance).

### Map

A map of type Map[Key, Value] is a data structure that associets keys of type Key with values of type Value.

E.g.

```scala
val capitalOfCountry = Map("US" -> "Washington", "Switzerland" -> "Bern")
val romanNumerals = Map("I" -> 1, "V" -> 5, "X" -> 10)
// maps are functions, you can write:
capitalOfCountry("US") //returns "Washington"
capitalOfCountry("Andorra") // java.util.NoSuchElementException: key not found: Andorra
// To query a map without knowing beforehand whether it contains a given key:
capitalOfCountry.get("US") //returns Some("Washington"), is an Option Type
capitalOfCountry.get("Andorra") // returns None
// Decomposition with pattern matching:
capitalOfCountry.get("US") match 
	case Some(capital) => capital
	case None => "missing data"
```

Functional updates of a map are donwe with the + and ++ operations:

- m + ( k -> v ) = The map that takes key "k" to value "v" and is otherwise equal to "m"

  ```scala
  val m1 = Map("red" -> 1, "blue" -> 2)
  val m2 = m1 + ("blue" -> 3) // now m2 = Map(red -> 1, blue -> 3)
  ```

- m ++ kvs = The map "m" updated via "+" with all key/value pairs in kvs.

### Sorted and GroupBy

Two useful operations known for SQL queries are groupBy and orderBy. 

- OrderBy on a collection can be expressed using sortWith and sorted.

```scala
val fuit = List("apple", "pear", "orange", "pineapple")
fruit.sortWith(_.length < _.length) //List("pear", "apple", "orange", "pineapple")
fruit.sorted //List("apple", "orange", "pear", "pineapple")
```

- GroupBy is available on Scala collections. It partitions a collection into a map of collections according to a discriminator function f.

  ```scala
  fruit.groupBy(_.head) // map based on the first character of each element) res: 
                // Map(p -> List(pear, pineapple) //because both starts with p,
               	//     a -> List(apple)
                //     o -> List(orange))
  ```

  

