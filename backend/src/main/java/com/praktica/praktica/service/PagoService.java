package com.praktica.praktica.service;

import com.praktica.praktica.model.Orden;
import com.praktica.praktica.model.Pack;
import com.praktica.praktica.model.Pago;
import com.praktica.praktica.model.Servicio;
import com.praktica.praktica.repository.PackRepository;
import com.praktica.praktica.repository.PagoRepository;
import com.praktica.praktica.repository.ServicioRepository;
import com.stripe.model.checkout.Session;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;

    public PagoService(PagoRepository pagoRepository) 
                {
                    this.pagoRepository = pagoRepository;
                }

    public void registrarPago(Session session) {
        Pago pago = new Pago();
        pago.setIdOrden(Integer.parseInt(session.getClientReferenceId()));
        pago.setMonto(BigDecimal.valueOf(session.getAmountTotal() / 100.0));

        String metodoStripe = session.getPaymentMethodTypes().get(0).toLowerCase();
        switch (metodoStripe) {
            case "card":
                pago.setMetodoPago(Pago.MetodoPago.tarjeta);
                break;
            case "bank_transfer":
                pago.setMetodoPago(Pago.MetodoPago.transferencia);
                break;
            default:
                pago.setMetodoPago(Pago.MetodoPago.otro);
                break;
        }

        pago.setFechaPago(LocalDateTime.ofInstant(
                Instant.ofEpochSecond(session.getCreated()),
                ZoneId.systemDefault()
        ));
        pago.setEstadoPago(Pago.EstadoPago.completado);
        pago.setComprobanteUrl(session.getUrl());

        pagoRepository.save(pago);
    }

    public BigDecimal calcularGananciaTotal() {
        List<Pago> pagos = pagoRepository.findAll();
        BigDecimal total = BigDecimal.ZERO;
        for (Pago p : pagos) {
            if (p.getMonto() != null) {
                total = total.add(p.getMonto());
            }
        }
        return total;
    }

    public BigDecimal calcularGananciaDiaria() {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.atTime(LocalTime.MAX);

        List<Pago> pagosHoy = pagoRepository.findAll();
        BigDecimal total = BigDecimal.ZERO;

        for (Pago p : pagosHoy) {
            if (p.getMonto() != null && p.getFechaPago() != null) {
                if (!p.getFechaPago().isBefore(inicio) && !p.getFechaPago().isAfter(fin)) {
                    total = total.add(p.getMonto());
                }
            }
        }
        return total;
    }

    public List<Map<String, Object>> obtenerVentasPorMes() {
        List<Object[]> resultados = pagoRepository.ventasPorMes();
        return resultados.stream()
                .map(r -> Map.of(
                        "mes", r[0],
                        "total", r[1]
                ))
                .collect(Collectors.toList());
    }

    public void registrarPagoEfectivo(Integer idOrden, BigDecimal monto) {
        Pago pago = new Pago();
        pago.setIdOrden(idOrden);
        pago.setMonto(monto);
        pago.setMetodoPago(Pago.MetodoPago.efectivo);
        pago.setEstadoPago(Pago.EstadoPago.completado);
        pago.setComprobanteUrl(null);
        pago.setFechaPago(LocalDateTime.now());
        pagoRepository.save(pago);
    }

    public List<Map<String, Object>> obtenerGananciasPorDiaMesActual() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());

        return obtenerGananciasPorDiaEntreFechas(inicioMes, finMes);
    }

    public List<Map<String, Object>> obtenerGananciasPorDiaMesAnterior() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMesAnterior = hoy.minusMonths(1).withDayOfMonth(1);
        LocalDate finMesAnterior = inicioMesAnterior.withDayOfMonth(inicioMesAnterior.lengthOfMonth());

        return obtenerGananciasPorDiaEntreFechas(inicioMesAnterior, finMesAnterior);
    }

    private List<Map<String, Object>> obtenerGananciasPorDiaEntreFechas(LocalDate inicio, LocalDate fin) {
        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime finDateTime = fin.atTime(LocalTime.MAX);

        List<Pago> pagos = pagoRepository.findAll(); // Podrías optimizar filtrando en DB
        Map<LocalDate, BigDecimal> gananciasPorDia = new HashMap<>();

        for (Pago p : pagos) {
            if (p.getFechaPago() != null && !p.getFechaPago().isBefore(inicioDateTime) && !p.getFechaPago().isAfter(finDateTime)) {
                LocalDate dia = p.getFechaPago().toLocalDate();
                gananciasPorDia.put(dia, gananciasPorDia.getOrDefault(dia, BigDecimal.ZERO).add(p.getMonto()));
            }
        }

        return gananciasPorDia.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Map<String, Object> mapa = new HashMap<>();
                    mapa.put("dia", entry.getKey().toString());
                    mapa.put("ganancia", entry.getValue());
                    return mapa;
                })
                .collect(Collectors.toList());

    }

    public List<Map<String, Object>> obtenerPagosConDetalles(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Pago> pagos = pagoRepository.findPagosCompletadosConOrden(fechaInicio, fechaFin);

        return pagos.stream().map(pago -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idPago", pago.getIdPago());
            map.put("fechaPago", pago.getFechaPago());
            map.put("monto", pago.getMonto());
            map.put("metodoPago", pago.getMetodoPago());
            map.put("estadoPago", pago.getEstadoPago());
            map.put("comprobanteUrl", pago.getComprobanteUrl());

            Orden orden = pago.getOrden();
            if (orden != null) {
                map.put("idOrden", orden.getIdOrden());
                map.put("nombreCliente", orden.getNombreCliente());
                map.put("dni", orden.getDniCePasaporte());
                map.put("telefono", orden.getTelefono());
                map.put("tipoPedido", orden.getTipoPedido());
                map.put("cantidad", orden.getCantidad());
                map.put("fechaEntrega", orden.getFechaEntregaEstimada());
                map.put("descripcion", orden.getDescripcion());
                map.put("estadoOrden", orden.getEstado());
            }

            return map;
        }).toList();
    }


}
