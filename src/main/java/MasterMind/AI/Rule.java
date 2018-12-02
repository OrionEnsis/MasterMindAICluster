package MasterMind.AI;

import java.util.Arrays;



//the incoming results use 0 to indicate number of non members, 1 to indicate must be present in location, and 2 for must
// be present.  value that have not been evaluated will be marked with 4.
public class Rule {
    int mustHave;
    int mayHave;
    int mustNotHave;
    int[] guess;

    public Rule(int[] guess, int must, int may){
        this.mustHave = must;
        this.mayHave = may;
        this.guess = guess;
        mustNotHave = 0;
    }
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
        /*
        int[] currentResults = new int[guess.length];
        Arrays.fill(currentResults,4);
        boolean isDifferent = false;
        for (int i = 0; i < guess.length; i++) {
            if(guess[i] != compareGuess[i]){
                isDifferent = true;
                break;
            }
        }
        if(isDifferent) {

//            boolean must =  mustInclude(compareGuess, currentResults);
//            boolean may =  mustShift(compareGuess, currentResults);
//            boolean mustNot =  mustNotInclude(compareGuess, currentResults);
//            System.out.println("mustNot = " + mustNot);
//            System.out.println("must = " + must);
//            System.out.println("may = " + may);
            return mustInclude(compareGuess, currentResults) && mustShift(compareGuess, currentResults) && mustNotInclude(compareGuess, currentResults);
        }
        else
            return false;
            */


    }

    boolean mustNotInclude(int[] compareGuess,int[] currentResults){
        int temp[] = {4,4,4,4};

        //Check to see if there is a number that are the correct amount changed.
        for (int i = 0; i < guess.length; i++) {
            if(currentResults[i] == 4) {
                boolean foundDupe = true;
                for (int j = 0; j < guess.length; j++) {
                    //if neither member has been evaluated before and they match
                    if (guess[i] == compareGuess[j]) {
                        foundDupe = false;
                        break;
                    }
                }
                if(foundDupe){
                    currentResults[i] = 0;
                }
            }
        }

        return Arrays.stream(currentResults).filter(x->x==0).count() == mustNotHave;
    }

    boolean mustInclude(int[] compareGuess, int[] currentResults){
        int temp[] = {4,4,4,4};
        //Check to see if there is a number that are the correct amount changed.
        for (int i = 0; i < guess.length; i++) {
            if(currentResults[i] == 4 && guess[i] == compareGuess[i]){
                currentResults[i] = 1;
            }
        }
        //if there are we aren't following the rules.
        return Arrays.stream(currentResults).filter(x->x==1).count() == mustHave;
    }

    boolean mustShift(int[] compareGuess, int[] currentResults){
        int temp[] = {4,4,4,4};
        //check to see if there is a correct amount in the same position
        int[] duplicateGuard1 = new int[guess.length];

        //Check to see if there is a number that are the correct amount changed.
        for (int i = 0; i < guess.length; i++) {
            for (int j = 0; j < guess.length; j++) {
                if(i != j &&(currentResults[i] == 4 && currentResults[j] == 4) &&
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
