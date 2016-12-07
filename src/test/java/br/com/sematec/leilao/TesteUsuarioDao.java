package br.com.sematec.leilao;


import org.hibernate.*;
import org.mockito.*;
import org.junit.*;
import static org.junit.Assert.*;

import br.com.sematec.leilao.dao.CriadorDeSessao;
import br.com.sematec.leilao.dao.UsuarioDao;
import br.com.sematec.leilao.dominio.Usuario;

public class TesteUsuarioDao {

	private Session session;
	private UsuarioDao usuarioDao;
	
	@Before
	public void antes(){
		session = new CriadorDeSessao().getSession();
		usuarioDao = new UsuarioDao(session);
		session.beginTransaction();
	}
	
	@After
	public void depois()
	{
		session.getTransaction().rollback();
		session.close();
	
	}
	
	@Test
	public void deveEncontrarPeloNomeEmailMoackado(){
		
		Session session = Mockito.mock(Session.class);
		Query query = Mockito.mock(Query.class);
		UsuarioDao usuarioDao = new UsuarioDao(session);
		
		Usuario usuario = new Usuario("usuarioTeste","usuario@teste.com.br");
		String sql = UsuarioDao.SQL_NOME_EMAIL;
		
		Mockito.when(session.createQuery(sql)).thenReturn(query);
		
		Mockito.when(query.uniqueResult()).thenReturn(usuario);
		Mockito.when(query.setParameter("nome", "usuarioTeste")).thenReturn(query);
		Mockito.when(query.setParameter("email", "usuario@teste.com.br")).thenReturn(query);

		Usuario usuarioDoBanco = usuarioDao.porNomeEEmail("usuarioTeste", "usuario@teste.com.br");
		assertEquals(usuario.getNome(),usuarioDoBanco.getNome());
		assertEquals(usuario.getEmail(),usuarioDoBanco.getEmail());
	}
	
	@Test
	public void deveEncontrarPeloNomeEmail(){

		
		Usuario usuario = new Usuario("usuarioTeste","usuario@teste.com.br");
		String sql = UsuarioDao.SQL_NOME_EMAIL;
		usuarioDao.salvar(usuario);
		Usuario usuarioDoBanco = usuarioDao.porNomeEEmail("usuarioTeste", "usuario@teste.com.br");
		
		
		assertEquals(usuario.getNome(),usuarioDoBanco.getNome());
		assertEquals(usuario.getEmail(),usuarioDoBanco.getEmail());
		
		usuarioDao.deletarPorNomeEMail(usuario);
		
	}
	
	@Test
	public void deveRetornarNuloSeNaoEncontrarUsuario(){
	
		Usuario usuarioDoBanco = usuarioDao.porNomeEEmail("usuarioTeste", "usuario@teste.com.br");
		assertNull(usuarioDoBanco);
	}
	

}
