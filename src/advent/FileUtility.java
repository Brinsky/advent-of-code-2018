package advent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtility 
{
	public static String fileToString(String filepath) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filepath));
		StringBuilder builder = new StringBuilder();
		
		while (reader.ready()) {
			builder.append(reader.readLine());
			
			// Preserve newlines
			if (reader.ready()) {
				builder.append('\n');
			}
		}
		
		reader.close();
		return builder.toString();
	}
	
	public static void printAndOutput(Object output, String filepath) throws IOException {
		System.out.println(output);
		stringToTextFile(output.toString(), filepath);
	}
	
	public static void stringToTextFile(String output, String filepath) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
		
		writer.write(output);
		writer.close();
	}
}