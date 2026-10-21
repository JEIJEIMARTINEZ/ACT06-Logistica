package co.edu.eseit.logistica;

/** Parte inmutable creada exclusivamente por Envio dentro del paquete. R7. */
public final class EventoSeguimiento {
    private final EstadoEnvio estado;
    EventoSeguimiento(EstadoEnvio estado) { this.estado = estado; }
    public EstadoEnvio getEstado() { return estado; }
}
