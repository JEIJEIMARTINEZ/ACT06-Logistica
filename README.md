# Actividad 06 — Diseño probado de una solución orientada a objetos

Prototipo académico de LogiTrans para registrar y cotizar envíos nacionales e internacionales, con seguimiento y pruebas de contrato.

## Integrantes

- Jeison Martínez Ramírez
- Mayerly Andrea Monroy Malagón
- Valerie Pamela Suárez Corredor

Distribución propuesta, pendiente de validar por el equipo: Jeison, implementación y ejecución; Mayerly, reglas y UML; Valerie, pruebas y trazabilidad. Esta distribución no acredita aportes ya realizados.

## Requisitos

- JDK 21 (se verificó con Amazon Corretto 21.0.12).
- Apache Maven 3.9.9 (o una versión compatible de la serie 3.9).
- Internet para descargar dependencias en la primera ejecución.
- JUnit Jupiter 5.11.4 y plugins fijados en pom.xml; Maven los obtiene automáticamente.

Compruebe que `java -version` y `mvn -version` usan JDK 21. Configure JAVA_HOME y PATH si el IDE o la terminal seleccionan otra versión.

## Compilar y ejecutar las pruebas

Abra una terminal dentro de la carpeta que contiene pom.xml:

```bash
mvn -B -ntp clean test
```

Resultado esperado: 31 casos ejecutados, cero fallos, cero errores, cero omitidos y BUILD SUCCESS. Los reportes nuevos están en target/surefire-reports. Si se agregan pruebas, el conteo puede cambiar.

## Ejecutar la demostración

Después de compilar con el comando anterior:

```bash
java -cp target/classes co.edu.eseit.logistica.Main
```

Salida esperada:

```text
COL-101 CREADO COP 14000.00
INT-901 CREADO COP 64000.00
Total COP 78000.00
```

## Abrir en un IDE

En IntelliJ, elija Open y seleccione pom.xml; configure Project SDK 21 y espere la importación de Maven. En Eclipse use File > Import > Existing Maven Projects y configure un JDK 21. Ejecute Main como aplicación Java o LogisticaTest como prueba JUnit. No cree paquetes adicionales ni borre `package co.edu.eseit.logistica;`.

## Estructura

- src/main/java/co/edu/eseit/logistica: siete clases de dominio, EstadoEnvio y Main.
- src/test/java/co/edu/eseit/logistica/LogisticaTest.java: suite JUnit.
- docs/modelo.puml y modelo.png: UML final editable y representación visual.
- docs/modelo_antes.puml y modelo_intermedio.puml: evolución del modelo.
- docs/trazabilidad.csv: matriz editable R1–R8 con los nombres exactos de las pruebas.
- docs/refactor_1.diff y refactor_2.diff: cambios reales por etapa.
- docs/ACT06_DisenoProbado_EquipoLogistica.pdf: informe técnico (agregado al paquete final).
- evidencias: resultados de línea base, dos refactorizaciones, defecto deliberado y revisión final; archivos XML JUnit y salida de Main.

[Consultar informe técnico](docs/ACT06_DisenoProbado_EquipoLogistica.pdf)

## Reglas del dominio

- R1: guía normalizada no vacía, peso en (0, 50] kg, ruta y tarifa obligatorias.
- R2: tarifas positivas con hasta dos decimales; nacional = base + peso × precio/kg; internacional añade flete de 45000 COP y arancel. Redondeo final HALF_UP a dos decimales.
- R3: origen y destino no vacíos ni equivalentes al ignorar mayúsculas y espacios exteriores.
- R4: arancel obligatorio no negativo con hasta dos decimales.
- R5: unicidad de guía dentro de cada registro; sensible a mayúsculas.
- R6: CREADO -> EN_TRANSITO -> ENTREGADO, sin saltos, repeticiones ni retrocesos.
- R7: historial compuesto por 1–3 eventos inmutables, generado internamente; copias no modificables de listas.
- R8: contrato común puro, determinista, costo positivo en COP; total polimórfico e identidad estable por guía.

Los precios son supuestos académicos, no precios comerciales. Una lista no modificable de envíos evita insertar o borrar elementos desde fuera del registro, pero los envíos mantienen su operación pública de avance de estado.

## Refactorizaciones y evidencias

1. Línea base: 01_linea_base.txt y .xml; 31 casos aprobados.
2. Extraer cálculo común a Tarifa.calcularComponente: 02_refactor_tarifa.txt y .xml; mismos 31 casos aprobados.
3. Extraer RegistroEnvios.validarRegistro: 03_refactor_registro.txt y .xml; mismos 31 casos aprobados.
4. Cambiar temporalmente flete de 45000 a 45001: 04_mutacion_detectada.txt termina en BUILD FAILURE. Se conservó el parche como mutacion_flete.diff. El defecto no está en la versión final.
5. Restaurar y ejecutar clean test: 05_revision_limpia.txt y .xml; 31 casos aprobados.

La prueba negativa detecta un defecto específico, no mide un porcentaje de mutaciones ni de cobertura.

## Recuperar la historia Git

El paquete general incluye ACT06_Logistica.bundle fuera de esta carpeta. Para recuperar el repositorio con sus commits:

```bash
git clone ACT06_Logistica.bundle ACT06_Logistica_historia
cd ACT06_Logistica_historia
git log --oneline
mvn -B -ntp clean test
```

La carpeta del proyecto en el ZIP contiene los mismos archivos finales y se puede abrir directamente, aunque no incluye .git. El bundle conserva la historia local real y una identidad técnica genérica para no atribuir commits a integrantes individuales.

## Publicar y completar la entrega

1. Cree un repositorio vacío en la cuenta GitHub elegida por el equipo, sin README inicial.
2. Recupere la historia con el bundle como se indica arriba.
3. Cambie el remoto origin, que apunta al bundle, por la URL real del repositorio vacío:

```bash
git remote set-url origin URL_REAL_DEL_REPOSITORIO
git push -u origin main
```

4. Abra el enlace remoto y verifique código, docs, pruebas y evidencias; otorgue al docente acceso si el repositorio es privado.
5. Pegue la URL real en la portada del Word, valide nombres y roles y vuelva a exportar el PDF como ACT06_DisenoProbado_EquipoLogistica.pdf. Reemplace también el PDF de docs y actualice el repositorio.
6. Verifique que el enlace funciona para el evaluador. Realice una única entrega grupal del PDF y el enlace en la plataforma. Un ZIP o un bundle no reemplaza el enlace solicitado.

## Limitaciones

Sin persistencia, usuarios ni concurrencia. La unicidad es local al registro. No se verifican fronteras de las rutas. El historial no incluye fechas ni responsables. La propiedad del evento se protege en la API pública y por la frontera del paquete; no se promete impedir modificaciones deliberadas hechas desde el mismo paquete.

## Fuentes

- ESEIT. Guía didáctica de la actividad 06.
- ESEIT. TG-04, Modelado UML, pruebas unitarias y refactorización de jerarquías, fragmento suministrado de pp. 28–33.
- JUnit Team. JUnit 5 user guide, versión 5.11.4: https://docs.junit.org/5.11.4/user-guide/
- OMG. UML 2.5.1: https://www.omg.org/spec/UML/2.5.1
- Oracle. Java Language Specification, SE 21, capítulo 8: https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html
