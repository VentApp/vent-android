package org.ventapp.vent.data;

public class Post {
    private final String mId;
    private final String mBody;
    private final String mCreatedAt;

    public Post(String body, String createdAt, String id) {
        mId = id;
        mBody = body;
        mCreatedAt = createdAt; // TODO convert to time
    }


    public String getId() {
        return mId;
    }

    public String getBody() {
        return mBody;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }
}
