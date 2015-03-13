# Purpose #

We don't yet have unit tests or Selenium tests.  For the time being, here's a list of suggestions for what to test by hand.


---

# Things to test for #

## sign in ##
  * Make sure that only the "home" page is available for visitors who **have not** signed in
  * Make sure that more than just the home page is available for visitors who **have** not signed in

## Footer ##
  * Does the footer include the app version number, and does it look okay (not something like "2.350510655301528073")
  * Do the footer links work?
    * Translation Workflow Home
    * Wiki
    * Bugs
    * Source

## Nicknames ##
  * Try creating a duplicate nickname -- you should get a red error message to the right of the nickname field
  * Try entering an empty nickname, like "  "
  * Try entering a nickname with Arabic characters
  * Try entering a really long nickname

## Nearest city and Country ##
  * Try entering an empty value, like "  "
  * Try entering Hindi characters
  * Try entering a really long nickname

## Deleting articles from a project ##
  * Setup
    * Have the admin create a project
    * Have the admin create lots of articles
    * Have users claim some of the articles
    * Have users translate some of the claimed articles
    * Have users review some of the translated articles
    * Have the admin delete a bunch of articles, including some unclaimed articles, and some of the translated, claimed, and reviewed articles
  * Make sure that the UI does something reasonable
    * Users cannot claim deleted articles
    * Mid-translation articles don't mysteriously disappear out from under a user

## "Refresh progress" button ##
  * Does the "Refresh progress" button work?
  * Make sure the page does **not** update the status every time it loads (make sure it loads fast)

## "I want a new item to translate" button ##
  * Make sure this works in safari (regression: used to be broken)

## Completed Items table ##
  * On the "my translations" page, make sure values for "Translated by" and "Reviewed by" are not transposed

## Error pages ##
  * Try loading a url that shouldn't exist (404 error)
  * Give a bogus project id to create a crash (e.g. /my\_translations?project=5959595959) (500 error)

