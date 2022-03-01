# For Expressions and Monads

## Functions and Pattern Matching

### Case classes

Case classes are Scala's preferred way to define complex data.

E.g. If we wanna represent a JSON like this one:

```json
{
  "firstName" : "John",
  "lastName" : "Smith",
  "address" : {
    "streetAddress": "21 2nd Street",
    "state": "NY",
    "postalCode": 10021
  },
  "phoneNumbers": [
    { "type": "home", "number": "212 555-1234"},
    { "type": "fax", "number": "646 555-4567"}
  ]
}
```

Two possible representations through case classes, the first one:

```scala
abstract class JSON
object JSON:
	case class Seq (elems: List[JSON]) extends JSON
	case class Obj (bindings: Map[String, JSON]) extends JSON
	case class Num (num: Double) extends JSON
	case class Str (str: String) extends JSON
	case class Bool (b: Boolean) extends JSON
	case class object Null extends JSON
```

The second representation is more concisely with enum:

```scala
enum JSON:
	case Seq (elems: List[JSON]) 
	case Obj (bindings: Map[String, JSON]) 
	case Num (num: Double) 
	case Str (str: String) 
	case Bool (b: Boolean) 
	case object Null 
```

So if we wanna define the JSON object previous stated with enum, it will look like this:

```scala
val jsData = JSON.Obj(Map(
	"firstName" -> JSON.Str("John"),
	"lastName" -> JSON.Str("Smith"),
	"address" -> JSON.Obj(Map(
  		"streetAddress" -> JSON.Str("21 2nd Street"),
  		"state" -> JSON.Str("NY"),
  		"postalCode" -> JSON.Num(10021)
  )),
	"phoneNumbers" -> JSON.Seq(List(
  		JSON.Obj(Map(
      		"type" -> JSON.Str("home"), "number" -> JSON.Str("212 555-1234")
      )),
    JSON.Obj(Map(
      		"type" -> JSON.Str("fax"), "number" -> JSON.Str("646 555-4567")
      ))
  	))
))
```

### Pattern Matching

We work with enum and case classes with pattern matching. Here's a method that returns the string representation of JSON data:

```scala
def show(json: JSON) : String = json match 
	case JSON.Seq(elems) => 
			elems.map(show).mkString("[",",","]") // recursively map the elements of the sequence and covert them to a String that starts with open brackets, a comma between adjacent string elements and ends with close brackets.
	case JSON.Obj(bindings) => 
			 val assocs = bindings.map((key, value) => s"${inQuotes(key)}: ${show(value)}")
			 assocs.mkString("{",",\n", "}")
  case JSON.Num(num) => num.toString
	case JSON.Str(str) => inQuotes(str)
	case JSON.Bool(b) => b.toString
	case JSON.Null => "null"

def InQuotes(str: String) : String = "\"" + str + "\"" // because key in JSON are in quotes
```

### For-expressions and pattern matching

The left-hand side of a generator may also be a pattern:

```scala
def bindings(x: JSON) : List[(String, JSON)] = x match
		case JSON.Obj(bindings) => bindings.toList
		case _ => Nil

for
		case ("phoneNumbers", JSON.Seq(numberInfos)) <- bindings(jsData) // bindings goes through all the element of jsData, and convert them to a list. We select the bindings that match the key with "phoneNumber" and has JSON.Seq(numberInfos) */
		numberInfo <- numberInfos // numerInfos is a list inside SEQ and we take each element
		case("number", JSON.Str(number)) <- bindings(numberInfo) // the numberInfo is another object and we retrieve the bindings
		if(number.startsWith("212")) // the number has to start with 212
		yield number // so we return the numbers that match that conditions

```

**Note: if the pattern starts with case, the sequence is filtered so that only elements matching the pattern are retained.**

## Queries with for

To find the names of all authors who have written at least two books present in the database:

```scala
val repeated =
    for
      b1 <- books
      b2 <- books
      if b1 != b2
      a1 <- b1.authors
      a2 <- b2 authors
      if a1 == a2
    yield a1
repeated.distinct // Remove duplicate authors who are in the result list twice
// A better alternative is to compute with sets instead of sequences:
val bookSet = books.toSet
    for
      b1 <- bookSet
      b2 <- bookSet
      if b1 != b2
      a1 <- b1.authors
      a2 <- b2 authors
      if a1 == a2
    yield a1

```

## Translation of for expression

In reality, the Scala compiler expresses for-expressions in terms of map, flatMap and a lazy variant of filter. Here is the translation scheme used by the compiler:

1. A simple for-expression:

   ```scala
   for x <- e1 yield e2
   // is translated to
   e1.map( x => e2)
   ```


2. A for-expression with filter:

   ```scala
   for x <- e1 if f; s yield e2
   /* Where f is a filter and s is a (potentially empty) sequence of generators and filters, is translated to: */
   for x <- e1.withFilter(x => f); s yield e2
   /* P.S. Filter will take the original collection and produce a new collection, but withFilter will non-strictly (i.e. lazily) pass unfiltered values through to later map/flatMap/withFilter calls, saving a second pass through the (filtered) collection. Hence it will be more efficient when passing through to these subsequent method calls */
   ```

3. A for-expression with multiple generator:

   ```scala
   for x <- e1; y <- e2; s yield e3
   /* Where s is a (potentially empty) sequence of generators and filters, is translated to: */
   e1.flatMap(x => for y <- e2; s yield e3)
   ```

Example:  

```scala
for 
	i <- 1 until n
	j <- 1 until i
	if isPrime(i + j)
yield (i, j)
// Applying the translation scheme it gives:
(1 until n).flatMap( i => 
          (1 until i)
                    .withFilter(j => isPrime(i + j))
										.map(j => (i, j))
                   )
```

**The translation of for expression is not limited to list or sequences, it is based solely on the presence of the methods map, flatMap and withFilter. This lets you use the for expression syntax on your own types as well, you must only define map, flatMap and withFilter for these types.** For example, books might not be a list, but a database store on some server. As long as the client interface to the database defines the methods map, flatMap and withFilter, we can use the for syntax for querying the database. This is the basis of data base connection frameworks such as Slick or Quill, as well as big data platforms such as Spark.

## Monads

Ok, the course on coursera, includes many formalism to explain this concept, I will refer to the book functional programming of Alvin Alexander, because is more straightforward and pragmatic.

- The primary purpose of monads is to ley you compose code in for expression (i.e. to glue code together).
- For a Scala class to be a monad, it needs 3 things:
  - A map method.
  - A flatMap method.
  - Some sort of lift function (to "lift" another type into monad).

E.g. :

```scala
val result: Wrapper[Int] = for {
  a <- Wrapper[1]
  b <- Wrapper[2]
} yield a + b 

// Translated 
Wrapper[1].flatMap( i => 
         Wrapper[2].map(j => i + j)
                   )
```

This code tells me a couple of things:

1. Wrapper will be a class that takes a single Int constructor parameter.
2. Because it works with multiple generators in a for expression, Wrapper must implement map and flatMap.
3. Because result has the type Wrapper[Int], those map and flatMap functions must return that same type.

```scala
class Wrapper[Int] private (value: Int) {
  
    def map(f: Int => Int) : Wrapper[Int] = {
      // apply f to an Int to get a new Int
      val newInt = f(value) // value is the value passed to the constructor when new Wrapper[1] for instance

      // wrap the new Int in a Wrapper
      Wrapper(newInt)
    }
  
  // flatmap is easier, just applies it's given function (f) to the value is wraps (value)
  def flatMap(f: Int => Wrapper[Int]): Wrapper[Int] = {
    // apply f to an Int to get a Wrapper[Int]
    val newValue = f(value)
    
    //return a new Wrapper[Int]
    newValue
  }
}

// The last step in the process is to create an apply method in the companion object. Apply is essentially a Factory method that lets us create new Wrapper instances without needing the new keyword val a = Wrapper(1), new not needed, is a syntatic sugar.
object Wrapper {
  def apply[Int](value: Int): Wrapper[Int] = new Wrapper(value)
}


```

The reason why there is an apply method in a companion object rather then using a case class is a techincal one: FP developers like to say that a method like apply "lifts" an ordinary value into the wrapper. Put another way, an Int on its own looks like this: 100, but when you use apply to lift the Int into Wrapper, the result is a wrapper around the Int: Wrapper(100). With Int maybe the concept is similar to not having apply and using new, but think about more complex data structures.

## Exceptional Monads

Exceptions in Scala are defined similarly as in Java. An exception class is any subclass of java.lang.Throwable, which has itself subclasses java.lang.Exception and java.lang.Error. Values of exception classes can be thrown. A thrown exception terminates computation if it is not handled with a try/ catch.

Rather than throwing exceptions, the Scala/FP idiom is to handle exceptions inside your function and return an Option (Monad):

```scala
// If all goes well, you're going to get a Some[Int] - an Int wrapped in a Some Wrapper, otherwise you are getting a None.
def makeInt(s: String): Option[Int] = {
  try{
    Some(s.trim.toInt)
  } catch {
    case e: Exception => None
  }
}

// The previous code is great because you can write code that uses makeInt like this:
makeInt(input) match {
  case Some(i) => println(s"i=$i")
  case None => println("toInt could not parse input")
}
```

In addition to Option you can also use Try, Either or third-party approaches. 

| Base Type | Success Case | Failure Case |
| --------- | ------------ | ------------ |
| Option    | Some         | None         |
| Try       | Success      | Failure      |
| Or        | Good         | Bad          |
| Either    | Right        | Left         |



| Construct | When to use                                               |
| --------- | --------------------------------------------------------- |
| Option    | When you don't need the erro message.                     |
| Try       | Particularly good when you want to wrap exceptions.       |
| Or        | An alternative to Try when you want the "failure reason". |
| Either    | No clue, never used for difficult naming conventions.     |

**Note: The same concept can be applied to null values, in FP use Option rather than them**.
