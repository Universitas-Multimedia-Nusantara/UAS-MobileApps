package id.ac.umn.chilli.addOrganisasi

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

fun generateQR(content: String, width: Int, height: Int): Bitmap? {
    val hints = mapOf(EncodeHintType.CHARACTER_SET to "UTF-8")
    val writer = QRCodeWriter()
    try {
        val bitMatrix: BitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    } catch (e: WriterException) {
        e.printStackTrace()
    }
    return null
}