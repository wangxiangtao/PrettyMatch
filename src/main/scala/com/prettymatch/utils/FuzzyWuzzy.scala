package com.prettymatch.utils

import com.google.common.collect.Sets
import java.util.HashSet
import java.util.StringTokenizer
import scala.collection.JavaConversions._
import org.apache.commons.lang3.StringUtils
import java.text.StringCharacterIterator
import java.text.CharacterIterator

object FuzzyWuzzy {

  def main(args: Array[String]): Unit = {
		println(getRatio("web services as a software", "software as a services", false));
		println(getRatio("CSK vs RCB", "RCB vs CSK", false));
		println(getRatio("software-as-a-service", "software as a service", false));
		println(getRatio("Microsoft's deal with skype", "Microsoft skype deal", false));
		println(getRatio("apple is good", "Google is best apple is", false));
  }
    def  getRatio( a:String,  b:String,  debug:Boolean): Int = {
		var s1 = a
		var s2 = b
		if (s1.length() >= s2.length()) {		
			// We need to swap s1 and s2		
			var temp = s2;
			s2 = s1;
			s1 = temp;			
		}

		// Get alpha numeric characters
		
		s1 = escapeString(s1);
		s2 = escapeString(s2);
		
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();
		
		
		val set1 = new HashSet[String]();
		val set2 = new HashSet[String]();
		
		//split the string by space and store words in sets
		val st1 = new StringTokenizer(s1);		
		while (st1.hasMoreTokens()) {
			set1.add(st1.nextToken());
		}

		val st2 = new StringTokenizer(s2);		
		while (st2.hasMoreTokens()) {
			set2.add(st2.nextToken());
		}
		
		val intersection = Sets.intersection(set1, set2);
		
		val sortedIntersection = Sets.newTreeSet(intersection);

//		if (debug) {
//		    print("Sorted intersection --> ");
//			for (String s:sortedIntersection) 
//				print(s + " ");
//		}
		
		// Find out difference of sets set1 and intersection of set1,set2
		
		val restOfSet1 = Sets.symmetricDifference(set1, intersection);
		
		// Sort it
		
		val sortedRestOfSet1 = Sets.newTreeSet(restOfSet1);
		
		val restOfSet2 = Sets.symmetricDifference(set2, intersection);
		val sortedRestOfSet2 = Sets.newTreeSet(restOfSet2);
		
//		if (debug) {
//		print("\nSorted rest of 1 --> ");
//		for (String s:sortedRestOfSet1) 
//			print(s + " ");
//		
//		print("\nSorted rest of 2 -->");
//		for (String s:sortedRestOfSet2) 
//			print(s + " ");
//		}
		
		var t0 = "";
		var t1 = "";
		var t2 = "";
		sortedIntersection.foreach(s => {
		  t0 = t0 + " " + s		
		})
		t0 = t0.trim()
		
		val setT1 = Sets.union(sortedIntersection, sortedRestOfSet1);
		setT1.foreach(s =>{
		  	t1 = t1 + " " + s;			
		})
		t1 = t1.trim();
		
		val setT2 = Sets.union(intersection, sortedRestOfSet2);	
		setT2.foreach(s =>{
		  t2 = t2 + " " + s;			
		})
		
		t2 = t2.trim();
		
		
		var amt1 = calculateLevensteinDistance(t0, t1);
		var amt2 = calculateLevensteinDistance(t0, t2);
		var amt3 = calculateLevensteinDistance(t1, t2);
		
//		if (debug) {
//			println();
//			println("t0 = " + t0 + " --> " + amt1);
//			println("t1 = " + t1 + " --> " + amt2);
//			println("t2 = " + t2 + " --> " + amt3);
//			println();
//		}
		
		
		var finalScore = Math.max(Math.max(amt1, amt2), amt3)
		return finalScore	
	}
	
	def calculateLevensteinDistance( s1:String,  s2:String): Int = {
		var distance = StringUtils.getLevenshteinDistance(s1, s2).toDouble
		var ratio = (distance) / (Math.max(s1.length(), s2.length())).toDouble
		return 100 - (ratio*100).intValue()		
	}
	
	def  escapeString( t: String) :String = {
		var token = t
		var s = new StringBuffer(t.length());

		var it = new StringCharacterIterator(t);
		var ch = it.first();
		while ( ch != CharacterIterator.DONE) {
			ch match {
			// '-,)(!`\":/][?;~><
			case '\''=> s.append(" ")
			case '/'=>s.append(" ")
			case '\\'=>s.append(" ")
			case '-'=>s.append(" ")
			case ','=>s.append(" ")
			case ')'=>s.append(" ")
			case '('=>s.append(" ")
			case '!'=>s.append(" ")
			case '`'=>s.append(" ")
			case '\"'=>s.append(" ")
			case ':'=>s.append(" ")
			case ']'=>s.append(" ")
			case '['=>s.append(" ")
			case '?'=>s.append(" ")
			case ';'=>s.append(" ")
			case '~'=>s.append(" ")
			case '<'=>s.append(" ")
			case '>'=> s.append(" ")
			case _ => s.append(ch)
			}
			ch = it.next()
		}

		token = s.toString();
		return token;
	}
}