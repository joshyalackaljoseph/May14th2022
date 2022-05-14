package com.sg.driver;

import java.lang.reflect.Method;
import java.util.Map;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.sg.methods.AppDependentMethods;
import com.sg.methods.AppIndependentMethods;
import com.sg.methods.Datatable;
import com.sg.methods.UserModuleMethods;
import com.sg.reports.ReportUtils;

public class DriverScript {
	public static AppIndependentMethods appInd = null;
	public static AppDependentMethods appDep = null;
	public static ReportUtils reports = null;
	public static Datatable datatable = null;
	public static UserModuleMethods userMethods = null;
	public static String screenshotLocation = null;
	public static ExtentReports extent = null;
	public static ExtentTest test = null;
	public static String runnerFilePath = null;
	
	
	@BeforeSuite
	public void loadClassFile() {
		try {
			runnerFilePath = System.getProperty("user.dir")+ "\\ExecutionController\\Runner.xlsx";
			appInd = new AppIndependentMethods();
			appDep = new AppDependentMethods();
			reports = new ReportUtils();
			datatable = new Datatable();
			userMethods = new UserModuleMethods();
			extent = reports.startExtentReport("TestExecutionReport", appInd.getPropData("BuildNumber"));
		}catch(Exception e) {
			System.out.println("Exception in 'loadClassFile()' method. " + e);
		}
	}
	
	
	@DataProvider(name = "testData")
	public Object[][] getDataProvider(){
		return datatable.createDataProvider("Runner", "Controller");
	}
	
	
	@Test(dataProvider = "testData")
	public void runTestScripts(Map<String, String> data) {
		Class cls = null;
		Object obj = null;
		Method script = null;
		try {
			cls = Class.forName(data.get("ClassName"));
			obj = cls.getDeclaredConstructor().newInstance();
			script = obj.getClass().getMethod(data.get("ScriptName"));
			
			boolean blnRes = (boolean) script.invoke(obj);
			if(blnRes) {
				datatable.setCellData(runnerFilePath, "Controller", "Status", data.get("TestCaseID"), "PASS");
				System.out.println("Pass");
			}else {
				datatable.setCellData(runnerFilePath, "Controller", "Status", data.get("TestCaseID"), "FAIL");
				System.out.println("Fail");
			}
		}catch(Exception e) {
			System.out.println("Exception in 'runTestScripts()' method. " + e);
		}
		finally {
			cls = null;
			obj = null;
			script = null;
		}
	}
		
}
