package com.prettymatch.learner

import java.io.PrintWriter
import java.io.File
import java.io.FileWriter
import scala.collection.Map
import scala.collection.mutable.LinkedHashMap

object DataProcessor {
  
  def buildTrainingFile(arffout: File , data: List[LinkedHashMap[String,String]]) = {
        val w = openWriter(arffout)
	    w.println("@relation prettymatch")
	    w.println()
	    data(0).keySet.filter(p => !p.equals("class")).foreach(f => {
	       w.println("@attribute '"+f+"' numeric ")
	    })
	    w.println("@attribute 'class' {0,1}")  
	    w.println()
	    w.println("@data")	    
        data.foreach(r => {
          val sr = r.values.mkString(",").split(",").toList.zipWithIndex.map{ case (e, i)  =>  (i)+" "+e }
      				.filter(p => !p.split(" ")(1).equals("0") && !p.split(" ")(1).equals("0.0")).mkString(",")
          if(!sr.equals("") && !sr.equals("0 1"))
             w.println("{"+sr+"}")
        })
        closeWriter(w)
   }
  
   def openWriter(f: File) = new PrintWriter(new FileWriter(f))
  
   def closeWriter(w: PrintWriter) = {
	     w.flush()
	     w.close()
   }
}