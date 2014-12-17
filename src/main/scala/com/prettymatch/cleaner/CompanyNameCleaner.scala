package com.prettymatch.cleaner

import scala.collection.mutable.Set
import com.prettymatch.utils.WorldMap

class CompanyNameCleaner extends Cleaner {
  
  override def clean (dataset:List[(String,String,Boolean)]) : List[(String,String,Boolean)] = {  
      dataset.map(r => {
		 var A = removeStopword(removeSpecialCharacter(r._1))
		 var B = removeStopword(removeSpecialCharacter(r._2))
		 (A,B,r._3)
      })
  }  
   
  def removeSpecialCharacter(s:String) = s.replaceAll("[(),'-.^:,]","")
    
  def removeStopword(s:String) = {
     val commonStrings = "and,&,manufacturing,s,products,int,mfg,finance,incorporated,personnel,asia,inc,services,llc,management,limited,ltd,pte,international,intl,company,group,enterprises,trust,corporate,corporation,corp,co,grp,holdings,global,technology,tech,hldgsltd,hldg,hldgs,private".split(",").toList
     s.toLowerCase().split("\\s+").filter(p => !commonStrings.contains(p) && !WorldMap.countryMap.contains(p) ).mkString(" ")
  }
  
}