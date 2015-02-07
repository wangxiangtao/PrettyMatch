package com.prettymatch

import java.io.File
import com.prettymatch.cleaner.GenericCleaner
import com.prettymatch.learner.DataProcessor
import com.prettymatch.learner.Learner
import com.prettymatch.stringmetric.StringMetric
import weka.classifiers.functions.SMO
import weka.classifiers.meta.CVParameterSelection
import com.prettymatch.mlpscala.MLPnetwork
class PrettyMatch {

  val cleaner = new GenericCleaner
  val learner =  new Learner("prettymatch.data","resources/prettymatch.model")

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
  
  def predict(name1: String, name2: String) : Double = {
	 val features = getSimilityScores(name1, name2)
	 val result = learner.predict(features)
	 result
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
//	  val ps = new CVParameterSelection()
//      ps.setClassifier(new SMO())
//      ps.setNumFolds(10)
//      ps.addCVParameter("C 1 8 8")
//      ps.buildClassifier(learner.reader)
	  val mlp = new MLPnetwork 
//    mlp.setLearningRate(0.2)
//    mlp.setMomentum(0.2)
//    mlp.setTrainingTime(1000)
//	  smo.setOptions(ps.getBestClassifierOptions())
	  learner.currentModel = mlp
	  learner.evaluate
    val model = learner.training
    learner.saveModel(model)
  }
  
}
