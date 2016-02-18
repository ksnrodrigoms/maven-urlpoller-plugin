This Maven plugin is designed to be used in a continuous deployment scenario where the command to deploy an application to an app server is asynchronous.

How do you know when the deploy is complete so you can move on to the next step in the continuous deploy pipeline?

This Maven plugin will poll a given url for a specified period (while blocking) until it times out, or gets an expected response (yay, deploy complete!)

See http://kennychua.net/blog/polling-for-deployment-completion-of-a-webapp-in-a-continuous-integration-environment-with-a-maven-plugin for example use


## News ##
  * **30 Dec, 2011:** Version 1.0.3 - Renamed goal to be clearer
  * **28 Dec, 2011:** Version 1.0.1 Bug in failOnFailure logic fixed
  * **16 Dec, 2011:** Version 1.0.0 - Initial release.

## Usage ##
Include the following in your pom.xml file. See the Configuration section for more info about the config parameters.
```
<plugins>
...
  <plugin>
    <groupId>net.kennychua</groupId>
    <artifactId>maven-urlpoller-plugin</artifactId>
    <configuration>
      <pollUrl>http://www.google.com</pollUrl>
      <statusCode>200</statusCode>
      <secondsBetweenPolls>10</secondsBetweenPolls>
      <repeatFor>5</repeatFor>
      <failOnFailure>false</failOnFailure>
    </configuration>
  </plugin>
...
</plugins>
```


## Configuration ##
| **Configuration Parameter** | **Required** | **Description** |
|:----------------------------|:-------------|:----------------|
| pollUrl                     | Yes          | Specify the URL to poll eg http://www.google.com |
| statusCode                  | Yes          | Specify the HTTP status code to expect for a success eg 200 |
| repeatFor                   | Yes          | Specify how many times to try if the statusCode received was not as expected. |
| secondsBetweenPolls         | Yes          | Specify the number of seconds between retries. |
| failOnFailure               | No. Default is true | Specify whether to continue with the build if the poll step does not receive the expected response in the specified period. |

## Goals ##
#### Poll a given URL for a HTTP status code ####

| **Goal** | poll |
|:---------|:-----|
| **Description** | Poll a given URL for a HTTP status code. Specify the number of retries and interval between retries |
| **Bound to** | Maven _pre-integration-test_ lifecycle phase |
