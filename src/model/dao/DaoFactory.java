package model.dao;

import db.DB;
import modal.dao.impl.DepartmentDaoJDBC;
import modal.dao.impl.SellerDaoJDBC;

/* classe auxiliar para instanciar os DAOs*/
public class DaoFactory {

	// executa independente da instancia de um objeto
	// porém, na sua execução irá instanciar o objeto DAO
	public static SellerDao createdSellerDao() {
		return new SellerDaoJDBC(DB.getConnection());
	}
	
	public static DepartmentDao createdDepartmentDao() {
		return new DepartmentDaoJDBC(DB.getConnection());
	}
}
