package com.example.algamoney.api.repository.lancamento;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.util.StringUtils;

import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Lancamento_;
import com.example.algamoney.api.repository.filter.LancamentoFilter;

	// Impl = Implementação
	public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

		// EntityManager permite trabalhar com a consulta
		@PersistenceContext
		private EntityManager manager;
		
		@Override
		public List<Lancamento> filtrar(LancamentoFilter lancamentoFilter) {
			// Criteria pertence ao JPA
			CriteriaBuilder builder = manager.getCriteriaBuilder();
			CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
			Root<Lancamento> root = criteria.from(Lancamento.class);
			
			Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
			criteria.where(predicates);
			
			TypedQuery<Lancamento> query = manager.createQuery(criteria);
			return query.getResultList();
		}

		// Retorna um Array
		private Predicate[] criarRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder,
				Root<Lancamento> root) {
			List<Predicate> predicates = new ArrayList<>();
			
			if (!StringUtils.isEmpty(lancamentoFilter.getDescricao())) {
				predicates.add(builder.like(
// Metamodel: só para evitar digitar a String no Predicate "LancamentoRepositoryImpl" na seção "builder.lower()
						builder.lower(root.get(Lancamento_.descricao)), "%" + lancamentoFilter.getDescricao().toLowerCase() + "%"));
			}
			
			if (lancamentoFilter.getDataVencimentoDe() != null) {
				predicates.add(
						builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoDe()));
			}
			
			if (lancamentoFilter.getDataVencimentoAte() != null) {
				predicates.add(
						builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoAte()));
			}
			
			return predicates.toArray(new Predicate[predicates.size()]);
		}

	}

