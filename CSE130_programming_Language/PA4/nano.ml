exception MLFailure of string

type binop = 
  Plus 
| Minus 
| Mul 
| Div 
| Eq 
| Ne 
| Lt 
| Le 
| And 
| Or          
| Cons

type expr =   
  Const of int 
| True   
| False      
| NilExpr
| Var of string    
| Bin of expr * binop * expr 
| If  of expr * expr * expr
| Let of string * expr * expr 
| App of expr * expr 
| Fun of string * expr    
| Letrec of string * expr * expr
	
type value =  
  Int of int		
| Bool of bool          
| Closure of env * string option * string * expr 
| Nil                    
| Pair of value * value     

and env = (string * value) list

let binopToString op = 
  match op with
      Plus -> "+" 
    | Minus -> "-" 
    | Mul -> "*" 
    | Div -> "/"
    | Eq -> "="
    | Ne -> "!="
    | Lt -> "<"
    | Le -> "<="
    | And -> "&&"
    | Or -> "||"
    | Cons -> "::"

let rec valueToString v = 
  match v with 
    Int i -> 
      Printf.sprintf "%d" i
  | Bool b -> 
      Printf.sprintf "%b" b
  | Closure (evn,fo,x,e) -> 
      let fs = match fo with None -> "Anon" | Some fs -> fs in
      Printf.sprintf "{%s,%s,%s,%s}" (envToString evn) fs x (exprToString e)
  | Pair (v1,v2) -> 
      Printf.sprintf "(%s::%s)" (valueToString v1) (valueToString v2) 
  | Nil -> 
      "[]"

and envToString evn =
  let xs = List.map (fun (x,v) -> Printf.sprintf "%s:%s" x (valueToString v)) evn in
  "["^(String.concat ";" xs)^"]"

and exprToString e =
  match e with
      Const i ->
        Printf.sprintf "%d" i
    | True -> 
        "true" 
    | False -> 
        "false"
    | Var x -> 
        x
    | Bin (e1,op,e2) -> 
        Printf.sprintf "%s %s %s" 
        (exprToString e1) (binopToString op) (exprToString e2)
    | If (e1,e2,e3) -> 
        Printf.sprintf "if %s then %s else %s" 
        (exprToString e1) (exprToString e2) (exprToString e3)
    | Let (x,e1,e2) -> 
        Printf.sprintf "let %s = %s in \n %s" 
        x (exprToString e1) (exprToString e2) 
    | App (e1,e2) -> 
        Printf.sprintf "(%s %s)" (exprToString e1) (exprToString e2)
    | Fun (x,e) -> 
        Printf.sprintf "fun %s -> %s" x (exprToString e) 
    | Letrec (x,e1,e2) -> 
        Printf.sprintf "let rec %s = %s in \n %s" 
        x (exprToString e1) (exprToString e2) 

(*********************** Some helpers you might need ***********************)

let rec fold f base args = 
  match args with [] -> base
    | h::t -> fold f (f(base,h)) t

let listAssoc (k,l) = 
  fold (fun (r,(t,v)) -> if r = None && k=t then Some v else r) None l

(*********************** Your code starts here ****************************)

let lookup (x,evn) = match listAssoc(x, evn) with
                    | Some a -> a
                    | None -> raise (MLFailure ("variable not bound: "^x))  


let rec eval (evn,e) = match e with
                      | Const intvalue -> Int intvalue 
                      | True -> Bool true 
                      | False -> Bool false
                      | NilExpr -> Nil
                      | Var symbol -> lookup( symbol, evn )
                      | Bin (e1, op, e2) -> 
                          let v1 = eval(evn, e1) in 
                          let v2 = eval(evn, e2) in ( 
                          match (v1, op, v2) with
                          | (Int a, Cons,Nil) ->Pair( Int a,Nil)
                          | (Int a, Cons,Pair(p1, p2)) ->Pair( Int a,Pair( p1, p2))
                          | (Pair(p1,p2), Cons,Pair(p3,p4)) ->Pair( Pair(p1, p2),Pair(p3,p4))
                          | (Int a, Plus, Int b) -> Int (a + b)
                          | (Int a, Minus, Int b) -> Int (a - b)
                          | (Int a, Mul, Int b) -> Int (a * b)
                          | (Int a, Div, Int b) -> Int (a / b)
                          | (Int a, Lt, Int b) -> Bool (a < b)
                          | (Int a, Le, Int b) -> Bool (a <= b)
                          | (Int a, Eq, Int b) -> Bool (a = b)
                          | (Int a, Ne, Int b) -> Bool (a <> b)
                          | (Bool a, Eq, Bool b) -> Bool (a = b)
                          | (Bool a, Ne, Bool b) -> Bool (a <> b)
                          | (Bool a, And, Bool b) -> Bool (a && b)
                          | (Bool a, Or, Bool b) -> Bool (a || b)                      
                          | _ -> raise (MLFailure ("Unexpected binary operation")) )
                      | If (a,b,c) -> let Bool condition = eval (evn,a) in 
                                      if condition then eval(evn, b) else eval(evn, c)
                      (* Let is evaluated immediately *)
                      | Let(var, expr, inExpr) -> 
                            let evn = ( (var, eval(evn,expr))::evn) in eval(evn,inExpr)
                      (* App is process of fetching env, <f x> or <(anonym) x> 
                         Should consider things like  *)
                      | App(appFunc_expr, paramSymbol) -> let paramExprValue = eval(evn, paramSymbol)  in
                          (
                            match appFunc_expr with
                              | Var ("hd") -> 
                                  (
                                    match paramExprValue with 
                                    |Pair(x,y) -> x
                                    |_ -> raise(MLFailure ("Param other than pair used in hd function"))
                                  ) 
                              | Var ("tl") -> 
                                  (
                                    match paramExprValue with 
                                    |Pair(x,y) -> y
                                    |_ -> raise(MLFailure ("Param other than pair used in tl function"))
                                  ) 
                              | _ -> 
                                (
                                  match eval(evn, appFunc_expr) with
                                  (* get a closure for function *)
                                  |Closure(evn, nameInClosure, var, exprInClosure) ->
                                    (
                                      match nameInClosure with 
(*                                       for something like < let rec fac x = fun x -> x*fac(x-1)  in fac 10 
                                      First: fac 10 -> calls app(), the env has a recclosure with name,
                                             exprInClosure is an anonymous closure also calls fac
                                      Second: So app() extract the anonymous closure, insert the name fac, add into env
                                              Ten evaluate the expression in the anonymous closure
 *)                                   |Some funcName -> let funcInEnv = (funcName, Closure(evn, nameInClosure, var, exprInClosure)) in 
                                                        let newevn = funcInEnv :: ((var, paramExprValue)::evn)
                                                          in eval(newevn, exprInClosure)
                                      (* Real corner case such as: <(fun x -> x*x) x> 
                                         Could be used for recursive functions
                                      *)
                                      |None -> eval( (var,paramExprValue)::evn, exprInClosure)
                                    )
                                  | _ -> raise (MLFailure ("Trouble parsing FuncName"))
                                )

                          )

                      | Fun(funcParam_str, funcExpr) -> Closure(evn, None, funcParam_str, funcExpr)
                      | Letrec(funcName_str, evalExpr, inExpr) -> let recFuncClosure = 
                        (
                        match eval(evn, evalExpr) with
                        |Closure(evn, None, var, expr) -> Closure(evn, Some funcName_str, var, expr)
                          (* incase the expression is just a evalution, not a function *)
                        |_ -> eval(evn, evalExpr)
                        ) in 
                        let newevn = (funcName_str, recFuncClosure)::evn in 
                        eval(newevn, inExpr)  
                      | _ -> raise (MLFailure ("Evaluation error"))



(**********************     Testing Code  ******************************)

(* Uncomment to test part (a) 
   
let evn = [("z1",Int 0);("x",Int 1);("y",Int 2);("z",Int 3);("z1",Int 4)]

let e1  = Bin(Bin(Var "x",Plus,Var "y"), Minus, Bin(Var "z",Plus,Var "z1"))

let _   = eval (evn, e1)        (* EXPECTED: Nano.value = Int 0 *)

let _   = eval (evn, Var "p")   (* EXPECTED:  Exception: Nano.MLFailure "variable not bound: p". *)

*)

(* Uncomment to test part (b) 

let evn = [("z1",Int 0);("x",Int 1);("y",Int 2);("z",Int 3);("z1",Int 4)]
 
let e1  = If(Bin(Var "z1",Lt,Var "x"),Bin(Var "y",Ne,Var "z"),False)
  
let _   = eval (evn,e1)         (* EXPECTED: Nano.value = Bool true *)

let e2  = If(Bin(Var "z1",Eq,Var "x"), 
                Bin(Var "y",Le,Var "z"),
                Bin(Var "z",Le,Var "y")
            )

let _   = eval (evn,e2)         (* EXPECTED: Nano.value = Bool false *)

*)

(* Uncomment to test part (c) 

let e1 = Bin(Var "x",Plus,Var "y")

let e2 = Let("x",Const 1, Let("y", Const 2, e1)) 

let _  = eval ([], e2)          (* EXPECTED: Nano.value = Int 3 *)

let e3 = Let("x", Const 1, 
           Let("y", Const 2, 
             Let("z", e1, 
               Let("x", Bin(Var "x",Plus,Var "z"), 
                 e1)
             )
           )
         )

let _  = eval ([],e3)           (* EXPCETED: Nano.value = Int 6 *)

*)


(* Uncomment to test part (d) 

let _ = eval ([], Fun ("x",Bin(Var "x",Plus,Var "x"))) 

(* EXPECTED: Nano.value = Closure ([], None, "x", Bin (Var "x", Plus, Var "x")) *)

let _ = eval ([],App(Fun ("x",Bin(Var "x",Plus,Var "x")),Const 3));;

(* EXPECTED: Nano.value = Int 6 *)

let e3 = Let ("h", Fun("y", Bin(Var "x", Plus, Var "y")), 
               App(Var "f",Var "h"))
 
let e2 = Let("x", Const 100, e3)
 
let e1 = Let("f",Fun("g",Let("x",Const 0,App(Var "g",Const 2))),e2) 

let _  = eval ([], e1)        
    (* EXPECTED: Nano.value = Int 102 *)

let _ = eval ([],Letrec("f",Fun("x",Const 0),Var "f"))
    (* EXPECTED: Nano.value = Closure ([], Some "f", "x", Const 0) *)

*)

(* Uncomment to test part (e)
 
let _ = eval ([], 
              Letrec("fac", 
                        Fun("n", If (Bin (Var "n", Eq, Const 0), 
                                    Const 1, 
                                    Bin(Var "n", Mul, App(Var "fac",Bin(Var "n",Minus,Const 1))))),
              App(Var "fac", Const 10)))

(* EXPECTED: Nano.value = Int 3628800 *)

 *)

(* Uncomment to test part (f)
 
let _ = eval ([],Bin(Const 1,Cons,Bin(Const 2,Cons,NilExpr)))

    (* EXPECTED: Nano.value = Pair (Int 1, Pair (Int 2, Nil)) *)

let _ = eval ([],App(Var "hd",Bin(Const 1,Cons,Bin(Const 2,Cons,NilExpr))))

    (* EXPECTED: Nano.value = Int 1 *)

let _ = eval ([],App(Var "tl",Bin(Const 1,Cons,Bin(Const 2,Cons,NilExpr))))
    
    (* EXPECTED: Nano.value = Pair (Int 2, Nil) *)

 *)
