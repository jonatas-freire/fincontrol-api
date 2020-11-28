import com.finance.control.mail.Mail
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import java.io.StringWriter
import java.util.*

class EmailBuilder {
    private var subject = ""
    private var mailTo = ""
    private var mailFrom = ""
    private var template = ""
    var velocityContext: VelocityContext = VelocityContext()
    var velocityEngine: VelocityEngine

    fun subject(subject: String?): EmailBuilder {
        this.subject = subject?: ""
        return this
    }

    fun to(to: String): EmailBuilder {
        mailTo = to
        return this
    }

    fun from(from: String): EmailBuilder {
        mailFrom = from
        return this
    }

    fun template(template: String?): EmailBuilder {
        this.template = template?: ""
        return this
    }

    fun addContext(key: String?, value: String?): EmailBuilder {
        velocityContext.put(key, value)
        return this
    }

    @Throws(IllegalArgumentException::class)
    fun createMail(): Mail {

        //Select Template
        val templateEngine = velocityEngine.getTemplate("templates/$template")
        //Apply template
        val stringWriter = StringWriter()
        templateEngine.merge(velocityContext, stringWriter)

        //Check state of the mails.
        require((mailTo.isNotEmpty() || mailFrom.isNotEmpty())) { "Missing mail headers" }

        //Build mail object
        return Mail(
                mailTo = mailTo,
                mailFrom = mailFrom,
                mailContent = stringWriter.toString(),
                mailSubject = subject
        )
    }

    init {

        // Initialize Velocity Engine
        val properties = Properties()
        properties.setProperty("input.encoding", "UTF-8")
        properties.setProperty("output.encoding", "UTF-8")
        properties.setProperty("resource.loader", "file, class, jar")
        properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader")
        velocityEngine = VelocityEngine(properties)
    }
}
