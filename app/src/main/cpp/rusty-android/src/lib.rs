#![allow(non_snake_case)]

use std::ffi::CString;

use jni::{
    JNIEnv,
    objects::{JClass, JObject, JString},
};

#[unsafe(no_mangle)]
pub fn Java_com_example_rustnativecpp_MainActivity_stringFromRust<'java>(
    env: JNIEnv<'java>,
    _obj: JObject<'java>,
) -> JString<'java> {
    env.new_string("Hello From Rust")
        .expect("Failed to create java string")
}

#[unsafe(no_mangle)]
pub extern "C" fn test() -> i32 {
    42
}
