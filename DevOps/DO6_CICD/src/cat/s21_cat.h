#ifndef CAT_H
#define CAT_H

#include <stdio.h>
#include <string.h>

typedef struct {
  int b;
  int n;
  int s;
  int v;
  int T;
  int E;
} OPT;

int is_short_option(char *s);

int is_long_option(char *s);

void cat(char *buf, const OPT *opt, int *n);

int squeeze_blank(const char *current_line, const char *previous_line,
                  int is_first_line, int s_flag);

void number_nonblank(char *str, const char *start, int len, int *n);

void number(char *str, char *start, int len, int *n);

void v_replace(char *str);

void E_replace(char *str);

void T_replace(char *str);

#endif
