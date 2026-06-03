package rahulshettyacademy.pageobjects;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import rahulshettyacademy.AbstractComponents.AbstractComponent;


public class CartPage extends AbstractComponent {
	WebDriver driver;

	@FindBy(css = ".totalRow button")
	WebElement checkoutEle;

	@FindBy(css = ".cartSection h3")
	private List<WebElement> cartProducts;
	
	@FindBy(css = "button[class='btn btn-danger']")
	private List<WebElement> getDeletebuttons;
	
	@FindBy(css = ".ng-star-inserted >h1")
	WebElement emptyCartMessage;

	public CartPage(WebDriver driver) {
		super(driver);
		this.driver = driver;
		PageFactory.initElements(driver, this);

	}

//	public Boolean VerifyProductDisplay(String productName) {
//		Boolean match = cartProducts.stream().anyMatch(product -> product.getText().equalsIgnoreCase(productName));
//		System.out.println("Verify the  product in Cart page");
//		return match;
//
//	}
	public Boolean VerifyProductDisplay(String productName) {

	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	    // Wait up to 10s for either cart items or the empty cart message to appear
	    wait.until(d -> d.findElements(By.cssSelector(".cartSection h3")).size() > 0
	            || d.findElements(By.cssSelector(".ng-star-inserted >h1")).size() > 0);
	    // If empty message present and indicates empty, return false
	    if (isCartEmpty()) {
	        System.out.println("Cart is EMPTY - VerifyProductDisplay returning false");
	        return false;
	    }

	    // Collect the product name elements freshly from the DOM (avoid stale refs)
	    List<WebElement> products = driver.findElements(By.cssSelector(".cartSection h3"));

	    System.out.println("Expected: '" + productName + "'");
	    if (products.isEmpty()) {
	        System.out.println("No products found in cart (products list empty)");
	        return false;
	    }

	    products.forEach(p -> {
	        try {
	            System.out.println("Actual: '" + p.getText().trim() + "'");
	        } catch (StaleElementReferenceException e) {
	            System.out.println("Skipped stale element when logging product names");
	        }
	    });

	    final String expected = (productName == null) ? "" : productName.trim().toLowerCase();
	    boolean found = products.stream().anyMatch(p -> {
	        try {
	            return p.getText().trim().equalsIgnoreCase(expected);
	        } catch (StaleElementReferenceException e) {
	            return false;
	        }
	    });

	    System.out.println("Product match found: " + found);
	    return found;
	}

	public CheckoutPage goToCheckout() {
		checkoutEle.click();
		System.out.println("Click on the Checkout button");
		return new CheckoutPage(driver);
		

	}
	
	// Method to delete all items from the cart
    public void deleteAllItems() throws InterruptedException {
    	List<WebElement> deleteButtons = getDeletebuttons;
    	while (!deleteButtons.isEmpty()) {
          for (WebElement deleteButton : deleteButtons) {
            try {
                deleteButton.click();
                waitForStaleness(deleteButton);
            } catch (StaleElementReferenceException e) {
            	// Handle stale element reference exception
                // Re-locate the elements or handle the situation accordingly
                System.out.println("Stale element reference: " + e.getMessage());
            }
          }
          deleteButtons = getDeletebuttons;
    	}
    }

    // Method to check if the cart is empty
    public boolean isCartEmpty() {
        try {
          // defensive: check if element exists in DOM
        	List<WebElement> elems = driver.findElements(By.cssSelector(".ng-star-inserted >h1"));
        	if (elems.isEmpty()) {
        		// If the empty-cart element isn't present, assume cart is NOT empty
        		return false;
        	}
        	String text = elems.get(0).getText().trim();
        	System.out.println("Cart Message is: '" + text + "'");
            return text.equalsIgnoreCase("No Products in Your Cart !");
        } catch (Exception e) {
            // In case of any unexpected error, assume cart is not empty to avoid false negatives
            System.out.println("Exception while checking empty cart: " + e.getMessage());
            return false;
        }
    }

}