all: s21_cat

s21_cat: s21_cat.c s21_cat.h
	gcc -std=c11 -Wall -Werror -Wextra s21_cat.c -o s21_cat

rebuild: clean all

clean:
	rm -f s21_cat s21_cat.exe

clang:
	clang-format --style=Google -n *.c *.h
	clang-format --style=Google -i *.c *.h

test:
	make clean
	make s21_cat
	make clang

int_test: all
	bash test_func_cat.sh

valgrind_test: all
	bash test_valgrind_cat.sh
