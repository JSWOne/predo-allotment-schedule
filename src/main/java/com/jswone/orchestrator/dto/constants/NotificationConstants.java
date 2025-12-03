package com.jswone.orchestrator.dto.constants;

public class NotificationConstants {

  public static final String EMAIL_CHANNEL = "EMAIL";
  public static final String WHATSAPP_CHANNEL = "WHATSAPP";
  public static final String PAYMENT_SENDER_TYPE = "PAYMENT";

  public static final String OVER_DUE_TEMPLATE_TYPE = "PAYMENT_OVERDUE_REMINDER";
  public static final String DUE_TODAY_TEMPLATE_TYPE = "PAYMENT_DUE_TODAY_CUSTOMER";
  public static final String DUE_IN_FIVE_DAY_TEMPLATE_TYPE =
      "PAYMENT_DUE_INVOICE_IN_5_DAYS_REMINDER";
  public static final String MAKE_PAYMENT_URL = "https://www.jswonemsme.com/payment-due";
  public static final String EMAIL_SUBJECT_DUE_IN_5DAYS =
      "Upcoming payments | %s | JSW One Platforms";
  public static final String EMAIL_SUBJECT_DUE_TODAY =
      "Payments due today | %s | JSW One Platforms";
  public static final String EMAIL_SUBJECT_OVERDUE = "Payments overdue | %s | JSW One Platforms";

  public static final String PAYMENT_NOTIFICATION_REPORT_TEMPLATE = "CONNECTOR_DATA_EMAIL_TEMPLATE";
  public static final String PAYMENT_NOTIFICATION_REPORT_FILE_NAME = "payment_notification_report";
  public static final String PAYMENT_NOTIFICATION_REPORT_MESSAGE_BODY =
      "Hi Team,%n%n"
          + "Please find attached the Payment Due Notification details for %s.%n%n"
          + "Thanks & Regards,%n";
  public static final String PAYMENT_NOTIFICATION_REPORT_SUBJECT =
      "Payment Due Notification for %s";

  public static final String PAYMENT_NOTIFICATION_DEFAULT_BANK_NAME =
      "PAYMENT_NOTIFICATION_REPORT_SUBJECT";
}
