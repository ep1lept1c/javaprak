package ru.msu.cmc.webprak;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderFormPageTest {
    private ChromeDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080";
    private static final String LOGIN_URL = BASE_URL + "/login?redirectTo=/order/1";
    private static final String ORDER_FORM_URL = BASE_URL + "/order/1";

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
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

        wait.until(ExpectedConditions.urlContains("/order/1"));
    }

    void navigateToStep(int stepNumber) {
        // Ждем загрузки первого шага
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("step1")));

        for (int i = 1; i < stepNumber; i++) {
            try {
                // Ждем пока кнопка "Далее" станет кликабельной
                int finalI = i;
                WebElement nextButton = wait.until(driver -> {
                    List<WebElement> buttons = driver.findElements(
                            By.xpath("//div[@id='step" + finalI + "']//button[contains(@class, 'next-step')]")
                    );
                    return buttons.isEmpty() ? null : buttons.get(0);
                });

                // Прокручиваем к кнопке и кликаем через Actions
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block: 'center'});", nextButton
                );

                new Actions(driver)
                        .moveToElement(nextButton)
                        .pause(500)
                        .click()
                        .perform();

                // Ждем появления следующего шага
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.id("step" + (i + 1))
                ));

                // Дополнительная пауза для анимации
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }

            } catch (TimeoutException e) {
                fail("Не удалось перейти на шаг " + (i + 1) + ": " + e.getMessage());
            }
        }
    }




    @Test
    @Order(1)
    void testOrderFormPageLoad() {
        loginAsTestUser();
        driver.get(ORDER_FORM_URL);

        assertEquals("Оформление заказа", driver.getTitle());
        assertTrue(driver.findElement(By.id("orderForm")).isDisplayed());

        // Проверяем, что первый шаг активен
        WebElement step1 = driver.findElement(By.id("step1"));
        assertTrue(step1.getAttribute("class").contains("active"));
    }

    @Test
    @Order(2)
    void testNavigationBetweenSteps() {
        loginAsTestUser();
        driver.get(ORDER_FORM_URL);

        // Проходим все шаги вперед
        navigateToStep(4);

        // Возвращаемся назад
        WebElement prevButton = driver.findElement(
                By.xpath("//div[@id='step4']//button[contains(@class, 'prev-step')]")
        );
        prevButton.click();

        // Проверяем, что вернулись на шаг 3
        WebElement step3 = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("step3"))
        );
        assertTrue(step3.getAttribute("class").contains("active"));

        // Продолжаем возвращаться
        prevButton = driver.findElement(
                By.xpath("//div[@id='step3']//button[contains(@class, 'prev-step')]")
        );
        prevButton.click();

        // Проверяем шаг 2
        WebElement step2 = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("step2"))
        );
        assertTrue(step2.getAttribute("class").contains("active"));
    }

    @Test
    @Order(3)
    void testOrderFormStep1() {
        loginAsTestUser();
        driver.get(ORDER_FORM_URL);

        // Проверяем содержимое первого шага
        WebElement carTitle = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[@id='step1']//h3")
                )
        );
        assertFalse(carTitle.getText().isBlank());

        WebElement carImage = driver.findElement(
                By.xpath("//div[@id='step1']//img")
        );
        assertTrue(carImage.isDisplayed());
        assertFalse(carImage.getAttribute("src").isBlank());

        // Проверяем наличие цены
        WebElement priceElement = driver.findElement(
                By.xpath("//div[@id='step1']//p[contains(@class, 'text-success') or contains(@class, 'text-dark')]")
        );
        assertTrue(priceElement.getText().contains("₽"));

        // Переходим на следующий шаг
        WebElement nextButton = driver.findElement(
                By.xpath("//div[@id='step1']//button[contains(@class, 'next-step')]")
        );
        nextButton.click();

        // Проверяем, что перешли на шаг 2
        WebElement step2 = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("step2"))
        );
        assertTrue(step2.getAttribute("class").contains("active"));
    }

    @Test
    @Order(4)
    void testOrderFormStep2WithoutTestDrive() {
        loginAsTestUser();
        driver.get(ORDER_FORM_URL);
        navigateToStep(2);

        // Проверяем содержимое второго шага
        WebElement stepTitle = driver.findElement(
                By.xpath("//div[@id='step2']//h4")
        );
        assertEquals("Запись на тест-драйв (опционально)", stepTitle.getText());

        // Проверяем, что поля тест-драйва скрыты
        WebElement testDriveFields = driver.findElement(By.id("testDriveFields"));
        assertEquals("none", testDriveFields.getCssValue("display"));

        // Переходим дальше без тест-драйва
        WebElement nextButton = driver.findElement(
                By.xpath("//div[@id='step2']//button[contains(@class, 'next-step')]")
        );
        nextButton.click();

        // Проверяем переход на шаг 3
        WebElement step3 = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("step3"))
        );
        assertTrue(step3.getAttribute("class").contains("active"));
    }

    @Test
    @Order(5)
    void testOrderFormStep2WithTestDrive() {
        loginAsTestUser();
        driver.get(ORDER_FORM_URL);
        navigateToStep(2);

        // Включаем тест-драйв
        WebElement testDriveCheckbox = driver.findElement(By.id("testDriveCheck"));
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", testDriveCheckbox);

        // Ждем появления полей тест-драйва
        WebElement testDriveFields = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("testDriveFields"))
        );
        assertNotEquals("none", testDriveFields.getCssValue("display"));

        // Заполняем данные тест-драйва
        // Генерируем дату на завтра (чтобы прошла валидация "минимум за 24 часа")
        String tomorrowDateTime = LocalDateTime.now().plusDays(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

        WebElement testDriveTime = driver.findElement(By.id("testDriveTime"));
        testDriveTime.sendKeys(tomorrowDateTime);

        Select salonSelect = new Select(driver.findElement(By.name("salon")));
        salonSelect.selectByVisibleText("Москва");

        // Переходим дальше
        WebElement nextButton = driver.findElement(
                By.xpath("//div[@id='step2']//button[contains(@class, 'next-step')]")
        );
        nextButton.click();

        // Проверяем переход на шаг 3
        WebElement step3 = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("step3"))
        );
        assertTrue(step3.getAttribute("class").contains("active"));
    }

    @Test
    @Order(6)
    void testOrderFormStep3WithPhone() {
        loginAsTestUser();
        driver.get(ORDER_FORM_URL);

        // Используем улучшенную навигацию
        navigateToStep(3);

        // Проверяем содержимое третьего шага
        WebElement stepTitle = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[@id='step3']//h4")
                )
        );
        assertEquals("Ваши данные", stepTitle.getText());

        // Проверяем телефон (с обработкой обоих случаев)
        try {
            WebElement phoneInput = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("phoneInput"))
            );

            // Если поле для ввода телефона есть
            phoneInput.sendKeys("1234567890");
            WebElement savePhoneBtn = driver.findElement(By.id("savePhoneBtn"));

            ((JavascriptExecutor)driver).executeScript(
                    "arguments[0].click();", savePhoneBtn
            );

            // Ждем сохранения телефона
            wait.until(ExpectedConditions.invisibilityOf(phoneInput));

        } catch (TimeoutException e) {
            // Если телефон уже указан
            WebElement phoneDisplay = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("phoneDisplay"))
            );
            assertTrue(phoneDisplay.isDisplayed());
        }

        // Переходим дальше с проверкой
        WebElement nextButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[@id='step3']//button[contains(@class, 'next-step')]")
                )
        );
        ((JavascriptExecutor)driver).executeScript(
                "arguments[0].click();", nextButton
        );

        // Проверяем переход на шаг 4
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("step4")
        ));
    }



    @Test
    @Order(7)
    void testOrderFormStep4AndSubmit() {
        loginAsTestUser();
        driver.get(ORDER_FORM_URL);
        navigateToStep(4);

        // Проверяем содержимое четвертого шага
        WebElement stepTitle = driver.findElement(
                By.xpath("//div[@id='step4']//h4")
        );
        assertEquals("Подтверждение заказа", stepTitle.getText());

        // Проверяем сводку по автомобилю
        WebElement carSummary = driver.findElement(
                By.xpath("//div[@id='step4']//h5[text()='Автомобиль']/following-sibling::div")
        );
        assertTrue(carSummary.isDisplayed());

        // Проверяем сводку по тест-драйву
        WebElement testDriveSummary = driver.findElement(By.id("testDriveSummary"));
        assertTrue(testDriveSummary.isDisplayed());

        // Проверяем контактные данные
        WebElement contactInfo = driver.findElement(
                By.xpath("//div[@id='step4']//h5[text()='Контактные данные']/following-sibling::div")
        );
        assertTrue(contactInfo.isDisplayed());

        // Соглашаемся с условиями
        WebElement agreeCheckbox = driver.findElement(By.id("agreeTerms"));
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", agreeCheckbox);

        // Отправляем форму
        WebElement submitButton = driver.findElement(
                By.xpath("//div[@id='step4']//button[@type='submit']")
        );
        submitButton.click();

        // Проверяем успешное оформление
        // (Замените на актуальный URL/условие успешного оформления)
        wait.until(ExpectedConditions.urlContains("/confirmation"));
        assertTrue(driver.getCurrentUrl().contains("/confirmation"));
    }


}