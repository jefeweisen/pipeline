#!/bin/bash

prog="$1"
shift
out="$1"
shift

"$prog" "$@" > "$out"
