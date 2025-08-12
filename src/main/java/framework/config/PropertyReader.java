package framework.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {

	private PropertyReader(){
		throw new UnsupportedOperationException("Property Reader class — do not instantiate.");
	}

	/**
	 * This method is used to read data from config properties.
	 * 
	 * @param key
	 * @return
	 */
	public static String readProperty(String key) {
		String returnText = "";
		File file = new File("src/main/resources/properties/config.properties");
		try (FileInputStream fileInput = new FileInputStream(file)){
			Properties properties = new Properties();
			properties.load(fileInput);
			returnText = properties.getProperty(key);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnText;
	}

	/**
	 * This method is used to write data from config properties.
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public static String writeProperty(String key, String value) {
		String returnText = "";
		try {
		File dirl = new File(".");
		String file = dirl.getCanonicalPath() + File.separator +"config" + ".properties";		
		try(FileInputStream fileIn = new FileInputStream(file);
			FileOutputStream fileOut = new FileOutputStream(file)) {
			Properties configProperty = new Properties();	
			configProperty.load(fileIn);
			configProperty.setProperty(key, value);
           configProperty.store(fileOut, "config properties");

		} 
		}catch (Exception e) {
			e.printStackTrace();
		} 
		return returnText;
	}

	/**
	 * This method used to get URL from properties path.
	 * 
	 * @param env
	 * @return
	 */
	public static String getPropertyFileURL(String appName, String env) {

		String returnText = null;
		String file = System.getProperty("user.dir") + File.separator + "src/main/resources/properties" + File.separator + appName + ".properties";
		try (FileInputStream fileInput = new FileInputStream(file);){
			Properties properties = new Properties();
			properties.load(fileInput);
			returnText = properties.getProperty(env);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnText;

	}

	public static String readDynamicProperty(String fileName, String key) {
		File file = new File("src/main/resources/properties/" + fileName +".properties");
		String returnText = "";
		try(FileInputStream fileInput = new FileInputStream(file)) {
			Properties properties = new Properties();
			properties.load(fileInput);
			returnText = properties.getProperty(key);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnText;
	}
}
