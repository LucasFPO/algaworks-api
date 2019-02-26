package com.example.algamoney.api.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;

@Service
public class PessoaService {
	
	
		@Autowired
		private PessoaRepository pessoaRepository;

		// NÃO poderia ter isso no controlador (PessoaResource), pois não fica legal, apesar de compilar.
		// O código ficaria localizado no método PUT
		public Pessoa atualizar(Long codigo, Pessoa pessoa) {
			Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);	

			//Copia todas as propriedas da "pessoa" no request, tirando o "código", pq na atualização não
			// o passamos
			// (também tiro o atributo "código" no Postmam, na requisição do PUT)
			BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo");
			return pessoaRepository.save(pessoaSalva);
		}
		
		public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
			Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);
			pessoaSalva.setAtivo(ativo);
			pessoaRepository.save(pessoaSalva);
		}
		
		// PUBLIC para também poder usá-lo no "LancamentoService"
		public Pessoa buscarPessoaPeloCodigo(Long codigo) {
			Pessoa pessoaSalva = pessoaRepository.findOne(codigo);
			// Ao tentar atualizar uma pessoa com o código inexistente, dá Status 500 (Erro Server)
			// pois "pessoaSalva" , no método "findOne" não pode ser nula, por isso a criação do If
			// o tamanho igual a 1 é o caractere (tem que ter pelo menos 1, e não 0 (null))
			if (pessoaSalva == null) {
				throw new EmptyResultDataAccessException(1);
			}
			return pessoaSalva;
		}
}
