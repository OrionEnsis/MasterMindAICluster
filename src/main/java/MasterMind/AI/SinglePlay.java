package MasterMind.AI;

import java.io.Serializable;

/**
 * this class represents a single play as an integer.  It turns each peg into 3 bits and shifts it into the correct position.
 * it also contains the score which is determined by an outside metric.
 *
 * @author Jim Spagnola
 */
public class SinglePlay implements Serializable, Comparable<SinglePlay> {
    private int bitGuess;
    private int score;
    private int pegs;

    /**
     * the default constructor.
     *
     * @param guess the incoming guess
     * @param pegs the size of the guess
     */
    SinglePlay(int guess,int pegs){
        bitGuess = guess;
        score = 0;
        this.pegs = pegs;
    }

    /**
     * sets the score that is evaluated outside.
     * @param score the score of the play
     */
    void setScore(int score) {
        this.score = score;
    }

    /**
     * retrieves the score
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * this method turns the compressed play into an int array for manipulation in the game.
     * @return the play decompressed back as an int array
     */
    public int[] getPlayAsArray(){
        int[] temp = new int[pegs];
        for (int i = 0; i < pegs; i++) {
            temp[i] = ((bitGuess >>>(3*i)) & 0x7);
        }
        return temp;
    }

    @Override
    public int compareTo(SinglePlay o) {
        return Integer.compare(bitGuess,o.bitGuess);
    }

    /**
     * gets the number of pegs
     * @return the number of pegs
     */
    int getPegs() {
        return pegs;
    }
}
