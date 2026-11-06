#ifndef GREP_H
#define GREP_H

#include <getopt.h>
#include <regex.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

#define MAX_PATTERN_LEN 4000
#define MAX_FILENAME_LEN 300
#define MAX_LINE_LEN 4000

typedef struct {
  int i;
  int v;
  int c;
  int l;
  int n;
} OPT;

typedef struct {
  OPT options;
  char pattern[MAX_PATTERN_LEN];
  int pattern_set;
  int file_start_index;
} Config;

int parse_arguments(int argc, char *argv[], Config *config);

int compile_regex(const char *pattern, regex_t *regex, int ignore_case);

void process_line(const char *line, int line_num, const Config *config,
                  regex_t *regex, const char *filename, int multiple_files,
                  int *count, int *file_match);

void process_file(const char *filename, const Config *config, regex_t *regex,
                  int *found_in_any, int *error_occurred, int multiple_files);

#endif