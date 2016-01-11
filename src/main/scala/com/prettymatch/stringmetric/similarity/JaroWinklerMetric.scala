package com.prettymatch.stringmetric.similarity

import com.prettymatch.stringmetric._

case object JaroWinklerMetric extends StringMetric[Double] {
	 def compare(a: Array[Char], b: Array[Char]): Option[Double] =
		JaroMetric.compare(a, b).map {
			case 0d => 0d
			case 1d => 1d
			case jaro => {
				val prefix = a.zip(b).takeWhile(t => t._1 == t._2)
				jaro + ((if (prefix.length <= 4) prefix.length else 4) * 0.1d * (1 - jaro))
			}
		}

	override def compare(a: String, b: String): Option[Double] = compare(a.toCharArray, b.toCharArray)
}
