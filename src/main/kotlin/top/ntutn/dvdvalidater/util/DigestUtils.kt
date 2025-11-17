package top.ntutn.dvdvalidater.util

import top.ntutn.dvdvalidater.Constants
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

object DigestUtils {
    fun getFileMD5(filePath: String): String {
        val md = MessageDigest.getInstance("MD5")
        FileInputStream(File(filePath)).use { fis ->
            val dataBytes = ByteArray(Constants.DEFAULT_BUFFER_SIZE)
            var nread: Int
            // 从文件输入流中读取数据，并更新MessageDigest对象
            while (fis.read(dataBytes).also { nread = it }!= -1) {
                md.update(dataBytes, 0, nread)
            }
        }
        // 完成MessageDigest操作
        val mdBytes = md.digest()
        // 将字节数组转换为十六进制字符串
        val sb = StringBuilder()
        for (mdByte in mdBytes) {
            sb.append(String.format("%02x", mdByte))
        }
        return sb.toString()
    }

}