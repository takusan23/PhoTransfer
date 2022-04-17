package io.github.takusan23.photransfer.data

/**
 * 接続先情報データクラス
 *
 * @param deviceName デバイス名
 * @param portNumber ポート番号
 * @param hostAddress IPアドレス
 * @param isLatestServerData 前回と接続情報が変わっておらず、端末内の情報から組み立てた場合はtrue
 */
data class ServerInfoData(
    val deviceName: String,
    val hostAddress: String,
    val portNumber: Int,
    val isLatestServerData: Boolean,
)