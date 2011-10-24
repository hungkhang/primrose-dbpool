/** 
 * Library name : Primrose - A Java Database Connection Pool. Published by Ben 
 * Keeping, http://primrose.org.uk . Copyright (C) 2004 Ben Keeping, 
 * primrose.org.uk Email: Use "Contact Us Form" on website This library is free 
 * software; you can redistribute it and/or modify it under the terms of the GNU 
 * Lesser General Public License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or (at your option) any later version. 
 * This library is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details. You should have received a copy of the GNU Lesser General Public 
 * License along with this library; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 */ 
package uk.org.primrose; 
 
import java.io.BufferedReader; 
import java.io.File; 
import java.io.IOException; 
import java.io.InputStreamReader; 
 
import java.util.Properties; 
import java.util.StringTokenizer; 
 
import javax.activation.DataHandler; 
import javax.activation.FileDataSource; 
 
import javax.mail.Message; 
import javax.mail.MessagingException; 
import javax.mail.Multipart; 
import javax.mail.Session; 
import javax.mail.Transport; 
import javax.mail.internet.AddressException; 
import javax.mail.internet.InternetAddress; 
import javax.mail.internet.MimeBodyPart; 
import javax.mail.internet.MimeMessage; 
import javax.mail.internet.MimeMultipart; 
 
 
/** 
 * @author Ben Keeping Generic sendMail class, allows for sending emails with 
 *         attachements. 
 */ 
public class SendMail { 
    static String mxServer; 
    static String mxServerPort; 
    String toAddress; 
    String fromAddress; 
    String subject; 
    String text; 
    MimeBodyPart mbp = null; 
    Multipart mp = new MimeMultipart(); 
    Logger logger = null; 
 
    /** 
     * Construct an email. <BR> 
     * If the mxServer is unknown, then the static method SendMail.nslookup() can 
     * be called to retrieve the domain's mx server. <BR> 
     * Attachments are optional. <BR> 
     * 
     * @param String 
     *          mxServer - The Mail eXchange server to send the mail to. 
     * @param String 
     *          toAddress - The recipient. 
     * @param String 
     *          fromAddress - The sender - can be anyting as long as looks like an 
     *          email address - eg 'me@somewhere.com'. 
     * @param String 
     *          subject - The mail's subject. 
     * @param String 
     *          text - The body of the mail. 
     */ 
    public SendMail(Logger logger, String mxServer, String mxServerPort, String toAddress, 
        String fromAddress, String subject, String text) { 
        SendMail.mxServer = mxServer; 
        SendMail.mxServerPort = mxServerPort; 
        this.toAddress = toAddress; 
        this.fromAddress = fromAddress; 
        this.subject = subject; 
        this.text = text; 
        this.logger = logger; 
    } 
 
    /** 
     * Add an attachment to the mail (from a file on disk). 
     * 
     * @param File 
     *          file - the File object to attach. 
     */ 
    public void attach(File file) { 
        try { 
            mbp = new MimeBodyPart(); 
            mbp.setFileName(file.getName()); 
            mbp.setDataHandler(new DataHandler(new FileDataSource(file))); 
            mp.addBodyPart(mbp); 
        } catch (MessagingException me) { 
            logger.printStackTrace(me); 
        } 
    } 
 
    /** 
     * Send a message using the contstructor properties. If there is also an 
     * attachment to send, add it too. 
     */ 
    public void send() { 
        new SendThread().start(); 
    } 
 
    /** 
     * Given a domain name like 'hotmail.com', perform an OS nslookup call, and 
     * loop it, looking for the word 'exchanger' in the line. On Linux and Windoze 
     * the mx mail server is always the last word/token in the line, so set it as 
     * such. This pays no attention to the preference of which mx server to use, 
     * but could (and should !) be built in really. Still, never mind. 
     * 
     * @param String 
     *          domain - the domain to lookup. 
     */ 
    public String nslookup(String domain) { 
        String mailserver = null; 
 
        try { 
            Process p = Runtime.getRuntime().exec("nslookup -type=mx " + 
                    domain); 
            BufferedReader br = new BufferedReader(new InputStreamReader( 
                        p.getInputStream())); 
 
            boolean gotMxLine = false; 
            String line = null; 
            String token = null; 
 
            while ((line = br.readLine()) != null) { 
                gotMxLine = false; 
 
                // System.out.println(line); 
                StringTokenizer st = new StringTokenizer(line); 
 
                while (st.hasMoreTokens()) { 
                    token = st.nextToken(); 
 
                    if (token.equals("exchanger")) { 
                        gotMxLine = true; 
                    } 
 
                    if (gotMxLine) { 
                        mailserver = token; 
                    } 
                } 
            } 
        } catch (IOException ioe) { 
            logger.printStackTrace(ioe); 
 
            return null; 
        } 
 
        System.out.println("Mail Server to use is :: " + mailserver); 
 
        return mailserver; 
    } 
 
    class SendThread extends Thread { 
        @Override 
        public void run() { 
            Properties props = System.getProperties(); 
            props.put("mail.smtp.host", mxServer); 
            props.put("mail.smtp.port", mxServerPort); 
 
            Session session = Session.getDefaultInstance(props, null); 
 
            try { 
                Message msg = new MimeMessage(session); 
                msg.setFrom(new InternetAddress(fromAddress)); 
                msg.setRecipients(Message.RecipientType.TO, 
                    InternetAddress.parse(toAddress, false)); 
                msg.setSubject(subject); 
                msg.setHeader("X-Mailer", "JavaMail"); 
 
                // If there have been attachments (one or more), then set the text/body 
                // as a MimeBodyPart, else set it on the Message. If this isn't done, 
                // then either text or an attachment is sent - not both ! 
                if (mbp != null) { 
                    MimeBodyPart mbp2 = new MimeBodyPart(); 
                    mbp2.setText(text); 
                    mp.addBodyPart(mbp2); 
                    msg.setContent(mp); 
                } else { 
                    msg.setText(text); 
                } 
 
                Transport.send(msg); 
            } catch (AddressException ae) { 
                logger.printStackTrace(ae); 
            } catch (MessagingException me) { 
                logger.printStackTrace(me); 
            } 
        } 
    } 
 
    /** 
     * Method main. 
     * 
     * @param args 
     *          Usuage :: <mxServer> <toAddress> <fromAddress> &lt;subject> &lt;text> 
     */ 
 
    /* 
     * public static void main(String args[]) { if (args.length  5) { 
     * System.out.println("Usuage :: <mxServer> <toAddress> <fromAddress> 
     * <subject> <text>"); System.exit(1); } String msgText = ""; for (int i = 4; 
     * i  args.length; i++) { msgText += (" " +args[i]); } new SendMail(args[0], 
     * args[1], args[2], args[3], msgText).send(); } 
     */ 
} 
