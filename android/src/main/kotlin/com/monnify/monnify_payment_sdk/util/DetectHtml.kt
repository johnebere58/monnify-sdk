package com.monnify.monnify_payment_sdk.util

import java.util.regex.Pattern

/**
 * Detect HTML markup in a string
 * This will detect tags or entities
 *
 * @author dbennett455@gmail.com - David H. Bennett
 *
 */

class DetectHtml {

    companion object {
        // adapted from post by Phil Haack and modified to match better
        private const val tagStart =
            "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)\\>"
        private const val tagEnd = "\\</\\w+\\>"
        private const val tagSelfClosing =
            "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)/\\>"
        private const val htmlEntity = "&[a-zA-Z][a-zA-Z0-9]+;"

        private val htmlPattern = Pattern.compile(
            "(" + Companion.tagStart + ".*" + Companion.tagEnd + ")|(" + Companion.tagSelfClosing + ")|(" + Companion.htmlEntity + ")",
            Pattern.DOTALL
        )


        /**
         * Will return true if s contains HTML markup tags or entities.
         *
         * @params String to test
         * @return true if string contains HTML
         */
        fun isHtml(s: String?): Boolean {
            var ret = false
            if (s != null) {
                ret = htmlPattern.matcher(s).find()
            }
            return ret
        }
    }
}