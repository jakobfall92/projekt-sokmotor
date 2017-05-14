/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Hedvig Kjellström, 2012
 */  

package ir;

import java.util.LinkedList;
import java.util.StringTokenizer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.*;
import java.lang.Math;


import java.io.File;

import java.io.*;
import java.util.*;
import java.nio.charset.*;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.*;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.pdmodel.PDDocument;




public class Query {
    
    public LinkedList<String> terms = new LinkedList<String>();
    public LinkedList<Double> weights = new LinkedList<Double>();
    //public Log log;
    //public LinkedList<LogEntry> logEntries = new LinkedList<LogEntry>();

    /**
     *  Creates a new empty Query 
     */
    
    
    public Query() {
       
    }
    
	
    /**
     *  Creates a new Query from a string of words
     */
    public Query( String queryString  ) {
	StringTokenizer tok = new StringTokenizer( queryString );
	while ( tok.hasMoreTokens() ) {
	    terms.add( tok.nextToken() );
	    weights.add( new Double(1) );
        //log = new Log();
        //logEntries = log.getLog();
	}    
    }
    
    /**
     *  Returns the number of terms
     */
    public int size() {
	return terms.size();
    }
    
    /**
     *  Returns a shallow copy of the Query
     */
    public Query copy() {
    	Query queryCopy = new Query();
    	queryCopy.terms = (LinkedList<String>) terms.clone();
    	queryCopy.weights = (LinkedList<Double>) weights.clone();
    	return queryCopy;
    }

    /*
    public void saveLog() {
        log.writeLog("log.txt",logEntries);
        log.message("test");
    }
     

    public void addToLog(int docId) {
        LogEntry entry = new LogEntry();
        entry.setClickedDoc(docId);
        entry.setQuery(this);
        logEntries.add(entry);
    }
     
     */
    
    /**
     *  Expands the Query using Relevance Feedback
     */
    public void relevanceFeedback( LinkedList<LogEntry> loggedDocuments) {
	// results contain the ranked list from the current search
	// docIsRelevant contains the users feedback on which of the 10 first hits are relevant
	
	//
	//  YOUR CODE HERE
	//
        
        HashMap<String,Double> howManyInstancesOfTerm = new HashMap<String,Double>();
        HashMap<String,Double> documentWordList = new HashMap<String,Double>();
        HashMap<String,Double> scores = new HashMap<String,Double>();
        //HashMap<String,Double> idf = new HashMap<String,Double>();

        double alpha = 1.0;
        double beta = 0.75;
        
        int numberOfResults = 0;
        int numberOfMarkedRelevantDocuments = 0;
        int numberOfUniqueWordsInDoc=0;
        int lend;
        int totalNumberOfRelevantWords = 0;
        double idf;
        

        numberOfMarkedRelevantDocuments = loggedDocuments.size();
        
        for (int i=0; i<loggedDocuments.size(); i++){
            if (i>10){
                break;
            }
        //for (int i=0; i<10; i++){
            
            if (true){

                documentWordList = new HashMap<String,Double>(); //one wordlist per document
                lend=0;
                numberOfUniqueWordsInDoc=0;
                
                try{
                    
                    String file = "/Users/oskarwilhelmsson/Dropbox/KTH/SearchEngine/DavisWiki/";
                    File f = new File(file + loggedDocuments.get(i).fileName);
                    String patterns_file = new String("patterns.txt");
                    Reader reader = new InputStreamReader( new FileInputStream(f));
                    Tokenizer tok = new Tokenizer(reader, true, false, true, patterns_file);

                    while ( tok.hasMoreTokens() ) {
                        String token = tok.nextToken();
                        lend+=1; //number of
                        totalNumberOfRelevantWords+=1; //number of new terms from the relevant documents.
                        
                        if (documentWordList.get(token) != null){
                            documentWordList.put(token, documentWordList.get(token)+(double)1.0);
                            howManyInstancesOfTerm.put(token, (double)howManyInstancesOfTerm.get(token)+(double)1.0);
                        }
                        
                        else{
                            
                            documentWordList.put(token,(double)(1.0));
                            numberOfUniqueWordsInDoc+=1;
                            howManyInstancesOfTerm.put(token, (double)1.0);

                            
                        }
                        
                    }
                    reader.close();
                }
                
                
                catch ( Exception e ) {
                    System.err.println(e);
                }
            
                double tf_idfdt=0.0;
                double idft=0.0;
                
                Set entrySet = documentWordList.entrySet();
                Iterator it = entrySet.iterator();
                
                
                while (it.hasNext()){
                    
                    Map.Entry me = (Map.Entry)it.next();
                    String term = (String) me.getKey();
                    
                    //documentWordList.get(term) motsvarar tfdt
                    
                    //length normalization of the document vector by the number of words in doc (lend)
                    //Same process as in rankedQuery (hashedindex.java).
                    
                    //not really tf_idfdt yet, I add idc later.
                    
                    tf_idfdt = (double)(documentWordList.get(term)/lend);   //normalizing
                    tf_idfdt = (double)(tf_idfdt/numberOfMarkedRelevantDocuments); //averaging
                    tf_idfdt = (double)(tf_idfdt*beta);   //multiplying with beta
                    //weight of term in query is 1.
                    
                    if (scores.get(term)!= null){
                        scores.put(term, scores.get(term)+tf_idfdt ); //scores keep track of the tf_idf weights
                    }
                    else{
                        scores.put(term,tf_idfdt);
                    }
                }//endwhile
                
            }//endif
            
        }//endfor
        
        
        //calculation of idf needs to be done after reading in the relevant documents.
        idf = 0.0;
        Set entrySet = scores.entrySet();
        Iterator it = entrySet.iterator();
        while (it.hasNext()){
            
            Map.Entry me = (Map.Entry)it.next();
            String newTerm = (String) me.getKey();
            
            idf = (double)Math.log(totalNumberOfRelevantWords/howManyInstancesOfTerm.get(newTerm)); //antal ord i alla valda dokument delat på antalet gånger termen används totalt i båda dokumenten. I Hashed Index har vi istället antelet dokument i korpus / dokument som innehållet t.
            scores.put(newTerm, (double)scores.get(newTerm)*idf);
            
        }
        
        
            //Length normalization of original query by the number of terms in query.
        for (int f=0; f<weights.size(); f++){
            weights.set(f,(double)(weights.get(f)/weights.size())); //normalizing
            weights.set(f,(double)weights.get(f)*alpha);    //multiplying with alpha
            
            
           if (scores.get(terms.get(f)) != null){
               scores.put(terms.get(f), scores.get(terms.get(f))+weights.get(f));
           }
           
           else{
               scores.put(terms.get(f), weights.get(f));
           }
        }

        terms.clear();
        weights.clear();
        
        double sum = 0;
        
        //sorting
        HashMap<Integer, String> map = sortByValues(scores);
        int counter = 0;
        
        entrySet = map.entrySet();
        it = entrySet.iterator();
        while (it.hasNext() && counter < 10){

            Map.Entry me = (Map.Entry)it.next();
            String newTerm = (String) me.getKey();
            Double newWeight = (double) me.getValue();
            
            terms.add(newTerm);
            weights.add(newWeight);
            
            counter++;
            /*
            sum+=(double)newWeight;
             
            System.err.println(newTerm);
            System.err.println(newWeight);
            */
            
            
            
        }
        
        /*
         System.err.println("sum");
         System.err.println(sum);
         System.err.println("--------------------------------------------");
        */
        
        
    }//end method
    
    private static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o2, Object o1) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                .compareTo(((Map.Entry) (o2)).getValue());
            }
        });
        
        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }
    
}//end class

    
