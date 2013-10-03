package gossip.main;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Writes events to a log file
 * 
 * @author etubil2
 * 
 */
public class Log {
	String filename;

	/**
	 * Constructor
	 * @param filename -file to write to
	 */
	public Log(String filename) {
		this.filename = filename;
	}

	/**
	 * Writes a message to the opened log file
	 * @param message -Sting to print in a log line
	 */
	public void writeLogMessage(String message){

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(this.filename, true)));
			out.println(System.currentTimeMillis() + " : " + message);
			out.close();
		} catch (FileNotFoundException e) {
			System.out.println("There was an error writing to the log");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
}
