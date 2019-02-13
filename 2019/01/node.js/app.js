require('http').createServer(function(request, response){
    if('/' == request.url) {
        response.write('<!DOCTYPE html>');
        response.write('<html>');
        response.write('<head>');
        response.write('    <title>Forever</title>');
        response.write('</head>');
        response.write('<body>');
        response.write('    <h1>Forever 3</h1>');
        response.write('</body>');
        response.write('</html>');
        response.end();
    } else {
        error.error.error();
    }
}).listen(52273, function() {
    console.log("Sever Running at http://127.0.0.1:52273");
});