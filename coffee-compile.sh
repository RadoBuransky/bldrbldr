#!/bin/bash

if [ ! -d ./target/coffee ]; then
    mkdir -p ./target/coffee
fi

echo "Compiling coffee script files..."
/home/dev/bin/node-v0.10.12-linux-x64/lib/node_modules/coffee-script/bin/coffee --output ./public/javascripts --compile ./app/assets/javascripts

echo "Done..."
