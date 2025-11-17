package top.ntutn.dvdvalidater

object Constants {
    // 文件相关常量
    const val CHECKSUM_FILE_EXTENSION = "dvdv"
    const val CHECKSUM_FILE_NAME = "checksum.dvdv"
    
    // XML 相关常量
    const val XML_ROOT_ELEMENT = "checksums"
    const val XML_CHECKSUM_ELEMENT = "checksum"
    const val XML_PATH_ATTRIBUTE = "path"
    const val XML_CHECKSUM_ATTRIBUTE = "checksum"
    const val XML_ALGORITHM_ATTRIBUTE = "algorithm"
    const val XML_MD5_ALGORITHM = "md5"
    
    // 按钮文本相关常量
    const val DIGEST_BUTTON_TAG = "digest_button"
    const val VALIDATE_BUTTON_TAG = "validate_button"
    const val FILE_CHOOSER_TAG = "file-chooser"
    
    // 日志记录中的标签
    const val DIGEST_BUTTON_LOG_TAG = "digest_button"
    const val VALIDATE_BUTTON_LOG_TAG = "validate_button"
    
    // 缓存相关常量
    const val DEFAULT_BUFFER_SIZE = 1024
    const val MAX_LOG_ENTRIES = 1000
}