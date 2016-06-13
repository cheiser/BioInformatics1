public class Levenshtein {

	public static final int MAX_LENGTH	= 100;

	public static final int MATCH_SCORE	= 2;
	public static final int MISMATCH_SCORE	= -1;
	public static final int GAP_PENALTY	= -2;

	public static final int STOP		= 0;
	public static final int UP		= 1;
	public static final int LEFT		= 2;
	public static final int DIAG		= 3;

    public static void main(String[] args) {

		int i, j;
		int alignmentLength, score, tmp, pIdentity = 0;
	
		String X = "ATCGAT";
		String Y = "ATACGT";
	
		int F[][] = new int[MAX_LENGTH+1][MAX_LENGTH+1];     /* score matrix */
		int trace[][] = new int[MAX_LENGTH+1][MAX_LENGTH+1]; /* trace matrix */	
		char[] alignX = new char[MAX_LENGTH*2];	/* aligned X sequence */
		char[] alignY = new char[MAX_LENGTH*2];	/* aligned Y sequence */
	
		int m = X.length();
		int n = Y.length();
	
	
		//
		// Initialise matrices
		//
	
		F[0][0] = 0;
		trace[0][0] = STOP;
		for ( i=1 ; i<=m ; i++ ) {
			F[i][0] = F[i-1][0] + GAP_PENALTY;
			trace[i][0] = STOP;
		}
		for ( j=1 ; j<=n ; j++ ) {
			F[0][j] = F[0][j-1] + GAP_PENALTY;
			trace[0][j] = STOP;
		}
	
	
		//
		// Fill matrices
		//
	
		for ( i=1 ; i<=m ; i++ ) {
	
			for ( j=1 ; j<=n ; j++ ) {
	
				if ( X.charAt(i-1)==Y.charAt(j-1) ) {
					score = F[i-1][j-1] + MATCH_SCORE;
				} else {
					score = F[i-1][j-1] + MISMATCH_SCORE;
				}
				trace[i][j] = DIAG;
	
				tmp = F[i-1][j] + GAP_PENALTY;
				if ( tmp>score ) {
					score = tmp;
					trace[i][j] = UP;
				}
	
				tmp = F[i][j-1] + GAP_PENALTY;
				if( tmp>score ) {
					score = tmp;
					trace[i][j] = LEFT;
				}
	
				F[i][j] = score;
			}
		}
	
	
		//
		// Print score matrix
		//
	
		System.out.println("Score matrix:");
		System.out.print("      ");
		for ( j=0 ; j<n ; ++j ) {
			System.out.print("    " + Y.charAt(j));
		}
		System.out.println();
		for ( i=0 ; i<=m ; i++ ) {
			if ( i==0 ) {
				System.out.print(" ");
			} else {
				System.out.print(X.charAt(i-1));
			}
			for ( j=0 ; j<=n ; j++ ) {
				System.out.format("%5d", F[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	
	
		//
		// Trace back from the lower-right corner of the matrix
		//
	
		i = m;
		j = n;
		alignmentLength = 0;
	
		while ( trace[i][j] != STOP ) {
	
			switch ( trace[i][j] ) {
	
				case DIAG:
					alignX[alignmentLength] = X.charAt(i-1);
					alignY[alignmentLength] = Y.charAt(j-1);
					i--;
					j--;
					alignmentLength++;
					break;
	
				case LEFT:
					alignX[alignmentLength] = '-';
					alignY[alignmentLength] = Y.charAt(j-1);
					j--;
					alignmentLength++;
					break;
	
				case UP:
					alignX[alignmentLength] = X.charAt(i-1);
					alignY[alignmentLength] = '-';
					i--;
					alignmentLength++;
			}
		}
	
	
		//
		// Unaligned beginning
		//
	
		while ( i>0 ) {
			alignX[alignmentLength] = X.charAt(i-1);
			alignY[alignmentLength] = '-';
			i--;
			alignmentLength++;
		}
	
		while ( j>0 ) {
			alignX[alignmentLength] = '-';
			alignY[alignmentLength] = Y.charAt(j-1);
			j--;
			alignmentLength++;
		}
	
	
		//
		// Print alignment
		//
	
		for ( i=alignmentLength-1 ; i>=0 ; i-- ) {
			System.out.print(alignX[i]);
		}
		
		System.out.println();
		// add line between the matched characters
		for ( i=alignmentLength-1 ; i>=0 ; i-- ) {
			if(alignX[i] == alignY[i]){
				System.out.print("|");
				pIdentity += 1;
			}
			else
				System.out.print(" ");
		}
		System.out.println();
		
		for ( i=alignmentLength-1 ; i>=0 ; i-- ) {
			System.out.print(alignY[i]);
		}
		System.out.println();
		
		// I decided to calculate the percent identity using the length of the shortest
		// sequence (m or n)
		System.out.println("percent identity: " + (pIdentity / (double)(m < n ? m : n)));
		System.out.println("hamming distance: " + hammingDistance(alignX, alignY));
		System.out.println("levenshtein distance: " + levenshteinDistance(alignX, alignY));
    }
    
    // TODO: WHAT BEHAVIOR ON STRINGS OF UNEQUAL LENGTH? THROW EXCEPTION?
    public static int hammingDistance(char[] alignedX, char[] alignedY){
    	int missmatched = 0;
    	
    	for(int i = 0; i < alignedX.length; i++){
    		if(alignedX[i] != alignedY[i])
    			missmatched++; // chars at "i" does not match, increase counter by one
    		
    	}
    	
    	return missmatched;
    }
    
    public static int levenshteinDistance(char[] alignedX, char[] alignedY){
    	return levenshteinHelper(alignedX, alignedY, usedLength(alignedX), usedLength(alignedY), 
    			0, 0, 0);
    }
    
    private static int usedLength(char[] arr){
    	int i = 0;
    	char x = '-';
//    	System.out.println("" + i + " " + (int)x);
    	for(i = 0; i < arr.length && ((int)arr[i] > 44 && (int)arr[i] < 150) ; i++);
//    		System.out.println("" + i + " : " + arr[i]);
    	
//    	System.out.println("RETURNING: " + i);
    	return i;
    }
    
    // we assume that all chars from 0 to startIndex matches the other char array...
    public static int levenshteinHelper(char[] alignedX, char[] alignedY, int endX, int endY,
    		int startIndexX, int startIndexY, int numberOfChanges){    	
    	if(startIndexX == endX){
//    		System.out.println("RETURNING: " + numberOfChanges + (endY - startIndexY));
    		return numberOfChanges + (endY - startIndexY); // we remove all characters that is left
    	}
    	
    	if(startIndexY == endY){
//    		System.out.println("RETURNING: " + numberOfChanges + (endX - startIndexX));
    		return numberOfChanges + (endX - startIndexX);
    	}
    	
    	/*
    	 * We have four options here
    	 * 1: the two characters from the arrays are the same and we don't do anything (just
    	 * 	increase counters).
    	 * 2: we replace a character
    	 * 3: we insert a new character
    	 * 4: we remove a character
    	 */
    	int minLength = Integer.MAX_VALUE;
    	int tempLength = Integer.MAX_VALUE;
    	
    	if(alignedX[startIndexX] == alignedY[startIndexY]){
    		minLength = levenshteinHelper(alignedX, alignedY, endX, endY, startIndexX+1, startIndexY+1, numberOfChanges);
    	}
    	
    	// replace character
    	tempLength = levenshteinHelper(alignedX, alignedY, endX, endY, startIndexX+1, startIndexY+1, numberOfChanges+1);
    	if(minLength > tempLength)
    		minLength = tempLength;
    	
    	// insert new character (move other chars back)
    	tempLength = levenshteinHelper(alignedX, alignedY, endX, endY, startIndexX+1, startIndexY, numberOfChanges+1);
    	if(minLength > tempLength)
    		minLength = tempLength;
    	// delete character (move other chars forward)
    	tempLength = levenshteinHelper(alignedX, alignedY, endX, endY, startIndexX, startIndexY+1, numberOfChanges+1);
    	if(minLength > tempLength)
    		minLength = tempLength;
    	
    	return minLength;
    }
    
}
