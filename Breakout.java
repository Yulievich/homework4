import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * !!!! INSTRUCTIONS FOR USE !!!!!!
 * 1. ! The game starts when you click on the racket. After that, the racket will follow your cursor within the window !
 * 2. Vertical && Horizontal ball velocity == ( ball speed ). Values are set to optimal for the game VX = 4;
 * VY = 5; Of course, you can change them, and they can change. But the author does not bear records for glitches.
 * Remember, this is an SDK, it's hard for him, he's not Unreal Engine 5!!!
 */

public class Breakout extends WindowProgram {

    // APPLICATION_WIDTH - The width of the application window in pixels, set to 400.
    public static final int APPLICATION_WIDTH = 600;

    // APPLICATION_HEIGHT - The height of the application window in pixels, set to 600.
    public static final int APPLICATION_HEIGHT = 800;

    // PADDLE_WIDTH - The width of the paddle in pixels, set to 60.
    private static final double PADDLE_WIDTH = 60;

    // PADDLE_HEIGHT - The height of the paddle in pixels, set to 10.
    private static final double PADDLE_HEIGHT = 10;

    // PADDLE_Y_OFFSET - The vertical offset of the paddle from the bottom of the window in pixels, set to 30.
    private static final double PADDLE_Y_OFFSET = 30;

    // N_BRICKS_PER_ROW - The number of bricks per row, set it.
    private static final double NBRICKS_PER_ROW = 10;

    // N_BRICK_ROWS - The number of brick rows, set it.
    private static final double NBRICK_ROWS = 10;

    private static double AMOUNT_OF_BRICKS = NBRICKS_PER_ROW * NBRICK_ROWS;

    // BRICK_SEP - The separation between bricks in pixels, set to 4.
    private static final double BRICK_SEP = 4;

    // BRICK_HEIGHT - The height of each brick in pixels, set to 8.
    private static final double BRICK_HEIGHT = 8;

    // BALL_RADIUS - The radius of the ball in pixels, set to 10.
    private static final double BALL_RADIUS = 10;

    // BRICK_Y_OFFSET - The vertical offset of the bricks from the top of the window in pixels,
    // calculated as the sum of BRICK_SEP and BRICK_HEIGHT.
    private static final double BRICK_Y_OFFSET = 15;

    // AMOUNT_OF_GAMES - The total amount of games to be played, set to 3.
    private static double NTURNS = 3;

    // Side wall width
    private static final double WALL_WIDTH = (int) (APPLICATION_WIDTH * 0.01);

    // PAUSE_TIME - The time delay in milliseconds for the game pause, calculated as 1000.0 divided by 100.
    private static final double PAUSE_TIME = 10;

    // vx - The horizontal velocity of the ball, set to 3.
    private double VX = 4;

    // vy - The vertical velocity of the ball, set to 4.
    private double VY = 5;
    // BALL - The graphical object representing the ball in the game.
    private static GOval BALL;

    // PADDLE - The graphical object representing the paddle in the game.
    private static GRect PADDLE;

    // TOP_WALL - The graphical object representing the top wall in the game.
    private static GRect TOP_WALL;

    // LEFT_WALL - The graphical object representing the left wall in the game.
    private static GRect LEFT_WALL;

    // RIGHT_WALL - The graphical object representing the right wall in the game.
    private static GRect RIGHT_WALL;

    // BOTTOM_WALL - The graphical object representing the bottom wall in the game.
    private static GRect BOTTOM_WALL;


    /**
     * The main game loop that runs the game.
     * Sets up the initial game elements including walls, paddle, matrix of bricks, and ball.
     * Handles ball movement, collision detection, and game over condition.
     */
    public void run() {
        // Set window size
        setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);

        // Create top wall using oneWall() method
        TOP_WALL = oneWall(0, 0, getWidth(), WALL_WIDTH);

        // Create left wall using oneWall() method
        LEFT_WALL = oneWall(0, 0, WALL_WIDTH, getHeight());

        // Create right wall using oneWall() method
        RIGHT_WALL = oneWall(getWidth() - WALL_WIDTH, 0, WALL_WIDTH, getHeight());

        // Create bottom wall using oneWall() method
        BOTTOM_WALL = oneWall(0, getHeight() - WALL_WIDTH, getWidth(), WALL_WIDTH);

        // Create PADDLE
        PADDLE = rocket();

        // Create matrix of bricks using matrix() method
        matrix();
        // Add mouse listeners for paddle control
        addMouseListeners();

        // Create ball using ballCreate() method
        ballCreate();

        // Main game loop
        while (NTURNS > 0) {
            // Move the ball
            moveBall();
            // Check for collision with walls, paddle, and bricks
            checkForCollision();
            // Pause for a short period of time
            pause(PAUSE_TIME);
            // Check if the ball reaches the bottom of the window
            if (BALL.getY() + BALL.getHeight() >= PADDLE.getY() + PADDLE_HEIGHT) {
                // Decrease amount of games remaining
                NTURNS--;
                // If there are still games remaining
                if (NTURNS > 0) {
                    // Remove the ball from the canvas
                    remove(BALL);
                    // Create a new ball
                    ballCreate();
                }
            }
        }
    }

    // private GObject selectedObject - The currently selected graphical object, initialized as null.
    private GObject selectedObject = null;

    // @Override - Indicates that the following methods are overriding methods from a superclass or an interface.
    @Override

    // This method is called when the mouse is pressed (clicked) by the user. It is responsible for updating the
    // selectedObject and isObjectSelected variables based on the mouse event.
    public void mousePressed(MouseEvent e) {

        // selectedObject is set to the graphical object located at the x and y coordinates of the mouse event.
        selectedObject = getElementAt(e.getX(), e.getY());
    }

    // This method is called when the mouse is moved by the user. It is responsible for updating the position of the
    // selectedObject based on the mouse movement.
    @Override
    public void mouseMoved(MouseEvent e) {
        // If selectedObject is not null, it means an object is currently selected by the mouse.
        if (selectedObject != null) {
            // Calculate the new x and y coordinates for the selectedObject based on the mouse movement.
            double newX = e.getX() - selectedObject.getWidth() / 2.0;
            double newY = selectedObject.getY();
            // Check if the new x coordinate is less than the wall width,
            // and if so, set it to the wall width to prevent the object from moving outside the game area.
            if (newX < WALL_WIDTH) {
                newX = WALL_WIDTH;
            }
            // Check if the new x coordinate is greater than the width of the game area minus the width of the
            // selectedObject take away the wall width, and if so, set it to the width of the game area minus the width
            // of the selectedObject take away the wall width, to prevent the object from moving outside the game area
            // on the right side.
            if (newX > getWidth() - selectedObject.getWidth() - WALL_WIDTH) {
                newX = getWidth() - selectedObject.getWidth() - WALL_WIDTH;
            }
            // Set the new x and y coordinates of the selectedObject to update its position.
            selectedObject.setLocation(newX, newY);
        }
    }

    public double oneBrickWidth() {
        // width without side walls
        double widthWithoutLateralWalls = getWidth() - (WALL_WIDTH * 2);
        // padding size
        double paddingSize = (NBRICKS_PER_ROW * BRICK_SEP) + BRICK_SEP;
        // size allotted for bricks
        double sizeAllottedForBricks = widthWithoutLateralWalls - paddingSize;
        // size of one brick
        return sizeAllottedForBricks / NBRICKS_PER_ROW;
    }

    private void brickCreate(double x, double y, double numberOfIterations) {

        GRect realBrick = new GRect(
                x,
                y,
                oneBrickWidth(),
                BRICK_HEIGHT);
        realBrick.setFilled(true);
        /*
        A colors array is created containing the color values and a resetColor array containing the threshold values.
        Then, in the for loop, iterates over the elements of the resetColor array.
         */
        Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.PINK};
        int[] resetColor = {1, 3, 5, 7, 9};
        // For each resetColor[i] value, the numberOfIterations<= resetColor[i] condition is checked,
        // where numberOfIterations is the given value in the brick function. If the condition is met, then the actions
        // inside the if block are executed.
        for (int i = 0; i < resetColor.length; i++) {
            if (numberOfIterations <= resetColor[i]) {
                realBrick.setFillColor(colors[i]);
                realBrick.setColor(colors[i]);
                break;
            }
        }
        add(realBrick);
    }

    // A function that draws the number of rows and columns specified in constants from bricks
    private void matrix() {
        // Initialize starting x and y coordinates for the bricks.
        double startX = BRICK_SEP + WALL_WIDTH;
        double startY = getHeight() * 0.1;

        // Loop through each row of bricks.
        for (int i = 0; i < NBRICK_ROWS; i++) {
            // Loop through each brick in the current row.
            for (int j = 0; j < NBRICKS_PER_ROW; j++) {
                // Create a brick at the current x and y coordinates.
                brickCreate(startX, startY, i);
                // Update the x coordinate for the next brick in the current row.
                startX += BRICK_SEP + oneBrickWidth();
            }
            // Update the y coordinate for the next row of bricks, and reset the x coordinate.
            startY += BRICK_Y_OFFSET;
            startX = WALL_WIDTH + BRICK_SEP;
        }
    }

    private GRect rocket() {

        // Create a new GRect object representing the paddle.
        GRect paddle = new GRect

                (       // Set x coordinate of the paddle based on center of the window
                        (double) (getWidth() / 2) - (PADDLE_WIDTH / 2),

                        // Set y coordinate of the paddle based on the height of the window and an offset
                        getHeight() - PADDLE_Y_OFFSET,

                        // Set the width of the paddle
                        PADDLE_WIDTH,

                        // Set the height of the paddle
                        PADDLE_HEIGHT
                );

        // Set the paddle to be filled with color
        paddle.setFilled(true);

        // Set the fill color of the paddle to black
        paddle.setFillColor(Color.BLACK);

        // Set the border color of the paddle to black
        paddle.setColor(Color.BLACK);

        // Add the paddle to the canvas
        add(paddle);

        // Return null as the function does not return any meaningful value
        return paddle;
    }

    /**
     * Creates and adds a wall to the canvas in the form of a rectangle.
     *
     * @param x      the x-coordinate of the top-left corner of the wall
     * @param width  the width of the wall
     * @param height the height of the wall
     * @return a GRect object representing the created wall
     */
    private GRect oneWall(double x, double y, double width, double height) {

        // Create a new GRect object representing the wall.
        GRect wall = new GRect(x, y, width, height);

        // Set the fill flag of the wall to true
        wall.setFilled(true);

        // Set the fill color of the wall to black
        wall.setFillColor(Color.BLACK);

        // Set the border color of the wall to black
        wall.setColor(Color.BLACK);

        // Add the wall to the canvas
        add(wall);

        // Return the GRect object representing the created wall
        return wall;
    }

    /**
     * Checks for collision between the ball and other objects on the canvas, and returns the colliding object if any.
     *
     * @return a GObject object representing the colliding object, or null if no collision occurred
     */
    private GObject getCollidingObject() {

        // Get the current x and y coordinates of the ball
        double x = BALL.getX();
        double y = BALL.getY();

        // Check for collision at the current x and y coordinates
        GObject obj = getElementAt(x, y);

        if (obj != null) {
            // Return the colliding object if found
            return obj;
        }

        // Check for collision at the x and y coordinates shifted by the diameter of the ball to the right
        obj = getElementAt(x + 2 * BALL_RADIUS, y);

        if (obj != null) {
            // Return the colliding object if found
            return obj;
        }

        // Check for collision at the x and y coordinates shifted by the diameter of the ball to the bottom-right
        obj = getElementAt(x + 2 * BALL_RADIUS, y + 2 * BALL_RADIUS);

        if (obj != null) {

            // Return the colliding object if found
            return obj;
        }

        // Check for collision at the x and y coordinates shifted by the diameter of the ball to the bottom
        obj = getElementAt(x, y + 2 * BALL_RADIUS);

        // Return the colliding object if found
        return obj;
    }

    /**
     * Creates a ball on the canvas.
     */
    private void ballCreate() {

        // Create a new GOval object representing the ball with a diameter of BALL_RADIUS * 2
        BALL = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);

        // Set the ball to be filled
        BALL.setFilled(true);

        // Add the ball to the canvas, centered horizontally and vertically
        add
                (
                        BALL,
                        (double) getWidth() / 2 - BALL.getWidth() / 2,
                        (double) getHeight() / 2 - BALL.getHeight() / 2
                );

        // Generate a random velocity for the ball using RandomGenerator
        RandomGenerator generator = RandomGenerator.getInstance();
        VX = generator.nextDouble(1, 3);

        // Randomly set the ball's x-velocity to be positive or negative
        if (generator.nextBoolean(0.5)) {
            VX = -VX;
        }
    }


    /**
     * Moves the ball on the canvas based on its current velocity and handles collisions with walls, bricks, and paddle.
     *
     * @return
     */
    private double moveBall() {

        if (selectedObject != null) {

            // Check if the ball hits the left or right wall, reverse x-velocity if it does
            // Such parameters like 1.2; 1,5; 0,5 - These are the offsets that are needed, because the program draws as
            // it wants every time. These parameters are needed so that the ball does not actually hit the walls and
            // they do not disappear.
            if (BALL.getX() <= BRICK_SEP + (WALL_WIDTH / 1.2)
                    || BALL.getX() + BALL.getWidth() >= getWidth() - WALL_WIDTH * 1.5) {
                VX = -VX;
            }

            // Check if the ball hits the top wall, reverse y-velocity if it does
            if (BALL.getY() <= WALL_WIDTH + 0.5) {
                VY = -VY;
            }

            // Check for collisions with objects on the canvas
            GObject collider = getCollidingObject();

            // If there is a collision
            if (collider != null) {

                // If the collider is not the paddle, or wall, remove it and reverse y-velocity
                if (collider != PADDLE) {
                    remove(collider);
                    AMOUNT_OF_BRICKS--;
                    VY = -VY;

                    /* The -Math.abs(VY) in the code is used to reverse the direction of the ball's vertical velocity
                     (VY) when it collides with the paddle.In the code, it's mentioned that if the collider is the
                     paddle, then the vertical velocity (VY) of the ball should be reversed with the absolute value.
                     By using -Math.abs(VY), it ensures that the direction of the velocity is reversed, but the
                     magnitude of the velocity remains unchanged. This means that if the ball is moving upwards
                     (with a negative VY value), it will continue to move upwards after the collision with the paddle,
                     but if it's moving downwards (with a positive VY value), it will continue to move downwards after
                     the collision. This is typically done to simulate a realistic bounce effect when the ball collides
                     with the paddle, where the ball retains its original speed but changes direction.
                     */
                } else {
                    // If the collider is the paddle, reverse y-velocity with the absolute value
                    VY = -Math.abs(VY);
                }
            }

            // Move the ball based on its current velocity
            BALL.move(VX, VY);

            // Condition to END THE GAME if all the bricks are gone
            if (AMOUNT_OF_BRICKS == 0) {
                NTURNS = 0;
                return NTURNS;
            }
        }
        return 0;
    }

    /**
     * Checks for collisions of the ball with objects on the canvas, and updates velocity accordingly.
     */
    private void checkForCollision() {

        // Get the collider object
        GObject collider = getCollidingObject();

        // If there is a collision
        if (collider != null) {

            // If the collider is the paddle, reverse y-velocity with the absolute value
            if (collider == PADDLE) {
                VY = -Math.abs(VY);
            }
        }
    }
}