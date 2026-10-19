package com.praktica.praktica.controller;

import com.praktica.praktica.service.PagoService;
import com.praktica.praktica.service.StripeService;
import com.stripe.model.checkout.Session;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final StripeService stripeService;
    private final PagoService pagoService;

    public PagoController(StripeService stripeService, PagoService pagoService) {
        this.stripeService = stripeService;
        this.pagoService = pagoService;
    }

    @PostMapping("/generar-link")
    public ResponseEntity<?> generarLink(@RequestBody Map<String, Object> data) {
        try {
            String descripcion = (String) data.get("descripcion");
            Double monto = Double.parseDouble(data.get("monto").toString());
            String moneda = (String) data.get("moneda");
            Long idOrden = Long.parseLong(data.get("idOrden").toString());

            Session session = stripeService.crearLinkPago(descripcion, monto, moneda, idOrden);

            return ResponseEntity.ok(Map.of("urlPago", session.getUrl()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se pudo generar el link de pago: " + e.getMessage()));
        }
    }

    @GetMapping("/ganancia-total")
    public ResponseEntity<?> obtenerGananciaTotal() {
        try {
            BigDecimal totalGanancia = pagoService.calcularGananciaTotal();
            return ResponseEntity.ok(Map.of("gananciaTotal", totalGanancia));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al calcular la ganancia total"));
        }
    }

    @GetMapping("/ganancia-diaria")
    public ResponseEntity<?> obtenerGananciaDiaria() {
        try {
            BigDecimal gananciaHoy = pagoService.calcularGananciaDiaria();
            return ResponseEntity.ok(Map.of("gananciaDiaria", gananciaHoy));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al calcular la ganancia diaria"));
        }
    }

    @GetMapping("/ventas-mensuales")
    public ResponseEntity<?> obtenerVentasMensuales() {
        try {
            return ResponseEntity.ok(pagoService.obtenerVentasPorMes());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al obtener ventas mensuales"));
        }
    }

    @PostMapping("/efectivo")
    public ResponseEntity<?> registrarPagoEfectivo(@RequestBody Map<String, Object> data) {
        try {
            Integer idOrden = Integer.parseInt(data.get("id_orden").toString());
            BigDecimal monto = new BigDecimal(data.get("monto").toString());

            pagoService.registrarPagoEfectivo(idOrden, monto);

            return ResponseEntity.ok(Map.of("mensaje", "Pago en efectivo registrado correctamente"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se pudo registrar el pago en efectivo: " + e.getMessage()));
        }
    }

    @GetMapping("/ganancias-ultimos-meses")
    public ResponseEntity<?> obtenerGananciasUltimosMeses() {
        try {
            // Mes actual
            List<Map<String, Object>> gananciasMesActual = pagoService.obtenerGananciasPorDiaMesActual();

            // Mes anterior
            List<Map<String, Object>> gananciasMesAnterior = pagoService.obtenerGananciasPorDiaMesAnterior();

            Map<String, Object> response = Map.of(
                "mesActual", gananciasMesActual,
                "mesAnterior", gananciasMesAnterior
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Error al obtener ganancias: " + e.getMessage()));
        }
    }

    @GetMapping("/ordenes-pagadas-detalle")
    public ResponseEntity<?> obtenerOrdenesPagadasConDetalle(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        try {
            LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
            LocalDateTime fin = fechaFin != null ? fechaFin.atTime(23, 59, 59) : null;

            List<Map<String, Object>> ordenes = pagoService.obtenerPagosConDetalles(inicio, fin);
            return ResponseEntity.ok(ordenes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al obtener las órdenes pagadas con detalle: " + e.getMessage()));
        }
    }
}
