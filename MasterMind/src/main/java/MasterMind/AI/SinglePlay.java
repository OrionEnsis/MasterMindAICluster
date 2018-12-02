package MasterMind.AI;


public class SinglePlay {
    int bitGuess;
    int score;
    int pegs;
    public SinglePlay(int guess,int pegs){
        bitGuess = guess;
        score = 0;
        this.pegs = pegs;
    }

    //TODO write tests for this.  These should be correct.
    public int getPegColor(int position){
        return ((bitGuess >>>(4*position)) & 0x8);
    }

    //TODO reimplement
    public int determineScore(){
        score = 1;

        return score;
    }

    public int getScore() {
        return score;
    }

    public int[] getPlayAsArray(){
        //TODO
        int[] temp = new int[pegs];
        for (int i = 0; i < pegs; i++) {
            temp[i] = ((bitGuess >>>(4*i)) & 0x7);
        }
        return temp;
    }

}
