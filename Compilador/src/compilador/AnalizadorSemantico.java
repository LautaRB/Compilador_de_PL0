package compilador;

public class AnalizadorSemantico {

    private IdentificadorBean casaDeBean[]; //Tabla
    private IndicadorDeErrores secuazGuiso;
    private IndicadorDeErrores secuazErroneo;

    public AnalizadorSemantico(IndicadorDeErrores secuazGuiso) {
        casaDeBean = new IdentificadorBean[Constantes.CANT_MAX_IDENT];
        this.secuazGuiso = secuazGuiso;
        secuazErroneo = new IndicadorDeErrores();
    }

    public int buscarIdentificador(int inicio, int fin, String nombreIdent) {
        int i = inicio;
        //EntradaSalida.escribir("\n INICIO: " + inicio + " FIN: " + fin + " BUSCA A: " + nombreIdent + "\n");
        while (i >= fin) {
            //EntradaSalida.escribir("\n ENCONTRO: " + casaDeBean[i].getNombre() + "(" + casaDeBean[i].getTipo() + ") EN POSICION: " + i + "\n");
            if (casaDeBean[i].getNombre().equalsIgnoreCase(nombreIdent)) {
                return i;
            }
            i--;
        }
        return -1;
    }

    public void cargarIdentificador(String nombreIdent, Terminal tipo, int valor, int pos) {
        //EntradaSalida.escribir("\n COSA: " + nombreIdent + " POSICION: " + pos + "\n");
        if (pos > Constantes.CANT_MAX_IDENT-1) {
            secuazErroneo.mostrar(24, nombreIdent);
        } else {
            casaDeBean[pos] = new IdentificadorBean(nombreIdent, tipo, valor);
        }

    }

    public IdentificadorBean buscarInfo(int pos) {
        return casaDeBean[pos];
    }
}
