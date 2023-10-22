package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chesspieces.King;
import chesspieces.Rook;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {
    private final Board board;
    private int turn;
    private Color currentPlayer;
    private boolean check;

    private List<ChessPiece> currentPieces = new ArrayList<>();
    private List<ChessPiece> capturedPieces = new ArrayList<>();

    public ChessMatch() {
        this.board = new Board(8, 8);
        turn = 1;
        currentPlayer = Color.WHITE;
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
        if(testCheck(currentPlayer)){
            undoMove(start, target, capturedPiece);
            throw new ChessException("You can't put yourself in check");
        }

        check = testCheck(getOpponent(currentPlayer));
        nextTurn();
        return (ChessPiece) capturedPiece;
    }

    private Piece makeMove(Position start, Position target){
        Piece p = board.removePiece(start);
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(p, target);
        if(capturedPiece != null){
            ChessPiece captured = (ChessPiece) capturedPiece;
            currentPieces.remove(captured);
            capturedPieces.add(captured);
        }
        return capturedPiece;
    }

    private void undoMove(Position start, Position target, Piece capturedPiece){
        Piece p = board.removePiece(target);
        board.placePiece(p, start);

        if(capturedPiece != null){
            board.placePiece(capturedPiece, target);
            currentPieces.add((ChessPiece) capturedPiece);
            capturedPieces.remove(capturedPiece);

        }
    }

    private void validateStartPosition(Position pos){
        if(!board.thereIsAPiece(pos)){
            throw new ChessException("There is no piece on start position");
        }
        if(((ChessPiece) board.piece(pos)).getColor() != currentPlayer){
            throw new ChessException("the chosen piece is not yours.");
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
        currentPieces.add(piece);
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

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public int getTurn() {
        return turn;
    }

    public List<ChessPiece> getCapturedPieces() {
        return capturedPieces;
    }

    private void nextTurn(){
        turn++;
        currentPlayer = (currentPlayer==Color.WHITE?Color.BLACK:Color.WHITE);
    }

    private Color getOpponent(Color color){
        return (color == Color.WHITE)?Color.BLACK:Color.WHITE;
    }

    private ChessPiece getKing(Color color){
        List<Piece> list = currentPieces.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for(Piece p:list){
            if(p instanceof King){
                return (ChessPiece) p;
            }
        }
        throw new IllegalStateException("There is no "+ color + " king on the board");
    }

    private boolean testCheck(Color color){
        Position kingPosition = getKing(color).getChessPosition().toPosition();
        List<Piece> opponentPieces = currentPieces.stream().filter(x-> x.getColor() == getOpponent(color)).collect(Collectors.toList());
        for(Piece p:opponentPieces){
            boolean[][] mat = p.possibleMoves();
            if(mat[kingPosition.getRow()][kingPosition.getColumn()]){
                return true;
            }
        }
        return false;
    }

    public boolean isCheck() {
        return check;
    }
}
