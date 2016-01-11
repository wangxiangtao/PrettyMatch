package com.prettymatch.cleaner

import com.prettymatch.utils.WorldMap

class GenericCleaner extends Cleaner {
  
  override def clean (dataset:List[(String,String,Boolean)]) : List[(String,String,Boolean)] = {  
      val nonCommonWords = scala.collection.mutable.Set[String]() 
      var commonWords = dataset.map(r => {
		 var tokenA = r._1.split("\\s+").toList
		 var tokenB = r._2.split("\\s+").toList
		 val diffwords = tokenA.diff(tokenB) ++ tokenB.diff(tokenA).toList
		 if(r._3 && diffwords.size == 1 ){
		     diffwords(0)
		 } 
		 else if(!r._3 && diffwords.size == 1 ){
		    nonCommonWords.add(diffwords(0)) 
		    diffwords(0)
		 } 
		 else ""
      }).toSet.filter(p => !p.equals("") && !nonCommonWords.contains(p))
      dataset.map(f => {
         ( removeCommonWords(f._1,commonWords) , removeCommonWords(f._2,commonWords) , f._3 )
      })
  }  
   
  def removeCommonWords(s:String,commonWords:Set[String]) = {
     s.split("\\s+").filter(p => !commonWords.contains(p)).mkString(" ")
  }
  
}