import java.util.ArrayList;
import java.util.Random;

public class StudentPlayer extends Player{
    public StudentPlayer(int playerIndex, int[] boardSize, int nToConnect) {
        super(playerIndex, boardSize, nToConnect);
    }
    ArrayList<Integer> validSteps(Board board){
        ArrayList<Integer> ret = new ArrayList<>();
        for (int col = 0; col < boardSize[1]; col++)
            if (board.stepIsValid(col))
                ret.add(col);
        return ret;
    }
    int measureChoice(int[][] board){
        int value = calculateSectionValue(new int[]{board[0][3], board[1][3], board[2][3], board[3][3], board[4][3], board[5][3]})*3;
        for(int row = boardSize[0]-1; row >=0; row--)
            for(int col = 0; col < boardSize[1]-3; col++)
                value += calculateSectionValue(new int[]{board[row][col], board[row][col+1], board[row][col+2], board[row][col+3]});
        for(int col = 0; col < boardSize[1]; col++)
            for (int row = boardSize[0] - 1; row >= 3; row--)
                value += calculateSectionValue(new int[]{board[row][col], board[row - 1][col], board[row - 2][col], board[row - 3][col]});
        for(int row = boardSize[0]-1; row >= 3; row--)
            for(int col = 0; col < boardSize[1]-3; col++)
               value += calculateSectionValue(new int[]{board[row][col], board[row-1][col+1], board[row-2][col+2], board[row-3][col+3]});
        for(int col = 0; col < boardSize[1]-3; col++)
            for(int row = boardSize[0]-1; row >= 3; row--)
                    value += calculateSectionValue(new int[]{board[row-3][col], board[row-2][col+1], board[row-1][col+2], board[row][col+3]});
        return value;
    }
    int freeRow(int[][] board, int column){
        int i = boardSize[0]-1;
        while(board[i][column] != 0) i--;
        return i;
    }
    int calculateSectionValue(int[] section){
        int value = 0;
        if(countMemberInArray(section, playerIndex) == 4) value += 1000;
        else if(countMemberInArray(section, playerIndex) == 3 && countMemberInArray(section, 0) == 1) value += 50;
        else if(countMemberInArray(section, playerIndex) == 2 && countMemberInArray(section, 0) == 2) value += 20;
        if(countMemberInArray(section, 1) == 3 && countMemberInArray(section, 0) == 1) value -= 60;
        return value;
    }

    boolean isCalculatedNodeTerminated(Board board){
        return winning_move(board.getState(), 1) || winning_move(board.getState(), 2) || validSteps(board).size() == 0;
    }

    boolean winning_move(int[][] board, int player){
        for(int row = boardSize[0]-1; row >=0; row--)
            for(int col = 0; col < boardSize[1]-3; col++)
                if(board[row][col] == player && board[row][col+1] == player && board[row][col+2] == player && board[row][col+3] == player ) return true;
        for(int col = 0; col < boardSize[1]; col++)
            for (int row = boardSize[0] - 1; row >= 3; row--)
                if(board[row][col] == player && board[row - 1][col] == player && board[row - 2][col]  == player && board[row - 3][col] == player ) return true;
        for(int row = boardSize[0]-1; row >= 3; row--)
            for(int col = 0; col < boardSize[1]-3; col++)
                if(board[row][col] == player && board[row-1][col+1] == player && board[row-2][col+2]  == player && board[row-3][col+3]  == player ) return true;
        for(int col = 0; col < boardSize[1]-3; col++)
            for(int row = boardSize[0]-1; row >= 3; row--)
                if(board[row-3][col] == player && board[row-2][col+1] == player && board[row-1][col+2]  == player && board[row][col+3]  == player ) return true;
        return false;
    }
    int[] minimax(Board board, int depth, int alpha, int beta,  boolean maximize){
        ArrayList<Integer> valLoc = validSteps(board);
        boolean term = isCalculatedNodeTerminated(board);
        if (depth == 0 || term){
            if(term) {
                if (winning_move(board.getState(), 2)) return (new int[]{-1, 999999});
                else if (winning_move(board.getState(), 1)) return (new int[]{-1, -999999});
                else return  (new int[]{-1, 0});
            }
            else return new int[]{-1, measureChoice(board.getState())};
        }
        if(maximize){
            int value = -9999999;
            int bestCol = new Random().nextInt(0, valLoc.size());
            for (int col: valLoc) {
                int row = freeRow(board.getState(), col);
                Board attempt = new Board(board);
                attempt.getState()[row][col] = 2;
                int newVal = minimax(attempt, depth-1, alpha, beta, false)[1];
                if(newVal > value){
                    value = newVal;
                    bestCol = col;
                }
                alpha = Math.max(value, alpha);
                if(alpha >= beta) break;
            }
            return (new int[]{bestCol, value});
        }
        else{
            int value = 9999999;
            int bestCol = new Random().nextInt(0, valLoc.size());
            for (int col: valLoc) {
                int row = freeRow(board.getState(), col);
                Board attempt = new Board(board);
                attempt.getState()[row][col] = 1;
                int newVal = minimax(attempt, depth-1, alpha, beta,true)[1];
                if(newVal < value){
                    value = newVal;
                    bestCol = col;
                }
                beta = Math.min(value, beta);
                if(alpha >= beta) break;
            }
            return (new int[]{bestCol, value});
        }
    }
    int countMemberInArray(int[] arr, int counted){
        int f = 0;
        for (int j : arr) if (j == counted) f++;
        return f;
    }
    @Override
    public int step(Board board) {
        return minimax(board, 5, -9999999, 9999999, true)[0];
    }
}