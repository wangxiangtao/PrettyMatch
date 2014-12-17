package com.prettymatch.stringmetric.similarity

import com.prettymatch.stringmetric._
import com.prettymatch.utils.SoundexAlgorithm

case object SoundexMetric extends StringMetric[Double] {

	override def compare(a: String, b: String): Option[Double] = {
	  val string1 = SoundexAlgorithm.compute(a).getOrElse("")
	  val string2 = SoundexAlgorithm.compute(b).getOrElse("")
	  val score =   JaroWinklerMetric.compare(string1, string2).getOrElse(0.5d)
	  Some(score)
	}
}
