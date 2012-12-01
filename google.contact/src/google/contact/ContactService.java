package google.contact;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.util.ServiceException;

public class ContactService {
	public static void main(String[] args) throws IOException, ServiceException {
		ContactsService myService = new ContactsService("Contact");
		String accessToken = "ya29.AHES6ZRrC5NoOWzeaiyg0Dbc7EU21CQN9EJG2d1PQg5ymA";
		URL feedUrl = new URL(
				"https://www.google.com/m8/feeds/contacts/default/full?oauth_token="
						+ accessToken + "&max-results=50");
		Query query = new Query(feedUrl);
		query.setMaxResults(50);
		// ContactFeed resultFeed = myService.query(query, ContactFeed.class);
		// &max-results=50
		ContactFeed resultFeed = myService.getFeed(feedUrl, ContactFeed.class);
		System.out.println(resultFeed.getTitle().getPlainText());
		List<ContactEntry> entries = resultFeed.getEntries();
		System.out.println("Number Of Entries: " + entries.size());
		for (ContactEntry entry : entries) {
			if (entry.hasName()) {
				Name name = entry.getName();
				if (name.hasFullName()) {
					String fullNameToDisplay = name.getFullName().getValue();
					if (name.getFullName().hasYomi()) {
						fullNameToDisplay += " ("
								+ name.getFullName().getYomi() + ")";
					}
					System.out.println("\\\t\\\t" + fullNameToDisplay);
				} else {
					System.out.println("\\\t\\\t (no full name found)");
				}
			} else {
				System.out.println("\\\t\\\t (no full name found)");
			}
		}
	}
}
