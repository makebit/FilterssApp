# FilterssApp
### Intro

What is FilteRSS? 

*Is a free RSS reader with filtering features for Android.*

The application we've built comes from the need to read news online in a simple and
effective way. In particular, we believe that being able to filter quality and interesting content
among the high number of articles published every day online, is a common problem nowadays.

The main components of the system are:
- **Server side part**, exposing a set of APIs, that are used to perform operations on a
relational database, which contains all the information about the user and the feeds;
- **Client side part**, the Android application, which tasks are to query the API, show
the gathered informations and provide users a point of interaction with the service.
The application is also in charge of gathering and parsing RSS feeds to retrieve and
show articles.



### Glossary

In order to better understand the features and functionalities of the application we provide
some definitions that should clarify some concepts of the application:
- **article** : It is the content produced by a feed. It has some properties, and the feed
decides which ones to provide, such as the title, the link to the page where the
content is published, a publication date, the content of the article, ecc. In the next
pages, we use content with the same meaning of article;
- **feed** : It is the content creator (e.g. “BBC”). It releases its content using the RSS
protocol;
- **multifeed** : it is a set of feeds. A multifeed is created and managed by the user (e.g. a
multifeed called “News” could contains different feeds such as “BBC”, “La Stampa”
and “The Guardian”);
- **collection** : It is a set of saved articles. A collection is created and managed by the
user. The user can decide to save articles storing them in different collections (e.g.
an article about a science discovery can be saved in the “Science” collection).



### Features

Here we list the core functionalities that can be found in the application.

- Signup / Login:

  - the user can sign up in the application, creating an account. The account will
    contains all the user’s information (more on this in the Data Design chapter);
  - the user can login in the application, retrieving all his information from the
    server side of the application.

- Multifeed:

  - the user can create a multifeed, giving it:
    - a name;
    - a color. The articles published by the feed that belongs to one
      multifeed with a color will have that color indication near them. In this
      way the user can easily distinguish if an articles belongs to one or
      another multifeed (more on this in the Application Design chapter);
    - a rating. It will be used in order to sort and filter articles (more on this
      in the Filtering chapter);
  - the user can decide which feed to add in each multifeed;
  - the user can view all the articles belonging to:
    - all the multifeeds;
    - a particular multifeed;
    - a single feed.
  - the user can modify a multifeed, and delete it.

- Feeds:

  - the user can add a feed to a multifeed;
  - each feeds has a name and belongs to a category. The user can perform a
    textual search, and sort feeds by their category. The user can also sort feeds
    by language (currently only English and Italian languages are supported);

- Collections:

  - the user can create a collection, giving it:
    - a name;
    - a color. The articles saved in the collection will have this color
      indication near them.
  - the user can add an article to a collection;
  - the user can remove an article from a collection;
  - the user can view all the articles belonging to a collection;
  - the user can modify a collection, and delete it.
  - a collection named “Read it later” is created by default and the user can use
    this collection to store articles that he wants to read it later on;

- Articles:

  - the user can read an article from the application. Some feeds provide the full
    text of an article while others only an excerpt and for this reason we also
    provide the possibility to open the article in a browser;

  - the user can read the article from the in-app browser or using an external
    browser;

  - the user can share the article (e.g. with a messaging app);

  - the user can use the TTS (Text-To-Speech) feature to read aloud the article;

  - the user can give a feedback for the article. It will be then used by the sorting
    algorithm (more on this in the Filtering chapter);

    

-------------

----------

