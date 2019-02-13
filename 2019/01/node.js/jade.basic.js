const http = require('http');
const fs = require('fs');
const jade = require('jade');

http.createServer(function (request, response) {
    fs.readFile('jadePage.jade', 'utf8', function (error, data) {
        response.writeHead(200, {
            'Content-Type': 'text/html'
        });

        const fn = jade.compile(data);
        response.end(fn({
            name: "DjY",
            description: "Hello jade with Node.js .. !"
        }));
    });
}).listen(52273, function () {
    console.log('Server Running at http://127.0.0.1:52273');
});