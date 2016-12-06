package br.com.sematec.leilao.dao;

import javax.transaction.Transaction;

import org.hibernate.Session;

import br.com.sematec.leilao.dominio.Usuario;

public class UsuarioDao {

	public static final String SQL_NOME_EMAIL = "from Usuario u where u.nome = :nome and u.email = :email";
	private final Session session;

	public UsuarioDao(Session session) {
		this.session = session;
	}
	
	public Usuario porId(int id) {
		return (Usuario) session.load(Usuario.class, id);
	}
	
	public Usuario porNomeEEmail(String nome, String email) {
		return (Usuario) session.createQuery(SQL_NOME_EMAIL)
				.setParameter("nome", nome)
				.setParameter("email", email)
				.uniqueResult();
	}
	
	public void salvar(Usuario usuario) {
		session.save(usuario);
	}
	
	public void atualizar(Usuario usuario) {
		session.merge(usuario);
	}
	
	public void deletar(Usuario usuario) {
		session.delete(usuario);
	}

	public void deletarPorNomeEMail(Usuario usuario) {
		session.delete(usuario);
	}
}
