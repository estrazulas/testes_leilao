package br.com.sematec.leilao;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.sematec.leilao.dao.CriadorDeSessao;
import br.com.sematec.leilao.dao.LeilaoDao;
import br.com.sematec.leilao.dao.UsuarioDao;
import br.com.sematec.leilao.dominio.Leilao;
import br.com.sematec.leilao.dominio.Usuario;

public class TesteLeilaoDao {
	private Session session;
	private LeilaoDao leilaoDao;
	private UsuarioDao usuarioDao;
	private Leilao xbox;
	private Leilao geladeira;
	private Usuario usuario;
	
	@Before
	public void antes(){
		session = new CriadorDeSessao().getSession();
		leilaoDao = new LeilaoDao(session);
		usuarioDao = new UsuarioDao(session);
		usuario = new Usuario("teste", "email");
		xbox  = new Leilao("XBOX",700.0,usuario,false);
		geladeira = new Leilao("Geladeira",1500.0,usuario,true);
		session.beginTransaction();
	}
	
	@After
	public void depois()
	{
		session.getTransaction().rollback();
		session.close();
	}
	

	@Test
	public void deveContaLeiloesNaoEncerrados(){
		Leilao ativo = geladeira;
		Leilao encerrado = xbox;
		encerrado.encerra();
		usuarioDao.salvar(usuario);
		leilaoDao.salvar(ativo);
		leilaoDao.salvar(encerrado);
		long total = leilaoDao.total();
		assertEquals(1L,total);
		
	}
	
	@Test
	public void deveTrazerSomenteLeiloesAntigos(){
		Leilao recente = xbox;
		Leilao antigo = geladeira;

		recente.setDataAbertura(Calendar.getInstance());
		
		Calendar lc = Calendar.getInstance();
		
	    lc.set(Calendar.YEAR, 2011);
	    lc.set(Calendar.MONTH, 10);
	    lc.set(Calendar.DAY_OF_MONTH, 10);
	    
		antigo.setDataAbertura(lc);
		
		usuarioDao.salvar(usuario);
		leilaoDao.salvar(recente);
		leilaoDao.salvar(antigo);
		
		List<Leilao> antigos = leilaoDao.antigos();
		
		assertEquals(1,antigos.size());
		assertEquals("Geladeira",antigos.get(0).getNome());
	}
	
	@Test
	public void deveRetornarLeiloesDeProdutosNovos(){
		Leilao produtoNovo = xbox;
		Leilao produtoUsado = geladeira;
		produtoUsado.setUsado(true);
		usuarioDao.salvar(usuario);
		leilaoDao.salvar(produtoNovo);
		leilaoDao.salvar(produtoUsado);
		List<Leilao> novos = leilaoDao.novos();
		assertEquals(1,novos.size());
		assertEquals("XBOX",novos.get(0).getNome());
	}
	
	@Test
	public void deveRetornarZeroSeNaoHaLeiloesNovos(){
		Leilao encerrado = xbox;
		Leilao tambemEncerrado = geladeira;
		
		encerrado.encerra();
		tambemEncerrado.encerra();
		usuarioDao.salvar(usuario);
		leilaoDao.salvar(encerrado);
		leilaoDao.salvar(tambemEncerrado);
		
		long total = leilaoDao.total();
		assertEquals(0,total);
	}
	
	@Test
	public void deveTrazerSomenteLeiloesHaMaisDe7Dias(){
		Leilao noLimite = xbox;
		Calendar seteDiasAtras = Calendar.getInstance();
		seteDiasAtras.add(Calendar.DAY_OF_MONTH, -7);
		
		noLimite.setDataAbertura(seteDiasAtras);
		
		usuarioDao.salvar(usuario);
		leilaoDao.salvar(noLimite);
		
		List<Leilao> antigos = leilaoDao.antigos();
		
		assertEquals(1,antigos.size());
	}
	
	@Test
	public void deveTrazerLeiloesEncerradosNoPeriodo(){
		Calendar comecoDoIntervalo = Calendar.getInstance();
		Calendar fimDoIntervalo = Calendar.getInstance();
		Calendar fora = Calendar.getInstance();
		fora.add(Calendar.DAY_OF_MONTH, -7);
		
		usuarioDao.salvar(usuario);
		leilaoDao.salvar(geladeira);
		leilaoDao.salvar(xbox);
		
		xbox.setDataAbertura(comecoDoIntervalo);
		geladeira.setDataAbertura(fora);
		List<Leilao> leiloes = leilaoDao.porPeriodo(comecoDoIntervalo,fimDoIntervalo);
		assertEquals(1, leiloes.size());
		assertEquals("XBOX",leiloes.get(0).getNome());
	}
	
}
