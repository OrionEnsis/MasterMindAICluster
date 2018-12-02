package MasterMind.AI;

import java.util.Arrays;



//the incoming results use 0 to indicate number of non members, 1 to indicate must be present in location, and 2 for must
// be present.  value that have not been evaluated will be marked with 4.
public class Rule {
    int mustHave;
    int mayHave;
    int mustNotHave;
    int[] guess;

    public Rule(int[] guess, int[] results){
        this.guess = guess;
        for (int result : results) {
            if (result == 0) {
                mustNotHave++;
            } else if (result == 1) {
                mustHave++;
            } else {
                mayHave++;
            }

        }

    }

    public boolean followsRules(int[] compareGuess){
        int[] currentResults = new int[guess.length];
        Arrays.fill(currentResults,4);
        boolean isDifferent = false;
        for (int i = 0; i < guess.length; i++) {
            if(guess[i] != compareGuess[i]){
                isDifferent = true;
                break;
            }
        }
        if(isDifferent)
            return mustNotInclude(compareGuess, currentResults) && mustInclude(compareGuess,currentResults) && mustShift(compareGuess, currentResults);
        else
            return false;
    }

    boolean mustNotInclude(int[] compareGuess,int[] currentResults){
        int[] duplicateGuard1 = new int[guess.length];

        //Check to see if there is a number that are the correct amount changed.
        for (int i = 0; i < guess.length; i++) {
            for (int j = 0; j < guess.length; j++) {
                //if neither member has been evaluated before and they match
                if((currentResults[i] == 4 && currentResults[j] == 4) &&
                        guess[i] == compareGuess[j]){
                    currentResults[i]= 0;
                }
            }
        }

        return Arrays.stream(currentResults).filter(x->x==0).count() == mustNotHave;
    }

    boolean mustInclude(int[] compareGuess, int[] currentResults){

        //Check to see if there is a number that are the correct amount changed.
        for (int i = 0; i < guess.length; i++) {
            if(currentResults[i] == 4 && guess[i] == compareGuess[i]){
                currentResults[i] = 2;
            }
        }
        //if there are we aren't following the rules.
        return Arrays.stream(currentResults).filter(x->x==2).count() == mustHave;
    }

    boolean mustShift(int[] compareGuess, int[] currentResults){
        //check to see if there is a correct amount in the same position
        int[] duplicateGuard1 = new int[guess.length];

        //Check to see if there is a number that are the correct amount changed.
        for (int i = 0; i < guess.length; i++) {
            for (int j = 0; j < guess.length; j++) {
                if((currentResults[i] == 4 && currentResults[j] == 4) &&
                        guess[i] == compareGuess[j] && duplicateGuard1[j] != 1){
                    duplicateGuard1[j] = 1;
                    currentResults[i] = 2;
                }
            }
        }
        //if there are we aren't following the rules.
        return Arrays.stream(currentResults).filter(x->x==2).count() == mayHave;
    }
}
