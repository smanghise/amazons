package amazons;

import org.junit.Test;

import static amazons.Piece.*;
import static amazons.Piece.WHITE;
import static org.junit.Assert.*;
import ucb.junit.textui;
import java.util.Iterator;

/** The suite of all JUnit tests for the enigma package.
 *  @author
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {

        textui.runClasses(UnitTest.class, IteratorTest.class);

    }

    @Test
    public void testLegalIterator() {
        Board b = new Board();
        int numMoves = 0;
        Iterator<Move> legalMoves = b.legalMoves(Piece.WHITE);
        while (legalMoves.hasNext()) {
            Move m = legalMoves.next();
            numMoves += 1;
        }
        assertEquals(2176, numMoves);
    }

    /** Tests direction. */
    @Test
    public void testDirection() {
        Square s = Square.sq(5, 5);
        assertEquals(0, s.direction(Square.sq(5, 6)));
        assertEquals(1, s.direction(Square.sq(6, 6)));
        assertEquals(2, s.direction(Square.sq(6, 5)));
        assertEquals(3, s.direction(Square.sq(6, 4)));
        assertEquals(4, s.direction(Square.sq(5, 4)));
        assertEquals(5, s.direction(Square.sq(4, 4)));
        assertEquals(6, s.direction(Square.sq(4, 5)));
        assertEquals(7, s.direction(Square.sq(4, 6)));
        assertEquals(-1, s.direction(Square.sq(6, 7)));
        assertEquals(-1, s.direction(Square.sq(3, 1)));

    }

    @Test
    /** Tests mapping between square and notation. */
    public void testNotationMapping() {
        Board b = new Board();
        Piece p1 = b.get(0, 3);
        Piece p2 = b.get(Square.sq("d1"));
        assertEquals(Piece.WHITE, p1);
        assertEquals(Piece.WHITE, p2);
        assertEquals(p1, p2);
    }

    /** Tests QueenMove. */
    @Test
    public void testQueenMove() {
        Square a = Square.sq(0,  0);
        Square b = Square.sq(9, 9);
        assertEquals(b, a.queenMove(1,  9));
        assertEquals(a, b.queenMove(5,  9));
    }

    /** Tests basic correctness of put and get on the initialized board. */
    @Test
    public void testBasicPutGet() {
        Board b = new Board();
        b.put(BLACK, Square.sq(3, 5));
        assertEquals(b.get(3, 5), BLACK);
        b.put(WHITE, Square.sq(9, 9));
        assertEquals(b.get(9, 9), WHITE);
        b.put(EMPTY, Square.sq(3, 5));
        assertEquals(b.get(3, 5), EMPTY);
    }

    /** Tests proper identification of legal/illegal queen moves. */
    @Test
    public void testIsQueenMove() {
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(1, 5)));
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(2, 7)));
        assertFalse(Square.sq(0, 0).isQueenMove(Square.sq(5, 1)));
        assertTrue(Square.sq(1, 1).isQueenMove(Square.sq(9, 9)));
        assertTrue(Square.sq(2, 7).isQueenMove(Square.sq(8, 7)));
        assertTrue(Square.sq(3, 0).isQueenMove(Square.sq(3, 4)));
        assertTrue(Square.sq(7, 9).isQueenMove(Square.sq(0, 2)));
    }

    /** Tests toString for initial board state and a smiling board state. :) */
    @Test
    public void testToString() {
        Board b = new Board();
        assertEquals(INIT_BOARD_STATE, b.toString());
        makeSmile(b);
        assertEquals(SMILE, b.toString());
    }

    private void makeSmile(Board b) {
        b.put(EMPTY, Square.sq(0, 3));
        b.put(EMPTY, Square.sq(0, 6));
        b.put(EMPTY, Square.sq(9, 3));
        b.put(EMPTY, Square.sq(9, 6));
        b.put(EMPTY, Square.sq(3, 0));
        b.put(EMPTY, Square.sq(3, 9));
        b.put(EMPTY, Square.sq(6, 0));
        b.put(EMPTY, Square.sq(6, 9));
        for (int col = 1; col < 4; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(2, 7));
        for (int col = 6; col < 9; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(7, 7));
        for (int lip = 3; lip < 7; lip += 1) {
            b.put(WHITE, Square.sq(lip, 2));
        }
        b.put(WHITE, Square.sq(2, 3));
        b.put(WHITE, Square.sq(7, 3));
    }

    static final String INIT_BOARD_STATE =
            "   - - - B - - B - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   B - - - - - - - - B\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   W - - - - - - - - W\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - W - - W - - -\n";

    static final String SMILE =
            "   - - - - - - - - - -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - S - S - - S - S -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - W - - - - W - -\n"
                    + "   - - - W W W W - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n";

    @Test
    public void checkIsLegalFrom() {
        Board b = new Board();
        assertFalse(b.isLegal(Square.sq("d4")));
        assertTrue(b.isLegal(Square.sq("d1")));
        b.setTurn(Piece.BLACK);
        assertFalse(b.isLegal(Square.sq("g1")));
    }

    @Test
    public void checkIsLegalFromTo() {
        Board b = new Board();
        assertTrue(b.isLegal(Square.sq("d1"), Square.sq("d5")));
        assertFalse(b.isLegal(Square.sq("d1"), Square.sq("d1")));
        b.put(Piece.WHITE, Square.sq("d3"));
        assertFalse(b.isLegal(Square.sq("d1"), Square.sq("d5")));
    }

    @Test
    public void checkIsLegalFromToSpear() {
        Board b = new Board();
        assertTrue(b.isLegal(Square.sq("d1"), Square.sq("d5"),
                Square.sq("e6")));
        assertTrue(b.isLegal(Square.sq("g1"), Square.sq("g5"),
                Square.sq("g1")));
        assertFalse(b.isLegal(Square.sq("g5"), Square.sq("g1"),
                Square.sq("g5")));
    }

    @Test
    public void checkMakeMove() {
        Board b = new Board();
        b.makeMove(Move.mv("d1-d5(e6)"));
        assertEquals(Piece.EMPTY, Square.sq("d1").getPiece());
        assertEquals(Piece.WHITE, Square.sq("d5").getPiece());
        assertEquals(Piece.SPEAR, Square.sq("e6").getPiece());

        b.makeMove(Square.sq("d5"), Square.sq("d8"), Square.sq("d5"));
        assertEquals(Piece.WHITE, Square.sq("d8").getPiece());
        assertEquals(Piece.SPEAR, Square.sq("d5").getPiece());
    }

    @Test
    public void checkIsUnblockedMove() {
        Board b = new Board();
        assertTrue(b.isUnblockedMove(Square.sq("d1"),
                Square.sq("d5"), null));
        b.put(Piece.WHITE, Square.sq("g5"));
        assertFalse(b.isUnblockedMove(Square.sq("g1"),
                Square.sq("g8"), null));
        assertTrue(b.isUnblockedMove(Square.sq("g1"),
                Square.sq("g8"), Square.sq("g5")));
    }

}

