package model.dao;

import java.util.List;

import model.entities.Department;
import model.entities.Seller;

public interface SellerDao {

	void insert(Seller obj); // operação responsável por inserir da dos no banco
	void update(Seller obj); // operação responsável por atualizar os dados do banco
	void deleteById(Integer id); // operação responsável por deletar linhas da tabela
	Seller findById(Integer id); // operação que realiza consultas no banco
	List<Seller> findAll(); // lista para retornar todos os departamentos
	List<Seller> findDepartment(Department department); // 
	
}
