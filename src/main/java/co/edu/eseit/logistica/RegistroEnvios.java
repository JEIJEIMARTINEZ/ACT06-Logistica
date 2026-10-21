package co.edu.eseit.logistica;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Asociación con envíos que existen independientemente del registro. R5 y R7. */
public final class RegistroEnvios {
    private final Map<String, Envio> envios = new LinkedHashMap<>();
    public void registrar(Envio envio) {
        validarRegistro(envio);
        envios.put(envio.getGuia(), envio);
    }
    private void validarRegistro(Envio envio) {
        if (envio == null) throw new IllegalArgumentException("Envío obligatorio");
        if (envios.containsKey(envio.getGuia())) {
            throw new IllegalArgumentException("Guía duplicada: " + envio.getGuia());
        }
    }
    public List<Envio> listar() { return List.copyOf(envios.values()); }
    public BigDecimal calcularTotal() {
        return envios.values().stream().map(Envio::calcularCosto)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
    }
}
