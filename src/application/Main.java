package application;

import boardgame.Position;
import chess.ChessMatch;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        ChessMatch match = new ChessMatch();
        UI.printBoard(match.getPieces());
        }
}
