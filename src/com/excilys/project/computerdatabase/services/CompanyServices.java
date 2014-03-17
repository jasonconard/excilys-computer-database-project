package com.excilys.project.computerdatabase.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.excilys.project.computerdatabase.domain.Company;
import com.excilys.project.computerdatabase.persistence.CompanyDAO;
import com.excilys.project.computerdatabase.persistence.ConnectionManager;

public class CompanyServices {

	public static CompanyServices instance = null;

	private CompanyDAO companyDAO = CompanyDAO.getInstance();

	public List<Company> getAllCompanies(){
		Connection connection = null;
		List<Company> companies = null;
		try{
			connection = ConnectionManager.getConnection();
			connection.setAutoCommit(false);

			//traitement des différentes instructions composant la transaction
			companies = companyDAO.retrieveAll(connection);
			if(companies!=null){	  
				connection.commit(); // c'est ici que l'on valide la transaction
				connection.setAutoCommit(true);
			}else{
				connection.rollback();
			}
		}catch(SQLException sqle){
			try{connection.rollback();}catch(Exception e){}
		}catch(Exception e){
			try{connection.rollback();}catch(Exception e1){}
		}finally{
			try{connection.close();}catch(Exception e){}
		}

		return companies;
	}

	public Company getCompany(long idCompany){
		Connection connection = null;
		Company company = null;

		try{
			connection = ConnectionManager.getConnection();
			connection.setAutoCommit(false);

			company = companyDAO.retrieveByCompanyId(idCompany, connection);

			if(company!=null){	  
				connection.commit();
				connection.setAutoCommit(true);
			}else{
				connection.rollback();
			}
		}catch(SQLException sqle){
			try{connection.rollback();}catch(Exception e){}
		}catch(Exception e){
			try{connection.rollback();}catch(Exception e1){}
		}finally{
			try{connection.close();}catch(Exception e){}
		}

		return company;
	}

	public void insert(Company company){
		Connection connection = null;

		try{
			connection = ConnectionManager.getConnection();
			connection.setAutoCommit(false);

			companyDAO.insert(company, connection);

			connection.commit();
			connection.setAutoCommit(true);

		}catch(SQLException sqle){
			try{connection.rollback();}catch(Exception e){}
		}catch(Exception e){
			try{connection.rollback();}catch(Exception e1){}
		}finally{
			try{connection.close();}catch(Exception e){}
		}
	}

	synchronized public static CompanyServices getInstance(){
		if(instance == null){
			instance = new CompanyServices();
		}
		return instance;
	}
}
