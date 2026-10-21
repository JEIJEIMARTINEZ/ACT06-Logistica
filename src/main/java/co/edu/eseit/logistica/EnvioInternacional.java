package co.edu.eseit.logistica;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class EnvioInternacional extends Envio {
    private final BigDecimal arancel;
    private static final BigDecimal FLETE = new BigDecimal("45000");
    public EnvioInternacional(String guia, BigDecimal pesoKg, Ruta ruta, Tarifa tarifa,
                              BigDecimal arancel) {
        super(guia, pesoKg, ruta, tarifa);
        if (arancel == null || arancel.signum() < 0
                || arancel.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Arancel no negativo con máximo dos decimales");
        }
        this.arancel = arancel;
    }
    public BigDecimal getArancel() { return arancel; }
    @Override public BigDecimal calcularCosto() {
        return getTarifa().getBase().add(getPesoKg().multiply(getTarifa().getPorKg()))
                .add(FLETE).add(arancel).setScale(2, RoundingMode.HALF_UP);
    }
}
