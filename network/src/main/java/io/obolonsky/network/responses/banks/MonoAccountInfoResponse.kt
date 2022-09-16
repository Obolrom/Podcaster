package io.obolonsky.network.responses.banks

import com.google.gson.annotations.SerializedName

data class MonoAccountInfoResponse(
    @SerializedName("clientId") val clientId: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("webHookUrl") val webHookUrl: String?,
    @SerializedName("permissions") val permissions: String?,
    @SerializedName("accounts") val accounts: List<MonoAccountResponse?>?,
    @SerializedName("jars") val jars: List<JarResponse?>?,
) {

    data class MonoAccountResponse(
        @SerializedName("id") val id: String?,
        @SerializedName("sendId") val sendId: String?,
        @SerializedName("currencyCode") val currencyCode: Int?,
        @SerializedName("cashbackType") val cashbackType: String?,
        @SerializedName("balance") val balance: Int?,
        @SerializedName("creditLimit") val creditLimit: Int?,
        @SerializedName("type") val type: String?,
        @SerializedName("iban") val iban: String?,
    )

    data class JarResponse(
        @SerializedName("id") val id: String?,
        @SerializedName("sendId") val sendId: String?,
        @SerializedName("title") val title: String?,
        @SerializedName("description") val description: String?,
        @SerializedName("currencyCode") val currencyCode: Int?,
        @SerializedName("balance") val balance: Int?,
        @SerializedName("goal") val goal: Int?,
    )
}
