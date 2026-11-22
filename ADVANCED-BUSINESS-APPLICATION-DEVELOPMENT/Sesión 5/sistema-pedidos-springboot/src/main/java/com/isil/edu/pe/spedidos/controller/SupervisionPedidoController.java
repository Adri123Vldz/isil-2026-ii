package com.isil.edu.pe.spedidos.controller;

import com.isil.edu.pe.spedidos.client.reportes.PedidoReporteClient;
import com.isil.edu.pe.spedidos.client.reportes.PedidoReporteNoDisponibleException;
import com.isil.edu.pe.spedidos.exception.PedidoRevisionException;
import com.isil.edu.pe.spedidos.service.PedidoRevisionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/supervision/pedidos")
public class SupervisionPedidoController {
  private final PedidoRevisionService revisionService;
  private final PedidoReporteClient reporteClient;

  public SupervisionPedidoController(PedidoRevisionService revisionService,
      PedidoReporteClient reporteClient) {
    this.revisionService = revisionService;
    this.reporteClient = reporteClient;
  }

  @GetMapping
  public String listar(
      @RequestParam(defaultValue = "PENDIENTE") String estado,
      Model model,
      HttpServletResponse response) {
    model.addAttribute("estado", estado);
    try {
      model.addAttribute("pedidos", reporteClient.listarRevisiones(estado));
      model.addAttribute("estadisticas", reporteClient.obtenerEstadisticas());
      return "supervision-pedidos";
    } catch (PedidoReporteNoDisponibleException e) {
      model.addAttribute("error",
          "El servicio de reportes no está disponible. Intente nuevamente.");
      response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
      return "supervision-pedidos";
    } catch (RuntimeException e) {
      model.addAttribute("error", "El estado solicitado no es válido.");
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return "supervision-pedidos";
    }
  }

  @PostMapping("/{id}/revisar")
  public String revisar(
      @PathVariable long id,
      @RequestParam String observacion,
      RedirectAttributes redirectAttributes,
      HttpServletResponse response) {
    try {
      revisionService.revisar(id, observacion);
      redirectAttributes.addFlashAttribute("exito",
          "Pedido #" + id + " revisado correctamente.");
      return "redirect:/supervision/pedidos";
    } catch (PedidoRevisionException e) {
      response.setStatus(e.getStatus());
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/supervision/pedidos";
    }
  }
}
