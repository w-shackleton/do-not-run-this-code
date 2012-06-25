package uk.digitalsquid.internetrestore.util.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A couple of helper I/O functions
 * @author william
 *
 */
public class FileIO {
	private FileIO() { }
	
	public static void writeContents(File file, String data) throws IOException {
		if(!file.exists()) file.createNewFile();
		FileWriter out = new FileWriter(file);
		
		BufferedWriter buf = new BufferedWriter(out); 
		
		buf.write(data);
		
		buf.close();
	}
	public static void writeContents(String file, String data) throws IOException {
		writeContents(new File(file), data);
	}
	
	public static String readContents(File file) throws IOException {
		FileReader in = new FileReader(file);
		BufferedReader buf = new BufferedReader(in);
		
		char[] data = new char[1024];
		int count;
		StringBuilder out = new StringBuilder(1024);
		while((count = buf.read(data)) != -1) {
			out.append(data, 0, count);
		}
		return out.toString();
	}
}
