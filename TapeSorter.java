package Assignment2;
import java.util.Random; //for testing purposes...see main method

/**
 * Represents a machine with limited memory that can sort tape drives.
 */
public class TapeSorter {

    private int memorySize;
    private int tapeSize;
    public int[] memory;
    private TapeDrive t1, t2, t3, t4;
    
    public TapeSorter(int memorySize, int tapeSize) {
        this.memorySize = memorySize;
        this.tapeSize = tapeSize;
        this.memory = new int[memorySize];
    }

    //Helper method that clears out a tape
    void clear(TapeDrive tape) {
    	for (int i = 0; i < tapeSize; i++) {
    		tape.write(0);
    	}
    }
    //Helper method that determines if a tape is filled with only zeros
    boolean isEmpty(TapeDrive tape) {
    	//assumes empty until element is found
    	boolean empty = true;
    	for (int j = 0; j < tapeSize; j++) {
			if (tape.read() != 0) {
				empty = false;
			}
		}
    	return empty;
    }
    		
    /**
     * Sorts the first `size` items in memory via quicksort
     */
    void quicksort(int size) {
		if (size == 0) {
			return;
		}
		quicksortHelper1(0, size - 1);
	}
	 
	void quicksortHelper1(int lo, int hi) {
		int num = quicksortHelper2(lo, hi);
		if (lo < num - 1) { 
			//Recursively calls itself with half the size
			quicksortHelper1(lo, num - 1);
		}
		if (num < hi) {
			quicksortHelper1(num, hi);
		}
	}
	
	int quicksortHelper2(int lo, int hi) {
		int temp;
		//Starts at the middle of the array
	    int pivot = memory[(lo + hi) / 2];
	     
	    while (lo <= hi) {
	    	while (memory[lo] < pivot) {
	    		lo++;
	        }
	        while (memory[hi] > pivot) {
	        	hi--;
	        }
	        if (lo <= hi) {
	        	//Bubble two array elements
	        	temp = memory[lo];
	        	memory[lo] = memory[hi];
	        	memory[hi] = temp;
	        	lo++;
	        	hi--;
	        }
	      }
	    //return to quickSortHelper1
	    return lo;
	}

    /**
     * Reads in numbers from drive `in` into memory (a chunk), sorts it, then writes it out to a different drive.
     * It writes chunks alternatively to drives `out1` and `out2`.
     *
     * If there are not enough numbers left on drive `in` to fill memory, then it should read numbers until the end of
     * the drive is reached.
     *
     * Example 1: Tape size = 8, memory size = 2
     * ------------------------------------------
     *   BEFORE:
     * in: 4 7 8 6 1 3 5 7
     *
     *   AFTER:
     * out1: 4 7 1 3 _ _ _ _
     * out2: 6 8 5 7 _ _ _ _
     *
     *
     * Example 2: Tape size = 10, memory size = 3
     * ------------------------------------------
     *   BEFORE:
     * in: 6 3 8 9 3 1 0 7 3 5
     *
     *   AFTER:
     * out1: 3 6 8 0 3 7 _ _ _ _
     * out2: 1 3 9 5 _ _ _ _ _ _
     *
     *
     * Example 3: Tape size = 13, memory size = 4
     * ------------------------------------------
     *   BEFORE:
     * in: 6 3 8 9 3 1 0 7 3 5 9 2 4
     *
     *   AFTER:
     * out1: 3 6 8 9 2 3 5 9 _ _ _ _ _
     * out2: 0 1 3 7 4 _ _ _ _ _ _ _ _
     */
    public void initialPass(TapeDrive in, TapeDrive out1, TapeDrive out2) {
    	int i;						//loop variable
    	int counter = 0;			//keeps track of whether or not tape size has been reached 
    	int chunkCounter = 0;		//variable to keep track of which out tape to write to
    	int chunkSize; 				//is either the memory size or the tape size
		int numLeft = tapeSize; 	//keeps track of how many numbers are left to write to out tapes
		int numToWrite;				//is either chunkSize or numLeft
    	//Iterates until entire tape has been read through
    	while (counter < tapeSize) {
    		//If memory size is greater than the tape size, 
    		//we will only read up that far (to avoid a null pointer exception)
    		if (tapeSize <= memorySize) {
    			chunkSize = tapeSize;
    		}
    		else {
    			chunkSize = memorySize;
    		}
    		
    		if (numLeft < chunkSize) {
    			chunkSize = numLeft;
    		}
    		//Reads in chunks of data from tape and puts into memory
    		for (i = 0; i < chunkSize; i++ ) {
    			memory[i] = in.read();
    			counter++;
    		}
    		chunkCounter++;
    		
    		//Sorts elements in memory
    		quicksort(chunkSize);
    	
    	
    		if (numLeft < chunkSize) {
    			numToWrite = numLeft;
    		}
    		else {
    			numToWrite = chunkSize;
    		}
    		//Writes to alternating 'out' tapes
    		for (i = 0; i < numToWrite; i++) {
    			if (chunkCounter % 2 != 0) {
    				out1.write(memory[i]);
    			}
    			else {
    				out2.write(memory[i]);
    			}
    			numLeft--;
    		} 
    	} 
    }

    /**
     * Merges the first chunk on drives `in1` and `in2` and writes the sorted, merged data to drive `out`.
     * The size of the chunk on drive `in1` is `size1`.
     * The size of the chunk on drive `in2` is `size2`.
     *
     *          Example
     *       =============
     *
     *  (BEFORE)
     * in1:  [ ... 1 3 6 8 9 ... ]
     *             ^
     * in2:  [ ... 2 4 5 7 8 ... ]
     *             ^
     * out:  [ ... _ _ _ _ _ ... ]
     *             ^
     * size1: 4, size2: 4
     *
     *   (AFTER)
     * in1:  [ ... 1 3 6 8 9 ... ]
     *                     ^
     * in2:  [ ... 2 4 5 7 8 ... ]
     *                     ^
     * out:  [ ... 1 2 3 4 5 6 7 8 _ _ _ ... ]
     *                             ^
     */
    public void mergeChunks(TapeDrive in1, TapeDrive in2, TapeDrive out, int size1, int size2) {
        int sizeCounter = 0;
        int overallSize = size1 + size2;
        int elementIn1 = 0, elementIn2 = 0;
        int leftOn1 = size1, leftOn2 = size2;
        
        //If there's nothing left to merge on either of the in tapes, we're done
        if ((leftOn1 == 0) && (leftOn2 == 0)) {
        	return;
        }
	
        //Check to make sure that you're not reading in more than you should
		if (leftOn1 != 0) {
			elementIn1 = in1.read();
		}
		if (leftOn2 != 0) {
			elementIn2 = in2.read();
		}
		
        while (sizeCounter < (overallSize)) {
        	//If either of the tapes are finished being read in, 
        	//break out of the loop
        	if ((leftOn1 == 0) || (leftOn2 == 0)) {
        		break;
        	}
     
        	if (elementIn1 <= elementIn2) {
        		out.write(elementIn1); 
        		leftOn1--;
        		if (leftOn1 != 0) {
        			elementIn1 = in1.read();
        		}
        		sizeCounter++;
        		
        	}
        	else {
        		out.write(elementIn2);	
        		leftOn2--;
        		if (leftOn2 != 0) {
        			elementIn2 = in2.read();
        		}
			sizeCounter++;
			}
        }
        //If there's nothing left to merge on either of the in tapes, we're done
        if ((leftOn1 == 0) && (leftOn2 == 0)) {
        	return;
        }
        //If there's nothing left on in1, read and write the rest of the stuff 
        //from in2 to out
        if (leftOn1 == 0) {
        	//extra variable left because leftOn2 changes throughout the loop
        	int left = leftOn2;
        	for (int i = 0; i < left; i++) {
        		out.write(elementIn2);	
				leftOn2--;
				if (leftOn2 != 0) {
					elementIn2 = in2.read();
				}
				sizeCounter++;    
        	}
        }
        //If there's nothing left to merge on either of the in tapes, we're done
        if ((leftOn1 == 0) && (leftOn2 == 0)) {
        	return;
        }
        //If there's nothing left on in2, read and write the rest of the stuff 
        //from in1 to out
        if (leftOn2 == 0) {
        	//extra variable left because leftOn1 changes throughout the loop
        	int left = leftOn1;
        	for (int i = 0; i < left; i++) {
        		out.write(elementIn1); 
        		leftOn1--;
        		if (leftOn1 != 0) {
        			elementIn1 = in1.read();
        		}
        		sizeCounter++;
        	}
        }

    }

    /**
     * Merges chunks from drives `in1` and `in2` and writes the resulting merged chunks alternatively to drives `out1`
     * and `out2`.
     *
     * The `runNumber` argument denotes which run this is, where 0 is the first run.
     *
     * -- Math Help --
     * The chunk size on each drive prior to merging will be: memorySize * (2 ^ runNumber)
     * The number of full chunks on each drive is: floor(tapeSize / (chunk size * 2))
     * Note: If the number of full chunks is 0, that means that there is a full chunk on drive `in1` and a partial
     * chunk on drive `in2`.
     * The number of leftovers is: tapeSize - 2 * chunk size * number of full chunks
     *
     * To help you better understand what should be happening, here are some examples of corner cases (chunks are
     * denoted within curly braces {}):
     *
     * -- Even number of chunks --
     * in1 ->   { 1 3 5 6 } { 5 7 8 9 }
     * in2 ->   { 2 3 4 7 } { 3 5 6 9 }
     * out1 ->  { 1 2 3 3 4 5 6 7 }
     * out2 ->  { 3 5 5 6 7 8 9 9 }
     *
     * -- Odd number of chunks --
     * in1 ->   { 1 3 5 } { 6 7 9 } { 3 4 8 }
     * in2 ->   { 2 4 6 } { 2 7 8 } { 0 3 9 }
     * out1 ->  { 1 2 3 4 5 6 } { 0 3 3 4 8 9 }
     * out2 ->  { 2 6 7 7 8 9 }
     *
     * -- Number of leftovers <= the chunk size --
     * in1 ->   { 1 3 5 6 } { 5 7 8 9 }
     * in2 ->   { 2 3 4 7 }
     * out1 ->  { 1 2 3 3 4 5 6 7 }
     * out2 ->  { 5 7 8 9 }
     *
     * -- Number of leftovers > the chunk size --
     * in1 ->   { 1 3 5 6 } { 5 7 8 9 }
     * in2 ->   { 2 3 4 7 } { 3 5 }
     * out1 ->  { 1 2 3 3 4 5 6 7 }
     * out2 ->  { 3 5 5 7 8 9 }
     *
     * -- Number of chunks is 0 --
     * in1 ->   { 2 4 5 8 9 }
     * in2 ->   { 1 5 7 }
     * out1 ->  { 1 2 4 5 5 7 8 9 }
     * out2 ->
     */
    public void doRun(TapeDrive in1, TapeDrive in2, TapeDrive out1, TapeDrive out2, int runNumber) {
    	int chunkSize = (int) (memorySize * (Math.pow(2, runNumber)));
    	int fullChunks =  (int) Math.floor((tapeSize / (chunkSize * 2)));
        int leftovers = tapeSize - 2 * chunkSize * fullChunks;
        int counter = 0;
        
        //If there are no full chunks, just put merged data into out1
        if (fullChunks == 0) {
        	mergeChunks(in1, in2, out1, chunkSize, (leftovers-chunkSize));
        	return;
        }   
        
        //Merges all of the full chunks first into alternating 'out' tapes
        for (int i = 0; i < fullChunks; i++) {
        	counter++;
        	if (counter % 2 != 0) {
        		mergeChunks(in1, in2, out1, chunkSize, chunkSize);
        	}
        	else {
        		mergeChunks(in1, in2, out2, chunkSize, chunkSize);
        	}
        }
        
        //Covers the four corner cases for leftovers
        if ((leftovers <= chunkSize) && (counter % 2 != 0)) {
        	mergeChunks(in1, in2, out2, leftovers, 0);
        }
        else if (leftovers <= chunkSize) {
        	mergeChunks(in1, in2, out1, leftovers, 0);
        }
        
        else if ((leftovers > chunkSize) && (counter % 2 != 0)){
        	mergeChunks(in1, in2, out2, chunkSize, (leftovers-chunkSize));
        }
        else {   //if (leftovers > chunkSize)
        	mergeChunks(in1, in2, out1, chunkSize, (leftovers-chunkSize));
        }
           
    }

    /**
     * Sorts the data on drive `t1` using the external sort algorithm. The sorted data should end up on drive `t1`.
     *
     * Initially, drive `t1` is filled to capacity with unsorted numbers.
     * Drives `t2`, `t3`, and `t4` are empty and are to be used in the sorting process.
     */
    public void sort(TapeDrive t1, TapeDrive t2, TapeDrive t3, TapeDrive t4) {
    	
    	initialPass(t1, t2, t3);
    	
    	//If the memory size is larger than the tape size, we're done here
    	//It's already sorted from initial pass; just need to put it back into t1
    	if (memorySize >= tapeSize) {
    		clear(t1);
    		t1.reset();
    		t2.reset();
        	//Move data from t2 to t1
        	for (int i = 0; i < tapeSize; i++) {
        		t1.write(t2.read());
        	}
        	return;
    	}
    
    	boolean needToTransferTot1 = false;			//Keeps track of whether or not t1 has the end result
    	int runNumber = 0;
    	for (int i= 0; i < tapeSize; i++) {
    		//Resets all of the current positions in the tapes to 0
    		t1.reset();				
    		t2.reset();
    		t3.reset();
    		t4.reset();
    		
    		//If an odd time, t2 and t3 are "in" and t1 and t4 are "out"
    		if (runNumber % 2 == 0) {
    			//don't need what was in t1 and t4
    			clear(t1);
    			clear(t4);
    			doRun(t2, t3, t1, t4, runNumber);
    		}
    		//If an even time, t1 and t4 are "in" and t2 and t3 are "out"
    		else {
    			//don't need what was in t2 and t3
    			clear(t2);
    			clear(t3);
    			doRun(t1, t4, t2, t3, runNumber);
    		}
    		
    		
    		//Check if the second out tape is empty
    		//If so, we are done
    		boolean empty = true;
      		if (runNumber % 2 == 0) {
      			empty = isEmpty(t4);
      			needToTransferTot1 = false;
    		}
    		else {
    			empty = isEmpty(t3);
    			needToTransferTot1 = true;
    		}
    		if (empty == true) {
    			break;
    		}
    		runNumber++;
    	}
    	
    	t1.reset();
    	if (needToTransferTot1) {
    		t2.reset();
        	//Move data from t2 to t1
        	for (int i = 0; i < tapeSize; i++) {
        		t1.write(t2.read());
        	}
    	}
    }

    public static void main(String[] args) {
        // Example of how to test
    	int random = -1;
    	int random2 = -1;
    	
    	//Test on random data
    	Random randomNum = new Random();
    	
    	while ((random < 0) || (random2 < 0)) {
    		random = randomNum.nextInt(1000);
    		random2 = randomNum.nextInt(1000);
    	}
    	System.out.println(random + " " + random2);
    	   	
    	int tapeSize = random;
    	int memorySize = random2;
        TapeSorter tapeSorter = new TapeSorter(memorySize, tapeSize);
        TapeDrive t1 = TapeDrive.generateRandomTape(tapeSize);
        TapeDrive t2 = new TapeDrive(tapeSize);
        TapeDrive t3 = new TapeDrive(tapeSize);
        TapeDrive t4 = new TapeDrive(tapeSize);

        t1.printTape();
        tapeSorter.sort(t1, t2, t3, t4);
        int last = Integer.MIN_VALUE;
        boolean sorted = true;
        for (int i = 0; i < tapeSize; i++) {
            int val = t1.read();
            sorted &= last <= val; // <=> sorted = sorted && (last <= val);
            last = val;
        }
        if (sorted)
            System.out.println("Sorted!");
        else
            System.out.println("Not sorted!");
        System.out.print("final t1: ");
        t1.printTape();
    } 

}
