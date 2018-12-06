package MasterMind.AI;


public class SinglePlay {
    private int bitGuess;
    private int score;
    private int pegs;
    SinglePlay(int guess,int pegs){
        bitGuess = guess;
        score = 0;
        this.pegs = pegs;
    }

    void setScore(int score) {
        this.score = score;
    }

    int getScore() {
        return score;
    }

    public int[] getPlayAsArray(){
        //TODO
        int[] temp = new int[pegs];
        for (int i = 0; i < pegs; i++) {
            temp[i] = ((bitGuess >>>(3*i)) & 0x7);
        }
        return temp;
    }

}
