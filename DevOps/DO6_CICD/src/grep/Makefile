all: s21_grep

s21_grep: s21_grep.c s21_grep.h
	gcc -std=c11 -Wall -Werror -Wextra s21_grep.c -o s21_grep

rebuild: clean all

clean:
	rm -f s21_grep s21_grep.exe

clang:
	clang-format --style=Google -n *.c *.h
	clang-format --style=Google -i *.c *.h

test:
	make clean
	make s21_grep
	make clang

int_test: all
	bash test_func_grep.sh

valgrind_test: all
	bash test_valgrind_grep.sh
