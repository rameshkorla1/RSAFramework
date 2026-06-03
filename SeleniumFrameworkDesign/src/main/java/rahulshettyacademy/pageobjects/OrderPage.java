package rahulshettyacademy.pageobjects;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import rahulshettyacademy.AbstractComponents.AbstractComponent;


public class OrderPage extends AbstractComponent {
	WebDriver driver;

	@FindBy(css = ".totalRow button")
	WebElement checkoutEle;

	@FindBy(css = "tr td:nth-child(3)")
	private List<WebElement> productNames;

	public OrderPage(WebDriver driver) {
		super(driver);
		this.driver = driver;
		PageFactory.initElements(driver, this);

	}

	public Boolean VerifyOrderDisplay(String productName) {
		// wait for orders table cells to be present
		By locator = By.cssSelector("tr td:nth-child(3)");
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
		} catch (Exception e) {
			System.out.println("Orders table items not present immediately: " + e.getMessage());
		}

		List<WebElement> products = driver.findElements(locator);
		System.out.println("Looking for order: '" + productName + "'");
		if (products.isEmpty()) {
			System.out.println("No products found on Orders page (product list empty)");
			return false;
		}
		products.forEach(p -> {
			try {
				System.out.println("Order contains: '" + p.getText().trim() + "'");
			} catch (Exception ex) {
				System.out.println("Skipped element when logging orders: " + ex.getMessage());
			}
		});
		final String expected = (productName == null) ? "" : productName.trim().toLowerCase();
		boolean found = products.stream().anyMatch(p -> {
			try {
				return p.getText().trim().equalsIgnoreCase(expected);
			} catch (Exception e) {
				return false;
			}
		});
		System.out.println("Order match found: " + found);
		return found;

	}


}