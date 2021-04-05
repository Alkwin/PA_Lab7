import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class GameManager {
    public Integer[][] board;
    public int n = 5;
    public Player p1;
    public Player p2;

    public Thread p1Thread;
    public Thread p2Thread;

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

        p1Thread = new Thread(player1Side);
        p2Thread = new Thread(player2Side);
        p1Thread.start();
        p2Thread.start();

        Runnable timeKeeper = () -> {
            try {
                Thread.sleep(1000 * 5); // 1s * x
            } catch (InterruptedException e) {
                //
            }
            System.out.println("Game entered timeout");
            p1Thread.interrupt();
            p2Thread.interrupt();
            decideWinner();
        };

        new Thread(timeKeeper).start();

    }

    private void decideWinner() {
        if(p1.sequenceSum() > p2.sequenceSum()) {
            System.out.println("Winner is Player1");
        } else if(p1.sequenceSum() < p2.sequenceSum()) {
            System.out.println("Winner is Player2");
        } else {
            System.out.println("Draw");
        }
    }

    private synchronized void playGame(Player p) {
        while(!isGameOver()) {
            while(turn.get()!=p.number) {
                try {
                    System.out.println(p.name + " entered wait");
                    wait();
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
            userInputX = readValue();
            userInputY = readValue();
        } catch (IOException e) {
            //
        }

        System.out.println("Player" + p.number + "'s selected position: " + userInputX + " " + userInputY);
        p.sequence.add(board[userInputX][userInputY]);
        board[userInputX][userInputY] = 0;

        if(turn.get() == 1) {
            turn.set(2);
        } else {
            turn.set(1);
        }
        notifyAll();
    }

    private int readValue() throws IOException {
        Scanner inputScanner = new Scanner(System.in);
        String userInput = inputScanner.next();

        int value = -1;
        try {
            while(value<0) {
                value = Integer.parseInt(userInput);
            }
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