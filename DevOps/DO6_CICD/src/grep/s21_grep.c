#include "s21_grep.h"

int main(int argc, char *argv[]) {
  if (argc < 2) {
    printf("n/a: Wrong arguments\n");
    return 2;
  }

  Config config = {0};
  if (parse_arguments(argc, argv, &config) != 0) {
    printf("n/a: Wrong arguments\n");
    return 2;
  }

  if (config.file_start_index >= argc) {
    printf("n/a: Wrong arguments\n");
    return 2;
  }

  regex_t regex;
  if (compile_regex(config.pattern, &regex, config.options.i) != 0) {
    printf("n/a: regex error\n");
    return 2;
  }

  int found_in_any = 0;
  int error_occurred = 0;
  int multiple_files = (argc - config.file_start_index) > 1;

  for (int i = config.file_start_index; i < argc; i++) {
    process_file(argv[i], &config, &regex, &found_in_any, &error_occurred,
                 multiple_files);
  }

  regfree(&regex);

  if (error_occurred) {
    return 2;
  }
  return found_in_any ? 0 : 1;
}

int parse_arguments(int argc, char *argv[], Config *config) {
  int c;
  config->pattern_set = 0;
  config->pattern[0] = '\0';
  int pattern_count = 0;

  while ((c = getopt(argc, argv, "e:ivcln")) != -1) {
    switch (c) {
      case 'e':
        if (pattern_count > 0) {
          if (strlen(config->pattern) + 1 >= MAX_PATTERN_LEN) return -1;
          strcat(config->pattern, "|");
        }
        if (strlen(config->pattern) + strlen(optarg) >= MAX_PATTERN_LEN)
          return -1;
        strcat(config->pattern, optarg);
        pattern_count++;
        config->pattern_set = 1;
        break;
      case 'i':
        config->options.i = 1;
        break;
      case 'v':
        config->options.v = 1;
        break;
      case 'c':
        config->options.c = 1;
        break;
      case 'l':
        config->options.l = 1;
        break;
      case 'n':
        config->options.n = 1;
        break;
      case '?':
        return -1;
    }
  }

  if (!config->pattern_set) {
    if (optind < argc) {
      if (strlen(argv[optind]) >= MAX_PATTERN_LEN) return -1;
      strcpy(config->pattern, argv[optind]);
      config->pattern_set = 1;
      optind++;
    } else {
      return -1;
    }
  }

  config->file_start_index = optind;
  return 0;
}

int compile_regex(const char *pattern, regex_t *regex, int ignore_case) {
  int flags = REG_EXTENDED | (ignore_case ? REG_ICASE : 0);
  return regcomp(regex, pattern, flags);
}

void process_line(const char *line, int line_num, const Config *config,
                  regex_t *regex, const char *filename, int multiple_files,
                  int *count, int *file_match) {
  char line_copy[MAX_LINE_LEN];
  strncpy(line_copy, line, MAX_LINE_LEN - 1);
  line_copy[MAX_LINE_LEN - 1] = '\0';

  size_t len = strlen(line_copy);
  if (len > 0 && line_copy[len - 1] == '\n') {
    line_copy[len - 1] = '\0';
  }

  int match = (regexec(regex, line_copy, 0, NULL, 0) == 0);
  if (config->options.v) match = !match;

  if (match) {
    (*count)++;

    if (config->options.l) {
      *file_match = 1;
    }

    if (!config->options.c && !config->options.l) {
      if (multiple_files) {
        if (config->options.n) {
          printf("%s:%d:%s\n", filename, line_num, line_copy);
        } else {
          printf("%s:%s\n", filename, line_copy);
        }
      } else {
        if (config->options.n) {
          printf("%d:%s\n", line_num, line_copy);
        } else {
          printf("%s\n", line_copy);
        }
      }
    }
  }
}

void process_file(const char *filename, const Config *config, regex_t *regex,
                  int *found_in_any, int *error_occurred, int multiple_files) {
  char filename_buf[MAX_FILENAME_LEN];
  strncpy(filename_buf, filename, MAX_FILENAME_LEN - 1);
  filename_buf[MAX_FILENAME_LEN - 1] = '\0';

  FILE *fp = fopen(filename_buf, "r");
  if (!fp) {
    *error_occurred = 1;
    return;
  }

  char line_buf[MAX_LINE_LEN];
  int line_num = 0;
  int count = 0;
  int file_match = 0;

  while (fgets(line_buf, MAX_LINE_LEN, fp)) {
    line_num++;
    process_line(line_buf, line_num, config, regex, filename_buf,
                 multiple_files, &count, &file_match);

    if (config->options.l && file_match) {
      break;
    }
  }

  if (config->options.l && file_match) {
    printf("%s\n", filename_buf);
    *found_in_any = 1;
  } else if (config->options.c) {
    if (multiple_files) {
      printf("%s:%d\n", filename_buf, count);
    } else {
      printf("%d\n", count);
    }
    if (count > 0) *found_in_any = 1;
  } else if (count > 0) {
    *found_in_any = 1;
  }

  fclose(fp);
}