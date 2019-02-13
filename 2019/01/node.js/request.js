const http = require('http');
const fs = require('fs');
const url = require('url');

http.createServer(function (request, response) {
    var pathname = url.parse(request.url).pathname;
    console.log(request.method + " " + request.connection.remoteFamily + ' ' + request.connection.remoteAddress + ':' + request.connection.remotePort + pathname);

    var query = url.parse(request.url, true).query;
    response.writeHead(200, {
        'Content-Type': 'text/html'
    });
    response.end('<h1>' + JSON.stringify(query) + '</h1>');
    
}).listen(52273, function () {
    console.log('Server Running at http://127.0.0.1:52273');
});