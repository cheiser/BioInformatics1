
//
//To compile this Java program, type:
//
//	javac GlobalAlignment.java
//
//To run the program, type:
//
//	java GlobalAlignment
//
import java.lang.System;
import java.util.ArrayList;
import java.util.Iterator;

public class Testing {

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
	
//		String X = "ATCGAT";
//		String Y = "ATACGT";
		
		String X = "ATGT";
		String Y = "AGTT";
		
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
		
		ArrayList<char[]> sStrings = new ArrayList<>();
		ArrayList<char[]> tStrings = new ArrayList<>();
		
//		System.out.println("total number of optimal solutions: " + allOptimalAlignments(F, sStrings, tStrings,
//				new char[alignmentLength], new char[alignmentLength], X, Y, 0, F.length, F[0].length, 0, 0));
		
		System.out.println("total number of optimal solutions: " + allOptimalAlignmentsWrapper(F, sStrings, tStrings, alignmentLength, X, Y));
		Iterator<char[]> sIterator = sStrings.iterator();
		Iterator<char[]> tIterator = tStrings.iterator();
		
		while(sIterator.hasNext() && tIterator.hasNext()){
			char[] tArr = tIterator.next();
			char[] sArr = sIterator.next();
			System.out.println("\nt & s:");
			for(i = 0; i < sArr.length; i++){
				System.out.print(sArr[i]);
			}
			
			System.out.println();
			for(i = 0; i < tArr.length; i++){
				System.out.print(tArr[i]);
			}
			System.out.println();
		}
		
//		char[] temp3 = {'a', 'b', 'c', 'd', '-', 'e'};
//		char[] temp4 = new char[8];
//		reverseCopy(temp4, temp3, temp3.length);
//		System.out.println();
//		for(i = 0; i < temp4.length; i++){
//			System.out.print(temp4[i]);
//		}
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
 
 public static int allOptimalAlignmentsWrapper(int[][] F, ArrayList<char[]> s, ArrayList<char[]> t, int alignmentLength, String textS, String textT) {
 	
 	return allOptimalAlignments(F, s, t,
				new char[alignmentLength*2], new char[alignmentLength*2], textS, textT, 0, textS.length(),
				textT.length(), 0, 0, textT.length()-1, textS.length()-1);
 }
 
 /**
  * Returns the number of optimal alignments for the given score matrix
  * @param F the scorematrix we want to get all the optimal alignments for
  * @param s one of the strings represented by the optimal alignment, the first index
  * represents one number of the optimal alignments and the second index are all the
  * characters for that alignment
  * @param t one of the strings represented by the optimal alignment, the first index
  * represents one number of the optimal alignments and the second index are all the
  * characters for that alignment
  * @return the number of optimal alignments
  * 
  * ** NOTE TO SELF: the size of s and t can be determined by the variable alignmentLength
  * instead of having s and t as char[][] have it as ArrayList<char[]> instead??
  */
 public static int allOptimalAlignments(int[][] F, ArrayList<char[]> s, ArrayList<char[]> t, char[] tempS,
 		char[] tempT, String textS, String textT, int numberOfSolutions, int i, int j,
 		int indexT, int indexS, int textTIndex, int textSIndex) {
 	// only move to a "square" if it makes sense eg. if the Pij = 5 and pi(j-1) = -50 then
 	// it would not make sense to go to the left as that is not the best choice, there must
 	// be either the square diagonally or the square above that gives the score 5.
 	// start from bottom right, don't stop until top left is reached.
 	if(i == 0 && j == 0) {
 		char[] temporaryS = new char[tempS.length];
 		char[] temporaryT = new char[tempT.length];
 		// System.arraycopy(tempS, 0, temporaryS, 0, indexS);
 		// System.arraycopy(tempT, 0, temporaryT, 0, indexT);
 		reverseCopy(temporaryS, tempS, indexS);
 		reverseCopy(temporaryT, tempT, indexT);
 		s.add(temporaryS);
 		t.add(temporaryT);
 		return 1 + numberOfSolutions;
 	}
 	
 	if((i > 0 && j > 0) && ((F[i-1][j-1]-1 == F[i][j]) || (F[i-1][j-1]+2 == F[i][j]))){
 		tempT[indexT] = textT.charAt(textTIndex);
 		tempS[indexS] = textS.charAt(textSIndex);
 		numberOfSolutions = allOptimalAlignments(F, s, t, tempS, tempT, textS, textT, numberOfSolutions,
 				i-1, j-1, indexT+1, indexS+1, textTIndex-1, textSIndex-1);
 	}
 	
 	if((i > 0 && j > 0) && (F[i-1][j]-2 == F[i][j])){
 		tempT[indexT] = '-';
 		numberOfSolutions = allOptimalAlignments(F, s, t, tempS, tempT, textS, textT, numberOfSolutions,
 				i-1, j, indexT+1, indexS, textTIndex, textSIndex);
 	}
 	
 	if((i > 0 && j > 0) && (F[i][j-1]-2 == F[i][j])){
 		tempS[indexS] = '-';
 		numberOfSolutions = allOptimalAlignments(F, s, t, tempS, tempT, textS, textT, numberOfSolutions,
 				i, j-1, indexT, indexS+1, textTIndex, textSIndex);
 	}
 	
 	return numberOfSolutions;
 }
 
 /*
 public static int allOptimalAlignments(int[][] F, ArrayList<char[]> s, ArrayList<char[]> t, char[] tempS,
 		char[] tempT, String textS, String textT, int numberOfSolutions, int i, int j,
 		int indexT, int indexS, int textTIndex, int textSIndex) {
 	// only move to a "square" if it makes sense eg. if the Pij = 5 and pi(j-1) = -50 then
 	// it would not make sense to go to the left as that is not the best choice, there must
 	// be either the square diagonally or the square above that gives the score 5.
 	// start from bottom right, don't stop until top left is reached.
 	if(i == 0 && j == 0) {
 		char[] temporaryS = new char[tempS.length];
 		char[] temporaryT = new char[tempT.length];
 		// System.arraycopy(tempS, 0, temporaryS, 0, indexS);
 		// System.arraycopy(tempT, 0, temporaryT, 0, indexT);
 		reverseCopy(temporaryS, tempS, indexS);
 		reverseCopy(temporaryT, tempT, indexT);
 		System.out.print("bottoming out with: ");
 		printArray(tempS, indexS);
 		s.add(temporaryS);
 		t.add(temporaryT);
 		return 1 + numberOfSolutions;
 	}
// 	System.out.println("i: " + i + " j: " + j + " F[i][j]: " + F[i][j]); // TODO: REMOVE
 	// NO GAP INSERT CHARACTERS AS IS
 	if(((i > 0) && (j > 0)) && (F[i-1][j-1] == F[i][j]+1 || F[i-1][j-1] == F[i][j]-2)){
//     	System.out.println("i-1, j-1: " + i + " " + j + " F[i-1][j-1]: " + F[i-1][j-1]); // TODO: REMOVE
//     	System.out.println("textT: " + textT + " textS: " + textS); // TODO: REMOVE
 		// one branch, follow it
 		tempT[indexT] = (textTIndex >= 0 ? textT.charAt(textTIndex) : ' '); // j-1
 		tempS[indexS] = (textSIndex >= 0 ? textS.charAt(textSIndex) : ' ');
 		System.out.println("adding to T: " + textT.charAt(textTIndex) + " at " + i + ", " + 
 	        	j + " numberofsolutions: " + numberOfSolutions +
 	        	"textSIndex: " + textSIndex + " textTIndex: " + textTIndex); // TODO: REMOVE
     	printArray(tempT, indexT+1);
 		numberOfSolutions = allOptimalAlignments(F, s, t, tempS, tempT, textS, textT, numberOfSolutions,
 				i-1, j-1, indexT+1, indexS+1, textTIndex-1, textSIndex-1);
 	}
 	
 	// GAP IN textS
 	if(((i > 0)) && (F[i-1][j] == F[i][j]+2)){
     	System.out.println("adding to s: '-' i-1: " + i + " j: " + (j-1) + " F[i-1][j]: " + F[i-1][j]); // TODO: REMOVE
 		// one branch, follow it
 		tempS[indexS] = '-';
 		System.out.println("GAPT: indexT: " + indexT + " textTIndex: " + textTIndex);
 		tempT[indexT] = (textTIndex >= 0 ? textT.charAt(textTIndex) : ' ');
 		numberOfSolutions = allOptimalAlignments(F, s, t, tempS, tempT, textS, textT, numberOfSolutions,
 				i-1, j, indexT+1, indexS+1, textTIndex-1, textSIndex);
 	}
 	
 	// GAP IN textT
 	if(((j > 0)) && (F[i][j-1] == F[i][j]+2)){
 		System.out.println("adding to t(aka down): '-' i: " + i + " j-1: " + (j-1) + " F[i][j]: " + F[i][j-1]); // TODO: REMOVE
 		// one branch, follow it
 		tempT[indexT] = '-';
 		System.out.println("GAPS: indexS: " + indexS + " textSIndex: " + textSIndex);
 		tempS[indexS] = (textSIndex >= 0 ? textS.charAt(textSIndex) : ' ');
 		numberOfSolutions = allOptimalAlignments(F, s, t, tempS, tempT, textS, textT, numberOfSolutions,
 				i, j-1, indexT+1, indexS+1, textTIndex, textSIndex-1);
 	}
 	
 	return numberOfSolutions;
 }/**/
 
 private static void reverseCopy(char[] to, char[] from, int length){
 	for(int i = 0; i < length; i++){
 		to[i] = from[(length - i)-1];
 	}
 }
 
 private static void printArray(char[] array1, int lengthArr){
 	for(int i = 0; i < lengthArr; i++){
 		System.out.print(array1[i]);
 	}
 	System.out.println();
 }
 
 /*
  public static int allOptimalAlignments(int[][] F, ArrayList<char[]> s, ArrayList<char[]> t, char[] tempS,
    		char[] tempT, String textS, String textT, int numberOfSolutions, int i, int j,
    		int indexT, int indexS, int textTIndex, int textSIndex) {
    	if(i == 0 && j == 0) {
    		char[] temporaryS = new char[tempS.length];
    		char[] temporaryT = new char[tempT.length];
    		// System.arraycopy(tempS, 0, temporaryS, 0, indexS);
    		// System.arraycopy(tempT, 0, temporaryT, 0, indexT);
    		reverseCopy(temporaryS, tempS, indexS);
    		reverseCopy(temporaryT, tempT, indexT);
    		System.out.print("bottoming out with: ");
    		printArray(tempS, indexS);
    		s.add(temporaryS);
    		t.add(temporaryT);
    		return 1 + numberOfSolutions;
    	}
//    	System.out.println("i: " + i + " j: " + j + " F[i][j]: " + F[i][j]); // TODO: REMOVE
    	// NO GAP INSERT CHARACTERS AS IS
    	if(((i > 0) && (j > 0)) && (F[i-1][j-1] == F[i][j]+1 || F[i-1][j-1] == F[i][j]-2)){
//        	System.out.println("i-1, j-1: " + i + " " + j + " F[i-1][j-1]: " + F[i-1][j-1]); // TODO: REMOVE
//        	System.out.println("textT: " + textT + " textS: " + textS); // TODO: REMOVE
    		// one branch, follow it
    		tempT[indexT] = (textTIndex >= 0 ? textT.charAt(textTIndex) : ' '); // j-1
    		tempS[indexS] = (textSIndex >= 0 ? textS.charAt(textSIndex) : ' ');
    		System.out.println("adding to T: " + (textTIndex >= 0 ? textT.charAt(textTIndex) : ' ') + " at " + i + ", " + 
    	        	j + " numberofsolutions: " + numberOfSolutions +
    	        	"textSIndex: " + textSIndex + " textTIndex: " + textTIndex); // TODO: REMOVE
        	printArray(tempT, indexT+1);
    		numberOfSolutions = allOptimalAlignments(F, s, t, tempS, tempT, textS, textT, numberOfSolutions,
    				i-1, j-1, indexT+1, indexS+1, textTIndex-1, textSIndex-1);
    	}
    	
    	// GAP IN textS
    	if(((i > 0)) && (F[i-1][j] == F[i][j]+2)){
        	System.out.println("adding to s: '-' i-1: " + i + " j: " + (j-1) + " F[i-1][j]: " + F[i-1][j]); // TODO: REMOVE
    		// one branch, follow it
    		tempS[indexS] = '-';
    		System.out.println("GAPT: indexT: " + indexT + " textTIndex: " + textTIndex);
    		tempT[indexT] = (textTIndex >= 0 ? textT.charAt(textTIndex) : ' ');
    		numberOfSolutions = allOptimalAlignments(F, s, t, tempS, tempT, textS, textT, numberOfSolutions,
    				i-1, j, indexT+1, indexS+1, textTIndex-1, textSIndex-1);
    	}
    	
    	// GAP IN textT
    	if(((j > 0)) && (F[i][j-1] == F[i][j]+2)){
    		System.out.println("adding to t(aka down): '-' i: " + i + " j-1: " + (j-1) + " F[i][j]: " + F[i][j-1]); // TODO: REMOVE
    		// one branch, follow it
    		tempT[indexT] = '-';
    		System.out.println("GAPS: indexS: " + indexS + " textSIndex: " + textSIndex);
    		tempS[indexS] = (textSIndex >= 0 ? textS.charAt(textSIndex) : ' ');
    		numberOfSolutions = allOptimalAlignments(F, s, t, tempS, tempT, textS, textT, numberOfSolutions,
    				i, j-1, indexT+1, indexS+1, textTIndex-1, textSIndex-1);
    	}
    	
    	return numberOfSolutions;
    }
    /**/
}

