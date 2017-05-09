/*  
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 * 
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 */  

package ir;

import java.util.LinkedList;
import java.io.Serializable;
import java.util.*;


/**
 *   A list of postings for a given word.
 */
public class PostingsList implements Comparable<PostingsList>,Serializable {
    
    /** The postings list as a linked list. */
    private LinkedList<PostingsEntry> list = new LinkedList<PostingsEntry>();
    private Integer frequenzy = 0; //tf(t)

    /**  Number of postings in this list  */
    public int size() {
	return list.size();
    }

    /**  Returns the ith posting */
    public PostingsEntry get( int i ) {
	return list.get( i );
    }

    //
    //  YOUR CODE HERE
    //
    
    public int compareTo( PostingsList other ) {
        return Integer.compare( other.frequenzy, frequenzy );
    }
    
    
    public int getFrequenzy() {
        return frequenzy;
    }
    
    public PostingsEntry getByDocID( int i ) {

        for (int k=0; k<list.size(); k++){
            if(list.get(k).docID == i){
                
                return list.get(k);
            }
            break;
        }
        return null;
    }
    
 
    public void addPostingsEntry( int docID, int offset){
        
        if (size()==0){
            
            PostingsEntry newPostingsEntry = new PostingsEntry();
            newPostingsEntry.docID = docID;
            newPostingsEntry.addPosition(offset);
            list.add(newPostingsEntry);
            frequenzy = frequenzy+1;
            
        }
        else{
            
            if (list.get(list.size()-1).docID == docID) {
                list.get(list.size()-1).addPosition(offset);
                frequenzy = frequenzy+1;
 
            }
            
            else{
                PostingsEntry newPostingsEntry = new PostingsEntry();
                newPostingsEntry.docID = docID;
                newPostingsEntry.addPosition(offset);
                list.add(newPostingsEntry);
                frequenzy = frequenzy+1;
                
            }
        }
    }
    
 
    public void addPostingsEntryByDocID( int docID){
        
        PostingsEntry newPostingsEntry = new PostingsEntry();
        newPostingsEntry.docID = docID;
        list.add(newPostingsEntry);
        
    }
    
    
    public void addPostingsEntryByEntry( PostingsEntry postingsEntry){
        list.add(postingsEntry);
    }



    
    
    
    
}











