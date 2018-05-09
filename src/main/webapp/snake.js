$(document).ready(function () {
    animate();
});

function animate() {
    $('div>div>div').each(function (id) {
        $(this).css({
            position: 'relative',
            top: '-200px',
            opacity: 0
        });
        var wait = Math.floor((Math.random() * 2000) + 1);
        $(this).delay(wait).animate({
            top: '0px',
            opacity: 1
        }, 1000);
    });
}

var snakeId = -1;
self.setInterval("refresh()", 1000);

document.onkeydown = function (event) {
    var e = event || window.event || arguments.callee.caller.arguments[0];
    if (e && e.keyCode == 37) { //Left
        move(1);
    } else if (e && e.keyCode == 38) { //Up
        move(2);
    } else if (e && e.keyCode == 39) { //Right
        move(3);
    } else if (e && e.keyCode == 40) { //Down
        move(4);
    }
};

function move(cmd) {
    $.ajax({
        url: "/api/control.htm",
        async: false,
        dataType: "json",
        type: "POST",
        data: {
            id: snakeId,
            cmd: cmd
        }
    });
}

function play() {
    $.ajax({
        url: "/api/create.htm",
        async: false,
        dataType: "json",
        type: "POST",
        success: function (data) {
            snakeId = data.data;
        }
    });
}

function refresh() {
    $.ajax({
        url: "/api/get.htm",
        async: false,
        dataType: "json",
        type: "GET",
        success: function (data) {
            var map = data.data;
            var table = $('.content > div > div');
            for (var y = 0; y < map.length; y++) {
                for (var x = 0; x < map[y].length; x++) {
                    if (map[y][x] > 10000) {
                        var block = table[y * 40 + x];
                        $(block).css({"background-color": "#FF0000"});
                    } else if (map[y][x] == 1) {
                        var block = table[y * 40 + x];
                        $(block).css({"background-color": "#060"});
                    } else if (map[y][x] == 0) {
                        var block = table[y * 40 + x];
                        $(block).css({"background-color": "#FFF"});
                    }
                }
            }
            console.log(data);
        }
    });
}
