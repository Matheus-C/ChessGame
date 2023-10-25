package application;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPosition;

import java.util.InputMismatchException;
import java.util.Scanner;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ChessMatch match = new ChessMatch();
        while(match.isNotCheckmate()){
            try {
                UI.clearScreen();
                UI.printChessMatch(match, match.getCapturedPieces());
                System.out.println();
                System.out.print("Start: ");
                ChessPosition start = UI.readChessPosition(sc);

                boolean[][] possibleMoves = match.possibleMoves(start);
                UI.clearScreen();
                UI.printBoard(match.getPieces(), possibleMoves);
                System.out.println();
                System.out.print("Target: ");
                ChessPosition target = UI.readChessPosition(sc);

                match.performChessMove(start, target);

                if(match.getPromoted() != null){
                    System.out.println("Enter piece for promotion B/Q/R/N: ");
                    String type = sc.nextLine().toUpperCase();
                    match.replacePromotedPiece(type);
                    while(!type.equals("B") && !type.equals("R") && !type.equals("N") && !type.equals("Q")){
                        System.out.println("invalid type! Enter piece for promotion P/Q/R/N: ");
                        type = sc.nextLine().toUpperCase();
                    }
                    match.replacePromotedPiece(type);
                }
            }catch(ChessException | InputMismatchException e){
                System.out.println(e.getMessage());
                sc.nextLine();
            }
        }
        UI.clearScreen();
        UI.printChessMatch(match, match.getCapturedPieces());
        System.out.println();

        }
}
