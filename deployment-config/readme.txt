The deployment-confg folder is added to the final packaged WAR and so is a convenient place to put config files for various deployment environments. 
These configurations are then versioned and can be referenced via symlinks from outside the exploded WAR
e.g Using the standard settings discovery pattern the file {catalina.home}/ApplicationSettings.properties could be a symlink pointing to
different files in the deployment-confg folder depending on the deployment environment.