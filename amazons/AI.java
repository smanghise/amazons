package amazons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * A Player that automatically generates moves.
 *
 * @author Sasha Manghise
 */

class AI extends Player {

    /**
     * A position magnitude indicating a win (for white if positive, black
     * if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;

    /**
     * A magnitude greater than a normal value.
     */
    private static final int INFTY = Integer.MAX_VALUE;

    /**
     * Starting max depth.
     */
    private int startMax = 0;

    /**
     * A new AI with no piece or controller (intended to produce
     * a template).
     */
    AI() {
        this(null, null);
    }

    /**
     * A new AI playing PIECE under control of CONTROLLER.
     */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        if (move != null) {
            _controller.reportMove(move);
            return move.toString();
        }
        return null;
    }

    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     */
    private Move findMove() {
        Board b = new Board(board());
        _lastFoundMove = null;
        startMax = maxDepth(b);
        findMove(b, startMax, true, 1, -INFTY, INFTY);
        return _lastFoundMove;
    }

    /**
     * The move found by the last call to one of the ...FindMove methods
     * below.
     */
    private Move _lastFoundMove;

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _lastMoveFound.
     */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {

        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }
        int bestVal = INFTY * -sense;
        int tempVal = 0;

        board.findAllQueens(Piece.WHITE);
        board.findAllQueens(Piece.BLACK);


        Iterator<Move> allMoves = board.legalMoves();
        while (allMoves.hasNext()) {
            Move move = allMoves.next();
            if (move == null) {
                break;
            }
            board.makeMove(move);
            tempVal = findMove(board, depth - 1, false, -sense, alpha, beta);

            if (tempVal > bestVal) {
                bestVal = tempVal;
                _lastFoundMove = move;
            }

            board.undo();

            if (sense == 1) {
                alpha = Math.max(alpha, bestVal);

            } else if (sense == -1) {
                beta = Math.min(beta, bestVal);
            }

            if (alpha >= beta) {
                break;
            }
        }
        return bestVal;
    }

    /**
     * Return a heuristically determined maximum search depth
     * based on characteristics of BOARD.
     */
    private int maxDepth(Board board) {
        int N = board.numMoves();

        if (N < 3 * 10 + 5) {
            return 1;
        }
        return 2;
    }
    /**
     * Return a heuristic value for BOARD.
     */
    private int staticScore(Board board) {
        Square wQueen;
        Square bQueen;

        int scoreWhite = 0;
        int scoreBlack = 0;
        int score = 0;

        long startTime = System.nanoTime();

        List<Integer> white = new ArrayList<Integer>();
        List<Integer> black = new ArrayList<Integer>();

        for (int cycle = 0; cycle < board.getWhiteQueens().size(); cycle++) {
            wQueen = board().getWhiteQueens().get(cycle);
            bQueen = board().getBlackQueens().get(cycle);

            for (int index = 0; index < Board.SIZE * Board.SIZE; index++) {
                Square sq = Square.sq(index);
                white.add(index, distance(wQueen, sq));
                black.add(index, distance(bQueen, sq));
            }

            for (int index = 0; index < Board.SIZE * Board.SIZE; index++) {
                if (black.get(index) < white.get(index)) {
                    scoreWhite++;
                } else if (black.get(index) > white.get(index)) {
                    scoreBlack++;
                }
            }
        }

        long endTime = System.nanoTime();
        evaluationTime = endTime - startTime;
        evaluated++;

        if (board.turn().equals(Piece.WHITE)) {
            if (scoreWhite > scoreBlack) {
                score = scoreWhite;
            }
        }
        if (board.turn().equals(Piece.BLACK)) {
            score = scoreBlack;
        }
        return score;
    }

    /**
     * Distance calculator.
     * @param from fromsquare
     * @param to tosquare
     * @return int
     */
    public static int distance(Square from, Square to) {

        int dx = Math.abs(from.col() - to.col());
        int dy = Math.abs(from.row() - from.row());
        return (int) Math.sqrt((dx * dx) + (dy * dy));
    }


    /**
     * List of all moves generated by LegalMovesIterator.
     */
    private List<Move> listofMoves = new ArrayList<Move>();

    /** Evaluation time. */
    private long evaluationTime = 0;

    /** Evaluated. */
    private int evaluated = 0;

}
