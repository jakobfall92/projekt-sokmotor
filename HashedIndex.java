/*
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 *
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 *   Additions: Hedvig Kjellström, 2012-14
 */

/*
IDF = log10(N/df)   -   inverse document frequenzy. N = number of docs. DF = hur många doc är termen med i (getPostings)
TF - term frequenzy - hur många ggr är termen med i ett dokument?

antalet dokument / hur många dok är termen med i     X     hur många gånger dyker termen upp i ett dokument

log frequenzy weights (Wt,d) = 1+log10(tf) if tf > 0, 0 otherwise.

each document gets a tf-idf vector! - ska finnas ett tal för varje term i dokumentet, alltså för varje postingslist (score)

varje term i queryn ska också ha en vektor, eller siffra 



först, loopa igenom alla postingslists för en term och gångra idf (log(N/df)) med tf

SCORE(query,document) = sum ( tf-idf )
*/


package ir;

import java.util.HashMap;
import java.util.Iterator;
import java.util.*;
import java.lang.Math;




/**
 *   Implements an inverted index as a Hashtable from words to PostingsLists.
 */
public class HashedIndex implements Index {
    
    /** The index as a hashtable. */
    private HashMap<String,PostingsList> index = new HashMap<String,PostingsList>();
    
    boolean verbose = false;
    
    Log log = new Log();
    
    /**
     *  Inserts this token in the index.
     */
    public int getDocId(String title) {
        
    }

    public void insert( String token, int docID, int offset ) {
        //
        //  YOUR CODE HERE
        //
        PostingsList postingsList = (getPostings(token));
        
        if (postingsList == null){
            PostingsList newPostingsList = new PostingsList();
            newPostingsList.addPostingsEntry(docID,offset);
            
            index.put(token, newPostingsList);
        }
        else{
            postingsList.addPostingsEntry(docID,offset);
        }
        
    }
    
    
    /**
     *  Returns all the words in the index.
     */
    public Iterator<String> getDictionary() {
        //
        //  REPLACE THE STATEMENT BELOW WITH YOUR CODE
        //
        
        
        Set entrySet = index.entrySet();
        Iterator it = entrySet.iterator();
        
        while (it.hasNext()){
            
            Map.Entry me = (Map.Entry)it.next();
            System.err.println(me.getKey());
        }
        
        return null;
    }
    
    
    /**
     *  Returns the postings for a specific term, or null
     *  if the term is not in the index.
     */
    public PostingsList getPostings( String token ) {
        //
        //  REPLACE THE STATEMENT BELOW WITH YOUR CODE
        //
        if (index.get(token) != null){
            return index.get(token);
        }
        
        return null;
    }
    
    
    
    /**
     *  Searches the index for postings matching the query.
     */
    public PostingsList search( Query query, int queryType, int rankingType, int structureType ) {
        //
        //  REPLACE THE STATEMENT BELOW WITH YOUR CODE
        //
        
        PostingsList postingsList = new PostingsList();
        
        if (queryType == 0){ //intersection
            
            log.readLog("log.txt");
            postingsList = intersectQuery(query);
            
            
            
        }
        if (queryType == 1){ //phrase
            
            //postingsList = phraseQuery(query);
            postingsList = phraseQuery2(query);
            
            
        }
        
        if (queryType == 2){ //ranked
            
            if (rankingType == 0){
                postingsList = rankedQuery(query);
            }
            
        }

        return postingsList;
        
    }
    
    
    /*public class Logger {
        public static void log(String message) {
            PrintWriter out = new PrintWriter(new FileWriter("output.txt", true), true);
            out.write(message);
            out.close();
        }
    }
     */

    //private static final Logger LOGGER = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());
    //private final static Logger LOGGER = Logger.getLogger(MyLogger.class.getName());


    public PostingsList rankedQuery(Query query){

     
        PostingsList postingsList = new PostingsList();
        PostingsList results = new PostingsList();                
        
        List<PostingsList> postings = new ArrayList<PostingsList>();
        List<List<PostingsEntry>> matchingEntries = new ArrayList<List<PostingsEntry>>();
        List<PostingsEntry> newList = new ArrayList<PostingsEntry>();
        HashMap<String,PostingsEntry> rankFinal = new HashMap<String,PostingsEntry>();
        HashMap<String,PostingsEntry> rank = new HashMap<String,PostingsEntry>();

        int tfdt;
        int n;
        int dft;
        int lend;
        double idft;
        double tf_idfdt;
        
        double sum=0.0;
        double normalization_rank=0.0;
        double tfdt_log;
        double lend_sqrt;
        


        int fakeDocID = 123; //docID
        log.createLogFile("log.txt"); //hamnar i överordnad mapp
        log.logQueryAndReply(query,fakeDocID);
        log.close();
        
        
        
        
        //n är storleken av hela biblioteket
        //n = index.size(); //ska vara antal dokument i korpus, nu är det antalet termer! docLengths.size()
        n = docLengths.size();
        try{
        //RANK INNEHALLER SUMMAN AV tf_idfdt FÖR ALLA QUERY TERMER I ETT DOKUMENT
        for (int i=0; i<query.terms.size(); i++){
            rank = new HashMap<String,PostingsEntry>();

            //dft är antalet dokuemnt som termen dyker upp i
            dft = getPostings(query.terms.get(i)).size();
            //lend_sqrt = (double)n;//Math.sqrt(lend);
            //storlekBibliotek/antalDocIDs
            //idft=Math.log10(n/dft);
            //idft=Math.log(n/dft); //denna reducerar vikten för ord som "the".
            
            idft=Math.log(n/dft)*query.weights.get(i); //denna reducerar vikten för ord som "the".
            //HÄR HAR JAG ÄNDRAT NYDLIGEN ------------------------------------------------;
            
            //SKA JAG UPPDATERA WEIGHTS HÄR, JAG TÄNKER ATT DET BORDE VARA SÅ.
            
            
            
            
            
            
            //loopar igenom alla docIDs tillhörande en term.
            for (int j=0; j<getPostings(query.terms.get(i)).size(); j++){

                //tfdt är antalet förekommelser av ordet per docID.
                tfdt = getPostings(query.terms.get(i)).get(j).size();
                
                //test log normalization:
                //tfdt_log = 1.0+(Math.log(tfdt)/Math.log(10.0)); //this did not give good result, "money" was off by 20 positions. Also bad for 1 query terms...
                

                //antal ord i ett docID tillhörande termen.
                lend = docLengths.get( "" + getPostings(query.terms.get(i)).get(j).docID);

                
                
                
                
                //relevans baserat på bibliotekets storlek, antal docIDs för termen,
                //antalet gånger den nämns i ett DocID, och antal ord i det DocID'et.
                //antalDocIDsSomHarTermen*(storlekBibliotek/antalDocIDs)/storlekPåDocID
                
                //DET är HÄR vi väger in cosine similarity!!!
                tf_idfdt = (double)(tfdt*idft)/lend; //<-- length normalization (cosine)
                
                
                //antalDocIDsSomHarTermen*(storlekBibliotek/antalDocIDs)/storlekPåDocID/antaletTermeriQuery.
                tf_idfdt = tf_idfdt/query.terms.size(); //? ----- normalize? no need since it's just a constant.
                
                //sätt in tf_idfdt som score för detta DocID. (ett score per docID).
                getPostings(query.terms.get(i)).get(j).score = tf_idfdt;
                
                //mata in (docID,postingsEntry) i en hash (rank).
                rank.put(""+getPostings(query.terms.get(i)).get(j).docID, getPostings(query.terms.get(i)).get(j));
                

            }
            
            /* //EUCLIDEAN OPTION
            normalization_rank=0.0;
            for (String key: rank.keySet()){
                normalization_rank+=Math.abs(rank.get(key).score);
            }
            for (String key: rank.keySet()){
                rank.get(key).score=rank.get(key).score/normalization_rank;
            }
            */
  
            for (String key: rank.keySet()){
                
                //loopar igenom alla postingsEntries i hashen rank.
                //Plussar på dessa i en ny hash, som ska spara alla värden från alla termer i queryt.
                if (rankFinal.get(""+rank.get(key).docID)!=null){
                    rankFinal.get(""+rank.get(key).docID).score += rank.get(key).score; //Wtd*Wtq (Wtq=1)
                    
                }
                
                //Om rankfinal råkade vara tom så behöver vi inte plussa, vi lägger in den istället bara.
                else{
                    rankFinal.put(""+rank.get(key).docID , rank.get(key));
                }
                
            }





            
            


            //System.err.println(rank.size());
            
        }
            
        }
        catch(java.lang.NullPointerException e){
            //System.err.println("-------fel!-------");
        }

        /*
        System.err.println(matchingEntries.size());
        System.err.println(matchingEntries.get(0));
        System.err.println(matchingEntries.get(1));
        */ 
        //System.err.println(rankFinal.size());
        int counter = 0;
        sum = 0;
        for (String key: rankFinal.keySet()){
            newList.add(rankFinal.get(key));
            sum+=rankFinal.get(key).score;
            counter++;
        }
        //System.err.println(counter);
    
        Collections.sort(newList);
        //System.err.println("sum");
        //System.err.println(sum);
            
        for (int i=0; i<newList.size(); i++){
            results.addPostingsEntryByEntry(newList.get(i));
        }
        //System.err.println(sum);
        

        return results;
    }



    public PostingsList handleDoublettes (List<PostingsEntry> previousMatches,List<List<PostingsEntry>> matchingEntries){

        List<PostingsEntry> newList = new ArrayList<PostingsEntry>();
        List<PostingsEntry> entries = new ArrayList<PostingsEntry>();
        entries=matchingEntries.get(0);

        int m=0;
        int n=0;
                
        while(m<previousMatches.size() && n<matchingEntries.get(0).size()){
            
            
            if (previousMatches.get(m).docID == matchingEntries.get(0).get(n).docID){
                previousMatches.get(m).score *= matchingEntries.get(0).get(n).score;
                entries.remove(n);

                m = m+1;
                n = n+1;
            }
            else if (previousMatches.get(m).docID < matchingEntries.get(0).get(n).docID){
                m = m+1;
            }
            else if (previousMatches.get(m).docID > matchingEntries.get(0).get(n).docID){
                n = n+1;
            }
        }


        
        previousMatches.addAll(entries);

    

        for (int i=0; i<previousMatches.size(); i++){
            newList.add(previousMatches.get(i));
        }
        
        //om det finns en kvar, eller mer
        if (matchingEntries.size()>1){
            matchingEntries.remove(0);
            handleDoublettes(newList,matchingEntries);
        }
        else{
            
            PostingsList scores = new PostingsList(); 



            Collections.sort(newList);


            for (int i=0; i<newList.size(); i++){
                scores.addPostingsEntryByEntry(newList.get(i));
                System.err.println(newList.get(i).score);
            }




            return scores;
        }
        return null;
        }
        


    public int[] intersect(int[] initialVector, int[][] matrix, int maximum, int n_max, int m_max, int matrixCounter){
        
        
        int[] intersectionVector = new int[maximum];
        
        int counter = 0; //counts the number of matches
        
        int m=0;
        int n=0;
        
        
        while ((m<m_max) && (n<n_max) ){
            
            if (initialVector[m]==matrix[matrixCounter][n]){
                
                intersectionVector[counter] = matrix[matrixCounter][n];
                counter++;
                
                
                m=m+1;
                n=n+1;
            }
            
            else if(initialVector[m]<matrix[matrixCounter][n]){
                m=m+1;
            }
            
            else if(initialVector[m]>matrix[matrixCounter][n]){
                n=n+1;
            }
            
        }
        
        
        
        if (counter == 0){ //special case because I use matrices
            return null;
        }
        
        int[] temp = new int[counter];
        
        for (int k=0; k<counter; k++){ // för att korta ner returnsatsen, kan bli problem annars.
            temp[k] = intersectionVector[k];
        }
        
        return temp;
        
    }
    
    
    
    
    
    
    
    
    public PostingsList intersectQuery(Query query){
        
        PostingsList postingsList = new PostingsList();
        int[] intersection;
        int[] initialVector;
        
        if (query.terms.size() == 0){
            return null;
        }
        
        if (query.terms.size() < 2){
            postingsList = index.get(query.terms.get(0));
            return postingsList;
        }
        else{
            
            try{
                
                
                int[][] matrix;
                
                int[] sizeVector = new int[query.terms.size()];
                
                for (int k=0; k<query.terms.size(); k++){
                    sizeVector[k] = getPostings(query.terms.get(k)).size();
                }
                
                int maximum = maximum(sizeVector);
                int minimum = Math.min(sizeVector[0],sizeVector[1]);
                
                
                matrix = new int[query.terms.size()][maximum]; //creates matrix with one row per term in query and one column per posting in the largest postingsList.
                
                
                
                initialVector = new int[getPostings(query.terms.get(0)).size()];
                
                for (int k=0; k<1; k++){
                    for (int i=0; i<getPostings(query.terms.get(k)).size(); i++){
                        initialVector[i] = getPostings(query.terms.get(k)).get(i).docID;
                    }
                }
                
                for (int k=1; k<query.terms.size(); k++){
                    for (int i=0; i<getPostings(query.terms.get(k)).size(); i++){
                        matrix[k-1][i] = getPostings(query.terms.get(k)).get(i).docID;
                    }
                }
                
                
                int m_max = getPostings(query.terms.get(0)).size(); //for initialVector
                int n_max = getPostings(query.terms.get(1)).size(); //for matrix
                
                int matrixCounter = 0;
                
                intersection = intersect(initialVector,matrix, maximum, n_max, m_max, matrixCounter);
                
                
                int intersectionCounter = 2;
                
                
                while(intersectionCounter<query.terms.size()){
                    
                    initialVector = intersection;
                    
                    matrixCounter++;
                    m_max = initialVector.length;
                    maximum = initialVector.length;
                    n_max = getPostings(query.terms.get(intersectionCounter)).size();
                    
                    
                    intersection = intersect(initialVector, matrix, maximum, n_max, m_max, matrixCounter);
                    
                    intersectionCounter++;
                    
                }
                
                
                
                for (int i=0; i<intersection.length; i++){
                    postingsList.addPostingsEntryByDocID(intersection[i]);
                    
                }
                
                return postingsList;
            }
            catch ( java.lang.NullPointerException e ) {
                return null;
            }
        }
        
    }
    
    
    
    public List<Integer> getPositionsForPosting(PostingsList a, int index){
        
        List<Integer> positions = new ArrayList<Integer>();
        PostingsEntry b = new PostingsEntry();
        
        b = a.get(index);
        
        for (int e=0; e<b.size(); e++){
            
            positions.add(b.getPositions(e));
            
        }
        
        return positions;
    }
    
    
    
    
    public List<Integer> intersectPostingsLists( List<Integer> postings1, List<Integer> postings2){
        
        List<Integer> match = new ArrayList<Integer>();
        int m=0;
        int n=0;
        
        
        while(m<postings1.size() && n<postings2.size()){
            
            
            if (postings1.get(m)+1 ==postings2.get(n)){
                match.add(postings2.get(n));
                
                m = m+1;
                n = n+1;
            }
            else if (postings1.get(m)+1 < postings2.get(n)){
                m = m+1;
            }
            else if (postings1.get(m)+1 > postings2.get(n)){
                n = n+1;
            }
        }
        
        
        return match;
    }
    
    
    public PostingsList phraseQuery(Query query){
        
        PostingsList result = new PostingsList();
        PostingsList postingsList = getPostings(query.terms.get(0));
        
        //sätt in try och catch för att ta hand om när man inte matar in något.
        
        
        if(query.terms.size()<2){
            
            return postingsList;
        }
        
///////

        Hashtable<Integer,Hashtable<Integer,PostingsEntry>> phraseHash = new Hashtable<Integer,Hashtable<Integer,PostingsEntry>>();
        Hashtable<Integer,PostingsEntry> phraseHashInner = new Hashtable<Integer,PostingsEntry>();


        PostingsList temp = new PostingsList();

        for (int i=0; i<query.terms.size(); i++){
            temp = getPostings(query.terms.get(i));
            phraseHashInner = new Hashtable<Integer,PostingsEntry>();
            for (int j=0; j<temp.size(); j++){  
                phraseHashInner.put(temp.get(j).docID, temp.get(j));
            }
            phraseHash.put(i, phraseHashInner);
        }
        //System.err.println(phraseHash.get(0).get(0));


    //målet är att kolla postingsentry i varje och se på nästa. Se om jag kan använda hash för att snabbare hitta docIDs, och på så sätt snabba upp processen.
    //kolla på LÄNGDEN, det går nu eftersom vi har docID. På så sätt borde vi undvika att kolla för många docids i slutet.



        int counter=1;
        Hashtable<Integer,PostingsEntry> initial = new Hashtable<Integer,PostingsEntry>();


        initial = phraseHash.get(0);

        while(counter<phraseHash.size()){
        
            Hashtable<Integer,PostingsEntry> matchHash = new Hashtable<Integer,PostingsEntry>();
            
        //phraseHash.get(0)

            for (Integer key: initial.keySet()){

                int n=0;
                int m=0;                
                PostingsEntry matchEntry = new PostingsEntry();
                
                try{

                while (m<initial.get(key).size() && n<phraseHash.get(counter).get(key).size()){

                    if (initial.get(key).getPositions(m)+1 == phraseHash.get(counter).get(key).getPositions(n)){
                        //System.err.println(phraseHash.get(0).get(m).docID);


                        matchEntry.addPosition(phraseHash.get(counter).get(key).getPositions(n));
                        //System.err.println(docIDs.get(""+initial.get(key).docID));
                        

                        m = m+1;
                        n = n+1;
                        

                    }

                    else if (initial.get(key).getPositions(m)+1<phraseHash.get(counter).get(key).getPositions(n)){
                        m = m+1;
                    }
                    else if (initial.get(key).getPositions(m)+1>phraseHash.get(counter).get(key).getPositions(n)){
                        n = n+1;
                    }



                }
            }
                catch(java.lang.NullPointerException e){
                    //

                }

                if(matchEntry.size()>0){
                    matchEntry.docID = phraseHash.get(counter).get(key).docID;
                    matchHash.put(phraseHash.get(counter).get(key).docID, matchEntry);
                }








            }
                counter=counter+1;
                initial = new Hashtable<Integer,PostingsEntry>();
                initial = matchHash;
                //System.err.println(initial.size());
                
                        
        }

        PostingsList ret = new PostingsList();
        for (Integer key1: initial.keySet()){
            ret.addPostingsEntryByEntry(initial.get(key1));

        }
        return ret;

        


    }
    


    public PostingsList phraseQuery2(Query query){

        PostingsList result = new PostingsList();
        PostingsList postingsList = getPostings(query.terms.get(0));
        
        //sätt in try och catch för att ta hand om när man inte matar in något.
        
        
        if(query.terms.size()<2){
            try{
                return postingsList;
            }
            catch (java.lang.IndexOutOfBoundsException e){
                //
            }
            
        }

        
        

        result = positionalIntersect(postingsList,query.terms.get(1));
        int counter = 2;
        //int counter = 2;
        
        if(query.terms.size()>2){
            
            while (counter<query.terms.size()){
                result = positionalIntersect(result,query.terms.get(counter));
                counter = counter+1;
            }
        }
        
        return result;
    }


    
    //ide, sortera efter score i postingslist
    public PostingsList positionalIntersect(PostingsList previousResult, String queryTerm){
        
        
        PostingsList fakePostingsListWithMatches = new PostingsList();
        PostingsList postingsList1 = new PostingsList();
        PostingsList postingsList2 = new PostingsList();
        
        postingsList1 = previousResult;
        postingsList2 = getPostings(queryTerm);
        

        
        boolean keepGoing = true;
        
        int n = 0;
        int m = 0;
        
        while (m<postingsList1.size() && n<postingsList2.size()){
            
            
            if (postingsList1.get(m).docID == postingsList2.get(n).docID){
                
                
                List<Integer> matchingPositions = new ArrayList<Integer>();
                
                matchingPositions = intersectPostingsLists(getPositionsForPosting(postingsList1,m),getPositionsForPosting(postingsList2,n));
                
                
                if(matchingPositions.size()>0){
                    
                    //add list of integers to a postings entry, which we will add to a postingslist later.
                    PostingsEntry fakeEntry = new PostingsEntry();
                    fakeEntry.docID = postingsList2.get(n).docID;
                    
                    
                    for (int i=0; i<matchingPositions.size(); i++){
                        fakeEntry.addPosition(matchingPositions.get(i));
                    }
                    fakePostingsListWithMatches.addPostingsEntryByEntry(fakeEntry);
                    
                }
                
                m = m+1;
                n = n+1;
                
            }
            
            else if (postingsList1.get(m).docID<postingsList2.get(n).docID){
                m = m+1;
            }
            else if (postingsList1.get(m).docID>postingsList2.get(n).docID){
                n = n+1;
            }
            
            //edit icon
        }
        
        
        return fakePostingsListWithMatches;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    public int maximum(int[] vector){
        int max = 0;
        for (int j=0; j<vector.length; j++){
            if(vector[j]>max){
                max = vector[j];
            }
        }
        return max;
    }
    
    
    
    
    
    public int minimum(int[] vector){
        int min = vector[0];
        for (int j=0; j<vector.length; j++){
            if(vector[j]<min){
                min = vector[j];
            }
        }
        return min;
    }
    
    
    
    
    /**
     *  No need for cleanup in a HashedIndex.
     */
    public void cleanup() {
    }
}
/*
 *   This file is part of the computer assignment for the
 *   Information Retrieval course at KTH.
 *
 *   First version:  Johan Boye, 2010
 *   Second version: Johan Boye, 2012
 *   Additions: Hedvig Kjellström, 2012-14
 */

