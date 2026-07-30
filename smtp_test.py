import smtplib
from email.message import EmailMessage

try:
    msg = EmailMessage()
    msg.set_content('This is a test from CareReach backend diagnostics')
    msg['Subject'] = 'CareReach Diagnostic'
    msg['From'] = 'carereach.official@gmail.com'
    msg['To'] = 'carereach.official@gmail.com'

    server = smtplib.SMTP('smtp-relay.brevo.com', 587)
    server.starttls()
    server.login('b1e5f7001@smtp-brevo.com', 'xsmtpsib-3b8e32393575bb8ccf70abf8af8ae8dc221f427339c252ith.powgD7lcHutNVR3u')
    server.send_message(msg)
    server.quit()
    print('SMTP Test SUCCESS')
except Exception as e:
    print('SMTP Test FAILED:', str(e))
