package MasterMind.AI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RuleTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    private boolean compareArrays(int[] result,int[] expected){
        for (int i = 0; i < result.length; i++) {
            if(result[i] != expected[i])
                return false;
        }
        return true;
    }
    @Test
    public void mustNotIncludeATC1(){
        int[] ruleGuess = {1,2,3,4};
        int[] ruleResult = {0,0,0,0};
        int[] newGuess = {5,5,5,5};
        int[] newResult = {4,4,4,4};
        int[] expectedResult = {0,0,0,0};

        Rule r = new Rule(ruleGuess,ruleResult);
        r.mustNotInclude(newGuess, newResult);

        assertTrue(compareArrays(newResult,expectedResult));
    }

    @Test
    public void mustNotIncludeATC2(){
        int[] ruleGuess = {1,2,3,4};
        int[] ruleResult = {2,2,0,0};
        int[] newGuess = {4,4,4,4};
        int[] newResult = {4,4,4,4};
        int[] expectedResult = {0,0,0,4};

        Rule r = new Rule(ruleGuess,ruleResult);
        r.mustNotInclude(newGuess,newResult);

        assertTrue(compareArrays(newResult,expectedResult));
    }

    @Test
    public void mustNotIncludeATC3(){
        int[] ruleGuess = {3,2,3,4};
        int[] ruleResult = {2,2,0,0};
        int[] newGuess = {5,3,3,4};
        int[] newResult = {4,4,4,4};
        int[] expectedResult = {0,4,4,4};

        Rule r = new Rule(ruleGuess,ruleResult);
        r.mustNotInclude(newGuess,newResult);

        assertTrue(compareArrays(newResult,expectedResult));
    }

    @Test
    public void mustNotIncludeAandBTC1(){
        int[] ruleGuess = {3,2,3,4};
        int[] ruleResult = {2,2,0,0};
        int[] newGuess = {5,3,3,4};
        int[] newResult = {4,4,4,4};
        int[] expectedResult = {0,4,0,0};

        Rule r = new Rule(ruleGuess,ruleResult);

        assertFalse(r.mustNotInclude(newGuess,newResult)&& compareArrays(newResult,expectedResult));
    }

    @Test
    public void mustNotIncludeAandBTC2(){
        int[] ruleGuess = {1,2,3,4};
        int[] ruleResult = {2,2,0,0};
        int[] newGuess = {5,3,3,4};
        int[] newResult = {4,4,4,4};
        int[] expectedResult = {4,4,0,0};

        Rule r = new Rule(ruleGuess,ruleResult);

        assertTrue(r.mustNotInclude(newGuess,newResult)&& compareArrays(newResult,expectedResult));
    }

    @Test
    public void mustNotIncludeAandBTC3(){
        int[] ruleGuess = {1,2,3,3};
        int[] ruleResult = {2,2,0,0};
        int[] newGuess = {5,3,3,4};
        int[] newResult = {4,4,4,4};
        int[] expectedResult = {4,4,0,0};

        Rule r = new Rule(ruleGuess,ruleResult);

        assertTrue(r.mustNotInclude(newGuess,newResult)&& compareArrays(newResult,expectedResult));
    }

    @Test
    public void mustIncludeTC1(){
        int[] guess = {1,2,3,4};
        int[] result = {2,2,1,0};
        int[] otherGuess = {5,5,5,5};
        int[] otherResult = {4,4,4,4};
        Rule r = new Rule(guess,result);
        assertFalse(r.mustInclude(otherGuess,otherResult));
    }

    @Test
    public void mustIncludeTC2(){
        int[] guess = {1,2,4,4};
        int[] result = {2,2,1,1};
        int[] otherGuess = {4,4,4,4};
        int[] otherResult = {4,4,4,4};
        Rule r = new Rule(guess,result);
        assertTrue(r.mustInclude(otherGuess,otherResult));
    }

    @Test
    public void mustIncludeTC3(){
        int[] guess = {1,2,4,4};
        int[] result = {2,2,0,0};
        int[] otherGuess = {4,4,4,4};
        int[] otherResult = {4,4,4,4};
        Rule r = new Rule(guess,result);
        assertFalse(r.mustInclude(otherGuess,otherResult));
    }


   @Test
    public void mayIncludeTC1(){
        int[] guess = {1,2,4,4};
        int[] result = {2,2,1,0};
        int[] otherGuess = {4,4,4,4};
        int[] otherResult = {4,4,4,4};
        Rule r = new Rule(guess,result);
        assertTrue(r.mustShift(otherGuess,otherResult));
    }

    @Test
    public void mayIncludeTC2(){
        int[] guess = {1,2,3,4};
        int[] result = {2,2,1,1};
        int[] otherGuess = {4,4,4,4};
        int[] otherResult = {4,4,4,4};
        Rule r = new Rule(guess,result);
        assertFalse(r.mustShift(otherGuess,otherResult));
    }

    @Test
    public void mayIncludeTC3(){
        int[] guess = {1,2,4,4};
        int[] result = {1,1,0,0};
        int[] otherGuess = {4,4,4,4};
        int[] otherResult = {4,4,4,4};
        Rule r = new Rule(guess,result);
        assertFalse(r.mustShift(otherGuess,otherResult));
    }

    @Test
    public void followRulesTC1(){
        int[] guess = {1,2,4,4};
        int[] result = {1,1,2,0};
        int[] otherGuess = {2,3,4,4};
        Rule r = new Rule(guess,result);
        assertTrue(r.followsRules(otherGuess));
    }

    @Test
    public void followRulesTC2(){
        int[] guess = {1,2,4,4};
        int[] result = {1,1,2,0};
        int[] otherGuess = {2,3,3,4};
        Rule r = new Rule(guess,result);
        assertFalse(r.followsRules(otherGuess));
    }
}