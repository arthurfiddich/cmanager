package google.contact;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.util.ServiceException;

public class ContactService {
	public static void main(String[] args) throws IOException, ServiceException {
		ContactsService myService = new ContactsService("Contact");
		URL feedUrl = new URL(
				"https://www.google.com/m8/feeds/contacts/default/full?oauth_token=ya29.AHES6ZTtsPfQCLxOC5XSQ9hrGu8ivldav4a7nxMTEZYUWnc");
		ContactFeed resultFeed = myService.getFeed(feedUrl, ContactFeed.class);
		System.out.println(resultFeed.getTitle().getPlainText());
	}
}
