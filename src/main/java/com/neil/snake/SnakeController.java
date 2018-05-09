package com.neil.snake;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * @author neil
 */
@Controller
@RequestMapping(value = "/api")
public class SnakeController {

    private Playground playground = new Playground();

    @ResponseBody
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Response<Object> getMap() throws IOException {
        Response<Object> response = new Response<>();
        response.setData(playground.getMap());
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Response<Object> create() {
        Response<Object> response = new Response<>();
        Snake snake = new Snake();
        Player player = new Player();
        player.adopt(snake);
        if (snake.join(playground)) {
            response.setData(snake.getId());
            return response;
        } else {
            response.setCode(500);
            response.setMessage("create failed.");
            return response;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/control", method = RequestMethod.POST)
    public Response<Object> control(@RequestParam(value = "id", defaultValue = "-1") int id,
                                    @RequestParam(value = "cmd", defaultValue = "1") int cmd) {
        Snake snake = playground.getSnake(id);
        if (snake != null) {
            snake.receive(cmd);
        }
        return new Response<>();
    }

}
