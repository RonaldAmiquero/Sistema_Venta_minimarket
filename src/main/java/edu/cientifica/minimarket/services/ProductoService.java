package edu.cientifica.minimarket.services;

import java.util.List;
import edu.cientifica.minimarket.model.Producto;

public interface ProductoService {
	public int registrarProducto(Producto producto);
	public List<Producto> listarProducto();
	
}
