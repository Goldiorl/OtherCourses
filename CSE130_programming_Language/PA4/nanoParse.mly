%{
(* See this for a tutorial on ocamlyacc 
 * http://plus.kaist.ac.kr/~shoh/ocaml/ocamllex-ocamlyacc/ocamlyacc-tutorial/ *)
open Nano 
%}

%token <int> Num
%token EOF
%token <string> Id
%token TRUE
%token FALSE
%token EOF
%token LPAREN
%token RPAREN
%token LET
%token REC
%token EQ
%token IN
%token FUN
%token IF
%token ELSE
%token THEN
%token ARROW

%token AND
%token OR
%token LT
%token LE
%token NE
%token PLUS
%token MINUS
%token MUL
%token DIV


%token LBRACK
%token RBRACK
%token SEMI
%token COLONCOLON

%left OR
%left AND
%left EQ NE LT LE
%right COLONCOLON
%left PLUS MINUS
%left MUL DIV
%nonassoc Num Id LPAREN LET IF LBRAC TRUE FALSE NILEXPR FUN
%left APP




%start exp 
%type <Nano.expr> exp

%%

exp: Num                        { Const $1 }
| Id                            { Var $1 }
| TRUE                          { True }
| FALSE                         { False }

| LET Id EQ exp IN exp          { Let ( $2, $4, $6) }
| LET REC Id EQ exp IN exp      { Letrec ( $3, $5, $7) }
| exp EQ exp                    { Bin( $1, Eq, $3) }
| FUN Id ARROW exp             { Fun ( $2, $4) }

| IF exp THEN exp ELSE exp      { If ($2, $4, $6) }

| exp AND exp                   { Bin ( $1, And, $3) }
| exp OR exp                    { Bin ( $1, Or, $3) }
| exp LT exp                    { Bin ( $1, Lt, $3) }
| exp LE exp                    { Bin ( $1, Le, $3) }
| exp NE exp                    { Bin ( $1, Ne, $3) }
| exp COLONCOLON exp            { Bin ( $1, Cons, $3) }
| exp PLUS exp                  { Bin ( $1, Plus, $3) }
| exp MINUS exp                 { Bin ( $1, Minus, $3) }
| exp MUL exp                   { Bin ( $1, Mul, $3) }
| exp DIV exp                   { Bin ( $1, Div, $3) }
| exp exp  %Prec APP            { App( $1, $2) }
| LPAREN exp RPAREN             { $2 }

| LBRACK RBRACK                 { NilExpr }
| LBRACK exp_in_semi RBRACK     { $2 }


exp_in_semi :
| exp { Bin( $1, Cons,NilExpr) }
| exp SEMI exp_in_semi { Bin ( $1, Cons, $3) }