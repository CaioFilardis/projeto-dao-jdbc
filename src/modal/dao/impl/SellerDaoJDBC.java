package modal.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	// dependência da conexao
	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	
	@Override
	public void insert(Seller obj) {
		
		
	}

	@Override
	public void update(Seller obj) {
		
		
	}

	@Override
	public void deleteById(Integer id) {
		
		
	}

	@Override
	public Seller findById(Integer id) {
		// 1. inserindo dados
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT seller.*, department.Name AS DepName "
				    + "FROM seller "
				    + "INNER JOIN department ON seller.DepartmentId = department.Id "
				    + "WHERE seller.Id = ?"
					);
			
			st.setInt(1, id); // passando parâmetro
			rs = st.executeQuery(); // resultado
			
			// testar se veio algum resultado(ou seja, se existe algum elemento do id inserido
			if (rs.next()) {
				Department dep = new Department();
				dep.setId(rs.getInt("DepartmentId")); // pegar o id do departamento
				dep.setName(rs.getString("DepName"));
				
				// instanciar objeto seller
				Seller obj = new Seller();
				obj.setId(rs.getInt("Id"));
				obj.setName(rs.getString("Name"));
				obj.setEmail(rs.getString("Email"));
				obj.setBaseSalary(rs.getDouble("BaseSalary"));
				obj.setBirthDate(rs.getDate("BirthDate"));
				obj.setDepartment(dep); // recebe o objeto por  conta da relação criada entre tabelas
				return obj;
			}
			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally { // não precisa fechar a conexao
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
	}

	@Override
	public List<Seller> findAll() {
		return null;
	}

	
	
}
