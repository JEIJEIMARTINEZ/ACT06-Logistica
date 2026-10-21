package co.edu.eseit.logistica;

/** Ruta inmutable compartible entre envíos. R3. */
public final class Ruta {
    private final String origen;
    private final String destino;
    public Ruta(String origen, String destino) {
        if (origen == null || destino == null || origen.isBlank() || destino.isBlank()
                || origen.strip().equalsIgnoreCase(destino.strip())) {
            throw new IllegalArgumentException("Origen y destino deben ser distintos y no vacíos");
        }
        this.origen = origen.strip();
        this.destino = destino.strip();
    }
    public String getOrigen() { return origen; }
    public String getDestino() { return destino; }
}
