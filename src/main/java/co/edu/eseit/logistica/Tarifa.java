package co.edu.eseit.logistica;
import java.math.BigDecimal;

/** Valores monetarios en COP; se admite hasta dos decimales. R2. */
public final class Tarifa {
    private final BigDecimal base;
    private final BigDecimal porKg;
    public Tarifa(BigDecimal base, BigDecimal porKg) {
        if (base == null || porKg == null || base.signum() <= 0 || porKg.signum() <= 0
                || base.stripTrailingZeros().scale() > 2 || porKg.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("Tarifas positivas con máximo dos decimales");
        }
        this.base = base;
        this.porKg = porKg;
    }
    public BigDecimal getBase() { return base; }
    public BigDecimal getPorKg() { return porKg; }
}
