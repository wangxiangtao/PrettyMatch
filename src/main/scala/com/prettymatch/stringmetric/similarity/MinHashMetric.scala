package com.prettymatch.stringmetric.similarity

import com.prettymatch.stringmetric._
import scala.math
import scala.math.min
import scala.util.hashing.MurmurHash3
import scala.util.Random
import com.aliasi.tokenizer.RegExTokenizerFactory
import com.aliasi.tokenizer.TokenNGramTokenizerFactory

final case class MinHashMetric(size: Int) extends StringMetric[Double] {

	override def compare(a: String, b: String): Option[Double] = {
	    val s1 = signatures(size,a).toList
	    val s2 = signatures(size,b).toList
	    val score = s1.intersect(s2).size.toDouble/Math.max(s1.size, s2.size)
	    Some(score)
	}
	  
	val Puncts = """[!"#$%&\'()*+,./:;<=>?@[\\]^_`{|}~]""".r
	  
	val factory = new TokenNGramTokenizerFactory(new RegExTokenizerFactory("\\S+"), size, size)

    def normalize(str: String): String = Puncts.replaceAllIn(str.toLowerCase, " ").replaceAll("\\s+", " ")
      
	def words(str: String): Array[String] = normalize(str).split(" ")
  
	def firstWord(str: String): String = words(str).head
	  
	def numWords(str: String): Int = words(str).size
	  
	def ngrams(str: String): Array[String] = {
		if (numWords(str) < size) Array(str)
		  else {
	      val normStr = normalize(str)
	      val tokenizer = factory.tokenizer(
	        normStr.toCharArray(), 0, normStr.length())
	      tokenizer.tokenize().toArray
		}
	}
	
	def hash(str: String): Int = MurmurHash3.stringHash(str, seed=42)
	  
	def minHash(hashes: Array[Int]): Int = {
	    hashes.toArray.sortWith(_ > _).head
	}
	  
	def signatures(size: Int, str: String): Array[Int] = {
	    Random.setSeed(42L)
	    Array.fill(size)(Random.nextInt).map(mask => 
	      minHash(ngrams(str).map(shingle => hash(shingle) ^ mask)))
	}
	
	def minimum(i1: Int, i2: Int, i3: Int): Int = min(min(i1, i2), i3)
}
