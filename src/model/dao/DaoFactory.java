package model.dao;

import modal.dao.impl.SellerDaoJDBC;

/* classe auxiliar para instanciar os DAOs*/
public class DaoFactory {

	// executa independente da instancia de um objeto
	// porém, na sua execução irá instanciar o objeto DAO
	public static SellerDao createdSellerDao() {
		return new SellerDaoJDBC();
	}
}
