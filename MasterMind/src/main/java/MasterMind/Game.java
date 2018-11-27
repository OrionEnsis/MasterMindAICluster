package MasterMind;

import java.util.*;

public class Game {
    private int currentGuesses;
    private int guesses;
    private int colors;
    private int pegs;
    private int[] answer;
    private boolean hasWon = false;
    private boolean hasLost = false;
    public List<int[]> pastGuesses;
    public List<int[]> pastResults;
    public Game(){
        //make a game with default solutions
        this(15,8,6);
    }

    public Game(int guesses, int colors, int pegs){
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

    public int[] guess(int[] guess){
        int[] result = new int[guess.length];
        //correct answers
        for (int i = 0; i < answer.length; i++) {
            if(guess[i] == answer[i]){
                result[i] = 1;
            }
        }
        //TODO partial correct answers
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
        //TODO turn into its own method
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

    public int[] getAnswer(){
        return answer;
    }

    public boolean checkWin() {
        return hasWon;
    }

    public boolean checkLost() {
        return hasLost;
    }

    public int getColors() {
        return colors;
    }

    public int getPegs() {
        return pegs;
    }
}
