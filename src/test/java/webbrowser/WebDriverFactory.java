package webbrowser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class WebDriverFactory {

    private WebDriverFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static WebDriver createWebDriver() throws MalformedURLException {
        String webdriver = System.getProperty("browser", "firefox");
        switch(webdriver) {
            case "firefox":
                System.setProperty("webdriver.gecko.driver","/usr/local/bin/geckodriver");
                return new FirefoxDriver();
            case "chrome":
                System.setProperty("webdriver.chrome.driver","/usr/local/bin/chromedriver");
                return new ChromeDriver();
            case "edge":
                System.setProperty("webdriver.edge.driver","/usr/local/bin/msedgedriver");
                return new EdgeDriver();
            case "remoteChrome":
                return new RemoteWebDriver(new URL("http:/remote/hub"), new ChromeOptions());
            case "remoteFirefox":
                return new RemoteWebDriver(new URL("http://remote/hub"), new FirefoxOptions());
            default:
                throw new RuntimeException("Unsupported webdriver: " + webdriver);
        }
    }
}