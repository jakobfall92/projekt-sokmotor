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


public class PostingsEntry implements Comparable<PostingsEntry>, Serializable {
    
    public int docID;
    
    public double score;
    public ArrayList<Integer> posting = new ArrayList<Integer>();

    /**
     *  PostingsEntries are compared by their score (only relevant 
     *  in ranked retrieval).
     *
     *  The comparison is defined so that entries will be put in 
     *  descending order.
     */
    public int compareTo( PostingsEntry other ) {
	return Double.compare( other.score, score );
    }

    
    
    public void addPosition(int offset){
        posting.add(offset);
    }

    //TF!
    public int size() {
        return posting.size();
    }
 
    /*
    public double logTF() {
        if(posting.size()!=0){
            return (1+Math.log(posting.size()));
        }
        else{
            return 0;
        }
        
    }
    */

    public double logTF() {
        if(posting.size()!=0){
            return 1;
        }
        else{
            return 0;
        }
        
    }

    public Integer getPositions(Integer p){
        return posting.get(p);
        
    }
}

    
