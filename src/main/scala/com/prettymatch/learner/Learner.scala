package com.prettymatch.learner

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.PrintWriter
import scala.collection.JavaConversions._
import scala.collection.mutable.LinkedHashMap
import weka.classifiers.Classifier
import weka.classifiers.Evaluation
import weka.core.Attribute
import weka.core.Instance
import weka.core.Instances
import weka.core.SparseInstance
import java.util.Random

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
  
  
  def predict(feature : LinkedHashMap[String,String]): Double = {
    if( trainedModel.isInstanceOf[TrainedModel]) {
        val inst = buildInstance(feature, trainedModel)
	    val pdist = trainedModel.cl.distributionForInstance(inst)
//	    println(pdist.toList)
	    pdist.zipWithIndex.maxBy(_._1)._2  // _1 is e, _2 is index,  so will return index 
    }
    else return -1
  }
  
   def predictWithProbaility(feature : LinkedHashMap[String,String]): Double = {
    if( trainedModel.isInstanceOf[TrainedModel]) {
        val inst = buildInstance(feature, trainedModel)
	    val pdist = trainedModel.cl.distributionForInstance(inst)
	    pdist.toList(1)
    }
    else return -1
  }
   
  def buildInstance(feature : LinkedHashMap[String,String], model: TrainedModel): Instance = {
    val inst = new SparseInstance(model.vo.size)
    model.vo.foreach{ case(index,word) => {
//       println(word+"-"+feature.getOrElse(word, "0"))
       inst.setValue(index, feature.getOrElse(word, "0").toDouble)
    }}  
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
     val writer = new PrintWriter(new FileWriter(new File("Evaluation.model")))
      writer.println(eval.precision(1))
       writer.println(eval.recall(1))
    writer.println(eval.toClassDetailsString)
     writer.close()
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
  def saveModel(model: TrainedModel) = {
    weka.core.SerializationHelper.write(modelPath, model)
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