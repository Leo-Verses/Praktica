package com.praktica.praktica.controller;

import com.praktica.praktica.model.Pack;
import com.praktica.praktica.model.Servicio;
import com.praktica.praktica.service.ProductoService;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public Map<String, Object> listarProductos() {
        Map<String, Object> response = new HashMap<>();
        try {
            response = productoService.obtenerProductos();
            response.put("status", "ok");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("mensaje", e.getMessage());
        }
        return response;
    }

    @GetMapping("/mas-pedidos")
    public Map<String, Object> listarProductosMasPedidos() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> productos = productoService.obtenerProductosMasSolicitados();
            response.put("status", "ok");
            response.put("productos", productos);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("mensaje", e.getMessage());
        }
        return response;
    }

    @GetMapping("/mas-vendidos")
    public List<Map<String, Object>> productosMasVendidos() {
        return productoService.obtenerProductosMasVendidos();
    }

    @GetMapping("/inventario/servicios")
    public Map<String, Object> listarTodosServicios() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Servicio> servicios = productoService.listarTodosServicios();
            response.put("status", "ok");
            response.put("servicios", servicios);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("mensaje", e.getMessage());
        }
        return response;
    }

    @GetMapping("/inventario/packs")
    public Map<String, Object> listarTodosPacks() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Pack> packs = productoService.listarTodosPacks();
            response.put("status", "ok");
            response.put("packs", packs);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("mensaje", e.getMessage());
        }
        return response;
    }

    @GetMapping("/buscar")
    public Map<String, Object> buscarProductos(@RequestParam("q") String termino) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> resultados = productoService.buscarProductos(termino);
            response.put("status", "ok");
            response.put("resultados", resultados);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("mensaje", e.getMessage());
        }
        return response;
    }

    @PostMapping("/servicio")
    public Map<String, Object> agregarServicio(@RequestBody Servicio servicio) {
        Map<String, Object> response = new HashMap<>();
        try {
            Servicio nuevo = productoService.agregarServicio(servicio);
            response.put("status", "ok");
            response.put("servicio", nuevo);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("mensaje", e.getMessage());
        }
        return response;
    }

    @PostMapping("/pack")
    public Map<String, Object> agregarPack(@RequestBody Pack pack) {
        Map<String, Object> response = new HashMap<>();
        try {
            Pack nuevo = productoService.agregarPack(pack);
            response.put("status", "ok");
            response.put("pack", nuevo);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("mensaje", e.getMessage());
        }
        return response;
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> eliminarProducto(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean eliminado = productoService.eliminarProducto(id);

            if (!eliminado) {
                response.put("status", "error");
                response.put("mensaje", "No existe el producto");
                return response;
            }

            response.put("status", "ok");
            response.put("mensaje", "Producto eliminado");

        } catch (Exception e) {
            response.put("status", "error");
            response.put("mensaje", e.getMessage());
        }
        return response;
    }

    @DeleteMapping("/eliminar-multiple")
    public Map<String, Object> eliminarProductos(@RequestBody List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            int eliminados = productoService.eliminarProductos(ids);
            response.put("status", "ok");
            response.put("eliminados", eliminados);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("mensaje", e.getMessage());
        }
        return response;
    }

    @PutMapping("/servicio/{id}")
    public Map<String, Object> actualizarServicio(@PathVariable Long id, @RequestBody Servicio servicio) {
        Map<String, Object> response = new HashMap<>();
        try {
            Servicio actualizado = productoService.actualizarServicio(id, servicio);
            response.put("status", "ok");
            response.put("servicio", actualizado);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("mensaje", e.getMessage());
        }
        return response;
    }

    @PutMapping("/pack/{id}")
    public Map<String, Object> actualizarPack(@PathVariable Long id, @RequestBody Pack pack) {
        Map<String, Object> response = new HashMap<>();
        try {
            Pack actualizado = productoService.actualizarPack(id, pack);
            response.put("status", "ok");
            response.put("pack", actualizado);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("mensaje", e.getMessage());
        }
        return response;
    }
}