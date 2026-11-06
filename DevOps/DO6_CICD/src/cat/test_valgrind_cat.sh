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
RESULT=0
DIFF_RES=""

# Массив тестов, полностью соответствующий test_func_cat.sh
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
    t=$(echo $@)
    valgrind --log-file="valgrind_s21_cat.log" --trace-children=yes --track-fds=yes --track-origins=yes --leak-check=full --show-leak-kinds=all ./s21_cat $t > test_s21_cat.log 2>&1
    leak=$(grep "ERROR SUMMARY" valgrind_s21_cat.log)
    (( COUNTER++ ))
    if [[ $leak == *"0 errors from 0 contexts"* ]]
    then
      (( SUCCESS++ ))
        echo -e "\033[31m$FAIL\033[0m/\033[32m$SUCCESS\033[0m/$COUNTER \033[32msuccess\033[0m cat $t"
    else
      (( FAIL++ ))
        echo -e "\033[31m$FAIL\033[0m/\033[32m$SUCCESS\033[0m/$COUNTER \033[31mfail\033[0m cat $t"
        echo "Valgrind errors found: $leak"
    fi
    rm -f test_s21_cat.log
    rm -f valgrind_s21_cat.log
}

# специфические тесты (полностью соответствуют test_func_cat.sh)
for i in "${extra[@]}"
do
    testing $i
done

# 1 параметр на test_case_cat.txt (соответствует test_func_cat.sh)
for var1 in b e n s t v E T
do
    testing "-$var1 test_case_cat.txt"
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