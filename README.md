PrettyMatch
===========
PrettyMatch is a String Matching library written in Scala. 
It has sophisticated comparators that perform approximate string matching, measurement of string similarity/distance, index of word pronunciation,semantic distance and sounds-like comparisons. Using an supervised Cleaner and Marcher model PrettyMatch can handle noisy data with good accuracy.

## Basic algorithms
Useful for approximate string matching and measurement of string distance. All metrics calculate the similarity of two strings as a double with a value between 0 and 1. A value of 0 being completely different and a value of 1 being completely similar.

* __[FuzzyWuzzy](http://chairnerd.seatgeek.com/fuzzywuzzy-fuzzy-string-matching-in-python/)__ 
* __[Dice / Sorensen](http://en.wikipedia.org/wiki/Dice%27s_coefficient)__ 
* __[Jaccard](http://en.wikipedia.org/wiki/Jaccard_index)__ 
* __[Jaro-Winkler](http://en.wikipedia.org/wiki/Jaro-Winkler_distance)__ 
* __[Levenshtein](http://en.wikipedia.org/wiki/Levenshtein_distance)__ 
* __[Word-level Levenshtein](http://en.wikipedia.org/wiki/Levenshtein_distance)__ 
* __[N-Gram](http://en.wikipedia.org/wiki/N-gram)__
* __[Word-level N-Gram](http://en.wikipedia.org/wiki/N-gram)__
* __[Overlap](http://en.wikipedia.org/wiki/Overlap_coefficient)__ 
* __[Soundex](http://en.wikipedia.org/wiki/Soundex)__ 
* __[Deep Learning-Word2Vec](https://code.google.com/p/word2vec/)__
* __[MinHash](http://en.wikipedia.org/wiki/MinHash)__ 
```scala
StringMetric.compareWithJaccard(1)("google crayon", "goodbye crayon")       //0.6875
StringMetric.compareWithJaro("google crayon", "goodbye crayon")             //0.7863
StringMetric.compareWithJaroWinkler("google crayon", "goodbye crayon") 	    //0.8504
StringMetric.compareWithNGramLetter(2)("google crayon", "goodbye crayon")   //0.6923
StringMetric.compareWithNGramWord(2)("google crayon", "goodbye crayon")     //0.3333
StringMetric.compareWithOverlap(2)("google crayon", "goodbye crayon")       //0.7500
StringMetric.compareWithLevenshtein("google crayon", "goodbye crayon")	    //0.7857
StringMetric.compareWithFuzzyWuzzy("google crayon", "goodbye crayon")       //0.7900
StringMetric.compareWithSoundex("google crayon", "goodbye crayon")          //0.7000
StringMetric.compareWithWordLevenshtein("google crayon", "goodbye crayon")  //0.5000
StringMetric.compareWithWord2vec("google crayon", "goodbye crayon")         //0.5920
StringMetric.compareWithMinHash("google crayon", "goodbye crayon")          //0.3333
```

You possibly need to add new comparators by easliy extend StringMetric class and implement the compare() method 

##  Advanced algorithms
Advanced algorithms can automatically clean and match strings without any customized code and config. it contains 

* __supervised Cleaner__  based on training data, it automatically identify and remove redundant tokens  
* __supervised Matcher__  learn from all the metrics using random forest and properly combine all metrics to build a classifier for matching.  the random forest is automatically optimized by grid search the proper num of trees. 

The pipeline are shown as below

![alt tag](https://raw.githubusercontent.com/wangxiangtao/prettymatch/master/pipe.png)

You can easily add other cleaners by extend Clearner class and implement the clean() method 

To train a comprehensive model by all the matrics , you just need to run :

```scala
val PrettyMatch  = new PrettyMatch 
PrettyMatch.run(trainingdata,true)  // The format of trainingdata is a list of tuple which has 3 elements                  
                                    // A tuple contain two strings and one boolean value to indicate they match or not)
                                    // The second parameter indicate use supervised cleaner or not
```


It will generate trained model for comparison and print Confusion Matric by 10 cross validation.


##  Example : company name matching
There are [6000 pairs of comany names](https://drive.google.com/file/d/0B3fXSfbZhqCFcVJDeDJmdmh4Umc/view?usp=sharing
) which contain 1000 matched names and 5000 unmatched names

Using it as training data, we run the learning algorithm to get the confusion matric below. 
##### Confusion Matric
###### without supervised cleaner
|               | TP            | FP  |
| ------------- |:-------------:| --: |
| Unique        | 4989          | 7   |
| Duplicated    | 991           | 9   |

###### with supervised cleaner
automatically identify and clean redundant tokens : LLC, Inc, Company, Corp, Ltd, Corp., Inc., Limited, Group ...

|               | TP            | FP  |
| ------------- |:-------------:| --: |
| Unique        | 4972          | 6   |
| Duplicated    | 996           | 4   |

#### Any feedback, ideas, suggestions, success stories etc are more than welcome!
