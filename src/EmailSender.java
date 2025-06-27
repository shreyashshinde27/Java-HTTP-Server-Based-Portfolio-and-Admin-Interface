// Requires jakarta.mail.jar in the classpath (put in lib/)
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class EmailSender {
    private static final String HOST = "smtp.gmail.com";
    private static final int PORT = 587;
    private static Properties appProps;

    static {
        appProps = new Properties();
        // Load properties from config.properties file in the project root
        try (InputStream input = new FileInputStream("config.properties")) {
            appProps.load(input);
        } catch (IOException ex) {
            System.err.println("Could not load config.properties. Please ensure the file exists in the project root directory.");
            ex.printStackTrace();
        }
    }

    private static final String USERNAME = appProps.getProperty("email.username");
    private static final String PASSWORD = appProps.getProperty("email.password");

    public static void sendEmail(String to, String subject, String body) throws MessagingException {
        if (USERNAME == null || PASSWORD == null || USERNAME.isEmpty() || PASSWORD.isEmpty() || PASSWORD.contains("your-new-16-character")) {
            System.err.println("Email credentials are not configured in config.properties. Email not sent.");
            return; // Silently fail to send email if not configured
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", String.valueOf(PORT));

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(USERNAME));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }
} 