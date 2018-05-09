package com.neil.snake;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author neil
 */
public class Snake {

    private static AtomicInteger idGenerator = new AtomicInteger(10001);

    private int id;
    private int headX = 0;
    private int headY = 0;
    private int tailX = 0;
    private int tailY = 0;
    private int bodyLength = 0;
    private int cmd = 1; // 1往前 2 往左 3 往右
    private boolean alive = true;
    private ScheduledExecutorService executor;
    private Playground playground;

    public Snake() {
        id = idGenerator.getAndIncrement();
        executor = Executors.newScheduledThreadPool(1);
    }

    public void receive(int cmd) {
        byte[] dir = getDirection();
        byte dirX = dir[0];
        byte dirY = dir[1];
        switch (cmd) {
            case 1://Left
                if (dirX == 0 && dirY == -1) {
                    this.cmd = 2;
                } else if (dirX == -1 && dirY == 0) {
                    this.cmd = 1;
                } else if (dirX == 0 && dirY == 1) {
                    this.cmd = 3;
                } else if (dirX == 1 && dirY == 0) {

                }
                break;
            case 2://Up
                if (dirX == 0 && dirY == -1) {
                    this.cmd = 1;
                } else if (dirX == -1 && dirY == 0) {
                    this.cmd = 3;
                } else if (dirX == 0 && dirY == 1) {

                } else if (dirX == 1 && dirY == 0) {
                    this.cmd = 2;
                }
                break;
            case 3://Right
                if (dirX == 0 && dirY == -1) {
                    this.cmd = 3;
                } else if (dirX == -1 && dirY == 0) {

                } else if (dirX == 0 && dirY == 1) {
                    this.cmd = 2;
                } else if (dirX == 1 && dirY == 0) {
                    this.cmd = 1;
                }
                break;
            case 4://Down
                if (dirX == 0 && dirY == -1) {

                } else if (dirX == -1 && dirY == 0) {
                    this.cmd = 2;
                } else if (dirX == 0 && dirY == 1) {
                    this.cmd = 1;
                } else if (dirX == 1 && dirY == 0) {
                    this.cmd = 3;
                }
                break;
        }
    }

    public boolean join(Playground playground) {
        if (playground.isCrowd()) {
            return false;
        }
        int pL = playground.getLength();
        int pW = playground.getWidth();
        int birthX = new Random().nextInt(pL);
        int birthY = new Random().nextInt(pW);
        while (!playground.isSafe(birthX, birthY)) {
            birthX = new Random().nextInt(pL);
            birthY = new Random().nextInt(pW);
        }
        this.headX = birthX;
        this.headY = birthY;
        this.playground = playground;
        int dir = new Random().nextInt(4);
        switch (dir) {
            case 0:
                this.tailX = birthX;
                this.tailY = birthY - 1;
                break;
            case 1:
                this.tailX = birthX - 1;
                this.tailY = birthY;
                break;
            case 2:
                this.tailX = birthX;
                this.tailY = birthY + 1;
                break;
            case 3:
                this.tailX = birthX + 1;
                this.tailY = birthY;
                break;
        }
        boolean success = playground.occupy(headX, headY, id) &&
                playground.occupy(tailX, tailY, id);
        if (success) {
            playground.printMap("join stat:");
            playground.add(this);
            running();
        }
        return success;
    }

    public int getId() {
        return id;
    }

    private void running() {
        executor.scheduleAtFixedRate(new Heartbeat(), 1, 1, TimeUnit.SECONDS);
    }

    private void destroy() {
        while (true) {
            playground.release(tailX, tailY);
            int[] next = next(tailX, tailY);
            tailX = next[0];
            tailY = next[1];
            if (tailX == -1 && tailY == -1) {
                break;
            }
        }
        playground.printMap("destroy stat:");
        playground.remove(this);
        executor.shutdown();
    }

    private int pollCommand() {
        int cmd = this.cmd;
        this.cmd = 1;
        return cmd;
    }

    private byte[] getDirection() {
        int[] next = next(headX, headY);
        return new byte[]{(byte) (headX - next[0]), (byte) (headY - next[1])};
    }

    private void move(byte dirX, byte dirY) throws CollidedException {
        if (!(dirX == 0 && dirY == -1) &&
                !(dirX == -1 && dirY == 0) &&
                !(dirX == 0 && dirY == 1) &&
                !(dirX == 1 && dirY == 0)) {
            return;
        }
        headX += dirX;
        headY += dirY;
        int pL = playground.getLength();
        int pW = playground.getWidth();
        if (headX < 0 || headX >= pL || headY < 0 || headY >= pW) {
            throw new CollidedException("[" + id + "] snake run out of playground.");
        }
        if (playground.occupy(headX, headY, id)) {
            playground.release(tailX, tailY);
            int[] next = next(tailX, tailY);
            tailX = next[0];
            tailY = next[1];
        } else if (playground.replace(headX, headY, id)) {
            bodyLength++;
        } else {
            throw new CollidedException("[" + id + "] snake collided.");
        }
    }

    private int[] next(int x, int y) {
        int[] next = new int[2];
        next[0] = -1;
        next[1] = -1;
        if (playground.check(x, y - 1) == id) {
            next[0] = x;
            next[1] = y - 1;
        } else if (playground.check(x - 1, y) == id) {
            next[0] = x - 1;
            next[1] = y;
        } else if (playground.check(x, y + 1) == id) {
            next[0] = x;
            next[1] = y + 1;
        } else if (playground.check(x + 1, y) == id) {
            next[0] = x + 1;
            next[1] = y;
        }
        return next;
    }

    private class Heartbeat implements Runnable {

        @Override
        public void run() {
            if (alive) {
                try {
                    int cmd = pollCommand();
                    byte[] dir = getDirection();
                    byte dirX = dir[0];
                    byte dirY = dir[1];
                    switch (cmd) {
                        case 1: //往前
                            move(dirX, dirY);
                            break;
                        case 2: //往左
                            if (dirX == 0 && dirY == -1) {
                                move((byte) -1, (byte) 0);
                            } else if (dirX == -1 && dirY == 0) {
                                move((byte) 0, (byte) 1);
                            } else if (dirX == 0 && dirY == 1) {
                                move((byte) 1, (byte) 0);
                            } else if (dirX == 1 && dirY == 0) {
                                move((byte) 0, (byte) -1);
                            }
                            break;
                        case 3: //往右
                            if (dirX == 0 && dirY == -1) {
                                move((byte) 1, (byte) 0);
                            } else if (dirX == -1 && dirY == 0) {
                                move((byte) 0, (byte) -1);
                            } else if (dirX == 0 && dirY == 1) {
                                move((byte) -1, (byte) 0);
                            } else if (dirX == 1 && dirY == 0) {
                                move((byte) 0, (byte) 1);
                            }
                            break;
                    }
                    playground.printMap("move stat:");
                } catch (CollidedException e) {
                    alive = false;
                    destroy();
                    System.out.println(e.getMessage());
                }
            }
        }

    }
}
