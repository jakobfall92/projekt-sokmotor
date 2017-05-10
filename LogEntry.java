/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 */  

package ir;

import java.io.Serializable;
import java.util.*;


public class LogEntry implements Comparable<LogEntry>,Serializable {
    
    private int docID;
    private double score;
    private Query query;
    
    
    public int compareTo( LogEntry other ) {
        return Double.compare( other.score, score );
    }
    
    public Query getQuery(){
        return query;
    }

    public String getStringQuery(){
        return query;
    }

    public int getDocId(){
        return docID;
    }
    
    public double getScore(){
        return score;
    }
    
    public void setQuery(Query theQuery){
        query = theQuery;
    }
    
    public void setClickedDoc(int theClickedDoc){
        docID = theClickedDoc;
    }
    
    public void setScore(double theScore){
        score = theScore;
    }
    
}

    
