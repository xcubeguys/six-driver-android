package com.tommy.driver.socialshare;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.tommy.driver.R;
import com.tommy.driver.ShareReferralActivity;
import com.tommy.driver.adapter.Constants;
import com.tommy.driver.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by javier on 24/04/15.
 **/
public class SocialShare {
    private Activity activity;
    private ArrayList<Integer> socialNetworks;
    private SocialImages socialImages;

    private String subject, message, imagePath, ReferralCode;

    private Uri fbphotoURI;

    private Bitmap bitMap;

    private ShareDialog shareDialog;

    private File iamge_file;

    /* *
     * Custom social networks and custom images
     * @param activity My activity
     * @param iWantThisNetworks I want to display this list of social networks
     * @param socialImages Custom social images, see SocialImages constructor
     *//*
    public SocialShare(Activity activity, ArrayList<Integer> iWantThisNetworks, SocialImages socialImages) {
        this.activity = activity;
        this.socialNetworks = iWantThisNetworks;
        this.socialImages = socialImages;
    }

    *//* *
     * All social networks and custom images
     * @param activity My activity
     * @param socialImages Custom social images, see SocialImages constructor
     *//*
    public SocialShare(Activity activity, SocialImages socialImages) {
        this.activity = activity;
        this.socialImages = socialImages;
        addAllSocial();
    }

    *//* *
     * Custom social networks, default images
     * @param activity My activity
     * @param iWantThisNetworks I want to display this list of social networks
     *//*
    public SocialShare(Activity activity, ArrayList<Integer> iWantThisNetworks) {
        this.activity = activity;
        this.socialNetworks = iWantThisNetworks;
        this.socialImages = new SocialImages();
    }*/

    /**
     * Default constructor:
     * All networks and default images
     *
     * @param activity My activity
     */
    public SocialShare(Activity activity) {

        this.activity = activity;
        socialImages = new SocialImages();
        addAllSocial();
    }

    private static void drawText(Canvas canvas, Paint paint, String text) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = ((canvas.getWidth() / 2) - (bounds.width() / 2)) - 100;
        int y = ((canvas.getHeight() / 2) - (bounds.height() / 2)) + 80;
        paint.setTextSize(70);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(text, x, y, paint);
    }

    private void addAllSocial() {

        FacebookSdk.sdkInitialize(activity);
        shareDialog = new ShareDialog(activity);

        this.socialNetworks = new ArrayList<>();
        this.socialNetworks.add(SocialNetwork.WHATSAPP);
        this.socialNetworks.add(SocialNetwork.FACEBOOK);
        this.socialNetworks.add(SocialNetwork.TWITTER);
        this.socialNetworks.add(SocialNetwork.INSTAGRAM);
        this.socialNetworks.add(SocialNetwork.PLUS_GOOGLE);
        this.socialNetworks.add(SocialNetwork.TELEGRAM);
        this.socialNetworks.add(SocialNetwork.GMAIL);
        this.socialNetworks.add(SocialNetwork.LINKEDIN);
        this.socialNetworks.add(SocialNetwork.VINE);
        this.socialNetworks.add(SocialNetwork.HANGOUTS);
        this.socialNetworks.add(SocialNetwork.PINTEREST);
        this.socialNetworks.add(SocialNetwork.LINE);
        this.socialNetworks.add(SocialNetwork.SNAPCHAT);
    }

    /**
     * Check what social networks has installed the user
     *
     * @return List of social Networks installed
     */
    private ArrayList<Integer> getUserNetworks() {
        final ArrayList<Integer> userNetworks = new ArrayList<>();
        // go over social networks
        for (Integer i : socialNetworks) {
            if (testById(i)) // user have this network
                userNetworks.add(i); // add it
        }
        return userNetworks;
    }

    /**
     * Display default share UI
     */
    public View getDefaultShareUI() {
        // get user networks (With id + image)
        final ArrayList<SocialNetwork> items = getSocialNetworkList();

        //fbphotoURI = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", convertToPath());

        try {
            iamge_file = convertToPath();
            fbphotoURI = Uri.fromFile(iamge_file);

            imagePath = fbphotoURI.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // build gridview
        final Resources r = activity.getResources();
        int dp64 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, r.getDisplayMetrics());
        final View v = View.inflate(activity, R.layout.intent_share, null);

        final GridView gridview = (GridView) v.findViewById(R.id.grid);
        gridview.setAdapter(new SocialAdapter(activity, items, dp64));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final SocialNetwork object = items.get(position);
                shareIntentById(object.getId());
            }
        });

        if (items.size() == 0)
            v.findViewById(R.id.no_results).setVisibility(View.VISIBLE);

        return v;
    }

    private ArrayList<SocialNetwork> getSocialNetworkList() {
        final ArrayList<SocialNetwork> items = new ArrayList<>();
        final ArrayList<Integer> userNetworks = getUserNetworks();
        for (Integer i : userNetworks) {
            items.add(new SocialNetwork(i, socialImages.getImageById(i)));
        }
        return items;
    }

    /****************************
     *
     * Intents for share content
     *
     ****************************/

    private void shareIntentById(int id) {

        switch (id) {

            case SocialNetwork.WHATSAPP:
                shareIntent(SocialNetwork.WHATSAPP_PACKAGE);
                break;
            case SocialNetwork.FACEBOOK:
                //shareIntent(SocialNetwork.FACEBOOK_PACKAGE);
                String text = activity.getString(R.string.share_text) + "\n" + activity.getString(R.string.sms_text_last);
                String text1 = "Your Referral Code is " + getReferralCode() + ".";

                LogUtils.i("fb error" + ShareDialog.canShow(ShareLinkContent.class));

                if (ShareDialog.canShow(SharePhotoContent.class) || ShareDialog.canShow(ShareLinkContent.class)) {

                    if (fbphotoURI != null && !fbphotoURI.toString().isEmpty() && ShareDialog.canShow(SharePhotoContent.class)) {

                        LogUtils.i("inside if the success");

                        if (bitMap == null)
                            LogUtils.i("null bitmap");

                        SharePhoto photo = new SharePhoto.Builder()
                                .setBitmap(bitMap)
                                .setImageUrl(Uri.parse(Constants.CATEGORY_LIVE_URL))
                                .build();

                        SharePhotoContent content = new SharePhotoContent.Builder()
                                //.setContentUrl(Uri.parse(Constants.CATEGORY_LIVE_URL))
                                .addPhoto(photo)
                                .build();
                        LogUtils.i("inside if the fb");
                        shareDialog.show(content);
                    } else {
                        ShareLinkContent linkContent;
                        LogUtils.i("inside else the fb");
                        linkContent = new ShareLinkContent.Builder()
                                .setContentTitle(text1)
                                .setContentDescription(text)
                                .setContentUrl(Uri.parse(Constants.CATEGORY_LIVE_URL))
                                .build();
                        LogUtils.i("inside else the");
                        shareDialog.show(linkContent);
                    }
                }

                break;

            case SocialNetwork.TWITTER:
                shareIntent(SocialNetwork.TWITTER_PACKAGE);
                break;

            case SocialNetwork.INSTAGRAM:

                //shareIntent(SocialNetwork.INSTAGRAM_PACKAGE);
                instagramShareIntent();
                break;
            case SocialNetwork.PLUS_GOOGLE:
                shareIntent(SocialNetwork.PLUS_GOOGLE_PACKAGE);
                break;
            case SocialNetwork.TELEGRAM:
                shareIntent(SocialNetwork.TELEGRAM_PACKAGE);
                break;
            case SocialNetwork.GMAIL:
                shareIntent(SocialNetwork.GMAIL_PACKAGE);
                break;
            case SocialNetwork.LINKEDIN:
                shareIntent(SocialNetwork.LINKEDIN_PACKAGE);
                break;
            case SocialNetwork.VINE:
                shareIntent(SocialNetwork.VINE_PACKAGE);
                break;
            case SocialNetwork.HANGOUTS:
                shareIntent(SocialNetwork.HANGOUTS_PACKAGE);
                break;
            case SocialNetwork.PINTEREST:
                shareIntent(SocialNetwork.PINTEREST_PACKAGE);
                break;
            case SocialNetwork.LINE:
                shareIntent(SocialNetwork.LINE_PACKAGE);
                break;
            case SocialNetwork.SNAPCHAT:
                shareIntenttoContacts();
                // shareIntent(SocialNetwork.SNAPCHAT_PACKAGE);
                break;
        }
    }

    private void instagramShareIntent() {

        if (iamge_file == null || !iamge_file.exists()) {
            LogUtils.e("file " + "no image file at location ");
        } else {
            String type = "image";
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType(type);
            //Uri photoURI = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", convertToPath());
            //Uri photoURI = Uri.fromFile(convertToPath());
            share.putExtra(Intent.EXTRA_STREAM, fbphotoURI);
            share.setPackage("com.instagram.android");
            share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(share);
        }
    }

    private void shareIntent(final String packageName) {
        if (imagePath != null && !imagePath.isEmpty()) {
            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setPackage(packageName);
            intent.setType("image");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (subject != null && !subject.isEmpty())
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            if (message != null && !message.isEmpty())
                intent.putExtra(Intent.EXTRA_TEXT, message);
            intent.putExtra(Intent.EXTRA_STREAM, fbphotoURI);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(Intent.createChooser(intent, "Share"));
        } else {
            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setPackage(packageName);
            intent.setType("text/plain");
            if (subject != null && !subject.isEmpty())
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            if (message != null && !message.isEmpty())
                intent.putExtra(Intent.EXTRA_TEXT, message);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(Intent.createChooser(intent, "Share"));
        }
    }

    private void shareIntenttoContacts() {

        Intent i = new Intent(activity, ShareReferralActivity.class);
        i.putExtra("subject", subject);
        i.putExtra("referralcode", message);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(i);
    /*    final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            if (subject != null && !subject.isEmpty())
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            if (message != null && !message.isEmpty())
                intent.putExtra(Intent.EXTRA_TEXT, message);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(Intent.createChooser(intent ,"Share"));*/
    }

    private File convertToPath() {

        Bitmap bitmap;
        bitmap = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_referral_new);
        Point p = new Point();
        Point p1 = new Point();

        p.set(10, 800);
        p1.set(10, 900);

        String text = activity.getString(R.string.share_text);
        String text1 = "Your Referral Code is " + ReferralCode + ".";

        bitMap = waterMark(bitmap, text, text1, p, p1, Color.BLACK, 200, 30, false);

        //Bitmap bitMap = BitmapFactory.decodeResource(activity.getResources(),R.drawable.rider_logo);

        String directory_name = "/storage/emulated/0/Download/";
        File mFile1 = new File(directory_name);

        String fileName = "Six_Referral_Code.jpg";

        File mFile2 = new File(mFile1, fileName);
        try {
            FileOutputStream outStream;

            outStream = new FileOutputStream(mFile2);

            bitMap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

            outStream.flush();

            outStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.i("inside catch");
        }

        String sdPath = mFile1.getAbsolutePath() + "/" + fileName;

        LogUtils.i("Your IMAGE ABSOLUTE PATH:-" + sdPath);

        File temp = new File(sdPath);

        if (!temp.exists()) {
            LogUtils.e("file " + "no image file at location :" + sdPath);
        }

        return temp;
    }

    private Bitmap waterMark(Bitmap src, String watermark, String watermark1, Point location1, Point location2, int color, int alpha, int size, boolean underline) {

        //get source image width and height
        int w = src.getWidth();
        int h = src.getHeight();

        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        //create canvas object
        Canvas canvas = new Canvas(result);
        //draw bitmap on canvas
        canvas.drawBitmap(src, 0, 0, null);
        //create paint object
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        //apply color
        paint.setColor(color);
        //set transparency
        paint.setAlpha(alpha);
        //set text size
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        //set should be underlined or not
        paint.setUnderlineText(underline);
        //draw text on given location
        //canvas.drawText(watermark, 40, 200, paint);

        drawText(canvas, paint, ReferralCode);

        return result;
    }

    /****************************
     *
     * Conditions for check if
     * social networks are installed
     *
     ****************************/

    private boolean testById(int id) {
        switch (id) {
            case SocialNetwork.WHATSAPP:
                return testSocial(SocialNetwork.WHATSAPP_PACKAGE);
            case SocialNetwork.FACEBOOK:
                return testSocial(SocialNetwork.FACEBOOK_PACKAGE);
            case SocialNetwork.TWITTER:
                return testSocial(SocialNetwork.TWITTER_PACKAGE);
            case SocialNetwork.INSTAGRAM:
                return testSocial(SocialNetwork.INSTAGRAM_PACKAGE);
            case SocialNetwork.PLUS_GOOGLE:
                return testSocial(SocialNetwork.PLUS_GOOGLE_PACKAGE);
            case SocialNetwork.TELEGRAM:
                return testSocial(SocialNetwork.TELEGRAM_PACKAGE);
            case SocialNetwork.GMAIL:
                return testSocial(SocialNetwork.GMAIL_PACKAGE);
            case SocialNetwork.LINKEDIN:
                return testSocial(SocialNetwork.LINKEDIN_PACKAGE);
            case SocialNetwork.VINE:
                return testSocial(SocialNetwork.VINE_PACKAGE);
            case SocialNetwork.HANGOUTS:
                return testSocial(SocialNetwork.HANGOUTS_PACKAGE);
            case SocialNetwork.PINTEREST:
                return testSocial(SocialNetwork.PINTEREST_PACKAGE);
            case SocialNetwork.LINE:
                return testSocial(SocialNetwork.LINE_PACKAGE);
            case SocialNetwork.SNAPCHAT:
                return true;
            //  return testSocial(SocialNetwork.SNAPCHAT_PACKAGE);
            default:
                return false;
        }
    }

    private boolean testSocial(String packageName) {
        try {
            activity.getPackageManager().
                    getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    /************
     * GETTERS & SETTERS
     ************/
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    private String getReferralCode() {
        return ReferralCode;
    }

    public void setReferralCode(String ReferralCode) {
        this.ReferralCode = ReferralCode;
    }
}