Standby Dashboard
===============================

Functionality Overview
----------------------

Provides synchronisation and monitoring tools for creating and maintaining a standby version of a web application.

Prerequisites
-------------

* Tomcat
* Maven (as per https://wiki.bright-interactive.com/display/knowhow/Set+up+Maven+on+your+machine)


Installation
------------

### Clone the repo:

    git clone git@github.com:brightinteractive/standby-dashboard.git

### Settings:

General settings for the application are placed in ApplicationSettings.properties   

You can override settings locally by creating a -local variant (e.g. ApplicationSettings-local.properties).

At minimum you'll need to set valid values for

	fileSync.source.directory
	fileSync.destination.directory

Settings will also be looked for in a folder under the catalina conf folder with a name matches that the name of the artifact id. 

e.g. [catalina.home]/conf/standby-dashboard/ApplicationSettings.properties

To limit the need for overrides on the production server use the main settings file to store the sensible default settings
unless doing so is deemed too risky.

e.g. a live Asset Bank URL as the default could result in devs inadvertently making calls to it.

### To run the project:

	mvn tomcat:run
	
### To run the tests:

    mvn test

Rollouts
--------

Having followed the standard Maven project setup instructions

    https://wiki.bright-interactive.com/display/knowhow/Create+a+New+Maven+Project

SNAPSHOT releases are continuously produced and deployed to Artifactory by Jenkins.

Final releases are also manually made via Jenkins

    https://wiki.bright-interactive.com/display/knowhow/Releasing+a+Maven+Artifact+to+the+Bright+Artifactory

To deploy take the appropriate war from Artifactory and copy it onto the server. You can then rename it (it will have a
version suffix) and deploy it using the Tomcat manager. The war generation only includes the default settings files so
for a fresh rollout you'll need to create local settings files on the server (see Settings above).

Alternatively, you can deploy the file directly using the WAR upload field in Tomcat manager after you've renamed it
locally.
