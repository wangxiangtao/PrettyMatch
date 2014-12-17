package com.prettymatch.learner

import java.io.BufferedReader
import weka.classifiers.functions.SMO
import weka.core.Instances
import weka.core.SparseInstance
import weka.classifiers.Evaluation
import java.io.File
import weka.classifiers.Classifier
import weka.core.Attribute
import weka.core.Instance
import java.io.FileReader
import weka.core.Debug.Random

case class TrainedModel(
    cl: Classifier, 
    in: Instances, 
    vo: Map[Int,String])
    
class Learner (arffIn: String = null, modelPath: String){
  
  var currentModel : Classifier = null
  val trainedModel  = readModel(modelPath).asInstanceOf[TrainedModel] 
  var invertVocab = scala.collection.immutable.Map[String,Int]()
  if(trainedModel != null)
     invertVocab = trainedModel.vo.map{_.swap}   
  val filename = arffIn
  def training = trainModel(new File(arffIn),currentModel)

  
  def trainModel(arff: File, classifier: Classifier) = {
    val _instances = reader
    classifier.buildClassifier(_instances)
    TrainedModel(classifier , _instances, readAt(_instances))
  }
  
  
  def buildInstance(input: (List[String],String,String) , model: TrainedModel) = {
	    val inst = new SparseInstance(model.vo.size)
        (input._1.groupBy ( t => t) mapValues ( t => {
             (((t.toString.split("--", -1).length.toFloat-1.toFloat)/2.toFloat+1)/5.toFloat).toString
        })).foreach(keyVal => {
          val index = invertVocab.get(keyVal._1).getOrElse(-1)
          if(index != -1){
        	  inst.setValue(index, keyVal._2.toDouble)
          }
        })
	    inst.setDataset(model.in)
	    inst
  }
  
  def evaluate = {
    val eval = new Evaluation(reader)
	eval.crossValidateModel(currentModel, reader, 10, new Random(1))
 	println("------Confusion Matric------\n")
	println("           TP      FP")
	print("Unique     "+eval.confusionMatrix()(0)(0)+"  ")
	println(eval.confusionMatrix()(0)(1))
	print("Duplicated "+eval.confusionMatrix()(1)(1)+"  ")
	println(eval.confusionMatrix()(1)(0))
  }
  
  def readModel(path: String) = {
    try{
      weka.core.SerializationHelper.read(path).asInstanceOf[TrainedModel]
    }
    catch {
        case e: Exception => {
          null
        }
    }
  }
  
  def reader = {
    val reader = new BufferedReader(new FileReader(new File(arffIn)))
    val _instances = new Instances(reader)
     reader.close()
    _instances.setClassIndex(_instances.numAttributes()-1)
    _instances
  }
  
  def readAt(_instances: Instances) = {
    val vo = scala.collection.mutable.Map[Int,String]()
    val e = _instances.enumerateAttributes()
    while (e.hasMoreElements()) {
      val attrib = e.nextElement().asInstanceOf[Attribute]
      if (! "class".equals(attrib.name())) 
        vo += (( attrib.index(), attrib.name() ))
    }
    vo.toMap
  }
}