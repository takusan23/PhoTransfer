package io.github.takusan23.server

/**
 * ファイルを受信したときにのデータ
 *
 * @param deviceName デバイス名
 * @param originalName ファイル名
 * @param filePath 一時保存先。MediaStoreに入ったら削除されるはず
 * @param mimeType Content-Type
 */
data class PhoTransferData(
    val deviceName: String,
    val originalName: String,
    val filePath: String,
    val mimeType: String,
)