package haehetool;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.time.Duration;

public class Main{

    //ADJUST THESE
    public static final int TIME_CLIPBOARD_REFRESH = 100;
    public static final String CHROMEDRIVER_PATH = "C:\\Users\\luuk7\\Desktop\\Sel\\chromedriver.exe";
    public static final String CHROME_PROFILE_PATH = "C:\\Users\\luuk7\\AppData\\Local\\Google\\Chrome\\User Data\\Cardmarket";

    private static String lastClipboardContent = "";
    static ChromeDriver driver;

    public static void main(String[] args){

        //listen to changes of the clipboard
        new Thread(() -> {
            lastClipboardContent = getClipboardContent();
            while (true) {
                String clipboardContent = getClipboardContent();
                if (!clipboardContent.equals(lastClipboardContent)) {
                    System.out.println("copied code: " + clipboardContent);
                    lastClipboardContent = clipboardContent;

                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    By elementLocator = By.name("reductions");
                    WebElement codeField = wait.until(ExpectedConditions.presenceOfElementLocated(elementLocator));
                    codeField.sendKeys(lastClipboardContent);
                    codeField.sendKeys(Keys.ENTER);
                    driver.findElement(By.className("paypal-buttons")).click();
                }

                try {
                    Thread.sleep(TIME_CLIPBOARD_REFRESH);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //setup chrome driver
        System.setProperty("webdriver.chrome.driver", CHROMEDRIVER_PATH);
        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("user-data-dir=" + CHROME_PROFILE_PATH);
        driver = new ChromeDriver(chromeOptions);
    }

    private static String getClipboardContent() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = clipboard.getContents(null);

        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                return (String) transferable.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}