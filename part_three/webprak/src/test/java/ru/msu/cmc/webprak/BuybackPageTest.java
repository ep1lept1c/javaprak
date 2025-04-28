package ru.msu.cmc.webprak;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.File;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

public class BuybackPageTest {

    private ChromeDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080";
    private static final String LOGIN_URL = BASE_URL + "/login?redirectTo=/buyback";
    private static final String BUYBACK_URL = BASE_URL + "/buyback";

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    void loginAsTestUser() {
        driver.get(LOGIN_URL);

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
        WebElement passwordInput = driver.findElement(By.name("password"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        emailInput.sendKeys("test@gmail.com");
        passwordInput.sendKeys("test");
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/buyback"));
    }

    @Test
    void testPageLoad() {
        loginAsTestUser();
        driver.get(BUYBACK_URL);

        assertEquals("Выкуп автомобиля", driver.getTitle());
        assertTrue(driver.findElement(By.tagName("h1")).getText().contains("Заявка на выкуп автомобиля"));
        assertTrue(driver.findElement(By.id("buybackForm")).isDisplayed());

        // Проверяем список марок
        Select brandSelect = new Select(driver.findElement(By.name("carBrand")));
        assertEquals(11, brandSelect.getOptions().size()); // Согласно контроллеру
    }

    @Test
    void testFormValidation() {
        loginAsTestUser();
        driver.get(BUYBACK_URL);

        // Прокручиваем к кнопке перед кликом
        WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);

        // Кликаем через Actions для надежности
        new Actions(driver)
                .moveToElement(submitButton)
                .pause(2000)
                .click()
                .perform();

        // Проверяем общее сообщение об ошибке (согласно вашему контроллеру)

        WebElement modelError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("modelError")
        ));
        assertTrue(modelError.isDisplayed());

        WebElement yearError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("yearError")
        ));
        assertTrue(yearError.isDisplayed());

        WebElement mileageError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("mileageError")
        ));
        assertTrue(mileageError.isDisplayed());

        WebElement conditionError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("conditionError")
        ));
        assertTrue(conditionError.isDisplayed());

        WebElement photosError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("photosError")
        ));
        assertTrue(photosError.isDisplayed());
    }


    @Test
    void testSuccessfulSubmission() {
        loginAsTestUser();
        driver.get(BUYBACK_URL);

        // Заполняем форму
        Select brandSelect = new Select(driver.findElement(By.name("carBrand")));
        brandSelect.selectByValue("Toyota"); // Выбираем конкретную марку

        WebElement modelInput = driver.findElement(By.name("carModel"));
        modelInput.sendKeys("Camry");

        WebElement yearInput = driver.findElement(By.name("carYear"));
        yearInput.sendKeys("2020");

        WebElement mileageInput = driver.findElement(By.name("mileage"));
        mileageInput.sendKeys("50000");

        WebElement conditionInput = driver.findElement(By.name("condition"));
        conditionInput.sendKeys("Отличное состояние");

        // Загрузка тестового изображения
        File testImage = new File("src/test/resources/test-car.png");
        WebElement photoInput = driver.findElement(By.name("photos"));
        photoInput.sendKeys(testImage.getAbsolutePath());

        // Прокручиваем и кликаем на кнопку
        WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);

        try {
            submitButton.click();
        } catch (ElementClickInterceptedException e) {
            // Альтернативный способ клика если перехватывается
            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", submitButton);
        }

        // Проверяем редирект на профиль (согласно контроллеру)
        wait.until(ExpectedConditions.urlContains("/profile"));
    }

    @Test
    void testYearValidation() {
        loginAsTestUser();
        driver.get(BUYBACK_URL);

        // Неверный год (слишком старый)
        WebElement yearInput = driver.findElement(By.name("carYear"));
        yearInput.sendKeys("1980");
        yearInput.sendKeys(Keys.TAB);

        // Отправляем форму
        WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", submitButton);

        // Проверяем сообщение об ошибке (согласно контроллеру)
        WebElement yearError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("yearError")
        ));

        assertTrue(yearError.getText().contains("Год выпуска"));
    }

    @Test
    void testPhotoValidation() {
        loginAsTestUser();
        driver.get(BUYBACK_URL);

        // Заполняем форму без фото
        fillFormWithoutPhotos();

        // Отправляем форму
        WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", submitButton);

        // Проверяем сообщение об ошибке
        WebElement photosError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("photosError")
        ));
        assertTrue(photosError.isDisplayed());

    }

    void fillFormWithoutPhotos() {
        Select brandSelect = new Select(driver.findElement(By.name("carBrand")));
        brandSelect.selectByValue("Toyota");

        driver.findElement(By.name("carModel")).sendKeys("Camry");
        driver.findElement(By.name("carYear")).sendKeys("2020");
        driver.findElement(By.name("mileage")).sendKeys("50000");
        driver.findElement(By.name("condition")).sendKeys("Отличное состояние");
    }
}