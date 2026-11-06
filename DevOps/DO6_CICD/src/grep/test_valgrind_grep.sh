#!/bin/bash

# Определяем директорию, где находится скрипт
SCRIPT_DIR="$(dirname "$0")"
cd "$SCRIPT_DIR" || exit 1

if [ ! -f "./s21_grep" ]; then
    echo "Error: s21_grep not found! Run 'make' first."
    exit 1
fi

SUCCESS=0
FAIL=0
COUNTER=0
RESULT=0
DIFF_RES=""

declare -a tests=(
"s test_1_grep.txt VAR"
"for s21_grep.c s21_grep.h Makefile VAR"
"for s21_grep.c VAR"
"-e for -e ^int s21_grep.c s21_grep.h Makefile VAR"
"-e for -e ^int s21_grep.c VAR"
)

declare -a extra=(
"s test_1_grep.txt -n"
"for s21_grep.c s21_grep.h Makefile -n"
"for s21_grep.c -n"
"s test_1_grep.txt -v"
"for s21_grep.c s21_grep.h Makefile -v"
"for s21_grep.c -v"
"-n for test_1_grep.txt test_2_grep.txt"
"-n for test_1_grep.txt"
"-e ^int test_1_grep.txt"
"-echar test_1_grep.txt test_2_grep.txt"
"-l for test_1_grep.txt test_2_grep.txt"
"-e = -e out test_2_grep.txt"
"-l for no_file.txt test_2_grep.txt"
"-i hello test_1_grep.txt"
"-v test test_2_grep.txt"
)

testing()
{
    t=$(echo $@ | sed "s/VAR/$var/")
    valgrind --log-file="valgrind_s21_grep.log" --trace-children=yes --track-fds=yes --track-origins=yes --leak-check=full --show-leak-kinds=all ./s21_grep $t > test_s21_grep.log 2>&1
    leak=$(grep "ERROR SUMMARY" valgrind_s21_grep.log)
    (( COUNTER++ ))
    if [[ $leak == *"0 errors from 0 contexts"* ]]
    then
      (( SUCCESS++ ))
        echo -e "\033[31m$FAIL\033[0m/\033[32m$SUCCESS\033[0m/$COUNTER \033[32msuccess\033[0m grep $t"
    else
      (( FAIL++ ))
        echo -e "\033[31m$FAIL\033[0m/\033[32m$SUCCESS\033[0m/$COUNTER \033[31mfail\033[0m grep $t"
        echo "Valgrind errors found: $leak"
    fi
    rm -f test_s21_grep.log
    rm -f valgrind_s21_grep.log
}

# специфические тесты (полностью соответствуют test_func_grep.sh)
for i in "${extra[@]}"
do
    var="-"
    testing $i
done

# 1 параметр (полностью соответствует test_func_grep.sh)
for var1 in i v c l n
do
    for i in "${tests[@]}"
    do
        var="-$var1"
        testing $i
    done
done

echo -e "\033[31mFAIL: $FAIL\033[0m"
echo -e "\033[32mSUCCESS: $SUCCESS\033[0m"
echo "ALL: $COUNTER"

if [ $FAIL -eq 0 ]; then
    echo -e "\033[32mAll Valgrind tests passed successfully!\033[0m"
    exit 0
else
    echo -e "\033[31mSome Valgrind tests failed!\033[0m"
    exit 1
fi