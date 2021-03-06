package com.example.algamoney.api.resource;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Categoria;
import com.example.algamoney.api.repository.CategoriaRepository;

@RestController // Controladores: o retorno é facilitado, já converte para JSON
@RequestMapping("/categorias") // Mapeamento da requisição (Veio na url "categorias", é
// direcionado para cá.
/* Classe que vai expor tudo que está relacionado ao Recurso (REST) Categoria */
public class CategoriaResource {
	
	@Autowired // Ache uma implementação de "CategoriaRepository" e injete no objeto 
	// "categoriaRepository" (SE NÃO FOSSE INJETADO, ERA @Override)
	private CategoriaRepository categoriaRepository;
	
	@Autowired // (SE NÃO FOSSE INJETADO, ERA @Override)
	private ApplicationEventPublisher publisher;
	
	// No OAuth, a implementação do @CrossOrigin não fica boa. Se eu utilizar outra segurança, ai ele é viável.
	// Por esse motivo foi criado o CrosFilter.java
	
	              // parâmetros são opcionais
	/* @CrossOrigin (maxAge = 10, origins = { "http://localhost:8000" }) // Implementação do CORS (Cross-Origin Http request) */
	@GetMapping // Mapeamento do MÉTODO/VERBO HTTP GET para a URL "/categorias"
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')") // permissões para acesso
		public List<Categoria> listar() {
			return categoriaRepository.findAll();
	}
	
	@PostMapping // Salvar a categoria
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_CATEGORIA') and #oauth2.hasScope('write')") // permissões para acesso
	// Criando uma categoria ex: Financiamento no Postman, ela é salva aqui
	/* @ResponseStatus(HttpStatus.CREATED)// ao terminar a execução do método, quero
	// que retorne o status CREATED ("RESPONSE" SUBSTITUTIDO PELO CODIGO "RETURN" E "RESPONSEENTITY") */
	public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
		// salva no Repositório Categoria
		Categoria categoriaSalva = categoriaRepository.save(categoria);
		// Metódo inteiro para criar o "Location" no Headers, respeitando o REST //
		/*  Através da classe "ServletUriComponentsBuilders" que é o HELP do spring,
		  eu vou com "fromCurrentRequestUri" pegar a partir da URI da requisição atual,
		  que no caso é "/categorias", eu vou adicionar o "/{codigo}" na URI e
		  , por fim, setar o Header "Location" nessa URI */
		/* URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}")
				.buildAndExpand(categoriaSalva.getCodigo()).toUri();
			response.setHeader("Location", uri.toASCIIString());
	
			// Cria o status e retorna na resposta "Response = resposta"
			return ResponseEntity.created(uri).body(categoriaSalva); */
		
		publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
	}
	
	// O código do "Location" criado irá ir para a variável "código" criada
	// eu busco ele com o "findOne" e consigo retornar a categoria desejada
	@GetMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')") // permissões para acesso
	public ResponseEntity<Categoria> buscarPeloCodigo(@PathVariable Long codigo) {
		 Categoria categoria = categoriaRepository.findOne(codigo);
		 // Se o código for inexistente, retorna 404, do contrario da o codigo certo //
		 return categoria != null ? ResponseEntity.ok(categoria) : ResponseEntity.notFound().build();
	}
	
}
