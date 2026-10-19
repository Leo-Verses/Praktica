package com.praktica.praktica.service;

import com.praktica.praktica.repository.ServicioRepository;
import com.praktica.praktica.model.Orden;
import com.praktica.praktica.model.Pack;
import com.praktica.praktica.model.Pago;
import com.praktica.praktica.model.Servicio;
import com.praktica.praktica.repository.OrdenRepository;
import com.praktica.praktica.repository.PackRepository;
import com.praktica.praktica.repository.PagoRepository;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductoService {
    private final OrdenRepository ordenRepository;
    private final ServicioRepository servicioRepository;
    private final PackRepository packRepository;
    private final PagoRepository pagoRepository;

    public ProductoService(OrdenRepository ordenRepository, ServicioRepository servicioRepository,
                        PackRepository packRepository, PagoRepository pagoRepository) {
        this.ordenRepository = ordenRepository;
        this.servicioRepository = servicioRepository;
        this.packRepository = packRepository;
        this.pagoRepository = pagoRepository;
    }

    public Map<String, Object> obtenerProductos() {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("servicios", servicioRepository.findByEstado("activo"));
        respuesta.put("packs", packRepository.findByEstado("activo"));
        return respuesta;
    }

    public String obtenerNombreProducto(Long idServicio, Long idPack) {
        if (idServicio != null) {
            return servicioRepository.findById(idServicio)
                    .map(s -> s.getNombre())
                    .orElse("Servicio no encontrado");
        } else if (idPack != null) {
            return packRepository.findById(idPack)
                    .map(p -> p.getNombre_pack())
                    .orElse("Pack no encontrado");
        }
        return "Sin producto";
    }


    public List<Map<String, Object>> obtenerProductosMasSolicitados() {
        List<Object[]> serviciosCount = ordenRepository.countServicios();
        List<Map<String, Object>> servicios = serviciosCount.stream().map(obj -> {
            Long idServicio = (Long) obj[0];
            Long cantidad = (Long) obj[1];
            Servicio servicio = servicioRepository.findById(idServicio).orElse(null);
            Map<String, Object> map = new HashMap<>();
            map.put("nombre", servicio != null ? servicio.getNombre() : "Servicio no encontrado");
            map.put("cantidad", cantidad != null ? cantidad : 0);
            return map;
        }).collect(Collectors.toList());

        List<Object[]> packsCount = ordenRepository.countPacks();
        List<Map<String, Object>> packs = packsCount.stream().map(obj -> {
            Long idPack = (Long) obj[0];
            Long cantidad = (Long) obj[1];
            Pack pack = packRepository.findById(idPack).orElse(null);
            Map<String, Object> map = new HashMap<>();
            map.put("nombre", pack != null ? pack.getNombre_pack() : "Pack no encontrado");
            map.put("cantidad", cantidad != null ? cantidad : 0);
            return map;
        }).collect(Collectors.toList());
        List<Map<String, Object>> productos = new ArrayList<>();
        productos.addAll(servicios);
        productos.addAll(packs);

        return productos;
    }

    public List<Map<String, Object>> obtenerProductosMasVendidos() {
        List<Map<String, Object>> productos = new ArrayList<>();
        ordenRepository.findByTipoPedido(Orden.TipoPedido.servicio).forEach(o -> {
            if (pagoRepository.existsByIdOrdenAndEstadoPago(o.getIdOrden(), Pago.EstadoPago.completado)) {
                Servicio s = servicioRepository.findById(o.getIdServicio()).orElse(null);
                if (s != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("nombre", s.getNombre());
                    map.put("cantidad", o.getCantidad());
                    productos.add(map);
                }
            }
        });

        ordenRepository.findByTipoPedido(Orden.TipoPedido.pack).forEach(o -> {
            if (pagoRepository.existsByIdOrdenAndEstadoPago(o.getIdOrden(), Pago.EstadoPago.completado)) {
                Pack p = packRepository.findById(o.getIdPack()).orElse(null);
                if (p != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("nombre", p.getNombre_pack());
                    map.put("cantidad", o.getCantidad());
                    productos.add(map);
                }
            }
        });

        Map<String, Long> agrupado = productos.stream()
            .collect(Collectors.groupingBy(m -> (String) m.get("nombre"), Collectors.summingLong(m -> (Integer) m.get("cantidad"))));
        return agrupado.entrySet().stream().map(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("nombre", e.getKey());
            map.put("cantidad", e.getValue());
            return map;
        }).collect(Collectors.toList());
    }

    public List<Servicio> listarTodosServicios() {
        return servicioRepository.findAll();
    }

    public List<Pack> listarTodosPacks() {
        return packRepository.findAll();
    }

    public List<Map<String, Object>> buscarProductos(String termino) {
        List<Map<String, Object>> resultados = new ArrayList<>();

        List<Servicio> servicios = servicioRepository.buscarPorNombreDescripcion(termino);
        List<Pack> packs = packRepository.buscarPorNombreDescripcion(termino);

        for (Servicio s : servicios) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", s.getId_servicio());
            item.put("tipo", "servicio");
            item.put("nombre", s.getNombre());
            item.put("descripcion", s.getDescripcion());
            item.put("precio", s.getPrecio_base());
            item.put("estado", s.getEstado());
            resultados.add(item);
        }

        for (Pack p : packs) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId_pack());
            item.put("tipo", "pack");
            item.put("nombre", p.getNombre_pack());
            item.put("descripcion", p.getDescripcion());
            item.put("precio", p.getPrecio_pack());
            item.put("estado", p.getEstado());
            resultados.add(item);
        }

        return resultados;
    }



    public Servicio agregarServicio(Servicio servicio) {
        return servicioRepository.save(servicio);
    }
    public Pack agregarPack(Pack pack) {
        return packRepository.save(pack);
    }


    public boolean eliminarProducto(Long id) {

        if (servicioRepository.existsById(id)) {
            servicioRepository.deleteById(id);
            return true;
        }

        if (packRepository.existsById(id)) {
            packRepository.deleteById(id);
            return true;
        }

        return false;
    }

    public int eliminarProductos(List<Long> ids) {
        int contador = 0;

        for (Long id : ids) {

            if (servicioRepository.existsById(id)) {
                servicioRepository.deleteById(id);
                contador++;
            } else if (packRepository.existsById(id)) {
                packRepository.deleteById(id);
                contador++;
            }
        }

        return contador;
    }

    public Servicio actualizarServicio(Long id, Servicio nuevo) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        servicio.setNombre(nuevo.getNombre());
        servicio.setDescripcion(nuevo.getDescripcion());
        servicio.setPrecio_base(nuevo.getPrecio_base());
        servicio.setEstado(nuevo.getEstado());

        return servicioRepository.save(servicio);
    }


    public Pack actualizarPack(Long id, Pack nuevo) {
        Pack pack = packRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pack no encontrado"));

        pack.setNombre_pack(nuevo.getNombre_pack());
        pack.setDescripcion(nuevo.getDescripcion());
        pack.setPrecio_pack(nuevo.getPrecio_pack());
        pack.setEstado(nuevo.getEstado());

        return packRepository.save(pack);
    }
}