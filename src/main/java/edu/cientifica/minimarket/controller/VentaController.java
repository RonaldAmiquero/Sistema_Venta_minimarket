package edu.cientifica.minimarket.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.cientifica.minimarket.model.DetalleVenta;
import edu.cientifica.minimarket.model.Producto;
import edu.cientifica.minimarket.model.Venta;
import edu.cientifica.minimarket.services.ProductoService;
import edu.cientifica.minimarket.services.VentaService;

@Controller
@RequestMapping("/venta")
public class VentaController {
	
	@Autowired
	ProductoService productoService;
	
	@Autowired
	VentaService ventaService;

	@GetMapping("/")
	public String interfazVenta(Model model, HttpServletRequest request) {
		model.addAttribute("producto", new Producto());
		float subTotal=0;
		List<DetalleVenta> carrito = Venta.obtenerCarrito(request);
		for (DetalleVenta d: carrito) {
			subTotal+= d.getImporte(); 
		}
		model.addAttribute("subTotal",subTotal);
		//model.addAttribute("carrito", carrito);
		return "realizarVenta";
	}
	
	@PostMapping("/agregar")
	public String agregarAlCarrito(@ModelAttribute("producto") Producto producto, HttpServletRequest request, RedirectAttributes redirectAttrs) {
		List<DetalleVenta> carrito = Venta.obtenerCarrito(request);
		Producto productoBuscadoPorCodigo = productoService.buscarCodBarras(producto.getCodigoBarras());
		if(productoBuscadoPorCodigo == null) {
			redirectAttrs.addFlashAttribute("mensaje", "El producto con el codigo " + producto.getIdProducto() + " no existe")
			.addFlashAttribute("clase","warning");
			return "redirect:/venta/";
		}
		
		if(productoBuscadoPorCodigo.sinExistencia()) {
			redirectAttrs
			.addFlashAttribute("mensaje", "el producto esta agotado")
			.addFlashAttribute("clase", "warning");
			return "redirect:/venta/";
		}
		
		boolean encontrado=false;
		
		for (DetalleVenta detalleVenta : carrito) {
			if (detalleVenta.getProducto().getCodigoBarras().equals(productoBuscadoPorCodigo.getCodigoBarras())) {
				detalleVenta.aumentarCantidad();
				encontrado = true;
				break;
			} 
		}
		
		if(!encontrado) {
			Venta venta = new Venta();
			venta.setIdVenta(ventaService.asignarIdVenta());
			carrito.add(new DetalleVenta(productoBuscadoPorCodigo, venta ,1));
		}
		Venta.guardarCarrito(carrito, request);
		return "redirect:/venta/";
	}
	
	@PostMapping(value="/quitar/{indice}")
	public String quitarDelCarrito(@PathVariable("indice") int indice, HttpServletRequest request) {
		List<DetalleVenta> carrito = Venta.obtenerCarrito(request);
        if (carrito != null && carrito.size() > 0 && carrito.get(indice) != null) {
            carrito.remove(indice);
            Venta.guardarCarrito(carrito, request);
        }
        return "redirect:/venta/";
	}
	
	
}
