package models

import com.google.gson.annotations.SerializedName

class FlickrResponse {
  @SerializedName("photos") var mPhoto: PhotoFlickr? = null
  @SerializedName("photo") var mPhotoInfo: PhotoInfo? = null
}

class PhotoInfo {
  @SerializedName("title") var mTitle: PhotoContent? = null
  @SerializedName("description") var mDescription: PhotoContent? = null
}

class PhotoContent {
  @SerializedName("_content") var mContent: String? = null
}


class PhotoFlickr {
  @SerializedName("page") var mPage: Int? = null
  @SerializedName("pages") var mPages: Int? = null
  @SerializedName("perpage") var mPerPage: Int? = null
  @SerializedName("total") var mTotal: String? = null
  @SerializedName("photo") var mPhotoDetailsList: Array<PhotoDetails>? = null
}


class PhotoDetails {
  @SerializedName("id") var mId: String? = null
  @SerializedName("owner") var mOwner: String? = null
  @SerializedName("secret") var mSecret: String? = null
  @SerializedName("server") var mServer: String? = null
  @SerializedName("farm") var mFarm: Int? = null
  @SerializedName("title") var mTitle: String? = null
  @SerializedName("ispublic") var mIsPublic: Int? = null
  @SerializedName("isfriend") var mIsFriend: Int? = null
  @SerializedName("isfamily") var mIsFamily: Int? = null
}