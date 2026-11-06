#!/bin/bash

# Определяем директорию, где находится скрипт
SCRIPT_DIR="$(dirname "$0")"
cd "$SCRIPT_DIR" || exit 1

if [ ! -f "./s21_cat" ]; then
    echo "Error: s21_cat not found! Run 'make' first."
    exit 1
fi

SUCCESS=0
FAIL=0
COUNTER=0
DIFF_RES=""

declare -a extra=(
"-s test_1_cat.txt"
"-t test_3_cat.txt"
"-n test_2_cat.txt"
"no_file.txt"
"-n -b test_1_cat.txt"
"-s -n -e test_4_cat.txt"
"test_1_cat.txt -n"
"-n test_1_cat.txt"
"-v test_5_cat.txt"
"-T test_3_cat.txt"
"-E test_1_cat.txt"
"--number-nonblank test_1_cat.txt"
"--number test_1_cat.txt"
"--squeeze-blank test_1_cat.txt"
)

testing()
{
    ./s21_cat $@ > test_s21_cat.log
    cat $@ > test_sys_cat.log
    DIFF_RES="$(diff -s test_s21_cat.log test_sys_cat.log)"
    (( COUNTER++ ))
    if [ "$DIFF_RES" == "Files test_s21_cat.log and test_sys_cat.log are identical" ]
    then
      (( SUCCESS++ ))
        echo "$SUCCESS/$COUNTER success cat $@"
    else
      (( FAIL++ ))
        echo "$FAIL/$COUNTER fail cat $@"
    fi
    rm test_s21_cat.log test_sys_cat.log
}

# специфические тесты
for i in "${extra[@]}"
do
    testing $i
done

# 1 параметр на test_case_cat.txt
for var1 in b e n s t v E T
do
    testing "-$var1 test_case_cat.txt"
done

echo "$SUCCESS/$COUNTER tests SUCCESS"
