package net.kennychua.maven_urlpoller_plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Goal which polls a given URL for a HTTP status code
 * 
 * @goal pre-integration-test
 * 
 * @phase poll
 */
public class MavenUrlPollerMojo extends AbstractMojo {
	/**
	 * URL to poll
	 * 
	 * @parameter expression="${urlpoller.pollurl}"
	 * @required
	 */
	private String pollUrl;

	/**
	 * HTTP Response Code to expect upon successful poll of URL
	 * 
	 * @parameter expression="${urlpoller.expectedhttpstatuscode}"
	 * @required
	 */
	private int statusCode;

	/**
	 * How many times to poll
	 * 
	 * @parameter expression="${urlpoller.repeatfor}"
	 * @required
	 */
	private int repeatFor;

	/**
	 * Interval between polls
	 * 
	 * @parameter expression="${urlpoller.secondsbetweenpolls}"
	 * @required
	 */
	private int secondsBetweenPolls;

	/**
	 * Boolean to fail build on failure
	 * 
	 * @parameter expression="${urlpoller.intervalbetweenpolls}"
	 *            default-value=false
	 */
	private boolean failOnFailure;

	// All valid HTTP status codes, although 200 is probably the only one that
	// will be applicable
	private static final int[] validHttpStatuses = { 100, 101, 102, 103, 122,
			200, 201, 202, 203, 204, 205, 206, 207, 208, 226, 300, 301, 302,
			303, 304, 305, 306, 307, 308, 400, 401, 402, 403, 404, 405, 406,
			407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 418, 422,
			423, 424, 425, 426, 428, 429, 431, 444, 449, 450, 499, 500, 501,
			502, 503, 504, 505, 506, 507, 508, 509, 510, 511, 598, 599 };

	public static final String INVALID_POLLURL_MESSAGE = "pollUrl must begin with http:// or https://";
	public static final String INVALID_STATUSCODE_MESSAGE = "statusCode provided is not a valid HTTP status code";
	public static final String INVALID_REPEATFOR_MESSAGE = "repeatFor must be greater than 0";
	public static final String INVALID_SECONDSBETWEENPOLL_MESSAGE = "secondsBetweenPolls must be greater than 0";
			
	public String getPollUrl() {
		return pollUrl;
	}

	public void setPollUrl(String pollUrl) {
		this.pollUrl = pollUrl;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getRepeatFor() {
		return repeatFor;
	}

	public void setRepeatFor(int repeatFor) {
		this.repeatFor = repeatFor;
	}

	public int getSecondsBetweenPolls() {
		return secondsBetweenPolls;
	}

	public void setSecondsBetweenPolls(int secondsBetweenPolls) {
		this.secondsBetweenPolls = secondsBetweenPolls;
	}
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		int retCode = 0;

		inputSanityCheck();

		// Poll code
		for (int i = 1; i <= repeatFor; i++) {
			System.out
					.println(String
							.format("Polling URL %s for HTTP status code %d - Attempt %d of %d (%d second interval)",
									pollUrl, statusCode, i, repeatFor,
									secondsBetweenPolls));
			URL url = null;
			HttpURLConnection connection;
			try {
				url = new URL(pollUrl.toString());
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.connect();
				int code = connection.getResponseCode();

				// Once we find the expected statusCode, report success and stop
				if (code == statusCode) {
					retCode = 0;
					break;
				}
			} catch (MalformedURLException e) {
				retCode = 1;
			} catch (IOException e) {
				retCode = 1;
			}

			// Sleep code
			try {
				Thread.sleep(secondsBetweenPolls * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (!failOnFailure) {
			// If ever retCode is more than 1, it means error(s) have occurred
			if (retCode > 0) {
				throw new MojoFailureException(
						String.format(
								"Expected response of HTTP status %d not found for %s after %d attempt(s) (%d seconds interval)",
								statusCode, pollUrl, repeatFor,
								secondsBetweenPolls));
			}
		}
	}

	private void inputSanityCheck() throws MojoFailureException {
		if(!ArrayUtils.contains(validHttpStatuses, statusCode)) {
			throw new MojoFailureException(INVALID_STATUSCODE_MESSAGE);
		}
		if (!pollUrl.startsWith("http")) {
			throw new MojoFailureException(INVALID_POLLURL_MESSAGE);
		}
		if(!(repeatFor > 0)) {
			throw new MojoFailureException(INVALID_REPEATFOR_MESSAGE);
		}
		if(!(secondsBetweenPolls > 0)) {
			throw new MojoFailureException(INVALID_SECONDSBETWEENPOLL_MESSAGE);
		}
	}
}
