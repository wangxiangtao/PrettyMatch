package com.prettymatch

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import com.prettymatch.cleaner.GenericCleaner
import com.prettymatch.learner.DataProcessor
import com.prettymatch.learner.Learner
import com.prettymatch.stringmetric.StringMetric
import weka.classifiers.functions.SMO
import weka.classifiers.meta.CVParameterSelection
import weka.classifiers.trees.RandomForest
import weka.core.Instances
import weka.core.Utils
class PrettyMatch {

  val cleaner = new GenericCleaner
  
  def getSimilityScores (a:String,b:String) = {
     val featureMap = scala.collection.mutable.LinkedHashMap[String,String]()
     featureMap.put("Jaccard",     StringMetric.compareWithJaccard(1)(a, b).get.toString)
	 featureMap.put("Jaro",        StringMetric.compareWithJaro(a, b).get.toString)
     featureMap.put("JaroWinkler", StringMetric.compareWithJaroWinkler(a, b).get.toString) 	
     featureMap.put("NGramLetter", StringMetric.compareWithNGramLetter(2)(a, b).get.toString) 	
     featureMap.put("NGramWord",   StringMetric.compareWithNGramLetter(2)(a, b).get.toString) 	
     featureMap.put("Overlap",     StringMetric.compareWithOverlap(2)(a, b).get.toString) 	
     featureMap.put("Levenshtein", StringMetric.compareWithLevenshtein(a, b).get.toString) 	
     featureMap.put("FuzzyWuzzy",  StringMetric.compareWithFuzzyWuzzy(a, b).get.toString) 	
     featureMap.put("Soundex", 	   StringMetric.compareWithSoundex(a, b).get.toString) 	
     featureMap
  }
  
  def run(rawdata:List[(String,String,Boolean)], autoclean: Boolean){
      var data = rawdata
      if(autoclean){
         data = cleaner.clean(data)
      }
      
	  val allFeatureMaps = data.map(f => {
		    val featureMap = getSimilityScores(f._1,f._2)
		    if(f._3) featureMap.put("class", "1")
		    else featureMap.put("class", "0")
		    featureMap
		  }).toList	
	  DataProcessor.buildTrainingFile(new File("prettymatch.data"), allFeatureMaps)
	  val learner =  new Learner("prettymatch.data","prettymatch.model")
	
	  val ps = new CVParameterSelection()
      ps.setClassifier(new RandomForest())
      ps.setNumFolds(10)
      ps.addCVParameter("I 10 50 5")
      ps.buildClassifier(learner.reader)
	  val rf = new RandomForest
	  rf.setOptions(ps.getBestClassifierOptions())
	  learner.currentModel = rf
	  learner.evaluate
  }
  
}