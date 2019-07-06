package com.makebit.filterss;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.makebit.filterss.controllers.OnSwipeTouchListener;
import com.makebit.filterss.models.Article;
import com.makebit.filterss.models.Collection;
import com.makebit.filterss.models.SQLOperation;
import com.makebit.filterss.models.User;
import com.makebit.filterss.restful_api.RESTMiddleware;
import com.makebit.filterss.restful_api.callbacks.CollectionCallback;
import com.makebit.filterss.restful_api.callbacks.SQLOperationCallback;
import com.makebit.filterss.restful_api.callbacks.SQLOperationCallbackLocal;
import com.makebit.filterss.service.SQLiteService;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringEscapeUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Response;
import top.defaults.colorpicker.ColorPickerPopup;

public class ArticleActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    public final static String logTag = "RSSLOG";
    private final String TAG = getClass().getName();
    private RESTMiddleware api;
    private TextToSpeech tts;

    private User user;
    private ArrayList<Collection> collections;

    private String articleBody;
    private String articleTitle;
    private MenuItem ttsPlayItem;

    private WebView articleBodyWebView;
    private boolean collectionsChange;
    private boolean articleReadSend;
    private Context context;
    private Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        context = this;

        Toolbar toolbar = findViewById(R.id.article_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        collectionsChange = false;

        api = new RESTMiddleware(this);

        // Init tts
        Thread initTTSThread = new Thread() {
            @Override
            public void run() {
                tts = new TextToSpeech(context, (TextToSpeech.OnInitListener) context);
            }
        };
        initTTSThread.start();

        // INPUT DATA
        ArticlesListActivity.ArticleDataHolder articleDataHolder = ArticlesListActivity.ArticleDataHolder.getInstance();
        article = articleDataHolder.getArticle();
        user = articleDataHolder.getUser();
        collections = (ArrayList<Collection>) articleDataHolder.getCollections();

        actionbar.setTitle(article.getTitle());

        //Log.d(ArticleActivity.logTag + ":" + TAG, "Article " + article);

        final String articleLink = article.getLink();
        articleTitle = StringEscapeUtils.unescapeHtml(article.getTitle().replaceAll("\\<[^>]*>|\\n", ""));
        articleBody = article.getDescription();
        //int readingTime = article.getReadingTime();

        // SETTERS
        TextView articleTitleTextView = findViewById(R.id.textViewArticleTitle);
        articleTitleTextView.setText(articleTitle);

        TextView articlePubDateTextView = findViewById(R.id.textViewArticlePubDate);
        Date pubDate = article.getPubDate();

        if (pubDate == null) {
            articlePubDateTextView.setVisibility(View.GONE);
        } else {
            String pubDateString = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(pubDate);
            articlePubDateTextView.setText(pubDateString);
        }

        String articleFeedName = article.getFeedObj().getTitle();
        TextView articleFeedNameTextView = findViewById(R.id.textViewArticleFeedName);
        articleFeedNameTextView.setText(articleFeedName);

        ImageView feedIconImageView = findViewById(R.id.imageViewArticleFeedIcon);
        String feedIcon = article.getFeedObj().getIconURL();
        if (feedIcon == null || feedIcon.isEmpty()) {
            feedIconImageView.setVisibility(View.GONE);
        } else {
            Picasso.get().load(feedIcon).into(feedIconImageView);
        }

        ImageView articleImageView = findViewById(R.id.imageViewArticleImage);
        String articleImgLink = article.getImgLink();

        if (Article.checkUrlIsValid(articleImgLink)) {
            Picasso.get()
                    .load(articleImgLink)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.ic_hourglass_empty_black_24dp)
                    .error(R.drawable.ic_error_outline_black_24dp)
                    .noFade()
                    .into(articleImageView);
            // Set the image to third size of the viewport
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            articleImageView.getLayoutParams().height = size.y / 3;
        } else {
            Log.e(ArticleActivity.logTag + ":" + TAG, "EXCEPTION: image url " + articleImgLink + " is not valid");
            articleImageView.setVisibility(View.GONE);
        }

        //Set Article Body View
        articleBodyWebView = findViewById(R.id.textViewArticleBody);

        if (articleBody == null || articleBody.isEmpty() || articleBody.length() < 10) {
            Log.v(ArticleActivity.logTag + ":" + TAG, "Article body is empty");
            articleBodyWebView.setVisibility(View.GONE);
        } else {
            Log.v(ArticleActivity.logTag + ":" + TAG, articleBody);
            articleBodyWebView.setVisibility(View.VISIBLE);
            articleBodyWebView.getSettings().setJavaScriptEnabled(true);
            articleBodyWebView.setVerticalScrollBarEnabled(false);
            articleBodyWebView.setBackgroundColor(Color.parseColor("#FAFAFA"));

            //Spanned spannedBody = Html.fromHtml(articleBody, this, null);
            articleBodyWebView.loadData("<style>img,iframe{display: inline;height: auto;max-width: 100%;}</style>" + articleBody, "text/html; charset=utf-8", "utf-8");

            articleBodyWebView.setOnTouchListener(new OnSwipeTouchListener(ArticleActivity.this) {
                @Override
                public void onSwipeRight() {
                    onBackPressed();
                }

            });
        }

        // Click on open article button
        Button buttonOpenArticle = findViewById(R.id.buttonArticleUrlOpen);
        if (articleLink != null && !articleLink.isEmpty()) {
            buttonOpenArticle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openWebPage(articleLink);
                }
            });
        } else {
            buttonOpenArticle.setVisibility(View.GONE);
        }

        // Article's feedback
        findViewById(R.id.linearLayoutFeedbackGood).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendArticleFeedback(article, 1);
            }
        });
        findViewById(R.id.linearLayoutFeedbackAverage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendArticleFeedback(article, 0);
            }
        });
        findViewById(R.id.linearLayoutFeedbackBad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendArticleFeedback(article, -1);
            }
        });

        // Scroll progess
        final ScrollView positionScrollView = findViewById(R.id.scrollViewArticle);
        final ProgressBar positionProgressBar = findViewById(R.id.progressBar);

        positionScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = positionScrollView.getScrollY() + positionScrollView.getHeight();
                int maxScroll = positionScrollView.getChildAt(0).getHeight();
                float percentageScrolled = (float) scrollY / (float) maxScroll * 100;
                positionProgressBar.setProgress((int) percentageScrolled);

                if (!articleReadSend && percentageScrolled >= 70) {
                    sendArticleRead(article);
                }
            }
        });

        sendArticleOpened(article);

        SQLiteService sqLiteService = SQLiteService.getInstance(getBaseContext());
        sqLiteService.setArticleRead(article.getHashId(), new SQLOperationCallbackLocal() {
            @Override
            public void onLoad(SQLOperation sqlOperation) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Article read saved locally");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(ArticleActivity.logTag + ":" + TAG, "Article read NOT saved locally: " + t.getMessage());
            }
        });

        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    private void sendArticleOpened(final Article article) {
        Log.d(ArticleActivity.logTag + ":" + TAG, "Sending article opened for " + article.getHashId());
        api.addUserOpenedArticle(user.getToken(), article.getHashId(), new SQLOperationCallback() {
            @Override
            public void onLoad(Response<SQLOperation> response) {
                if (response.code() == 200 && response.body() != null && response.body().getAffectedRows() >= 1) {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "sendArticleOpened for " + article.getHashId() + " DONE");
                } else {
                    Log.e(ArticleActivity.logTag + ":" + TAG, "sendArticleOpened for " + article.getHashId() + " returned 0, " + response);
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(ArticleActivity.logTag + ":" + TAG, "sendArticleOpened for " + article.getHashId() + " ERROR " + t.getMessage());
            }
        });

    }

    private void sendArticleRead(final Article article) {
        Log.d(ArticleActivity.logTag + ":" + TAG, "Sending article read for " + article.getHashId());
        articleReadSend = true;

        api.addUserReadArticle(user.getToken(), article.getHashId(), new SQLOperationCallback() {
            @Override
            public void onLoad(Response<SQLOperation> response) {
                if (response.code() == 200 && response.body() != null && response.body().getAffectedRows() >= 1) {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "sendArticleRead for " + article.getHashId() + " DONE");
                } else {
                    Log.e(ArticleActivity.logTag + ":" + TAG, "sendArticleRead for " + article.getHashId() + " returned 0, " + response);
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(ArticleActivity.logTag + ":" + TAG, "sendArticleRead for " + article.getHashId() + " ERROR " + t.getMessage());
            }
        });

    }

    private void sendArticleFeedback(final Article article, final int vote) {
        Log.d(ArticleActivity.logTag + ":" + TAG, "Sending feedback " + vote + " for " + article.getHashId());
        api.addUserFeedbackArticle(user.getToken(), article.getHashId(), vote, new SQLOperationCallback() {
            @Override
            public void onLoad(Response<SQLOperation> response) {
                if (response.code() == 200 && response.body() != null && response.body().getAffectedRows() >= 1) {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "sendArticleFeedback " + vote + " for " + article.getHashId() + " DONE");
                    Snackbar.make(findViewById(android.R.id.content), R.string.thank_you_feedback_submit, Snackbar.LENGTH_LONG).show();

                } else {
                    Log.e(ArticleActivity.logTag + ":" + TAG, "sendArticleFeedback for " + article.getHashId() + " returned 0, " + response);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(ArticleActivity.logTag + ":" + TAG, "sendArticleFeedback " + vote + " for " + article.getHashId() + " ERROR " + t.getMessage());
                Snackbar.make(findViewById(android.R.id.content), R.string.error_connection, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Show the dialog to save the article in a existing collection and for creating a new collection
     */
    public void showCollectionsList() {
        if (collectionsChange) {
            api.getUserCollections(user.getToken(), new CollectionCallback() {
                @Override
                public void onLoad(Response<List<Collection>> response) {
                    final List<Collection> collections = response.body();

                    if (response.code() == 200 && collections != null) {
                        for (Collection collection : collections) {
                            Log.d(ArticleActivity.logTag + ":" + TAG, "Collection: " + collection);
                        }
                        collections.get(0).setTitle(getString(R.string.read_it_later));

                        showCollectionsDialog(collections);

                    } else {
                        Log.e(ArticleActivity.logTag + ":" + TAG, "Multifeed response is: " + response.code());
                    }

                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e(ArticleActivity.logTag + ":" + TAG, "User's collections NOT retrieved: " + t.getMessage());
                    Snackbar.make(findViewById(android.R.id.content), R.string.error_connection, Snackbar.LENGTH_LONG).show();

                }
            });
        } else {
            showCollectionsDialog(collections);
        }
    }

    private void showCollectionsDialog(final List<Collection> collections) {
        new android.app.AlertDialog.Builder(ArticleActivity.this)
                .setTitle(R.string.dialog_add_article_to_collection)
                .setSingleChoiceItems(Collection.toStrings(collections), -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedIndex) {
                                Log.d(ArticleActivity.logTag + ":" + TAG, "Collection " + selectedIndex + " clicked, info: " + collections.get(selectedIndex).toString());
                                addArticleToCollection(article, collections.get(selectedIndex));
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton(R.string.dialog_add_article_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(ArticleActivity.logTag + ":" + TAG, "Creating new collection");
                        createNewCollection(new Collection());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(ArticleActivity.logTag + ":" + TAG, "Dialog closed");
                    }
                })
                .show();
    }


    /**
     * Manages the creation of a new collection
     *
     * @param collection the collection that is going to be created. It is used to pass data between
     *                   the dialog and the color picker dialog
     */
    private void createNewCollection(final Collection collection) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ArticleActivity.this);

        View dialogView = LayoutInflater.from(ArticleActivity.this).inflate(R.layout.dialog_collection_edit, null);

        final TextView collectionTitleTextView = dialogView.findViewById(R.id.editTextCollectionEditTitle);
        final View collectionColor = dialogView.findViewById(R.id.viewCollectionEditColor);

        collectionTitleTextView.setText(collection.getTitle());
        GradientDrawable background = (GradientDrawable) collectionColor.getBackground();
        background.setColor(collection.getColor() == 0 ? Color.BLACK : collection.getColor());

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(R.string.save, null);


        builder.setView(dialogView);
        final android.app.AlertDialog editCollectionDialog = builder.create();

        editCollectionDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = editCollectionDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        collection.setTitle(collectionTitleTextView.getText().toString());
                        collection.setColor(collection.getColor() == 0 ? Color.BLACK : collection.getColor());

                        // Validate the collection name
                        if (collection.getTitle() == null || collection.getTitle().isEmpty()) {
                            collectionTitleTextView.setError(getText(R.string.name_not_empty));
                        } else {
                            Log.d(ArticleActivity.logTag + ":" + TAG, "Saving collection: " + collection.toString());
                            api.addUserCollection(user.getToken(), collection.getTitle(), collection.getColor(), new SQLOperationCallback() {
                                @Override
                                public void onLoad(Response<SQLOperation> response) {
                                    if (response.code() == 200 && response.body() != null && response.body().getAffectedRows() >= 1) {
                                        Log.d(ArticleActivity.logTag + ":" + TAG, "addUserCollection Collection: " + collection.toString() + " saved...");
                                        Snackbar.make(findViewById(android.R.id.content), R.string.collection_created, Snackbar.LENGTH_LONG).show();
                                        Collection newCollection = collection;
                                        newCollection.setId(response.body().getInsertId());
                                        addArticleToCollection(article, newCollection);
                                        collectionsChange = true;
                                    } else {
                                        Log.e(ArticleActivity.logTag + ":" + TAG, "addUserCollection Collection: " + collection.toString() + " returned 0 " + response.code());
                                    }
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    Log.e(ArticleActivity.logTag + ":" + TAG, "addUserCollection Collection " + collection.toString() + "NOT saved..." + t.getMessage());
                                    Snackbar.make(findViewById(android.R.id.content), R.string.error_connection, Snackbar.LENGTH_LONG).show();
                                }

                            });
                            //Dismiss once everything is OK.
                            editCollectionDialog.dismiss();
                        }

                    }
                });
            }
        });


        collectionColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                editCollectionDialog.dismiss(); // dismiss the edit dialog
                new ColorPickerPopup.Builder(ArticleActivity.this)
                        .initialColor(collection.getColor())
                        .enableBrightness(false)
                        .enableAlpha(false)
                        .okTitle(getString(R.string.choose))
                        .cancelTitle(getString(R.string.cancel))
                        .showIndicator(false)
                        .showValue(false)
                        .build()
                        .show(new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                collection.setTitle(collectionTitleTextView.getText().toString());
                                collection.setColor(color);
                                createNewCollection(collection); // restart the edit of the collection with the edited values
                            }

                            @Override
                            public void onColor(int color, boolean fromUser) {

                            }
                        });
            }
        });

        editCollectionDialog.show();
    }

    /**
     * Add the selected article to the selected collection
     *
     * @param article    the article to add to the collection
     * @param collection where to add the article
     */
    private void addArticleToCollection(final Article article, final Collection collection) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        Log.d(ArticleActivity.logTag + ":" + TAG, "Article " +
                article.getTitle() + " " +
                article.getComment() + " " +
                article.getLink() + " " +
                article.getImgLink() + " " +
                sdf.format(article.getPubDate()) + " " +
                user.getId() + " " +
                article.getFeed() + " " +
                collection.getId() + " to collection " + collection.getTitle());

        api.addArticleToCollection(
                user.getToken(),
                article.getTitle(),
                article.getDescription(),
                article.getLink(),
                article.getImgLink(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(article.getPubDate()),
                article.getFeed(),
                collection.getId(),
                new SQLOperationCallback() {
                    @Override
                    public void onLoad(Response<SQLOperation> response) {
                        if (response.code() == 200 && response.body() != null && response.body().getAffectedRows() >= 1) {
                            Log.d(ArticleActivity.logTag + ":" + TAG, "addArticleToCollection Article " + article.getTitle() + " to collection " + collection.getTitle() + " saved");
                            Snackbar.make(findViewById(android.R.id.content), R.string.article_added_to_collection, Snackbar.LENGTH_LONG).show();
                            collectionsChange = true;
                        } else {
                            Log.e(ArticleActivity.logTag + ":" + TAG, "addUserCollection Collection: " + article.getTitle() + " to collection " + collection.getTitle() + " returned 0 " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e(ArticleActivity.logTag + ":" + TAG, "addArticleToCollection Article " + article.getTitle() + " to collection " + collection.getTitle() + " NOT saved" + t.getMessage());
                        Snackbar.make(findViewById(android.R.id.content), R.string.error_adding_article, Snackbar.LENGTH_LONG).show();

                    }
                }
        );
    }

    public void openWebPage(String url) {
        sendArticleRead(article);
        Intent intent = new Intent(this, BrowserActivity.class);
        intent.putExtra(BrowserActivity.URL, url);
        startActivity(intent);
    }

    private void shareArticle() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, article.getTitle());
        i.putExtra(Intent.EXTRA_TEXT, article.getLink());
        startActivity(Intent.createChooser(i, getText(R.string.share_article)));
    }

    /**
     * Used to notify the ArticleListActivity that collections have been changed
     */
    @Override
    public void onBackPressed() {
        if (collectionsChange) {
            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article, menu);

        ttsPlayItem = menu.findItem(R.id.itemReadArticle);
        // Disable play button until tts is initialized
        ttsPlayItem.setEnabled(false);
        ttsPlayItem.getIcon().setAlpha(130);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.itemReadArticle:
                speakOut();
                return (true);
            case R.id.itemSaveArticle:
                showCollectionsList();
                return (true);
            case R.id.itemShareArticle:
                shareArticle();
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }

    /*
     * TTS initialization
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.getDefault());

            // tts.setPitch(5); // set pitch level

            // tts.setSpeechRate(2); // set speech speed rate

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(ArticleActivity.logTag + ":" + TAG, "TTS: Language is not supported");

                /*// missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);*/
            } else {
                Log.d(ArticleActivity.logTag + ":" + TAG, "TTS: init with locale " + Locale.getDefault());
                // Init completed show play button
                if (ttsPlayItem != null) {
                    ttsPlayItem.setEnabled(true);
                    ttsPlayItem.getIcon().setAlpha(255);
                }
            }

        } else {
            Log.e(ArticleActivity.logTag + ":" + TAG, "TTS: init failed");
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    private void speakOut() {
        if (tts.isSpeaking()) {
            Log.d(ArticleActivity.logTag + ":" + TAG, "TTS: stopped");
            // Stop the player
            tts.stop();
            // Show play button
            ttsPlayItem.setIcon(R.drawable.ic_play_circle_outline_white_24dp);
        } else {
            Log.d(ArticleActivity.logTag + ":" + TAG, "TTS: started");
            // Start the player
            tts.speak(articleTitle, TextToSpeech.QUEUE_FLUSH, null);

            speech(articleBody.replaceAll("\\<[^>]*>", ""));

            // Show stop icon
            ttsPlayItem.setIcon(R.drawable.ic_stop_white_24dp);
        }

    }


    private void speech(String charSequence) {

        int position = 0;

        int sizeOfChar = charSequence.length();
        String substring = charSequence.substring(position, sizeOfChar);

        int next = 20;
        int pos = 0;
        while (true) {
            String temp = "";

            try {

                temp = substring.substring(pos, next);
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, temp);
                tts.speak(temp, TextToSpeech.QUEUE_ADD, params);

                pos = pos + 20;
                next = next + 20;

            } catch (Exception e) {
                temp = substring.substring(pos, substring.length());
                tts.speak(temp, TextToSpeech.QUEUE_ADD, null);
                break;

            }

        }

    }

    /**
     @Override public Drawable getDrawable(String source) {
     LevelListDrawable d = new LevelListDrawable();
     Drawable empty = getResources().getDrawable(R.drawable.ic_launcher);
     d.addLevel(0, 0, empty);
     d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

     //Start the AsyncTask that will load the images in the body text
     new LoadImage().execute(source, d);

     return d;
     }




      * AsyncTask that loads the images in the Article's TextBody
     *//*
    class LoadImage extends AsyncTask<Object, Void, Bitmap> {
        private LevelListDrawable mDrawable;

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];
            Log.d(TAG, "doInBackground " + source);
            try {
                InputStream is = new URL(source).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d(TAG, "onPostExecute drawable " + mDrawable);
            Log.d(TAG, "onPostExecute bitmap " + bitmap);
            if (bitmap != null) {
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mDrawable.addLevel(1, 1, d);
                mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                mDrawable.setLevel(1);
                // i don't know yet a better way to refresh TextView
                // TextView articleBodyWebView.invalidate() doesn't work as expected

                CharSequence t = articleBodyWebView.getText();
                articleBodyWebView.setText(t);
            }
        }
    }*/
}
