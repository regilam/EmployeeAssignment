package employeeAssignment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
	
	public static void main(String args[])
	  {
		File file = new File("C:\\Users\\regilam\\eclipse-workspace\\Files\\employee.csv");
		File successFile = new File("C:\\Users\\regilam\\eclipse-workspace\\Files\\successReport.csv");
		File failureFile = new File("C:\\Users\\regilam\\eclipse-workspace\\Files\\failureReport.csv");
		try {
			DBConnection.emptyDB();
			Scanner sc = new Scanner(file);
			sc.nextLine();
			while (sc.hasNextLine())
			{
				insertIntoDB(sc.nextLine());
			}
		} catch (FileNotFoundException e) {
			System.out.println("Exception : "+e);
			e.printStackTrace();
		}

		try
		{
			List<EmployeeReport> success_report = DBConnection.getReport(true);
			List<EmployeeReport> failed_report = DBConnection.getReport(false);
			
			
			BufferedWriter out = new BufferedWriter(new FileWriter(successFile));
			out.write("EmployeeID,Name,Email,Job Description,Department Name,Manager Name");
			out.newLine();
			for(EmployeeReport er : success_report)
			{
				out.write(er.getEmployee_Id()+","+er.getEmployee_Name()+","+er.getEmail()+","+er.getJob_description()+","+er.getDepartment_Name()+","+er.getManager_Name());
				out.newLine();
			}
			out.close();
			System.out.println("Success Report generated!");
			
			out = new BufferedWriter(new FileWriter(failureFile));
			out.write("EmployeeID,Name,Email,Job Description,Department Name,Manager Name");
			out.newLine();
			for(EmployeeReport er : failed_report)
			{
				out.write(er.getEmployee_Id()+","+er.getEmployee_Name()+","+er.getEmail()+","+er.getJob_description()+","+er.getDepartment_Name()+","+er.getManager_Name());
				out.newLine();
			}
			out.close();
			System.out.println("Failure Report generated!");
			
		}
		catch(Exception e)
		{
			System.out.println("Exception->"+e);
		}
	  }
	
	public static void insertIntoDB(String record)
	{
		
		record = record.replaceAll(",,",",-,");
		record = record.replaceAll(",,",",-,");
		if(record.charAt(record.length()-1)==',')
		{
			record = record+"-";
		}
		
		String records[] = record.split(",");
		EmployeeDetails successReport = new EmployeeDetails();
		EmployeeDetails failureReport = new EmployeeDetails();
		
		//Employee_ID
		if(isNull(records[0]))
		{
			//inserting the complete records into failure report
			failureReport.setEmployee_Id(records[0]);
			failureReport.setEmployee_Name(records[1]);
			failureReport.setEmail(records[2]);
			failureReport.setPhone_number(records[3]);
			failureReport.setHire_Date(records[4]);
			failureReport.setJob_Id(records[5]);
			failureReport.setSalary(records[6]);
			failureReport.setCommission_Pct(records[7]);
			failureReport.setManager_Id(records[8]);
			failureReport.setDepartment_Id(records[9]);
			DBConnection.insertIntoDB(failureReport, false);
		}
		else
		{
			successReport.setEmployee_Id(records[0]);
			failureReport.setEmployee_Id(records[0]);
		
			//Name
			if(isNameOnlyAlphabet(records[2]))
			{
				if(isNull(records[1]))
				{
					successReport.setEmployee_Name(records[2]);;
					failureReport.setEmployee_Name("-");
				}
				else
				{
					successReport.setEmployee_Name(records[1]+" "+records[2]);
					failureReport.setEmployee_Name("-");
				}
			}
			else
			{
				failureReport.setEmployee_Name(records[1]+" "+records[2]);
			}
			
			//Email
			String email = isValidEmail(records[3]);
			if(email.equalsIgnoreCase("false"))
			{
				failureReport.setEmail(records[3]);
				successReport.setEmail("-");
			}
			else
			{
				successReport.setEmail(email);
				failureReport.setEmail("-");
			}
			
			//PhoneNumber
			if(isValidPhoneNumber(records[4]))
			{
				successReport.setPhone_number(records[4]);
				failureReport.setPhone_number("-");
			}
			else
			{
				successReport.setPhone_number("-");
				failureReport.setPhone_number(records[4]);
			}
			
			//HireDate
			if(isValidDate(records[5]))
			{
				successReport.setHire_Date(records[5]);
				failureReport.setHire_Date("-");
			}
			else
			{
				successReport.setHire_Date("-");
				failureReport.setHire_Date(records[5]);
			}
			
			//JOBID
			if(isNull(records[6]))
			{
				successReport.setJob_Id("-");
				failureReport.setJob_Id("-");
			}
			else
			{
				successReport.setJob_Id(records[6]);
				failureReport.setJob_Id(records[6]);
			}
			
			//Salary
			if(isValidSalary(records[7]))
			{
				successReport.setSalary(records[7]);
				failureReport.setSalary("-");
			}
			else
			{
				failureReport.setSalary(records[7]);
				successReport.setSalary("-");
			}
			
			//COMMISSION_PCT
			successReport.setCommission_Pct(records[8]);
			failureReport.setCommission_Pct("-");
			
			//MANAGER_ID
			if(isNull(records[9]))
			{
				successReport.setManager_Id(records[0]);
				failureReport.setManager_Id(records[0]);
			}
			else
			{
				successReport.setManager_Id(records[9]);
				failureReport.setManager_Id(records[9]);
			}
			
			//DEPARTMENT_ID
			if(isNull(records[10]))
			{
				successReport.setDepartment_Id("-");
				failureReport.setDepartment_Id("-");
			}
			else
			{
				successReport.setDepartment_Id(records[10]);
				failureReport.setDepartment_Id(records[10]);
			}
			if(!(failureReport.getEmployee_Name().equalsIgnoreCase("-") && failureReport.getEmail().equalsIgnoreCase("-")  && 
					failureReport.getPhone_number().equalsIgnoreCase("-") && failureReport.getHire_Date().equalsIgnoreCase("-")
					 && failureReport.getSalary().equalsIgnoreCase("-")))
			{
				//Insert Failure record
				DBConnection.insertIntoDB(failureReport, false);
			}
			//Insert Success record
			DBConnection.insertIntoDB(successReport, true);
		}
		
		
	}
	
	public static boolean isNull(String value)
	{
		if(value.trim().equalsIgnoreCase("")||value.equals("null")||value.equals(null)||value.trim().equalsIgnoreCase("-"))
		{
			return true;
		}
		return false;
	}
	
	public static boolean isNameOnlyAlphabet(String value) 
	{
	    return ((!value.equals(""))
	            && (value != null)
	            && (value.matches("^[a-zA-Z ]*$")));
	}
	
	public static String isValidEmail(String value)
    {
		if(isNull(value))
		{
			return "false";
		}
		else
		{
			if(value.length()<=50)
			{
				if(isNameOnlyAlphabet(value))
				{
					return value+"@abc.com";
				}
		        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
		                            "[a-zA-Z0-9_+&*-]+)*@" +
		                            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
		                            "A-Z]{2,7}$";
		                              
		        Pattern pat = Pattern.compile(emailRegex);
		        if(pat.matcher(value).matches())
		        {
		        	return value;
		        }
		        else
		        {
		        	return "false";
		        }
			}
			else
			{
				return "false";
			}
		}
    }
	
	public static boolean isValidPhoneNumber(String value)
	{
		if(isNull(value))
		{
			return false;
		}
		else
		{
			if(value.length()==12)
			{
				//"\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}" pattern for the complete phoneNumber
				if(value.charAt(3)=='.' && value.charAt(7)=='.')
				{
					String phoneRegex ="^[\\.0-9]*$";
					Pattern pat = Pattern.compile(phoneRegex);
					return pat.matcher(value).matches();
				}
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
	}
	
	public static boolean isValidDate(String value)
    {
        String regex = "([0-2][0-9]||3[0-1])-(0[0-9]||1[0-2])-((19|20)\\d\\d)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher((CharSequence)value);
        return matcher.matches();
    }
	
	public static boolean isValidSalary(String value)
    {
		if(isNull(value))
		{
			return false;
		}
		else
		{
		String regex = "^([0-9]*)(.[[0-9]+]?)?$";
		Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher((CharSequence)value);
        return matcher.matches();
		}
    }
}
