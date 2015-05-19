{
  open Nano        (* nano.ml *)
  open NanoParse   (* nanoParse.ml from nanoParse.mly *)
}

rule token = parse
    eof         { EOF }
  | [' ' '\t' '\n' '\r']    { token lexbuf }

  | "true"             { TRUE }
  | "false"            { FALSE }

  | "let"              { LET }
  | "rec"              { REC }
  | "="                { EQ }
  | "in"               { IN }
  | "fun"              { FUN }
  | "->"               { ARROW }
  | "if"               { IF }
  | "then"             { THEN }
  | "else"             { ELSE }

  | "+"                { PLUS }
  | "-"                { MINUS }
  | "*"                { MUL }
  | "/"                { DIV }
  | "<"                { LT }
  | "<="               { LE }
  | "!="               { NE }
  | "&&"               { AND }
  | "||"               { OR }

  | "("                { LPAREN }
  | ")"                { RPAREN }

  | "["                { LBRACK }
  | "]"                { RBRACK }
  | "::"               { COLONCOLON }
  | ";"                { SEMI }  
  | [ '0'-'9']+ as num             { Num(int_of_string num) }
  | ['A'-'Z' 'a'-'z' '_']['A'-'Z' 'a'-'z' '0'-'9' '_']* as str { Id(str) }  
  | _           { raise (MLFailure ("Illegal Character '"^(Lexing.lexeme lexbuf)^"'")) }
