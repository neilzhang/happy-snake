package snake;

/**
 * @author neil
 */
public class Player {

    private Snake snake;

    public void adopt(Snake snake) {
        this.snake = snake;
    }

    public void direct(int cmd) {
        snake.receive(cmd);
    }
}
