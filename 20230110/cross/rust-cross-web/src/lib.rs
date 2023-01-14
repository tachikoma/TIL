use wasm_bindgen::prelude::*;

#[wasm_bindgen]
pub fn add(left: i64, right: i64) -> i64 {
    rust_cross::add(left, right)
}

#[wasm_bindgen]
pub fn sub(a: i64, b: i64) -> i64 {
    rust_cross::sub(a, b)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_works() {
        let result = add(2, 2);
        assert_eq!(result, 4);
    }
}
