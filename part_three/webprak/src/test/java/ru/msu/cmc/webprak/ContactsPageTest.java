package ru.msu.cmc.webprak;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

public class ContactsPageTest {

    private ChromeDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080";
    private static final String CONTACTS_URL = BASE_URL + "/contacts";

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

    @Test
    void testContactsPageLoad() {
        driver.get(CONTACTS_URL);

        // Проверка заголовка страницы
        assertEquals("Контакты | Автосалон", driver.getTitle());

        // Проверка основного заголовка
        WebElement pageHeader = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.tagName("h1"))
        );
        assertEquals("Контакты", pageHeader.getText());
    }

    @Test
    void testMapAndBranches() {
        driver.get(CONTACTS_URL);

        // Проверка наличия карты
        WebElement mapContainer = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("map-container"))
        );
        assertTrue(mapContainer.isDisplayed());

        // Проверка iframe с картой
        WebElement mapFrame = driver.findElement(By.id("map-frame"));
        assertTrue(mapFrame.isDisplayed());
        assertTrue(mapFrame.getAttribute("src").contains("yandex.ru/map-widget"));

        // Проверка карточек салонов
        WebElement moscowBranch = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class, 'branch-card') and contains(., 'Москва')]")
                )
        );
        assertTrue(moscowBranch.isDisplayed());
        assertTrue(moscowBranch.getAttribute("class").contains("active"));

        WebElement spbBranch = driver.findElement(
                By.xpath("//div[contains(@class, 'branch-card') and contains(., 'Санкт-Петербург')]")
        );
        assertTrue(spbBranch.isDisplayed());
    }

    @Test
    void testBranchSwitching() {
        driver.get(CONTACTS_URL);

        // Находим карточку СПб и кликаем
        WebElement spbBranch = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[contains(@class, 'branch-card') and contains(., 'Санкт-Петербург')]")
                )
        );
        spbBranch.click();

        // Проверяем что она стала активной
        wait.until(d ->
                spbBranch.getAttribute("class").contains("active") &&
                        !driver.findElement(
                                By.xpath("//div[contains(@class, 'branch-card') and contains(., 'Москва')]")
                        ).getAttribute("class").contains("active")
        );

        // Проверяем что карта обновилась
        WebElement mapFrame = driver.findElement(By.id("map-frame"));
        assertTrue(mapFrame.getAttribute("src").contains("9z8y7x6w5v4u3t2s1r0q"));
    }

    @Test
    void testFeedbackForm() {
        driver.get(CONTACTS_URL);

        // 1. Проверяем наличие формы
        WebElement feedbackForm = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("div#feedback-form")
                )
        );
        assertTrue(feedbackForm.isDisplayed());

        // 2. Проверяем видимость всех полей ввода
        WebElement nameInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#feedback-form input#name"))
        );
        WebElement emailInput = driver.findElement(By.cssSelector("#feedback-form input#email"));
        WebElement messageInput = driver.findElement(By.cssSelector("#feedback-form textarea#message"));

        assertAll(
                () -> assertTrue(nameInput.isDisplayed()),
                () -> assertTrue(emailInput.isDisplayed()),
                () -> assertTrue(messageInput.isDisplayed())
        );

        // 3. Заполняем форму
        nameInput.sendKeys("Тестовый Пользователь");
        emailInput.sendKeys("test@example.com");
        messageInput.sendKeys("Тестовое сообщение");

        // 4. Ищем кнопку несколькими способами
        WebElement submitButton = null;
        try {
            // Попытка 1: По точному CSS-селектору
            submitButton = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.cssSelector("#feedback-form form button[type='submit']")
                    )
            );
        } catch (TimeoutException e1) {
            try {
                // Попытка 2: По тексту на кнопке
                submitButton = wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.xpath("//form[@id='feedback-form']//button[contains(text(), 'Отправить')]")
                        )
                );
            } catch (TimeoutException e2) {
                // Попытка 3: По классу кнопки
                submitButton = wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.cssSelector("#feedback-form form button.btn-primary")
                        )
                );
            }
        }

        // 5. Прокручиваем к кнопке и кликаем
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", submitButton);

        try {
            submitButton.click();
        } catch (ElementClickInterceptedException e) {
            // Альтернативный способ клика если элемент перекрыт
            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", submitButton);
        }

        // 6. Проверяем результат
        WebElement successMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("div#feedback-success")
                )
        );
        assertTrue(successMessage.isDisplayed());

        // Дополнительная проверка скрытия формы
        WebElement formElement = driver.findElement(By.id("feedback-form"));
        assertEquals("none", formElement.getCssValue("display"), "Форма должна скрыться после отправки");
    }

    @Test
    void testContactInfo() {
        driver.get(CONTACTS_URL);

        // Проверяем контактную информацию Москвы
        WebElement moscowContact = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//h3[contains(text(), 'Главный салон')]/following-sibling::p")
                )
        );
        String moscowText = moscowContact.getText();
        assertTrue(moscowText.contains("ул. Автозаводская, 12"));
        assertTrue(moscowText.contains("+7 (495) 123-45-67"));
        assertTrue(moscowText.contains("info@autosalon.ru"));

        // Проверяем контактную информацию СПб
        WebElement spbContact = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//h3[contains(text(), 'Филиал в Санкт-Петербурге')]/following-sibling::p")
                )
        );
        String spbText = spbContact.getText();
        assertTrue(spbText.contains("пр. Энтузиастов, 45"));
        assertTrue(spbText.contains("+7 (812) 987-65-43"));
        assertTrue(spbText.contains("spb@autosalon.ru"));
    }
}