package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chesspieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {
    private final Board board;
    private int turn;
    private Color currentPlayer;
    private boolean check;
    private boolean checkmate;

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
        if(testCheckmate(getOpponent(currentPlayer))){
            checkmate = true;
        }else {
            nextTurn();
        }
        return (ChessPiece) capturedPiece;
    }

    private Piece makeMove(Position start, Position target){
        ChessPiece p = (ChessPiece) board.removePiece(start);
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(p, target);
        if(capturedPiece != null){
            ChessPiece captured = (ChessPiece) capturedPiece;
            currentPieces.remove(captured);
            capturedPieces.add(captured);
        }
        //Kingside Castling
        if(p instanceof King && target.getColumn() == start.getColumn()+2){
            Position startR = new Position(start.getRow(), start.getColumn()+3);
            Position targetR = new Position(start.getRow(), start.getColumn()+1);
            ChessPiece rook = (ChessPiece) board.removePiece(startR);
            board.placePiece(rook, targetR);
            rook.increaseMoveCount();
        }
        //Queenside castling
        if(p instanceof King && target.getColumn() == start.getColumn()-2){
            Position startR = new Position(start.getRow(), start.getColumn()-4);
            Position targetR = new Position(start.getRow(), start.getColumn()-1);
            ChessPiece rook = (ChessPiece) board.removePiece(startR);
            board.placePiece(rook, targetR);
            rook.increaseMoveCount();
        }
        p.increaseMoveCount();
        return capturedPiece;
    }

    private void undoMove(Position start, Position target, Piece capturedPiece){
        ChessPiece p = (ChessPiece) board.removePiece(target);
        board.placePiece(p, start);

        if(capturedPiece != null){
            board.placePiece(capturedPiece, target);
            currentPieces.add((ChessPiece) capturedPiece);
            capturedPieces.remove(capturedPiece);
        }
        p.decreaseMoveCount();
        //Kingside Castling
        if(p instanceof King && target.getColumn() == start.getColumn()+2){
            Position startR = new Position(start.getRow(), start.getColumn()+3);
            Position targetR = new Position(start.getRow(), start.getColumn()+1);
            ChessPiece rook = (ChessPiece) board.removePiece(targetR);
            board.placePiece(rook, startR);
            rook.decreaseMoveCount();
        }
        //Queenside castling
        if(p instanceof King && target.getColumn() == start.getColumn()-2){
            Position startR = new Position(start.getRow(), start.getColumn()-4);
            Position targetR = new Position(start.getRow(), start.getColumn()-1);
            ChessPiece rook = (ChessPiece) board.removePiece(targetR);
            board.placePiece(rook, startR);
            rook.decreaseMoveCount();
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
        //white
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE));

        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));

        //black
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
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

    private boolean testCheckmate(Color color){
        if(!testCheck(color)){
            return false;
        }
        List<Piece> list = currentPieces.stream().filter(x->((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for(Piece p: list){
            boolean[][] mat = p.possibleMoves();
            for(int i=0;i<board.getRows();i++){
                for(int j=0;j<board.getColumns();j++){
                    if(mat[i][j]){
                        Position start = ((ChessPiece)p).getChessPosition().toPosition();
                        Position target = new Position(i, j);
                        Piece captured = makeMove(start, target);
                        boolean test = testCheck(color);
                        undoMove(start, target, captured);
                        if(!test){
                            return false;
                        }

                    }
                }
            }
        }
        return true;
    }

    public boolean isCheck() {
        return check;
    }

    public boolean isCheckmate() {
        return checkmate;
    }
}
