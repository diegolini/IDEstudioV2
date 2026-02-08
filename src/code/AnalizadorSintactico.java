package code;

import compilerTools.Grammar;
import compilerTools.Token;
import java.util.ArrayList;

public class AnalizadorSintactico {

    private ArrayList<Token> tokens;

    public AnalizadorSintactico(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }
public void ejecutar() {
    System.out.println("---- Ejecutando sintáctico ----");
    ArrayList<Token> lista = new ArrayList<>();
    for (Token t : tokens) {
        if (!"ERROR".equals(t.getLexicalComp())) {
            lista.add(t);
        }
    }
    
    Grammar g = new Grammar(lista, Repositorio.listaErrores);
    
    // ============================================================
    //  REGLAS BASE
    // ============================================================
    g.group("valor", "(ENTERO | FLOTANTE | CADENA | CARACTER | BOOLEAN_TRUE | BOOLEAN_FALSE)");
    g.group("tipo_dato", "(TIPO_ENTERO | TIPO_DECIMAL | TIPO_TEXTO | TIPO_CAR | TIPO_LOGICO | TIPO_CORTO)");
    
    // =====================
    // REGLAS RECURSIVAS
    // =====================
    g.loopForFunExecUntilChangeNotDetected(() -> {
        
        // PASO 1: DECLARACIONES
        g.group("declaracion", "VAR tipo_dato IDENTIFICADOR (COMA IDENTIFICADOR)* PUNTO_COMA");
        g.group("declaracion","VAR tipo_dato IDENTIFICADOR (COMA IDENTIFICADOR)*",true,3, "Error sintáctico: Se esperaba ';' al final de la declaración");
        // PASO 2: EXPRESIONES
        g.group("factor", "(PAREN_IZQ expresion PAREN_DER | IDENTIFICADOR | valor)");
        g.group("termino", "factor ((MULTIPLICACION | DIVISION | MODULO) factor)*");
        g.group("expresion", "termino ((SUMA | RESTA | CONCAT) termino)*");
        
        // NUEVA: Combinar expresiones con operadores
        g.group("expresion", "expresion (SUMA | RESTA | CONCAT) expresion");
        
        // PASO 3: ASIGNACIÓN
        g.group("asignacion_st", "expresion ASIGNACION expresion PUNTO_COMA");
        
        // PASO 4: CONDICIONES
        g.group("condicion_rel",
            "expresion (OP_MAYOR | OP_MENOR | OP_MAYOR_IGUAL | OP_MENOR_IGUAL | OP_IGUAL_IGUAL | OP_DIFERENTE) expresion"
        );
        g.group("condicion_bool", "(BOOLEAN_TRUE | BOOLEAN_FALSE | IDENTIFICADOR)");
        g.group("condicion", "(condicion_rel | condicion_bool)");
        
        // PASO 5: SENTENCIAS SIMPLES
        g.group("mostrar_st", "MOSTRAR PAREN_IZQ expresion PAREN_DER PUNTO_COMA");
        g.group("leer_st", "LEER PAREN_IZQ expresion PAREN_DER PUNTO_COMA");
        
        // PASO 6: INCREMENTO/DECREMENTO
        g.group("incremento", "(OP_INCREMENTO | OP_DECREMENTO) expresion");
        
        // PASO 7: SENTENCIAS (sin estructuras de control)
        g.group("sentencia", "(declaracion | asignacion_st | mostrar_st | leer_st)");
        
        // PASO 8: LISTA DE SENTENCIAS
        g.group("lista_sentencias", "sentencia (sentencia)*");
        
        // PASO 9: BLOQUE
        g.group("bloque", "LLAVE_IZQ lista_sentencias LLAVE_DER");
        
        // PASO 10: IF
        g.group("if_st",
            "SI PAREN_IZQ condicion PAREN_DER bloque " +
            "(SINO bloque)?"
        );
        
        // PASO 11: FOR
        g.group("for_st",
            "FOR PAREN_IZQ lista_sentencias condicion PUNTO_COMA incremento PAREN_DER bloque"
        );
        
        // PASO 12: SWITCH
        g.group("case_st", "CASE valor DOS_PUNTOS lista_sentencias");
        g.group("default_st", "DEFAULT DOS_PUNTOS lista_sentencias");
        g.group("switch_st",
            "SWITCH PAREN_IZQ expresion PAREN_DER LLAVE_IZQ case_st+ default_st? LLAVE_DER"
        );
        
        // PASO 13: REDEFINIR sentencia incluyendo estructuras de control
        g.group("sentencia", "(declaracion | asignacion_st | mostrar_st | leer_st | if_st | for_st | switch_st)");
        
        // PASO 14: REDEFINIR lista_sentencias
        g.group("lista_sentencias", "sentencia (sentencia)*");
        
        // PASO 15: COMBINAR múltiples lista_sentencias
        g.group("lista_sentencias", "lista_sentencias (lista_sentencias)+");
    });
    
    // =====================
    // PROGRAMA FINAL
    // =====================
    g.group("bloque", "LLAVE_IZQ lista_sentencias LLAVE_DER");
    g.group("programa", "INICIO bloque FIN");
    
    g.show();
}
}
