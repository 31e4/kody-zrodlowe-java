import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ChessGame extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessGame game = new ChessGame();
            game.setVisible(true);
        });
    }

    public ChessGame() {
        setTitle("Java Chess Game");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(new ChessBoardPanel());
    }
}

class ChessBoardPanel extends JPanel implements MouseListener {
    private static final int SIZE = 8;
    private Square[][] board = new Square[SIZE][SIZE];
    private Piece selectedPiece = null;
    private int selectedRow = -1, selectedCol = -1;
    private boolean whiteTurn = true;
    private Set<Point> highlightedSquares = new HashSet<>();

    public ChessBoardPanel() {
        initializeBoard();
        addMouseListener(this);
    }

    private void initializeBoard() {
        // Initialize squares and place pieces
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                board[row][col] = new Square(row, col);
            }
        }

        // Place pawns
        for (int col = 0; col < SIZE; col++) {
            board[1][col].setPiece(new Pawn(false));
            board[6][col].setPiece(new Pawn(true));
        }

        // Place Rooks
        board[0][0].setPiece(new Rook(false));
        board[0][7].setPiece(new Rook(false));
        board[7][0].setPiece(new Rook(true));
        board[7][7].setPiece(new Rook(true));

        // Place Knights
        board[0][1].setPiece(new Knight(false));
        board[0][6].setPiece(new Knight(false));
        board[7][1].setPiece(new Knight(true));
        board[7][6].setPiece(new Knight(true));

        // Place Bishops
        board[0][2].setPiece(new Bishop(false));
        board[0][5].setPiece(new Bishop(false));
        board[7][2].setPiece(new Bishop(true));
        board[7][5].setPiece(new Bishop(true));

        // Place Queens
        board[0][3].setPiece(new Queen(false));
        board[7][3].setPiece(new Queen(true));

        // Place Kings
        board[0][4].setPiece(new King(false));
        board[7][4].setPiece(new King(true));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int cellSize = Math.min(getWidth(), getHeight()) / SIZE;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                boolean isLight = (row + col) % 2 == 0;
                g.setColor(isLight ? new Color(240, 217, 181) : new Color(181, 136, 99));
                g.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);

                // Highlight selected or possible move squares
                if (highlightedSquares.contains(new Point(row, col))) {
                    g.setColor(new Color(0, 255, 0, 128));
                    g.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
                }
            }
        }

        // Draw pieces
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Piece piece = board[row][col].getPiece();
                if (piece != null) {
                    drawPiece(g, piece, row, col, cellSize);
                }
            }
        }
    }

    private void drawPiece(Graphics g, Piece piece, int row, int col, int size) {
        String imgPath = piece.getImagePath();
        Image img = getImage(imgPath);
        if (img != null) {
            g.drawImage(img, col * size + 10, row * size + 10, size - 20, size - 20, this);
        } else {
            // fallback: draw initials
            g.setColor(piece.isWhite() ? Color.WHITE : Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, size / 2));
            String initial = piece.getInitial();
            g.drawString(initial, col * size + size / 2 - 10, row * size + size / 2 + 10);
        }
    }

    private Image getImage(String path) {
        try {
            return new ImageIcon(getClass().getResource(path)).getImage();
        } catch (Exception e) {
            return null;
        }
    }

    // Mouse events
    @Override
    public void mouseClicked(MouseEvent e) {
        int cellSize = Math.min(getWidth(), getHeight()) / SIZE;
        int col = e.getX() / cellSize;
        int row = e.getY() / cellSize;

        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) return;

        Square clickedSquare = board[row][col];

        Piece clickedPiece = clickedSquare.getPiece();

        if (selectedPiece != null) {
            // If clicked on a highlighted move, move the piece
            if (highlightedSquares.contains(new Point(row, col))) {
                movePiece(selectedRow, selectedCol, row, col);
                whiteTurn = !whiteTurn;
                selectedPiece = null;
                highlightedSquares.clear();
                repaint();
                return;
            } else if (clickedPiece != null && clickedPiece.isWhite() == whiteTurn) {
                // Select a different piece
                selectedPiece = clickedPiece;
                selectedRow = row;
                selectedCol = col;
                highlightedSquares = getValidMoves(row, col);
                repaint();
                return;
            } else {
                // Deselect
                selectedPiece = null;
                highlightedSquares.clear();
                repaint();
                return;
            }
        } else {
            // No selection yet
            if (clickedPiece != null && clickedPiece.isWhite() == whiteTurn) {
                selectedPiece = clickedPiece;
                selectedRow = row;
                selectedCol = col;
                highlightedSquares = getValidMoves(row, col);
                repaint();
            }
        }
    }

    private void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        Square from = board[fromRow][fromCol];
        Square to = board[toRow][toCol];
        Piece movingPiece = from.getPiece();

        // Basic move validation (already filtered by getValidMoves)
        to.setPiece(movingPiece);
        from.setPiece(null);
    }

    private Set<Point> getValidMoves(int row, int col) {
        Set<Point> moves = new HashSet<>();
        Piece piece = board[row][col].getPiece();
        if (piece == null) return moves;

        List<Point> potentialMoves = piece.getLegalMoves(row, col, board);
        for (Point p : potentialMoves) {
            // For simplicity, ignore check detection here
            moves.add(p);
        }
        return moves;
    }

    // Unused mouse events
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}

// Board square class
class Square {
    private int row, col;
    private Piece piece;

    public Square(int row, int col) {
        this.row = row;
        this.col = col;
        this.piece = null;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece p) {
        this.piece = p;
    }
}

// Abstract Piece class
abstract class Piece {
    private boolean isWhite;

    public Piece(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public abstract List<Point> getLegalMoves(int row, int col, Square[][] board);
    public abstract String getImagePath();
    public abstract String getInitial();
}

// Specific Piece implementations
class Pawn extends Piece {
    public Pawn(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public List<Point> getLegalMoves(int row, int col, Square[][] board) {
        List<Point> moves = new ArrayList<>();
        int dir = isWhite() ? -1 : 1;
        int startRow = isWhite() ? 6 : 1;

        // Forward move
        if (isInBounds(row + dir, col) && board[row + dir][col].getPiece() == null) {
            moves.add(new Point(row + dir, col));
            // Double move from start
            if (row == startRow && board[row + 2 * dir][col].getPiece() == null) {
                moves.add(new Point(row + 2 * dir, col));
            }
        }

        // Capture diagonally
        if (isInBounds(row + dir, col - 1) && board[row + dir][col - 1].getPiece() != null
                && board[row + dir][col - 1].getPiece().isWhite() != isWhite()) {
            moves.add(new Point(row + dir, col - 1));
        }
        if (isInBounds(row + dir, col + 1) && board[row + dir][col + 1].getPiece() != null
                && board[row + dir][col + 1].getPiece().isWhite() != isWhite()) {
            moves.add(new Point(row + dir, col + 1));
        }

        // En passant not implemented for simplicity

        return filterInBounds(moves);
    }

    @Override
    public String getImagePath() {
        return "/images/" + (isWhite() ? "white_pawn.png" : "black_pawn.png");
    }

    @Override
    public String getInitial() {
        return "P";
    }

    private List<Point> filterInBounds(List<Point> points) {
        List<Point> inBounds = new ArrayList<>();
        for (Point p : points) {
            if (isInBounds(p.x, p.y))
                inBounds.add(p);
        }
        return inBounds;
    }

    private boolean isInBounds(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }
}

class Rook extends Piece {
    public Rook(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public List<Point> getLegalMoves(int row, int col, Square[][] board) {
        List<Point> moves = new ArrayList<>();
        int[] directions = {-1, 1};

        // Horizontal and vertical
        for (int d : directions) {
            // Horizontal
            for (int c = col + d; isInBounds(row, c); c += d) {
                if (addMoveIfPossible(row, c, moves, board)) break;
            }
            // Vertical
            for (int r = row + d; isInBounds(r, col); r += d) {
                if (addMoveIfPossible(r, col, moves, board)) break;
            }
        }
        return moves;
    }

    @Override
    public String getImagePath() {
        return "/images/" + (isWhite() ? "white_rook.png" : "black_rook.png");
    }

    @Override
    public String getInitial() {
        return "R";
    }

    private boolean addMoveIfPossible(int r, int c, List<Point> moves, Square[][] board) {
        Piece p = board[r][c].getPiece();
        if (p == null) {
            moves.add(new Point(r, c));
            return false; // continue
        } else {
            if (p.isWhite() != isWhite()) {
                moves.add(new Point(r, c));
            }
            return true; // block further movement
        }
    }

    private boolean isInBounds(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }
}

class Knight extends Piece {
    public Knight(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public List<Point> getLegalMoves(int row, int col, Square[][] board) {
        int[][] offsets = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };
        List<Point> moves = new ArrayList<>();
        for (int[] offset : offsets) {
            int r = row + offset[0];
            int c = col + offset[1];
            if (isInBounds(r, c)) {
                Piece p = board[r][c].getPiece();
                if (p == null || p.isWhite() != isWhite()) {
                    moves.add(new Point(r, c));
                }
            }
        }
        return moves;
    }

    @Override
    public String getImagePath() {
        return "/images/" + (isWhite() ? "white_knight.png" : "black_knight.png");
    }

    @Override
    public String getInitial() {
        return "N";
    }

    private boolean isInBounds(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }
}

class Bishop extends Piece {
    public Bishop(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public List<Point> getLegalMoves(int row, int col, Square[][] board) {
        List<Point> moves = new ArrayList<>();
        int[] directions = {-1, 1};

        for (int dr : directions) {
            for (int dc : directions) {
                int r = row + dr;
                int c = col + dc;
                while (isInBounds(r, c)) {
                    if (addMoveIfPossible(r, c, moves, board))
                        break;
                    r += dr;
                    c += dc;
                }
            }
        }
        return moves;
    }

    @Override
    public String getImagePath() {
        return "/images/" + (isWhite() ? "white_bishop.png" : "black_bishop.png");
    }

    @Override
    public String getInitial() {
        return "B";
    }

    private boolean addMoveIfPossible(int r, int c, List<Point> moves, Square[][] board) {
        Piece p = board[r][c].getPiece();
        if (p == null) {
            moves.add(new Point(r, c));
            return false; // continue
        } else {
            if (p.isWhite() != isWhite()) {
                moves.add(new Point(r, c));
            }
            return true; // block further movement
        }
    }

    private boolean isInBounds(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }
}

class Queen extends Piece {
    public Queen(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public List<Point> getLegalMoves(int row, int col, Square[][] board) {
        List<Point> moves = new ArrayList<>();
        // Combine rook and bishop moves
        moves.addAll(new Rook(isWhite()).getLegalMoves(row, col, board));
        moves.addAll(new Bishop(isWhite()).getLegalMoves(row, col, board));
        return moves;
    }

    @Override
    public String getImagePath() {
        return "/images/" + (isWhite() ? "white_queen.png" : "black_queen.png");
    }

    @Override
    public String getInitial() {
        return "Q";
    }
}

class King extends Piece {
    public King(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public List<Point> getLegalMoves(int row, int col, Square[][] board) {
        int[][] offsets = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},   {1, 1}
        };
        List<Point> moves = new ArrayList<>();
        for (int[] offset : offsets) {
            int r = row + offset[0];
            int c = col + offset[1];
            if (isInBounds(r, c)) {
                Piece p = board[r][c].getPiece();
                if (p == null || p.isWhite() != isWhite()) {
                    moves.add(new Point(r, c));
                }
            }
        }
        // Castling not implemented for simplicity
        return moves;
    }

    @Override
    public String getImagePath() {
        return "/images/" + (isWhite() ? "white_king.png" : "black_king.png");
    }

    @Override
    public String getInitial() {
        return "K";
    }

    private boolean isInBounds(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }
}
