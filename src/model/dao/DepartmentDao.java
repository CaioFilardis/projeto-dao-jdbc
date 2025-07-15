package model.dao;

import java.util.List;

import model.entities.Department;

public interface DepartmentDao {
	
	void insert(Department obj); // operação responsável por inserir da dos no banco
	void update(Department obj); // operação responsável por atualizar os dados do banco
	void deleteById(Integer id); // operação responsável por deletar linhas da tabela
	Department findById(Integer id); // operação que realiza consultas no banco
	List<Department> findAll(); // lista para retornar todos os departamentos
}
