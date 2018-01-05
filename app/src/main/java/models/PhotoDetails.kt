package models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
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


@Entity(tableName = PHOTO_DETAILS_TABLE_NAME)
data class PhotoDetails(

        @PrimaryKey @SerializedName("id") var mId: String,
        @SerializedName("owner") var mOwner: String? = null,
        @SerializedName("secret") var mSecret: String? = null,
        @SerializedName("server") var mServer: String? = null,
        @SerializedName("farm") var mFarm: Int? = null,
        @SerializedName("title") var mTitle: String? = null,
        @SerializedName("ispublic") var mIsPublic: Int? = null,
        @SerializedName("isfriend") var mIsFriend: Int? = null,
        @SerializedName("isfamily") var mIsFamily: Int? = null) {

  private fun imageURL(): String = "http://farm$mFarm.staticflickr.com/$mServer/${mId}_$mSecret"

  fun imageURLMed() : String  = "${imageURL()}$IMAGE_SIZE_MEDIUM"
  fun imageURLLarge(): String = "${imageURL()}$IMAGE_SIZE_LARGE"
}

const val PHOTO_DETAILS_TABLE_NAME = "photo_details"
private const val IMAGE_SIZE_MEDIUM = "_m.jpg"
private const val IMAGE_SIZE_LARGE = "_z.jpg"
