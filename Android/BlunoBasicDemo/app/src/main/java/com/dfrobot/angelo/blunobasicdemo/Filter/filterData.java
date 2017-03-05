package com.dfrobot.angelo.blunobasicdemo.Filter;
import java.util.*;

public class filterData {
	private static List<String> lines;
	private static String INPUT_FILE_NAME;
	private static String OUTPUT_FILE_NAME;
	//private static Path OUTPUT_PATH;
	private static filter f1;
	private static filter f2;
	private static filter f3;


	public int calculateRespRate(int initsize, ArrayList<Float> data) {
        boolean grtThanFormer, grtThanLatter;
		lines = new ArrayList<String>();
		f1 = new filter(initsize);
		f2 = new filter(initsize);
		f3 = new filter(initsize);	
		List<Float> y = new ArrayList<Float>();

			for (Float f: data)
			{
				y.add(f3.step(f2.step(f1.step(f))));
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

		return 0;   //TODO: ADD REAL RETURN VALUE (RESPIRATIONS/MINUTE)
	}

}
