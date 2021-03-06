package gossip.main;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

/**
 * Writes events to a log file
 * 
 * @author etubil2
 * 
 */
public class Log {
	String filename;
	String sessionName;

	/**
	 * Constructor
	 * @param filename -file to write to
	 */
	public Log(String filename) {
		this.filename = filename;
		this.sessionName = "session";
	}

	/**
	 * Writes a message to the opened log file
	 * @param message -Sting to print in a log line
	 */
	public synchronized void writeLogMessage(String message){
		Timestamp timeStamp= new Timestamp(System.currentTimeMillis());
		String key = this.sessionName + " " + timeStamp;
		String value = message; 
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(this.filename, true)));
			out.println(key + " : " + value);
			out.close();
		} catch (FileNotFoundException e) {
			System.out.println("There was an error writing to the log");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	/**
	 * Session name is part of the key in the log file
	 * @param sessionName
	 */
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
		
	}
	
}
