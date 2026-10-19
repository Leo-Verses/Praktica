package com.praktica.praktica.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    @Column(name = "id_orden", nullable = false)
    private Integer idOrden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orden", insertable = false, updatable = false)
    private Orden orden;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto; // <-- CAMBIO: BigDecimal

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = true)
    private MetodoPago metodoPago = MetodoPago.efectivo;

    @Column(name = "fecha_pago", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", nullable = true)
    private EstadoPago estadoPago = EstadoPago.pendiente;

    @Column(name = "comprobante_url", length = 255, nullable = true)
    private String comprobanteUrl;

    // Getters y Setters
    public Integer getIdPago() { return idPago; }
    public void setIdPago(Integer idPago) { this.idPago = idPago; }

    public Integer getIdOrden() { return idOrden; }
    public void setIdOrden(Integer idOrden) { this.idOrden = idOrden; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

    public EstadoPago getEstadoPago() { return estadoPago; }
    public void setEstadoPago(EstadoPago estadoPago) { this.estadoPago = estadoPago; }

    public String getComprobanteUrl() { return comprobanteUrl; }
    public void setComprobanteUrl(String comprobanteUrl) { this.comprobanteUrl = comprobanteUrl; }

    // Enumeraciones
    public enum MetodoPago {
        tarjeta, transferencia, efectivo, otro
    }

    public enum EstadoPago {
        pendiente, parcial, completado, fallido
    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }
}
