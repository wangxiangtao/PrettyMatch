package com.prettymatch.stringmetric.similarity

import com.prettymatch.stringmetric._
import scala.math

final case class NGramWordMetric(n: Int) extends StringMetric[Double] {

	override def compare(a: String, b: String): Option[Double] = {
	    val ngramA = a.split("\\s+").toList.sliding(n).toList.union(a.split("\\s+").toList)
	    val ngramB = b.split("\\s+").toList.sliding(n).toList.union(b.split("\\s+").toList)
	    val score = ngramA.intersect(ngramB).size.toDouble/Math.max(ngramA.size, ngramB.size)
	    Some(score)
	}

}
