package compilador;

import static compilador.Terminal.*;
import static compilador.Constantes.*;
import java.util.List;

public class AnalizadorSintactico {

    private AnalizadorLexico alex;
    private IndicadorDeErrores secuazErroneo;
    private AnalizadorSemantico aSemBler; //Analizador semantico
    private GeneradorDeCodigo secuazCodeador; //Generador de código
    private int CountVonCount; //Cont de variables

    public AnalizadorSintactico(IndicadorDeErrores secuazErroneo, AnalizadorLexico alex, GeneradorDeCodigo secuazCodeador) {
        this.alex = alex;
        this.secuazErroneo = secuazErroneo;
        aSemBler = new AnalizadorSemantico(secuazErroneo);
        this.secuazCodeador = secuazCodeador;
        CountVonCount = 0;
    }

    public void programa() {
//        while (alex.getS() != EOF) {
//            EntradaSalida.escribir("\n[" + alex.getCad() + "]:" + alex.getS() + "\n");
//            alex.escanear();
//        }
        alex.escanear();
        secuazCodeador.cargarByte(EDI_OPCODE); //Mov EDI, 0
        secuazCodeador.cargarEntero(0);
        bloque(0);
        if (alex.getS() == PUNTO) {
            alex.escanear();
            secuazCodeador.apocalipsisFixUP(CountVonCount);
            secuazCodeador.volcar();
        } else {
            secuazErroneo.mostrar(1, alex.getCad().toUpperCase());
        }
        if (alex.getS() == EOF) {
            secuazErroneo.mostrar(0, "");
        } else {
            secuazErroneo.mostrar(2, "");
        }
    }

    public void bloque(int base) {
        int desplazamiento = 0;
        int resultadoBusqueda;
        String nombreDelIndent = "";
        secuazCodeador.cargarByte(JMP_OPCODE);
        secuazCodeador.cargarEntero(0x00);
        int inicioBloque = secuazCodeador.getTopeMemoria();
        if (alex.getS() == CONST) {
            alex.escanear();
            verificarTerminal(IDENTIFICADOR, 4);

            nombreDelIndent = alex.getCad();
            resultadoBusqueda = aSemBler.buscarIdentificador(base + desplazamiento - 1, base, nombreDelIndent);

            if (resultadoBusqueda != -1) {
                secuazErroneo.mostrar(19, alex.getCad());
            }

            alex.escanear();
            verificarTerminal(List.of(IGUAL, ASIGNACION), 5); // aqui validamos que se le pueda asignar numero a la constante.

            alex.escanear();
            boolean esNegativo = false;
            int valorNumero;
            if (alex.getS() == MENOS) {
                alex.escanear();
                esNegativo = true;
            }
            verificarTerminal(NUMERO, 6);
            valorNumero = Integer.parseInt(alex.getCad());
            valorNumero = (esNegativo) ? -valorNumero : valorNumero;
            esNegativo = false;
            //edito el negativo
            aSemBler.cargarIdentificador(nombreDelIndent, CONST, valorNumero, base + desplazamiento);
            desplazamiento++;
            alex.escanear();
            while (alex.getS() == COMA) {//Inicio del ciclo Scaloni
                alex.escanear();
                verificarTerminal(IDENTIFICADOR, 4);
                nombreDelIndent = alex.getCad();
                resultadoBusqueda = aSemBler.buscarIdentificador(base + desplazamiento - 1, base, nombreDelIndent);
                if (resultadoBusqueda != -1) {
                    secuazErroneo.mostrar(19, alex.getCad());
                }

                alex.escanear();
                verificarTerminal(List.of(IGUAL, ASIGNACION), 5);

                alex.escanear();
                if (alex.getS() == MENOS) {
                    alex.escanear();
                    esNegativo = true;
                }
                valorNumero = (esNegativo ? -Integer.parseInt(alex.getCad()) : Integer.parseInt(alex.getCad()));
                esNegativo = false;
            //Termino de editar el negativo
                aSemBler.cargarIdentificador(nombreDelIndent, CONST, valorNumero, base + desplazamiento);
                desplazamiento++;
                alex.escanear();

            }//Copa del mundo en casa
            verificarTerminal(PUNTO_Y_COMA, 15);
            alex.escanear();
        }
        if (alex.getS() == VAR) {
            alex.escanear();
            verificarTerminal(IDENTIFICADOR, 4);
            nombreDelIndent = alex.getCad();
            resultadoBusqueda = aSemBler.buscarIdentificador(base + desplazamiento - 1, base, nombreDelIndent);
            if (resultadoBusqueda != -1) {
                secuazErroneo.mostrar(19, alex.getCad());
            }
            aSemBler.cargarIdentificador(nombreDelIndent, VAR, CountVonCount, base + desplazamiento);
            CountVonCount = CountVonCount + 4;
            desplazamiento++;
            alex.escanear();

            while (alex.getS() == COMA) {
                alex.escanear();
                verificarTerminal(IDENTIFICADOR, 4);
                nombreDelIndent = alex.getCad();
                resultadoBusqueda = aSemBler.buscarIdentificador(base + desplazamiento - 1, base, nombreDelIndent);
                if (resultadoBusqueda != -1) {
                    secuazErroneo.mostrar(19, alex.getCad());
                }
                aSemBler.cargarIdentificador(nombreDelIndent, VAR, CountVonCount, base + desplazamiento);
                CountVonCount = CountVonCount + 4;
                desplazamiento++;
                alex.escanear();
            }
            verificarTerminal(PUNTO_Y_COMA, 15);
            alex.escanear();
        }

        while (alex.getS() == PROCEDURE) {
            alex.escanear();
            verificarTerminal(IDENTIFICADOR, 4);
            nombreDelIndent = alex.getCad();
            resultadoBusqueda = aSemBler.buscarIdentificador(base + desplazamiento - 1, base, nombreDelIndent);
            if (resultadoBusqueda != -1) {
                secuazErroneo.mostrar(19, alex.getCad());
            }
            aSemBler.cargarIdentificador(nombreDelIndent, PROCEDURE, secuazCodeador.getTopeMemoria(), base + desplazamiento);
            desplazamiento++;
            alex.escanear();
            verificarTerminal(PUNTO_Y_COMA, 8);
            alex.escanear();
            bloque(base + desplazamiento);
            secuazCodeador.cargarByte(RET_OPCODE);
            verificarTerminal(PUNTO_Y_COMA, 8);
            alex.escanear();
        }

        int origen = inicioBloque;
        int destino = secuazCodeador.getTopeMemoria();
        int distancia = destino - origen;

        if (distancia != 0) {
            secuazCodeador.cargarEnteroEn(distancia, origen - 4);//AREGLADOR
        } else {
            secuazCodeador.setTopeMemoria(secuazCodeador.getTopeMemoria() - 5);
        }
        proposicion(base, desplazamiento);
    }

    public void proposicion(int base, int desplazamiento) {
        int resultadoBusqueda = 0;
        String nombreDelIndent = "";
        IdentificadorBean resultadoBean;
        switch (alex.getS()) {
            case IDENTIFICADOR:
                nombreDelIndent = alex.getCad();
                resultadoBusqueda = aSemBler.buscarIdentificador(base + desplazamiento - 1, 0, nombreDelIndent);
                if (resultadoBusqueda == -1) {
                    secuazErroneo.mostrar(23, nombreDelIndent);
                }
                resultadoBean = aSemBler.buscarInfo(resultadoBusqueda);
                if (resultadoBean.getTipo() != VAR) {
                    secuazErroneo.mostrar(20, nombreDelIndent);
                }
                alex.escanear();
                verificarTerminal(ASIGNACION, 9);
                alex.escanear();
                expresion(base, desplazamiento);
                secuazCodeador.cargarPOP(); // POP EAX
                secuazCodeador.cargarByte(MOV_VAR_EAX_OPCODE); // MOV [EDI+abcdefgh], EAX 
                secuazCodeador.cargarByte(MOV_VAR_EAX_OPCODE2);
                secuazCodeador.cargarEntero(resultadoBean.getValor());
                break;
            case CALL:
                alex.escanear();
                verificarTerminal(IDENTIFICADOR, 4);
                nombreDelIndent = alex.getCad();
                resultadoBusqueda = aSemBler.buscarIdentificador(base + desplazamiento - 1, 0, nombreDelIndent);
                if (resultadoBusqueda == -1 || aSemBler.buscarInfo(resultadoBusqueda).getTipo() != PROCEDURE) {
                    secuazErroneo.mostrar(21, alex.getCad());
                }
                alex.escanear();

                int origen = secuazCodeador.getTopeMemoria() + 5;
                int destino = aSemBler.buscarInfo(resultadoBusqueda).getValor();
                int distancia = destino - origen;
                secuazCodeador.cargarByte(CALL_OPCODE); // CALL
                secuazCodeador.cargarEntero(distancia);
                break;
            case BEGIN:
                alex.escanear();
                proposicion(base, desplazamiento);
                while (alex.getS() == PUNTO_Y_COMA) {
                    alex.escanear();
                    proposicion(base, desplazamiento);
                }
                if (alex.getS() == HALT) {
                    secuazCodeador.cargarByte(JMP_OPCODE);
                    secuazCodeador.cargarEntero(0x588 - (secuazCodeador.getTopeMemoria() + 4)); //Fin del programa
                    alex.escanear();
                }
                verificarTerminal(END, 10);
                alex.escanear();
                break;
            case IF:
                alex.escanear();
                condicion(base, desplazamiento);
                int origenSalto = secuazCodeador.getTopeMemoria();
                verificarTerminal(THEN, 11);
                alex.escanear();
                proposicion(base, desplazamiento);
                //////////////////////////////////////Else
                secuazCodeador.cargarByte(JMP_OPCODE);
                secuazCodeador.cargarEntero(0x00);
                int origenSaltoElse = secuazCodeador.getTopeMemoria();
                int destinoSalto = secuazCodeador.getTopeMemoria();
                secuazCodeador.cargarEnteroEn(destinoSalto - origenSalto, origenSalto - 4); //salto if normal
                if(alex.getS() == ELSE){
                    alex.escanear();
                    proposicion(base, desplazamiento);
                }
                ////////////////////////////////////////
                int distanciaSalto = secuazCodeador.getTopeMemoria();
                secuazCodeador.cargarEnteroEn(distanciaSalto - origenSaltoElse, origenSaltoElse - 4);
                break;
            case WHILE:
                alex.escanear();
                int inicioCondicion = secuazCodeador.getTopeMemoria();
                condicion(base, desplazamiento);
                origenSalto = secuazCodeador.getTopeMemoria();
                verificarTerminal(DO, 12);
                alex.escanear();
                proposicion(base, desplazamiento);
                origen = secuazCodeador.getTopeMemoria() + 5;
                destino = inicioCondicion;
                distancia = destino - origen;
                secuazCodeador.cargarByte(JMP_OPCODE);// JMP
                secuazCodeador.cargarEntero(distancia);
                destinoSalto = secuazCodeador.getTopeMemoria();
                distanciaSalto = destinoSalto - origenSalto;
                secuazCodeador.cargarEnteroEn(distanciaSalto, origenSalto - 4);// Fix Up del JUMP del condicional ODD.
                break;
            case READLN:
                alex.escanear();
                verificarTerminal(ABRE_PARENTESIS, 14);
                alex.escanear();
                verificarTerminal(IDENTIFICADOR, 4);
                nombreDelIndent = alex.getCad();
                resultadoBusqueda = aSemBler.buscarIdentificador(base + desplazamiento - 1, base, nombreDelIndent);
                if (resultadoBusqueda == -1 || aSemBler.buscarInfo(resultadoBusqueda).getTipo() != VAR) {
                    secuazErroneo.mostrar(20, alex.getCad());
                }
                alex.escanear();

                origen = secuazCodeador.getTopeMemoria() + 5;
                destino = 0x0590; // lee por consola un número entero y lo deja guardado en EAX.
                secuazCodeador.cargarByte(CALL_OPCODE); // CALL
                secuazCodeador.cargarEntero(destino - origen);
                secuazCodeador.cargarByte(MOV_VAR_EAX_OPCODE); // MOV [EDI+abcdefgh], EAX 
                secuazCodeador.cargarByte(MOV_VAR_EAX_OPCODE2);
                secuazCodeador.cargarEntero(aSemBler.buscarInfo(resultadoBusqueda).getValor());
                while (alex.getS() == COMA) {
                    alex.escanear();
                    verificarTerminal(IDENTIFICADOR, 4);
                    nombreDelIndent = alex.getCad();
                    resultadoBusqueda = aSemBler.buscarIdentificador(base + desplazamiento - 1, base, nombreDelIndent);
                    if (resultadoBusqueda == -1 || aSemBler.buscarInfo(resultadoBusqueda).getTipo() != VAR) {
                        secuazErroneo.mostrar(20, alex.getCad());
                    }
                    alex.escanear();
                    origen = secuazCodeador.getTopeMemoria() + 5;
                    destino = 0x0590; // lee por consola un número entero y lo deja guardado en EAX.
                    secuazCodeador.cargarByte(CALL_OPCODE); // CALL
                    secuazCodeador.cargarEntero(destino - origen);
                    secuazCodeador.cargarByte(MOV_VAR_EAX_OPCODE); // MOV [EDI+abcdefgh], EAX 
                    secuazCodeador.cargarByte(MOV_VAR_EAX_OPCODE2);
                    secuazCodeador.cargarEntero(aSemBler.buscarInfo(resultadoBusqueda).getValor());
                }
                verificarTerminal(CIERRA_PARENTESIS, 13);
                alex.escanear();
                break;
            case WRITELN:
                alex.escanear();
                if (alex.getS() == ABRE_PARENTESIS) {
                    alex.escanear();
                    if (alex.getS() == CADENA_LITERAL) {
                        int ubCad = secuazCodeador.getUbicacionCadena();
                        secuazCodeador.cargarByte(MOV_EAX_CONST_OPCODE);
                        secuazCodeador.cargarEntero(ubCad);
                        origen = secuazCodeador.getTopeMemoria() + 5;
                        destino = 0x3E0;
                        distancia = destino - origen;
                        secuazCodeador.cargarByte(CALL_OPCODE);
                        secuazCodeador.cargarEntero(distancia);
                        //EntradaSalida.escribir(alex.getCad());
                        String cadena = alex.getCad().substring(1, alex.getCad().length() - 1);
                        secuazCodeador.cargarByte(JMP_OPCODE);
                        secuazCodeador.cargarEntero(cadena.length() + 1);
                        for (int i = 0; i < cadena.length(); i++) {
                            secuazCodeador.cargarByte(cadena.charAt(i));
                        }
                        secuazCodeador.cargarByte(0x00);
                        alex.escanear();
                    } else {
                        expresion(base, desplazamiento);
                        secuazCodeador.cargarPOP();
                        origen = secuazCodeador.getTopeMemoria() + 5;
                        destino = 0x420;
                        distancia = destino - origen;
                        secuazCodeador.cargarByte(CALL_OPCODE);
                        secuazCodeador.cargarEntero(distancia);
                    }
                    while (alex.getS() == COMA) {
                        alex.escanear();
                        if (alex.getS() == CADENA_LITERAL) {
                            int ubCad = secuazCodeador.getUbicacionCadena();
                            secuazCodeador.cargarByte(MOV_EAX_CONST_OPCODE);
                            secuazCodeador.cargarEntero(ubCad);
                            origen = secuazCodeador.getTopeMemoria() + 5;
                            destino = 0x3E0;
                            distancia = destino - origen;
                            secuazCodeador.cargarByte(CALL_OPCODE);
                            secuazCodeador.cargarEntero(distancia);
                            //EntradaSalida.escribir(alex.getCad());
                            String cadena = alex.getCad().substring(1, alex.getCad().length() - 1);
                            secuazCodeador.cargarByte(JMP_OPCODE);
                            secuazCodeador.cargarEntero(cadena.length() + 1);
                            for (int i = 0; i < cadena.length(); i++) {
                                secuazCodeador.cargarByte(cadena.charAt(i));
                            }
                            secuazCodeador.cargarByte(0x00);
                            alex.escanear();
                        } else {
                            expresion(base, desplazamiento);
                            secuazCodeador.cargarPOP();
                            origen = secuazCodeador.getTopeMemoria() + 5;
                            destino = 0x420;
                            distancia = destino - origen;
                            secuazCodeador.cargarByte(CALL_OPCODE);
                            secuazCodeador.cargarEntero(distancia);
                        }
                    }
                    verificarTerminal(CIERRA_PARENTESIS, 18);
                    alex.escanear();
                }
                origen = secuazCodeador.getTopeMemoria() + 5;
                destino = 0x410;
                distancia = destino - origen;
                secuazCodeador.cargarByte(CALL_OPCODE);
                secuazCodeador.cargarEntero(distancia);
                break;
            case WRITE:
                alex.escanear();
                verificarTerminal(ABRE_PARENTESIS, 14);
                alex.escanear();
                if (alex.getS() == CADENA_LITERAL) {
                    int ubCad = secuazCodeador.getUbicacionCadena();
                    secuazCodeador.cargarByte(MOV_EAX_CONST_OPCODE);
                    secuazCodeador.cargarEntero(ubCad);
                    origen = secuazCodeador.getTopeMemoria() + 5;
                    destino = 0x3E0;
                    distancia = destino - origen;
                    secuazCodeador.cargarByte(CALL_OPCODE);
                    secuazCodeador.cargarEntero(distancia);
                    //EntradaSalida.escribir(alex.getCad());
                    String cadena = alex.getCad().substring(1, alex.getCad().length() - 1);
                    secuazCodeador.cargarByte(JMP_OPCODE);
                    secuazCodeador.cargarEntero(cadena.length() + 1);
                    for (int i = 0; i < cadena.length(); i++) {
                        secuazCodeador.cargarByte(cadena.charAt(i));
                    }
                    secuazCodeador.cargarByte(0x00);
                    alex.escanear();
                } else {
                    expresion(base, desplazamiento);
                    secuazCodeador.cargarPOP();
                    origen = secuazCodeador.getTopeMemoria() + 5;
                    destino = 0x420;
                    distancia = destino - origen;
                    secuazCodeador.cargarByte(CALL_OPCODE);
                    secuazCodeador.cargarEntero(distancia);
                }
                while (alex.getS() == COMA) {
                    alex.escanear();
                    if (alex.getS() == CADENA_LITERAL) {
                        int ubCad = secuazCodeador.getUbicacionCadena();
                        secuazCodeador.cargarByte(MOV_EAX_CONST_OPCODE);
                        secuazCodeador.cargarEntero(ubCad);
                        origen = secuazCodeador.getTopeMemoria() + 5;
                        destino = 0x3E0;
                        distancia = destino - origen;
                        secuazCodeador.cargarByte(CALL_OPCODE);
                        secuazCodeador.cargarEntero(distancia);
                        //EntradaSalida.escribir(alex.getCad());
                        String cadena = alex.getCad().substring(1, alex.getCad().length() - 1);
                        secuazCodeador.cargarByte(JMP_OPCODE);
                        secuazCodeador.cargarEntero(cadena.length() + 1);
                        for (int i = 0; i < cadena.length(); i++) {
                            secuazCodeador.cargarByte(cadena.charAt(i));
                        }
                        secuazCodeador.cargarByte(0x00);
                        alex.escanear();
                    } else {
                        expresion(base, desplazamiento);
                        secuazCodeador.cargarPOP();
                        origen = secuazCodeador.getTopeMemoria() + 5;
                        destino = 0x420;
                        distancia = destino - origen;
                        secuazCodeador.cargarByte(CALL_OPCODE);
                        secuazCodeador.cargarEntero(distancia);
                    }
                }
                verificarTerminal(CIERRA_PARENTESIS, 18);
                alex.escanear();
                break;
        }
    }

    public void condicion(int base, int desplazamiento) {
        if (alex.getS() == NOT) {
            alex.escanear();
            verificarTerminal(ABRE_PARENTESIS, 14);
            alex.escanear();
            condicion(base, desplazamiento);
            ////////////////////////////////////////////////// FIX_UP //////////////////////////////////////////////////
            int posSaltoACorregir = secuazCodeador.getTopeMemoria() - 7;
            byte saltoACorregir = secuazCodeador.descargarByteVon(posSaltoACorregir);
            switch (saltoACorregir) {
                case 0x74: // JE_OPCODE
                    secuazCodeador.cargarByteEn(JNE_OPCODE, posSaltoACorregir);
                    break;
                case 0x75: // JNE_OPCODE
                    secuazCodeador.cargarByteEn(JE_OPCODE, posSaltoACorregir);
                    break;
                case 0x7F: // JG_OPCODE
                    secuazCodeador.cargarByteEn(JLE_OPCODE, posSaltoACorregir);
                    break;
                case 0x7D: // JGE_OPCODE
                    secuazCodeador.cargarByteEn(JL_OPCODE, posSaltoACorregir);
                    break;
                case 0x7C: // JL_OPCODE
                    secuazCodeador.cargarByteEn(JGE_OPCODE, posSaltoACorregir);
                    break;
                case 0x7E: // JLE_OPCODE
                    secuazCodeador.cargarByteEn(JG_OPCODE, posSaltoACorregir);
                    break;
                case 0x7B: // JPO_OPCODE
                    secuazCodeador.cargarByteEn(JP_OPCODE, posSaltoACorregir);
                    break;
            }
            ////////////////////////////////////////////////// FIX_UP //////////////////////////////////////////////////
            verificarTerminal(CIERRA_PARENTESIS, 13);
            alex.escanear();
        } else if (alex.getS() == ODD) {
            alex.escanear();
            expresion(base, desplazamiento);
            secuazCodeador.cargarPOP(); // POP EAX
            secuazCodeador.cargarByte(0xA8); // TEST AL...
            secuazCodeador.cargarByte(0x01); // ...TO 01
            secuazCodeador.cargarByte(JPO_OPCODE); // JPO dir                         =7 <─┐
            secuazCodeador.cargarByte(0x05); // Saltea el JMP                         +1   │
            secuazCodeador.cargarByte(JMP_OPCODE); // JMP dir                         +1   │
            secuazCodeador.cargarEntero(0x00); // hay Fix-Up                          +4  ─┘
        } else {
            expresion(base, desplazamiento);
            Terminal operador = alex.getS();
            verificarTerminal(List.of(IGUAL, DISTINTO, MENOR, MENOR_IGUAL, MAYOR, MAYOR_IGUAL), 16);
            alex.escanear();
            expresion(base, desplazamiento);
            secuazCodeador.cargarPOP(); //POP EAX
            secuazCodeador.cargarByte(POP_EBX_OPCODE); //POP EBX
            secuazCodeador.cargarByte(CMP_OPCODE); //CMP EBX, EAX
            secuazCodeador.cargarByte(CMP_OPCODE2);
            switch (operador) {//LAUTI HASHMAP...
                case IGUAL:
                    secuazCodeador.cargarByte(JE_OPCODE); //Salta si
                    break;
                case DISTINTO:
                    secuazCodeador.cargarByte(JNE_OPCODE); //Salta si
                    break;
                case MENOR:
                    secuazCodeador.cargarByte(JL_OPCODE); //Salta si
                    break;
                case MENOR_IGUAL:
                    secuazCodeador.cargarByte(JLE_OPCODE); //Salta si
                    break;
                case MAYOR:
                    secuazCodeador.cargarByte(JG_OPCODE); //Salta si
                    break;
                case MAYOR_IGUAL:
                    secuazCodeador.cargarByte(JGE_OPCODE); //Salta si
                    break;
            }
            secuazCodeador.cargarByte(0x05); //Distancia del salto anterior
            secuazCodeador.cargarByte(JMP_OPCODE); //JMP dir
            secuazCodeador.cargarEntero(0x00);
        }
    }

    public void expresion(int base, int desplazamiento) {
        Terminal simbolo = null;
        int num;
        switch (alex.getS()) {
            case MAS:
                simbolo = alex.getS();
                alex.escanear();
                break;
            case MENOS:
                simbolo = alex.getS();
                alex.escanear();
                break;
        }
        termino(base, desplazamiento);
        if (simbolo == MENOS) { // Convierte a negativo el numero que devuelve TERMINO en caso de que el switch de arriba diga MENOS.
            secuazCodeador.cargarPOP(); //POP EAX
            secuazCodeador.cargarByte(NEG_OPCODE); //NEG EAX
            secuazCodeador.cargarByte(NEG_OPCODE2);
            secuazCodeador.cargarByte(PUSH_EAX_OPCODE); //PUSH EAX
        }
        while (alex.getS() == MAS || alex.getS() == MENOS) {
            simbolo = alex.getS();
            alex.escanear();
            termino(base, desplazamiento);
            secuazCodeador.cargarPOP(); //POP EAX
            secuazCodeador.cargarByte(POP_EBX_OPCODE); //POP EBX
            if (simbolo == MAS) {
                secuazCodeador.cargarByte(ADD_OPCODE); //ADD EAX, EBX
                secuazCodeador.cargarByte(ADD_OPCODE2);
            } else {
                secuazCodeador.cargarByte(XCHG_OPCODE); //XCHG EAX, EBX
                secuazCodeador.cargarByte(SUB_OPCODE); //SUB EAX, EBX
                secuazCodeador.cargarByte(SUB_OPCODE2);
            }
            secuazCodeador.cargarByte(PUSH_EAX_OPCODE); //PUSH EAX
        }
    }

    public void termino(int base, int desplazamiento) {
        factor(base, desplazamiento);
        while (alex.getS() == POR || alex.getS() == DIVIDIDO) {
            Terminal operador = alex.getS();
            alex.escanear();
            factor(base, desplazamiento);
            secuazCodeador.cargarPOP(); //POP EAX
            secuazCodeador.cargarByte(POP_EBX_OPCODE); //POP EBX
            if (operador == POR) {
                secuazCodeador.cargarByte(IMUL_OPCODE); //IMUL EBX
                secuazCodeador.cargarByte(IMUL_OPCODE2);
            } else {
                secuazCodeador.cargarByte(XCHG_OPCODE); //XCHG EAX, EBX
                secuazCodeador.cargarByte(CDQ_OPCODE);//CDQ
                secuazCodeador.cargarByte(IDIV_OPCODE);//IDIV EBX
                secuazCodeador.cargarByte(IDIV_OPCODE2);
            }
            secuazCodeador.cargarByte(PUSH_EAX_OPCODE); // PUSH EAX
        }
    }

    public void factor(int base, int desplazamiento) {
        int resultadoBusqueda;
        String nombreDelIndent = "";
        switch (alex.getS()) {
            case IDENTIFICADOR:
                nombreDelIndent = alex.getCad();
                resultadoBusqueda = aSemBler.buscarIdentificador(base + desplazamiento - 1, 0, nombreDelIndent);
                if (resultadoBusqueda == -1) {
                    secuazErroneo.mostrar(23, alex.getCad());
                }
                IdentificadorBean resultadoBusBean = aSemBler.buscarInfo(resultadoBusqueda);
                switch (resultadoBusBean.getTipo()) {
                    case PROCEDURE:
                        secuazErroneo.mostrar(22, alex.getCad());
                        break;
                    case CONST:
                        secuazCodeador.cargarByte(MOV_EAX_CONST_OPCODE); //MOV EAX
                        secuazCodeador.cargarEntero(resultadoBusBean.getValor()); //nro
                        secuazCodeador.cargarByte(PUSH_EAX_OPCODE); //PUSH EAX
                        break;
                    case VAR:
                        secuazCodeador.cargarByte(MOV_EAX_VAR_OPCODE); //MOV EAX, [EDI + ...]
                        secuazCodeador.cargarByte(MOV_VAR_EAX_OPCODE2);
                        secuazCodeador.cargarEntero(resultadoBusBean.getValor()); //nro
                        secuazCodeador.cargarByte(PUSH_EAX_OPCODE); //PUSH EAX
                }
                alex.escanear();
                break;
            case NUMERO:
                secuazCodeador.cargarByte(MOV_EAX_CONST_OPCODE); //MOV EAX
                secuazCodeador.cargarEntero(Integer.parseInt(alex.getCad())); //nro
                secuazCodeador.cargarByte(PUSH_EAX_OPCODE); //PUSH EAX
                alex.escanear();
                break;
            case ABRE_PARENTESIS:
                alex.escanear();
                expresion(base, desplazamiento);
                verificarTerminal(CIERRA_PARENTESIS, 13);
                alex.escanear();
                break;
            default:
                secuazErroneo.mostrar(17, alex.getCad().toUpperCase());
                break;
        }
    }

    private void verificarTerminal(Terminal terminaleEsperado, int errorCode) {
        if (alex.getS() != terminaleEsperado) {
            secuazErroneo.mostrar(errorCode, alex.getCad().toUpperCase());
        }
    }

    private void verificarTerminal(List<Terminal> terminalesEsperados, int errorCode) {
        if (!terminalesEsperados.contains(alex.getS())) {
            secuazErroneo.mostrar(errorCode, alex.getCad().toUpperCase());
        }
    }
}
