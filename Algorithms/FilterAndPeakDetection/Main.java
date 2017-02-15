import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.util.*;

public class Main {
	private static List<String> lines;
	private static String INPUT_FILE_NAME;
	private static String OUTPUT_FILE_NAME;
	private static Path OUTPUT_PATH;
	private static filter f1;
	private static filter f2;
	private static filter f3;
//	public Main(int initsize){
//	}
	public static void main(String[] args) {boolean grtThanFormer, grtThanLatter;
		int initsize = Integer.parseInt(args[0]);
		lines = new ArrayList<String>();
		INPUT_FILE_NAME = args[1];
		OUTPUT_FILE_NAME = args[2];
		OUTPUT_PATH = Paths.get(OUTPUT_FILE_NAME);
		f1 = new filter(initsize);
		f2 = new filter(initsize);
		f3 = new filter(initsize);	
		List<Float> y = new ArrayList<Float>();	
		try{
			FileReader freader = new FileReader(INPUT_FILE_NAME);
			BufferedReader reader = new BufferedReader(freader);
			float x;
			for (String s = reader.readLine(); s != null; s = reader.readLine()) 
			{
				x = Float.parseFloat(s);
				y.add(f3.step(f2.step(f1.step(x))));
			}
			freader.close();
			reader.close();
		}
		catch (FileNotFoundException e) 
		{
			System.err.println ("Error: File not found. Exiting...");
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) 
		{
			System.err.println ("Error: IO exception. Exiting...");
			e.printStackTrace();
			System.exit(-1);
		}
		for(int i = 0; i < (f1.size() + f2.size() + f3.size()); i++){ //remove filter setup values (worthless)
			y.remove(i);
		}
		
		for(int i = 1; i < y.size()-1; i++){ //does not check first and last value
			grtThanFormer = y.get(i-1)	<	y.get(i);
			grtThanLatter = y.get(i+1)	<	y.get(i);
			String peakDetection = String.valueOf(grtThanFormer&&grtThanLatter);
			String fileLine = new String(y.get(i).toString());
			fileLine = fileLine.concat(",\t");
			fileLine = fileLine.concat(peakDetection);
			lines.add(fileLine);
		}
		try{
			Files.write(OUTPUT_PATH,lines,Charset.forName("UTF-8"));
		}
		catch (FileNotFoundException e) 
		{
			System.err.println ("Error: File not found. Exiting...");
			e.printStackTrace();
			System.exit(-1);
		} 
		catch (IOException e) 
		{
			System.err.println ("Error: IO exception. Exiting...");
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
