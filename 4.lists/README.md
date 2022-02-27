## Lists

## List Methods

**Basic operations**:

- xs.head = the first element of the list.
- xs.tail = the list composed of all the elements except the first.
- xs.isEmpty =  true if the list is empty, false otherwise.
- xs.length  = Number of element of xs.
- xs.last = The list's last element, exception if xs is empty.
- xs.init = A list consisting of all elements of xs except the last one, exception if xs is empty.
- xs.take(n) = A list consisting of the first n elements of xs, or xs itself if it is shorter than n.
- xs.drop(n) = the rest of the collection after taking n elements.
- xs(n) =  The element of xs at index n.

**Creating new lists**:

- xs ++ ys = The list consisting of all elements of xs followed by all elements of ys.
- xs.reverse = The list containing the elements of xs in reversed order.
- xs.updated(n, x) = The list containing the same elements as XS, except at index n where it contains x.

**Finding elements**: 

- xs.indexOf(x) = The index of the first element in XS equal to x, or -1 if does not appear in xs.
- xs.contains(x) = same as (xs.indexOf(x) >= 0).

## Generic methods

Let's try to implement the merge sort. The idea is as follows:

- If the list consists of zero or one elements, it is already sorted.
- Otherwise:
  - Separate the list into two sub-lists, each containing around half of the elements of the original list.
  - Sort the two sub-lists.
  - Merge the two sorted sub-lists into a single sorted list.

```scala
def msort[T](xs: List[T])(lt: (T, T) => Boolean) = 
	val n = xs.length/2
	if(n == 0) then xs
	else
		val (fst, snd) = xs.splitAt(n)
		merge(msort(fst)(lt), msort(snd)(lt))

def merge[T](xs: List[T], ys: List[T]) = (xs, ys) match
	case (Nil, ys ) => ys
	case (xs, Nil) => xs
	case (x :: xs1, y :: ys1) => 
		if lt(x, y) then x :: merge (xs1, ys)
		else y :: merge(xs, ys1)

// In this way we can pass the function for the comparison, e.g.
val xs = List("apple", "pear", "orange")
msort(fruits)((x: String, y: String) => x.compareTo(y) < 0)
```

## Higher-Order List Functions

### **Mapping**

Theoretically the implementation looks like this one (in reality is efficiently implemented):				

```scala
def map[U](f: T => U): List[U] = xs match
		case Nil => xs
		case x :: xs => f(x) :: xs.map(f)
// this can be written more concisely:
xs.map(x => x * factor)
```

### Filter

This pattern is generalized by the method filter of the List class:				

```scala
def filter(p: T=> Boolean): List[T] = xs match
		case Nil => xs
		case x :: xs => if p(x) then x :: xs.filter(p) else xs.filter(p)
// this can be written more concisely:
xs.filter(x => x > 0)
```

 	Besides filter, there are also the following methods that extract sublists based on a predicate:

- xs.filterNot(p) = Same as xs.filter(x => !p(x)); the list consisting of those elements of xs that do not satisfy the predicate p.
- xs.partition(p) = Same as (xs.filter(p), xs.filterNot(p)), but computed in as single traversal of the list xs.
- xs.takeWhile(p) = The longest prefix of list xs consisting of elements that all satisfy the predicate p.
- xs.dropWhile(p) = The remainder of the list xs after any leading elements satisfying p have been removed. DropWhile discards all the  items at the start of a collection for which the condition is true . It  stops discarding as soon as the first item fails the condition. filter  discards all the items throughout the collection where the condition is  not true. It does not stop until the end of the collection
- xs.span(p) = Same as (xs.takeWhile(p), xs.dropWhile(p)) but computed in a single traversal of the list xs.

### Reduction of lists

Another common operation on lists is to combine the elements of a list using a given operator.  For example:

- sum(List(x1, ..., xn)) = 0 + x1 + ... + xn
- product(List(x1, ..., xn)) = 1 * x1 * ... * xn

We can implement this with the usual recursive schema:

```scala
def sum(xs: List[Int]): Int = xs match
	case Nil => 0
	case y :: ys => y + sum(ys)
// this can be written more concisely:
// List(x1, ..., xn).reduceLeft(opt) = x1.op(x2). ... .op(xn)
def sum(xs: List[Int]): Int = (0 :: xs).reduceLeft((x, y) => x + y)
def product(xs: List[Int]): Int = (1 :: xs).reduceLeft((x, y) => x * y)
// there is even reduceRight, essentially, it starts computing from the end instead of from beginning
// reduceLeft or reduceRight come from the FoldLeft/ FoldRight generalization that takes an aggregate too as a parameter
//E.g. 
def f(ls: List[Int]) = 
	ls.foldRight(0/*aggregate*/)((_/*element of ls*/, acc /*aggregate*/) => acc + 1 )
// it counts the number of elements in ls
```

### 

