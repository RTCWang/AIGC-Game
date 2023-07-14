var chatGPT = require("./robot/robot").chatGPT
var express = require('express');
var app = express();
 

app.post('/ask', function (req, res) {
    let ask = req.query["ask"];
 
    console.log(ask)
    chatGPT(ask, function (succ, txt) {
        if (succ) {
            res.send({ state: 0, txt });
        } else {
            res.send({ state: -1, txt: "" });
        }
    })
})

var server = app.listen(8888, function () {
    var port = server.address().port
    console.log("应用实例，访问地址为 http://%s:%s", '0.0.0.0', port)

})