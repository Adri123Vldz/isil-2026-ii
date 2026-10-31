package com.isil.edu.pe.spedidos.controller;

import com.isil.edu.pe.spedidos.service.PedidoReporteService;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PedidoReporteWebController {

  private final PedidoReporteService reporteService;

  public PedidoReporteWebController(PedidoReporteService reporteService) {
    this.reporteService = reporteService;
  }

  @GetMapping("/reportes/pedidos")
  public String mostrarReportes(
      @RequestParam(required = false) String cliente,
      @RequestParam(required = false) BigDecimal totalMinimo,
      @RequestParam(required = false) Integer minPedidos,
      Model model,
      HttpServletResponse response) {
    model.addAttribute("resumen", reporteService.obtenerResumen());
    model.addAttribute("pedidosFiltrados", List.of());
    model.addAttribute("clientesFrecuentes", List.of());
    model.addAttribute("clienteBuscado", cliente);
    model.addAttribute("totalMinimo", totalMinimo);
    model.addAttribute("minPedidos", minPedidos);
    model.addAttribute(
        "busquedaRealizada",
        cliente != null || totalMinimo != null
    );
    model.addAttribute("frecuenciaConsultada", minPedidos != null);

    try {
      if (cliente != null || totalMinimo != null) {
        if (cliente == null || totalMinimo == null) {
          throw new IllegalArgumentException(
              "Ingrese el cliente y el total mínimo para realizar la búsqueda."
          );
        }
        model.addAttribute(
            "pedidosFiltrados",
            reporteService.buscarPedidosPorClienteYTotalMinimo(
                cliente,
                totalMinimo
            )
        );
      }

      if (minPedidos != null) {
        model.addAttribute(
            "clientesFrecuentes",
            reporteService.listarClientesFrecuentes(minPedidos)
        );
      }
    } catch (IllegalArgumentException exception) {
      model.addAttribute("error", exception.getMessage());
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    return "reportes-pedidos";
  }
}
