package com.isil.edu.pe.spedidos.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PedidoReporteControllerIntegrationTest {

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context)
        .apply(springSecurity())
        .build();

    jdbcTemplate.update("DELETE FROM pedido");
    insertarPedido("Ana Torres", "LAP-001", "Laptop", "2500.00", 2, "5000.00");
    insertarPedido("Carlos Perez", "MOU-001", "Mouse", "50.00", 3, "150.00");
  }

  @Test
  @DisplayName("Return plain JSON and HTTP 200 when CONSULTA requests level 2 summary")
  void returnPlainJsonAndHttp200WhenConsultaRequestsLevelTwoSummary() throws Exception {
    mockMvc.perform(get("/api/v1/pedidos/estadisticas/resumen")
            .with(user("consulta").roles("CONSULTA")))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.totalPedidos").value(2))
        .andExpect(jsonPath("$._links").doesNotExist());
  }

  @Test
  @DisplayName("Return plain JSON array when level 1 resource is filtered")
  void returnPlainJsonArrayWhenLevelOneResourceIsFiltered() throws Exception {
    mockMvc.perform(get("/api/v1/pedidos")
            .param("cliente", "Ana")
            .with(user("consulta").roles("CONSULTA")))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0]").exists())
        .andExpect(jsonPath("$._links").doesNotExist());
  }

  @Test
  @DisplayName("Return HAL product sales collection when report is requested")
  void returnHalProductSalesCollectionWhenReportIsRequested() throws Exception {
    mockMvc.perform(get("/api/v1/pedidos/estadisticas/productos")
            .with(user("operador").roles("OPERADOR")))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
        .andExpect(jsonPath("$._embedded").exists())
        .andExpect(jsonPath("$._links.self.href").exists())
        .andExpect(jsonPath("$._links['estadisticas-resumen'].href").exists());
  }

  @Test
  @DisplayName("Return filtered orders from advanced JSON search")
  void returnFilteredOrdersFromAdvancedJsonSearch() throws Exception {
    mockMvc.perform(get("/api/v1/pedidos/busqueda-avanzada")
            .param("cliente", "Ana")
            .param("totalMinimo", "1000")
            .with(user("consulta").roles("CONSULTA")))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].cliente").value("Ana Torres"))
        .andExpect(jsonPath("$[0].total").value(5000.0))
        .andExpect(jsonPath("$[1]").doesNotExist());
  }

  @Test
  @DisplayName("Return aggregate frequent-customer metrics from JSON endpoint")
  void returnAggregateFrequentCustomerMetricsFromJsonEndpoint() throws Exception {
    insertarPedido("Ana Torres", "MOU-001", "Mouse", "50.00", 2, "100.00");

    mockMvc.perform(get("/api/v1/pedidos/clientes-frecuentes")
            .param("minPedidos", "2")
            .with(user("consulta").roles("CONSULTA")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].cliente").value("Ana Torres"))
        .andExpect(jsonPath("$[0].totalPedidos").value(2))
        .andExpect(jsonPath("$[0].totalUnidades").value(4))
        .andExpect(jsonPath("$[0].importeAcumulado").value(5100.0))
        .andExpect(jsonPath("$[0].ticketPromedio").value(2550.0));
  }

  @Test
  @DisplayName("Return bad request when advanced report parameters are invalid")
  void returnBadRequestWhenAdvancedReportParametersAreInvalid() throws Exception {
    mockMvc.perform(get("/api/v1/pedidos/busqueda-avanzada")
            .param("cliente", "Ana")
            .param("totalMinimo", "-1")
            .with(user("consulta").roles("CONSULTA")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value(
            "El total mínimo debe ser mayor o igual a cero."
        ));

    mockMvc.perform(get("/api/v1/pedidos/clientes-frecuentes")
            .param("minPedidos", "0")
            .with(user("consulta").roles("CONSULTA")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value(
            "El mínimo de pedidos debe ser al menos 1."
        ));
  }

  @Test
  @DisplayName("Return bad request when advanced search customer is blank")
  void returnBadRequestWhenAdvancedSearchCustomerIsBlank() throws Exception {
    mockMvc.perform(get("/api/v1/pedidos/busqueda-avanzada")
            .param("cliente", " ")
            .param("totalMinimo", "0")
            .with(user("consulta").roles("CONSULTA")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("El cliente es obligatorio."));
  }

  @Test
  @DisplayName("Render the report JSP for an authorized CONSULTA user")
  void renderReportJspForAuthorizedConsultaUser() throws Exception {
    mockMvc.perform(get("/reportes/pedidos")
            .with(user("consulta").roles("CONSULTA")))
        .andExpect(status().isOk())
        .andExpect(view().name("reportes-pedidos"))
        .andExpect(model().attributeExists(
            "resumen",
            "pedidosFiltrados",
            "clientesFrecuentes"
        ));
  }

  @Test
  @DisplayName("Apply advanced filters and frequent-customer threshold to the report JSP model")
  void applyAdvancedFiltersAndFrequentCustomerThresholdToReportJspModel() throws Exception {
    insertarPedido("Ana Torres", "MOU-001", "Mouse", "50.00", 2, "100.00");

    mockMvc.perform(get("/reportes/pedidos")
            .param("cliente", "Ana")
            .param("totalMinimo", "1000")
            .param("minPedidos", "2")
            .with(user("consulta").roles("CONSULTA")))
        .andExpect(status().isOk())
        .andExpect(view().name("reportes-pedidos"))
        .andExpect(model().attribute("pedidosFiltrados", hasSize(1)))
        .andExpect(model().attribute("clientesFrecuentes", hasSize(1)));
  }

  @Test
  @DisplayName("Return not found when legacy action style search URI is requested")
  void returnNotFoundWhenLegacyActionStyleSearchUriIsRequested() throws Exception {
    mockMvc.perform(get("/api/v1/pedidos/buscar")
            .param("cliente", "Ana")
            .with(user("consulta").roles("CONSULTA")))
        .andExpect(status().isNotFound());
  }

  private void insertarPedido(
      String cliente,
      String productoCodigo,
      String productoNombre,
      String precioUnitario,
      int cantidad,
      String total) {

    jdbcTemplate.update(
        """
        INSERT INTO pedido (
          cliente,
          producto_codigo,
          producto_nombre,
          precio_unitario,
          cantidad,
          total,
          fecha
        ) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
        """,
        cliente,
        productoCodigo,
        productoNombre,
        new BigDecimal(precioUnitario),
        cantidad,
        new BigDecimal(total)
    );
  }
}
