package MasterMind;

import java.io.Serializable;
import java.util.*;

/**
 * This Class represents a game of mastermind.  The game is played with a series of multi-colored pegs.  A possible code
 * is selected as the answer.  A player must enter in a potential combination.  They are then given a series of white
 * and black pegs.  White indicates a peg is in the wrong location.  The order does not matter for these results.
 *
 *
 * It returns its guesses and results in the form of an int[].  0 represents
 * not correct, 1 represents correct (white), 2 represents wrong location (black).
 * @author Jim Spagnola
 */
public class Game implements Serializable {
    private int currentGuesses;
    private int guesses;
    private int colors;
    private int pegs;
    private int[] answer;
    private boolean hasWon = false;
    private boolean hasLost = false;
    public List<int[]> pastGuesses;
    public List<int[]> pastResults;

    /**
     * The constructor.
     *
     * @param guesses the number of guesses the player is permitted
     * @param colors the number of different colors that can be played
     * @param pegs the number of pegs in the code.
     */
    Game(int guesses, int colors, int pegs){
        this.currentGuesses = 0;
        this.guesses = guesses;
        this.colors = colors;
        this.pegs = pegs;
        pastGuesses = new ArrayList<>();
        pastResults = new ArrayList<>();
        generateAnswer();
    }

    private void generateAnswer(){
        answer = new int[pegs];
        Random random = new Random();
        for (int i = 0; i < pegs; i++) {
            answer[i] = random.nextInt(colors);
        }
    }

    int[] guess(int[] guess){
        int[] result = new int[guess.length];
        //correct answers
        for (int i = 0; i < answer.length; i++) {
            if(guess[i] == answer[i]){
                result[i] = 1;
            }
        }

        for (int i = 0; i < answer.length; i++) {
            if(result[i] != 1){
                for (int j = 0; j < answer.length; j++) {
                    if(result[j] != 1 && result[j] != 2 && answer[i] == guess[j]){
                        result[j] = 2;
                        break;
                    }
                }
            }
        }


        Arrays.sort(result);
        for(int i = 0; i < result.length / 2; i++)
        {
            int temp = result[i];
            result[i] = result[result.length - i - 1];
            result[result.length - i - 1] = temp;
        }

        boolean b = true;
        for (int aResult : result) {
            if (aResult != 1) {
                b = false;
                break;
            }
        }
        hasWon = b;
        currentGuesses++;
        if(guesses == currentGuesses)
            hasLost = true;
        pastGuesses.add(guess);
        pastResults.add(result);
        return result;
    }

    int[] getAnswer(){
        return answer;
    }

    boolean checkWin() {
        return hasWon;
    }

    boolean checkLost() {
        return hasLost;
    }

    public int getColors() {
        return colors;
    }

    public int getPegs() {
        return pegs;
    }

    public int getGuesses() {
        return guesses;
    }
}
