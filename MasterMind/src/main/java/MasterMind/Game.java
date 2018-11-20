package MasterMind;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Game {
    private int currentGuesses;
    private int guesses;
    private int colors;
    private int pegs;
    private List<Integer> answer;

    public Game(){
        //make a game with default solutions
        this(10,6,4);
    }

    public Game(int guesses, int colors, int pegs){
        this.currentGuesses = 0;
        this.guesses = guesses;
        this.colors = colors;
        this.pegs = pegs;

        answer = generateAnswer();
    }

    private List<Integer> generateAnswer(){
        List<Integer> answer = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < pegs; i++) {
            answer.add(random.nextInt(colors));
        }
        return answer;
    }

    public int[] guess(List<Integer> guess){
        List<Integer> temp = new ArrayList<>(guess);
        int[] result = new int[guess.size()];
        //correct answers
        for (int i = 0; i < answer.size(); i++) {
            if(temp.get(i).equals(answer.get(i))){
                result[i] = 1;
            }
        }
        //TODO partial correct answers


        return result;
    }

    public List<Integer> getAnswer(){
        return answer;
    }
}
