const http = require('http')
const express = require('express');
const fs = require('fs')
const path = require('path')
const morgan = require('morgan')

const app = express();

var accessLogStream = fs.createWriteStream(path.join(__dirname, 'access.log'), { flags: 'a' })

app.use(morgan('short', { stream: accessLogStream }))
app.use(express.static(__dirname + '/public'))
app.use(express.Router())

app.use( (request, response, next) => {
    request.number = 52
    response.number = 273
    next();
})

app.get('/index', function (request, response) {
    response.send('<h1>Index Page</h1>')
})

app.get('/page/:id', (request, response) => {
    const name = request.params.id
    response.send('<h1>' + name + ' Page</h1>')
})

app.get('/a', (request, response) => {
    response.send('<a href="/b">Go to B</a>')
})

app.get('/b', (request, response) => {
    response.send('<a href="/a">Go to A</a>')
})

app.all('*', (request, response) => {
    response.send(404,'<h1>ERROR - Page Not Found</h1>')
})

http.createServer(app).listen(52273, () => {
    console.log('Server running at http://127.0.0.1/52273')
})
