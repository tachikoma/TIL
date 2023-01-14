#!/bin/sh

echo "ANDROID_NDK_HOME=$ANDROID_NDK_HOME"
if [ -n "$ANDROID_NDK_HOME" ]; then
	build() {
		if [ -n "$2" ]; then
		cargo ndk --target $1 -p $2 build --release
		else
		cargo ndk --target $1 build --release
		fi
	}

	build armv7-linux-androideabi $1
	build aarch64-linux-android $1
	build x86_64-linux-android $1
	build i686-linux-android $1
else
	echo "need ANDROID_NDK_HOME"
fi
