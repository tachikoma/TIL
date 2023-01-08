#!/bin/zsh
cargo new $1;mv $2 $1/src/main.rs;cd $1;cargo run;cd ..
