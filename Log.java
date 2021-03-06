
package ir;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.util.logging.Logger;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.ListIterator;


//!!!OBS!!!

/*
 //sätt denna i deklarationen
 
 Log log = new Log();
 Har lagt till en konstruktor så den kor createlogfile automatiskt nar man skapar objeketet.
 
 
 //sätt dessa där ni vill skriva in något i loggen
 
 int fakeDocID = 123; //docID
 log.logQueryAndReply(query,fakeDocID);
 log.close();
 
 
 //använd denna för att läsa loggen till en länkad lista av logEntry objekt.
 
 log.readLog("log.txt");
 
 
 
 */

public class Log {
    
    private PrintWriter printWriter;
    private File file;
    
    public LinkedList<LogEntry> logEntries = new LinkedList<LogEntry>();

    public Log(){ //constructor
        this.createLogFile("log.txt"); //Creates logfile when object is initiated.
        //readLog("log.txt"); //fills logEntries with file content
    }
    
    public void close(){
        printWriter.close();
    }
    
    public void message(String message){
        printWriter.println(message);
    }
    
    public void logQueryReplyAndFilename(Query query,int docID, String fileName){
        String temp = "";
        for (int i=0; i<query.terms.size(); i++){
            temp += " "+query.terms.get(i);
            System.err.println(query.terms.get(i));
        }
        
        
        printWriter.println(""+docID+","+temp+","+fileName);
    }
    
    public void createLogFile(String location){
        
        try{
            
            file = new File (location);
            
            if(file.isFile()){
                printWriter = new PrintWriter(new FileOutputStream(file, true));
                //printWriter = new PrintWriter (file,true);
            }
            
            else{
                printWriter = new PrintWriter (location);
                //printWriter.println ("hello");
            }
            
            
            }
        catch(Exception e){
            
            System.err.println(e);
            
        }
    }
    
    
    //reads log and creates a corresponding logEntry object for each line in the log
    public void readLog(String location){
        
        try{
            
            file = new File (location);
            
            if(file.isFile()){
                
                FileReader reader = new FileReader(location);
                BufferedReader inStream = new BufferedReader(reader);
                String inString;
                
                while ((inString = inStream.readLine()) != null) {
                    
                    LogEntry logEntry = new LogEntry();
                    
                    String[] parts = inString.split(",");
                    
                    int part1 = Integer.parseInt(parts[0]); // 004 (docid)
                    String part2 = parts[1]; // this is a query
                    String part3 = parts[2]; // this is a query
                    
                    //System.err.println(""+part1+" "+part2);
                    
                    logEntry.setClickedDoc(part1);
                    
                    Query query = new Query(part2);
                    
                    logEntry.setQuery(query);
                    logEntry.setFileName(part3);
                    logEntries.add(logEntry);
                    
                    
                    
                }
                
                inStream.close();

            }
            
            else{
                //
            }
            
        
    }
        catch(Exception e){
            
            System.err.println(e);
            
        }
        
    }

    public LinkedList<LogEntry> getLog() {
        return logEntries;
    }
    //reads log and creates a corresponding logEntry object for each line in the log
    public void writeLog(String location, LinkedList<LogEntry> logEntries){
        try{
            file = new File (location);    
            if(file.isFile()){     
                printWriter = new PrintWriter(new FileOutputStream(file, true));
                file.delete();
                ListIterator<LogEntry> list = logEntries.listIterator(0);
                while(list.hasNext()) {
                    LogEntry currentEntry = list.next();
                    Integer docId = currentEntry.getDocId();
                    Query query = currentEntry.getQuery();
                    String temp = "";
                    for (int i=0; i<query.terms.size(); i++){
                        temp += " "+query.terms.get(i);
                    }
                    printWriter.println(""+docId+","+temp);
                }
                printWriter.close();
            } else{
                //
            }

        }
        catch(Exception e){   
            System.err.println(e);  
        }  
    }
    
}
