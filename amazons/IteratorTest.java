package amazons;
import org.junit.Test;
import ucb.junit.textui;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Junit tests for our Board iterators.
 *  @author Sasha Manghise
 */
public class IteratorTest {

    /** Run the JUnit tests in this package. */
    public static void main(String[] ignored) {
        textui.runClasses(IteratorTest.class);
    }


    /** Tests reachableFromIterator to make sure it returns all reachable
     *  Squares. This method may need to be changed based on
     *   your implementation. */
    @Test
    public void testReachableFrom() {
        Board b = new Board();
        buildBoard(b, REACHABLEFROMTESTBOARD);
        int numSquares = 0;
        Set<Square> squares = new HashSet<>();
        Iterator<Square> reachableFrom = b.reachableFrom(Square.sq(5, 5), null);
        while (reachableFrom.hasNext()) {
            Square s = reachableFrom.next();
            assertTrue(REACHABLEFROMTESTSQUARES.contains(s));
            numSquares += 1;
            squares.add(s);
        }
        assertEquals(REACHABLEFROMTESTSQUARES.size(), numSquares);
        assertEquals(REACHABLEFROMTESTSQUARES.size(), squares.size());
    }

    @Test
    public void testReachableFromAsEmpty() {
        Board b = new Board();
        buildBoard(b, REACHABLEFROMTESTBOARD);
        int numSquares = 0;
        Set<Square> squares = new HashSet<>();
        Iterator<Square> reachableFrom = b.reachableFrom(Square.sq(5, 5),
                Square.sq(5, 6));
        while (reachableFrom.hasNext()) {
            Square s = reachableFrom.next();
            assertTrue(REACHABLEFROMASEMPTYTEST.contains(s));
            numSquares += 1;
            squares.add(s);
        }
        assertEquals(REACHABLEFROMASEMPTYTEST.size(), numSquares);
        assertEquals(REACHABLEFROMASEMPTYTEST.size(), squares.size());
    }

    /** Test ReachableFrom iterator. */
    @Test
    public void testReachable() {
        Board b = new Board();
        int numSquares = 0;
        Set<Square> squares = new HashSet<>();
        Iterator<Square> reachableFrom = b.reachableFrom(Square.sq(5, 5), null);
        while (reachableFrom.hasNext()) {
            Square s = reachableFrom.next();
            numSquares++;
        }
        assertEquals(35, numSquares);
    }

    /** ReachableFrom corner cases */
    @Test
    public void checkReachableFromCorner() {
        Board b = new Board();
        int numSquares = 0;
        String[] corners = new String[] {"a1", "j1", "a10", "j10"};
        for (String corner: corners) {
            Iterator<Square> sqit = b.reachableFrom(Square.sq(corner), null);
            numSquares = 0;
            while (sqit.hasNext()) {
                Square newSquare = sqit.next();
                numSquares++;
            }
            assertEquals(13, numSquares);
        }
    }

    /** ReachableFrom side cases. */
    @Test
    public void checkReachableFromSide() {
        Board b = new Board();
        int numSquares = 0;
        String[] corners = new String[] {"e1", "a5", "e10", "j5"};
        for (String corner: corners) {
            Iterator<Square> sqit = b.reachableFrom(Square.sq(corner), null);
            numSquares = 0;
            while (sqit.hasNext()) {
                Square newSquare = sqit.next();
                numSquares++;
            }
            assertEquals(19, numSquares);
        }
    }

    /** Queen completely surrounded. */
    @Test
    public void checkReachableSurrounded() {
        Board b = new Board();
        String[] places = new String[] {"e6", "e4", "d5", "f5",
            "f6", "d6", "d4", "f4"};
        for (String place: places) {
            b.put(Piece.SPEAR, Square.sq(place));
        }
        int numSquares = 0;
        Iterator<Square> sqit = b.reachableFrom(Square.sq("e5"), null);
        while (sqit.hasNext()) {
            Square newSquare = sqit.next();
            numSquares++;
        }
        assertEquals(0, numSquares);
    }



    /** Tests legalMovesIterator to make sure it returns all legal Moves.
     *  This method needs to be finished and may need to be changed
     *  based on your implementation. */
    @Test
    public void testLegalMoves() {
        Board b = new Board();
        String[] spears = new String[] {"a4", "b4", "c4",
            "d4", "d3", "d2", "d1"};
        for (String s: spears) {
            b.put(S, Square.sq(s));
        }

        b.put(W, Square.sq("b1"));
        b.put(W, Square.sq("c2"));
        b.put(W, Square.sq("b3"));
        b.put(W, Square.sq("a2"));

        b.getBlackQueens().clear();
        b.getWhiteQueens().clear();
        b.getWhiteQueens().add(Square.sq("b1"));

        int numMoves = 0;
        Set<Move> moves = new HashSet<>();
        Iterator<Move> legalMoves = b.legalMoves(Piece.WHITE);
        while (legalMoves.hasNext()) {
            Move m = legalMoves.next();
            assertTrue(LEGALTESTMOVES.contains(m));
            numMoves++;
            moves.add(m);
        }
        assertEquals(13, numMoves);
        assertEquals(LEGALTESTMOVES.size(), moves.size());
    }


    private void buildBoard(Board b, Piece[][] target) {
        for (int col = 0; col < Board.SIZE; col++) {
            for (int row = 0; row < Board.SIZE; row++) {
                Piece piece = target[row][col];
                b.put(piece, Square.sq(col, row));
            }
        }
    }

    static final Piece E = Piece.EMPTY;

    static final Piece W = Piece.WHITE;

    static final Piece B = Piece.BLACK;

    static final Piece S = Piece.SPEAR;

    static final Piece[][] REACHABLEFROMTESTBOARD =
    {
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, W, W },
        { E, E, E, E, E, E, E, S, E, S },
        { E, E, E, S, S, S, S, E, E, S },
        { E, E, E, S, E, E, E, E, B, E },
        { E, E, E, S, E, W, E, E, B, E },
        { E, E, E, S, S, S, B, W, B, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
    };

    static final Set<Square> REACHABLEFROMTESTSQUARES =
            new HashSet<>(Arrays.asList(
                    Square.sq(5, 4),
                    Square.sq(4, 4),
                    Square.sq(4, 5),
                    Square.sq(6, 5),
                    Square.sq(7, 5),
                    Square.sq(6, 4),
                    Square.sq(7, 3),
                    Square.sq(8, 2)));

    static final Set<Square> REACHABLEFROMASEMPTYTEST =
            new HashSet<>(Arrays.asList(
                    Square.sq(5, 6),
                    Square.sq(5, 7),
                    Square.sq(5, 8),
                    Square.sq(5, 9),
                    Square.sq(5, 4),
                    Square.sq(4, 4),
                    Square.sq(4, 5),
                    Square.sq(6, 5),
                    Square.sq(7, 5),
                    Square.sq(6, 4),
                    Square.sq(7, 3),
                    Square.sq(8, 2)));


    static final Set<Move> LEGALTESTMOVES =
            new HashSet<>(Arrays.asList(
                    Move.mv("b1-b2(c3)"),
                    Move.mv("b1-b2(c1)"),
                    Move.mv("b1-b2(b1)"),
                    Move.mv("b1-b2(a1)"),
                    Move.mv("b1-b2(a3)"),
                    Move.mv("b1-c1(b1)"),
                    Move.mv("b1-c1(a1)"),
                    Move.mv("b1-c1(b2)"),
                    Move.mv("b1-c1(a3)"),
                    Move.mv("b1-a1(b2)"),
                    Move.mv("b1-a1(c3)"),
                    Move.mv("b1-a1(b1)"),
                    Move.mv("b1-a1(c1)"),
                    Move.mv("b1-a1(c1)")));


}
