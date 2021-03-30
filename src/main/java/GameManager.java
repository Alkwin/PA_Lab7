import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class GameManager {
    public Integer[][] board;
    public int n = 5;
    public Player p1;
    public Player p2;

    // Decides whose turn it is
    public AtomicInteger turn = new AtomicInteger(1);

    public void initializeGame() {
        initializePlayers();
        initializeBoard();
        startGame();
    }

    private void startGame() {
        Runnable player1Side = () -> {
            playGame(p1);
        };

        Runnable player2Side = () -> {
            playGame(p2);
        };

        new Thread(player1Side).start();
        new Thread(player2Side).start();
    }

    private void playGame(Player p) {
        if(!isGameOver()) {
            while(turn.get()!=p.number) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            makeMove(p);
        }
    }

    private void makeMove(Player p) {
        System.out.println("For Player" + p.number + ", choose the next element in the matrix: ");
        displayBoard();
        int userInputX = -1;
        int userInputY = -1;
        try {
            while (userInputX == -1) {
                userInputX = readValue();
            }
            while (userInputY == -1) {
                userInputY = readValue();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Player" + p.number + "'s selected position: " + userInputX + " " + userInputY);
        p.sequence.add(board[userInputX][userInputY]);
        board[userInputX][userInputY] = 0;
        if(turn.get() == 1) {
            turn.set(2);
        } else {
            turn.set(1);
        }
        playGame(p);
    }

    private int readValue() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int value = -1;
        try {
            value = Integer.parseInt(br.readLine());
        } catch(NumberFormatException nfe) {
            System.err.println("Invalid Format!");
        }
        return value;
    }

    private boolean isGameOver() {
        for(int i = 0; i < n; i ++) {
            for(int j = 0; j < n; j ++) {
                if(board[i][j]!=0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void displayBoard() {
        for(int i = 0; i < n; i ++) {
            for(int j = 0; j < n; j ++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void initializeBoard() {
        board = new Integer[n][n];
        for(int i = 0; i < n; i ++) {
            for(int j = 0; j < n; j ++) {
                if(i!=j) {
                    board[i][j] = ThreadLocalRandom.current().nextInt(0, n + 1);
                } else {
                    board[i][j] = 0;
                }
            }
        }
    }

    public void initializePlayers() {
        p1 = new Player("p1", new ArrayList<Integer>(), 1);
        p2 = new Player("p2", new ArrayList<Integer>(), 2);
    }
}
