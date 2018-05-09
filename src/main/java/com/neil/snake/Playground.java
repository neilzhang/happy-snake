package com.neil.snake;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author neil
 */
public class Playground {

    private int width = 40;
    private int length = 40;
    private ConcurrentHashMap<Integer, Snake> snakeMap;
    private final AtomicInteger[][] map;
    private ScheduledExecutorService executor;

    public Playground() {
        int width = this.width;
        int length = this.length;
        this.snakeMap = new ConcurrentHashMap<>();
        this.map = new AtomicInteger[width][length];
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < length; x++) {
                this.map[y][x] = new AtomicInteger(0);
            }
        }
        this.executor = Executors.newScheduledThreadPool(1);
        this.executor.scheduleAtFixedRate(new Feeder(), 0, 1, TimeUnit.SECONDS);
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public boolean isSafe(int x, int y) {
        if (x < 1 || x >= length - 1 || y < 1 || y >= width - 1) {
            return false;
        }
        return map[y][x].get() == 0 &&
                map[y - 1][x].get() == 0 &&
                map[y][x - 1].get() == 0 &&
                map[y + 1][x].get() == 0 &&
                map[y][x + 1].get() == 0;
    }

    public boolean isCrowd() {
        return false;
    }

    public int check(int x, int y) {
        if (x < 0 || x >= length || y < 0 || y >= width) {
            return -1;
        }
        return map[y][x].get();
    }

    public boolean occupy(int x, int y, int id) {
        if (x < 0 || x >= length || y < 0 || y >= width) {
            return false;
        }
        return map[y][x].compareAndSet(0, id);
    }

    public boolean replace(int x, int y, int id) {
        if (x < 0 || x >= length || y < 0 || y >= width) {
            return false;
        }
        return map[y][x].compareAndSet(1, id);
    }

    public int release(int x, int y) {
        if (x >= 0 && x < length && y >= 0 && y < width) {
            return map[y][x].getAndSet(0);
        }
        return 0;
    }

    public void printMap(String message) {
        System.out.println(message);
        StringBuilder sb = new StringBuilder();
        sb.append("#####Playground Map Start#####\n");
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < length; x++) {
                sb.append(this.map[y][x].get()).append(' ');
            }
            sb.append('\n');
        }
        sb.append("#####Playground Map End#####");
        System.out.println(sb.toString());
    }

    public void add(Snake snake) {
        snakeMap.put(snake.getId(), snake);
    }

    public void remove(Snake snake) {
        snakeMap.remove(snake.getId());
    }

    public int[][] getMap() {
        int[][] result = new int[width][length];
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < length; x++) {
                result[y][x] = map[y][x].get();
            }
        }
        return result;
    }

    public Snake getSnake(int id) {
        return snakeMap.get(id);
    }

    private class Feeder implements Runnable {

        @Override
        public void run() {
            int pW = width;
            int pL = length;
            int count = 0;
            for (int y = 0; y < pW; y++) {
                for (int x = 0; x < pL; x++) {
                    if (map[y][x].get() == 1) {
                        count++;
                    }
                }
            }
            count = snakeMap.size() * 3 - count;
            while (count > 0) {
                int birthX = new Random().nextInt(pL);
                int birthY = new Random().nextInt(pW);
                while (check(birthX, birthY) > 10000) {
                    birthX = new Random().nextInt(pL);
                    birthY = new Random().nextInt(pW);
                }
                if (occupy(birthX, birthY, 1)) {
                    count--;
                }
            }
        }

    }

}
