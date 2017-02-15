// filter.java
// Andrew Lynch 
// [feat. Xintong, Amanda, Andrew Wong]
// January 30, 2017
// averaging filter

public class filter {
	private int MAX_FILTER_SIZE = 200;
	int[] a = new int[5];
	private float[] Buffer;
	private int FilterSize;
	private int BuffIndex = 0;
	private float Averager;

	public filter(int size) {
		Buffer = new float[MAX_FILTER_SIZE];
		FilterSize = size;
		for (int i = 0; i < FilterSize; i++) { // set buffer to 0
			Buffer[i] = 0;
		}
		Averager = (float)1 / FilterSize;
	}

	public float step(float newValue) {
		float output = 0;
		Buffer[stepIndex()] = newValue * Averager;
		for (int i = 0; i < FilterSize; i++) {
			output += Buffer[i];
		}
		return output;
	}

	public int size(){
		return FilterSize;
	}
	
	private int stepIndex() {
		if (BuffIndex == (FilterSize - 1)) {
			BuffIndex = 0;
		} else {
			BuffIndex++;
		}
		return BuffIndex;
	}
}