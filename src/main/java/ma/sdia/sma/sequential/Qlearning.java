package ma.sdia.sma.sequential;

import java.util.Random;

public class Qlearning {
    private final double GAMMA=0.9;
    private final double EPS=0.4;
    private final int MAX_EPOCH=100000;
    private final int GRID_SIZE=8;
    private final int ACTION_SIZE=4;
    private  int[][] grid;
    private  double[][] qTable=new double[GRID_SIZE*GRID_SIZE][ACTION_SIZE];
    private  int[][] actions;
    private int stateI;
    private int stateJ;
    private long executionTime;
    private double alpha=0;
    Random random=new Random();
    public Qlearning(){
        alpha= random.nextDouble();
        System.out.println(alpha);
        actions=new int[][]{
                {0,-1},  //gauche
                {0,1},   //droit
                {1,0},   //bas
                {-1,0}   //haut
        };
        grid=new int[][]{
                {0,0,0,0,0,0,0,0},
                {0,0,-1,-1,0,0,0,0},
                {-1,-1,0,0,0,0,0,0},
                {0,0,0,0,0,-1,0,0},
                {0,0,0,0,0,0,-1,0},
                {0,-1,-1,-1,0,0,0,0},
                {0,0,0,0,0,0,0,-1},
                {-1,0,0,0,0,-1,0,1}
        };
    }

    private void resetState(){
        stateI=1;
        stateJ=1;
    }

    private int chooseAction(double eps){
        Random random=new Random();
        double bestQ=0;
        int action=0;
        if(random.nextDouble()<eps){
            //exploration
            action= random.nextInt(ACTION_SIZE);

        }else {
            //exploitation
            int st=stateI*GRID_SIZE+stateJ; //convert metrics to array with one dim
            for(int i=0;i<ACTION_SIZE;i++){
                if(qTable[st][i]>bestQ){
                    bestQ=qTable[st][i];
                    action=i;
                }
            }
        }
        return action;
    }

    private int executeAction(int action){
        //update state
        stateI=Math.max(0,Math.min(actions[action][0]+stateI,GRID_SIZE-1));
        stateJ=Math.max(0,Math.min(actions[action][1]+stateJ,GRID_SIZE-1));

        return stateI*GRID_SIZE+stateJ;
    }
    private boolean isFinished(){
        return grid[stateI][stateJ]==1;
    }
    private void showResult(){
        System.out.println("------------------Q table------------------");
        for(double[] line:qTable){
            System.out.print("[");
            for(double qv:line){
                System.out.print(qv+" , ");
            }
            System.out.println("]");
        }
        resetState();
        while (!isFinished()){
            int act=chooseAction(0);
            System.out.println("State : "+(stateI*GRID_SIZE+stateJ)+" action : "+act);
            executeAction(act);
        }
        System.out.println("Final State : "+(stateI*GRID_SIZE+stateJ));
    }
    public void runQlearning(){
        long startTime = System.currentTimeMillis();
        int it=0;
        int currentState;
        int nextState;
        int act1,act2;
        while (it<MAX_EPOCH){
            resetState();
            while (!isFinished()){
                currentState=stateI*GRID_SIZE+stateJ;
                act1=chooseAction(EPS);
                nextState = executeAction(act1);
                act2=chooseAction(0);
                //value of q table of current state and the executed action
                qTable[currentState][act1]=qTable[currentState][act1]+alpha*(grid[stateI][stateJ]+GAMMA*qTable[nextState][act2]-qTable[currentState][act1]);
            }

            it++;
        }
        long endTime = System.currentTimeMillis();
        executionTime = endTime - startTime;

        //showResult();
    }

    public long getExecutionTime() {
        return executionTime;
    }
}
