//                            PENSAMENTO REST
// SE EU MUDAR O "CODIGO", POR EXEMPLO, NA REQUISIÇÃO DO POSTMAN, NO ARQUIVO JSON
// O ERRO, CASO EXISTA, DEVE SER 4.. (erro do cliente)
// ERRO 5.. É SÓ SE EU NÃO MUDAR NADA DA REQUISIÇÃO (parte de cima do postman)

package com.example.algamoney.api.exceptionhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice // Observa toda a aplicação (consigo compartilhar a classe para todos os controladores)
// a classe "CategoriaResource" reconhece a classe "AlgamoneyExcpetionHandler" devido ao controller
                                        // captura exceções de resposta das entidades
public class AlgamoneyExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Autowired
	private MessageSource messageSource;
	
	// captura as mensagens que eu não consegui ler na resposta do Postman,
	// ao por exemplo, dar 400 Bad Request (tentando criar atributo que não existe)
	@Override
	//" HttpMessageNotReadable" aparece como Exception no Postman ao ocorrer o erro.
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
                                                              		// sem parâmetro extra eu passo "null"
		String mensagemUsuario = messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale());
		
		// Trata o @RequestBody, se for null e se não for null
		String mensagemDesenvolvedor = ex.getCause() != null ? ex.getCause().toString() : ex.toString();
		// Criei um Array para se diferenciar do List erro do método abaixo "MethodArgumentNotValidException"
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		// Trata a exceção                          // tratando o body                                // tratando o status
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	
	//captura argumentos de métodos que não são válidos, como por exemplo: "null"
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		List<Erro> erros = criarListaDeErros(ex.getBindingResult());
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	// CRIADO PARA DAR A MENSAGEM AO USAR DELETE NO POSTMAN
	@ExceptionHandler({ EmptyResultDataAccessException.class }) // EXCEÇÃO: AO INVEZ DO RETORNO PADRAO 500
	// NO POSTMAN, RETORNA O ABAIXO, 404 (NOT FOUND)
	public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex, WebRequest request) {
		String mensagemUsuario = messageSource.getMessage("recurso.nao-encontrado", null, LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
	
	/* SE EU NÃO QUISER ENVIAR UMA MENSAGEM, EU PODERIA APENAS TER:
	 * @ExceptionHandler({ EmptyResultDataAccessException.class })
	 * @ResponseStatus(HttpStatus.NOT_FOUND)
	 * public void handleEmptyResultDataAccessException() {
	 * } */
	
	@ExceptionHandler({ DataIntegrityViolationException.class } )
	public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
		String mensagemUsuario = messageSource.getMessage("recurso.operacao-nao-permitida", null, LocaleContextHolder.getLocale());
		// "mensagemDesenvolvedor" alterada pela dependency pom.xml "apache" 
		String mensagemDesenvolvedor = ExceptionUtils.getRootCauseMessage(ex);
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}
	                                     // local da lista de erros
	private List<Erro> criarListaDeErros(BindingResult bindingResult) {
		List<Erro> erros = new ArrayList<>();
		// "getFieldErrors" mê da todos os erros que aconteceram nos campos dos atributos que foram
		// validados na entidade pelo @Valid em "CategoriaRepository"
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			String mensagemUsuario = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
			String mensagemDesenvolvedor = fieldError.toString();
			erros.add(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		}
			
		return erros;
	}
	
public static class Erro {
		
		private String mensagemUsuario;
		private String mensagemDesenvolvedor;
		
		public Erro(String mensagemUsuario, String mensagemDesenvolvedor) {
			this.mensagemUsuario = mensagemUsuario;
			this.mensagemDesenvolvedor = mensagemDesenvolvedor;
		}

		public String getMensagemUsuario() {
			return mensagemUsuario;
		}

		public String getMensagemDesenvolvedor() {
			return mensagemDesenvolvedor;
		}
		
	}
}
	
