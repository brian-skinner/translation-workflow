# Contents #



---

# How do I get set up to build and run the code? #

## (1.1) Create a Google Translator Toolkit (GTT) account that your development app will use ##

  * You will need to create a Translation Memory (TM) and Translation Glossary in Google Translator Toolkit (GTT), and you will need to create them using a newly made Google account that will be used to manage the TM and the Glossary and the other project documents in GTT.
    1. Create a new Google account and Gmail address on [this page](https://www.google.com/accounts/SignUpWidget)
    1. Using your new account, [log into GTT](http://translate.google.com/toolkit/list)
    1. Click on the [Glossaries](http://translate.google.com/toolkit/list?hl=en#glossaries) link on the left-hand navigation bar, and then click the "Add" button to upload a glossary file
    1. Return to the [Glossaries](http://translate.google.com/toolkit/list?hl=en#glossaries) list, and click on the glossary you just added.  Copy the URL from the browser's address bar, which will include the glossary id ("gid") for the glossary.
    1. Click on the [Translation memories](http://translate.google.com/toolkit/list?hl=en#tms) link on the left-hand navigation bar, and then click the "Add" button to create a new Translation memory.  Be sure to select "Shared with everyone".
    1. Return to the [Translation memories](http://translate.google.com/toolkit/list?hl=en#tms) list, and click on the translation memory you just added.  Copy the URL from the browser's address bar, which will include the translation memory id ("tmid") for the TM.
    1. Make a note of this info, which you will use later in step 1.4:
```
account: my.example.account@gmail.com
password: some-SECret-pasSWord
glossary: http://translate.google.com/toolkit/gl?gid=g_7fc08aaa-7301-4e4e-a6cc-0cc7d404f5c0&hl=en
translation-memory: http://translate.google.com/toolkit/tm?tmid=p_53cd339b570b0d4f&hl=en
```

## (1.2) Setup Eclipse and Mercurial ##

  1. Install Eclipse
    * (TODO: add link)
    * we were using Eclipse version 3.6 at the time we wrote these notes
    * (TODO: recommend picking 'Java')
  1. Install the Google Plugin for Eclipse
    * http://code.google.com/eclipse/
  1. Install the MercurialEclipse plugin
    * http://code.google.com/a/eclipselabs.org/p/mercurialeclipse/
    * http://www.intland.com/blog/google-code-and-mercurial-tutorial-for-eclipse-users/
  1. Set your username in MercurialEclipse
    * in Eclipse: Window > Preferences > Team > Mercurial > Default User Name = me@example.org
  1. Install plugin for editing JSPs
    * Help > Install New Software...  -- to use the Eclipse update manager
    * with this update site: http://download.eclipse.org/webtools/repository/helios/
    * Choose "Web Page Editor (Optional)" from the WTP 3.2.x (Web Tools 3.2.x for Helios)
  1. Tweak settings to use taglibs
    * Follow these instructions: http://someprog.blogspot.com/2010/08/google-app-engine-can-not-find-tag.html

## (1.3) Get the code ##

  1. Copy all the code from our code.google.com site
    * Launch Eclipse
    * File > Import > Mercurial > Clone Existing Mercurial Repository > Next
    * URL: https://translation-workflow.googlecode.com/hg/
    * Username: me@example.org
    * Password: EXamPLEpassWORD (the password from https://code.google.com/hosting/settings)

## (1.4) Copy the template files in the source code ##

  1. Make an `appengine-web.xml` file by copying the `appengine-web.xml.template` file
    * In the `war/WEB_INF folder`, make a copy of the `appengine-web.xml.template` file and name the copy `appengine-web.xml`
  1. Make a `translator-toolkit-account.csv` file by copying the `translator-toolkit-account.csv.template` file
    * In the `war/WEB-INF/content-config/` folder, make a copy of the `translator-toolkit-account.csv.template` file and name the copy `translator-toolkit-account.csv`.  The file should look something like this:
```
app-name,user,password,glossaryId,translationMemoryId
Translation Workflow - gtt,<<email@address>>,<<password>>,<<glossaryId>>,<<translationMemoryId>>
```
    * Edit the `translator-toolkit-account.csv` file to replace the template values with the values from step 1.1 above
```
app-name,user,password,glossaryId,translationMemoryId
Translation Workflow - gtt,my.example.account@gmail.com,some-SECret-pasSWord,g_7fc08aaa-7301-4e4e-a6cc-0cc7d404f5c0,p_53cd339b570b0d4f
```

## (1.5) AppEngine SDK .jar files ##

Hopefully, your project setup should already have a bunch of jar files in war/WEB-INF/lib.  If you don't have them already, you can download a zip file of jars from http://code.google.com/appengine/downloads.html and then find these in the extracted directories /lub/user and /lib/user/orm.  In Eclipse, drag-and-drop the jar files into war/WEB-INF/lib
```
appengine-api-1.0-sdk-1.4.0.jar        (or ...1.4.3.jar)
appengine-api-labs-1.4.0.jar           (or ...1.4.3.jar)
appengine-jsr107cache-1.4.0.jar        (or ...1.4.3.jar)
datanucleus-appengine-1.0.8.final.jar
datanucleus-core-1.1.5.jar
datanucleus-jpa-1.1.5.jar
geronimo-jpa_3.0_spec-1.1.1.jar
geronimo-jta_1.1_spec-1.1.1.jar
jdo2-api-2.3-eb.jar
```


---

# DEPRECATED -- How do I get set up to build and run the code? #

Here are some of the approaches we used in the past, which we should no longer need to use

## (DEPRECATED) Without using Eclipse (approach #2) ##

### (2.1) Getting the code ###

  1. install mercurial (TODO: add link)
  1. find out what your GoogleCode.com password is by visiting https://code.google.com/hosting/settings
  1. create a directory where you plan to work -- let's call it "foo"
    * $ mkdir foo
    * $ cd foo
  1. get a local copy of the translation-workflow repository, as described here: https://code.google.com/p/translation-workflow/source/checkout
    * $ hg clone https://translation-workflow.googlecode.com/hg/ translation-workflow
    * $ user: <me@example.org>  // whatever account you log into code.google.com with
    * $ password: <password from above step, from https://code.google.com/hosting/settings>
  1. add your password to your hgrc file so that you don't have to type it all the time
    * $ cd translation-workflow
    * $ cd .hg
    * edit hgrc, and add:
```
[paths]
default = https://translation-workflow.googlecode.com/hg/
       
[ui]
username = Mister Darcy <me@example.org>
verbose = True
       
[auth]
foobar.prefix = https://translation-workflow.googlecode.com/hg/
foobar.username = me@example.org
foobar.password = EXamPLEpassWORD
```

### (DEPRECATED) Using Eclipse (approach #3) ###

  1. create a new workspace in Eclipse
  1. create a new project in Eclipse
    * File > New > Web Application Project
    * uncheck "Use Google Web Toolkit"
    * Project name: translation-workflow
    * Package: com.google.dotorg.translation\_workflow
    * click "Finish"
  1. copy all the code from our code.google.com site
    * File > Import > Mercurial > Clone Existing Mercurial Repository > Next
    * URL: https://translation-workflow.googlecode.com/hg/
    * YOU WILL SEE AN ERROR: "Directory '.../translation\_workflow' already exists. Please choose a new, not existing directory!
    * Cone directory name: translation-workflow/temp
    * Username: me@example.org
    * Password: EXamPLEpassWORD

### (DEPRECATED) Using Eclipse (approach #4) ###

  1. open Eclipse
  1. File > Import...
  1. General > Existing Projects into Workspace
  1. "Next"
  1. Select root directory > Browse...
  1. select the .../foo/translation-workflow directory from back when you checked out the code (see above)
  1. "OK"
  1. "Finish"
  1. in Package Explorer, open translation-workflow > src > com.google.dotorg.translation\_workflow > Website.java
  1. Run > Run As > Web Application


---

# See also #

## Working with patches in Mercurial ##
  * Mozilla Mercurial FAQ: https://developer.mozilla.org/en/Mercurial_FAQ

## For comparison, here are some other example AppEngine Java projects on code.google.com ##

  * [appengine-mapreduce](http://code.google.com/p/appengine-mapreduce/source/browse/)
  * [gwtguestbook-namespaces](http://code.google.com/p/google-app-engine-samples/source/browse/#svn%2Ftrunk%2Fgwtguestbook-namespaces)
  * [retrieving-gdata-feeds-java](http://code.google.com/p/google-app-engine-samples/source/browse/#svn%2Ftrunk%2Fretrieving-gdata-feeds-java) (INCLUDES gdata .jar files)
  * [chrometophone](http://code.google.com/p/chrometophone/source/browse/)