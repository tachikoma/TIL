var url = 'http://sbs777.net/'
var savepath = 'test-rhino.html'

var aUrl = new java.net.URL(url)
var conn = aUrl.openConnection()
var ins = conn.getInputStream()
var file = new java.io.File(savepath)
var out = new java.io.FileOutputStream(file)

var b
while ((b = ins.read()) != -1) {
    out.write(b)
}
out.close()
ins.close()