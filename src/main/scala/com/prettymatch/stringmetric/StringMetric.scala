package com.prettymatch.stringmetric

import com.prettymatch.stringmetric.similarity.DiceSorensenMetric
import com.prettymatch.stringmetric.similarity.FuzzyWuzzyMetric
import com.prettymatch.stringmetric.similarity.HammingMetric
import com.prettymatch.stringmetric.similarity.JaccardMetric
import com.prettymatch.stringmetric.similarity.JaroMetric
import com.prettymatch.stringmetric.similarity.JaroWinklerMetric
import com.prettymatch.stringmetric.similarity.LevenshteinMetric
import com.prettymatch.stringmetric.similarity.NGramLetterMetric
import com.prettymatch.stringmetric.similarity.NGramWordMetric
import com.prettymatch.stringmetric.similarity.OverlapMetric
import com.prettymatch.stringmetric.similarity.SoundexMetric
import com.prettymatch.stringmetric.similarity.WeightedLevenshteinMetric
import com.prettymatch.stringmetric.similarity.WordLevelLevenshteinMetric
import com.prettymatch.stringmetric.similarity.MinHashMetric

trait StringMetric[A] {
	def compare(a: String, b: String): Option[A]
}

object StringMetric {

	def compareWithDiceSorensen(n: Int)(a: String, b: String) = DiceSorensenMetric(n).compare(a, b)

	def compareWithHamming(a: String, b: String) = HammingMetric.compare(a, b)

	def compareWithJaccard(n: Int)(a: String, b: String) = JaccardMetric(n).compare(a, b)

	def compareWithJaro(a: String, b: String) = JaroMetric.compare(a, b)

	def compareWithJaroWinkler(a: String, b: String) = JaroWinklerMetric.compare(a, b)

	def compareWithLevenshtein(a: String, b: String) = LevenshteinMetric.compare(a, b)
	
	def compareWithFuzzyWuzzy(a: String, b: String) = FuzzyWuzzyMetric.compare(a, b)

	def compareWithNGramLetter(n: Int)(a: String, b: String) = NGramLetterMetric(n).compare(a, b)
	
	def compareWithNGramWord(n: Int)(a: String, b: String) = NGramWordMetric(n).compare(a, b)

	def compareWithOverlap(n: Int)(a: String, b: String) = OverlapMetric(n).compare(a, b)
	
	def compareWithSoundex(a: String, b: String) = SoundexMetric.compare(a, b)
                
	def compareWithWordLevelLevenshtein(a: String, b: String) = WordLevelLevenshteinMetric.compare(a, b)
	
	def compareWithSignatureHash(a: String, b: String) = MinHashMetric(3).compare(a, b)
	
	def compareWithWeightedLevenshtein(delete: BigDecimal, insert: BigDecimal, substitute: BigDecimal)(a: String, b: String) 
										= WeightedLevenshteinMetric(delete, insert, substitute).compare(a, b)
}
