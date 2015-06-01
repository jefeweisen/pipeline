package org.allenai.pipeline.contrib

import scala.reflect.ClassTag
import scala.util.hashing.MurmurHash3

// SamplingUtils adapted from spark private code:
//
// https://github.com/apache/spark/blob/master/core/src/main/scala/org/apache/spark/util/random/SamplingUtils.scala
// commit hash: dc9653641f8806960d79652afa043c3fb84f25d2

object SamplingUtils {
  /**
   * Reservoir sampling implementation.  Hashes based on each item's content for reproducibility,
   * but thus does not support duplicates.  Does not check for duplicates either.
   *
   * @param input input size
   * @param k reservoir size
   * @param seed random seed
   * @return (samples, input size)
   */
  val dScale = Math.pow(0.5, 32)

  def randMurmur(a:String, bLessThan:Int) : Int  = {
    val rand0:Int = MurmurHash3.stringHash(a)
    val rand1 = rand0.toDouble*dScale + 0.5
    (rand1 * bLessThan.toDouble).floor.toInt
  }

  def reservoirSample[T: ClassTag](
                                            input: Iterator[T],
                                            k: Int,
                                            seed: Long)
  : (Array[T]) = {
    val stSeed = seed.toString();

    val reservoir = new Array[T](k)
    // Put the first k elements in the reservoir.
    var i = 0
    while (i < k && input.hasNext) {
      val item = input.next()
      reservoir(i) = item
      i += 1
    }

    // If we have consumed all the elements, return them. Otherwise do the replacement.
    if (i < k) {
      // If input size < k, trim the array to return only an array of input size.
      val trimReservoir = new Array[T](i)
      System.arraycopy(reservoir, 0, trimReservoir, 0, i)
      trimReservoir
    } else {
      // If input size > k, continue the sampling process.
      while (input.hasNext) {
        val item = input.next()
        val stHash =               // It bugs me to not put a separator.  But if a tree falls in the forest
          stSeed + item.toString() // and noone is there to hear it, does it really make a sound?
        val replacementIndex = randMurmur(stHash, i)
        if (replacementIndex < k) {
          reservoir(replacementIndex) = item
        }
        i += 1
      }
      reservoir
    }
  }
}
