import java.util .*;
public class KMPsearch {

    public static void main(String[] args) {
        //need one or two arguments
        if(args.length < 1 || args.length > 2){
            System.err.println("Usage: java KMPsearch <target string> <filename.txt>");
            System.exit(1);
        }

        String target = args[0];
        
        //If the program is invokes with just target string, we must
        //print out the skip table
        try {
            if(args.length == 1){
                makeSkipArray(target);
                //printSkipTable();
            }  else{
                makeSkipArray(target);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void makeSkipArray(String pattern){
        int patternLen = pattern.length();
        Set<Character> chars = new TreeSet<>();
        
        //Add the characters from the input string into the character set
        for (int i = 0; i < patternLen; i++) {
            char c = pattern.charAt(i);
            chars.add(c);
        }

        //Map each character to a row index
        Map<Character, Integer> charToRow = new HashMap<>();
        int rowIndex = 0;
        for(char c : chars){
            charToRow.put(c, rowIndex++);
        }

        //Create 2D Skip table with the rows as n(chars) and columns as patternLen
        int[][] skipArray = new int[chars.size()][patternLen];

        //Get the longest prefix that is also a suffix
        int[] longestPS = new int[patternLen];
        int prefixLen = 0; 

        for (int suffixIndex = 1; suffixIndex < patternLen;) {
            if(pattern.charAt(suffixIndex) == pattern.charAt(prefixLen)){ 
                //characters matched so iterate the prefixLength
                prefixLen++; 
                longestPS[suffixIndex] = prefixLen; //store the length in the array
                suffixIndex++;
            } else if(prefixLen != 0){ //mismatch at prefixLen > 0
                prefixLen = longestPS[prefixLen -1]; //see if the previous prefix works
            } else{ //mismatch where the prefixLen < 0
                longestPS[suffixIndex] = 0;
                suffixIndex++;
            }
        }

        //Add the longestPS values to the skip array
        for (char c : chars) {
            int row = charToRow.get(c);
            for (int i = 0; i < patternLen; i++) {
                int skipVal = i + 1;
                int prefixIndex = i;

                while (prefixIndex > 0 && pattern.charAt(prefixIndex) != c) {
                    prefixIndex = longestPS[prefixIndex - 1];
                }

                if (pattern.charAt(prefixIndex) == c) {
                    skipVal = i - prefixIndex;
                }

                skipArray[row][i] = skipVal;
            }
        }

        //Create the header row
        System.out.print("*,");
        for (int i = 0; i < patternLen-1; i++) {
            System.out.print(pattern.charAt(i) + ",");
        }
        System.out.print(pattern.charAt(patternLen -1));
        System.out.println();

        // Character rows
        for (char c : chars) {
            int row = charToRow.get(c); //get the row of the character
            System.out.print(c + ",");
            for (int i = 0; i < patternLen - 1; i++) {
                System.out.print(skipArray[row][i] + ",");
            }
            System.out.println(skipArray[row][patternLen - 1]);
        }

        // Row for other characters
        System.out.print("*,");
        for(int i = 1; i < patternLen; i++){
            System.out.print(i + ",");
        }
        System.out.print(patternLen);
        System.out.println();

    }
}