package code;

import compilerTools.Token;
import compilerTools.ErrorLSSL;
import java.util.ArrayList;
public class Repositorio {
    public static ArrayList<Token> listaTokens = new ArrayList<>();
    public static ArrayList<ErrorLSSL> listaErrores = new ArrayList<>();
    public static void limpiar() {
        listaTokens.clear();
        listaErrores.clear();
    }
}
