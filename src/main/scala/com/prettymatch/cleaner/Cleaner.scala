package com.prettymatch.cleaner
import scala.collection.mutable.Set

abstract class Cleaner {
  
   def clean(dataset:List[(String,String,Boolean)]) :List[(String,String,Boolean)] 
   
}