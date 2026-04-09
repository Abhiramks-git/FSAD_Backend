package com.finvest.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class EmailService {

    private static final Logger log = Logger.getLogger(EmailService.class.getName());

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.name:FinVest}")
    private String appName;

    @Async
    public void sendOtpEmail(String toEmail, String userName, String otp) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(fromEmail, appName);
            helper.setTo(toEmail);
            helper.setSubject(appName + " — Your Password Reset OTP");
            helper.setText(buildOtpEmail(userName, otp), true);
            mailSender.send(msg);
            log.info("OTP email sent to " + toEmail);
        } catch (Exception e) {
            log.warning("Failed to send OTP email to " + toEmail + ": " + e.getMessage());
        }
    }

    private String buildOtpEmail(String name, String otp) {
        return "<!DOCTYPE html><html><head><meta charset='utf-8'/><style>"
            + "body{font-family:Arial,sans-serif;background:#f8fafc;margin:0;padding:0;}"
            + ".container{max-width:520px;margin:40px auto;background:white;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.08);}"
            + ".header{background:linear-gradient(135deg,#1e3a8a,#2563eb);padding:32px;text-align:center;}"
            + ".header h1{color:white;margin:0;font-size:24px;}"
            + ".header p{color:rgba(255,255,255,0.7);margin:6px 0 0;font-size:14px;}"
            + ".body{padding:32px;}"
            + ".otp-box{background:#eff6ff;border:2px solid #bfdbfe;border-radius:10px;padding:24px;text-align:center;margin:24px 0;}"
            + ".otp-label{font-size:12px;color:#64748b;text-transform:uppercase;letter-spacing:0.08em;margin-bottom:8px;}"
            + ".otp-code{font-size:42px;font-weight:800;letter-spacing:0.2em;color:#1e3a8a;font-family:monospace;}"
            + ".otp-expiry{font-size:13px;color:#64748b;margin-top:8px;}"
            + ".warning{background:#fffbeb;border-left:4px solid #f59e0b;padding:12px 16px;border-radius:6px;margin-top:20px;font-size:13px;color:#78350f;}"
            + ".footer{background:#f1f5f9;padding:20px 32px;font-size:12px;color:#94a3b8;text-align:center;line-height:1.6;}"
            + "</style></head><body>"
            + "<div class='container'>"
            + "<div class='header'><h1>FinVest</h1><p>Mutual Fund Investment Platform</p></div>"
            + "<div class='body'>"
            + "<p style='color:#334155;font-size:16px;'>Hello <strong>" + name + "</strong>,</p>"
            + "<p style='color:#475569;font-size:15px;'>We received a request to reset your FinVest password. Use the OTP below. Valid for <strong>10 minutes</strong>.</p>"
            + "<div class='otp-box'>"
            + "<p class='otp-label'>Your One-Time Password</p>"
            + "<p class='otp-code'>" + otp + "</p>"
            + "<p class='otp-expiry'>⏱ Expires in 10 minutes</p>"
            + "</div>"
            + "<div class='warning'>🔒 If you did not request a password reset, please ignore this email. Your account is safe.</div>"
            + "</div>"
            + "<div class='footer'>© 2025 FinVest · AMFI Registered · Automated email, do not reply.<br/>"
            + "Mutual fund investments are subject to market risks.</div>"
            + "</div></body></html>";
    }
}
