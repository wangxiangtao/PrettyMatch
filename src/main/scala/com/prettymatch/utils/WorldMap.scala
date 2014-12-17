package com.prettymatch.utils

import org.ahocorasick.trie.Trie
import scala.io.Source
import java.util.regex.Pattern
import org.ahocorasick.trie.Emit

object WorldMap{
  
  val country = new Trie().removeOverlaps().onlyWholeWords().caseInsensitive()
  val city = new Trie().removeOverlaps().onlyWholeWords().caseInsensitive()
  val locationMarks = new Trie().removeOverlaps().onlyWholeWords().caseInsensitive()
  val stopWord = "January,Jan,February,Feb,March,Mar,April,Apr,May,June,Jun,July,Jul,Central,Center"+
	     "August,Aug,September,Sep,October,Oct,November,Nov,December,Dec,"+
	     "Telephone,Fax,Tel,Main,Centre,Message,Mobile,General,Best,Call,Case,Mon,Power," +
         "Kim,Sales,Group,Coast,Supply,Line,Green" +
	     ",Media,Officer,Asia,United,Area,Dollar,Sun,Of,Prime,Dept,More,Pool,Pacific,Lee,Purchase,Square,Globe"+ 
	     ",Register,Asset,Export,Hotel,Cafe,Golf,Site,Best,America,Tower,Login,Park,Middle,"+
	     "Street,Forum,Real,Grove,Chan,Lines,Home,Billing,Sells,Content,North,West,Leader";
  val locationMarkDict = "road,way,street,tower,towers,building,floor,town,district,str,ave,address,"+
		  			   "block,blk,st.,dr,centre,temple,place,room,area,sector,lane,estate,"+ 
		  			   "square,park,plaza,drive,house,avenue,level,no.,opp.,central,unit,units,bldg,ayer"
  val stopWordSet =  stopWord.toLowerCase().split(",").toSet[String] 	
  val countryMap = scala.collection.mutable.Map[String,String]()
  val cityMap = scala.collection.mutable.Map[String,String]()

  for(line <- Source.fromFile("resources/worldmap.txt").getLines()){
       val tokens = line.split(",")
       if(tokens(3).equals("CO") && !stopWordSet.contains(tokens(2).toLowerCase())){
 		  country.addKeyword(tokens(2).toLowerCase())
 		  countryMap.put(tokens(2).toLowerCase(), tokens(1))
       }
	   else if(!stopWordSet.contains(tokens(2).toLowerCase())){
	      city.addKeyword(tokens(2).toLowerCase())
	      cityMap.put(tokens(2).toLowerCase(), tokens(1).split("-")(0))
	   }
  }
  countryMap.put("usa", "")
  countryMap.put("us", "")

  
  locationMarkDict.split(",").foreach(f => {
      locationMarks.addKeyword(f)
  })
    
  def extractCountry(s: String) = {
     country.parseText(s).toArray().toList.asInstanceOf[List[Emit]].map(f => f.getKeyword())
  }
  def extractCity(s: String) = {
     city.parseText(s).toArray().toList.asInstanceOf[List[Emit]].map(f => f.getKeyword())
  }
  def extractCity(s: String, country: List[String]) = {
     val countryCode = country.map(f => countryMap.get(f).get).toSet
     city.parseText(s).toArray().toList.asInstanceOf[List[Emit]]
     .map(f => f.getKeyword()).filter(p => countryCode.contains(cityMap.get(p).get))
  }
  
  def extractLocationMarks(s: String) = {
     locationMarks.parseText(s).toArray().toList.asInstanceOf[List[Emit]].map(f => f.getKeyword())
  }
  
}