/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package plus;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

/**
 * Assorted utility methods.
 * 
 * @author Will Norris
 * @author Jenny Murphy
 * @author Lee Denison
 */
public class Util {
	public static final boolean DEBUG = false;

	public static final JsonFactory JSON_FACTORY = new GsonFactory();
	public static final HttpTransport TRANSPORT = new NetHttpTransport();

	/**
	 * Try to load the specified URL in the user's browser. If unable to launch
	 * a browser, prompt the user to open the URL directly.
	 * 
	 * @param uri
	 *            URL to open
	 */
	public static void openBrowser(URI uri) {
		boolean browsed = false;

		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Action.BROWSE)) {
				try {
					desktop.browse(uri);
					browsed = true;
				} catch (IOException e) {
					// sometimes BROWSE appears to be supported but isn't
					if (DEBUG) {
						System.err
								.println("Action.BROWSE failed to start a browser.");
					}
				}
			}
		}

		if (!browsed) {
			try {
				Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler " + uri);
				browsed = true;
			} catch (IOException e) {
				// rundll32 only works on Windows
				if (DEBUG) {
					System.err.println("rundll32 failed to start a browser.");
				}
			}
		}

		if (!browsed) {
			try {
				String browser = "google-chrome";
				Runtime.getRuntime().exec(
						new String[] { browser, uri.toString() });
			} catch (IOException e) {
				// Google Chrome does not appear to be installed
				if (DEBUG) {
					System.err
							.println("Exec google-chrome failed to start a browser.");
				}
			}
		}

		if (!browsed) {
			System.out.println("Please open the following URL to continue: "
					+ uri);
			System.out.println("");
		}
	}

	/**
	 * Try to load the specified URL in the user's browser. If unable to launch
	 * a browser, prompt the user to open the URL directly.
	 * 
	 * @param str
	 *            String representation of URL to open
	 */
	public static void openBrowser(String str) {
		openBrowser(URI.create(str));
	}

}
