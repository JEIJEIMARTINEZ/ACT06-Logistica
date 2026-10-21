package co.edu.eseit.logistica;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class EnvioNacional extends Envio {
    public EnvioNacional(String guia, BigDecimal pesoKg, Ruta ruta, Tarifa tarifa) {
        super(guia, pesoKg, ruta, tarifa);
    }
    @Override public BigDecimal calcularCosto() {
        return getTarifa().calcularComponente(getPesoKg())
                .setScale(2, RoundingMode.HALF_UP);
    }
}
