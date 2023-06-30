package io.obolonsky.podcaster.misc

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import io.obolonsky.podcaster.MyPrefs
import java.io.InputStream
import java.io.OutputStream

class MyPrefsSerializer : Serializer<MyPrefs> {

    override val defaultValue: MyPrefs = MyPrefs.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): MyPrefs {
        try {
            return MyPrefs.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: MyPrefs, output: OutputStream) {
        t.writeTo(output)
    }
}