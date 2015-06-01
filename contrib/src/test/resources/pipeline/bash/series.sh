#!/bin/bash

max=$1

i=0
while [  $i -lt $max ]; do
    echo $i
    let i=i+1 
done
