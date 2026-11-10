CI/CD на GitLab CI: сборка, стайл-гейт, интеграционные тесты и деплой C-утилит (GitLab CI/CD, GitLab Runner, bash, clang-format, scp/ssh):
- Поднял gitlab-runner на Ubuntu Server 24.04 и спроектировал пайплайн из 4 стадий (build → style → test → deploy) в .gitlab-ci.yml с сохранением артефактов сборки (src/cat/s21_cat, src/grep/s21_grep, срок хранения 30 дней).
- Сборка утилит cat/grep на C через make; стайл-гейт clang-format -n -Werror (Google style) с автоматическим «фейлом» пайплайна при нарушениях.
- Написал интеграционные тесты на bash: побайтовое сравнение вывода своих утилит с системными cat/grep через diff на десятках комбинаций флагов и кейсов (включая обработку несуществующего файла); этап запускается только после успешных build и style.
- Реализовал CD-этап (ручной триггер when: manual): deploy.sh через scp/ssh (StrictHostKeyChecking=no) доставляет собранные бинарники на вторую ВМ-«продакшн» в /usr/local/bin и выставляет chmod +x; весь pipeline завязан на тег раннера dob6.
