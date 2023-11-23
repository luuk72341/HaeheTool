package haehetool;

import org.openqa.selenium.By;
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

public class Main {

    //ADJUST THESE
    public static final int TIME_CLIPBOARD_REFRESH = 1000;

    private static String lastClipboardContent = "";
    static ChromeDriver driver;

    public static void main(String[] args) {

        //listen to changes of the clipboard
        new Thread(() -> {
            lastClipboardContent = getClipboardContent();
            while (true) {
                String clipboardContent = getClipboardContent();
                if (!clipboardContent.equals(lastClipboardContent)) {
                    System.out.println("Clipboard content changed: " + clipboardContent);
                    lastClipboardContent = clipboardContent;
                    driver.get("https://lowlightsstudios.com/cart");
                    driver.findElement(By.id("thecheckoutbutton")).click();

                    By elementLocator = By.name("reductions");
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5)); // 10 seconds timeout
                    WebElement elementToBePresent = wait.until(ExpectedConditions.presenceOfElementLocated(elementLocator));
                    elementToBePresent.sendKeys(lastClipboardContent);

                    driver.findElement(By.xpath("//*[@id=\"Form0\"]/div[1]/div/div/div[4]/div/section/div[1]/div/div/div[2]/div/section/div/div[1]/div/button")).click();
                }

                try {
                    Thread.sleep(TIME_CLIPBOARD_REFRESH);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //setup chrome driver
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\luuk7\\Desktop\\Sel\\chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
        String chromeProfilePath = "C:\\Users\\luuk7\\AppData\\Local\\Google\\Chrome\\User Data\\Cardmarket";
        chromeOptions.addArguments("user-data-dir=" + chromeProfilePath);
        driver = new ChromeDriver(chromeOptions);
        driver.get("https://lowlightsstudios.com/");
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