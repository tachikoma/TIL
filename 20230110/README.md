[Rust 크로스 플랫폼 프로그래밍 (2022)](https://www.slideshare.net/utilforever/2022-rust) 을 참고하여

Rust iOS, Web, AOS 라이브러리 바인딩 위한 빌드를 테스트 해 봄

## AOS 바인딩은 NDK를 써야하는데 여기저기서 찾아서 [Android Rust JNI tests](https://github.com/supercurio/android-rust-jni-tests) 를 찾아서 설정해 보았지만 빌드가 안되었다.
NDK toolchain 설정이 필요하고

NDK 로 링크 실패가 발생함 

-lgcc 옵션 때문에 

여기 저기 찾아 보다가 며칠 걸려서

[Rust bindings to the Android NDK](https://github.com/rust-mobile/ndk) 를 찾아서 해결함(잘 되는게 맞는 거겠지?)

최종본 [build-android-libs.sh](cross/rust-cross-aos/build-android-libs.sh)

