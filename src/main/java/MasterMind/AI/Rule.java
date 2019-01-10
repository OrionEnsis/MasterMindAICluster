package MasterMind.AI;

import java.io.Serializable;

/**
 * This class represents a "Rule" all remaining answers must follow.  This is used for filtering out guesses that are
 * not potentially winnable.
 *
 * the incoming results use 0 to indicate number of non members, 1 to indicate must be present in location, and 2 for must
 * be present.  value that have not been evaluated will be marked with 4.
 * @author Jim Spagnola
 */
class Rule implements Serializable {
    private int mustHave;
    private int mayHave;
    private int[] guess;

    /**
     * the default constructor.  its must and may have been preparsed.
     *
     * @param guess the specific guess
     * @param must how many are correct
     * @param may how many colors are correct
     */
    Rule(int[] guess, int must, int may){
        this.mustHave = must;
        this.mayHave = may;
        this.guess = guess;
    }

    /**
     * a constructor that parses the results
     * @param guess the specific guess
     * @param results unparsed results
     */
    Rule(int[] guess, int[] results){
        this.guess = guess;
        for (int result : results) {
            if (result == 1) {
                mustHave++;
            } else {
                mayHave++;
            }
        }
    }

    /**
     * A method that examines if a given guess is valid.
     *
     * @param compareGuess the incoming guess
     * @return whether or not the result is valid
     */
    boolean followsRules(int[] compareGuess){
        int match = 0;
        int shift = 0;
        int[] dupeStopper = new int[compareGuess.length];
        for (int i = 0; i < compareGuess.length; i++) {
            if(guess[i] == compareGuess[i]){
                match++;
                dupeStopper[i] = 1;
            }
        }

        //TODO fix this section.
        for (int i = 0; i < compareGuess.length; i++) {
            if(dupeStopper[i] != 1){
                for (int j = 0; j < compareGuess.length; j++) {
                    if(dupeStopper[j] != 1 && compareGuess[j] != 2 && compareGuess[i] == guess[j]){
                        dupeStopper[j] = 2;
                        shift++;
                        break;
                    }
                }
            }
        }

        return match == mustHave;// && mayHave == shift;
    }

}
