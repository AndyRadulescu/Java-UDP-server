package useability;

/**
 * Created by AndyRadulescu 5/22/2017
 */
public interface Sizes {
    int canvasSizeX = 900;
    int canvasSizeY = 600;
    int rect1X = 0;// position on x coordinates for rect1
    int startX = canvasSizeY / 2 - 50; // position on y coordinates for rect1
    // and
    // rect2
    int rect2X = canvasSizeX - 20; // position on x coordinates for rect2
    int rectXsize = 20; // width of the rackets
    int rectYsize = 100;// height of the rackets
    double increaseSpeed = 0.8; // speed increase after every hit
    double maxBallSpeed = 10; // the maximum number of pixels that the ball can
    // use for a movement
    int edge = 100;
}