package compilador;

public class IndicadorDeErrores {

    public void mostrar(int cod, String cad) {
        if (cad.contains("\r")) {
            cad = cad.substring(0, cad.length() - 1);
        }
        EntradaSalida.escribir("");
        switch (cod) {
            case 0 -> EntradaSalida.escribir("\t\t- Archival cumplio su cometido. -\n");//Termino de leer el archivo
            case 1 -> EntradaSalida.escribir("\t\t- ERROR: se encontro. " + cad + ", se esperaba: PUNTO. -\n");
            case 2 -> EntradaSalida.escribir("\t\t- Archival sigue profugo -\n");//No termino de leer el archivo
            case 3 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba: EOF. -\n");
            case 4 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba: IDENTIFICADOR. -\n");
            case 5 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba: IGUAL. -\n");
            case 6 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba: NUMERO. -\n");
            case 7 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba: COMA. -\n");
            case 8 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba: PUNTO Y COMA. -\n");
            case 9 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba: ASIGNACION. -\n");
            case 10 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba: END o PUNTO Y COMA. -\n");
            case 11 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba: THEN. -\n");
            case 12 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba: DO. -\n");
            case 13 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba: CIERRA PARENTESIS. -\n");
            case 14 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba: ABRE PARENTESIS. -\n");
            case 15 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba: PUNTO Y COMA o COMA. -\n");
            case 16 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba un amor incondicional. -\n");//No se encontro una condicion
            case 17 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba una fabrica comun o un 'String'. -\n");//No se encontro un factor
            case 18 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba: CIERRA PARENTESIS o PUNTO Y COMA. -\n");
            case 19 -> EntradaSalida.escribir("\t\t- ERROR: se encontro el identificador [" + cad + "] duplicado. -\n");
            case 20 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba un VAR. -\n");
            case 21 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba un PROCEDURE. -\n");
            case 22 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba un VAR o CONST. -\n");
            case 23 -> EntradaSalida.escribir("\t\t- ERROR: identificador " + cad + " no ha sido declarado. -\n");
            case 24 -> EntradaSalida.escribir("\t\t- ERROR: fallo la declaracion de " + cad + " (cantidad maxima de identificadores superada). -\n");
            case 25 -> EntradaSalida.escribir("\t\t- ERROR: " + cad + " no es un numero entero de 32bits valido. -\n");
            case 26 -> EntradaSalida.escribir("\t\t- ERROR: " + cad + " se esperaba un numero. -\n");
            case 27 -> EntradaSalida.escribir("\t\t- ERROR: se encontro " + cad + ", se esperaba: TO. -\n");

        }
        System.exit(0);
    }
}
