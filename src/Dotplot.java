//
// To compile this Java program, type:
//
//    javac Dotplot.java
//
// To run the program, type:
//
//    java Dotplot
//


public class Dotplot {
    public static void main(String[] args) {
        String sequence1 = "DOROTHYHODGKIN";
        String sequence2 = "DOROTHYCROWFOOTHODGKIN";

        int length1 = sequence1.length();
        int length2 = sequence2.length();

        for ( int i=0 ; i<length1 ; i++ ) {
            for ( int j=0 ; j<length2 ; j++ ) {
                if ( sequence1.charAt(i) == sequence2.charAt(j) ) {
                    System.out.print(sequence1.charAt(i));
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
}
