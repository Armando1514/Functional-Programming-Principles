package quickcheck

import org.scalacheck.*
import Arbitrary.*
import Gen.*
import Prop.forAll

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap:
  lazy val genHeap: Gen[H] = oneOf(
    const(empty),
    for
      v <- arbitrary[Int]
      h <- genHeap
    yield insert(v, h)
  )

  given Arbitrary[H] = Arbitrary(genHeap)

  //  adding a single element to an empty heap, and then removing this element, should yield the element in question
  property("min1") = forAll { (a: Int) =>
    val h = insert(a, empty)
    findMin(h) == a
  }

  // adding the minimal element, and then finding it, should return the element in question:
  property("gen1") = forAll { (h: H) =>
    val m = if isEmpty(h) then 0 else findMin(h)
    findMin(insert(m, h)) == m
  }

  // If you insert any two elements into an empty heap, finding the minimum of the resulting heap should get the smallest of the two elements back.
  property("min2") = forAll { (a: Int, b: Int) =>
    val min = a.min(b)
    val h = insert(a, empty)
    val newH = insert(b, h)
    findMin(newH) == min
  }

  // If you insert an element into an empty heap, then delete the minimum, the resulting heap should be empty.
  property("emptyH") = forAll { (a: Int) =>
    val h = insert(a, empty)
    val emptyH = deleteMin(h)
    isEmpty(emptyH) == true
  }

  // Given any heap, you should get a sorted sequence of elements when continually finding and deleting minima. (Hint: recursion and helper functions are your friends.)
  property("sortedH") = forAll { (h: H) =>
    def isSorted(h: H): List[A] =
      if (isEmpty(h)) Nil
      else findMin(h) :: isSorted(deleteMin(h))

    val maybeSorted = isSorted(h)
    println(maybeSorted)
    maybeSorted == maybeSorted.sorted
  }

  // Finding a minimum of the melding of any two heaps should return a minimum of one or the other.
  property("minimumMeldH") = forAll { (h1: H, h2: H) =>
      val min1 = findMin(h1)
      val min2 = findMin(h2)
      val minMeldH = findMin(meld(h1, h2))

      (minMeldH == min1) || (minMeldH == min2)
  }
