package com.example.abhilash.cleartax_twittersearch.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;


public class TweetColumns {
    @DataType(DataType.Type.INTEGER)
    @AutoIncrement  @PrimaryKey
    public static final String _ID = "_id";

    @DataType(DataType.Type.TEXT)  @NotNull
    @Unique(onConflict = ConflictResolutionType.REPLACE)
    public static final String ID = "id";

    @DataType(DataType.Type.TEXT)  @NotNull
    @Unique(onConflict = ConflictResolutionType.REPLACE)
    public static final String ID_STR = "id_str";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String TEXT = "text";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String SCREEN_NAME = "screen_name";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String NAME = "name";

    @DataType(DataType.Type.TEXT)
    public static final String PROFILE_IMAGE = "profile_image_url";

    @DataType(DataType.Type.INTEGER)
    public static final String USER_MENTIONS = "user_mentions";

    @DataType(DataType.Type.TEXT)
    public static final String CREATED_AT = "created_at";

}
