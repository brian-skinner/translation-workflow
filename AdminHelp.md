# Contents #



---


# For volunteer users #

## How do I get started? ##

NOTE: Before a volunteer can start using the app, the website owner must have already set up the site:
  * for a list of site setup steps, see [How do I get a site up and running?](https://code.google.com/p/translation-workflow/wiki/AdminHelp?ts=1309475017&updated=AdminHelp#How_do_I_get_a_site_up_and_running?)

Here are the how-to steps for a volunteer who wants to do a translation:
  1. Visit http://...  (whatever URL is being used for the site)
  1. Sign in (if you aren't already signed in)
  1. Click on the "my profile" tab
  1. Enter a nickname, select one of the languages in which a project has already been started, and click "Save"
  1. Click on the "my translations" tab
  1. To see a list of article to translate, click on the "Try your luck" button, or enter a search term and click "Search"
  1. Click on one of the "I will translate this" buttons
  1. Click on the "edit translation" link
  1. In Google Translator Toolkit, translate the article (for help with Google Translator Toolkit, see the Google Translator Toolkit help info)
  1. In Google Translator Toolkit, when you have finished your translation, mark translation as complete (by clicking on "Edit" > "Translation Complete") and then click on "Save and close"
  1. When you are back in the translation-workflow app, click on the "Refresh progress" button to see the "Status" column change from "0% translated" to "100% translated". (If the status does not change, you may have to wait a few minutes and then try clicking "Refresh progress" again.)
  1. When the article shows up as "100% translated", then you can click on the "Request a review" button for that article.  After you click on "Request a review", all of the other volunteers will then see that article listed in their "My Items to Review" lists.  The article will not show up in your own "My Items to Review" list, because you are not allowed to review your own article.

## On the "my translations" page, what are the 4 sections for? ##

All participants will see all three sections of the "my translations" page.

**My Translations**

This section shows the list of articles that you have volunteered to translate, and that you may have started working on.  You can also get more articles to translate by clicking on "Show / hide items to translate".

**My Newly Authored Articles**

This section shows the list of articles that you have written yourself.  You can also add a new article by entering the URL of the article and clicking "Add".

**My Articles to Review**

This section shows the list of articles that you have volunteered to review, and may have started to review already.  You can also get more items to review by clicking on "Show / hide items to review", but that feature will only appear once other participants have already finished translating some articles and have marked them as ready for review.

**My completed Articles**

This section shows the list of articles that you translated which are now completely finished: articles which are 100% translated and have been reviewed by another participant.



---


# For the website owner #

## How do I get a site up and running? ##

Before your volunteers can start using the app, you have to get a website set up for them to use:
  * Steps the website owner does
    1. Decide on languages
      * The website owner needs to decide what languages should appear in the list of languages that each user see on their profile page.  Make a list like this, with the languages listed in the order you want them to appear:
```
AF,Afrikaans
ST,Setswana
ZU,IsiZulu
EN,English
FR,French
DE,German
```
    1. Create mailing lists (optional)
      * The website owner can create mailing lists that volunteers can use to discuss the project.  You can create one mailing list for each language, or you can have mailing lists that are shared across languages, or you can choose not to have mailing lists.  You can create mailing lists by visiting [Google Groups](http://groups.google.com).  Make a list like this, to take the different Google Groups you just created and assign them to the different languages (by language code):
```
AF,http://groups.google.com/group/translation-workflow-example-group
ST,http://groups.google.com/group/translation-workflow-example-group
ZU,http://groups.google.com/group/translation-workflow-example-group
```
    1. Decide on the website name, URL, and detail text
      * The website owner needs to decide on a name and a URL for the website, as well as a logo, intro text to appear on the home page, and a list of links that should appear at the footer of every page (for privacy policy, terms of service, etc.).  Make a list of all this info for your deployment engineer:
```
website-name: My New Translator Community
website-logo: my_new_logo.png
homepage-text: Welcome to our brand new website.  Sign in to see all the projects we are working on...
homepage-graphic: home_page_image.png
footer-links: <a href="http://.../.../...">About my website</a> - <a href="http://.../.../...">Report a problem</a> - <a href="http://.../.../...">Contact us</a> 
```
    1. Set up your Google Translator Toolkit (GTT) account
      * The website owner needs to create a Translation Memory (TM) and Translation Glossary in Google Translator Toolkit (GTT), and creates them using a newly made Google account that will be used to manage the TM and the Glossary and the other project documents in GTT.
        1. Create a new Google account and Gmail address on [this page](https://www.google.com/accounts/SignUpWidget)
        1. Using your new account, [log into GTT](http://translate.google.com/toolkit/list)
        1. Click on the [Glossaries](http://translate.google.com/toolkit/list?hl=en#glossaries) link on the left-hand navigation bar, and then click the "Add" button to upload a glossary file
        1. Return to the [Glossaries](http://translate.google.com/toolkit/list?hl=en#glossaries) list, and click on the glossary you just added.  Copy the URL from the browser's address bar, which will include the glossary id ("gid") for the glossary.
        1. Click on the [Translation memories](http://translate.google.com/toolkit/list?hl=en#tms) link on the left-hand navigation bar, and then click the "Add" button to create a new Translation memory.  Be sure to select "Shared with everyone".
        1. Return to the [Translation memories](http://translate.google.com/toolkit/list?hl=en#tms) list, and click on the translation memory you just added.  Copy the URL from the browser's address bar, which will include the translation memory id ("tmid") for the TM.
        1. Make a note for your deployment engineer with all this new info:
```
account: example@gmail.com
password: some-secret-password
glossary: http://translate.google.com/toolkit/gl?gid=g_7fc08aaa-7301-4e4e-a6cc-0cc7d404f5c0&hl=en
translation-memory: http://translate.google.com/toolkit/tm?tmid=p_53cd339b570b0d4f&hl=en
```
    1. Pick "admin" users for the website (optional)
      * The website owner can choose a few colleagues who should have "admin" access to the website so that they will be able to create new translation projects or administer projects.  Make a list of these people's email accounts for your deployment engineer:
```
amy@example.com
joe@gmail.com
pat@gmail.com
```
  * Steps the deployment engineer does
    1. Make configuration files for the website
      * Get the code and compile it.  For more info see DeveloperHowTo
      * Create a set of configuration files that capture all of the info provided by the website owner (website name and website customizations, Translation Memory id, Glossary id, etc.).  Your config files should match the names and formats of the config flies in these two directories of the original source code:
        * the [war/site-config directory](https://code.google.com/p/translation-workflow/source/browse/#hg%2Fwar%2Fsite-config)
        * the [war/WEB-INF/content-config directory](https://code.google.com/p/translation-workflow/source/browse/#hg%2Fwar%2FWEB-INF%2Fcontent-config)
    1. Deploy the website
      * Deploy the app to App Engine.  For more info see these pages:  [Uploading Your Application](http://code.google.com/appengine/docs/java/gettingstarted/uploading.html), [Deploying your Application on your Google Apps URL](http://code.google.com/appengine/articles/domains.html), [Using the Google Plugin for Eclipse](http://code.google.com/appengine/docs/java/tools/eclipse.html#Uploading_to_Google_App_Engine) -- note that this step may require getting a new App Engine app id, if this is the first time you're deploying the site
    1. Grant admin access to admin users
      * use the appengine console to grant access (typically "Viewer" Role access) to the accounts of the people who will be serving as translation project admins.




---


# For translation project admins (admins) #

## What can an admin do? ##

If you have been given "admin" level access on the site, you can do all the same things that a regular user can do, plus you can also
  * create a new project with the "Create a new project" button on the "all projects tab
  * edit any project to set: name, language, and description of each project
  * add articles to any project by copying and pasting the CSV article format list from the bottom of an existing project page into the "Add articles" section of a new project
  * delete any project, or remove articles from any project
  * see details about the completed articles in a project, and copy that info over into a spreadsheet to work with it more
  * see a "leaderboard" that shows how many articles the most active volunteers have completed, and how those articles were scored

## How do I create a simple example project? ##

To create simple example project:
  1. Visit the website
  1. Sign in, using an account that has admin access
  1. Click on the "all projects" tab
  1. On the "All projects" page, click on the "Create a new project" button
  1. On the "Project Overview" page
    * click on the pink button labeled "start the Simple Example Project"
    * click on the "Language" drop-down menu to select what language the project volunteers will be creating content in
    * do **not** change the name of the project
    * click "Save"
    * scroll down, and click on the pink button labeled "start the 'Simple Example Project' articles"
      * the text box for adding articles will now be filled in with a CSV list that specifies a couple dozen articles
    * click "Add articles"
      * the "Articles" section of the page will now have a table with one row per article, with links to the articles

## How do I create a real project? ##

  1. Visit the website
  1. Sign in, using an account that has admin access
  1. Click on the "all projects" tab
  1. On the "All projects" page, click on the "Create a new project" button
  1. On the "Project Overview" page
    * fill in the **Name** and **Description** that users will see for the project
    * click on the "Language" drop-down menu to select what language the project volunteers will be creating content in
    * click "Save"
  1. Use a spreadsheet to compile a list of the articles you want to include in the project.
    * The spreadsheet should have exactly four columns, in this order: Title Url Category Difficulty
  1. From the spreadsheet, use "save as" or "export" to create a CSV format list of the articles
    * The rows in the CSV format list should look something like this (if you have CSV with quotes):
```
"Pond","http://en.wikipedia.org/wiki/Pond","geography","Easy"
"River","http://en.wikipedia.org/wiki/River","geography","Easy"
"Spinach","http://en.wikipedia.org/wiki/Spinach","vegitables","Easy"
"Stream","http://en.wikipedia.org/wiki/Stream","geography","Easy"
"Turnip","http://en.wikipedia.org/wiki/Turnip","vegitables","Easy"
"Valley","http://en.wikipedia.org/wiki/Valley","geography","Easy"
```
    * or like this, if you have CSV without quotes:
```
Pond,http://en.wikipedia.org/wiki/Pond,geography,Easy
River,http://en.wikipedia.org/wiki/River,geography,Easy
Spinach,http://en.wikipedia.org/wiki/Spinach,vegitables,Easy
Stream,http://en.wikipedia.org/wiki/Stream,geography,Easy
Turnip,http://en.wikipedia.org/wiki/Turnip,vegitables,Easy
Valley,http://en.wikipedia.org/wiki/Valley,geography,Easy
```
  1. On the "Project Overview" page
    * copy and paste the CSV list into the add articles text box
    * click "Add articles"
      * the "Articles" section of the page will now have a table with one row per article, with links to the articles

## How do I create a duplicate of my project in a new language? ##

  1. Visit the website
  1. Sign in, using an account that has admin access
  1. Click on the "all projects" tab
  1. Click on the link for the project that you want to make a copy of
  1. Scroll down to the section labeled "Export: Article list in CSV format"
  1. Copy the CSV article list
    * Click in the text box that has the article list
    * Select all (try using ctrl-a, or command-a, or right clicking to get a "Select All" menu item)
  1. Click on the "all projects" tab
  1. Click on the "Create a new project" button
  1. Paste in the CSV article list
    * Scroll down to the "Articles" section, click in the big text box, paste
  1. Scroll back up, fill in a project Name and Description, and select a Language
  1. Click "Save"
    * the "Articles" section of the page will now have a table with one row per article, with links to the articles


---


# For software developers #

See the DeveloperHowTo page.



---


# Terminology #

  * **Google Translate** - an automatic machine pre-translation service (see [Google Translate](http://en.wikipedia.org/wiki/Google_Translate))
  * **Google Translator Toolkit** (GTT) - a web site where human translators can edit the pre-translations generated by Google Translate (see [Google Translator Toolkit](http://en.wikipedia.org/wiki/Google_Translator_Toolkit))
    * **Document** - a document to be translated, such as a Wikipedia page, an html web page, a text file, etc.
    * **Glossary** - multi-lingual glossaries for translators to refer to while they're working
  * **Google App Engine** (GAE) - a platform for hosting web applications (see [Google App Engine](http://en.wikipedia.org/wiki/Google_App_Engine))
  * **translation-workflow**
    * **website** - a site that the translation-workflow app has been deployed to
    * **project** - a set of items to be translated to a specific language (for example: the "2010 Arabic Wikipedia Health Content project")
    * **translation** - a single item that has been translated
    * **item** - something to be translated, such as a dictionary definition or a Wikipedia page
    * **lexicon** - a collection of terms and definitions to be translated
    * **profile** - a user's profile info, including nickname and languages they're fluent in
  * file formats
    * **csv**
    * **xdxf** - XML Dictionary eXchange Format(see [XDXF](http://en.wikipedia.org/wiki/XDXF))
