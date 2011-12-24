package net.kennychua.maven_urlpoller_plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class MavenUrlPollerMojoTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MavenUrlPollerMojoTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( MavenUrlPollerMojoTest.class );
    }

    public void testValidParams()
    {
        MavenUrlPollerMojo test = new MavenUrlPollerMojo();
        test.setPollUrl("http://www.google.com");
        test.setStatusCode(200);
        test.setRepeatFor(1);
        test.setSecondsBetweenPolls(1);
        
        try {
			test.execute();
		} catch (MojoExecutionException e) {
			fail("This test should not throw MojoExecutionException ever");
		} catch (MojoFailureException e) {
			fail("This test should not fail as the params are all valid");
		}
    }
    
    public void testValidParamsKnownBadUrl()
    {
        MavenUrlPollerMojo test = new MavenUrlPollerMojo();
        test.setPollUrl("http://www.google222.com");
        test.setStatusCode(200);
        test.setRepeatFor(1);
        test.setSecondsBetweenPolls(1);
        
        try {
			test.execute();
		} catch (MojoExecutionException e) {
			fail("This test should not throw MojoExecutionException ever");
		} catch (MojoFailureException e) {
			assertTrue(e.getMessage().contains("Expected response of HTTP status 200 not found for http://www.google222.com after 1 attempt(s) (1 seconds interval)"));
		}
    }
    
    public void testInvalidUrlSanityCheck()
    {
        MavenUrlPollerMojo test = new MavenUrlPollerMojo();
        test.setPollUrl("www.google22831476.com");
        test.setStatusCode(200);
        test.setRepeatFor(1);
        test.setSecondsBetweenPolls(1);
        
        try {
			test.execute();
			fail("execute method didn't throw when I expected it to" );
		} catch (MojoExecutionException e) {
			fail("This test should not throw MojoExecutionException ever");
		} catch (MojoFailureException e) {
			assertTrue(e.getMessage().contains(MavenUrlPollerMojo.INVALID_POLLURL_MESSAGE));
		}
    }
    
    public void testInvalidStatusCodeSanityCheck()
    {
        MavenUrlPollerMojo test = new MavenUrlPollerMojo();
        test.setPollUrl("www.google.com");
        test.setStatusCode(2000);
        test.setRepeatFor(1);
        test.setSecondsBetweenPolls(1);
        
        try {
			test.execute();
			fail("execute method didn't throw when I expected it to" );
		} catch (MojoExecutionException e) {
			fail("This test should not throw MojoExecutionException ever");
		} catch (MojoFailureException e) {
			assertTrue(e.getMessage().contains(MavenUrlPollerMojo.INVALID_STATUSCODE_MESSAGE));
		}
    }
    
    public void testInvalidRepeatForSanityCheck()
    {
        MavenUrlPollerMojo test = new MavenUrlPollerMojo();
        test.setPollUrl("http://www.google.com");
        test.setStatusCode(200);
        test.setRepeatFor(0);
        test.setSecondsBetweenPolls(1);
        
        try {
			test.execute();
			fail("execute method didn't throw when I expected it to" );
		} catch (MojoExecutionException e) {
			fail("This test should not throw MojoExecutionException ever");
		} catch (MojoFailureException e) {
			assertTrue(e.getMessage().contains(MavenUrlPollerMojo.INVALID_REPEATFOR_MESSAGE));
		}
    }
    
    public void testInvalidSecondsBetweenPollSanityCheck()
    {
        MavenUrlPollerMojo test = new MavenUrlPollerMojo();
        test.setPollUrl("http://www.google.com");
        test.setStatusCode(200);
        test.setRepeatFor(1);
        test.setSecondsBetweenPolls(0);
        
        try {
			test.execute();
			fail("execute method didn't throw when I expected it to" );
		} catch (MojoExecutionException e) {
			fail("This test should not throw MojoExecutionException ever");
		} catch (MojoFailureException e) {
			assertTrue(e.getMessage().contains(MavenUrlPollerMojo.INVALID_SECONDSBETWEENPOLL_MESSAGE));
		}
    }
}
