package co.edu.eseit.logistica;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Contrato común: costo positivo en COP, cálculo puro y misma secuencia de estados. */
public abstract class Envio {
    private final String guia;
    private final BigDecimal pesoKg;
    private final Ruta ruta;
    private final Tarifa tarifa;
    private EstadoEnvio estado = EstadoEnvio.CREADO;
    private final List<EventoSeguimiento> historial = new ArrayList<>();

    protected Envio(String guia, BigDecimal pesoKg, Ruta ruta, Tarifa tarifa) {
        if (guia == null || guia.isBlank() || pesoKg == null || pesoKg.signum() <= 0
                || pesoKg.compareTo(new BigDecimal("50")) > 0 || ruta == null || tarifa == null) {
            throw new IllegalArgumentException("Guía y referencias obligatorias; peso en (0, 50] kg");
        }
        this.guia = guia.strip();
        this.pesoKg = pesoKg;
        this.ruta = ruta;
        this.tarifa = tarifa;
        historial.add(new EventoSeguimiento(estado));
    }
    public String getGuia() { return guia; }
    public BigDecimal getPesoKg() { return pesoKg; }
    public Ruta getRuta() { return ruta; }
    public Tarifa getTarifa() { return tarifa; }
    public EstadoEnvio getEstado() { return estado; }
    public final List<EventoSeguimiento> getHistorial() { return List.copyOf(historial); }
    public abstract BigDecimal calcularCosto();
    public final void avanzarEstado(EstadoEnvio siguiente) {
        boolean valido = (estado == EstadoEnvio.CREADO && siguiente == EstadoEnvio.EN_TRANSITO)
                || (estado == EstadoEnvio.EN_TRANSITO && siguiente == EstadoEnvio.ENTREGADO);
        if (!valido) throw new IllegalStateException("Transición no permitida");
        estado = siguiente;
        historial.add(new EventoSeguimiento(estado));
    }
    @Override public final boolean equals(Object otro) {
        return otro instanceof Envio envio && guia.equals(envio.guia);
    }
    @Override public final int hashCode() { return Objects.hash(guia); }
    @Override public String toString() { return guia + " " + estado; }
}
