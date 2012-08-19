package topq.frankdreiver;

import org.junit.Test;
import org.topq.mobile.frank_client.imp.FrankClientImpl;
import org.topq.mobile.frank_client.interfaces.FrankClient;
/**
 * 
 * @author Bortman Limor
 *
 */
public class FrankTest {
	FrankClient frank = new FrankClientImpl("C:\\Users\\Vadim\\git\\mobile-automation\\new\\2\\frank-client\\src\\resources\\test.properties");


	@Test
	public void nevigte() throws Exception {
		frank.clickOnButtonWithText("Events");

	}

	@Test
	public void voidclickOnButtonWithText() throws Exception {
		frank.clickOnButtonWithText("Back");
	}

	
	@Test
	public void lanch() throws Exception {
		frank.launch();
	}
	
	@Test
	public void clickInTable() throws Exception {
		frank.clickInList(1);
	}
	
	@Test
	public void close() throws Exception {
		frank.closeConnection();
	}

	@Test
	public void enterText() throws Exception {
		frank.enterText(0, "Vadim");
	}
}
