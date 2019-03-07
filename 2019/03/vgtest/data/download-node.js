
const url = "https://jpub.tistory.com/"
const savepath = "test.html"

const https = require('https')
const fs = require('fs')

var outfile = fs.createWriteStream(savepath)

https.get(url, function(res) {
    res.pipe(outfile)
    res.on('end', function() {
        outfile.close()
        console.log("ok")
    })
})