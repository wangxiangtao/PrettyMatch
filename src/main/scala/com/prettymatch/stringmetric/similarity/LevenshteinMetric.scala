package com.prettymatch.stringmetric.similarity

import com.prettymatch.stringmetric._

case object LevenshteinMetric extends StringMetric[Double] {
	def compare(a: Array[Char], b: Array[Char]): Option[Double] =
		if (a.length == 0 || b.length == 0) None
		else if (a.sameElements(b)) Some(0)
		else Some(levenshteinByPercentage(a, b))

	override def compare(a: String, b: String): Option[Double] = compare(a.toCharArray, b.toCharArray)

	private val levenshtein: (CompareTuple[Char] => Int) = (ct) => {
		val m = Array.fill[Int](ct._1.length + 1, ct._2.length + 1)(-1)

		def distance(t: (Int, Int)): Int = t match {
			case (r, 0) => r
			case (0, c) => c
			case (r, c) if m(r)(c) != -1 => m(r)(c)
			case (r, c) => {
				val min =
					if (ct._1(r - 1) == ct._2(c - 1)) distance(r - 1, c - 1)
					else math.min(
						math.min(
							distance(r - 1, c) + 1, // Delete (left).
							distance(r, c - 1) + 1 // Insert (up).
						),
						distance(r - 1, c - 1) + 1 // Substitute (left-up).
					)

				m(r)(c) = min
				min
			}
		}

		distance(ct._1.length, ct._2.length)
	}
	
	def levenshteinByPercentage(a:Array[Char],b:Array[Char]): Double = {
	   val distance = levenshtein(a, b).toDouble
	   (1 - distance/math.max(a.length, b.length).toDouble) 
	}
}
