package compilador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class AnalizadorLexico {

    private Terminal s;
    private File archival;
    private BufferedReader secuaz;
    private HashMap reservadas;
    private String cad;
    private boolean yaSeUso;
    private char caracter;
    private int contLineas;
    private IndicadorDeErrores secuazErroneo;

    public AnalizadorLexico(IndicadorDeErrores secuazErroneo, String nomArch) {
        this.secuazErroneo = secuazErroneo;
        cad = "";
        contLineas = 1;
        this.reservadas = new HashMap<>();
        reservadas.put("CALL", Terminal.CALL);
        reservadas.put("BEGIN", Terminal.BEGIN);
        reservadas.put("IF", Terminal.IF);
        reservadas.put("CONST", Terminal.CONST);
        reservadas.put("VAR", Terminal.VAR);
        reservadas.put("PROCEDURE", Terminal.PROCEDURE);
        reservadas.put("END", Terminal.END);
        reservadas.put("THEN", Terminal.THEN);
        reservadas.put("WHILE", Terminal.WHILE);
        reservadas.put("DO", Terminal.DO);
        reservadas.put("ODD", Terminal.ODD);
        reservadas.put("READLN", Terminal.READLN);
        reservadas.put("WRITELN", Terminal.WRITELN);
        reservadas.put("WRITE", Terminal.WRITE);
        reservadas.put("NOT", Terminal.NOT);
        reservadas.put("HALT", Terminal.HALT);
        reservadas.put("TO", Terminal.TO);
        reservadas.put("ELSE",Terminal.ELSE);
        archival = new File(nomArch);

        if (archival.exists()) {
            EntradaSalida.escribir("\t\t- PELIGRO: el proyecto ARCHIVAL ha escapado -\n");
            try {
                secuaz = new BufferedReader(new FileReader(archival));//secuaz == BufferedReader
            } catch (IOException ex) {
                EntradaSalida.escribir("\t\t- ERROR: el secuaz ha sido asesinado -");
                EntradaSalida.escribir("\t\tArchival tendra su venganza...");
                System.exit(0);
            }
        } else {
            EntradaSalida.escribir("\t\t- AMENAZA NEUTRALIZADA -\n");
            System.exit(0);
        }
        yaSeUso = false;
        EntradaSalida.escribirSinSaltar("  " + contLineas + ": ");
    }

    public String getCad() {
        return cad;
    }

    public Terminal getS() {
        return s;
    }

    public Terminal escanear() {
        cad = "";

        while (caracter == ' ' || caracter == '\t' || caracter == '\n' || caracter == '\r') {
            caracter = secuazLee();
        }
        if (!yaSeUso) {
            caracter = secuazLee();
            yaSeUso = false;
        }

        if (caracter == '\uffff') {
            s = Terminal.EOF;
            cad = "";
        } else {
            switch (caracter) {
                case ':' -> {
                    caracter = secuazLee();
                    if (caracter == '=') {
                        s = Terminal.ASIGNACION;
                        cad = ":=";
                        caracter = secuazLee();
                    } else {
                        s = Terminal.NULO;
                        cad = ":";
                        yaSeUso = true;
                    }
                }
                case '<' -> {
                    caracter = secuazLee();
                    switch (caracter) {
                        case '=' -> {
                            s = Terminal.MENOR_IGUAL;
                            cad = "<=";
                            caracter = secuazLee();
                        }
                        case '>' -> {
                            s = Terminal.DISTINTO;
                            cad = "<>";
                            caracter = secuazLee();
                        }
                        default -> {
                            s = Terminal.MENOR;
                            cad = "<";
                            yaSeUso = true;
                        }
                    };
                }
                case '>' -> {
                    caracter = secuazLee();
                    if (caracter == '=') {
                        s = Terminal.MAYOR_IGUAL;
                        cad = ">=";
                        caracter = secuazLee();
                    } else {
                        s = Terminal.MAYOR;
                        cad = ">";
                        yaSeUso = true;
                    }
                }
                case '+' -> {
                    s = Terminal.MAS;
                    cad = "+";
                    caracter = secuazLee();
                }
                case '-' -> {
                    s = Terminal.MENOS;
                    cad = "-";
                    caracter = secuazLee();
                }
                case '*' -> {
                    s = Terminal.POR;
                    cad = "*";
                    caracter = secuazLee();
                }
                case '/' -> {
                    s = Terminal.DIVIDIDO;
                    cad = "/";
                    caracter = secuazLee();
                }
                case '(' -> {
                    s = Terminal.ABRE_PARENTESIS;
                    cad = "(";
                    caracter = secuazLee();
                }
                case ')' -> {
                    s = Terminal.CIERRA_PARENTESIS;
                    cad = ")";
                    caracter = secuazLee();
                }
                case '.' -> {
                    s = Terminal.PUNTO;
                    cad = ".";
                    caracter = secuazLee();
                }
                case ',' -> {
                    s = Terminal.COMA;
                    cad = ",";
                    caracter = secuazLee();
                }
                case ';' -> {
                    s = Terminal.PUNTO_Y_COMA;
                    cad = ";";
                    caracter = secuazLee();
                }
                case '=' -> {
                    s = Terminal.IGUAL;
                    cad = "=";
                    caracter = secuazLee();
                }
                case '\'' -> {
                    cad = "'";
                    while ((caracter = secuazLee()) != '\'') {
                        if (caracter == '\n' || caracter == -1 || caracter == '\r') {
                            s = Terminal.NULO;
                            break;
                        } else {
                            cad = cad + caracter;
                        }
                    }
                    cad = cad + caracter;
                    if (cad.endsWith("'")) {
                        s = Terminal.CADENA_LITERAL;
                    } else {
                        s = Terminal.NULO;
                    }
                    caracter = secuazLee();
                }
                default -> {
                    s = secuazIdentifica();
                }
            }
        }

        //EntradaSalida.escribir(s + " " + cad);
        if (s == null) {
            s = Terminal.NULO;
            //cad = "";
            yaSeUso = true;
            caracter = secuazLee();
        }

        return s;
    }

    public char secuazLee() {
        char caracter = 0;
        try {
            caracter = (char) (secuaz.read());
            if (caracter != '\uffff') {
                EntradaSalida.escribirSinSaltar(caracter);
                if (caracter == '\n') {
                    contLineas++;
                    EntradaSalida.escribirSinSaltar((contLineas < 10 ? "  " : contLineas < 100 ? " " : "") + contLineas + ": ");
                }
            }
        } catch (IOException ex) {
            EntradaSalida.escribir(ex.getMessage());
        }
        return caracter;
    }

    private Terminal secuazIdentifica() {
        cad = "";
        cad = cad + caracter;
        Terminal symbol = null;
        boolean valido = true;
        if (Character.isDigit(caracter)) {
            do {
                caracter = secuazLee();
                if (Character.isDigit(caracter)) {
                    cad = cad + caracter;
                } else {
                    valido = false;
                }
            } while (valido);
            try {
                Integer.parseInt(cad);
                symbol = Terminal.NUMERO;
                yaSeUso = true;
            } catch (Exception e) {
                secuazErroneo.mostrar(25, cad);
            }

        } else if (Character.isLetter(caracter)) {
            do {
                caracter = secuazLee();
                if (Character.isLetterOrDigit(caracter)) {
                    cad = cad + caracter;
                } else {
                    valido = false;
                }
            } while (valido);
            yaSeUso = true;
            symbol = (Terminal) reservadas.get(cad.toUpperCase());
            if (symbol == null) {
                symbol = Terminal.IDENTIFICADOR;
            }
        }
        return symbol;
    }
}
