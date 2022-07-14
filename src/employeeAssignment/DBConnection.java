package employeeAssignment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;



public class DBConnection {
	
	static String url = "jdbc:sqlserver://MAVCHN0821118\\SQLEXPRESS;databaseName=Employee;integratedSecurity=true;encrypt=false;";
	
	public static void insertIntoDB(EmployeeDetails emd,boolean flag)
	{
		try
		{
			String query="";
			if(flag==true)
			{
				query = "Insert into EmployeeDetails values('"+emd.getEmployee_Id()+"','"+emd.getEmployee_Name()+"','"+emd.getEmail()+"','"+emd.getPhone_number()+"','"+emd.getHire_Date()+"','"+emd.getJob_Id()+"','"+emd.getSalary()+"','"+emd.getCommission_Pct()+"','"+emd.getManager_Id()+"','"+emd.getDepartment_Id()+"',"+"getDate()"+")";
			}
			else
			{
				query = "Insert into EmployeeDetails_Failed values('"+emd.getEmployee_Id()+"','"+emd.getEmployee_Name()+"','"+emd.getEmail()+"','"+emd.getPhone_number()+"','"+emd.getHire_Date()+"','"+emd.getJob_Id()+"','"+emd.getSalary()+"','"+emd.getCommission_Pct()+"','"+emd.getManager_Id()+"','"+emd.getDepartment_Id()+"',"+"getDate()"+")";
			}
			System.out.println("Inserting the details \n"+query);
			try(Connection conn = DriverManager.getConnection(url))
				{
				PreparedStatement stmt = conn.prepareStatement(query);
				stmt.executeUpdate();
				System.out.println("Insertion Done Successfully!\n");
				conn.close();
				}
			catch(SQLException e)
			{
			System.out.println("DB Connection Failed!");
			e.printStackTrace();
			}
			
		}
		catch(Exception e)
		{
		System.out.println("DB Connection Failed!");
		e.printStackTrace();
		}
	}
	
	public static List<EmployeeReport> getReport(boolean flag) {
		List<EmployeeReport> errList = new ArrayList<EmployeeReport>();
		try
		{
			String query="";
			if(flag==true)
			{
				query = "SELECT  emp.employee_Id,emp.employee_Name,emp.email,empj.job_description,[dbo].[get_name](emp.manager_Id) as ManagerName,"
					+ "dept.department_desc\r\n"
					+ "   FROM EmployeeDetails emp"
					+ "   LEFT JOIN Employee_Job empj ON emp.job_Id = empj.job_Id"
					+ "   Left Join Employee_Department dept on emp.department_Id=dept.department_Id";
			}
			else
			{
				query = "SELECT  emp.employee_Id,[dbo].[get_name](emp.employee_Id) as EmployeeName,emp.email,empj.job_description,[dbo].[get_name](emp.manager_Id) as ManagerName,"
						+ "dept.department_desc\r\n"
						+ "   FROM EmployeeDetails_Failed emp\r\n"
						+ "   left join EmployeeDetails empf on emp.employee_Id=empf.employee_Id"
						+ "   LEFT JOIN Employee_Job empj ON emp.job_Id = empj.job_Id"
						+ "   Left Join Employee_Department dept on emp.department_Id=dept.department_Id";
			}
			try(Connection conn = DriverManager.getConnection(url))
				{
				
				Statement stmt = conn.createStatement();
				ResultSet result = stmt.executeQuery(query);
				while(result.next())
				{
					EmployeeReport emr = new EmployeeReport();
					emr.setEmployee_Id(result.getString(1));
					emr.setEmployee_Name(result.getString(2));
					emr.setEmail(result.getString(3));
					emr.setJob_description(result.getString(4));
					emr.setManager_Name(result.getString(5));
					emr.setDepartment_Name(result.getString(6));
					errList.add(emr);
				}
				
				conn.close();
				}
		
		}
		catch(SQLException e)
		{
		System.out.println("DB Connection failed!");
		e.printStackTrace();
		}
		return errList;
	} 
	
	public static void emptyDB()
	{
		String query1 = "Delete from EmployeeDetails";
		String query2 = "Delete from EmployeeDetails_failed";
		try(Connection conn = DriverManager.getConnection(url))
		{
			PreparedStatement stmt = conn.prepareStatement(query1);
			stmt.executeUpdate();
			System.out.println("Old Records from EmployeeDetails deleted!");
			stmt = conn.prepareStatement(query2);
			stmt.executeUpdate();
			System.out.println("Old Records from EmployeeDetails_failed deleted!");
			conn.close();
		}
		catch(SQLException e)
		{
			System.out.println("DB Connection failed!");
			e.printStackTrace();
		}
	}
}
