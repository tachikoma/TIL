// 모듈을 추출합니다.
const fs = require('fs');

// 모듈을 사용합니다.
console.time("read");
try {
    const read = fs.readFileSync('./textfile.txt');
    console.log(read);
    fs.readFile('textfile.txt', 'utf-8', (error, data) => {
        console.timeEnd('read');
        if (error) {
            console.log(error);
            throw error;
        }
        console.log(data);
        console.log("loaded");
    });
} catch (error) {
    console.log(error);
}
console.log("source end");