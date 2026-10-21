package co.edu.eseit.logistica;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import java.math.BigDecimal;
import java.util.List;
import java.util.HashSet;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

class LogisticaTest {
    static BigDecimal d(String valor) { return new BigDecimal(valor); }
    static Tarifa tarifa() { return new Tarifa(d("10000"), d("2000")); }
    static Ruta ruta() { return new Ruta("Bogotá", "Lima"); }
    static Envio nacional(String guia) { return new EnvioNacional(guia, d("2"), ruta(), tarifa()); }
    static Envio internacional(String guia) {
        return new EnvioInternacional(guia, d("2"), ruta(), tarifa(), d("5000"));
    }
    static Stream<Arguments> subtipos() {
        return Stream.of(Arguments.of(nacional("N-1"), d("14000.00")),
                Arguments.of(internacional("I-1"), d("64000.00")));
    }
    @ParameterizedTest @MethodSource("subtipos")
    void contratoDelSupertipoConservaCostoEstadoEHistorial(Envio envio, BigDecimal esperado) {
        var historial = envio.getHistorial();
        assertAll(() -> assertEquals(esperado, envio.calcularCosto()),
                () -> assertEquals(esperado, envio.calcularCosto()),
                () -> assertTrue(envio.calcularCosto().signum() > 0),
                () -> assertEquals(EstadoEnvio.CREADO, envio.getEstado()),
                () -> assertEquals(historial, envio.getHistorial()));
        envio.avanzarEstado(EstadoEnvio.EN_TRANSITO);
        envio.avanzarEstado(EstadoEnvio.ENTREGADO);
        assertEquals(EstadoEnvio.ENTREGADO, envio.getEstado());
        assertEquals(esperado, envio.calcularCosto());
        assertEquals(3, envio.getHistorial().size());
        assertThrows(IllegalStateException.class, () -> envio.avanzarEstado(EstadoEnvio.CREADO));
    }
    @ParameterizedTest @ValueSource(strings={"0", "-1", "50.001"})
    void rechazaPesoFueraDelIntervaloEnAmbosSubtipos(String peso) {
        assertThrows(IllegalArgumentException.class, () -> new EnvioNacional("N", d(peso), ruta(), tarifa()));
        assertThrows(IllegalArgumentException.class, () -> new EnvioInternacional("I", d(peso), ruta(), tarifa(), d("0")));
    }
    @ParameterizedTest @ValueSource(strings={"0.001", "50"})
    void aceptaPesosValidosCercanosALosLimites(String peso) {
        Envio n = new EnvioNacional("N", d(peso), ruta(), tarifa());
        Envio i = new EnvioInternacional("I", d(peso), ruta(), tarifa(), d("0"));
        assertEquals(d("10000").add(d(peso).multiply(d("2000"))).setScale(2), n.calcularCosto());
        assertEquals(n.calcularCosto().add(d("45000")), i.calcularCosto());
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings={"   "})
    void rechazaGuiaAusenteEnAmbosSubtipos(String guia) {
        assertThrows(IllegalArgumentException.class, () -> new EnvioNacional(guia, d("1"), ruta(), tarifa()));
        assertThrows(IllegalArgumentException.class, () -> new EnvioInternacional(guia, d("1"), ruta(), tarifa(), d("0")));
    }
    @Test void rechazaReferenciasObligatoriasNulasEnAmbosSubtipos() {
        for (int tipo = 0; tipo < 2; tipo++) {
            final boolean internacional = tipo == 1;
            assertThrows(IllegalArgumentException.class, () -> crear(internacional, null, ruta(), tarifa()));
            assertThrows(IllegalArgumentException.class, () -> crear(internacional, d("1"), null, tarifa()));
            assertThrows(IllegalArgumentException.class, () -> crear(internacional, d("1"), ruta(), null));
        }
    }
    static Envio crear(boolean internacional, BigDecimal peso, Ruta ruta, Tarifa tarifa) {
        return internacional ? new EnvioInternacional("G", peso, ruta, tarifa, d("0"))
                : new EnvioNacional("G", peso, ruta, tarifa);
    }
    @ParameterizedTest @ValueSource(strings={"0", "-0.01", "0.001"})
    void rechazaTarifasNoPositivasOPrecisionExcesiva(String valor) {
        assertThrows(IllegalArgumentException.class, () -> new Tarifa(d(valor), d("1")));
        assertThrows(IllegalArgumentException.class, () -> new Tarifa(d("1"), d(valor)));
    }
    @Test void rechazaTarifasNulas() {
        assertThrows(IllegalArgumentException.class, () -> new Tarifa(null, d("1")));
        assertThrows(IllegalArgumentException.class, () -> new Tarifa(d("1"), null));
    }
    @Test void aceptaTarifaMinimaYRedondeaUnaSolaVez() {
        Tarifa t = new Tarifa(d("0.01"), d("0.01"));
        Envio n = new EnvioNacional("N", d("0.5"), ruta(), t);
        Envio i = new EnvioInternacional("I", d("0.5"), ruta(), t, d("0"));
        assertEquals(d("0.02"), n.calcularCosto());
        assertEquals(d("45000.02"), i.calcularCosto());
    }
    @Test void rutaNormalizaEspaciosYPermiteCompartirReferencias() {
        Ruta r = new Ruta(" Bogotá ", " Medellín ");
        Envio a = new EnvioNacional("A", d("1"), r, tarifa());
        Envio b = new EnvioNacional("B", d("1"), r, tarifa());
        assertEquals("Bogotá", r.getOrigen()); assertEquals("Medellín", r.getDestino());
        assertSame(a.getRuta(), b.getRuta());
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings={"  "})
    void rechazaExtremosDeRutaAusentes(String valor) {
        assertThrows(IllegalArgumentException.class, () -> new Ruta(valor, "Lima"));
        assertThrows(IllegalArgumentException.class, () -> new Ruta("Bogotá", valor));
    }
    @Test void rechazaRutaConOrigenYDestinoEquivalentes() {
        assertThrows(IllegalArgumentException.class, () -> new Ruta(" Bogotá ", "bogotá"));
    }
    @ParameterizedTest @ValueSource(strings={"-0.01", "-1", "0.001"})
    void rechazaArancelNegativoOPrecisionExcesiva(String arancel) {
        assertThrows(IllegalArgumentException.class, () -> new EnvioInternacional("I", d("1"), ruta(), tarifa(), d(arancel)));
    }
    @Test void aceptaArancelCeroYRechazaNulo() {
        Envio e = new EnvioInternacional("I", d("2"), ruta(), tarifa(), d("0"));
        assertEquals(d("59000.00"), e.calcularCosto());
        assertThrows(IllegalArgumentException.class, () -> new EnvioInternacional("I", d("2"), ruta(), tarifa(), null));
    }
    @Test void registraEnviosDistintosYRechazaGuiaDuplicadaSinAlterarRegistro() {
        RegistroEnvios r = new RegistroEnvios(); Envio n = nacional(" G-1 ");
        r.registrar(n); r.registrar(internacional("G-2"));
        assertThrows(IllegalArgumentException.class, () -> r.registrar(internacional("G-1")));
        assertEquals(List.of(n, internacional("G-2")), r.listar());
        assertEquals(d("78000.00"), r.calcularTotal());
    }
    @Test void rechazaRegistroNuloYConservaRegistroVacio() {
        RegistroEnvios r = new RegistroEnvios();
        assertThrows(IllegalArgumentException.class, () -> r.registrar(null));
        assertTrue(r.listar().isEmpty()); assertEquals(d("0.00"), r.calcularTotal());
    }
    @Test void rechazaSaltosRepeticionesRetrocesosYNulosSinAlterarHistorial() {
        Envio e = nacional("N");
        for (EstadoEnvio estado : new EstadoEnvio[]{null, EstadoEnvio.CREADO, EstadoEnvio.ENTREGADO}) {
            assertThrows(IllegalStateException.class, () -> e.avanzarEstado(estado));
        }
        assertEquals(1, e.getHistorial().size());
        e.avanzarEstado(EstadoEnvio.EN_TRANSITO);
        assertThrows(IllegalStateException.class, () -> e.avanzarEstado(EstadoEnvio.EN_TRANSITO));
        assertThrows(IllegalStateException.class, () -> e.avanzarEstado(EstadoEnvio.CREADO));
        assertEquals(2, e.getHistorial().size());
        e.avanzarEstado(EstadoEnvio.ENTREGADO);
        assertThrows(IllegalStateException.class, () -> e.avanzarEstado(EstadoEnvio.ENTREGADO));
        assertEquals(3, e.getHistorial().size());
    }
    @Test void protegeColeccionesYPropiedadDelHistorial() {
        Envio a = nacional("A"), b = nacional("B");
        var copia = a.getHistorial();
        assertThrows(UnsupportedOperationException.class, copia::clear);
        assertNotSame(a.getHistorial().getFirst(), b.getHistorial().getFirst());
        a.avanzarEstado(EstadoEnvio.EN_TRANSITO);
        assertEquals(1, copia.size()); assertEquals(1, b.getHistorial().size());
        assertEquals(EstadoEnvio.EN_TRANSITO, a.getHistorial().getLast().getEstado());
        RegistroEnvios r = new RegistroEnvios(); r.registrar(a);
        var lista = r.listar();
        assertThrows(UnsupportedOperationException.class, () -> lista.add(b));
        r.registrar(b); assertEquals(1, lista.size()); assertEquals(2, r.listar().size());
    }
    @Test void igualdadYHashPermanecenEstablesAlCambiarEstado() {
        Envio a = nacional(" G "), b = internacional("G"), c = nacional("G");
        assertEquals(a, a); assertEquals(a, b); assertEquals(b, a);
        assertEquals(b, c); assertEquals(a, c); assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, null); assertNotEquals(a, "G"); assertNotEquals(a, nacional("OTRA"));
        var conjunto = new HashSet<Envio>(); conjunto.add(a);
        a.avanzarEstado(EstadoEnvio.EN_TRANSITO);
        assertTrue(conjunto.contains(b)); assertEquals(a, b);
        assertEquals("G EN_TRANSITO", a.toString());
    }
    @Test void registroVacioYMixtoCalculanTotalMedianteSupertipo() {
        RegistroEnvios r = new RegistroEnvios(); assertEquals(d("0.00"), r.calcularTotal());
        r.registrar(nacional("N")); r.registrar(internacional("I"));
        assertEquals(d("78000.00"), r.calcularTotal());
        assertEquals(2, r.listar().size());
    }
}
