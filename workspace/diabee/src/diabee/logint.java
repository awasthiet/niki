package diabee;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;

public class logint {

	AppiumDriver<MobileElement> driver;
	String otpValue;

	@BeforeMethod
	public void setUP() throws MalformedURLException
	{
		DesiredCapabilities cap = new DesiredCapabilities();
		cap.setCapability("appiunVersion","1.6.4");
		cap.setCapability("plateformVersion" ,"6.0");
		cap.setCapability("platformName", "Android");
		cap.setCapability("deviceName", "Lenovo A6600d40");
		cap.setCapability("app", System.getProperty("user.dir")+"//app//Niki-test-29-06.apk");
		//cap.setCapability("appWaitActivity", "com.techbins.niki.activities.RegisterActivity");
		//cap.setCapability("appPackage", "com.techbins.niki");
		cap.setCapability("newCommandTimeout", "120");
		driver = new AndroidDriver<MobileElement>(new URL("http://0.0.0.0:4723/wd/hub"),cap);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test(enabled=true)
	public void testLogin() throws ExecuteException, IOException, InterruptedException
	{
		
		driver.findElementById("com.techbins.niki:id/edtTxtPhone").sendKeys("9821681554");
		String test = driver.findElementById("com.techbins.niki:id/edtTxtPhone").getText();
		System.out.println(test);
		driver.findElementById("com.techbins.niki:id/btnSubmit").click();
		getOTP();
		System.out.println(otpValue);
		driver.findElementById("com.techbins.niki:id/editTxtOtp").sendKeys(otpValue);
	}

	public void getOTP() throws ExecuteException, IOException, InterruptedException
	{
		String previousOTP = readOTP();
		System.out.println("Previous OTP : "+previousOTP);
		AndroidDriver<MobileElement> androidDriver = (AndroidDriver<MobileElement>)(driver);
		androidDriver.startActivity("com.android.mms", "com.android.mms.ui.ConversationList");
		String msg = driver.findElement(By.id("com.android.mms:id/subject")).getText();
		boolean flag = false;
		for(int j=1;j<=50;j++)
		{
			if(msg.contains("OTP:"))
			{
				String otp = msg.split("OTP:")[1].split("\\.")[0].trim();
				System.out.println(otp);
				if(otp.equals(previousOTP))
				{
					for(int i =1;i<=50;i++)
					{
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						msg = driver.findElement(By.id("com.android.mms:id/subject")).getText();
						otp = msg.split("OTP:")[1].split("\\.")[0].trim();
						if(!otp.equals(previousOTP))
						{
							otpValue = otp;
							flag = true;
							break;
						}else
						{
							System.out.println("NEW OTP IS NOT RECIVED");
						}
					}
				}else
				{
					otpValue = otp;
				}
			} else
			{
				Thread.sleep(2000);
			}
			if(flag)
			{
				break;
			}
		}
		System.out.println("NEW OTP : "+otpValue);
		startActivity();
	}
	
	@AfterMethod
	public void tearDown() throws FileNotFoundException
	{
		if(!otpValue.equals(null))
		{
			printLastOtp();
		}
		driver.quit();
	}

	public void startActivity() throws ExecuteException, IOException
	{
		CommandLine command = new CommandLine("cmd"); 
		command.addArgument("/c"); 
		command.addArgument("adb shell am start -n com.techbins.niki/com.techbins.niki.SplashActivity"); 
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler(); 
		DefaultExecutor executor = new DefaultExecutor(); 
		executor.setExitValue(1); 
		executor.execute(command, resultHandler);
	}
	
	public void printLastOtp() throws FileNotFoundException
	{
		//otpValue = "2156";
		PrintWriter out=null;
		try {
			out = new PrintWriter(System.getProperty("user.dir")+"\\Files\\lastOTP");
			out.print(otpValue.trim());
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			out.flush();
			out.close();
		}
	}
	

	public String readOTP() throws IOException
	{
		BufferedReader bf =null;
		String t=null;
		StringBuilder sb = new StringBuilder();
		try {
			bf = new BufferedReader(new FileReader(new File(System.getProperty("user.dir")+"\\Files\\lastOTP")));
			
			while((t=bf.readLine())!=null)
			{
				sb.append(t);
				//System.out.println(t);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			bf.close(); 
		}
		//System.out.println(sb);
		return sb.toString().trim();
	}
}