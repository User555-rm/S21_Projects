#include "s21_cat.h"

int main(int argc, char *argv[]) {
  if (argc == 1) {
    fprintf(stderr, "n/a: need more arguments\n");
    return 1;
  }

  OPT opt = {0};
  int counter = 0;

  for (int i = 1; i < argc; i++) {
    if (is_short_option(argv[i]) || is_long_option(argv[i])) {
      char *p = argv[i];
      if (is_long_option(argv[i])) {
        if (!strcmp("--number-nonblank", p)) opt.b = 1;
        if (!strcmp("--number", p)) opt.n = 1;
        if (!strcmp("--squeeze-blank", p)) opt.s = 1;
      }
      if (is_short_option(argv[i])) {
        for (int j = 1; p[j] != '\0'; j++) {
          if (p[j] == 'b') opt.b = 1;
          if (p[j] == 'n') opt.n = 1;
          if (p[j] == 's') opt.s = 1;
          if (p[j] == 'v') opt.v = 1;
          if (p[j] == 'E') opt.E = 1;
          if (p[j] == 'T') opt.T = 1;
          if (p[j] == 'e') {
            opt.E = 1;
            opt.v = 1;
          }
          if (p[j] == 't') {
            opt.T = 1;
            opt.v = 1;
          }
        }
      }
      if (!opt.b && !opt.n && !opt.s && !opt.v && !opt.E && !opt.T) {
        fprintf(stderr, "n/a: wrong option\n");
        return 1;
      }
    }
  }
  if (opt.b && opt.n) opt.n = 0;

  int line_counter = 1;
  for (int i = 1; i < argc; i++) {
    if (!is_short_option(argv[i]) && !is_long_option(argv[i])) {
      counter++;
      FILE *fp = fopen(argv[i], "r");
      if (fp == NULL) {
        fprintf(stderr, "n/a: %s no such file or directory\n", argv[i]);
        return 1;
      }
      char buf[8000] = {0};
      char ch;
      int k = 0;
      while ((ch = fgetc(fp)) != EOF) buf[k++] = ch;
      fclose(fp);

      cat(buf, &opt, &line_counter);
    }
  }

  if (counter < 1) {
    fprintf(stderr, "n/a: need more arguments\n");
  }
  return 0;
}

int is_short_option(char *s) {
  if (*s == '-' && strlen(s) > 1 && *(s + 1) != '-') return 1;
  return 0;
}

int is_long_option(char *s) {
  if (*s == '-' && strlen(s) > 1 && *(s + 1) == '-') return 1;
  return 0;
}

void cat(char *buf, const OPT *opt, int *n) {
  char *start = buf;
  char str[2000] = {0};
  char prev_raw[2000] = {0};
  int first_line = 1;

  while (*start != '\0') {
    char *end = strchr(start, '\n');
    if (end == NULL) end = strchr(start, '\0');
    int len = end - start;

    char raw_current[2000] = {0};
    strncpy(raw_current, start, len);
    raw_current[len] = *end;

    if (squeeze_blank(raw_current, prev_raw, first_line, opt->s)) {
      start = end + 1;
      continue;
    }

    memset(str, 0, sizeof(str));
    strncpy(str, start, len);
    str[len] = *end;

    if (opt->b) number_nonblank(str, start, len, n);
    if (opt->n) number(str, start, len, n);
    if (opt->E) E_replace(str);
    if (opt->T) T_replace(str);
    if (opt->v) v_replace(str);

    printf("%s", str);
    strcpy(prev_raw, raw_current);
    first_line = 0;
    start = end + 1;
  }
}

int squeeze_blank(const char *current_line, const char *previous_line,
                  int is_first_line, int s_flag) {
  if (s_flag && !is_first_line) {
    if (strcmp(previous_line, "\n") == 0 && strcmp(current_line, "\n") == 0) {
      return 1;
    }
  }
  return 0;
}

void number_nonblank(char *str, const char *start, int len, int *n) {
  if (strcmp(str, "\n")) {
    sprintf(str, "%6d\t%.*s", *n, len + 1, start);
    *n += 1;
  }
}

void number(char *str, char *start, int len, int *n) {
  sprintf(str, "%6d\t%.*s", *n, len + 1, start);
  *n += 1;
}

void v_replace(char *str) {
  for (int i = 0; str[i] != '\n' && str[i] != '\0'; i++) {
    if ((str[i] > 0 && str[i] < 9) || (str[i] > 10 && str[i] < 32)) {
      int tmp = str[i];
      for (int j = 2000; j > i + 1; j--) str[j] = str[j - 1];
      str[i] = '^';
      str[i + 1] = tmp + 64;
    }
  }
}

void E_replace(char *str) {
  char *ch;
  for (int i = 0; str[i] != '\n' && str[i] != '\0'; i++) {
    if (str[i] == '\r') {
      for (int j = 2000; j >= i; j--) str[j + 1] = str[j];
      str[i] = '^';
      str[i + 1] = 'M';
      i++;
    }
  }
  if (strchr(str, '\n') != NULL) {
    ch = strchr(str, '\n');
    *ch = '$';
    *(ch + 1) = '\n';
  }
}

void T_replace(char *str) {
  for (int i = 0; str[i] != '\n' && str[i] != '\0'; i++) {
    if (str[i] == '\t') {
      for (int j = 2000; j >= i; j--) str[j + 1] = str[j];
      str[i] = '^';
      str[i + 1] = 'I';
    }
  }
}