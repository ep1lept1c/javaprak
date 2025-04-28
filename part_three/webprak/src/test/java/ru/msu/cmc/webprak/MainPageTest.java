package ru.msu.cmc.webprak;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MainPageTest {

    private ChromeDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
        wait = new WebDriverWait(driver, Duration.ofSeconds(2));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testMainPageTitle() {
        driver.get(BASE_URL);
        assertEquals("Главная | Автосалон", driver.getTitle());
    }

    @Test
    void testPromotionsSection() {
        driver.get(BASE_URL);

        // 1. Проверка заголовка (более надёжный способ)
        WebElement promotionsHeader = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//h2[text()='Топ акции' or contains(text(), 'Акции')]")
                )
        );

        // 2. Альтернативные варианты поиска карточек акций:
        List<WebElement> promotions = driver.findElements(
                By.xpath("//div[h5[contains(@class, 'card-title')]]")
        );

        // ИЛИ если карточки имеют определённую структуру:
        // List<WebElement> promotions = driver.findElements(
        //     By.xpath("//div[contains(@class, 'card') and .//p[contains(@class, 'text-success')]]")
        // );

        if (promotions.isEmpty()) {
            // Дебаг-информация
            System.out.println("HTML страницы:");
            System.out.println(driver.getPageSource());
            System.out.println("Найдены элементы класса 'card': " +
                    driver.findElements(By.className("card")).size());
        }

        assertFalse(promotions.isEmpty(),
                "Акции не найдены. Проверьте:\n" +
                        "1. Отображаются ли акции при ручном открытии страницы\n" +
                        "2. Корректность данных в модели (${promotions})\n" +
                        "3. CSS-классы карточек в разметке");
    }

    @Test
    void testPopularCarsSection() {
        driver.get(BASE_URL);

        // 1. Ожидаем появления заголовка
        WebElement carsHeader = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//h2[contains(normalize-space(), 'Популярные автомобили')]")
                )
        );

        // 2. Ждём появления карточек (с явным ожиданием)
        wait.until(d -> {
            List<WebElement> cards = d.findElements(By.xpath("//div[contains(@class, 'car-card')]"));
            return !cards.isEmpty() && cards.get(0).isDisplayed();
        });

        // 3. Получаем все карточки
        List<WebElement> carCards = driver.findElements(
                By.xpath("//div[contains(@class, 'car-card') and .//a[contains(@class, 'btn-primary')]]")
        );

        // Дебаг-информация
        if (carCards.isEmpty()) {
            System.out.println("=== DEBUG: Карточки не найдены ===");
            System.out.println("Весь HTML:\n" + driver.getPageSource());
            System.out.println("Элементы с классом 'car-card': " +
                    driver.findElements(By.className("car-card")).size());
        }
        assertFalse(carCards.isEmpty(), "Не найдены карточки автомобилей");

        // 4. Работаем с первой карточкой
        WebElement firstCard = carCards.get(0);

        // Прокручиваем к элементу
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", firstCard);

        // 5. Проверка изображения
        try {
            WebElement carImage = wait.until(d ->
                    firstCard.findElement(By.tagName("img"))
            );
            assertTrue(carImage.isDisplayed(), "Изображение не отображается");
            assertFalse(carImage.getAttribute("src").isBlank(), "Отсутствует src у изображения");
        } catch (Exception e) {
            System.out.println("Ошибка при проверке изображения: " + e.getMessage());
            System.out.println("HTML карточки:\n" + firstCard.getAttribute("outerHTML"));
            throw e;
        }

        // 6. Подготовка к клику
        WebElement detailsBtn = wait.until(d ->
                firstCard.findElement(By.xpath(".//a[contains(@class, 'btn-primary')]"))
        );

        // Дополнительные действия перед кликом
        try {
            // Проверяем, что кнопка кликабельна
            wait.until(ExpectedConditions.elementToBeClickable(detailsBtn));

            // Альтернативный клик через JavaScript
            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", detailsBtn);

            // Ожидаем перехода
            wait.until(ExpectedConditions.urlContains("/cars/"));
        } catch (Exception e) {
            System.out.println("=== DEBUG: Проблема с кликом ===");
            System.out.println("Размеры кнопки: " + detailsBtn.getRect());
            System.out.println("Видимость: " + detailsBtn.isDisplayed());
            System.out.println("Активность: " + detailsBtn.isEnabled());
            System.out.println("HTML кнопки:\n" + detailsBtn.getAttribute("outerHTML"));
            throw e;
        }
    }

    @Test
    void testNavigation() {
        driver.get(BASE_URL);

        String[] expectedLinks = {"Каталог", "Выкуп авто", "Контакты", "Личный кабинет"};
        for (String linkText : expectedLinks) {
            WebElement link = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//a[@class='nav-link' and contains(text(), '" + linkText + "')]")
                    )
            );
            assertTrue(link.isDisplayed());
        }

        // Проверка клика по "Каталог"
        driver.findElement(By.xpath("//a[contains(text(), 'Каталог')]")).click();
        wait.until(ExpectedConditions.urlContains("/catalog"));
    }
}