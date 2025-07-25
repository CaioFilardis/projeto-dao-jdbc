package modal.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId()); // id do department
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Unexpected error! No rows affected!");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+ "WHERE Id = ? "
					);
			
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId()); // id do department
			st.setInt(6, obj.getId()); // id do seller
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");
			
			st.setInt(1, id);
			
			
			int rows = st.executeUpdate();
			
			if (rows == 0) {
				throw new DbException("Vendedor não encontrado!");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}	
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
				// refatorado, chamada da função
				Department dep = instantiateDepartment(rs);
				
				// instanciar objeto seller
				Seller obj = instantiateSeller(rs, dep);
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

	// propagando exceção na assinatura do método
	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setDepartment(dep); // recebe o objeto por  conta da relação criada entre tabelas
		return obj;
	}


	// propagando exceção na assinatura do método
	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId")); // pegar o id do departamento
		dep.setName(rs.getString("DepName"));
		return dep;
	}


	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT seller.*, department.Name AS DepName "
				    + "FROM seller INNER JOIN department "
				    + "ON seller.DepartmentId = department.Id "
				    + "ORDER BY Name"
					);
	
			rs = st.executeQuery(); // resultado
			
			List<Seller> list = new ArrayList<>();
			
			// apenas executar com base em uma 'chave'(que no caso será o id)
			Map<Integer, Department> map = new HashMap<>(); 
			
			while (rs.next()) {
				
				// pegar os departamentos existentes
				Department dep = map.get(rs.getInt("DepartmentId"));				
				
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				
				Seller obj = instantiateSeller(rs, dep);
				list.add(obj);
			}
			return list;
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
	public List<Seller> findDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT seller.*, department.Name AS DepName "
				    + "FROM seller "
				    + "INNER JOIN department "
				    + "ON seller.DepartmentId = department.Id "
				    + "WHERE DepartmentId = ? "
				    + "ORDER BY Name"
					);
			
			st.setInt(1, department.getId()); // passando parâmetro
			rs = st.executeQuery(); // resultado
			
			List<Seller> list = new ArrayList<>();
			
			// apenas executar com base em uma 'chave'(que no caso será o id)
			Map<Integer, Department> map = new HashMap<>(); 
			
			while (rs.next()) {
				
				// pegar os departamentos existentes
				Department dep = map.get(rs.getInt("DepartmentId"));				
				
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				
				Seller obj = instantiateSeller(rs, dep);
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally { // não precisa fechar a conexao
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
}
