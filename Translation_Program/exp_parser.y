%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "exp_parser.tab.h"
extern void yyerror(const char *s);
extern FILE *yyin;
extern int yylex();
extern int yyparse();

void add_variable(char *variable_name, int value);
int get_variable_value(char *variable_name);
FILE *outputFile;
int result;
%}

%union {
    int int_val;
    char *string_val;
}

%token <int_val> INTEGER
%token <string_val> VARIABLE
%token ASSIGN PLUS MINUS MULTIPLY DIVIDE MODULO
%token EOL

%start program
%type <string_val> assignment_expression
%type <int_val> expression term factor

%%

program:
    assignment_expression EOL
    assignment_expression EOL
    assignment_expression EOL
    expression {result = $7}
    ;

assignment_expression: VARIABLE ASSIGN INTEGER {add_variable($1, $3);}
                    ;

expression: term { $$ = $1; }
        | expression PLUS term { $$ = $1 + $3; }
        | expression MINUS term { $$ = $1 - $3; }
        | expression ASSIGN {yyerror("syntax error")}
        ;

term: factor { $$ = $1; }
        | term MULTIPLY factor { $$ = $1 * $3; }
        | term DIVIDE factor { if ($3 != 0) $$ = $1 / $3; else yyerror("Division by zero"); }
        | term MODULO factor { if ($3 != 0) $$ = $1 % $3; else yyerror("Division by zero"); }
        ;

factor: VARIABLE { $$ = get_variable_value($1); }
        ;

%%

void yyerror(const char *s) {
    fprintf(stderr, "%s\n", s);
    exit(EXIT_FAILURE);
}

typedef struct Variable {
    char name[50];
    int value;
    struct Variable *next;
} Variable;

Variable *head = NULL;

void add_variable(char *variable_name, int value) {
    Variable *current_var = head;
    while (current_var != NULL) {
        if (strcmp(current_var->name, variable_name) == 0) {
            fprintf(stderr, "Error: Variable %s already exists.\n", variable_name);
            exit(EXIT_FAILURE);
        }
        current_var = current_var->next;
    }

    Variable *new_var = (Variable *)malloc(sizeof(Variable));
    strcpy(new_var->name, variable_name);
    new_var->value = value;
    new_var->next = head;
    head = new_var;
}

int get_variable_value(char *variable_name) {
    Variable *current_var = head;
    while (current_var != NULL) {
        if (strcmp(current_var->name, variable_name) == 0) {
            return current_var->value;
        }
        current_var = current_var->next;
    }
    fprintf(stderr, "Error: Variable %s does not exist.\n", variable_name);
    exit(EXIT_FAILURE);
}


int main() {
    FILE *inputFile = fopen("input.txt", "r");
    outputFile = fopen("output.txt", "w");

    int line_count = 0;

    if (inputFile == NULL) {
        fprintf(stderr, "Error: Could not open file.\n");
        exit(EXIT_FAILURE);
    }
        
    int ch;
    while (EOF != (ch=getc(inputFile)))
        if (ch=='\n')
        ++line_count;

    if (line_count != 3) {
        fprintf(stderr, "Error: Program must consist of 4 lines of code.\n");
        exit(EXIT_FAILURE);
    }

    rewind(inputFile);
    yyin = inputFile;
    yyparse();

    rewind(inputFile);
    while (EOF != (ch=getc(inputFile))) {
        putc(ch, outputFile);
    }
    fprintf(outputFile, " = %d", result);
    printf("Calculate successful. Result is written to the file: output.txt");

    fclose(inputFile);
    fclose(outputFile);

    return 0;
}
