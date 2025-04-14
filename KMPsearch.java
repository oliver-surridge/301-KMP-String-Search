import java.io .*;
import java.util .*;
import java.util.regex.Pattern;
public class KMPsearch {

    public static void main(String[] args) {
        //need one or two arguments
        if(args.length < 1 || args.length > 2){
            System.err.println("Usage: java KMPsearch <target string> <filename.txt>");
            System.exit(1);
        }

        String target = args[0];
        boolean oneArg = false;
        
        //If the program is invokes with just target string, we must
        //print out the skip table
        try {
            if(args.length == 1){
                oneArg = true;
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

        //Get the longest possible substring
        int[] lps = new int[pattern.length()];
        int prefixLen = 0; 

        for (int suffixIndex = 1; suffixIndex < patternLen;) {
            if(pattern.charAt(suffixIndex) == pattern.charAt(prefixLen)){ 
                //characters matched so iterate the prefixLength
                prefixLen++; 
                lps[suffixIndex] = prefixLen; //store the length in the array
                suffixIndex++;
            } else if(prefixLen != 0){ //mismatch at prefixLen > 0
                prefixLen = lps[prefixLen -1]; //see if the previous prefix works
            } else{ //mismatch where the prefixLen < 0
                lps[suffixIndex] = 0;
                suffixIndex++;
            }
        }
        
        //Create the header row
        System.out.print("*,");
        for (int i = 0; i < patternLen-1; i++) {
            System.out.print(pattern.charAt(i) + ",");
        }
        System.out.print(pattern.charAt(patternLen -1));
        System.out.println();

        //For each character in the pattern
        for(char c: chars){
            System.out.print(c + ",");
            for(int i= 0; i< patternLen; i++){
                int skipVal = i+1; //default skip to i+1
                int prefixIndex = i;

                while (prefixIndex > 0 && pattern.charAt(prefixIndex) != c) { //if the char does not match the letter of the pattern at the curr index
                    prefixIndex = lps[prefixIndex -1]; //fall back to the shorter known prefix
                }
                if(pattern.charAt(prefixIndex) == c){
                    skipVal = i - prefixIndex;
                }
                System.out.print(skipVal);
                if(i < patternLen - 1){
                    System.out.print(",");
                }

            }
            System.out.println();
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