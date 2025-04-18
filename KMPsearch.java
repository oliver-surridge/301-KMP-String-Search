/**
 * Knuth-Morris-Pratt (KMP) string search algorithm
 * @author Oliver Surridge - ID: 1607940
 */
import java.util .*;
import java.io .*;
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
                boolean oneArg = true;
                makeSkipArray(target, oneArg);
                //printSkipTable();
            }  else{
                String fileName = args[1];
                readFile(target, fileName);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Search the specified file line by line
     * @param pattern target substring
     * @param fileName name of the file to search
     */
    public static void readFile(String pattern, String fileName){
        boolean oneArg = false;
        int[][] skipArray = makeSkipArray(pattern, oneArg); //get the lps of the substring
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            String line;
            while ((line = reader.readLine()) != null) { //while there are still lines to read
                int index = kmpSearch(line, pattern, skipArray);
                if(index != -1){//if the pattern was found
                    System.out.println((index + 1) + " " + line);
                }    
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Searches for the first occurrence of the pattern in the given line using the Skip Array
     * @param line the line of the file being searched
     * @param pattern the pattern entered by the user
     * @param skipArray a 2d kmp skip array
     * @return the index of the pattern in the line, or -1 if not found.
     */
    public static int kmpSearch(String line, String pattern, int[][] skipArray) {
        int textIndex = 0;  
        int patternIndex = 0;  
        Map<Character, Integer> charToRow = charToRow(pattern);

        while (textIndex < line.length()) {
            char textChar = line.charAt(textIndex);   // Current character from the text
            char patternChar = pattern.charAt(patternIndex); // Current character from the pattern

            //character from pattern is found
            if (textChar == patternChar) {
                textIndex++;
                patternIndex++;
                //pattern has been found
                if (patternIndex == pattern.length()) {
                    return textIndex - patternIndex; // Return starting index of the match in the text
                }
            } else {
                //if some characters already matched and textChar exists in pattern
                if (patternIndex != 0 && charToRow.containsKey(textChar)) { 
                    int row = charToRow.get(textChar);//get the row index of the char
                    //use skipArray shift patternIndex
                    patternIndex = patternIndex - skipArray[row][patternIndex - 1];
                } else if (patternIndex != 0) { //if the textChar isn't in the pattern but there has already been a match
                    patternIndex = 0;
                } else {
                    // No partial match and no matching character in pattern,
                    textIndex++;
                }
            }
        }
        return -1; //line does not contain pattern
    }

    /**
     * builds an array of the longest possible prefix that is also a suffix 
     * @param pattern the pattern entered by the user
     * @return returns an array of the lengths of the lps for each char in the pattern
     */
    public static int[] buildLPS(String pattern) {
        //Get the longest prefix that is also a suffix
        int[] longestPS = new int[pattern.length()];
        int prefixLen = 0; 

        for (int suffixIndex = 1; suffixIndex < pattern.length();) {
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
        return longestPS;
    }

    /**
     * returns the unique characters from the input string 
     * @param pattern the pattern entered by the user
     * @return returns the unique characters from the input string 
     */
    public static Set<Character> getChars(String pattern){
        Set<Character> chars = new TreeSet<>();
        
        //Add the characters from the input string into the character set
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            chars.add(c);
        }

        return chars;
    }

    /**
     * maps unique characters in the pattern to rows
     * @param pattern the pattern entered by the user
     * @return map where each character in the pattern is associated with a row 
     */
    public static Map<Character, Integer> charToRow(String pattern){
        Set<Character>chars = getChars(pattern);
        //Map each character to a row index
        Map<Character, Integer> charToRow = new HashMap<>();
        int rowIndex = 0;
        for(char c : chars){
            charToRow.put(c, rowIndex++);
        }
        return charToRow;
    }
   
    /**
     * creates a 2d kmp skip array
     * @param pattern 
     * @param oneArg we want to print the array if only one argument was given
     * @return the skip array
     */
    public static int[][] makeSkipArray(String pattern, boolean oneArg){
        int patternLen = pattern.length();
        Set<Character>chars = getChars(pattern);
        Map<Character, Integer> charToRow = charToRow(pattern);

        //Create 2D Skip table with the rows as n(chars) and columns as patternLen
        int[][] skipArray = new int[chars.size()][patternLen];
        int[] longestPS = buildLPS(pattern);
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
        if(oneArg == true){
            printSkipArray(pattern, chars, charToRow, skipArray);
        }
        return skipArray;
    }

    /**
     * outputs the contents of the skip array in the assignment format to standard output
     * @param pattern the pattern entered by the user
     * @param chars the unique characters of the pattern
     * @param charToRow the row associated with each char
     * @param skipArray the 2d kmp skip array
     */
    public static void printSkipArray(String pattern, Set<Character> chars, Map<Character, Integer> charToRow, int[][] skipArray){
        int patternLen = pattern.length();
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

    

    

