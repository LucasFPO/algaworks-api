package com.example.algamoney.api.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.model.Categoria;
import com.example.algamoney.api.repository.CategoriaRepository;

@RestController // Controladores: o retorno é facilitado, já converte para JSON
@RequestMapping("/categorias") // Mapeamento da requisição (Veio na url "categorias", é
// direcionado para cá.
/* Classe que vai expor tudo que está relacionado ao Recurso (REST) Categoria */
public class CategoriaResource {
	
	@Autowired // Ache uma implementação de "CategoriaRepository" e injete no objeto 
	// "categoriaRepository"
	private CategoriaRepository categoriaRepository;
	
	@GetMapping // Mapeamento do MÉTODO/VERBO HTTP GET para a URL "/categorias"
	public List<Categoria> listar() {
		return categoriaRepository.findAll();
	}
}
