package co.edu.eseit.logistica;
import java.math.BigDecimal;
public final class Main {
    public static void main(String[] args) {
        Tarifa tarifa = new Tarifa(new BigDecimal("10000"), new BigDecimal("2000"));
        RegistroEnvios registro = new RegistroEnvios();
        registro.registrar(new EnvioNacional("COL-101", new BigDecimal("2"),
                new Ruta("Bogotá", "Medellín"), tarifa));
        registro.registrar(new EnvioInternacional("INT-901", new BigDecimal("2"),
                new Ruta("Bogotá", "Lima"), tarifa, new BigDecimal("5000")));
        for (Envio envio : registro.listar()) {
            System.out.println(envio + " COP " + envio.calcularCosto());
        }
        System.out.println("Total COP " + registro.calcularTotal());
    }
}
