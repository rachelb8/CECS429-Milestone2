package cecs429.text;
import java.util.List;
import java.util.ArrayList;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

public class MSOneTokenProcessor implements TokenProcessor {
 

    @Override
    public List<String> processToken(String token) {
        SnowballStemmer stemmer = new englishStemmer();
        List<String> processedTokens = new ArrayList<String>();
        //Converts string to an array of individual character strings
        //This makes operating on the string easier 
        ArrayList<String> baseStringArray = new ArrayList<>();
        for (char lChar : token.toCharArray()) {
            String charString = String.valueOf(lChar);
            baseStringArray.add(charString);
        }

        //If the ending character is non-alphanumeric
        if (!Character.isLetterOrDigit(baseStringArray.get(baseStringArray.size()-1).charAt(0))){
            int alphaNumWalker = baseStringArray.size()-1;
            //walk while the walker is not at the beginning of the string
            while (alphaNumWalker >= 0){
                //if the character at walker is not alphanumeric, remove it and iterate
                if (!Character.isLetterOrDigit(baseStringArray.get(alphaNumWalker).charAt(0))) {
                    baseStringArray.remove(alphaNumWalker);
                    alphaNumWalker--;
                } else { //otherwise the trailing non-alphanumeric characters have been removed
                    break;
                }
            }            
        }

        //If there still exists a string after trail character processing
        if (baseStringArray.size() != 0) {
            //if the character at the current start is non-alphanumeric
            if (!Character.isLetterOrDigit(baseStringArray.get(0).charAt(0))) {
                //continue to remove characters at the start of the string 
                while (!Character.isLetterOrDigit(baseStringArray.get(0).charAt(0))){
                    baseStringArray.remove(0);
                }
            }
        }       
        
        int i = 0;
        //Walking through the remaining string
        while (i < baseStringArray.size()) {
            //Get the chracter
            String currentChar  = baseStringArray.get(i);
            String aposString = "'";
            String quoteString = "\"";
            //and evaluate if the character is an apostrophe or quote
            boolean strEqualsApos = (currentChar.equals(aposString));
            boolean strEqualsQuote = (currentChar.equals(quoteString));

            //if it is one, remove it
            if (strEqualsApos || strEqualsQuote) {
                baseStringArray.remove(i);                
            }//otherwise walk to the next position
            else{
                i++;
            }
        }

        //Create a list to collect the split strings in case of hyphens
        List<String> allParts = new ArrayList<>();
        StringBuilder fullString = new StringBuilder();
        //Only if a hyphen is a part of the word
        if (baseStringArray.contains("-")){
            int k = 0;
            StringBuilder partialBuilder = new StringBuilder();
            //walk through the string
            while (k < baseStringArray.size()){
                String currentLetter = baseStringArray.get(k);
                //if the current letter is not a hyphen
                if (!currentLetter.equals("-")){
                    //add the letter to both the full string and the partial string
                    fullString.append(currentLetter);
                    partialBuilder.append(currentLetter);
                    k++;
                }
                else{//If it is a hyphen
                    //Add the current partial string to the allParts list
                    allParts.add(partialBuilder.toString());
                    //And start a new partial string
                    partialBuilder = new StringBuilder();
                    k++;
                }
            }
            //Add the last partial string after the last hyphen
            allParts.add(partialBuilder.toString());
                        
        }
        else{
            //Otherwise just construct the full string from the array
            for (String currentLetter : baseStringArray) {
                fullString.append(currentLetter);
            }
        }
        //In either case, add the complete string to the parts list as well
        allParts.add(fullString.toString());

        List<String> allStemmed = new ArrayList<>();
        //Stem each part within all parts
        for (String lString : allParts){
            stemmer.setCurrent(lString);
            stemmer.stem();
            allStemmed.add(stemmer.getCurrent());
        }
        
        
        //add the stemmed processed terms to a list
        for (String string : allStemmed) {
            processedTokens.add(string.toLowerCase());
        }
        
        //return processed terms
        return processedTokens;
    }

    

    

}


//========================== Code Graveyard ==============
//Here there be monsters
/*
// static private boolean isAlphaNumeric (char charArg){
    //     int asciiVal = (int) charArg;
        
    //     return  ((asciiVal >= 48 && asciiVal <= 57) //Is digit
    //         ||  (asciiVal >= 97 && asciiVal <= 122) //Is lower case alphabet
    //         ||  (asciiVal >= 65 && asciiVal <= 90));//Is uppercase alphabet
    // }

    // System.out.println("Adding - " + string);
            // System.out.println("=====================");
            // debugCount = DebugPrint(debugCount, "Adding processed token", ("Token - " + string.toLowerCase()));
    // System.out.println("=====================");

    // List<Integer> hyphenPosition = new ArrayList<>();
            // List<String> allParts = new ArrayList<>();
            // for (int k = 0; k < currentString.size(); k++){
            //     if (currentString.get(k) == "-") {
            //         hyphenPosition.add(k);
            //     }
            // }

     // if (baseStringArray.get(0).toLowerCase().equals("k") && baseStringArray.get(1).toLowerCase().equals("i")&& baseStringArray.get(2).toLowerCase().equals("w")){
        //     System.out.println();;
        // }        // debugCount = DebugPrint(debugCount, "base array cons", ("ToString() - " + baseStringArray.toString()));
        
        // ArrayList<String> baseStringArray = (ArrayList<String>) baseStringArray.clone();
        //System.out.println();

        public static void main(String[] args) {
        MSOneTokenProcessor msOne = new MSOneTokenProcessor();
        ArrayList<String> testTerms = new ArrayList<>();

        String StringA = "$.(,A,Apple,.,,.@#$^&()_{}|<>:?!~`)";        
        String StringB = "(//Recognize//.....)";
        String StringC = "(\\Only\\";
        String StringD = "&.WAP&";
        String StringE = "!OMAE'WAMO'SHINDERU-NANI-";
        String StringF = "$10";
        String StringG = "";
        String StringH = "This is a full sentence with no special characters but has 2 different digits like 6";
        String StringI = "Hewlett-Packard-Computing";
        String StringJ = "Hewlett-\"Packard\"-Computing!";
        String StringK = "Say\"Sike\"rightnow!";
        String StringL = "\"Sike\"";        
        String StringM = "'apos'trefe's'";
        String StringN = "\"Quotation\"Ma\"rks\"";
        //String StringO = "Hewlett-\"Packard\"-Computing!";
        testTerms.add(StringA);
        testTerms.add(StringB);
        testTerms.add(StringC);
        testTerms.add(StringD);
        testTerms.add(StringE);
        testTerms.add(StringF);
        //testTerms.add(StringG);
        //testTerms.add(StringH);
        testTerms.add(StringI);
        testTerms.add(StringJ);
        testTerms.add(StringK);
        testTerms.add(StringL);
        testTerms.add(StringM);
        testTerms.add(StringN);

        List<String> processedStrings = new ArrayList<>();
        for (String string : testTerms) {
            for (String lString : msOne.processToken(string)){
                processedStrings.add(lString);
            }
        }     
        for (String lString : processedStrings){
            System.out.println(lString);
        }
    }
    private static int DebugPrint(int repItr, String sectStr, String comment){
        System.out.println("=================");
        System.out.println("Debug Marker - " + repItr);
        System.out.println("=================");
        System.out.println("Section - " + sectStr);
        if (comment != null){
            System.out.println("---------------");
            System.out.println(comment);
        }
        System.out.println("_________________");
        return ++repItr;
    }
 */