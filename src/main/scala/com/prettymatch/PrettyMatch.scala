package com.prettymatch
import scala.collection.JavaConversions._
import java.io.File
import com.prettymatch.cleaner.GenericCleaner
import com.prettymatch.learner.DataProcessor
import com.prettymatch.learner.Learner
import com.prettymatch.stringmetric.StringMetric
import com.prettymatch.mlpscala.MLPnetwork
import scala.collection.mutable.Set
import com.prettymatch.libliner.LibLINEAR

class PrettyMatch {

  val cleaner = new GenericCleaner
  val learner =  new Learner("prettymatch.data","prettymatch.model")

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
     featureMap.put("Soundex",     StringMetric.compareWithSoundex(a, b).get.toString)  
     featureMap
  }
  
  def predict(name1: String, name2: String) : Double = {
   val features = getSimilityScores(name1, name2)
   val result = learner.predict(features)
   result
  }
  
  def trainInJava(allData : java.util.List[String],matchedData : java.util.List[String]) = {
     val data = scala.collection.mutable.ListBuffer[(String, String, Boolean)]()
     val matcheddataSet = matchedData.toSet
     var i = 0
     println("start training.....")
     (1 to allData.length combinations 2).foreach( f =>{
        val pair1 = allData.get(f.get(0)-1)+"||"+allData.get(f.get(1)-1)
        val pair2 = allData.get(f.get(1)-1)+"||"+allData.get(f.get(0)-1)

        if( matcheddataSet.contains(pair1) || matcheddataSet.contains(pair2))
        {
          
          data += ((allData.get(f.get(0)-1), allData.get(f.get(1)-1), true))
          i +=1
          matchedData.remove(pair1)
          matchedData.remove(pair2)
        }
        else  {
          data += ((allData.get(f.get(0)-1), allData.get(f.get(1)-1), false))
        }
     })

     train(data.toList,false)
     
    val pm = new PrettyMatch
    val pw1 = new java.io.PrintWriter(new File("true_positive.txt"))
    val pw2 = new java.io.PrintWriter(new File("true_negative.txt"))
    val pw3 = new java.io.PrintWriter(new File("false_positive.txt"))
    val pw4 = new java.io.PrintWriter(new File("false_negative.txt"))
    var right = 0
    var wrong = 0
    var true_positive = 0
    var true_negative = 0
    var false_positive = 0
    var false_negative = 0

     data.foreach(f => {
        val result = if (pm.predict(f._1,f._2) > 0.5 ) true else false
        if(result == f._3) right += 1
        else wrong += 1
        if( pm.predict(f._1,f._2) > 0.5 && f._3 == true) {
          pw1.println(f._1+" || "+f._2)
          true_positive +=1
        }
        if( pm.predict(f._1,f._2) < 0.5 && f._3 == false) {
          pw2.println(f._1+" || "+f._2)
          true_negative +=1
        }
        if( pm.predict(f._1,f._2) > 0.5 && f._3 == false){
           pw3.println(f._1+" || "+f._2)
           false_positive +=1
        }
        if( pm.predict(f._1,f._2) < 0.5 && f._3 == true){
           pw4.println(f._1+" || "+f._2)
           false_negative +=1
        }
     })
     pw1.close()
     pw2.close()
     pw3.close()
     pw4.close()
     println("--------------Confusion Matrix-------------")
     println("true_positive:"+true_positive)
     println("true_negative:"+true_negative)
     println("false_positive:"+false_positive)
     println("false_negative:"+false_negative)
     println("Accuracy:"+right.toDouble/(right+wrong).toDouble)

  }
  def combinations[T](k: Int, list: List[T]) : List[List[T]] =
        list match {
          case Nil => Nil
          case head :: xs =>
           if (k <= 0 || k > list.length) {
              Nil
            } else if (k == 1) {
              list.map(List(_))
            } else {
              combinations(k-1, xs).map(head :: _) ::: combinations(k, xs)
            }
    }
  
  def train(rawdata:List[(String,String,Boolean)], autoclean: Boolean){
      var data = rawdata
      if(autoclean){
         data = cleaner.clean(data)
      }
      
    val allFeatureMaps = data.filter(p => !p._1.equals("") && !p._2.equals("")).map(f => {
        val featureMap = getSimilityScores(f._1,f._2)
        if(f._3) featureMap.put("class", "1")
        else featureMap.put("class", "0")
        featureMap
      }).toList 
    DataProcessor.buildTrainingFile(new File("prettymatch.data"), allFeatureMaps)
    val mlp = new MLPnetwork 
    learner.currentModel = mlp
    val model = learner.training
    learner.saveModel(model)
  }
  
}
