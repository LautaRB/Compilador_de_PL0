package compilador;

import static compilador.EntradaSalida.*;

public class Compilador {

    public static void main(String[] args) {
        String nomArch = leer("Ingrese el nombre del Plan Malevolo de Archival (archivo a compilar): ");
        IndicadorDeErrores secuazErroneo = new IndicadorDeErrores();
        AnalizadorLexico alex = new AnalizadorLexico(secuazErroneo, nomArch + ".PL0");
        GeneradorDeCodigo secuazCodeador = new GeneradorDeCodigo(nomArch + ".exe", secuazErroneo);
        AnalizadorSintactico aSinTacc = new AnalizadorSintactico(secuazErroneo, alex, secuazCodeador);
        
        aSinTacc.programa();
    }
}