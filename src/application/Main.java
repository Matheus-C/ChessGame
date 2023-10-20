package application;

import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Scanner;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ChessMatch match = new ChessMatch();
        while(true){
            UI.printBoard(match.getPieces());
            System.out.println();
            System.out.print("Start: ");
            ChessPosition start = UI.readChessPosition(sc);
            System.out.println();
            System.out.print("Target: ");
            ChessPosition target = UI.readChessPosition(sc);

            ChessPiece capturedPiece = match.performChessMove(start, target);

        }

        }
}
