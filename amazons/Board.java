package amazons;

import java.util.Iterator;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import static amazons.Piece.*;
import static amazons.Utils.error;

/**
 * The state of an Amazons Game.
 *
 * @author Sasha Manghise
 */
class Board {

    /**
     * The number of squares on a side of the board.
     */
    static final int SIZE = 10;

    /**
     * Initializes a game board with SIZE squares on a side in the
     * initial position.
     */
    Board() {
        init();
    }

    /**
     * Initializes a copy of MODEL.
     */
    Board(Board model) {
        copy(model);
    }

    /**
     * Copies MODEL into me.
     */
    void copy(Board model) {
        this.whiteQueens = model.whiteQueens;
        this.blackQueens = model.blackQueens;
        this._turn = model._turn;
        this._winner = model._winner;
    }

    /**
     * Finds positions of queens of color SIDE.
     */
    void getQueenPositions(amazons.Piece side) {

        if (side.equals(amazons.Piece.WHITE)) {
            whiteQueens.clear();
            queenList = whiteQueens;
        } else {
            blackQueens.clear();
            queenList = blackQueens;
        }

        allSquares = amazons.Square.iterator();
        while (allSquares.hasNext()) {
            amazons.Square nextPiece = allSquares.next();
            if (nextPiece.getPiece().equals(side)) {
                queenList.add(nextPiece);
            }
        }
    }

    /**
     * Clears the board to the initial position.
     */
    void init() {
        _turn = WHITE;
        _winner = EMPTY;

        for (amazons.Square s : amazons.Square.SQUARES) {
            this.put(amazons.Piece.EMPTY, s);
        }

        blackQueens.clear();
        whiteQueens.clear();


        blackQueens.add(put(amazons.Piece.BLACK, 0, 6));
        blackQueens.add(put(amazons.Piece.BLACK, 3, 9));
        blackQueens.add(put(amazons.Piece.BLACK, 6, 9));
        blackQueens.add(put(amazons.Piece.BLACK, 9, 6));

        whiteQueens.add(put(amazons.Piece.WHITE, 0, 3));
        whiteQueens.add(put(amazons.Piece.WHITE, 3, 0));
        whiteQueens.add(put(amazons.Piece.WHITE, 6, 0));
        whiteQueens.add(put(amazons.Piece.WHITE, 9, 3));

    }

    /**
     * Return the Piece whose move it is (WHITE or BLACK).
     */
    amazons.Piece turn() {
        return _turn;
    }

    /**
     * Return the number of moves (that have not been undone) for this
     * board.
     */
    int numMoves() {
        return allMoves.size();
    }

    /**
     * Return the winner in the current position, or null if the game is
     * not yet finished.
     */

    Piece winner() {
        int trapped = 0;
        _winner = null;

        if (turn().equals(WHITE)) {
            for (amazons.Square s : whiteQueens) {
                if (isSquareFree(s, null, 0)) {
                    break;
                } else {
                    trapped++;
                }
            }
            if (trapped == whiteQueens.size() && trapped > 0) {
                _winner = amazons.Piece.BLACK;
                return _winner;
            }
        }

        if (turn().equals(BLACK)) {
            trapped = 0;
            for (amazons.Square s : blackQueens) {
                if (isSquareFree(s, null, 0)) {
                    break;
                } else {
                    trapped++;
                }
            }
            if (trapped == blackQueens.size() && trapped > 0) {
                _winner = amazons.Piece.WHITE;
                return _winner;
            }
        }
        return null;
    }

    /**
     * Return the contents of the square at S.
     */
    final amazons.Piece get(amazons.Square s) {
        return s.getPiece();
    }

    /**
     * Return the contents of the square at (COL, ROW), where
     * 0 <= COL, ROW <= 9.
     */
    final amazons.Piece get(int col, int row) {
        return get(amazons.Square.sq(col, row));
    }

    /**
     * Return the contents of the square at COL ROW.
     */
    final amazons.Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /**
     * Set square S to P.
     */
    final void put(amazons.Piece p, amazons.Square s) {
        s.setPiece(p);
    }

    /**
     * Set square (COL, ROW) to P.
     * @return square
     */
    final amazons.Square put(amazons.Piece p, int col, int row) {
        _winner = EMPTY;
        amazons.Square.sq(col, row).setPiece(p);
        return amazons.Square.sq(col, row);
    }

    /**
     * Set square COL ROW to P.
     */
    final void put(amazons.Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /**
     * Return true iff FROM - TO is an unblocked queen move on the current
     * board, ignoring the contents of ASEMPTY, if it is encountered.
     * For this to be true, FROM-TO must be a queen move and the
     * squares along it, other than FROM and ASEMPTY, must be
     * empty. ASEMPTY may be null, in which case it has no effect.
     */
    boolean isUnblockedMove(amazons.Square from, amazons.Square to,
                            amazons.Square asEmpty) {

        int dir = from.direction(to);

        if (dir < 0 || dir > 7) {
            return false;
        }
        int dx = amazons.Square.DIR[dir][0];
        int dy = amazons.Square.DIR[dir][1];

        amazons.Square nextSquare = from;
        while (!(nextSquare.row() == to.row()
                && nextSquare.col() == to.col())) {
            nextSquare = amazons.Square.sq(nextSquare.col() + dx,
                    nextSquare.row() + dy);
            if (nextSquare.equals(asEmpty)) {
                continue;
            }
            if (!nextSquare.getPiece().equals(EMPTY)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return true iff FROM is a valid starting square for a move.
     */
    boolean isLegal(amazons.Square from) {
        return !from.getPiece().equals(EMPTY) && from.getPiece().equals(_turn);
    }

    /**
     * Return true iff FROM-TO is a valid first part of move, ignoring
     * spear throwing.
     */
    boolean isLegal(amazons.Square from, amazons.Square to) {
        if (!isLegal(from) && isUnblockedMove(from, to, null)) {
            return false;
        }

        if (!isLegal(amazons.Square.sq(from.index()))) {
            return false;
        }
        if (!from.isQueenMove(amazons.Square.sq(to.index()))) {
            return false;
        }

        int dir = from.direction(to);
        int dx = amazons.Square.DIR[dir][0];
        int dy = amazons.Square.DIR[dir][1];

        amazons.Square nextSquare = from;
        while (!(nextSquare.row() == to.row()
                && nextSquare.col() == to.col())) {
            nextSquare = amazons.Square.sq(nextSquare.col() + dx,
                    nextSquare.row() + dy);
            if (!nextSquare.getPiece().equals(EMPTY)) {
                return false;
            }
        }
        return true;

    }
    /**
     * Return true iff FROM-TO(SPEAR) is a legal move in the current
     * position.
     */
    boolean isLegal(amazons.Square from,
                    amazons.Square to, amazons.Square spear) {
        if (!isLegal(from) && isUnblockedMove(from, to, null)) {
            return false;
        }
        return isUnblockedMove(from, to, null)
                && isUnblockedMove(to, spear, from);
    }

    /**
     * Return true iff MOVE is a legal move in the current
     * position.
     */
    boolean isLegal(amazons.Move move) {
        if (move == null
                || (!isLegal(move.from())
                && isUnblockedMove(move.from(), move.to(), null))) {
            return false;
        }
        return isLegal(move.from(), move.to(), move.spear());
    }
    /** Position of all queens.
     * @param p piece */
    public void findAllQueens(amazons.Piece p) {
        whiteQueens.clear();
        blackQueens.clear();
        for (int index = 0; index < Board.SIZE * Board.SIZE; index++) {
            amazons.Square s = amazons.Square.sq(index);
            if (s.getPiece().equals(WHITE)) {
                whiteQueens.add(s);
                continue;
            }
            if (s.getPiece().equals(BLACK)) {
                blackQueens.add(s);
                continue;
            }
        }
    }

    /**
     * Check squares one step away from S in all directions.
     * @param asEmpty empty
     * @param dir direction
     * @param s square
     * @return square
     */
    boolean isSquareFree(amazons.Square s, amazons.Square asEmpty, int dir) {
        for (int i = dir + 1; i < 8; i++) {
            int col = s.col() + amazons.Square.DIR[i][0];
            int row = s.row() + amazons.Square.DIR[i][1];
            if (row == Board.SIZE || row < 0
                    || col == Board.SIZE || col < 0) {
                continue;
            }
            amazons.Square next = amazons.Square.sq(col, row);
            if (next.getPiece().equals(amazons.Piece.EMPTY)
                    || next.equals(asEmpty)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Move FROM-TO(SPEAR), assuming this is a legal move.
     */
    void makeMove(amazons.Square from, amazons.Square to,
                  amazons.Square spear) {
        makeMove(amazons.Move.mv(from, to, spear));
    }

    /**
     * Move according to MOVE, assuming it is a legal move.
     */
    void makeMove(amazons.Move move) {
        if (!isLegal(move)) {
            throw error("Invalid move");
        } else {
            allMoves.push(move);
            this.put(move.from().getPiece(), move.to());
            this.put(EMPTY, move.from());
            this.put(SPEAR, move.spear());
        }
    }

    /**
     * Undo one move.  Has no effect on the initial board.
     */
    void undo() {
        amazons.Move move = allMoves.pop();
        this.put(move.to().getPiece(), move.from());
        this.put(EMPTY, move.to());
        if (!move.from().equals(move.spear())) {
            this.put(EMPTY, move.spear());
        }
    }

    /**
     * Return an Iterator over the Squares that are reachable by an
     * unblocked queen move from FROM. Does not pay attention to what
     * piece (if any) is on FROM, nor to whether the game is finished.
     * Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     * feature is useful when looking for Moves, because after moving a
     * piece, one wants to treat the Square it came from as empty for
     * purposes of spear throwing.
     */
    Iterator<amazons.Square> reachableFrom(amazons.Square from,
                                           amazons.Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /**
     * Return an Iterator over all legal moves on the current board.
     */
    Iterator<amazons.Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /**
     * Return an Iterator over all legal moves on the current board for
     * SIDE (regardless of whose turn it is).
     */
    Iterator<amazons.Move> legalMoves(amazons.Piece side) {
        return new LegalMoveIterator(side);
    }


    /**
     * An iterator used by reachableFrom.
     */
    private class ReachableFromIterator implements Iterator<amazons.Square> {

        /**
         * Iterator of all squares reachable by queen move from FROM,
         * treating ASEMPTY as empty.
         */
        ReachableFromIterator(amazons.Square from, amazons.Square asEmpty) {
            _from = from;
            _dir = 0;
            _steps = 0;
            _asEmpty = asEmpty;
            nextSquare = from;
        }

        @Override
        public boolean hasNext() {
            if (_dir < 8) {
                return done || isSquareFree(_from, _asEmpty, _dir);
            } else {
                return done;
            }
        }

        @Override
        public amazons.Square next() {
            toNext();
            while (!found && _dir < 8) {
                toNext();
            }
            return nextSquare;

        }

        /**
         * Advance _dir and _steps, so that the next valid Square is
         * _steps steps in direction _dir from _from.
         */
        private void toNext() {
            found = false;

            int col = nextSquare.col() + amazons.Square.DIR[_dir][0];
            int row = nextSquare.row() + amazons.Square.DIR[_dir][1];

            if (row >= Board.SIZE || row < 0 || col >= Board.SIZE
                    || col < 0 && _dir < 8) {
                nextSquare = _from;
                _dir++;
            } else {
                nextSquare = amazons.Square.sq(col, row);
                if (nextSquare.getPiece().equals(amazons.Piece.EMPTY)
                        || nextSquare.equals(_asEmpty)) {
                    found = true;
                    done = lookahead(nextSquare, _dir);
                } else {
                    _dir++;
                    nextSquare = _from;
                }
            }
        }


        /**
         * Look ahead to next reachable square to check if iterator is done.
         * @param s square
         * @param dir direction
         * @return boolean
         */
        private boolean lookahead(amazons.Square s, int dir) {
            int col = nextSquare.col() + amazons.Square.DIR[_dir][0];
            int row = nextSquare.row() + amazons.Square.DIR[_dir][1];
            if (row >= Board.SIZE || row < 0
                    || col >= Board.SIZE || col < 0) {
                return false;
            }
            if (amazons.Square.sq(col, row).
                    getPiece().equals(amazons.Piece.EMPTY)
                    || amazons.Square.sq(col, row).equals(_asEmpty)) {
                return true;
            }
            return false;

        }

        /** Done. */

        private boolean done = false;
        /**
         * Starting square.
         */
        private amazons.Square _from;
        /**
         * Current direction.
         */
        private int _dir;
        /**
         * Current distance.
         */
        private int _steps;
        /**
         * Square treated as empty.
         */
        private amazons.Square _asEmpty;
        /**
         * Current square of iterator.
         */
        private amazons.Square nextSquare;
        /**
         * True if toNext() finds square.
         */
        private boolean found = true;
    }

    /**
     * An iterator used by legalMoves.
     */
    private class LegalMoveIterator implements Iterator<amazons.Move> {

        /**
         * All legal moves for SIDE (WHITE or BLACK).
         */
        LegalMoveIterator(amazons.Piece side) {
            _startingSquares = amazons.Square.iterator();
            _spearThrows = NO_SQUARES;
            _fromPiece = side;

            if (side.equals(amazons.Piece.WHITE)) {
                queenList = whiteQueens;
            } else {
                queenList = blackQueens;
            }

            getNextQueenMove();
        }

        @Override
        public boolean hasNext() {
            if (done || _queenMoves == null || _spearThrows == null) {
                return false;
            }
            return true;
        }

        @Override
        public amazons.Move next() {
            toNext();
            while (!foundMove && !done) {
                toNext();
            }
            return move;
        }

        /**
         * Advance so that the next valid Move is
         * _start-_nextSquare(sp), where sp is the next value of
         * _spearThrows.
         */
        private void toNext() {
            foundMove = false;
            done = false;
            if (!_spearThrows.hasNext()) {
                if (_queenMoves.hasNext()) {
                    _nextQueen = _queenMoves.next();

                } else {
                    if (getNextQueenMove()) {
                        _nextQueen = _queenMoves.next();
                    } else {
                        done = true;
                        return;
                    }

                }
                _spearThrows = new ReachableFromIterator(_nextQueen, _start);
            }
            _nextSpear = _spearThrows.next();

            if (isLegal(_start, _nextQueen, _nextSpear)) {
                move = amazons.Move.mv(_start, _nextQueen, _nextSpear);
                foundMove = true;
                if (!_spearThrows.hasNext() && !_queenMoves.hasNext()) {
                    if (queenIndex > queenList.size() - 1) {
                        done = true;
                    }
                }
            } else {
                foundMove = false;
            }
        }

        /**
         * Create iterator for next queen if
         * previous queen has no more spear throws.
         * @return boolean
         */
        private boolean getNextQueenMove() {
            if (queenIndex < queenList.size()) {
                _start = queenList.get(queenIndex);
                _queenMoves = reachableFrom(_start, _start);
                queenIndex++;
                return true;
            }
            return false;
        }


        /**
         * Color of side whose moves we are iterating.
         */
        private amazons.Piece _fromPiece;
        /**
         * Current starting square.
         */
        private amazons.Square _start;
        /**
         * Remaining starting squares to consider.
         */
        private Iterator<amazons.Square> _startingSquares;
        /**
         * Current piece's new position.
         */
        private amazons.Square _nextQueen = _start;
        /**
         * Next spear.
         */
        private amazons.Square _nextSpear;
        /**
         * Remaining moves from _start to consider.
         */
        private Iterator<amazons.Square> _queenMoves;
        /**
         * Remaining spear throws from _piece to consider.
         */
        private Iterator<amazons.Square> _spearThrows;
        /**
         * Current move of iterator.
         */
        private amazons.Move move;
        /**
         * True if legal move is found.
         */
        private boolean foundMove = true;
        /**
         * Index of current queen.
         */
        private int queenIndex = 0;

        /** queenList. */
        private List<amazons.Square> queenList;
        /**
         * If iterator is done.
         */
        private boolean done = false;
    }

    @Override
    public String toString() {
        String row = "";
        String result = "";

        for (int j = SIZE * (SIZE - 1); j >= 0; j -= SIZE) {
            for (int i = 0; i < SIZE; i++) {
                row += amazons.Square.sq(j + i).getPiece().toString();
                if (i != SIZE - 1) {
                    row += " ";
                }
            }

            result += "   " + row + "\n";
            row = "";
        }
        return result;
    }

    /** setTurn.
     * @param p piece */
    public void setTurn(amazons.Piece p) {
        this._turn = p;
    }

    /**
     * An empty iterator for initialization.
     */
    private static final Iterator<amazons.Square> NO_SQUARES =
            Collections.emptyIterator();

    /**
     * Piece whose turn it is (BLACK or WHITE).
     */
    private amazons.Piece _turn;

    /**
     * Cached value of winner on this board, or EMPTY if it has not been
     * computed.
     */
    private amazons.Piece _winner;

    /**
     * Locations of WHITE queens.
     */
    private List<amazons.Square> whiteQueens = new ArrayList<amazons.Square>();

    /** get whiteQueens.
     * @return list squares*/
    public List<amazons.Square> getWhiteQueens() {
        return whiteQueens;
    }

    /**
     * Locations of BLACK queens.
     */
    private List<amazons.Square> blackQueens = new ArrayList<amazons.Square>();

    /** get blackQueens.
     *
     * @return list of squares */
    public List<amazons.Square> getBlackQueens() {
        return blackQueens;
    }

    /**
     * Iterator over all squares.
     */
    private Iterator<amazons.Square> allSquares;

    /**
     * List of all queen positions.
     */
    private List<amazons.Square> queenList;

    /**
     * Stack of all moves.
     */
    private static Stack<amazons.Move> allMoves = new Stack<amazons.Move>();
}


