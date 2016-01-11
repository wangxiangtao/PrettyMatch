package com.prettymatch.stringmetric.similarity

import com.prettymatch.stringmetric._
import com.prettymatch.utils.FuzzyWuzzy

case object FuzzyWuzzyMetric extends StringMetric[Double] {

	override def compare(a: String, b: String): Option[Double] = {
	  val score = FuzzyWuzzy.getRatio(a, b, false).toDouble/100d
	  Some(score)
	}

}
