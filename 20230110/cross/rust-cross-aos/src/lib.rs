#[cfg(target_os = "android")]
extern crate android_logger;
extern crate jni;
#[cfg(target_os = "android")]
#[macro_use]
extern crate log;

#[cfg(target_os = "android")]
use log::LogLevel;
use jni::JNIEnv;
use jni::objects::{JClass};
use jni::sys::{jint, jlong};

#[no_mangle]
#[allow(non_snake_case)]
pub extern "system" fn JNI_OnLoad() -> jint {
    #[cfg(target_os = "android")]
    {
        android_logger::init_once(LogLevel::Debug);
        info!("Native library loaded");
    }

    jni::sys::JNI_VERSION_1_6
}

#[no_mangle]
#[allow(non_snake_case)]
pub extern "system" fn Java_kr_ds_rust_Cross_add(
    _env: JNIEnv,
    _class: JClass,
    left: jlong, right: jlong
) -> jlong {
    let result = rust_cross::add(left, right);
    result as jlong
}

#[no_mangle]
#[allow(non_snake_case)]
pub extern "system" fn Java_kr_ds_rust_Cross_sub(
    _env: JNIEnv,
    _class: JClass,
    a: jlong, b: jlong
) -> jlong {
    rust_cross::sub(a, b) as jlong
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
