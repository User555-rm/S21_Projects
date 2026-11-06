#include <fcgi_stdio.h>
#include <stdlib.h>

int main() {
    while (FCGI_Accept() >= 0) {
        printf("Content-Type: text/html\r\n"
                        "\r\n"
                        "Hello World!");
    }
    return 0;
}
