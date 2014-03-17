package com.excilys.project.computerdatabase.persistence;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.project.computerdatabase.domain.Company;
import com.excilys.project.computerdatabase.domain.Computer;
import com.excilys.project.computerdatabase.domain.WrapperComputer;
import com.excilys.project.computerdatabase.services.LogsServices;

public class ComputerDAO {
	
	private static final String table = "computer";
	
	public static ComputerDAO instance = null;
	
	public static Logger logger = LoggerFactory.getLogger(ComputerDAO.class);
	
	private static LogsServices logsServices = LogsServices.getInstance();
	
	public List<Computer> retrieveAllWithCompanyNameByWrapper(WrapperComputer wc, Connection connection){
		
		List<Computer> alc = new ArrayList<Computer>();
		
		String like = "";
		if(wc.getFilter()!=null){
			like +=wc.getFilter();
		}
		
		String order = "";
		if(wc.getColumn()!=null){
			order +=wc.getColumn();
		}
		
		String direction = "";
		if(wc.getDirection()!=null){
			direction +=wc.getDirection();
		}
		
		int idBegin = wc.getPage()*WrapperComputer.NBLINEPERPAGES; 
		int nbLines   = WrapperComputer.NBLINEPERPAGES; 
		
		String query = "SELECT cu.*, ca.name AS name2 FROM company AS ca "
				+ "RIGHT OUTER JOIN computer AS cu ON cu.company_id = ca.id "
				+ "WHERE cu.name LIKE '%"+like+"%' OR ca.name LIKE '%"+like+"%'"
				+ "ORDER BY "+ order + " "+direction+" "
				+ "LIMIT "+idBegin+", "+nbLines;
		
		ResultSet results = null;
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = connection.prepareStatement(query);
			results = preparedStatement.executeQuery();
			while(results.next()){
				long id = results.getLong("id");
				String name = results.getString("name");
				Date introduced = results.getDate("introduced");
				Date discontinued = results.getDate("discontinued");
				long companyId = results.getLong("company_id");
				String companyName = results.getString("name2");
				alc.add(
						new Computer.ComputerBuilder(id, name)
						.introduced(introduced)
			            .discontinued(discontinued)
			            .company(
			            		new Company.CompanyBuilder(companyId)
			            		.name(companyName)
			            		.build()
			            		)
			            .build()
				);
			}
		} catch (SQLException e) {
			logsServices.insert("Retrieve all computers with company name and orderBy and like clause error. SQL query : "+query, "Error");
			//logger.error("Retrieve all computers with company name and orderBy and like clause error. SQL query : "+query);
		} finally{
			logsServices.insert("Retrieve all computers with company name and orderBy and like clause completed", "Complete");
			//logger.info("Retrieve all computers with company name and orderBy and like clause completed.");
			closeAll(results,preparedStatement);
		}

		return alc;
	}
	
	public Company retrieveCompanyByComputerId(long idComputer, Connection connection){
		
		Company company = null;
		
		String query = "SELECT ca.* FROM company AS ca INNER JOIN "+table+" AS cu ON cu.company_id = ca.id WHERE cu.id = "+idComputer;
		
		ResultSet results = null;
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = connection.prepareStatement(query);
			results = preparedStatement.executeQuery();
			if(results.next()){
				long id = results.getLong("id");
				String name = results.getString("name");
				company = new Company.CompanyBuilder(id).name(name).build();
			}
			results.close();
			preparedStatement.close();
		} catch (SQLException e) {
			logsServices.insert("Retrieve company using computer id error. SQL query : "+query, "Error");
			//logger.error("Retrieve company using computer id error. SQL query : "+query);
		} finally{
			logsServices.insert("Retrieve company using computer id completed.", "Complete");
			//logger.info("Retrieve company using computer id completed.");
			closeAll(results,preparedStatement);
		}
		
		return company;
	}
	
	public Computer retrieveByComputerId(long idComputer, Connection connection) {
		Computer computer = null;
		
		String query = "SELECT cu.*, ca.name AS name2 FROM company AS ca "
				+ "RIGHT OUTER JOIN computer AS cu ON cu.company_id = ca.id "
				+ "WHERE cu.id = ?";
		String visualQuery = "SELECT cu.*, ca.name AS name2 FROM company AS ca "
				+ "RIGHT OUTER JOIN computer AS cu ON cu.company_id = ca.id "
				+ "WHERE cu.id = "+idComputer;
		ResultSet results = null;
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setLong(1, idComputer);
			results = preparedStatement.executeQuery();
			if(results.next()){
				long id = results.getLong("id");
				String name = results.getString("name");
				Date introduced = results.getDate("introduced");
				Date discontinued = results.getDate("discontinued");
				long companyId = results.getLong("company_id");
				String companyName = results.getString("name2");
				computer = new Computer.ComputerBuilder(id, name)
							.introduced(introduced)
				            .discontinued(discontinued)
				            .company(
				            		new Company.CompanyBuilder(companyId)
				            		.name(companyName)
				            		.build()
				            		)
				            .build();
			}
		} catch (SQLException e) {
			logsServices.insert("Retrieve computer using id error. SQL query : "+visualQuery, "Error");
			//logger.error("Retrieve computer using id error. SQL query : "+visualQuery);
		} finally{
			logsServices.insert("Retrieve computer using id completed.", "Complete");
			//logger.info("Retrieve computer using id completed.");
			closeAll(results, preparedStatement);
		}
		
		return computer;
	}
	
	public void insert(Computer computer, Connection connection){
		String query = "INSERT INTO "+table+" VALUES(?,?,?,?,?)";
		String visualQuery = "INSERT INTO "+table+" VALUES("+computer.getId()+",'"+computer.getName()+"','"+computer.getIntroduced()+"','"+computer.getDiscontinued()+"'";
		if(computer.getCompany()!=null){		
			visualQuery += ","+computer.getCompany().getId()+")";
		}else{
			visualQuery += ", 0)";
		}
		PreparedStatement preparedStatement = null;
		
		try{
			preparedStatement = connection.prepareStatement(query);
			
			preparedStatement.setLong  (1, computer.getId()  );
			preparedStatement.setString(2, computer.getName());
			
			if(computer.getIntroduced()!=null){
				preparedStatement.setDate(3,  new java.sql.Date(computer.getIntroduced().getTime()));
			}else{
				preparedStatement.setNull(3, Types.TIMESTAMP);
			}
			
			if(computer.getDiscontinued()!=null){
				preparedStatement.setDate(4, new java.sql.Date(computer.getDiscontinued().getTime()));
			}else{
				preparedStatement.setNull(4, Types.TIMESTAMP);
			}
			
			if(computer.getCompany() != null){
				preparedStatement.setLong(5, computer.getCompany().getId());
			}else{
				preparedStatement.setNull(5, Types.BIGINT);
			}
			
			preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			logsServices.insert("Insert computer error. SQL query : "+visualQuery, "Error");
			//logger.error("Insert computer error. SQL query : "+visualQuery);
		} finally{
			logsServices.insert("Insert computer completed.", "Complete");
			//logger.info("Insert computer completed.");
			closeAll(null,preparedStatement);
		}
	}
	
	public void delete(long id, Connection connection) {
		String query = "DELETE FROM "+table+" WHERE id = ?";
		String visualQuery = "DELETE FROM "+table+" WHERE id = "+id;
		PreparedStatement preparedStatement = null;
		
		try{
			preparedStatement = connection.prepareStatement(query);
			
			preparedStatement.setLong(1, id);
				
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			logsServices.insert("Delete computer error. SQL query : "+visualQuery, "Error");
			//logger.error("Delete computer error. SQL query : "+visualQuery);
		} finally{
			logsServices.insert("Delete computer completed", "Complete");
			//logger.info("Delete computer completed.");
			closeAll(null,preparedStatement);
		}
	}

	public void update(Computer c, Connection connection) {
		ResultSet results = null;
		PreparedStatement preparedStatement = null;
		
		String query = "UPDATE "+table+" SET name=?, introduced=?, discontinued=?, company_id=? WHERE id = ?";
		String visualQuery = "UPDATE "+table+" SET name='"+c.getName()+"', introduced='"+c.getIntroduced()
							+"', discontinued='"+c.getDiscontinued()+"'";
		
		
		if(c.getCompany()!=null){
			visualQuery += ", company_id="+c.getCompany().getId()+" WHERE id = "+c.getId();
		}else{
			visualQuery += ", company_id= NULL WHERE id = "+c.getId();
		}
		
		
		try{
			
			
			preparedStatement = connection.prepareStatement(query);
			
			preparedStatement.setString(1, c.getName());
			
			if(c.getIntroduced()!=null){
				preparedStatement.setDate(2,  new java.sql.Date(c.getIntroduced().getTime()));
			}else{
				preparedStatement.setNull(2, Types.TIMESTAMP);
			}
			
			if(c.getDiscontinued()!=null){
				preparedStatement.setDate(3, new java.sql.Date(c.getDiscontinued().getTime()));
			}else{
				preparedStatement.setNull(3, Types.TIMESTAMP);
			}
			
			if(c.getCompany() != null){
				preparedStatement.setLong(4, c.getCompany().getId());
			}else{
				preparedStatement.setNull(4, Types.BIGINT);
			}
			
			preparedStatement.setLong(5, c.getId());
			preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			logsServices.insert("Update computer error. SQL query : "+visualQuery, "Error");
			//logger.error("Update computer error. SQL query : "+visualQuery);
		} finally{
			logsServices.insert("Update computer completed.", "Complete");
			//logger.info("Update computer completed.");
			closeAll(results, preparedStatement);
		}
	}
	
	public int computerNumberByFilter(String filter, Connection connection) {
		int count = 0;
		
		String query = "SELECT count(*) AS countComputer FROM company AS ca "
				+ "RIGHT OUTER JOIN computer AS cu ON cu.company_id = ca.id "
				+ "WHERE cu.name LIKE '%"+filter+"%' OR ca.name LIKE '%"+filter+"%'";
		
		ResultSet results = null;
		PreparedStatement preparedStatement = null;
		
		try {
			preparedStatement = connection.prepareStatement(query);
			results = preparedStatement.executeQuery();
			if(results.next()){
				count = results.getInt("countComputer");
			}
		} catch (SQLException e) {
			logsServices.insert("Computer count error. SQL query : "+query, "Error");
			//logger.error("Computer count error. SQL query : "+query);
		} finally{
			logsServices.insert("Computer count completed.", "Complete");
			//logger.info("Computer count completed.");
			closeAll(results,preparedStatement);
		}

		return count;
	}
	
	private void closeAll(ResultSet rs,PreparedStatement ps){
		try {
			if(rs!=null){
				rs.close();
			}
			if(ps!=null){
				ps.close();
			}
			//logsServices.insert("Every connections closed !", "Complete");
			logger.info("Every connections closed !");
		} catch (SQLException e) {
			//logsServices.insert("Every connections failed !", "Error");
			logger.error("Connections closing failed.");
			e.printStackTrace();
		}
	}
	
	synchronized public static ComputerDAO getInstance(){
		if(instance == null){
			instance = new ComputerDAO();
		}
		return instance;
	}

}
