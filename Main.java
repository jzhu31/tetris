//Tetris
//Amanda Zhu and Alec Petrone

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Main extends JComponentWithEvents {
  
  //instance variables
  private boolean debug = true;
  private int rows = 15;
  private int cols = 10;
  private Color[][] board = new Color[rows][cols];
  private Color emptyColor = Color.blue;
  private int border = 30;
  private Random random = new Random();
  private boolean[][] fallingPiece;
  private Color fallingPieceColor;
  private int fallingPieceRow;
  private int fallingPieceCol;
  public boolean isGameOver = false;
  private int score = 0;
  
  //pieces or tentrominoes
  //copied from http://kosbie.net/cmu/fall-08/15-100/handouts/notes-tetris/2_3_CreatingTheFallingPiece.html
  private static final boolean[][] I_PIECE = {
    { true,  true,  true,  true}
  };
  
  private static final boolean[][] J_PIECE = {
    { true, false, false },
    { true, true,  true}
  };
  
  private static final boolean[][] L_PIECE = {
    { false, false, true},
    { true,  true,  true}
  };
  
  private static final boolean[][] O_PIECE = {
    { true, true},
    { true, true}
  };
  
  private static final boolean[][] S_PIECE = {
    { false, true, true},
    { true,  true, false }
  };
  
  private static final boolean[][] T_PIECE = {
    { false, true, false },
    { true,  true, true}
  };

  private static final boolean[][] Z_PIECE = {
    { true,  true, false },
    { false, true, true}
  };
  
  private static boolean[][][] TETRIS_PIECES = {
    I_PIECE, J_PIECE, L_PIECE, O_PIECE, S_PIECE, T_PIECE, Z_PIECE
  };
  
  private static Color[] TETRIS_PIECE_COLORS = {
    Color.red, Color.yellow, Color.magenta, Color.pink,
    Color.cyan, Color.green, Color.orange
  }; 
  
  //begin the game
  public void start() {
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        board[row][col] = emptyColor;
      }
    }
    newFallingPiece();
    //loop("tetris.mid");  // play tetris.mid in a loop
  }
  
  public void resetGame() {
    if (isGameOver) {
      start();
      isGameOver = false;
    }
  }
  
  public void timerFired() {
    if (!isGameOver) {
      setTimerDelay(550);
      if (!moveFallingPiece(1, 0)) {
        placeFallingPiece();
        newFallingPiece();
        if (!moveFallingPiece(1, 0)) {
          isGameOver = true;
        }
      }
    }
    //else {
      //stopSounds();
    //}
  }
  
  //pieces
  public void newFallingPiece() {
    int pieceIndex = random.nextInt(TETRIS_PIECE_COLORS.length);
    fallingPiece = TETRIS_PIECES[pieceIndex];
    fallingPieceColor = TETRIS_PIECE_COLORS[pieceIndex];
    int fallingPieceWidth = fallingPiece[0].length;
    fallingPieceRow = 0;
    fallingPieceCol = (cols / 2) - (fallingPieceWidth / 2);
  }
  
  public boolean moveFallingPiece(int dRow, int dCol) {
    if (!isGameOver) {
      fallingPieceRow += dRow;
      fallingPieceCol += dCol;
      if (!fallingPieceIsLegal(fallingPiece, fallingPieceRow, fallingPieceCol)) {
        fallingPieceRow -= dRow;
        fallingPieceCol -= dCol;
        return false;
      }
    }
    return true;
  }
  
  public boolean fallingPieceIsLegal(boolean[][] fallingPiece, int fallingPieceRow, int fallingPieceCol) {
    for (int i = 0; i < fallingPiece.length; i++) {
      for (int j = 0; j < fallingPiece[0].length; j++)  {
        if (fallingPiece[i][j]) {
          if (fallingPieceRow < 0 || (fallingPieceRow + i) >= rows || 
              fallingPieceCol < 0 || (fallingPieceCol + j) >= cols || 
              board[fallingPieceRow + i][fallingPieceCol + j] != emptyColor) {
            return false;
          }
        }
      }
    }
    return true;
  }
 
  public void rotateFallingPiece() {
    int dimension1 = fallingPiece.length;
    int dimension2 = fallingPiece[0].length;
    boolean[][] rotatedFallingPiece = new boolean[dimension2][dimension1];
    for (int i = 0; i < rotatedFallingPiece.length; i++)
      for (int j = 0; j < rotatedFallingPiece[0].length; j++)
        rotatedFallingPiece[i][j] = fallingPiece[j % fallingPiece.length][fallingPiece[0].length - 1 - i];
    fallingPiece = rotatedFallingPiece;
    fallingPieceRow -= (rotatedFallingPiece.length - rotatedFallingPiece[0].length) / 2;
    fallingPieceCol -= (rotatedFallingPiece[0].length - rotatedFallingPiece.length) / 2;
    if (!fallingPieceIsLegal(fallingPiece, fallingPieceRow, fallingPieceCol)) {
      boolean[][] reverseFallingPiece = new boolean[dimension1][dimension2];
      for (int i = 0; i < reverseFallingPiece.length; i++)
        for (int j = 0; j < reverseFallingPiece[0].length; j++)
          reverseFallingPiece[i][j] = fallingPiece[fallingPiece.length - 1 - j][i % fallingPiece[0].length];
      fallingPiece = reverseFallingPiece;
      fallingPieceRow += (rotatedFallingPiece.length - rotatedFallingPiece[0].length) / 2;
      fallingPieceCol += (rotatedFallingPiece[0].length - rotatedFallingPiece.length) / 2;
    }
  }
  
  public void placeFallingPiece() {
    for (int i = 0; i < fallingPiece.length; i++) {
      for (int j = 0; j < fallingPiece[0].length; j++) {
        if (fallingPiece[i][j]) {
          board[fallingPieceRow + i][fallingPieceCol + j] = fallingPieceColor;
        }
      }
    }
    removeFullRows();
  }
  
  //clearing rows
  public void removeFullRows() {
    int newRow = rows - 1;
    int fullRows = 0;
    for (int oldRow = rows - 1; oldRow >= 0; oldRow--) {
      if (!isRowFull(oldRow)) {
        copyRows(oldRow, newRow);
        newRow--;
      }
      else {
        fullRows++;
      }
    }
    refillRows(fullRows);
    score += Math.pow(fullRows, 2);
  }

  public boolean isRowFull(int row) {
    for (int col = 0; col < cols; col++) {
        if (board[row][col] == emptyColor) {
          return false;
      }
    }
    return true;
  }
    
  public void copyRows(int oldRow, int newRow) {
    for (int col = 0; col < cols; col++) {
      board[newRow][col] = board[oldRow][col];
    }
  }
  
  public void refillRows(int fullRows) {
    for (int row = 0; row < fullRows; row++) {
      for (int col = 0; col < cols; col++) {
        board[row][col] = emptyColor;
      }
    }
  }  
      
  public void keyPressed(char key) {
    if (key == DOWN) {
      moveFallingPiece(1, 0);
    }
    else if (key == LEFT) {
      moveFallingPiece(0, -1);
    }
    else if (key == RIGHT) {
      moveFallingPiece(0, 1);
    }
    else if (key == UP) {
      rotateFallingPiece();
    }
    else if ((isGameOver == true) && (key == 'r')) {
      resetGame();
    }
    else if (key == SPACE) {
      newFallingPiece();
    }
  }
  
  //paints
  public void paintBoard(Graphics2D page, int row, int col, Color color) {
    int width = getWidth() - (2 * border), height = getHeight() - (2 * border);
    int left = col * width / cols;
    int right = (col + 1) * width / cols; 
    int top  = row * height / rows;
    int bottom = (row + 1) * height / rows;
    page.setColor(color);
    page.fillRect(left + border, top + border, right-left, bottom-top);
    page.setColor(Color.black);
    page.drawRect(left + border, top + border, right-left, bottom-top);
  }
  
  public void paintFallingPiece(Graphics2D page) {
    for (int i = 0; i < fallingPiece.length; i++) {
      for (int j = 0; j < fallingPiece[0].length; j++) {
        if (fallingPiece[i][j])  {
          paintBoard(page, fallingPieceRow + i, fallingPieceCol + j, fallingPieceColor);
        }
      }
    }
  }
  
  public void paint(Graphics2D page) {
    page.setColor(Color.orange);
    page.fillRect(0, 0, getWidth(), getHeight());
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        paintBoard(page, row, col, board[row][col]);
      }
    }
    paintFallingPiece(page);
    page.setColor(Color.black);
    page.setFont(new Font("SansSerif", Font.BOLD, 15));
    page.drawString("Score: " + score, 10, 20);
    if (isGameOver) {
      page.setColor(Color.white);
      page.setFont(new Font("SansSerif", Font.BOLD, 45));
      page.drawString("GAME OVER", 45, 210);
      page.setFont(new Font("SansSerif", Font.BOLD, 22));
      page.drawString("Press 'r' to restart", 92, 260);
    }
  }
  
  public static void main(String[] args) { launch(360, 510); }
}