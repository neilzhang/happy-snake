package snake;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author neil
 */
public class Test {

    public static void main(String[] args) {
        try {
            new Test().work();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //1.创建地图，会随机刷新出食物
        //2.创建一条蛇
        //3.蛇加入地图
        //4.蛇在地图中行走
    }

    private void work() throws IOException {
        Playground playground = new Playground();
        Snake snake1 = new Snake();
        Player player1 = new Player();
        player1.adopt(snake1);
        if (snake1.join(playground)) {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                if ("exit".equals(str)) {
                    break;
                } else {
                    try {
                        int cmd = Integer.valueOf(str);
                        player1.direct(cmd);
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
    }

}
