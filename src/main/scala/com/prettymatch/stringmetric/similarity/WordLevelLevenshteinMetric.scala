package com.prettymatch.stringmetric.similarity

import com.prettymatch.stringmetric._
import scala.math
import scala.math.min

final case object WordLevelLevenshteinMetric extends StringMetric[Double] {

	override def compare(a: String, b: String): Option[Double] = {
	    val distance = editDistance(a.split("\\s+"),b.split("\\s+")).toDouble
	    val score = (1 - distance/math.max(a.length, b.length).toDouble) 
	    Some(score)
	}
	
	
	def editDistance(words1: Array[String], words2: Array[String]): Int = {
	    val len1 = words1.length
	    val len2 = words2.length
	    val distances = Array.ofDim[Int](len1+1, len2+1)
	    for (i <- 0 to len1;
	         j <- 0 to len2) {
	      if (j == 0) distances(i)(j) = i
	      else if (i == 0) distances(i)(j) = j
	      else distances(i)(j) = 0
	    }
	    for (i <- 1 to len1;
	         j <- 1 to len2) {
	      distances(i)(j) = if (words1(i-1).equals(words2(j-1))) 
	          distances(i-1)(j-1)
	        else minimum(
	          distances(i-1)(j) + 1,  // deletion
	          distances(i)(j-1) + 1,  // insertion
	          distances(i-1)(j-1) + 1 // substitution
	        )
	    }
	    distances(len1)(len2)
	}
	
	def minimum(i1: Int, i2: Int, i3: Int): Int = min(min(i1, i2), i3)
}
