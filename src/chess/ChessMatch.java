package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chesspieces.King;
import chesspieces.Rook;

public class ChessMatch {
    private final Board board;

    public ChessMatch() {
        this.board = new Board(8, 8);
        initialSetup();
    }

    public ChessPiece[][] getPieces(){
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for(int i=0; i< board.getRows(); i++){
            for (int j=0; j< board.getColumns(); j++){
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat;
    }

    public boolean[][] possibleMoves(ChessPosition startPos){
        Position pos = startPos.toPosition();
        validateStartPosition(pos);
        return  board.piece(pos).possibleMoves();
    }

    public ChessPiece performChessMove(ChessPosition startPosition, ChessPosition targetPosition){
        Position start = startPosition.toPosition();
        Position target = targetPosition.toPosition();
        validateStartPosition(start);
        validateTargetPosition(start, target);
        Piece capturedPiece = makeMove(start, target);
        return (ChessPiece) capturedPiece;
    }

    private Piece makeMove(Position start, Position target){
        Piece p = board.removePiece(start);
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(p, target);
        return capturedPiece;
    }

    private void validateStartPosition(Position pos){
        if(!board.thereIsAPiece(pos)){
            throw new ChessException("There is no piece on start position");
        }
        if(!board.piece(pos).isThereAnyPossibleMove()){
            throw new ChessException("There is no possible moves for the chosen piece.");
        }
    }

    private void validateTargetPosition(Position start, Position target){
        if(!board.piece(start).possibleMove(target)){
            throw new ChessException("The chosen Piece can't move to target position.");
        }
    }

    private void placeNewPiece(char column, int row, ChessPiece piece){
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
    }

    public void initialSetup(){
        placeNewPiece('c', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new King(board, Color.WHITE));

        placeNewPiece('c', 7, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 8, new King(board, Color.BLACK));
    }

}
