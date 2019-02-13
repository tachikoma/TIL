const http = require('http');

http.createServer(function (requst, response) {
    console.log(requst.connection.remoteFamily + ' ' + requst.connection.remoteAddress + ':' + requst.connection.remotePort);

    var date = new Date();
    date.setDate(date.getDate() + 7);

    response.writeHead(200, {
        'Content-Type': 'text/html',
        'Set-Cookie': [
            'breakfas = toast;Expires = ' + date.toUTCString(),
            'dinner = chicken'
        ]
    });
    // response.writeHead(404);
    // response.end();
    // response.write('<h1>Hello Web Server with Node.js</h1>' + '<br/>' + requst.headers.cookie);
    //  response.end('<h1>Hello Web Server with Node.js</h1>' + '<br/>' + requst.headers.cookie);

    setInterval(function(){
        response.writeHead(302, {
            'Location': 'http://hanb.co.kr'
        });
        response.end();
    }, 1000);
}).listen(52273, function () {
    console.log('Server Running at http://127.0.0.1:52273');
});