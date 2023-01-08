fn transpose(matrix: [[i32; 3]; 3]) -> [[i32; 3]; 3] {
    let mut r: [[i32; 3]; 3] = [[0; 3]; 3];
    for x in 0..3 {
        for y in 0..3 {
            r[y][x] = matrix[x][y]
        }
    }
    r
}

fn pretty_print(matrix: &[[i32; 3]; 3]) {
    for x in 0..3 {
        print!("[");
        for y in 0..3 {
            if y != 0 {
                print!(" ")
            }
            print!("{}", matrix[x][y])
        }
        println!("]")
    }
}

fn main() {
    let array = [10, 20, 30];
    print!("Iterating over array:");
    for n in array {
        print!(" {n}");
    }
    println!();

    print!("Iterating over range:");
    for i in 0..3 {
        print!(" {}", array[i]);
    }
    println!();

    let matrix = [
        [101, 102, 103], // <-- the comment makes rustfmt add a newline
        [201, 202, 203],
        [301, 302, 303],
    ];

    println!("matrix:");
    pretty_print(&matrix);

    let transposed = transpose(matrix);
    println!("transposed:");
    pretty_print(&transposed);
}
