package com.finance.control.mail

class Mail(
        val mailFrom: String = "",
        val mailTo: String = "",
        val mailSubject: String = "",
        val mailContent: String = "",
        private val templateName: String? = null,
        private val contentType: String = "text/html"
) {

    override fun toString() =
            """
               Email [
                    mailFrom="$mailFrom",  
                    mailTo="$mailTo", 
                    mailSubject="$mailSubject", 
                    mailContent="$mailContent", 
                    templateName="$templateName", 
                    contentType="$contentType"
                ]
            """
}