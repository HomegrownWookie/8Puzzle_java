import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class Puzzle {

    private static Scanner scan = new Scanner(System.in);
    private static LinkedList<Node> explored;
    private static int[] goalState = {0, 1, 2, 3, 4, 5, 6, 7, 8};

    public static void main(String[] args) {
        System.out.println("Welcome to the 8-puzzle solver.");
        System.out.println("Type \'1\' to auto generate 500 puzzles \'2\' to enter your own puzzle.");
        int choice = scan.nextInt();
        int[] board = new int[9];
        switch (choice) {
            case 1:
                System.out.print("Goal State");
                printBoard(goalState);
                System.out.println();
                System.out.println();
                System.out.println("Enter your choice of algorithm:\n1. A* with the Misplaced Tile heuristic.\n" +
                        "2. A* with the Manhattan distance heuristic.");
                choice = scan.nextInt();
                switch (choice){
                    case 1:
                        int i =0;
                        while(i < 500) {
                            int[] randBoard = new int[9];
                            System.arraycopy(goalState, 0, randBoard, 0, goalState.length);
                            randBoard = shuffleArray(randBoard);
                            if(possible(randBoard)) {
                                i++;
                                explored = new LinkedList<>();
                                generalSearch(randBoard, 1);
                            }
                        }
                        break;
                    case 2:
                        int j = 0;
                        while(j < 500) {
                            int[] randBoard = new int[9];
                            System.arraycopy(goalState, 0, randBoard, 0, goalState.length);
                            randBoard = shuffleArray(randBoard);
                            if(possible(randBoard)) {
                                j++;
                                explored = new LinkedList<>();
                                generalSearch(randBoard, 2);
                            }
                        }
                        break;
                    default:
                        System.out.println("Invalid Choice!");
                        break;
                }
                break;

            case 2:
                System.out.println("Enter values for puzzle: (0-8 separated by a space)");
                scan.nextLine();
                String temp = scan.nextLine();
                char[] tempC = temp.toCharArray();
                for (int i = 0; i <= tempC.length / 2; i++) {
                    int x = 2 * i;
                    board[i] = tempC[x] - 48;
                }
                System.out.print("Goal State");
                printBoard(goalState);
                System.out.println();
                System.out.println();
                System.out.println("Enter your choice of algorithm:\n1. A* with the Misplaced Tile heuristic.\n" +
                        "2. A* with the Manhattan distance heuristic.");
                choice = scan.nextInt();
                switch (choice){
                    case 1:
                        if(possible(board)) {
                            explored = new LinkedList<>();
                            generalSearch(board, 1);
                        }
                        break;
                    case 2:
                        if(possible(board)) {
                            explored = new LinkedList<>();
                            generalSearch(board, 1);
                        }
                        break;
                    default:
                        System.out.println("Invalid Choice!");
                        break;
                }
                break;
        }
    }

    private static int[] shuffleArray(int[] arr)
    {
        Random rnd = ThreadLocalRandom.current();
        for (int i = arr.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            int a = arr[index];
            arr[index] = arr[i];
            arr[i] = a;
        }
        return arr;
    }

    private static boolean possible(int[] arr){
        int inversions = 0;
        for(int i = 0; i < arr.length; i ++){
            int temp = arr[i];
            for(int j = i; j < arr.length; j++){
                if(temp > arr[j] && arr[j] != 0)
                    inversions++;
            }
        }
        return inversions % 2 == 0;
    }

    private static void generalSearch(int[] initialBoard, int i) {
        long startTime = System.currentTimeMillis();
        PriorityQueue mainQueue = new PriorityQueue();
        mainQueue.add(initialBoard);
        while(!mainQueue.empty()){
            Node node = mainQueue.getItem();
            printBoard(node.getBoard());
            if(goalTest(node)){
                long endTime = System.currentTimeMillis();
                System.out.println("Goal State Reached!");
                System.out.println("Expanded Nodes: " + getLevel());
                System.out.println("Depth Of Goal Node: " + node.getDistanceTraveled());
                System.out.println("Elapsed Time: " + (endTime-startTime) + "ms");
                return;
            }
            if(i == 1){
                misplacedTileHeuristic(mainQueue, expand(node));
            } else if(i == 2) {
                manhattanDistanceHeuristic(mainQueue, expand(node));
            }
        }
    }

    private static PriorityQueue expand(Node node){
        PriorityQueue localQueue = new PriorityQueue();
        int[] board1 = new int[9];
        System.arraycopy(node.getBoard(), 0, board1, 0, node.getBoard().length);
        int[] board2 = new int[9];
        System.arraycopy(node.getBoard(), 0, board2, 0, node.getBoard().length);
        int[] board3 = new int[9];
        System.arraycopy(node.getBoard(), 0, board3, 0, node.getBoard().length);
        int[] board4 = new int[9];
        System.arraycopy(node.getBoard(), 0, board4, 0, node.getBoard().length);
        if(canMoveUp(board1)){
            Node node1 = new Node(moveUp(board1), 0, node.getDistanceTraveled()+1, 0);
            if(!isExplored(node1))
                localQueue.add(node1);
        }
        if(canMoveDown(board2)){
            Node node2 = new Node(moveDown(board2), 0, node.getDistanceTraveled()+1, 0);
            if(!isExplored(node2))
                localQueue.add(node2);
        }
        if(canMoveLeft(board3)){
            Node node3 = new Node(moveLeft(board3), 0, node.getDistanceTraveled()+1, 0);
            if(!isExplored(node3))
                localQueue.add(node3);
        }
        if(canMoveRight(board4)){
            Node node4 = new Node(moveRight(board4), 0, node.getDistanceTraveled()+1, 0);
            if(!isExplored(node4))
                localQueue.add(node4);
        }
        return localQueue;
    }

    private static int countMisplaced(int[] board){
        int count = 0;
        for(int i = 1; i < board.length; i++){
            if(board[i] != i){
                count++;
            }
        }
        return count;
    }

    private static int countManhattan(int[] board) {
        int count = 0;
        for(int i = 0; i < board.length; i++){
            if(board[i] != i){
                int row_diff = Math.abs((i / 3) - (board[i] / 3));
                int col_diff = Math.abs((i % 3) - (board[i] % 3));
                count += (row_diff + col_diff);
            }
        }
        return count;
    }

    private static void misplacedTileHeuristic(PriorityQueue mainQueue, PriorityQueue newNodes){
        while(!newNodes.empty()){
            Node node = newNodes.getItem();
            mainQueue.add(node.getBoard(), countMisplaced(node.getBoard()), node.getDistanceTraveled(),
                    countMisplaced(node.getBoard()) + node.getDistanceTraveled());
        }
    }

    private static void manhattanDistanceHeuristic(PriorityQueue mainQueue, PriorityQueue newNodes){
        while(!newNodes.empty()){
            Node node = newNodes.getItem();
            mainQueue.add(node.getBoard(), countManhattan(node.getBoard()), node.getDistanceTraveled(),
                    countManhattan(node.getBoard()) + node.getDistanceTraveled());
        }
    }

    private static void printBoard(int[] mat){
        System.out.println("Board:");
        System.out.println(mat[0] + " " + mat[1] + " " + mat[2]);
        System.out.println(mat[3] + " " + mat[4] + " " + mat[5]);
        System.out.println(mat[6] + " " + mat[7] + " " + mat[8]);
    }

    private static boolean canMoveUp(int[] mat){
        return mat[0] != 0 && mat[1] != 0 && mat[2] != 0;
    }

    private static boolean canMoveDown(int[] mat){
        return mat[6] != 0 && mat[7] != 0 && mat[8] != 0;
    }

    private static boolean canMoveLeft(int[] mat){
        return mat[0] != 0 && mat[3] != 0 && mat[6] != 0;
    }

    private static boolean canMoveRight(int[] mat){
        return mat[2] != 0 && mat[5] != 0 && mat[8] != 0;
    }

    private static int[] moveUp(int[] mat){
        if(canMoveUp(mat)){
            for(int i = 0; i < mat.length; i++){
                if(mat[i] == 0){
                    int temp = mat[i-3];
                    mat[i-3] = 0;
                    mat[i] = temp;
                    break;
                }
            }
            return mat;
        } else {
            return null;
        }
    }

    private static int[] moveDown(int[] mat){
        if(canMoveDown(mat)){
            for(int i = 0; i < mat.length; i++){
                if(mat[i] == 0){
                    int temp = mat[i+3];
                    mat[i+3] = 0;
                    mat[i] = temp;
                    break;
                }
            }
            return mat;
        } else {
            return null;
        }
    }

    private static int[] moveLeft(int[] mat){
        if(canMoveLeft(mat)){
            for(int i = 0; i < mat.length; i++){
                if(mat[i] == 0){
                    int temp = mat[i-1];
                    mat[i-1] = 0;
                    mat[i] = temp;
                    break;
                }
            }
            return mat;
        } else {
            return null;
        }
    }

    private static int[] moveRight(int[] mat){
        if(canMoveRight(mat)){
            for(int i = 0; i < mat.length; i++){
                if(mat[i] == 0){
                    int temp = mat[i+1];
                    mat[i+1] = 0;
                    mat[i] = temp;
                    break;
                }
            }
            return mat;
        } else {
            return null;
        }
    }

    private static boolean isExplored(Node node){
        boolean test;
        int[] currentBoard = node.getBoard();

        for(int i = 0; i < explored.size(); i++){
            test = true;
            int[] tempBoard = explored.get(i).getBoard();
            for(int j = 0; j < currentBoard.length; j++){
                if(currentBoard[j] != tempBoard[j])
                    test = false;
            }
            if(test)
                return true;
        }
        return false;
    }

    private static int getLevel(){
        return explored.size();
    }

    private static void markExplored(Node node){
        explored.add(node);
    }

    private static boolean goalTest(Node node){
        markExplored(node);
        boolean test = true;
        int[] tempBoard = node.getBoard();
        for(int i = 0; i < tempBoard.length; i++){
            if(tempBoard[i] != i)
                test = false;
        }
        return test;
    }

}


class PriorityQueue {
    private LinkedList<Node> elements;

    /**
     * Constructor
     */
    PriorityQueue() {
        this.elements = new LinkedList<>();
    }

    void add(int[] board) {
        elements.add(new Node(board, 0, 0, 0));
        elements.sort(Comparator.comparing(Node::getPriority));
    }

    void add(int[] board, int heuristicDistance, int distanceTraveled, int priority) {
        elements.add(new Node(board, heuristicDistance, distanceTraveled, priority));
        elements.sort(Comparator.comparing(Node::getPriority));
    }

    void add(Node node){
        elements.add(node);
        elements.sort(Comparator.comparing(Node::getPriority));
    }

    boolean empty() {
        return elements.size() == 0;
    }

    Node getItem(){
        return elements.pop();
    }
}

class Node {
    private int[] board;
    private int heuristicDistance;
    private int distanceTraveled;
    private int priority;


    Node(int[] board, int heuristicDistance, int distanceTraveled, int priority){
        this.board = board;
        this.heuristicDistance = heuristicDistance;
        this.distanceTraveled = distanceTraveled;
        this.priority = priority;
    }

    int getPriority() {
        return priority;
    }

    int[] getBoard() {
        return board;
    }

    int getDistanceTraveled() {
        return distanceTraveled;
    }

}
